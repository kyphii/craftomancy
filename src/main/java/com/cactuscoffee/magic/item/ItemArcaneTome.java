package com.cactuscoffee.magic.item;

import com.cactuscoffee.magic.ItemRegister;
import com.cactuscoffee.magic.MagicMod;
import com.cactuscoffee.magic.data.Element;
import com.cactuscoffee.magic.data.Spell;
import com.cactuscoffee.magic.data.Spells;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemArcaneTome extends Item implements IRegistrableItem {
    private final String name;

    public ItemArcaneTome() {
        this.name = "arcane_tome";
        setUnlocalizedName(name);
        setRegistryName(name);

        this.setHasSubtypes(true);
        this.setMaxDamage(0);

        this.addPropertyOverride(new ResourceLocation("color"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                Spell spell = Spells.getFromNBT(stack);
                if (spell != null) {
                    return spell.getElement().getMeta();
                }
                else {
                    return 0;
                }
            }
        });

        ItemRegister.addItemToList(this);
    }

    @Nonnull
    public String getUnlocalizedName() {
        return name;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        Spell spell = Spells.getFromNBT(stack);
        if (spell != null) {
            tooltip.add(Element.getTextColor(spell.getElement()) + spell.getLocalizedName());
            Spells.getSpellInfo(tooltip, spell);
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        Spell spell = Spells.getFromNBT(stack);
        return spell != null && spell.isLegendary();
    }

    @Override
    public boolean getHasSubtypes() {
        return true;
    }

    @Override
    public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
        if (tab == MagicMod.creativeTab || tab == CreativeTabs.SEARCH) {
            for (int i = 0; i < Spells.getListSize(); ++i) {
                Spell spell = Spells.getFromListCreative(i);
                if (!spell.isLegendary()) {
                    items.add(getFromIndex(spell.getIndex()));
                }
            }
        }
    }

    public static ItemStack getFromIndex(int index) {
        ItemStack stack = new ItemStack(ItemRegister.arcaneTome);

        NBTTagCompound nbt;

        if (stack.hasTagCompound()) {
            nbt = stack.getTagCompound();
        } else {
            nbt = new NBTTagCompound();
        }

        nbt.setInteger("Spell", index);
        stack.setTagCompound(nbt);

        return stack;
    }

    public static ItemStack getFromSpell(Spell spell) {
        return getFromIndex(spell.getIndex());
    }

    @Override
    public void registerItemModel() {
        MagicMod.proxy.registerItemRenderer(this, 0, name);
    }
}