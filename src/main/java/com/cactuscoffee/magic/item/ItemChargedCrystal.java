package com.cactuscoffee.magic.item;

import com.cactuscoffee.magic.data.Element;

public class ItemChargedCrystal extends ModItem {
    private final Element variant;

    public ItemChargedCrystal(Element variant) {
        super("charged_crystal_" + variant.getName());
        this.variant = variant;
    }

    public Element getElement() {
        return variant;
    }
}