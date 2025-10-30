package com.oierbravo.watercondenser;

import com.mojang.logging.LogUtils;
import com.oierbravo.watercondenser.block.ModBlocks;
import com.oierbravo.watercondenser.config.WaterCondenserConfig;
import com.oierbravo.watercondenser.entity.ModBlockEntities;
import com.oierbravo.watercondenser.item.ModItems;
import com.oierbravo.watercondenser.network.ModMessages;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

@Mod(WaterCondenser.MODID)
public class WaterCondenser
{
    public static final String MODID = "watercondenser";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public WaterCondenser(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        //modEventBus.addListener(this::commonSetup);

        // Register config once NeoForge config system is ready
        modContainer.registerConfig(ModConfig.Type.COMMON, WaterCondenserConfig.SPEC);
        modEventBus.addListener(WaterCondenserConfig::onLoad);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMessages.register();


        //NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(ModMessages::registerNetworking);

        //modContainer.registerConfig(ModConfig.Type.COMMON, ModConfigCommon.SPEC, "watercondenser-common.toml");

        if (FMLEnvironment.dist == Dist.CLIENT)
            modEventBus.addListener(ClientModEvents::registerEntityRenderers);


    }
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        /*event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.WATERCONDENSER_ENTITY.get(),
                WatercondenserBlockEntity::getFluidHandler
        );*/
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.WATERCONDENSER_ENTITY.get(), (be, context) -> be.getFluidHandler());
        //TODO 1.22 REMOVE
        /*event.registerBlock(Capabilities.FluidHandler.BLOCK,
                (level, pos, state, be, side) -> {
                    if (side != null)
                        return ((WatercondenserBlockEntity) be).getFluidHandler();
                    else
                        return new FluidTank(ModConfigCommon.CONDENSER_CAPACITY.get());
                },
                // blocks to register for
                ModBlocks.WATERCONDENSER.get());
*/
    }
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.WATERCONDENSER);
        }
    }

   /* @SubscribeEvent
    public void onServerAboutToStart(final ServerAboutToStartEvent event) {
        WatercondenserBlockEntity.verifyConfig(LOGGER);
    }*/
}
