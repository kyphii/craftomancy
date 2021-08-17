package com.cactuscoffee.magic.tileentity;

import com.cactuscoffee.magic.ItemRegister;
import com.cactuscoffee.magic.container.ContainerEssenceExtractor;
import com.cactuscoffee.magic.data.IElementalMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityEssenceExtractor extends TileEntityLockable implements ITickable, ISidedInventory {

    private static final int TICKS_TO_FINISH = 200;

    //Slot IDs
    //Magic Material Input
    private static final int SLOT_INPUT1 = 0;
    //Mana Crystal Input
    private static final int SLOT_INPUT2 = 1;
    //Output
    private static final int SLOT_OUTPUT = 2;

    private String customName;

    private int progress;
    private boolean isWorking;

    private ItemStackHandler slots;

    private Item outputType;

    public TileEntityEssenceExtractor() {
        progress = 0;
        slots = new ItemStackHandler(3);
        outputType = null;
    }

    public void update() {
        if (!world.isRemote) {
            if (canWork()) {
                isWorking = true;
                ++progress;
                if (progress >= TICKS_TO_FINISH) {
                    progress = 0;
                    work();
                }
                markDirty();
            }
            else {
                isWorking = false;
                progress = 0;
            }
        }
    }

    private void work() {
        ItemStack stack = new ItemStack(outputType);

        if (slots.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
            slots.setStackInSlot(SLOT_OUTPUT, stack.copy());
        } else if (slots.getStackInSlot(SLOT_OUTPUT).getItem() == stack.getItem()) {
            slots.getStackInSlot(SLOT_OUTPUT).grow(1);
        }

        slots.getStackInSlot(SLOT_INPUT1).shrink(1);
        slots.getStackInSlot(SLOT_INPUT2).shrink(1);
        if (slots.getStackInSlot(SLOT_INPUT1).getCount() <= 0) {
            slots.setStackInSlot(SLOT_INPUT1, ItemStack.EMPTY);
        }
        if (slots.getStackInSlot(SLOT_INPUT2).getCount() <= 0) {
            slots.setStackInSlot(SLOT_INPUT2, ItemStack.EMPTY);
        }
    }

    /*
     * Returns output item stack for convenience (TRUE) or null (FALSE)
     */
    private boolean canWork() {
        if (outputType == null) {
            return false;
        }
        if (slots.getStackInSlot(SLOT_INPUT1).isEmpty()
                || slots.getStackInSlot(SLOT_INPUT2).isEmpty()) {
            return false;
        }
        else {
            Item outputItem = getOutputItem(slots.getStackInSlot(SLOT_INPUT1));
            if (outputItem != null) {
                ItemStack itemStack = new ItemStack(outputItem);
                if (slots.getStackInSlot(SLOT_OUTPUT).isEmpty()) {
                    return true;
                }
                if (!slots.getStackInSlot(SLOT_OUTPUT).isItemEqual(itemStack)) {
                    return false;
                }
                int result = slots.getStackInSlot(SLOT_OUTPUT).getCount() + itemStack.getCount();
                return result <= getInventoryStackLimit() && result <= slots.getStackInSlot(SLOT_OUTPUT).getMaxStackSize();
            }
            return false;
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
        compound.setInteger("Progress", progress);

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

        progress = compound.getInteger("Progress");

        if (compound.hasKey("CustomName", 8)) {
            customName = compound.getString("CustomName");
        }

        outputType = getOutputItem(slots.getStackInSlot(SLOT_INPUT1));
    }

    @Override
    public boolean isItemValidForSlot(int index, @Nonnull ItemStack stack) {
        if (index == SLOT_INPUT1)
            return isAcceptableInput(stack);
        else return index == SLOT_INPUT2 && stack.getItem() == ItemRegister.manaCrystal;
    }

    public static boolean isAcceptableInput(ItemStack stack) {
        return getInputMaterial(stack) != null;
    }

    /*
     * If input is acceptable material, return it
     * Else, return null
     */
    private static IElementalMaterial getInputMaterial(ItemStack input) {
        IElementalMaterial material = null;
        Item inputItem = input.getItem();
        if (inputItem instanceof IElementalMaterial) {
            material = (IElementalMaterial) inputItem;
        }
        else if (inputItem instanceof ItemBlock) {
            Block inputBlock = ((ItemBlock) inputItem).getBlock();
            if (inputBlock instanceof  IElementalMaterial) {
                material = (IElementalMaterial) inputBlock;
            }
        }
        return material;
    }

    private static Item getOutputItem(ItemStack input) {
        IElementalMaterial material = getInputMaterial(input);
        if (material == null)
            return null;
        switch (material.getElement()) {
            case RED:
                return ItemRegister.chargedCrystalRed;
            case YELLOW:
                return ItemRegister.chargedCrystalYellow;
            case GREEN:
                return ItemRegister.chargedCrystalGreen;
            case BLUE:
                return ItemRegister.chargedCrystalBlue;
            case BLACK:
                return ItemRegister.chargedCrystalBlack;
            case WHITE:
                return ItemRegister.chargedCrystalWhite;
        }
        return null;
    }

    public void setCustomInventoryName(String displayName) {
        this.customName = displayName;
    }

    @Nonnull
    @Override
    public String getName() {
        return hasCustomName() ? customName : "container.essence_extractor";
    }

    @Override
    public boolean hasCustomName() {
        return customName != null && !customName.isEmpty();
    }

    @Nonnull
    @Override
    public Container createContainer(@Nonnull InventoryPlayer playerInventory, @Nonnull EntityPlayer playerIn) {
        return new ContainerEssenceExtractor(playerInventory, this);
    }

    @Nonnull
    @Override
    public String getGuiID() {
        return "tfmagic2:essence_extractor";
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
            if (index == SLOT_INPUT1) {
                outputType = null;
            }
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
        if (index == SLOT_INPUT1) {
            outputType = getOutputItem(stack);
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
                return progress;
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                progress = value;
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

    public int getProgressScaled(int i) {
        return progress * i / TICKS_TO_FINISH;
    }

    public boolean isWorking() {
        return isWorking;
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

    public void setProgress(int progress) {
        this.progress = progress;
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
        if (side == EnumFacing.DOWN) {
            return new int[]{SLOT_OUTPUT};
        }
        else {
            return side == EnumFacing.UP ? new int[]{SLOT_INPUT1} : new int[]{SLOT_INPUT2};
        }
    }

    @Override
    public boolean canInsertItem(int index, @Nonnull ItemStack itemStackIn, @Nonnull EnumFacing direction) {
        return this.isItemValidForSlot(index, itemStackIn);
    }

    @Override
    public boolean canExtractItem(int index, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
        return true;
    }
}