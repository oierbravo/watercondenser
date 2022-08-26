package com.oierbravo.watercondenser.entity;

import com.oierbravo.watercondenser.WaterCondenser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;

import java.util.stream.Stream;
/**
 *  Code adapted from https://github.com/EwyBoy/ITank/blob/1.18.2/src/main/java/com/ewyboy/itank/common/content/tank/TankTile.java
 *
 */
public class WatercondenserBlockEntity extends BlockEntity {
//public class WatercondenserBlockEntity extends BlockEntity implements IFluidHandler{

    private final int FLUID_CAPACITY = 1000;

    private CompoundTag updateTag;
    private final FluidTank fluidTankHandler = createFluidTank();
    //private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> fluidTankHandler);
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
    public WatercondenserBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(ModBlockEntities.WATERCONDENSER_ENTITY.get(), pWorldPosition, pBlockState);
        updateTag = getTileData();
    }

    private FluidTank createFluidTank() {

        return new FluidTank(FLUID_CAPACITY, (fluid -> fluid.getFluid().is(FluidTags.WATER))) {
            @Override
            protected void onContentsChanged() {
                setChanged();
                clientSync();
            }

        };
    }

    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return lazyFluidHandler.cast();
        } else {
            return super.getCapability(cap, side);
        }
    }
    public int getFluidAmount() {
        return this.fluidTankHandler.getFluidAmount();
    }
    public FluidStack getFluidStack() {

        if (!fluidTankHandler.isEmpty()) {
            return fluidTankHandler.getFluid();
        }
        return new FluidStack(Fluids.WATER, 1);
    }

    public float getFluidProportion() {
        return (float) fluidTankHandler.getFluidAmount() / 1000;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyFluidHandler = LazyOptional.of(() -> fluidTankHandler);
    }


    @Override
    public void invalidateCaps()  {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

    /*@Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        tag.put("fluid", fluidTankHandler.writeToNBT(new CompoundTag()));
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        fluidTankHandler.readFromNBT(nbt.getCompound("fluid"));
        super.load(nbt);
    }*/
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        fluidTankHandler.readFromNBT(nbt);
        fluidTankHandler.setCapacity(nbt.getInt("fluid"));
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        fluidTankHandler.writeToNBT(nbt);
        nbt.putInt("fluid", fluidTankHandler.getCapacity());
        updateTag = nbt;
    }




    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, WatercondenserBlockEntity pBlockEntity) {

        if(pLevel.isClientSide()) {
            return;
        }
        //int currentAmount = pBlockEntity.fluidTankHandler.getFluidAmount();

       // FluidStack fluid = pBlockEntity.fluidTankHandler.getFluid();
       // if(fluid.isEmpty()){
       //     fluid =  new FluidStack(Fluids.WATER, 0);
       // }
        //fluid.grow(1);
        //fluid.setAmount(currentAmount + 10);
        //WaterCondenser.LOGGER.info("TICK Amount: " + pBlockEntity.fluidTankHandler.getFluidAmount());
        int amount = (int)Math.floor(2 * Math.random());
        if(amount > 500){
            //pState.setValue()
        }
        pBlockEntity.fluidTankHandler.fill( new FluidStack(Fluids.WATER, amount), IFluidHandler.FluidAction.EXECUTE);
        //this.clientSync();
        /*if(hasRecipe(pBlockEntity) && hasEnoughEnergy(pBlockEntity)) {
            pBlockEntity.progress++;
            extractEnergy(pBlockEntity);
            setChanged(pLevel, pPos, pState);
            if(pBlockEntity.progress >= pBlockEntity.maxProgress) {
                craftItem(pBlockEntity);
            }
        } else {
            pBlockEntity.resetProgress();
            setChanged(pLevel, pPos, pState);
        }

        if(hasWaterSourceInSlot(pBlockEntity)) {
            transferItemWaterToWaterTank(pBlockEntity);
        }*/
    }

    /*//@Override
    public int getTanks() {
        return this.fluidTankHandler.getTanks();
    }

    @NotNull
    //@Override
    public FluidStack getFluidInTank(int tank) {
        return this.fluidTankHandler.getFluidInTank(tank);
    }

    //@Override
    public int getTankCapacity(int tank) {
        return this.fluidTankHandler.getTankCapacity(tank);
    }

    //@Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return this.fluidTankHandler.isFluidValid(tank, stack);
    }

    //@Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return this.fluidTankHandler.fill(resource, action);
    }

    @NotNull
    //@Override
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return this.fluidTankHandler.drain(resource, action);
    }

    @NotNull
    //@Override
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return this.fluidTankHandler.drain(maxDrain, action);
    }
*/
    public IFluidHandler getTank() {
       return this.fluidTankHandler;
    }
    public IFluidHandler getFluidHandler() {
        return this.fluidTankHandler;
    }


    @Override
    public CompoundTag getUpdateTag() {
        this.saveAdditional(updateTag);
        return updateTag;
    }


    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    public void clientSync() {
        if (Objects.requireNonNull(this.getLevel()).isClientSide) {
            return;
        }
        ServerLevel world = (ServerLevel) this.getLevel();
        Stream<ServerPlayer> entities = world.getChunkSource().chunkMap.getPlayers(new ChunkPos(this.worldPosition), false).stream();
        Packet<ClientGamePacketListener> updatePacket = this.getUpdatePacket();
        entities.forEach(e -> {
            if (updatePacket != null) {
                e.connection.send(updatePacket);
            }
        });
    }


}
