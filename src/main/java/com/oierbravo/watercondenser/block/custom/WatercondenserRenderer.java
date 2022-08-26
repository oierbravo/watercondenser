package com.oierbravo.watercondenser.block.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.oierbravo.watercondenser.WaterCondenser;
import com.oierbravo.watercondenser.entity.ModBlockEntities;
import com.oierbravo.watercondenser.entity.WatercondenserBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;


/**
 *  Code adapted from https://github.com/EwyBoy/ITank/blob/1.18.2/src/main/java/com/ewyboy/itank/client/TankRenderer.java
 *
 */
public class WatercondenserRenderer implements BlockEntityRenderer<WatercondenserBlockEntity> {

    public WatercondenserRenderer(BlockEntityRendererProvider.Context context) {
    }
    public static void register() {
        BlockEntityRenderers.register(ModBlockEntities.WATERCONDENSER_ENTITY.get(), WatercondenserRenderer::new);
    }
    @Override
    public void render(WatercondenserBlockEntity watercondenser, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int light, int overlay) {
        FluidStack fluidStack = watercondenser.getFluidStack();
        int amountT =  watercondenser.getFluidAmount();
        float percentT = watercondenser.getFluidProportion();
        if (!fluidStack.isEmpty()) {
            int amount = fluidStack.getAmount();
            int total = watercondenser.getTank().getTankCapacity(0);
            this.renderFluidInTank(watercondenser.getLevel(), watercondenser.getBlockPos(), fluidStack, matrix, buffer, (amount / (float) total));
        }
    }

    private void renderFluidInTank(BlockAndTintGetter world, BlockPos pos, FluidStack fluidStack, PoseStack matrix, MultiBufferSource buffer, float percent) {
        matrix.pushPose();
        matrix.translate(0.5d, 0.29d, 0.5d);
        Matrix4f matrix4f = matrix.last().pose();
        Matrix3f matrix3f = matrix.last().normal();

        Fluid fluid = fluidStack.getFluid();
        FluidAttributes fluidAttributes = fluid.getAttributes();
        TextureAtlasSprite fluidTexture = getFluidStillSprite(fluidAttributes, fluidStack);

        int color = fluidAttributes.getColor(fluidStack);

        VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
        //percent = percent / 2; // we only need half block liquid.
        //for (int i = 0; i < 4; i++) {
            //this.renderNorthFluidFace(fluidTexture, matrix4f, matrix3f, builder, color, percent);
            //matrix.mulPose(Vector3f.YP.rotationDegrees(90));
        //}
        this.renderTopFluidFace(fluidTexture, matrix4f, matrix3f, builder, color, percent);
        matrix.popPose();

    }

    private void renderTopFluidFace(TextureAtlasSprite sprite, Matrix4f matrix4f, Matrix3f normalMatrix, VertexConsumer builder, int color, float percent) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = ((color) & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        float width = 12 / 16f;
        float height = 7 / 16f;

        float minU = sprite.getU(3);
        float maxU = sprite.getU(13);
        float minV = sprite.getV(3);
        float maxV = sprite.getV(13);
        float test = percent * height;

        float test2 = percent * height;

        float pY = -height / 2 + percent * height;

        builder.vertex(matrix4f, -width / 2, pY , -width / 2).color(r, g, b, a)
                .uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();

        builder.vertex(matrix4f, -width / 2, pY, width / 2).color(r, g, b, a)
                .uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();

        builder.vertex(matrix4f, width / 2, pY, width / 2).color(r, g, b, a)
                .uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();

        builder.vertex(matrix4f, width / 2, pY, -width / 2).color(r, g, b, a)
                .uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 1, 0)
                .endVertex();
    }

    private void renderNorthFluidFace(TextureAtlasSprite sprite, Matrix4f matrix4f, Matrix3f normalMatrix, VertexConsumer builder, int color, float percent) {
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = ((color) & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        float width = 12 / 16f;
        float height = 16 / 16f;

        float minU = sprite.getU(3);
        float maxU = sprite.getU(13);
        float minV = sprite.getV(1);
        float maxV = sprite.getV(15 * percent);

        builder.vertex(matrix4f, -width / 2, -height / 2 + height * percent, (-width / 2) + 0.001f).color(r, g, b, a)
                .uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 0, 1)
                .endVertex();

        builder.vertex(matrix4f, width / 2, -height / 2 + height * percent, (-width / 2) + 0.001f).color(r, g, b, a)
                .uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 0, 1)
                .endVertex();

        builder.vertex(matrix4f, width / 2, -height / 2, (-width / 2) + 0.001f).color(r, g, b, a)
                .uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 0, 1)
                .endVertex();

        builder.vertex(matrix4f, -width / 2, -height / 2, (-width / 2) + 0.001f).color(r, g, b, a)
                .uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(normalMatrix, 0, 0, 1)
                .endVertex();
    }

    private TextureAtlasSprite getFluidStillSprite(FluidAttributes attributes, FluidStack fluidStack) {
        return Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(attributes.getStillTexture(fluidStack));
    }

    private TextureAtlasSprite getFluidFlowingSprite(FluidAttributes attributes, FluidStack fluidStack) {
        return Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(attributes.getFlowingTexture(fluidStack));
    }
}
