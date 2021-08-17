package com.cactuscoffee.magic.item;

import com.cactuscoffee.magic.ItemRegister;
import com.cactuscoffee.magic.data.StringConstants;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public interface IChargeableItem {
    int CHARGE_AMOUNT = 20;

    static boolean charge(ItemStack stack, int chargeAmount) {
        if (stack.getItemDamage() > 1) {
            stack.setItemDamage(Math.max(stack.getItemDamage() - chargeAmount, 1));
            return true;
        }
        return false;
    }

    static void chargeInHand(EntityPlayerMP player) {
        ItemStack inHand = player.getHeldItemMainhand();
        if (inHand.getItem() instanceof IChargeableItem) {
            int toCharge = Math.min(CHARGE_AMOUNT, inHand.getItemDamage() - 1);
            for (ItemStack stack : player.inventory.mainInventory) {
                if (stack.getItem() == ItemRegister.arcaneSoul && stack != inHand
                        && stack.getItemDamage() <= stack.getMaxDamage() - toCharge) {
                    if (IChargeableItem.charge(inHand, toCharge)) {
                        stack.setItemDamage(stack.getItemDamage() + toCharge);
                    }
                    return;
                }
            }
            for (ItemStack stack : player.inventory.mainInventory) {
                if (stack.getItem() == ItemRegister.manaCrystal) {
                    if (IChargeableItem.charge(inHand, CHARGE_AMOUNT)) {
                        stack.shrink(1);
                    }
                    return;
                }
            }
        }

    }

    static void addChargeTooltip(ItemStack stack, List<String> tooltip) {
        if (stack.getItemDamage() > 0) {
            int charge = getCharge(stack);
            String data = StringConstants.CHARGE + ": ";
            if (charge == 0) {
                data += TextFormatting.DARK_RED + String.valueOf(charge) + TextFormatting.WHITE;
            } else if (charge <= stack.getMaxDamage() / 4) {
                data += TextFormatting.RED + String.valueOf(charge) + TextFormatting.WHITE;
            } else {
                data += TextFormatting.WHITE + String.valueOf(charge);
            }

            data += " / " + (stack.getMaxDamage() - 1);
            tooltip.add(data);
        }
    }

    static int getCharge(@Nonnull ItemStack stack) {
        int c = stack.getMaxDamage() - stack.getItemDamage();
        if (stack.getItemDamage() == 0) {
            return c - 1;
        }
        return c;
    }
}
