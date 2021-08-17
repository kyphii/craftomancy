package com.cactuscoffee.magic.block;

import com.cactuscoffee.magic.BlockRegister;
import com.cactuscoffee.magic.gui.GuiHandler;
import com.cactuscoffee.magic.MagicMod;
import com.cactuscoffee.magic.tileentity.TileEntityEssenceExtractor;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockEssenceExtractor extends BlockContainer implements IRegistrableBlock {

    private static final PropertyDirection FACING = BlockHorizontal.FACING;

    private final String name;

    public BlockEssenceExtractor() {
        super(Material.ROCK);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));

        this.name = "essence_extractor";

        this.setHardness(4F);
        this.setHarvestLevel("pickaxe", 0);

        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(MagicMod.creativeTab);

        setSoundType(SoundType.STONE);

        BlockRegister.addBlockToList(this);
    }

    @Override
    public void breakBlock(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        IItemHandler inventory = worldIn.getTileEntity(pos).getCapability
                (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            EntityItem entityIn;
            if (!stack.isEmpty()) {
                entityIn = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), stack);
                entityIn.setDefaultPickupDelay();
                worldIn.spawnEntity(entityIn);
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    /**
     * Called when the block is right clicked by a player.
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, MagicMod.instance, GuiHandler.GuiId.GUI_ESSENCE_EXTRACTOR.ordinal(),
                    world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);

        if (stack.hasDisplayName()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityEssenceExtractor) {
                ((TileEntityEssenceExtractor) tileentity).setCustomInventoryName(stack.getDisplayName());
            }
        }
    }

    /**
     * The type of render function called. MODEL for mixed tesr and static model, MODELBLOCK_ANIMATED for TESR-only,
     * LIQUID for vanilla liquids, INVISIBLE to skip all rendering
     */
    @Nonnull
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    @Nonnull
    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);
        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(FACING)).getIndex();
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityEssenceExtractor();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public void registerItemModel(Item itemBlock) {
        MagicMod.proxy.registerItemRenderer(itemBlock, 0, name);
    }

    @Override
    public Item getItemBlock() {
        return new ItemBlock(this).setRegistryName(getRegistryName());
    }

    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return Container.calcRedstone(worldIn.getTileEntity(pos));
    }
}
