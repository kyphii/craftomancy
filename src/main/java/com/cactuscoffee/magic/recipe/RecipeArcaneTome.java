package com.cactuscoffee.magic.recipe;

import com.cactuscoffee.magic.ItemRegister;
import com.cactuscoffee.magic.data.Spells;
import com.cactuscoffee.magic.item.ItemArcaneTome;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class RecipeArcaneTome extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    private ItemStack resultItem = ItemStack.EMPTY;
    private int resultIndex;

    @Override
    public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World worldIn) {
        resultItem = ItemStack.EMPTY;

        int width = inv.getWidth();

        //Check book in middle
        boolean bookFound = false;
        int bookIndex;
        for (bookIndex = width; bookIndex < inv.getSizeInventory() - width; ++bookIndex) {
            if (inv.getStackInSlot(bookIndex).getItem() == Items.BOOK) {
                bookFound = true;
                break;
            }
        }
        if (!bookFound) {
            return false;
        }

        if (inv.getSizeInventory() < 9 || bookIndex < 4) {
            return false;
        }

        for (int i = 0; i < bookIndex - width - 1; ++i) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        for (int i = bookIndex + width + 2; i < inv.getSizeInventory(); ++i) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }

        //Check crystals match
        if (!inv.getStackInSlot(bookIndex - width).getItem().equals(ItemRegister.manaCrystal)
                || !inv.getStackInSlot(bookIndex - 1).getItem().equals(ItemRegister.manaCrystal)
                || !inv.getStackInSlot(bookIndex + 1).getItem().equals(ItemRegister.manaCrystal)
                || !inv.getStackInSlot(bookIndex + width).getItem().equals(ItemRegister.manaCrystal)) {
            return false;
        }

        //Check ingredients match
        Item ingredient = inv.getStackInSlot(bookIndex - width - 1).getItem();
        int meta = inv.getStackInSlot(bookIndex - width - 1).getMetadata();
        if (ingredientMatches(inv, bookIndex - width + 1, ingredient)
                && ingredientMatches(inv, bookIndex + width - 1, ingredient)
                && ingredientMatches(inv, bookIndex + width + 1, ingredient)) {
            return validCombination(ingredient, meta) != -1;
        }

        return false;
    }

    private boolean ingredientMatches(InventoryCrafting inv, int index, Item ingredient) {
        return inv.getStackInSlot(index).getItem() == ingredient;
    }

    private int validCombination(Item ingredient, int meta) {
        resultIndex = Spells.getSpellForCrafting(ingredient, meta);
        resultItem = ItemArcaneTome.getFromIndex(resultIndex);
        return resultIndex;
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
        return this.resultItem;
    }
}
