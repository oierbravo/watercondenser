package com.oierbravo.watercondenser.compat.jei;

import com.oierbravo.watercondenser.WaterCondenser;
import com.oierbravo.watercondenser.block.ModBlocks;
import com.oierbravo.watercondenser.config.WaterCondenserConfig;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(WaterCondenser.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new WaterCondenserCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.WATERCONDENSER.get()), WaterCondenserCategory.TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(WaterCondenserCategory.TYPE, WaterCondenserCategory.getRecipes());
    }

    public class WaterCondenserCategory implements IRecipeCategory<WaterCondenserCategory.WaterCondenserRecipe> {

        public final static RecipeType<WaterCondenserRecipe> TYPE = RecipeType.create("watercondenser", "production", WaterCondenserRecipe.class);

        private final IDrawable background;
        private final IDrawable icon;
        private final IDrawable slotDrawable;

        private final Fluid producedFluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(WaterCondenserConfig.condenserFluid));


        public WaterCondenserCategory(IGuiHelper guiHelper) {
            this.background = new IDrawable() {
                @Override
                public int getWidth() {
                    return 176;
                }

                @Override
                public int getHeight() {
                    return 30;
                }

                @Override
                public void draw(GuiGraphics graphics, int xOffset, int yOffset) {

                }
            };
            this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.WATERCONDENSER));
            this.slotDrawable = guiHelper.getSlotDrawable();
        }

        @Override
        public RecipeType<WaterCondenserRecipe> getRecipeType() {
            return TYPE;
        }

        @Override
        public Component getTitle() {
            return Component.translatable("watercondenser.recipe", Component.translatable(producedFluid.getFluidType().getDescriptionId()).getString());
        }

        @Override
        public IDrawable getBackground() {
            return this.background;
        }

        @Override
        public IDrawable getIcon() {
            return this.icon;
        }

        @Override
        public void draw(WaterCondenserRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
            Minecraft minecraft = Minecraft.getInstance();
            Font fontRenderer = minecraft.font;
            String translationKey = "watercondenser.recipe.amount_each_tick";
            if(WaterCondenserConfig.ticksPerCycle > 1)
                translationKey += ".plural";
            guiGraphics.drawString(fontRenderer, Component.translatable(translationKey, WaterCondenserConfig.mbPerCycle, WaterCondenserConfig.ticksPerCycle),43,15,0xFF808080, false);
        }

        @Override
        public void setRecipe(IRecipeLayoutBuilder builder, WaterCondenserRecipe recipe, IFocusGroup iFocusGroup) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 24, 7)
                    .setBackground(slotDrawable, -1, -1)
                   .addIngredient(NeoForgeTypes.FLUID_STACK,recipe.outputFluid);

            builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addIngredient(VanillaTypes.ITEM_STACK,new ItemStack(producedFluid.getBucket().asItem()));
        }


        public static List<WaterCondenserRecipe> getRecipes() {
            List<WaterCondenserRecipe> recipes = new ArrayList<>();
            Fluid configuredFluid = BuiltInRegistries.FLUID.get(ResourceLocation.tryParse(WaterCondenserConfig.condenserFluid));

            recipes.add(new WaterCondenserRecipe(
                    new FluidStack(configuredFluid,1000)
            ));
            return recipes;
        }

        public record WaterCondenserRecipe(@Nullable FluidStack outputFluid) {

        }
    }
}
