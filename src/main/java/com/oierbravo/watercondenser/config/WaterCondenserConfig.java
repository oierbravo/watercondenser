package com.oierbravo.watercondenser.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Objects;

public class WaterCondenserConfig {

    // === BUILDER ===
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // === DEFAULT FLUID ===
    public static final String CONDENSER_FLUID_DEFAULT = "minecraft:water";

    // === CONFIG VALUES ===
    static {
        BUILDER.push("Configs for WaterCondenser");

        CONDENSER_FLUID = BUILDER.comment(
                        "The fluid to generate. If not valid, will revert to minecraft:water. " +
                                "Existing worlds will retroactively change their fluid type on change (but keep the same amount).")
                .define("Output fluid", "minecraft:water", WaterCondenserConfig::validateFluidName);

        CONDENSER_CAPACITY = BUILDER.comment("Tank capacity in mB")
                .defineInRange("Condenser capacity", 1000, 1, Integer.MAX_VALUE);

        CONDENSER_TICKS_PER_CYCLE = BUILDER.comment("The length of a fill cycle, in ticks")
                .defineInRange("Ticks between cycles", 1, 1, Integer.MAX_VALUE);

        CONDENSER_MB_PER_CYCLE = BUILDER.comment("How much mB to generate per fill cycle")
                .defineInRange("Fluid per cycle", 2, 1, Integer.MAX_VALUE);

        CONDENSER_MB_MULTI_MIN = BUILDER.comment("For random variance, the minimum multiplier for each fill cycle")
                .defineInRange("Fluid multiplier chance min", 0.0, 0.0, 5.0);

        CONDENSER_MB_MULTI_MAX = BUILDER.comment("For random variance, the maximum multiplier for each fill cycle")
                .defineInRange("Fluid multiplier chance max", 1.0, 0.0, 5.0);

        CONDENSER_BOTTLE_MB_CONSUMPTION = BUILDER.comment("Bottle consumption per bottle, in mB")
                .defineInRange("Fluid amount in mB", 250, 1, Integer.MAX_VALUE);

        CONDENSER_RAIN_MULTIPLIER = BUILDER.comment("Multiplier for condensation rate during rain. Default = 3.0 (3x faster)")
                .define("Rain multiplier", 3.0);


        BUILDER.pop();
    }

    // === CONFIG VALUE FIELDS ===
    public static ModConfigSpec.ConfigValue<String> CONDENSER_FLUID;
    public static ModConfigSpec.IntValue CONDENSER_CAPACITY;
    public static ModConfigSpec.IntValue CONDENSER_TICKS_PER_CYCLE;
    public static ModConfigSpec.IntValue CONDENSER_MB_PER_CYCLE;
    public static ModConfigSpec.DoubleValue CONDENSER_MB_MULTI_MIN;
    public static ModConfigSpec.DoubleValue CONDENSER_MB_MULTI_MAX;
    public static ModConfigSpec.IntValue CONDENSER_BOTTLE_MB_CONSUMPTION;
    public static ModConfigSpec.ConfigValue<Double> CONDENSER_RAIN_MULTIPLIER;

    // === BUILD SPEC ===
    public static final ModConfigSpec SPEC = BUILDER.build();

    // === RUNTIME CACHED VALUES ===
    public static String condenserFluid = "minecraft:water";
    public static int condenserCapacity = 1000;
    public static int ticksPerCycle = 1;
    public static int mbPerCycle = 2;
    public static float mbMultiplierMin = 0.0f;
    public static float mbMultiplierMax = 1.0f;
    public static int bottleConsumption = 250;
    public static double rainMultiplier = 3.0;

    // === VALIDATION ===
    private static boolean validateFluidName(final Object obj) {
        if (!(obj instanceof String fluidName)) return false;
        ResourceLocation id = ResourceLocation.tryParse(fluidName);
        return id != null && BuiltInRegistries.FLUID.containsKey(Objects.requireNonNull(id));
    }

    // === LOAD EVENT ===
    public static void onLoad(final ModConfigEvent event) {
        condenserFluid = CONDENSER_FLUID.get();
        condenserCapacity = CONDENSER_CAPACITY.get();
        ticksPerCycle = CONDENSER_TICKS_PER_CYCLE.get();
        mbPerCycle = CONDENSER_MB_PER_CYCLE.get();
        mbMultiplierMin = CONDENSER_MB_MULTI_MIN.get().floatValue();
        mbMultiplierMax = CONDENSER_MB_MULTI_MAX.get().floatValue();
        bottleConsumption = CONDENSER_BOTTLE_MB_CONSUMPTION.get();
        rainMultiplier = CONDENSER_RAIN_MULTIPLIER.get();
    }
}
