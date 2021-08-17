package com.cactuscoffee.magic.item;

import com.cactuscoffee.magic.ItemRegister;
import com.cactuscoffee.magic.MagicMod;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemModRecord extends ItemRecord implements IRegistrableItem{
    private final String name;

    public ItemModRecord(String name, SoundEvent soundIn) {
        super(name, soundIn);
        this.name = name;

        setUnlocalizedName(name);
        setRegistryName(name);

        this.setCreativeTab(MagicMod.creativeTabSecret);

        ItemRegister.addItemToList(this);
    }

    public void registerItemModel() {
        MagicMod.proxy.registerItemRenderer(this, 0, name);
    }
}
