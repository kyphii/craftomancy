package com.cactuscoffee.magic.data;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

public enum Element {
    RED("red", 0),
    YELLOW("yellow", 1),
    GREEN("green", 2),
    BLUE("blue", 3),
    BLACK("black", 4),
    WHITE("white", 5);

    private final String name;
    private final int meta;

    Element(String name, int meta) {
        this.name = name;
        this.meta = meta;
    }

    public String getName() {
        return name;
    }

    public String getNameProper() {
        return I18n.translateToLocal("tfmagic2.tooltip." + name);
    }

    public int getMeta() {
        return meta;
    }

    public static TextFormatting getTextColor(Element element) {
        switch (element) {
            case RED:
                return TextFormatting.RED;
            case YELLOW:
                return TextFormatting.YELLOW;
            case GREEN:
                return TextFormatting.GREEN;
            case BLUE:
                return TextFormatting.BLUE;
            case BLACK:
                return TextFormatting.DARK_PURPLE;
            case WHITE:
                return TextFormatting.WHITE;
        }
        return TextFormatting.RESET;
    }
}
