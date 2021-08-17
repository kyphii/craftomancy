package com.cactuscoffee.magic.entity;

import com.cactuscoffee.magic.MagicMod;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nullable;

public class RenderGuardStar extends Render<EntityGuardStar> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MagicMod.MODID,
            "textures/entity/white_hole.png");
    private final ModelBase MODEL = new ModelEnderCrystal(0.0F, false);

    public RenderGuardStar(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    public void doRender(EntityGuardStar entity, double x, double y, double z, float entityYaw, float partialTicks) {
        float f = (float) entity.ticksExisted + partialTicks;
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.translate((float)x, (float)y + 0.5, (float)z);
        this.bindTexture(TEXTURE);
        float f1 = MathHelper.sin(f * 0.2F) / 2.0F + 0.5F;
        f1 = f1 * f1 + f1;

        if (this.renderOutlines)
        {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
        }

        MODEL.render(entity, 0.0F, f * 3.0F, f1 * 0.03F, 0.0F, 0.0F, 0.03F);

        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }

        GlStateManager.enableLighting();
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityGuardStar entity) {
        return TEXTURE;
    }
}
