package com.cactuscoffee.magic.tileentity;

import com.cactuscoffee.magic.ItemRegister;
import com.cactuscoffee.magic.container.ContainerManaCollector;
import com.cactuscoffee.magic.item.IChargeableItem;
import com.cactuscoffee.magic.item.ItemStaff;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityManaCollector extends TileEntityLockable implements ITickable, ISidedInventory {

    private static final int MANA_MAX = 5000;
    private static final float MANA_RATE = 80;

    //Slot IDs
    //Magic Material Input
    private static final int SLOT_INPUT1 = 0;
    //Mana Crystal Input
    private static final int SLOT_INPUT2 = 1;

    private String customName;

    private int mana;
    private int progress;

    private ItemStackHandler slots;

    public TileEntityManaCollector() {
        mana = 0;
        progress = 0;
        slots = new ItemStackHandler(3);
    }

    public void update() {
        if (!world.isRemote) {
            if (progress < MANA_RATE) {
                ++progress;
            }
            else if (mana < MANA_MAX) {
                progress = 0;
                ++mana;
            }

            if (mana > MANA_MAX) {
                mana = MANA_MAX;
            }
            else if (mana < MANA_MAX && !slots.getStackInSlot(SLOT_INPUT2).isEmpty()) {
                Item fuel = slots.getStackInSlot(SLOT_INPUT2).getItem();
                if (fuel == ItemRegister.manaCrystal) {
                    mana += IChargeableItem.CHARGE_AMOUNT;
                    slots.getStackInSlot(SLOT_INPUT2).shrink(1);
                    if (slots.getStackInSlot(SLOT_INPUT2).getCount() == 0) {
                        slots.setStackInSlot(SLOT_INPUT2, ItemStack.EMPTY);
                    }
                }
                else if (fuel instanceof IChargeableItem) {
                    ItemStack fuelStack = slots.getStackInSlot(SLOT_INPUT2);
                    if (fuelStack.getItemDamage() <= fuelStack.getMaxDamage() - 1) {
                        mana += 1;
                        fuelStack.setItemDamage(fuelStack.getItemDamage() + 1);
                    }
                }
            }

            if (slots.getStackInSlot(SLOT_INPUT1).getItem() instanceof IChargeableItem && mana > 0) {
                if (IChargeableItem.charge(slots.getStackInSlot(SLOT_INPUT1), 1)) {
                    --mana;
                }
            }
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        //Save Inventory
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < slots.getSlots(); ++i) {
            if (!slots.getStackInSlot(i).isEmpty()) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte) i);
                slots.getStackInSlot(i).writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }
        compound.setTag("Items", nbttaglist);

        //Save Progress
        compound.setInteger("Mana", mana);

        if (hasCustomName()) {
            compound.setString("CustomName", customName);
        }
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        NBTTagList nbttaglist = compound.getTagList("Items", 10);
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b = nbttagcompound1.getByte("Slot");
            if (b >= 0 && b < slots.getSlots()) {
                slots.setStackInSlot(b, new ItemStack(nbttagcompound1));
            }
        }

        mana = compound.getInteger("Mana");

        if (compound.hasKey("CustomName", 8)) {
            customName = compound.getString("CustomName");
        }
    }

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        Item i = stack.getItem();
        if (index == SLOT_INPUT1) {
            return i instanceof IChargeableItem;
        }
        else {
            return i == ItemRegister.manaCrystal;
        }
    }

    public void setCustomInventoryName(String displayName) {
        this.customName = displayName;
    }

    @Nonnull
    @Override
    public String getName() {
        return hasCustomName() ? customName : "container.mana_collector";
    }

    @Override
    public boolean hasCustomName() {
        return customName != null && !customName.isEmpty();
    }

    @Nonnull
    @Override
    public Container createContainer(@Nonnull InventoryPlayer playerInventory, @Nonnull EntityPlayer playerIn) {
        return new ContainerManaCollector(playerInventory, this);
    }

    @Nonnull
    @Override
    public String getGuiID() {
        return "tfmagic2:mana_collector";
    }

    @Override
    public int getSizeInventory() {
        return slots.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < slots.getSlots(); ++i) {
            if (!slots.getStackInSlot(i).isEmpty())
                return false;
        }
        return true;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int index) {
        return slots.getStackInSlot(index);
    }

    @Nonnull
    @Override
    public ItemStack decrStackSize(int index, int count) {
        return index >= 0 && index < slots.getSlots()
                && !(slots.getStackInSlot(index)).isEmpty()
                && count > 0 ? (slots.getStackInSlot(index)).splitStack(count) : ItemStack.EMPTY;
    }

    @Nonnull
    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index >= 0 && index < slots.getSlots()) {
            ItemStack stack = slots.getStackInSlot(index);
            slots.setStackInSlot(index, ItemStack.EMPTY);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        slots.setStackInSlot(index, stack);
        if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
        return world.getTileEntity(pos) == this && player.getDistanceSq(
                pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64D;
    }

    @Override
    public void openInventory(@Nonnull EntityPlayer player) {}

    @Override
    public void closeInventory(@Nonnull EntityPlayer player) {}

    @Override
    public int getField(int id) {
        switch (id) {
            case 0:
                return mana;
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                mana = value;
        }
    }

    @Override
    public int getFieldCount() {
        return 1;
    }

    @Override
    public void clear() {
        for (int i = 0; i < slots.getSlots(); ++i) {
            slots.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public int getManaScaled(int i) {
        return mana * i / MANA_MAX;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, @Nonnull IBlockState oldState, @Nonnull IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return this.getCapability(capability, facing) != null;
    }

    private net.minecraftforge.items.IItemHandler handlerTop =
            new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.EnumFacing.UP);
    private net.minecraftforge.items.IItemHandler handlerBottom =
            new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.EnumFacing.DOWN);
    private net.minecraftforge.items.IItemHandler handlerSide =
            new net.minecraftforge.items.wrapper.SidedInvWrapper(this, net.minecraft.util.EnumFacing.WEST);

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            if (facing == EnumFacing.DOWN)
                return (T) handlerBottom;
            else if (facing == EnumFacing.UP)
                return (T) handlerTop;
            else
                return (T) handlerSide;
        return super.getCapability(capability, facing);
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.getPos(), 0, this.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.setPos(pkt.getPos());
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Nonnull
    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.UP || side == EnumFacing.DOWN) {
            return new int[]{SLOT_INPUT1};
        }
        return new int[]{SLOT_INPUT2};
    }

    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull EnumFacing direction) {
        if (index == SLOT_INPUT1) {
            return this.isItemValidForSlot(index, itemStackIn) && itemStackIn.getItemDamage() > 0;
        }
        else {
            return this.isItemValidForSlot(index, itemStackIn);
        }
    }

    @Override
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
        return index == SLOT_INPUT1 && stack.getItemDamage() == 0;
    }

    public int getMana() {
        return mana;
    }
}