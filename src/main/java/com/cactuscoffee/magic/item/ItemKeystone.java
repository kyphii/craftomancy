package com.cactuscoffee.magic.item;

import com.cactuscoffee.magic.BlockRegister;
import com.cactuscoffee.magic.block.BlockSealedArcana;
import com.cactuscoffee.magic.data.Sounds;
import com.cactuscoffee.magic.utils.SpellUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemKeystone extends ModItem
{
    public ItemKeystone(String name) {
        super(name, false);
    }

    @Nonnull
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        Block block = world.getBlockState(pos).getBlock();
        if (block == BlockRegister.sealedArcana) {
            world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3F, true);
            if (!world.isRemote && !world.restoringBlockSnapshots) {
                float f = 0.7F;
                double d0 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                double d1 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                double d2 = (double)(world.rand.nextFloat() * f) + (double)(1.0F - f) * 0.5D;
                EntityItem entityitem = new EntityItem(world,
                        pos.getX() + d0, pos.getY() + d1, pos.getZ() + d2,
                        ((BlockSealedArcana) block).getLoot());
                entityitem.setPickupDelay(40);
                world.spawnEntity(entityitem);
            }
            world.playSound(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                        Sounds.SEALED_ARCANA_OPEN, SoundCategory.AMBIENT, 1F, 1F);
            world.setBlockToAir(pos);

            //Destroy this item
            ItemStack keystone = player.getHeldItem(hand);
            keystone.setCount(0);

            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    @Nonnull
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.EPIC;
    }
}