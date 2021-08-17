package com.cactuscoffee.magic.gui;

import com.cactuscoffee.magic.BlockRegister;
import com.cactuscoffee.magic.MagicMod;
import com.cactuscoffee.magic.container.ContainerArcaneInfuser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class GuiArcaneInfuser extends GuiContainer {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MagicMod.MODID, "textures/gui/arcane_infuser.png");

    private final ContainerArcaneInfuser container;

    public GuiArcaneInfuser(InventoryPlayer inventoryPlayer, World world, BlockPos pos) {
        super(new ContainerArcaneInfuser(inventoryPlayer, world, pos));
        this.container = (ContainerArcaneInfuser) inventorySlots;
        xSize = 176;
        ySize = 166;
    }


    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = I18n.format(BlockRegister.arcaneInfuser.getLocalizedName());
        fontRenderer.drawString(s, xSize / 2 - fontRenderer.getStringWidth(s) / 2, 6, GuiHandler.TEXT_COLOR);

        fontRenderer.drawString(I18n.format("container.inventory"), 8, ySize - 94, GuiHandler.TEXT_COLOR);
    }

    @Override
    public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        int progressScaled = container.getProgressVisual(46);
        drawTexturedModalRect(guiLeft + 63, guiTop + 35, 63, 166, progressScaled, 16);
        if (container.getWorkState() == -1) {
            drawTexturedModalRect(guiLeft + 131, guiTop + 33, 131, 166, 22, 22);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }
}