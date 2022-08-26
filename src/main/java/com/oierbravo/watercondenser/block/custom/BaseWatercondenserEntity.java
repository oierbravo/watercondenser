package com.oierbravo.watercondenser.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public abstract class BaseWatercondenserEntity<T extends BlockEntity> extends Block implements EntityBlock {

    protected abstract BlockEntityType.BlockEntitySupplier<T> getTileSupplier();

    public BaseWatercondenserEntity(Properties properties) {
        super(properties);
    }

    protected T getTileEntity(Level world, BlockPos pos) {
        return (T) world.getBlockEntity(pos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return getTileSupplier().create(blockPos, blockState);
    }
}