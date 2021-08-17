package com.cactuscoffee.magic.item;

import com.cactuscoffee.magic.data.Element;
import com.cactuscoffee.magic.data.IElementalMaterial;

public class ItemMagite extends ModItem implements IElementalMaterial {
    private final Element variant;

    public ItemMagite(Element variant) {
        super("magite_" + variant.getName());
        this.variant = variant;
    }

    @Override
    public Element getElement() {
        return variant;
    }
}
