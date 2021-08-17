package com.cactuscoffee.magic.data;

import com.cactuscoffee.magic.entity.EntityMagicOrb;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpellProjectile extends Spell {
    public SpellProjectile(int index, String name, Element element, int level,
                           int cost, SoundEvent castSound, Item ingredient) {
        super(index, name, element, level, cost, castSound,
                null, ingredient, false, 0, false);
    }

    public SpellProjectile(int index, String name, Element element, int level,
                           int cost, SoundEvent castSound, Item ingredient, int meta) {
        super(index, name, element, level, cost, castSound,
                null, ingredient, false, meta, true);
    }

    public SpellProjectile(int index, String name, Element element, int level,
                           int cost, SoundEvent castSound, Block ingredient) {
        super(index, name, element, level, cost, castSound,
                ingredient, null, true, 0, false);
    }

    public SpellProjectile(int index, String name, Element element, int level,
                           int cost, SoundEvent castSound, Block ingredient, int meta) {
        super(index, name, element, level, cost, castSound, ingredient,
                null, true, meta, true);
    }

    public SpellProjectile(int index, String name, Element element, int level,
                           int cost, SoundEvent castSound) {
        super(index, name, element, level, cost, castSound,
                null, null, true, 0, false);
    }

    @Override
    public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
        return spawnProjectile(player, world);
    }

    public boolean spawnProjectile(EntityPlayer player, World world) {
        if (!world.isRemote) {
            EntityMagicOrb entity = new EntityMagicOrb(world, player, this);
            entity.setNoGravity(true);
            onProjectileShoot(entity, player);
            world.spawnEntity(entity);
            return true;
        }
        return false;
    }

    public void onProjectileShoot(EntityMagicOrb orb, EntityPlayer player) {
        orb.shoot(player, player.rotationPitch, player.rotationYaw,
                0.0F, 1F, 0.5F);
    }

    public void projectileEffect(EntityMagicOrb orb, EntityPlayer player, World world) {}

    public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player,
                                            Entity target, World world, MultiPartEntityPart part) {
        projectileEffect(orb, player, world);
        return true;
    }

    public boolean projectileEffectOnBlock(EntityMagicOrb orb, EntityPlayer player, World world, BlockPos blockPos, EnumFacing sideHit) {
        projectileEffect(orb, player, world);
        return true;
    }

    public void projectileTick(EntityMagicOrb orb, World world) {
        orb.processProjectile();
    }
}
