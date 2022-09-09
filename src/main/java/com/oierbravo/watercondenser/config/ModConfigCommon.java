package com.oierbravo.watercondenser.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfigCommon {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> CONDENSER_CAPACITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> CONDENSER_FLUID_PER_TICK;

    static {
        BUILDER.push("Configs for WaterCondenser");

        CONDENSER_CAPACITY = BUILDER.comment("Tank capacity in mB")
                .define("Condenser capacity", 1000);
        CONDENSER_FLUID_PER_TICK = BUILDER.comment("mB per tick increment")
                .define("Fluid per tick", 2);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
