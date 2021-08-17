package com.cactuscoffee.magic.gui;

import com.cactuscoffee.magic.MagicMod;
import com.cactuscoffee.magic.container.ContainerManaCollector;
import com.cactuscoffee.magic.tileentity.TileEntityManaCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiManaCollector extends GuiContainer {
    public static final ResourceLocation TEXTURE =
            new ResourceLocation(MagicMod.MODID, "textures/gui/mana_collector.png");

    public TileEntityManaCollector manaCollector;

    public GuiManaCollector(InventoryPlayer inventoryPlayer, TileEntityManaCollector entity) {
        super(new ContainerManaCollector(inventoryPlayer, entity));
        manaCollector = entity;
        xSize = 176;
        ySize = 176;
    }

    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = manaCollector.hasCustomName() ?
                manaCollector.getName() :
                I18n.format(manaCollector.getName());
        fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, GuiHandler.TEXT_COLOR);

        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, GuiHandler.TEXT_COLOR);

        fontRenderer.drawString(String.valueOf(manaCollector.getMana()), 50, 48, GuiHandler.TEXT_COLOR);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        int n = manaCollector.getManaScaled(78);
        drawTexturedModalRect(guiLeft + 49, guiTop + 47, 0, 176, n, 9);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
}