package com.cactuscoffee.magic.item;

import com.cactuscoffee.magic.data.Sounds;
import com.cactuscoffee.magic.data.Spell;
import com.cactuscoffee.magic.data.Spells;
import com.cactuscoffee.magic.data.StringConstants;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemStaff extends ModItem implements IChargeableItem {
    private final int level;

    public ItemStaff(String name, int level, boolean addToCreativeTab) {
        super(name, addToCreativeTab);
        this.level = level;
        setMaxDamage((level * 50) + 51);
        setNoRepair();
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (getDamage(stack) == 0 &&
                stack.hasTagCompound() && stack.getTagCompound().hasKey("Spell")) {
            setDamage(stack, 1);
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        Spell spell = Spells.getFromNBT(stack);
        if (spell != null) {
            Spells.getSpellInfo(tooltip, spell);

            IChargeableItem.addChargeTooltip(stack, tooltip);
        }
        else {
            tooltip.add(StringConstants.LEVEL + " " + level + " " + StringConstants.STAFF);
        }
    }

    @Nonnull
    @Override
    public String getItemStackDisplayName(@Nonnull ItemStack stack) {
        Spell spell = Spells.getFromNBT(stack);
        if (spell != null) {
            return spell.getLocalizedName();
        }
        return super.getItemStackDisplayName(stack);
    }

    private static boolean canUseSpell(@Nonnull ItemStack stack, int cost) {
        return IChargeableItem.getCharge(stack) >= cost;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return Spells.getFromNBT(stack) != null;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        Spell spell = Spells.getFromNBT(stack);
        if (spell != null) {
            return spell.getSpellUseDuration();
        }
        return super.getMaxItemUseDuration(stack);
    }

    @Nonnull
    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    /*-------------------------------Spell Activation Events-------------------------------*/

    private void cast(World world, EntityPlayer player, ItemStack stack, Spell spell, int cost) {
        if (!world.isRemote) {
            stack.damageItem(cost, player);
            spell.coolDown(this, player);
        }
        spell.playCastSound(world, player);
    }

    //General
    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        Spell spell = Spells.getFromNBT(stack);
        if (spell != null) {
            int cost = spell.getCost();

            if (canUseSpell(stack, cost)) {
                if (spell.getSpellUseDuration() > 1) {
                    player.setActiveHand(hand);
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }

                if (spell.spellEffect(stack, player, world)) {
                    cast(world, player, stack, spell, cost);
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                }
            }
        }
        return new ActionResult<>(EnumActionResult.FAIL, stack);
    }

    //Point (At Block)
    @Nonnull
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        Spell spell = Spells.getFromNBT(stack);
        if (spell != null) {
            int cost = spell.getCost();

            if (canUseSpell(stack, cost)) {

                if (spell.getSpellUseDuration() > 1) {
                    player.setActiveHand(hand);
                    return EnumActionResult.SUCCESS;
                }

                if (spell.spellEffectOnWorld(stack, player, world, pos, facing, hand, hitX, hitY, hitZ)) {
                    cast(world, player, stack, spell, cost);
                    return EnumActionResult.SUCCESS;
                }
            }
        }
        return EnumActionResult.FAIL;
    }

    //Point (At Entity)
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        if (!player.getCooldownTracker().hasCooldown(stack.getItem())) {
            Spell spell = Spells.getFromNBT(stack);
            if (spell != null) {
                int cost = spell.getCost();

                if (canUseSpell(stack, cost)) {

                    if (spell.getSpellUseDuration() > 1) {
                        player.setActiveHand(hand);
                        return true;
                    }

                    if (spell.spellEffectOnEntity(stack, player, target, player.getEntityWorld())) {
                        cast(player.world, player, stack, spell, cost);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //Hold
    @Nonnull
    @Override
    public ItemStack onItemUseFinish(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLiving;
            Spell spell = Spells.getFromNBT(stack);

            if (spell != null) {
                int cost = spell.getCost();

                if (canUseSpell(stack, cost) ) {
                    if (spell.spellEffect(stack, player, worldIn)) {
                        cast(player.world, player, stack, spell, cost);
                    }
                }
            }
        }
        return stack;
    }
}
