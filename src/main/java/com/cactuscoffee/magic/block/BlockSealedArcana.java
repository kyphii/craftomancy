package com.cactuscoffee.magic.block;

import com.cactuscoffee.magic.BlockRegister;
import com.cactuscoffee.magic.ItemRegister;
import com.cactuscoffee.magic.MagicMod;
import com.cactuscoffee.magic.data.Sounds;
import com.cactuscoffee.magic.data.Spells;
import com.cactuscoffee.magic.item.ItemArcaneTome;
import com.cactuscoffee.magic.utils.SpellUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockSealedArcana extends Block implements IRegistrableBlock {
    private final String name;

    public BlockSealedArcana() {
        super(Material.ROCK);

        this.setHardness(20F);
        this.setHarvestLevel("pickaxe", 2);
        this.setSoundType(SoundType.METAL);

        this.name = "sealed_arcana";

        setUnlocalizedName(name);
        setRegistryName(name);

        BlockRegister.addBlockToList(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World world, BlockPos pos, Random random) {
        if (random.nextInt(16) == 0) {
            world.playSound(
                    pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F,
                    Sounds.SEALED_ARCANA_AMBIENT, SoundCategory.AMBIENT,
                    random.nextFloat() * 0.25F + 0.75F, random.nextFloat() * 1.0F + 0.5F, false);
        }
        for (int i = 0; i < 3; i++) {
            world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE,
                    pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D,
                    (random.nextDouble() - 0.5) * 4,
                    random.nextDouble() - 0.5,
                    (random.nextDouble() - 0.5) * 4);
        }
    }

    @Override
    public void registerItemModel(Item itemBlock) {
        MagicMod.proxy.registerItemRenderer(itemBlock, 0, name);
    }

    @Override
    public Item getItemBlock() {
        return new ItemBlock(this).setRegistryName(getRegistryName());
    }

    public ItemStack getLoot() {
        Random random = new Random();
        float randomRes = random.nextFloat();

        // 20% chance for music disc
        if (randomRes < 0.2) {
            switch (random.nextInt(6)) {
                case 0:
                    return new ItemStack(ItemRegister.recordRed);
                case 1:
                    return new ItemStack(ItemRegister.recordYellow);
                case 2:
                    return new ItemStack(ItemRegister.recordGreen);
                case 3:
                    return new ItemStack(ItemRegister.recordBlue);
                case 4:
                    return new ItemStack(ItemRegister.recordBlack);
                case 5:
                    return new ItemStack(ItemRegister.recordWhite);
            }
        }
        // 50% chance for spell book
        else if (randomRes < 0.7) {
            switch (random.nextInt(6)) {
                case 0:
                    return ItemArcaneTome.getFromSpell(Spells.spellSolarFlare);
                case 1:
                    return ItemArcaneTome.getFromSpell(Spells.spellStormCall);
                case 2:
                    return ItemArcaneTome.getFromSpell(Spells.spellOvergrow);
                case 3:
                    return ItemArcaneTome.getFromSpell(Spells.spellAbsoluteZero);
                case 4:
                    return ItemArcaneTome.getFromSpell(Spells.spellShadowBomb);
                case 5:
                    return ItemArcaneTome.getFromSpell(Spells.spellGuardianStar);
            }
        }
        // 30% chance for legendary staff
        return new ItemStack(ItemRegister.staffLegendary);
    }
}
