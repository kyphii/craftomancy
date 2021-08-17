package com.cactuscoffee.magic.gui;

import com.cactuscoffee.magic.container.ContainerArcaneInfuser;
import com.cactuscoffee.magic.container.ContainerEssenceExtractor;
import com.cactuscoffee.magic.container.ContainerManaCollector;
import com.cactuscoffee.magic.tileentity.TileEntityEssenceExtractor;
import com.cactuscoffee.magic.tileentity.TileEntityManaCollector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiHandler implements IGuiHandler {

    public static final int TEXT_COLOR = 4210752;

    public enum GuiId {
        GUI_ESSENCE_EXTRACTOR, GUI_ARCANE_INFUSER, GUI_MANA_COLLECTOR;
    }

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        if (tileEntity != null) {
            if (id == GuiId.GUI_ESSENCE_EXTRACTOR.ordinal()) {
                return new ContainerEssenceExtractor(player.inventory, (TileEntityEssenceExtractor) tileEntity);
            }
            else if (id == GuiId.GUI_MANA_COLLECTOR.ordinal()) {
                return new ContainerManaCollector(player.inventory, (TileEntityManaCollector) tileEntity);
            }
        }
        else if (id == GuiId.GUI_ARCANE_INFUSER.ordinal()) {
            return new ContainerArcaneInfuser(player.inventory, world, new BlockPos(x, y, z));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        if (tileEntity != null) {
            if (id == GuiId.GUI_ESSENCE_EXTRACTOR.ordinal()) {
                return new GuiEssenceExtractor(player.inventory, (TileEntityEssenceExtractor) tileEntity);
            }
            else if (id == GuiId.GUI_MANA_COLLECTOR.ordinal()) {
                return new GuiManaCollector(player.inventory, (TileEntityManaCollector) tileEntity);
            }
        }
        else if (id == GuiId.GUI_ARCANE_INFUSER.ordinal()) {
            return new GuiArcaneInfuser(player.inventory, world, new BlockPos(x, y, z));
        }
        return null;
    }
}