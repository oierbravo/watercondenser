package com.oierbravo.watercondenser.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfigCommon {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> CONDENSER_CAPACITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> CONDENSER_TICKS_PER_CYCLE;
    public static final ForgeConfigSpec.ConfigValue<Integer> CONDENSER_MB_PER_CYCLE;
    public static final ForgeConfigSpec.ConfigValue<Float> CONDENSER_MB_MULTI_MIN;
    public static final ForgeConfigSpec.ConfigValue<Float> CONDENSER_MB_MULTI_MAX;

    static {
        BUILDER.push("Configs for WaterCondenser");

        CONDENSER_CAPACITY = BUILDER.comment("Tank capacity in mB")
                .define("Condenser capacity", 1000);
        CONDENSER_TICKS_PER_CYCLE = BUILDER.comment("The length of a fill cycle, in ticks")
                .define("Ticks between cycles", 1);
        CONDENSER_MB_PER_CYCLE = BUILDER.comment("How much mB to generate per fill cycle")
                .define("Fluid per cycle", 2);
        CONDENSER_MB_MULTI_MIN = BUILDER.comment("For random variance, the minimum multiplier for each fill cycle")
                .define("Fluid multiplier chance min", 0.0f);
        CONDENSER_MB_MULTI_MAX = BUILDER.comment("For random variance, the maximum multiplier for each fill cycle")
                .define("Fluid multiplier chance max", 1.0f);


        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
