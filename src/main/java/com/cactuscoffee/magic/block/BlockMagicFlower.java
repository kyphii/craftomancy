package com.cactuscoffee.magic.block;

import com.cactuscoffee.magic.BlockRegister;
import com.cactuscoffee.magic.data.Element;
import com.cactuscoffee.magic.MagicMod;
import com.cactuscoffee.magic.data.IElementalMaterial;
import com.cactuscoffee.magic.data.PotionRef;
import com.cactuscoffee.magic.data.Sounds;
import com.cactuscoffee.magic.utils.SpellUtils;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockMagicFlower extends BlockBush implements IRegistrableBlock, IElementalMaterial {

    private static final int SCORCHBLOOM_FIRETIME = 5;
    private static final DamageSource GLOOMTHORN_DAMAGE = new DamageSource("gloomthorn");
    private static final float BRIGHTROOT_LIGHT = 0.5F;

    private final String name;
    private final Element variant;

    public BlockMagicFlower(String name, Element variant) {
        super(Material.PLANTS);

        this.name = name;
        this.variant = variant;

        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(MagicMod.creativeTab);

        BlockRegister.addBlockToList(this);

        setSoundType(SoundType.PLANT);

        if (variant == Element.WHITE) { //Brightroot
            setLightLevel(BRIGHTROOT_LIGHT);
        }
        setTickRandomly(true);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn instanceof EntityLivingBase) {
            switch (variant) {
                case RED: //Scorchbloom
                    entityIn.setFire(SCORCHBLOOM_FIRETIME);
                    break;
                case GREEN: //Rockgrass
                    ((EntityLivingBase) entityIn).addPotionEffect(
                            new PotionEffect(PotionRef.POISON, 200, 1));
                    break;
                case BLUE: //Frostbell
                    ((EntityLivingBase) entityIn).addPotionEffect(
                            new PotionEffect(PotionRef.SLOWNESS, 200, 1));
                    break;
                case BLACK: //Gloomthorn
                    entityIn.attackEntityFrom(GLOOMTHORN_DAMAGE, 1.0F);
                    break;
            }
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos)
                && !worldIn.getBlockState(pos).getMaterial().isLiquid()
                && canExistOn(worldIn.getBlockState(pos.down()).getBlock());
    }

    @Override
    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
        if (state.getBlock() == this) {
            return canExistOn(worldIn.getBlockState(pos.down()).getBlock());
        }
        return this.canSustainBush(worldIn.getBlockState(pos.down()));
    }

    public boolean canOverwrite(World world, BlockPos blockPos) {
        return world.mayPlace(this, blockPos, true, EnumFacing.DOWN, null)
                || world.isAirBlock(blockPos);
    }

    private boolean canExistOn(Block block) {
        if (variant == Element.GREEN) {
            return block == Blocks.STONE;
        }
        else if (variant == Element.RED) {
            return block instanceof BlockSand;
        }
        else return block == Blocks.GRASS || block == Blocks.DIRT;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        //Fluteweed Sounds
        if (variant == Element.YELLOW && rand.nextInt(25) == 0) {
            worldIn.playSound(
                    pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                    Sounds.FLUTEWEED, SoundCategory.AMBIENT,
                    0.3F, rand.nextFloat() + 0.5F, false);
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        //Don't spread if >9 are in 5x3x5 area around this
        int i = 9;
        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-2, -1, -2), pos.add(2, 1, 2))) {
            if (worldIn.getBlockState(blockpos).getBlock() == this) {
                --i;
                if (i <= 0) {
                    return;
                }
            }
        }
        if (rand.nextInt(15) == 0) {
            BlockPos newPos = new BlockPos(
                    pos.getX() + rand.nextInt(3) - 1,
                    pos.getY() + rand.nextInt(2) - rand.nextInt(2),
                    pos.getZ() + rand.nextInt(3) - 1);
            if (canOverwrite(worldIn, newPos) && canExistOn(worldIn.getBlockState(newPos.down()).getBlock())) {
                worldIn.setBlockState(newPos, this.getDefaultState());
            }
        }
    }

    @Override
    public Element getElement() {
        return variant;
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