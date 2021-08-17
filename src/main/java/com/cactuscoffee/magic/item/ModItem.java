package com.cactuscoffee.magic.item;

import com.cactuscoffee.magic.ItemRegister;
import com.cactuscoffee.magic.MagicMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ModItem extends Item implements IRegistrableItem {
    protected String name;

    public ModItem(String name) {
        this.name = name;
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(MagicMod.creativeTab);

        ItemRegister.addItemToList(this);
    }

    public ModItem(String name, boolean addCreativeTab) {
        this.name = name;
        setUnlocalizedName(name);
        setRegistryName(name);
        if (addCreativeTab) {
            setCreativeTab(MagicMod.creativeTab);
        }

        ItemRegister.addItemToList(this);
    }

    public void registerItemModel() {
        MagicMod.proxy.registerItemRenderer(this, 0, name);
    }
}
