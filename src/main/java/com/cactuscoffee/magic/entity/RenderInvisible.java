package com.cactuscoffee.magic.entity;

import com.cactuscoffee.magic.MagicMod;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class RenderInvisible extends Render<Entity> {

    private static ResourceLocation TEXTURE = new ResourceLocation(MagicMod.MODID,
            "textures/entity/magic_orb_black.png");

    public RenderInvisible(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    public void doRender(@Nonnull Entity entity, double x, double y, double z,
                         float entityYaw, float partialTicks) {
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull Entity entity) {
        return null;
    }
}
