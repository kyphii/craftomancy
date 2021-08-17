package com.cactuscoffee.magic.block;

import com.cactuscoffee.magic.ItemRegister;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockManaCrystalOre extends ModBlock {
    public BlockManaCrystalOre() {
        super("mana_crystal_ore", Material.ROCK, SoundType.STONE);
        this.setHardness(3F);
        this.setHarvestLevel("pickaxe", 0);
    }

    @Nonnull
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return ItemRegister.manaCrystal;
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }

    @Override
    public int quantityDroppedWithBonus(int fortune, @Nonnull Random random) {
        return quantityDropped(random) + random.nextInt(fortune + 1);
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull IBlockState state, @Nonnull RayTraceResult target,
                                  @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
        return new ItemStack(ItemRegister.manaCrystal, 1);
    }

    @Override
    public int getExpDrop(IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune) {
        if (this.getItemDropped(state, RANDOM, fortune) != Item.getItemFromBlock(this)) {
            return 1 + RANDOM.nextInt(2);
        }
        return 0;
    }
}
