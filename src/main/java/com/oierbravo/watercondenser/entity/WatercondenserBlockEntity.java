package com.oierbravo.watercondenser.entity;

import com.oierbravo.watercondenser.config.WaterCondenserConfig;
import com.oierbravo.watercondenser.network.ModMessages;
import com.oierbravo.watercondenser.network.packets.data.FluidSyncPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;


import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;
/**
 *  Code adapted from https://github.com/EwyBoy/ITank/blob/1.18.2/src/main/java/com/ewyboy/itank/common/content/tank/TankTile.java
 *
 */
public class WatercondenserBlockEntity extends BlockEntity {
    private static final RandomGenerator sharedRandom = new Random();
    private static Fluid fluidOutput = null;
    private static long lastCycleTime = -1;
    private static int cycleCounter = 0;
    private static boolean resetCycle = false;
    private CompoundTag updateTag;
    private final FluidTank fluidTankHandler = createFluidTank();

    private Lazy<IFluidHandler> lazyFluidHandler = Lazy.of(() -> fluidTankHandler);
    public WatercondenserBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.WATERCONDENSER_ENTITY.get(), pWorldPosition, pBlockState);
        updateTag = getPersistentData();

        // --- Safe fluid initialization ---
        // Use cached config value; if it's not yet loaded or invalid, fall back to default.
        String fluidName = WaterCondenserConfig.condenserFluid;
        if (fluidName == null || fluidName.isEmpty()) {
            fluidName = WaterCondenserConfig.CONDENSER_FLUID_DEFAULT;
        }

        try {
            fluidOutput = BuiltInRegistries.FLUID.get(ResourceLocation.parse(fluidName));
        } catch (Exception ignored) {
            fluidOutput = null;
        }

        // Always guarantee a valid still fluid reference
        if (fluidOutput == null || fluidOutput == Fluids.EMPTY) {
            fluidOutput = Fluids.WATER;
        }
    }

    @Override
    public void invalidateCapabilities() {
        super.invalidateCapabilities();
        lazyFluidHandler.invalidate();
    }

    private FluidTank createFluidTank() {
        // --- Add null guard to filter predicate ---
        return new FluidTank(WaterCondenserConfig.condenserCapacity,
                fluid -> fluidOutput != null && fluid.getFluid().isSame(fluidOutput)) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                assert level != null;
                if (!level.isClientSide()) {
                    ModMessages.sendToAllClients(new FluidSyncPayload(getFluidStack(), worldPosition));
                }
            }
        };
    }

    public FluidStack getFluidStack() {

        if (!fluidTankHandler.isEmpty()) {
            return fluidTankHandler.getFluid();
        }

        return new FluidStack(fluidOutput, 1);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        fluidTankHandler.writeToNBT(registries, tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fluidTankHandler.readFromNBT(registries, tag);

        if (!fluidTankHandler.getFluid().getFluid().isSame(fluidOutput)) {
            // fluid in config differs from saved NBT, override it
            final FluidStack changedFluidStack = new FluidStack(fluidOutput, fluidTankHandler.getFluid().getAmount());
            fluidTankHandler.setFluid(changedFluidStack);
        }
    }

    public static <T extends BlockEntity> void tick(Level pLevel, BlockPos pPos, BlockState pState, T pBlockEntity) {
        WatercondenserBlockEntity blockEntity = (WatercondenserBlockEntity) pBlockEntity;
        if(pLevel.isClientSide()) {
            return;
        }

        // Use gameTime so it always advances even if daylight cycle is disabled
        final long timeNow = pLevel.getGameTime();
        if (timeNow != lastCycleTime) {
            lastCycleTime = timeNow;
            if (resetCycle) {
                // A "lazy" counter reset; allows all TE's to actually get a chance to tick their cycle
                resetCycle = false;
                cycleCounter = 0;
            }

            cycleCounter++;
        }

        if (cycleCounter >= WaterCondenserConfig.ticksPerCycle) {
            resetCycle = true;

            final float amountMultiMin = WaterCondenserConfig.mbMultiplierMin;
            final float amountMultiMax = WaterCondenserConfig.mbMultiplierMax;
            int amount = WaterCondenserConfig.mbPerCycle;

            // Apply rain multiplier if raining directly above
            if (pLevel.isRainingAt(pPos.above())) {
                amount = (int) Math.round(amount * WaterCondenserConfig.rainMultiplier);
            } else if (amountMultiMin < 1.0f || amountMultiMax > 1.0f) {
                // Apply random variance only when not raining
                final float randomMultiplier = amountMultiMin +
                        (sharedRandom.nextFloat() * (amountMultiMax - amountMultiMin));
                amount = Math.round(amount * randomMultiplier);
            }

            blockEntity.fluidTankHandler.fill(new FluidStack(fluidOutput, amount), IFluidHandler.FluidAction.EXECUTE);
        }



    }
    public IFluidHandler getFluidHandler() {
        return this.fluidTankHandler;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }


    public void setFluid(FluidStack fluidStack) {

        this.fluidTankHandler.setFluid(fluidStack);
    }

    public boolean consumeWaterBottle() {
        int consumption = WaterCondenserConfig.bottleConsumption;
        if( consumption > fluidTankHandler.getFluidAmount()){
            return false;
        }
        fluidTankHandler.drain(consumption, IFluidHandler.FluidAction.EXECUTE);
        return true;
    }

    public int getProgressPercent() {
        return cycleCounter * 100 / WaterCondenserConfig.ticksPerCycle;
    }

}
