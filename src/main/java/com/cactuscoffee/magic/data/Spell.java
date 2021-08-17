package com.cactuscoffee.magic.data;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class Spell {
    private final int index;
    private final String unlocalizedName;
    private final Element element;

    private final Item ingredientItem;
    private final Block ingredientBlock;
    private final boolean ingredientIsBlock;
    private final int meta;
    private final boolean hasMeta;
    private final int level;
    public final SoundEvent castSound;

    private final int cost;
    private int spellUseDuration = 0;
    private int cooldown = 0;

    Spell(int index, String name, Element element, int level,
          int cost, SoundEvent castSound, Item ingredient) {
        this(index, name, element, level, cost, castSound, null, ingredient, false, 0, false);
    }

    Spell(int index, String name, Element element, int level,
          int cost, SoundEvent castSound, Item ingredient, int meta) {
        this(index, name, element, level, cost, castSound, null, ingredient, false, meta, true);
    }

    Spell(int index, String name, Element element, int level,
          int cost, SoundEvent castSound, Block ingredient) {
        this(index, name, element, level, cost, castSound, ingredient, null, true, 0, false);
    }

    Spell(int index, String name, Element element, int level,
          int cost, SoundEvent castSound, Block ingredient, int meta) {
        this(index, name, element, level, cost, castSound, ingredient, null, true, meta, true);
    }

    Spell(int index, String name, Element element, int level,
          int cost, SoundEvent castSound) {
        this(index, name, element, level, cost, castSound, null, null, true, 0, false);
    }

    Spell(int index, String name, Element element, int level, int cost, SoundEvent castSound,
          Block ingredientB, Item ingredientI, boolean ingredientIsBlock,
          int meta, boolean hasMeta) {
        this.unlocalizedName = "spell." + name + ".name";
        this.element = element;
        this.level = level;
        this.cost = cost;

        this.index = index;

        this.ingredientBlock = ingredientB;
        ingredientItem = ingredientI;
        this.ingredientIsBlock = ingredientIsBlock;

        this.meta = meta;
        this.hasMeta = hasMeta;

        this.castSound = castSound;

        Spells.addToList(this);
    }

    public int getIndex() {
        return index;
    }

    private String getUnlocalizedName() {
        return this.unlocalizedName;
    }

    public String getLocalizedName() {
        return I18n.translateToLocal(getUnlocalizedName());
    }

    public Element getElement() {
        return element;
    }

    public Item getIngredientItem() {
        return ingredientItem;
    }

    public Block getIngredientBlock() {
        return ingredientBlock;
    }

    public int getMeta() {
        return meta;
    }

    public boolean isIngredientBlock() {
        return ingredientIsBlock;
    }

    public boolean hasNoMeta() {
        return !hasMeta;
    }

    public int getLevel() {
        return level;
    }

    public boolean isLegendary() {
        return level == 6;
    }

    public int getCost() {
        return cost;
    }

    public Spell setSpellUseDuration(int spellUseDuration) {
        this.spellUseDuration = spellUseDuration;
        return this;
    }

    public int getSpellUseDuration() {
        return spellUseDuration;
    }

    public Spell setSpellCooldown(int cool) {
        this.cooldown = cool;
        return this;
    }


    public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
        return false;
    }

    public boolean spellEffectOnWorld(ItemStack stack, EntityPlayer player, World world,
                                      BlockPos pos, EnumFacing facing, EnumHand hand,
                                      float hitX, float hitY, float hitZ) {
        return spellEffect(stack, player, world);
    }

    public boolean spellEffectOnEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, World world) {
        return spellEffect(stack, player, world);
    }

    public void coolDown(Item item, EntityPlayer player) {
        if (cooldown > 0) {
            player.getCooldownTracker().setCooldown(item, cooldown);
        }
    }

    public void playCastSound(World world, EntityPlayer player) {
        if (castSound != null) {
            world.playSound(null, player.posX, player.posY, player.posZ,
                    castSound, SoundCategory.PLAYERS,
                    0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        }
    }
}
