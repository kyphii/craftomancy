package com.cactuscoffee.magic.block;

import net.minecraft.item.Item;

public interface IRegistrableBlock {
    void registerItemModel(Item itemBlock);
    Item getItemBlock();
}
