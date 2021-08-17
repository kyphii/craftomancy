package com.cactuscoffee.magic.container;

import com.cactuscoffee.magic.ItemRegister;
import com.cactuscoffee.magic.tileentity.TileEntityEssenceExtractor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerEssenceExtractor extends Container
{
    private TileEntityEssenceExtractor essenceExtractor;
    private int progress;

    public ContainerEssenceExtractor(InventoryPlayer inventory, TileEntityEssenceExtractor entity) {
        essenceExtractor = entity;

        //Input Slots
        addSlotToContainer(new Slot(entity, 0, 56, 23));
        addSlotToContainer(new Slot(entity, 1, 56, 46));

        //Output Slots
        addSlotToContainer(new SlotOutput(inventory.player, entity, 2, 116, 35));

        //Player Inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++)
                this.addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
        }
        for (int i = 0; i < 9; i++)
            this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 142));
    }

    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.essenceExtractor);
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener l : listeners) {
            if (this.progress != this.essenceExtractor.getField(0)) {
                l.sendWindowProperty(this, 0, this.essenceExtractor.getField(0));
            }
        }
        this.progress = this.essenceExtractor.getField(0);
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        essenceExtractor.setProgress(data);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index == 2) {
                if (!this.mergeItemStack(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index > 1) {
                if (TileEntityEssenceExtractor.isAcceptableInput(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (itemstack1.getItem() == ItemRegister.manaCrystal) {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index < 30) {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            }
            else {
                slot.onSlotChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return essenceExtractor.isUsableByPlayer(player);
    }
}