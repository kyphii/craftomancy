package com.cactuscoffee.magic.container;

import com.cactuscoffee.magic.BlockRegister;
import com.cactuscoffee.magic.ItemRegister;
import com.cactuscoffee.magic.data.Spell;
import com.cactuscoffee.magic.data.Spells;
import com.cactuscoffee.magic.item.ItemChargedCrystal;
import com.cactuscoffee.magic.item.ItemStaff;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ContainerArcaneInfuser extends Container {
    private static final int CRYSTALS_PER_LEVEL = 5;

    private static final byte STATE_STANDBY = 0;
    private static final byte STATE_INVALID = -1;
    private static final byte STATE_WORKING = -2;
    private static final byte STATE_DONE = -3;

    //Slot IDs
    private static final int SLOT_INPUT_STAFF = 1;
    private static final int SLOT_INPUT_TOME = 2;
    private static final int SLOT_INPUT_CRYSTAL = 3;
    private static final int SLOT_OUTPUT = 0;

    private final World world;
    private final BlockPos pos;
    private final InventoryBasic inventory;

    private Spell spell;
    private int workState;

    public ContainerArcaneInfuser(InventoryPlayer inventory, World world, BlockPos pos) {
        this.world = world;
        this.pos = pos;

        this.inventory = new InventoryBasic("arcane_infuser_TEST", false, 4);

        //Input Slots
        this.addSlotToContainer(new SlotInfuserInput(this.inventory, SLOT_INPUT_STAFF, 16, 24));
        this.addSlotToContainer(new SlotInfuserInput(this.inventory, SLOT_INPUT_TOME, 16, 46));
        this.addSlotToContainer(new SlotInfuserInput(this.inventory, SLOT_INPUT_CRYSTAL, 38, 35));

        //Output Slots
        this.addSlotToContainer(new SlotInfuserOutput(inventory.player, this.inventory, SLOT_OUTPUT, 133, 35));

        //Player Inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++)
                this.addSlotToContainer(new Slot(inventory, j + (i * 9) + 9, 8 + (j * 18), 84 + (i * 18)));
        }
        for (int i = 0; i < 9; i++) {
            this.addSlotToContainer(new Slot(inventory, i, 8 + (i * 18), 142));
        }
    }

    public void updateState() {
        if (isTomeValid(inventory.getStackInSlot(SLOT_INPUT_TOME))) {
            spell = Spells.getFromList(inventory.getStackInSlot(SLOT_INPUT_TOME).getTagCompound().getInteger("Spell"));
        } else {
            spell = null;
        }

        workState = calculateWorkState();
        if (workState == STATE_WORKING) {
            work();
            workState = STATE_DONE;
        } else if (workState != STATE_DONE && !inventory.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
            inventory.setInventorySlotContents(SLOT_OUTPUT, ItemStack.EMPTY);
        }
    }

    public byte calculateWorkState() {
        boolean missingItem = false;

        if (!isStaffValid(inventory.getStackInSlot(SLOT_INPUT_STAFF)) || spell == null) {
            missingItem = true;
        }
        else if (this.spell.getLevel() > ((ItemStaff) inventory.getStackInSlot(SLOT_INPUT_STAFF).getItem()).getLevel()) {
            return STATE_INVALID;
        }

        if (!isCrystalValid(inventory.getStackInSlot(SLOT_INPUT_CRYSTAL))) {
            missingItem = true;
        }
        else if (spell != null) {
            if (this.spell.getElement() != ((ItemChargedCrystal) inventory.getStackInSlot(SLOT_INPUT_CRYSTAL).getItem()).getElement()) {
                return STATE_INVALID;
            }
            else {
                int itemNumber = inventory.getStackInSlot(SLOT_INPUT_CRYSTAL).getCount();
                if (spell.getLevel() * CRYSTALS_PER_LEVEL > itemNumber) {
                    return (byte) itemNumber;
                }
            }
        }

        if (missingItem) {
            return STATE_STANDBY;
        }
        else if (!inventory.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
            return STATE_DONE;
        }
        else {
            return STATE_WORKING;
        }
    }

    private void work() {
        ItemStack stack = new ItemStack(inventory.getStackInSlot(SLOT_INPUT_STAFF).getItem());

        NBTTagCompound nbt;
        if (stack.hasTagCompound()) {
            nbt = stack.getTagCompound();
        }
        else {
            nbt = new NBTTagCompound();
        }

        nbt.setInteger("Spell", spell.getIndex());
        stack.setTagCompound(nbt);

        if (inventory.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
            inventory.setInventorySlotContents(SLOT_OUTPUT, stack);
        }
    }

    public static boolean isStaffValid(ItemStack stack) {
        return stack.getItem() instanceof ItemStaff &&
                (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Spell"));
    }

    public static boolean isTomeValid(ItemStack stack) {
        return stack.getItem() == ItemRegister.arcaneTome &&
                stack.hasTagCompound() && stack.getTagCompound().hasKey("Spell");
    }

    public static boolean isCrystalValid(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemChargedCrystal;
    }

    public int getProgressVisual(int max) {
        if (workState == STATE_DONE) {
            return max;
        }
        else if (spell != null && workState > 0) {
            return workState * max / (spell.getLevel() * CRYSTALS_PER_LEVEL);
        }
        else {
            return 0;
        }
    }

    public ItemStack shrinkStack(int index, int count) {
        return index >= 0 && index < inventory.getSizeInventory()
                && !(inventory.getStackInSlot(index)).isEmpty()
                && count > 0 ? (inventory.getStackInSlot(index)).splitStack(count) : ItemStack.EMPTY;
    }

    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        switch (index) {
            case SLOT_INPUT_STAFF:
                return isStaffValid(stack);
            case SLOT_INPUT_TOME:
                return isTomeValid(stack);
            case SLOT_INPUT_CRYSTAL:
                return isCrystalValid(stack);
        }
        return false;
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 0) {
                if (!this.mergeItemStack(itemstack1, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index > 3) {
                if (isItemValidForSlot(1, itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (isItemValidForSlot(2, itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (isItemValidForSlot(3, itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index < 31) {
                    if (!this.mergeItemStack(itemstack1, 31, 40, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index < 40) {
                    if (!this.mergeItemStack(itemstack1, 4, 31, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
            else if (!this.mergeItemStack(itemstack1, 4, 40, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
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
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);

        if (!this.world.isRemote) {
            inventory.setInventorySlotContents(SLOT_OUTPUT, ItemStack.EMPTY);
            this.clearContainer(playerIn, this.world, this.inventory);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        if (this.world.getBlockState(this.pos).getBlock() != BlockRegister.arcaneInfuser) {
            return false;
        }
        else {
            return playerIn.getDistanceSq(
                    (double) this.pos.getX() + 0.5D,
                    (double) this.pos.getY() + 0.5D,
                    (double) this.pos.getZ() + 0.5D)
                    <= 64.0D;
        }
    }

    public int getWorkState() {
        return workState;
    }

    private class SlotInfuserInput extends Slot {
        SlotInfuserInput(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public void onSlotChanged() {
            super.onSlotChanged();
            updateState();
        }
    }

    private class SlotInfuserOutput extends SlotOutput {
        public SlotInfuserOutput(EntityPlayer player, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
            super(player, inventoryIn, slotIndex, xPosition, yPosition);
        }

        @Override
        public void onSlotChanged() {
            super.onSlotChanged();
            if (workState == STATE_DONE && !this.getHasStack()) {
                inventory.setInventorySlotContents(SLOT_OUTPUT, ItemStack.EMPTY);
                inventory.setInventorySlotContents(SLOT_INPUT_TOME, ItemStack.EMPTY);
                inventory.setInventorySlotContents(SLOT_INPUT_STAFF, ItemStack.EMPTY);
                shrinkStack(SLOT_INPUT_CRYSTAL, spell.getLevel() * CRYSTALS_PER_LEVEL);

                world.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                        SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE,
                        SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.1F + 0.9F, false);

                spell = null;
                workState = STATE_STANDBY;
            }
        }
    }

}