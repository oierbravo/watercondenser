package com.oierbravo.watercondenser;


import com.oierbravo.watercondenser.block.ModBlocks;
import com.oierbravo.watercondenser.block.custom.WatercondenserRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = WaterCondenser.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        WaterCondenser.LOGGER.info("INIT CLIENT SETUP");
        WatercondenserRenderer.register();
    }
}