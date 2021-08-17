package com.cactuscoffee.magic.entity;

import com.cactuscoffee.magic.MagicMod;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class RenderMagicOrb extends Render<EntityMagicOrb> {

    private static ResourceLocation TEXTURE_RED = new ResourceLocation(MagicMod.MODID,
            "textures/entity/magic_orb_red.png");
    private static ResourceLocation TEXTURE_YELLOW = new ResourceLocation(MagicMod.MODID,
            "textures/entity/magic_orb_yellow.png");
    private static ResourceLocation TEXTURE_GREEN = new ResourceLocation(MagicMod.MODID,
            "textures/entity/magic_orb_green.png");
    private static ResourceLocation TEXTURE_BLUE = new ResourceLocation(MagicMod.MODID,
            "textures/entity/magic_orb_blue.png");
    private static ResourceLocation TEXTURE_BLACK = new ResourceLocation(MagicMod.MODID,
            "textures/entity/magic_orb_black.png");
    private static ResourceLocation TEXTURE_WHITE = new ResourceLocation(MagicMod.MODID,
            "textures/entity/magic_orb_white.png");

    public RenderMagicOrb(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public boolean shouldRender(EntityMagicOrb livingEntity, ICamera camera, double camX, double camY, double camZ) {
        return !livingEntity.isInvisible();
    }

    public void doRender(@Nonnull EntityMagicOrb entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        bindEntityTexture(entity);
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.enableRescaleNormal();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.6F, 0.6F, 0.6F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((float)(this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        if (renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        double frameY = (entity.ticksExisted % 4) / 4D;

        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        bufferbuilder.pos(-0.5D, -0.25D, 0.0D).tex(0.0D, frameY + 0.25D).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(0.5D, -0.25D, 0.0D).tex(1.0D, frameY + 0.25D).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(0.5D, 0.75D, 0.0D).tex(1.0D, frameY).normal(0.0F, 1.0F, 0.0F).endVertex();
        bufferbuilder.pos(-0.5D, 0.75D, 0.0D).tex(0.0D, frameY).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();

        if (renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private double getAnimFrame(float partialTicks) {
        if (partialTicks < 0.25) {
            return 0.0;
        }
        else if (partialTicks < 0.50) {
            return 0.25;
        }
        else if (partialTicks < 0.75) {
            return 0.50;
        }
        else return 0.75;
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityMagicOrb entity) {
        switch (entity.getElement()) {
            case RED:
                return TEXTURE_RED;
            case YELLOW:
                return TEXTURE_YELLOW;
            case GREEN:
                return TEXTURE_GREEN;
            case BLUE:
                return TEXTURE_BLUE;
            case BLACK:
                return TEXTURE_BLACK;
            case WHITE:
                return TEXTURE_WHITE;
        }
        //Default, should never happen
        return TEXTURE_RED;
    }
}
