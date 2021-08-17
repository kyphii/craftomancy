package com.cactuscoffee.magic.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemArcaneSoul extends ModItem implements IChargeableItem {

    private int MAX_CHARGE = 1000;

    public ItemArcaneSoul(String name) {
        super(name);
        setMaxDamage(MAX_CHARGE + 1);
        setNoRepair();
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (getDamage(stack) == 0) {
            setDamage(stack, MAX_CHARGE + 1);
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        IChargeableItem.addChargeTooltip(stack, tooltip);
    }
}
