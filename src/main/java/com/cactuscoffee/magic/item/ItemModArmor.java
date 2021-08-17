package com.cactuscoffee.magic.item;

import com.cactuscoffee.magic.ItemRegister;
import com.cactuscoffee.magic.MagicMod;
import com.cactuscoffee.magic.data.Element;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ItemModArmor extends ItemArmor implements IRegistrableItem {
    private static final float BONUS_AMOUNT = 0.15F;
    private final String name;
    private final Element element;

    public ItemModArmor(ArmorMaterial materialIn, Element element, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);

        this.name = "magic_robes_" + equipmentSlotIn.getName() + "_" + element.getName();
        setUnlocalizedName(name);
        setRegistryName(name);

        this.element = element;

        this.addPropertyOverride(new ResourceLocation("color"), new IItemPropertyGetter() {
            @SideOnly(Side.CLIENT)
            public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
                if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Element")) {
                    return stack.getTagCompound().getInteger("Element");
                }
                else {
                    return 0;
                }
            }
        });

        setCreativeTab(MagicMod.creativeTab);

        ItemRegister.addItemToList(this);
    }

    public static float[] getElementalBonus(EntityPlayer player) {
        float[] bonuses = new float[Element.values().length];
        for (int i = 0; i < bonuses.length; ++i) {
            bonuses[i] = 1F;
        }
        if (player == null) {
            return bonuses;
        }
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack != null && stack.getItem() instanceof ItemModArmor) {
                Element e = ((ItemModArmor) stack.getItem()).element;
                bonuses[e.getMeta()] += BONUS_AMOUNT;
            }
        }
        return bonuses;
    }

    @Override
    public void registerItemModel() {
        MagicMod.proxy.registerItemRenderer(this, 0, name);
    }
}
