package com.oierbravo.watercondenser.block.custom;

import com.mojang.serialization.MapCodec;
import com.oierbravo.watercondenser.entity.ModBlockEntities;
import com.oierbravo.watercondenser.entity.WatercondenserBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Stream;

public class WatercondenserBlock extends Block implements EntityBlock{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    // Base voxel shape (facing north)
    private static final VoxelShape SHAPE_NORTH = Stream.of(
            Block.box(0, 0, 0, 16, 1, 16),   // base
            Block.box(1, 1, 14, 14, 9, 15),  // wall2
            Block.box(1, 1, 1, 2, 9, 14),    // wall3 (left)
            Block.box(14, 1, 2, 15, 9, 15),  // wall3 (right)
            Block.box(2, 1, 1, 15, 9, 2)     // wall4 (front)
    ).reduce(Shapes.empty(), Shapes::or);

    // Precomputed rotated shapes for each facing
    private static final Map<Direction, VoxelShape> SHAPES = Map.of(
            Direction.NORTH, SHAPE_NORTH,
            Direction.EAST, rotateShape(Direction.EAST, SHAPE_NORTH),
            Direction.SOUTH, rotateShape(Direction.SOUTH, SHAPE_NORTH),
            Direction.WEST, rotateShape(Direction.WEST, SHAPE_NORTH)
    );

    public WatercondenserBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES.getOrDefault(state.getValue(FACING), SHAPE_NORTH);
    }

    // Utility: rotate voxel shape clockwise around Y axis
    private static VoxelShape rotateShape(Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = (to.get2DDataValue() + 4) % 4;

        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                buffer[1] = Shapes.or(buffer[1],
                        Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX));
            });
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
    /* BLOCK ENTITY */

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHitResult) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof WatercondenserBlockEntity) {
                WatercondenserBlockEntity watercondenser = (WatercondenserBlockEntity) entity;

                boolean success = FluidUtil.interactWithFluidHandler(pPlayer,pHand,watercondenser.getFluidHandler());
                if(success){
                    watercondenser.setChanged();
                }

                ItemStack held = pPlayer.getItemInHand(pHand);

                if (!pLevel.isClientSide() && held.getItem() == Items.GLASS_BOTTLE){
                    boolean waterConsumed = watercondenser.consumeWaterBottle();
                    if(waterConsumed) {
                        held.shrink(1);
                        ItemStack waterPotion = PotionContents.createItemStack(new ItemStack(Items.POTION).getItem(), Potions.WATER);
                        pPlayer.getInventory().placeItemBackInInventory(waterPotion);
                        return ItemInteractionResult.CONSUME;
                    }
                    return ItemInteractionResult.FAIL;
                }

            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return ItemInteractionResult.sidedSuccess(pLevel.isClientSide());
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModBlockEntities.WATERCONDENSER_ENTITY.get().create(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.WATERCONDENSER_ENTITY.get() ? WatercondenserBlockEntity::tick : null;
    }

}
