package com.oierbravo.watercondenser;


import com.oierbravo.watercondenser.block.ModBlocks;
import com.oierbravo.watercondenser.block.custom.WatercondenserRenderer;
import com.oierbravo.watercondenser.block.interfaces.IHasRenderType;
import com.oierbravo.watercondenser.block.interfaces.IHasSpecialRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = WaterCondenser.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    /*@SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PEDESTAL.get(), PedestalBlockEntityRenderer::new);
    }*/

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        WaterCondenser.LOGGER.info("INIT CLIENT SETUP");
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.WATERCONDENSER.get(), RenderType.cutout());
        WatercondenserRenderer.register();
    }
    /*@OnlyIn(Dist.CLIENT)
    public static void initRenderTypes(FMLClientSetupEvent ignoredEvent) {
        ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block instanceof IHasRenderType).forEach(
                block -> ItemBlockRenderTypes.setRenderLayer(block, ((IHasRenderType) block).getRenderType())
        );
    }

    @OnlyIn(Dist.CLIENT)
    public static void initSpecialRenders(FMLClientSetupEvent ignoredEvent) {
        ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block instanceof IHasSpecialRenderer).forEach(
                block -> {
                    ((IHasSpecialRenderer) block).initSpecialRenderer();
                }
        );
    }*/
}