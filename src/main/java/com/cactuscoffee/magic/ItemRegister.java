package com.cactuscoffee.magic;

import com.cactuscoffee.magic.data.Element;
import com.cactuscoffee.magic.data.Sounds;
import com.cactuscoffee.magic.item.*;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class ItemRegister {

    private static List<IRegistrableItem> itemList = new ArrayList<>();

    /*============== ADD ITEMS HERE ==============*/

    public static ModItem manaCrystal = new ModItem("mana_crystal");

    public static ModItem magiteRed = new ItemMagite(Element.RED);
    public static ModItem magiteYellow = new ItemMagite(Element.YELLOW);
    public static ModItem magiteGreen = new ItemMagite(Element.GREEN);
    public static ModItem magiteBlue = new ItemMagite(Element.BLUE);
    public static ModItem magiteBlack = new ItemMagite(Element.BLACK);
    public static ModItem magiteWhite = new ItemMagite(Element.WHITE);

    public static ModItem chargedCrystalRed = new ItemChargedCrystal(Element.RED);
    public static ModItem chargedCrystalYellow = new ItemChargedCrystal(Element.YELLOW);
    public static ModItem chargedCrystalGreen = new ItemChargedCrystal(Element.GREEN);
    public static ModItem chargedCrystalBlue = new ItemChargedCrystal(Element.BLUE);
    public static ModItem chargedCrystalBlack = new ItemChargedCrystal(Element.BLACK);
    public static ModItem chargedCrystalWhite = new ItemChargedCrystal(Element.WHITE);

    public static ModItem magicSilk = new ModItem("magic_silk");

    public static ModItem staffWood = new ItemStaff("staff_wooden",         1, true);
    public static ModItem staffMetal = new ItemStaff("staff_metal",         2, true);
    public static ModItem staffObsidian = new ItemStaff("staff_obsidian",   3, true);
    public static ModItem staffHell = new ItemStaff("staff_hell",           4, true);
    public static ModItem staffGrand = new ItemStaff("staff_grand",         5, true);
    public static ModItem staffLegendary = new ItemStaff("staff_legendary", 6, false);

    public static ModItem arcaneSoul = new ItemArcaneSoul("arcane_soul");

    public static ItemArcaneTome arcaneTome = new ItemArcaneTome();

    public static ItemModRecord recordRed = new ItemModRecord("record_red", Sounds.MUSIC_RECORD_RED);
    public static ItemModRecord recordYellow = new ItemModRecord("record_yellow", Sounds.MUSIC_RECORD_YELLOW);
    public static ItemModRecord recordGreen = new ItemModRecord("record_green", Sounds.MUSIC_RECORD_GREEN);
    public static ItemModRecord recordBlue = new ItemModRecord("record_blue", Sounds.MUSIC_RECORD_BLUE);
    public static ItemModRecord recordBlack = new ItemModRecord("record_black", Sounds.MUSIC_RECORD_BLACK);
    public static ItemModRecord recordWhite = new ItemModRecord("record_white", Sounds.MUSIC_RECORD_WHITE);

    public static ModItem keystone = new ItemKeystone("keystone");

    public static List<ItemModArmor> modArmors = initModArmors();

    private static List<ItemModArmor> initModArmors() {
        List<ItemModArmor> armors = new ArrayList<>();

        int[] reductionAmounts = new int[]{1, 2, 3, 1};

        for (Element e : Element.values()) {
            ItemArmor.ArmorMaterial material = EnumHelper.addArmorMaterial(
                    "robes_" + e.getName(),
                    "tfmagic2:robes_" + e.getName(),
                    10, reductionAmounts, 20,
                    SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F).setRepairItem(new ItemStack(magicSilk));
            armors.add(new ItemModArmor(material, e, 0, EntityEquipmentSlot.HEAD));
            armors.add(new ItemModArmor(material, e, 0, EntityEquipmentSlot.CHEST));
            armors.add(new ItemModArmor(material, e, 0, EntityEquipmentSlot.LEGS));
            armors.add(new ItemModArmor(material, e, 0, EntityEquipmentSlot.FEET));
        }

        return armors;
    }

    /*============================================*/

    public static void register(IForgeRegistry<Item> registry) {
        for (IRegistrableItem i : itemList) {
            registry.register((Item) i);
        }
    }

    public static void registerModels() {
        for (IRegistrableItem i : itemList) {
            i.registerItemModel();
        }
    }

    public static void addItemToList(IRegistrableItem item) {
        itemList.add(item);
    }
}
