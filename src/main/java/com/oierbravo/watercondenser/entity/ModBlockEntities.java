package com.oierbravo.watercondenser.entity;

import com.oierbravo.watercondenser.WaterCondenser;
import com.oierbravo.watercondenser.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, WaterCondenser.MOD_ID);

    public static final RegistryObject<BlockEntityType<WatercondenserBlockEntity>> WATERCONDENSER_ENTITY =
            BLOCK_ENTITIES.register("watercondenser_entity", () ->
                    BlockEntityType.Builder.of(WatercondenserBlockEntity::new,
                            ModBlocks.WATERCONDENSER.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
