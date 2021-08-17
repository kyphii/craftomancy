package com.cactuscoffee.magic.utils;

import com.cactuscoffee.magic.data.Element;
import com.cactuscoffee.magic.entity.EntityMagicOrb;
import com.cactuscoffee.magic.item.ItemModArmor;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import java.util.List;

public class SpellUtils {
    public static boolean attackEntity(float damage, Entity target, EntityPlayer player, Element element, World world, MultiPartEntityPart part) {
        if (!world.isRemote) {
            if (target instanceof EntityLivingBase) {
                ((EntityLivingBase) target).setLastAttackedEntity(player);
            }
            if (part != null && target instanceof IEntityMultiPart) {
                return ((IEntityMultiPart) target).attackEntityFromPart(part, magicDamageSource(target, player, true),
                        getDamageWithBonus(damage, player, element));
            }
            return target.attackEntityFrom(magicDamageSource(target, player, true),
                    getDamageWithBonus(damage, player, element));
        }
        return false;
    }

    public static DamageSource magicDamageSource(Entity target, EntityPlayer player, boolean knockback) {
        if (knockback) {
            return new EntityDamageSourceIndirect("tfmagic2withcaster", target, player).setProjectile().setMagicDamage();
        }
        else {
            return new DamageSource("tfmagic2").setMagicDamage();
        }
    }

    public static float getDamageWithBonus(float damage, EntityPlayer player, Element element) {
        return damage * ItemModArmor.getElementalBonus(player)[element.getMeta()];
    }

    public static List<EntityLivingBase> getEntitiesInAABB(World world, double originX, double originY, double originZ,
                                                           double aabbRadius) {
        AxisAlignedBB aabb = new AxisAlignedBB(
                originX - aabbRadius, originY - aabbRadius, originZ - aabbRadius,
                originX + aabbRadius, originY + aabbRadius, originZ + aabbRadius);
        return world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
    }

    public static boolean isEnemy(Entity target, EntityLivingBase player, Class excludeClass) {
        if (target != null && target != player) {
            if (target.isCreatureType(EnumCreatureType.MONSTER, false) && target.getClass() != excludeClass) {
                return true;
            }
            if (player != null) {
                if (player.getAttackingEntity() == target){
                    return true;
                }
                if (player.getLastAttackedEntity() == target){
                    return true;
                }
            }
        }
        return false;
    }

    public static void addPotionEffect(Entity target, Potion potion, int duration, int power,
                                       World world, EntityPlayer player, Element element) {
        float bonus = ItemModArmor.getElementalBonus(player)[element.getMeta()];
        addPotionEffect(target, potion, duration, power, world, bonus);
    }

    public static void addPotionEffect(Entity target, Potion potion, int duration, int power,
                                       World world, float bonus) {
        if (!world.isRemote && target instanceof EntityLivingBase) {
            ((EntityLivingBase) target).addPotionEffect(new PotionEffect(potion, (int) (duration * bonus), power));
        }
    }

    public static void knockback(EntityMagicOrb orb, Entity target, float strength, World world) {
        if (!world.isRemote) {
            float f4 = MathHelper.sqrt(orb.motionX * orb.motionX + orb.motionZ * orb.motionZ);
            if (f4 > 0.0F) {
                float mult = strength * 0.6000000238418579F / f4;

                double a1 = orb.motionX * mult;
                double a3 = orb.motionZ * mult;
                target.addVelocity(a1, 0.1F, a3);
            }
        }
    }

    public static void makeParticles(Entity entity, World world, EnumParticleTypes particle, int num, boolean xOffset, boolean yOffset) {
        if (world.isRemote) {
            for (int i = 0; i < num; i++) {
                double y = entity.posY;
                if (yOffset) {
                    y += (2 * world.rand.nextDouble()) - entity.getYOffset();
                }
                world.spawnParticle(particle,
                        entity.posX + (xOffset ? world.rand.nextDouble() - 0.5 : 0),
                        y,
                        entity.posZ + (xOffset ? world.rand.nextDouble() - 0.5 : 0), 0, 0, 0);
            }
        }
    }

    public static void customExplosion(World world, EntityPlayer player, Element element, double x, double y, double z,
                                       float expSize, float atkPower, boolean isFirey, boolean isDestructive) {
        int size = (int) Math.ceil(expSize);

        if (!world.isRemote) {
            if (world.getDifficulty() == EnumDifficulty.PEACEFUL) {
                atkPower += 4F;
            }

            world.newExplosion(null, x, y, z, expSize, isFirey, isDestructive);

            if (atkPower > 0) {
                BlockPos pos1 = new BlockPos(x - size, y - size, z - size);
                BlockPos pos2 = new BlockPos(x + size, y + size, z + size);

                List<Entity> list =
                        world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos1, pos2));
                for (Entity entity : list) {
                    if (entity.getDistanceSq(x, y, z) < size * size) {
                        entity.attackEntityFrom(DamageSource.causeExplosionDamage(player),
                                getDamageWithBonus(atkPower, player, element));
                    }
                }
            }
        }
        else if (!isDestructive) {
            if (expSize >= 2.0F) {
                world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, x, y, z, 1.0D, 0.0D, 0.0D);
            }

            float sizeSq = expSize * expSize;

            for (int i = -size; i <= size; ++i) {
                for (int j = -size; j <= size; ++j) {
                    for (int k = -size; k <= size; ++k) {
                        double d0 = (x + i + world.rand.nextFloat());
                        double d1 = (y + j + world.rand.nextFloat());
                        double d2 = (z + k + world.rand.nextFloat());
                        float vx = i / 4F;
                        float vy = j / 4F;
                        float vz = k / 4F;
                        if (Math.pow(x - d0, 2) + Math.pow(y - d1, 2) + Math.pow(z - d2, 2) < sizeSq)
                            world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + x) / 2.0D, (d1 + y) / 2.0D, (d2 + z) / 2.0D, vx, vy, vz);
                        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, vx / 2, vy / 2, vz / 2);
                    }
                }
            }
        }
    }
}
