package com.cactuscoffee.magic.block;

import com.cactuscoffee.magic.BlockRegister;
import com.cactuscoffee.magic.MagicMod;
import com.cactuscoffee.magic.gui.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class BlockArcaneInfuser extends Block implements IRegistrableBlock {

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 1, .9375, 1);
    private final String name;

    public BlockArcaneInfuser() {
        super(Material.IRON);

        name = "arcane_infuser";

        this.setHardness(4F);
        this.setHarvestLevel("pickaxe", 0);

        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(MagicMod.creativeTab);

        setLightOpacity(0);

        setSoundType(SoundType.METAL);

        BlockRegister.addBlockToList(this);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isTopSolid(IBlockState state) {
        return false;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOUNDING_BOX;
    }

    /**
     * Called when the block is right clicked by a player.
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            FMLNetworkHandler.openGui(player, MagicMod.instance, GuiHandler.GuiId.GUI_ARCANE_INFUSER.ordinal(),
                    world, pos.getX(), pos.getY(), pos.getZ());
        }
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
}
