package com.cactuscoffee.magic.gui;

import com.cactuscoffee.magic.MagicMod;
import com.cactuscoffee.magic.container.ContainerEssenceExtractor;
import com.cactuscoffee.magic.tileentity.TileEntityEssenceExtractor;
import net.minecraft.client.resources.I18n;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiEssenceExtractor extends GuiContainer {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MagicMod.MODID, "textures/gui/essence_extractor.png");

    public TileEntityEssenceExtractor essenceExtractor;

    public GuiEssenceExtractor(InventoryPlayer inventoryPlayer, TileEntityEssenceExtractor entity) {
        super(new ContainerEssenceExtractor(inventoryPlayer, entity));
        essenceExtractor = entity;
        xSize = 176;
        ySize = 166;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = essenceExtractor.hasCustomName() ?
                essenceExtractor.getName() :
                I18n.format(essenceExtractor.getName());
        fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, GuiHandler.TEXT_COLOR);

        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, GuiHandler.TEXT_COLOR);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        int progressScaled = essenceExtractor.getProgressScaled(24);
        drawTexturedModalRect(guiLeft + 80, guiTop + 34, 176, 0, progressScaled, 16);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
}