package com.cactuscoffee.magic.container;

import com.cactuscoffee.magic.ItemRegister;
import com.cactuscoffee.magic.item.IChargeableItem;
import com.cactuscoffee.magic.tileentity.TileEntityManaCollector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerManaCollector extends Container {
    private TileEntityManaCollector manaCollector;
    private int progress;

    public ContainerManaCollector(InventoryPlayer inventory, TileEntityManaCollector entity) {
        manaCollector = entity;

        addSlotToContainer(new Slot(entity, 0, 80, 21));
        addSlotToContainer(new Slot(entity, 1, 80, 62));

        //Player Inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++)
                this.addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 94 + i * 18));
        }
        for (int i = 0; i < 9; i++)
            this.addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 152));
    }

    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, this.manaCollector);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener l : listeners) {
            if (this.progress != this.manaCollector.getField(0)) {
                l.sendWindowProperty(this, 0, this.manaCollector.getField(0));
            }
        }
        this.progress = this.manaCollector.getField(0);
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data) {
        manaCollector.setMana(data);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index > 1) {
                if (itemstack1.getItem() instanceof IChargeableItem) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (itemstack1.getItem() == ItemRegister.manaCrystal) {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index < 29) {
                    if (!this.mergeItemStack(itemstack1, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index < 38 && !this.mergeItemStack(itemstack1, 2, 29, false)) {
                    return ItemStack.EMPTY;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 2, 38, false)) {
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
        return manaCollector.isUsableByPlayer(player);
    }
}
