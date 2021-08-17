package com.cactuscoffee.magic.recipe;

import com.cactuscoffee.magic.ItemRegister;
import com.cactuscoffee.magic.data.Element;
import com.cactuscoffee.magic.item.ItemChargedCrystal;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class RecipeMagicRobes extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    private ItemStack resultItem = ItemStack.EMPTY;
    private int resultIndex;

    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
        int width = inv.getWidth();

        Item s = ItemRegister.magicSilk;
        ItemChargedCrystal c = null;
        Item x = Items.AIR;

        int first = -1;
        for (int i = 0; i < inv.getSizeInventory(); ++i) {
            Item slotItem = inv.getStackInSlot(i).getItem();
            if (slotItem != x && first == -1) {
                first = i;
            }
            if (slotItem instanceof ItemChargedCrystal) {
                if (c == null) {
                    c = (ItemChargedCrystal) slotItem;
                }
                else if (slotItem != c) {
                    return false;
                }
            }
        }

        if (c != null) {
            Element element = c.getElement();

            int i = 0;
            while (i < first) {
                if (!inv.getStackInSlot(i).isEmpty()) {
                    return false;
                }
                else {
                    ++i;
                }
            }

            if (patternMatches(inv, i, width, s, s, s, c, x, c)) {
                return getFromList(element, 0);
            }
            if (patternMatches(inv, i, width, c, x, c, s, c, s, s, s, s)) {
                return getFromList(element, 1);
            }
            if (patternMatches(inv, i, width, s, s, s, s, x, s, c, x, c)) {
                return getFromList(element, 2);
            }
            if (patternMatches(inv, i, width, s, x, s, c, x, c)) {
                return getFromList(element, 3);
            }
        }

        return false;
    }

    private boolean getFromList(Element element, int equipSlotOffset) {
        resultIndex = (element.getMeta() * 4) + equipSlotOffset;
        resultItem = new ItemStack(ItemRegister.modArmors.get(resultIndex));
        return true;
    }

    private boolean patternMatches(InventoryCrafting inv, int i, int width, Item... items) {
        if (inv.getSizeInventory() < i + items.length) {
            return false;
        }

        boolean matches = true;

        for (int j = 0; j < 3; ++j) {
            Item slotItem;
            slotItem = inv.getStackInSlot(i + j).getItem();
            if (slotItem != items[j]) {
                matches = false;
                break;
            }
            slotItem = inv.getStackInSlot(i + j + width).getItem();
            if (slotItem != items[j + 3]) {
                matches = false;
                break;
            }
            if (items.length > 6) {
                slotItem = inv.getStackInSlot(i + j + (width * 2)).getItem();
                if (slotItem != items[j + 6]) {
                    matches = false;
                    break;
                }
            }
        }
        i += items.length;

        if (!matches) {
            return false;
        }

        while (i < inv.getSizeInventory()) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                return false;
            }
            else {
                ++i;
            }
        }

        return true;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
        ItemStack res = resultItem.copy();
        resultItem = ItemStack.EMPTY;
        resultIndex = -1;
        return res;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width > 2 && height > 2;
    }

    @Nonnull
    @Override
    public ItemStack getRecipeOutput() {
        return resultItem;
    }
}
