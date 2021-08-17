package com.cactuscoffee.magic.data;

import com.cactuscoffee.magic.ItemRegister;
import com.cactuscoffee.magic.entity.EntityMagicOrb;
import com.cactuscoffee.magic.entity.EntityVexFriendly;
import com.cactuscoffee.magic.item.ItemModArmor;
import com.cactuscoffee.magic.utils.SpellUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public abstract class Spells {
    private static final int HOLD_LENGTH_SHORT = 12;
    private static final int HOLD_LENGTH_LONG = 24;
    private static final int DEFAULT_COST = 5;

    private static List<Spell> list = new ArrayList<>();

    /* ---------------------------------------- IGNITE ---------------------------------------- */
    public static SpellProjectile spellIgnite = new SpellProjectile(0, "ignite", Element.RED, 1,
            4, Sounds.CAST_PROJECTILE, Items.FLINT) {
        @Override
        public boolean projectileEffectOnBlock(EntityMagicOrb orb, EntityPlayer player, World world, BlockPos blockPos, EnumFacing sideHit) {
            SpellEffects.flintAndSteel(world, blockPos, sideHit);
            return true;
        }

        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            target.setFire(10);
            world.playSound(null, target.posX, target.posY, target.posZ,
                    SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS,
                    1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
            return true;
        }
    };
    /* ---------------------------------------- GRENADE ---------------------------------------- */
    public static Spell spellGrenade = new SpellProjectile(1, "grenade", Element.RED, 2,
            10, Sounds.CAST_PROJECTILE, Items.GUNPOWDER) {
        @Override
        public void onProjectileShoot(EntityMagicOrb orb, EntityPlayer player) {
            super.onProjectileShoot(orb, player);
            orb.setNoGravity(false);
        }

        @Override
        public void projectileEffect(EntityMagicOrb orb, EntityPlayer player, World world) {
            SpellUtils.customExplosion(world, player, getElement(),
                    orb.posX, orb.posY, orb.posZ, 2F, 6F, false, false);
        }

        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            if (target.getDistance(orb) > 1.2F) {
                orb.setPosition(orb.posX + orb.motionX, orb.posY + orb.motionY, orb.posZ + orb.motionZ);
            }
            this.projectileEffect(orb, player, world);
            return true;
        }
    }.setSpellCooldown(15);
    /* ---------------------------------------- VULCANIZE ---------------------------------------- */
    public static Spell spellVulcanize = new Spell(2, "vulcanize", Element.RED, 2,
            20, SoundEvents.ITEM_FIRECHARGE_USE, Items.REDSTONE) {
        @Override
        public boolean spellEffectOnEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, World world) {
            float bonus = ItemModArmor.getElementalBonus(player)[getElement().getMeta()];
            SpellUtils.addPotionEffect(target, PotionRef.STRONG_ATTACK, 300, 1,
                    world, bonus);
            SpellUtils.addPotionEffect(target, PotionRef.SPEED, 300, 0,
                    world, bonus);
            SpellUtils.addPotionEffect(target, PotionRef.DEFENSE, 300, 0,
                    world, bonus);
            return true;
        }
    }.setSpellCooldown(200);
    /* ---------------------------------------- PRESSURE COOK ---------------------------------------- */
    public static Spell spellPressureCook = new Spell(3, "pressure_cook", Element.RED, 3,
            2, SoundEvents.ITEM_FIRECHARGE_USE, Items.COAL, 1) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            return SpellEffects.smelt(player, world);
        }
    }.setSpellCooldown(40);
    /* ---------------------------------------- LAVA BOMB ---------------------------------------- */
    public static Spell spellLavaBomb = new Spell(4, "lava_bomb", Element.RED, 4,
            16, Sounds.CAST_PROJECTILE, Items.BLAZE_POWDER) {
        @Override
        public boolean spellEffectOnWorld(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, EnumHand hand, float hitX, float hitY, float hitZ) {
            return SpellEffects.makeLiquid(world, pos.offset(facing), Blocks.FLOWING_LAVA);
        }
    }.setSpellCooldown(80);
    /* ---------------------------------------- MAGMA SHIELD ---------------------------------------- */
    public static Spell spellMagmaShield = new Spell(5, "magma_shield", Element.RED, 4,
            25, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, Blocks.MAGMA) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            SpellUtils.addPotionEffect(player, PotionRef.FIRE_DEFENSE, 300, 0,
                    world, player, getElement());
            SpellUtils.addPotionEffect(player, PotionRef.ABSORPTION, 300, 0,
                    world, player, getElement());
            SpellUtils.makeParticles(player, world, EnumParticleTypes.LAVA, 4, true, true);
            SpellUtils.makeParticles(player, world, EnumParticleTypes.FLAME, 6, true, true);
            SpellUtils.makeParticles(player, world, EnumParticleTypes.SMOKE_NORMAL, 8, true, true);
            return true;
        }
    }.setSpellUseDuration(HOLD_LENGTH_LONG).setSpellCooldown(80);
    /* ---------------------------------------- ERUPTION ---------------------------------------- */
    public static Spell spellEruption = new Spell(6, "eruption", Element.RED, 5,
            32, null, Items.FIRE_CHARGE) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            SpellEffects.erupt(player, world);
            return true;
        }
    }.setSpellUseDuration(HOLD_LENGTH_SHORT).setSpellCooldown(80);
    /* ---------------------------------------- FLAME TRAIL ---------------------------------------- */
    public static Spell spellFlameTrail = new SpellProjectile(48, "flame_trail", Element.RED, 5,
            4, SoundEvents.ITEM_FIRECHARGE_USE, Items.MAGMA_CREAM) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            SpellUtils.makeParticles(player, world, EnumParticleTypes.FLAME, 8, true, false);
            return super.spellEffect(stack, player, world);
        }

        @Override
        public void onProjectileShoot(EntityMagicOrb orb, EntityPlayer player) {
            orb.setPosition(player.posX, player.posY + 0.1, player.posZ);
            orb.setLife(300);
            orb.setInvisible(true);
        }

        @Override
        public void projectileTick(EntityMagicOrb orb, World world) {
            super.projectileTick(orb, world);
            if (world.rand.nextInt(6) == 0) {
                SpellUtils.makeParticles(orb, world, EnumParticleTypes.FLAME, 1, true, false);
            }
        }

        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            target.setFire(5);
            SpellUtils.attackEntity(6F, target, player, getElement(), world, part);
            return false;
        }
    };
    /* ---------------------------------------- SOLAR FLARE ---------------------------------------- */
    public static Spell spellSolarFlare = new SpellProjectile(7, "solar_flare", Element.RED, 6,
            25, Sounds.CAST_PROJECTILE) {
        @Override
        public void projectileEffect(EntityMagicOrb orb, EntityPlayer player, World world) {
            SpellUtils.customExplosion(world, player, getElement(),
                    orb.posX, orb.posY, orb.posZ, 3F, 8F, true, true);
        }

        @Override
        public void projectileTick(EntityMagicOrb orb, World world) {
            super.projectileTick(orb, world);
            SpellUtils.makeParticles(orb, world, EnumParticleTypes.FLAME, 3, true, false);
        }

        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            if (target.getDistance(orb) > 1.8F) {
                orb.setPosition(orb.posX + orb.motionX, orb.posY + orb.motionY, orb.posZ + orb.motionZ);
            }
            this.projectileEffect(orb, player, world);
            return true;
        }
    }.setSpellUseDuration(HOLD_LENGTH_SHORT).setSpellCooldown(40);

    /* ---------------------------------------- HASTE ---------------------------------------- */
    public static Spell spellHaste = new Spell(8, "haste", Element.YELLOW, 1,
            8, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, Items.SUGAR) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            SpellUtils.addPotionEffect(player, PotionRef.SPEED, 400, 1,
                    world, player, getElement());
            return true;
        }
    }.setSpellUseDuration(HOLD_LENGTH_SHORT).setSpellCooldown(200);
    /* ---------------------------------------- SPARK ---------------------------------------- */
    public static Spell spellSpark = new SpellProjectile(9, "spark", Element.YELLOW, 2,
            4, Sounds.CAST_PROJECTILE, Blocks.YELLOW_FLOWER, 0) {
        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            SpellUtils.attackEntity(5F, target, player, getElement(), world, part);
            return true;
        }

        @Override
        public void onProjectileShoot(EntityMagicOrb orb, EntityPlayer player) {
            orb.shoot(player, player.rotationPitch, player.rotationYaw,
                    0.0F, 1.2F, 2F);
        }
    }.setSpellCooldown(10);
    /* ---------------------------------------- ACID FOG ---------------------------------------- */
    public static Spell spellAcidFog = new Spell(10, "acid_fog", Element.YELLOW, 2,
            16, SoundEvents.BLOCK_FIRE_EXTINGUISH, Items.WHEAT) {
        @Override
        public boolean spellEffectOnWorld(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, EnumHand hand, float hitX, float hitY, float hitZ) {
            pos = pos.offset(facing);
            return SpellEffects.summonCloud(PotionTypes.HARMING, 0xE0C038, 300, 3F, world, player,
                    pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        }
    }.setSpellCooldown(40);
    /* ---------------------------------------- AIRBURST ---------------------------------------- */
    public static Spell spellAirburst = new Spell(11, "airburst", Element.YELLOW, 3,
            8, SoundEvents.BLOCK_SNOW_BREAK, Items.FEATHER) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            if (!player.isInWater()) {
                player.motionY = player.onGround ? 0.85 : 0.6;
                player.fallDistance = -2F;
                SpellUtils.makeParticles(player, world, EnumParticleTypes.CLOUD, 6, true, true);
                return true;
            }
            return false;
        }
    }.setSpellCooldown(40);
    /* ---------------------------------------- TAILWIND ---------------------------------------- */
    public static Spell spellTailwind = new Spell(12, "tailwind", Element.YELLOW, 4,
            12, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, Items.FIREWORKS) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            SpellEffects.tailwind(player, world);
            return true;
        }
    }.setSpellUseDuration(HOLD_LENGTH_LONG).setSpellCooldown(60);
    /* ---------------------------------------- SQUALL ---------------------------------------- */
    public static Spell spellSquall = new SpellProjectile(13, "squall", Element.YELLOW, 4,
            7, Sounds.CAST_PROJECTILE, Items.GOLD_INGOT) {
        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            SpellUtils.attackEntity(5F, target, player, getElement(), world, part);
            SpellUtils.knockback(orb, target, 2.5F, world);
            return true;
        }
    }.setSpellCooldown(15);
    /* ---------------------------------------- MEGAVOLT ---------------------------------------- */
    public static Spell spellMegavolt = new SpellProjectile(14, "megavolt", Element.YELLOW, 5,
            12, Sounds.CAST_MEGAVOLT, Blocks.SPONGE) {
        @Override
        public void projectileTick(EntityMagicOrb orb, World world) {
            int i = 0;
            while (i < 54 && !orb.isDead) {
                orb.processProjectile();
                if (world.isRemote) {
                    world.spawnParticle(EnumParticleTypes.CRIT,
                            orb.posX, orb.posY, orb.posZ,
                            0, 0, 0);
                }
                ++i;
            }
            orb.setDead();
        }

        @Override
        public void onProjectileShoot(EntityMagicOrb orb, EntityPlayer player) {
            orb.shoot(player, player.rotationPitch, player.rotationYaw,
                    0.0F, 1.2F, 0);
            orb.adjustStartPos();
            orb.setInvisible(true);
            orb.ticksExisted = 6;
        }

        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            SpellUtils.attackEntity(8F, target, player, getElement(), world, part);
            return false;
        }
    }.setSpellCooldown(20);
    /* ---------------------------------------- SPIRIT SENTRY ---------------------------------------- */
    public static Spell spellSpiritSentry = new Spell(49, "spirit_sentry", Element.YELLOW, 5,
            30, SoundEvents.EVOCATION_ILLAGER_PREPARE_SUMMON, Blocks.END_STONE) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            double x = player.posX - MathHelper.sin(player.rotationYaw * 0.017453292F);
            double y = player.posY + 0.5;
            double z = player.posZ + MathHelper.cos(player.rotationYaw * 0.017453292F);
            if (!world.isRemote) {
                EntityVexFriendly entity = new EntityVexFriendly(world, player);
                entity.setPosition(x, y, z);
                world.spawnEntity(entity);
                return true;
            }
            else {
                for (int i = 0; i < 8; ++i) {
                    world.spawnParticle(EnumParticleTypes.CLOUD,
                            x + world.rand.nextFloat() - 0.5,
                            y + world.rand.nextFloat() - 0.5,
                            z + world.rand.nextFloat() - 0.5,
                            0, 0, 0);
                }
                return false;
            }
        }
    }.setSpellUseDuration(HOLD_LENGTH_SHORT).setSpellCooldown(100);
    /* ---------------------------------------- STORM CALL ---------------------------------------- */
    public static Spell spellStormCall = new SpellProjectile(15, "storm_call", Element.YELLOW, 6,
            25, Sounds.CAST_PROJECTILE) {
        @Override
        public void projectileEffect(EntityMagicOrb orb, EntityPlayer player, World world) {
            if (!world.isRemote) {
                world.addWeatherEffect(new EntityLightningBolt(world, orb.posX, orb.posY, orb.posZ, false));
            }
        }
    }.setSpellUseDuration(HOLD_LENGTH_SHORT).setSpellCooldown(100);

    /* ---------------------------------------- SIPHON ---------------------------------------- */
    public static Spell spellSiphon = new Spell(16, "siphon", Element.GREEN, 1,
            3, Sounds.CAST_SIPHON, Blocks.SAPLING) {
        @Override
        public boolean spellEffectOnEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, World world) {
            SpellUtils.attackEntity(6F, target, player, getElement(), world, null);
            SpellUtils.makeParticles(target, world, EnumParticleTypes.SMOKE_NORMAL, 5, true, true);
            if (player.getHealth() < player.getMaxHealth()) {
                player.heal(3F);
                player.hurtResistantTime = 10;
                SpellUtils.makeParticles(player, world, EnumParticleTypes.VILLAGER_HAPPY, 2, true, true);
            }
            return true;
        }
    }.setSpellCooldown(5);
    /* ---------------------------------------- VENOM ---------------------------------------- */
    public static Spell spellVenom = new SpellProjectile(17, "venom", Element.GREEN, 2,
            8, Sounds.CAST_PROJECTILE, Blocks.BROWN_MUSHROOM) {
        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            SpellUtils.addPotionEffect(target, PotionRef.POISON, 160, 2,
                    world, player, getElement());
            return true;
        }
    }.setSpellCooldown(40);
    /* ---------------------------------------- BLOOM ---------------------------------------- */
    public static Spell spellBloom = new Spell(18, "bloom", Element.GREEN, 2,
            5, SoundEvents.BLOCK_GRASS_HIT, Items.BONE) {
        @Override
        public boolean spellEffectOnWorld(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos,
                                          EnumFacing facing, EnumHand hand, float hitX, float hitY, float hitZ) {
            return SpellEffects.boneMeal(stack, player, worldIn, pos, hand);
        }
    };
    /* ---------------------------------------- ROCKHIDE ---------------------------------------- */
    public static Spell spellRockhide = new Spell(19, "rockhide", Element.GREEN, 3,
            25, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, Blocks.MOSSY_COBBLESTONE, 0) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            float bonus = ItemModArmor.getElementalBonus(player)[getElement().getMeta()];
            SpellUtils.addPotionEffect(player, PotionRef.DEFENSE, 300, 2, world, bonus);
            SpellUtils.addPotionEffect(player, PotionRef.SLOWNESS, 300, 0, world, bonus);
            return true;
        }
    }.setSpellUseDuration(HOLD_LENGTH_LONG).setSpellCooldown(80);
    /* ---------------------------------------- EXCAVATE ---------------------------------------- */
    public static Spell spellExcavate = new Spell(20, "excavate", Element.GREEN, 4,
            8, Sounds.CAST_EXCAVATE, Blocks.OBSIDIAN) {
        @Override
        public boolean spellEffectOnWorld(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, EnumHand hand, float hitX, float hitY, float hitZ) {
            SpellEffects.excavate(world, pos);
            return true;
        }
    }.setSpellCooldown(40);
    /* ---------------------------------------- FISSURE ---------------------------------------- */
    public static Spell spellFissure = new Spell(21, "fissure", Element.GREEN, 4,
            7, Sounds.CAST_PROJECTILE, Items.QUARTZ) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            return SpellEffects.fissure(player, world);
        }
    }.setSpellUseDuration(HOLD_LENGTH_SHORT).setSpellCooldown(20);
    /* ---------------------------------------- PETRIFY ---------------------------------------- */
    public static Spell spellBlight = new SpellProjectile(50, "blight", Element.GREEN, 5,
            10, Sounds.CAST_PROJECTILE, Items.SLIME_BALL) {
        @Override
        public void projectileTick(EntityMagicOrb orb, World world) {
            super.projectileTick(orb, world);
            if (orb.ticksExisted > 0 && orb.ticksExisted % 2 == 0) {
                SpellEffects.summonCloud(PotionTypes.POISON, PotionRef.POISON.getLiquidColor(),
                        60, 0.8F, world, orb.getThrower(),
                        orb.posX, orb.posY, orb.posZ);
            }
        }

        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            return SpellUtils.attackEntity(8F, target, player, getElement(), world, part);
        }
    }.setSpellCooldown(60);
    /* ---------------------------------------- PETRIFY ---------------------------------------- */
    public static Spell spellPetrify = new SpellProjectile(22, "petrify", Element.GREEN, 5,
            20, Sounds.CAST_PROJECTILE, Items.SHULKER_SHELL) {
        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            float bonus = ItemModArmor.getElementalBonus(player)[getElement().getMeta()];
            SpellUtils.addPotionEffect(target, PotionRef.DEFENSE, 100, 10, world, bonus);
            SpellUtils.addPotionEffect(target, PotionRef.SLOWNESS, 100, 10, world, bonus);
            SpellUtils.addPotionEffect(target, PotionRef.DIG_SLOW, 100, 10, world, bonus);
            SpellUtils.addPotionEffect(target, PotionRef.JUMP_BOOST, 100, -10, world, bonus);
            SpellUtils.addPotionEffect(target, PotionRef.WEAK_ATTACK, 100, 10, world, bonus);
            return true;
        }
    }.setSpellCooldown(80);
    /* ---------------------------------------- OVERGROW ---------------------------------------- */
    public static Spell spellOvergrow = new SpellProjectile(23, "overgrow", Element.GREEN, 6,
            16, Sounds.CAST_PROJECTILE) {
        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            SpellUtils.attackEntity(6F, target, player, getElement(), world, part);
            SpellUtils.addPotionEffect(target, PotionRef.POISON, 160, 1,
                    world, player, getElement());
            SpellEffects.overgrow(target.getPosition(), world);
            return true;
        }

        @Override
        public boolean projectileEffectOnBlock(EntityMagicOrb orb, EntityPlayer player, World world, BlockPos blockPos, EnumFacing sideHit) {
            SpellEffects.overgrow(blockPos, world);
            return true;
        }
    }.setSpellUseDuration(HOLD_LENGTH_SHORT).setSpellCooldown(60);

    /* ---------------------------------------- ICE BULLETS ---------------------------------------- */
    public static Spell spellIceBullets = new SpellProjectile(24, "ice_bullets", Element.BLUE, 1,
            3, Sounds.CAST_PROJECTILE, Items.SNOWBALL) {
        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            SpellUtils.attackEntity(4F, target, player, getElement(), world, part);
            return true;
        }
    };
    /* ---------------------------------------- FROSTBITE ---------------------------------------- */
    public static Spell spellFrostbite = new SpellProjectile(25, "frostbite", Element.BLUE, 2,
            10, Sounds.CAST_PROJECTILE, Items.DYE, EnumDyeColor.BLUE.getDyeDamage()) {
        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            SpellUtils.addPotionEffect(target, PotionRef.SLOWNESS, 200, 1,
                    world, player, getElement());
            return true;
        }
    }.setSpellCooldown(20);
    /* ---------------------------------------- WELLSPRING ---------------------------------------- */
    public static Spell spellWellspring = new SpellProjectile(26, "wellspring", Element.BLUE, 2,
            15, Sounds.CAST_PROJECTILE, Blocks.WATERLILY) {
        @Override
        public boolean projectileEffectOnBlock(EntityMagicOrb orb, EntityPlayer player, World world, BlockPos blockPos, EnumFacing sideHit) {
            if (world.provider.doesWaterVaporize()) {
                BlockPos b = blockPos.offset(sideHit);
                world.playSound(null, b.getX() + 0.5F, b.getY() + 0.5F, b.getZ() + 0.5F,
                        SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS,
                        0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

                for (int i = 0; i < 8; ++i) {
                    world.spawnParticle(EnumParticleTypes.SMOKE_LARGE,
                            b.getX() + Math.random(),
                            b.getY() + Math.random(),
                            blockPos.getZ() + Math.random(),
                            0.0D, 0.0D, 0.0D);
                }
            }
            else {
                SpellEffects.makeLiquid(world, blockPos.offset(sideHit), Blocks.FLOWING_WATER);
            }
            return true;
        }
    }.setSpellCooldown(40);
    /* ---------------------------------------- TIDAL WAVE ---------------------------------------- */
    public static Spell spellTidalWave = new SpellProjectile(27, "tidal_wave", Element.BLUE, 3,
            7, SoundEvents.ENTITY_PLAYER_SWIM, Items.FISH, 0) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            return SpellEffects.tidalWave(player, world, this);
        }

        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            int damage = orb.data == 0 ? 4 : 8;
            SpellUtils.attackEntity(damage, target, player, getElement(), world, part);
            SpellUtils.makeParticles(orb, world, EnumParticleTypes.WATER_SPLASH, 4, true, false);
            return true;
        }

        @Override
        public boolean projectileEffectOnBlock(EntityMagicOrb orb, EntityPlayer player, World world, BlockPos blockPos, EnumFacing sideHit) {
            SpellUtils.makeParticles(orb, world, EnumParticleTypes.WATER_SPLASH, 4, true, false);
            return true;
        }
    }.setSpellCooldown(15);
    /* ---------------------------------------- COLD STEP ---------------------------------------- */
    public static Spell spellChillstride = new Spell(28, "chillstride", Element.BLUE, 4,
            4, Sounds.CAST_AVALANCHE, Blocks.ICE) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            SpellEffects.chillStride(player, world);
            return true;
        }
    };
    /* ---------------------------------------- GILLS ---------------------------------------- */
    public static Spell spellGills = new Spell(29, "gills", Element.BLUE, 4,
            32, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, Items.FISH, 3) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            SpellUtils.addPotionEffect(player, PotionRef.WATER_BREATHING, 600, 0,
                    world, player, getElement());
            return true;
        }
    }.setSpellUseDuration(HOLD_LENGTH_SHORT).setSpellCooldown(60);
    /* ---------------------------------------- CRYSTALLIZE ---------------------------------------- */
    public static Spell spellCrystallize = new Spell(30, "crystallize", Element.BLUE, 5,
            22, SoundEvents.BLOCK_GLASS_FALL, Items.PRISMARINE_CRYSTALS) {
        @Override
        public boolean spellEffectOnWorld(ItemStack stack, EntityPlayer player, World world, BlockPos pos,
                                          EnumFacing facing, EnumHand hand, float hitX, float hitY, float hitZ) {
            BlockPos pos1 = pos.offset(facing);
            if (!world.isRemote) {
                world.spawnEntity(new EntityItem(world, pos1.getX() + 0.5, pos1.getY(), pos1.getZ() + 0.5,
                        new ItemStack(ItemRegister.manaCrystal)));
            }
            world.playSound(null, pos1.getX() + 0.5F, pos1.getY() + 0.5F,pos1.getZ() + 0.5F,
                    SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS,
                    0.5F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            return true;
        }
    };
    /* ---------------------------------------- MAELSTROM ---------------------------------------- */
    public static Spell spellMaelstrom = new SpellProjectile(51, "maelstrom", Element.BLUE, 5,
            25, SoundEvents.ENTITY_GENERIC_SPLASH, Blocks.PACKED_ICE) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            if (!world.isRemote) {
                double y = player.posY + 0.5;
                for (int i = 0; i < 12; ++i) {
                    int angle = i * 30;
                    double x = player.posX - MathHelper.sin(angle * 0.017453292F);
                    double z = player.posZ + MathHelper.cos(angle * 0.017453292F);
                    EntityMagicOrb entity = new EntityMagicOrb(world, player, this);
                    entity.setPosition(x, y, z);
                    entity.setLife(400 + world.rand.nextInt(20));
                    entity.data = (byte) i;
                    entity.setNoGravity(true);
                    world.spawnEntity(entity);
                }
                return true;
            }
            return false;
        }

        @Override
        public void projectileTick(EntityMagicOrb orb, World world) {
            if (orb.getThrower() != null) {
                float angle = orb.data * 30 * 0.017453292F;
                double d = Math.min(3, 1 + orb.ticksExisted / 64F);
                double x = orb.getThrower().posX - (MathHelper.sin((orb.ticksExisted / 4.0F) + angle) * d);
                double y = orb.getThrower().posY + 0.5 + (MathHelper.sin((orb.ticksExisted + orb.data) / 2.0F) / 4);
                double z = orb.getThrower().posZ + (MathHelper.cos((orb.ticksExisted / 4.0F) + angle) * d);

                orb.motionX = x - orb.posX;
                orb.motionY = y - orb.posY;
                orb.motionZ = z - orb.posZ;
            }
            super.projectileTick(orb, world);
        }

        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            SpellUtils.attackEntity(8F, target, player, getElement(), world, part);
            SpellUtils.makeParticles(orb, world, EnumParticleTypes.WATER_SPLASH, 4, true, false);
            return false;
        }

        @Override
        public boolean projectileEffectOnBlock(EntityMagicOrb orb, EntityPlayer player, World world, BlockPos blockPos, EnumFacing sideHit) {
            return false;
        }
    }.setSpellUseDuration(HOLD_LENGTH_SHORT).setSpellCooldown(100);
    /* ---------------------------------------- ABSOLUTE ZERO ---------------------------------------- */
    public static Spell spellAbsoluteZero = new SpellProjectile(31, "absolute_zero", Element.BLUE, 6,
            18, Sounds.CAST_PROJECTILE) {
        @Override
        public void projectileTick(EntityMagicOrb orb, World world) {
            super.projectileTick(orb, world);
            if (orb.data > 0 && orb.ticksExisted > 10) {
                orb.setDead();
            }
        }

        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            if (SpellUtils.attackEntity(4F + (orb.data * 2), target, player, getElement(), world, part)
                    && orb.data < 6 && orb.ticksExisted > 0) {
                SpellUtils.addPotionEffect(target, PotionRef.SLOWNESS, 200, 0, world, player, getElement());
                SpellEffects.absoluteZero(target, player, world, this, orb.data + 1);
                return true;
            }
            return false;
        }
    }.setSpellUseDuration(HOLD_LENGTH_SHORT).setSpellCooldown(40);

    /* ---------------------------------------- CHAOS ORB ---------------------------------------- */
    public static Spell spellChaosOrb = new SpellProjectile(32, "chaos_orb", Element.BLACK, 1,
            6, Sounds.CAST_PROJECTILE, Items.DYE, EnumDyeColor.BLACK.getDyeDamage()) {

        @Override
        public void onProjectileShoot(EntityMagicOrb orb, EntityPlayer player) {
            orb.shoot(player, player.rotationPitch, player.rotationYaw,
                    0.0F, 0.8F, 0);
            orb.setNoGravity(false);
            orb.setLife(200);
        }

        @Override
        public boolean projectileEffectOnBlock(EntityMagicOrb orb, EntityPlayer player, World world, BlockPos blockPos, EnumFacing sideHit) {
            switch (sideHit) {
                case UP:
                case DOWN:
                    orb.motionY = -orb.motionY;
                    break;
                case NORTH:
                case SOUTH:
                    orb.motionZ = -orb.motionZ;
                    break;
                case EAST:
                case WEST:
                    orb.motionX = -orb.motionX;
                    break;
            }
            orb.motionX *= 0.90;
            orb.motionY *= 0.90;
            orb.motionZ *= 0.90;
            SpellUtils.makeParticles(orb, world, EnumParticleTypes.SMOKE_NORMAL, 3, true, false);
            return false;
        }

        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            SpellUtils.attackEntity(7F, target, player, getElement(), world, part);
            return false;
        }
    }.setSpellCooldown(10);
    /* ---------------------------------------- BLIND ---------------------------------------- */
    public static Spell spellBlind = new SpellProjectile(33, "blind", Element.BLACK, 2,
            12, Sounds.CAST_PROJECTILE, Blocks.SAND) {
        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            SpellUtils.attackEntity(0F, target, player, getElement(), world, part);
            SpellUtils.addPotionEffect(target, PotionRef.BLINDNESS, 200, 0,
                    world, player, getElement());
            return true;
        }
    }.setSpellCooldown(40);
    /* ---------------------------------------- CURSE ---------------------------------------- */
    public static Spell spellCurse = new Spell(34, "curse", Element.BLACK, 2,
            18, Sounds.CAST_CURSE, Items.SPIDER_EYE) {
        @Override
        public boolean spellEffectOnEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, World world) {
            SpellUtils.addPotionEffect(target, MobEffects.WITHER, 200, 0,
                    world, player, getElement());
            SpellUtils.makeParticles(target, world, EnumParticleTypes.SMOKE_LARGE, 10, true, true);
            return true;
        }
    };
    /* ---------------------------------------- NULLIFY ---------------------------------------- */
    public static Spell spellNullify = new Spell(35, "nullify", Element.BLACK, 3,
            16, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, Items.MILK_BUCKET) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            if (!world.isRemote) {
                if (player.getActivePotionEffects().size() > 0) {
                    player.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
                    return true;
                }
            }
            SpellUtils.makeParticles(player, world, EnumParticleTypes.CLOUD, 10, true, true);
            return false;
        }
    }.setSpellUseDuration(HOLD_LENGTH_LONG);
    /* ---------------------------------------- BLINK ---------------------------------------- */
    public static Spell spellBlink = new Spell(36, "blink", Element.BLACK, 4,
            8, null, Items.ENDER_PEARL) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            return SpellEffects.blink(player, world);
        }
        @Override
        public boolean spellEffectOnWorld(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, EnumHand hand, float hitX, float hitY, float hitZ) {
            return SpellEffects.blink(player, world);
        }
    }.setSpellCooldown(15);
    /* ---------------------------------------- SEEKER ---------------------------------------- */
    public static Spell spellSeeker = new SpellProjectile(37, "seeker", Element.BLACK, 4,
            10, Sounds.CAST_PROJECTILE, Items.ENDER_EYE) {
        @Override
        public void projectileTick(EntityMagicOrb orb, World world) {
            super.projectileTick(orb, world);
            if (orb.ticksExisted % 8 == 0) {
                SpellEffects.seeker(orb, world);
            }
        }

        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            SpellUtils.attackEntity(5F, target, player, getElement(), world, part);
            return true;
        }
    }.setSpellCooldown(10);
    /* ---------------------------------------- VANISH ---------------------------------------- */
    public static Spell spellVanish = new Spell(38, "vanish", Element.BLACK, 5,
            9, null, Blocks.PUMPKIN) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            SpellUtils.addPotionEffect(player, PotionRef.INVISIBILITY, 300, 0,
                    world, player, getElement());
            return true;
        }
    }.setSpellUseDuration(HOLD_LENGTH_SHORT).setSpellCooldown(160);
    /* ---------------------------------------- DELIRIUM ---------------------------------------- */
    public static Spell spellDelirium = new Spell(52, "delirium", Element.BLACK, 5,
            30, Sounds.CAST_DELIRIUM, Items.CHORUS_FRUIT) {
        @Override
        public boolean spellEffectOnEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, World world) {
            SpellUtils.addPotionEffect(target, PotionRef.NAUSEA, 400, 0, world, player, getElement());
            List<EntityLivingBase> entityList = SpellUtils.getEntitiesInAABB(world, target.posX, target.posY, target.posZ, 8);
            entityList.remove(player);
            entityList.remove(target);
            if (!entityList.isEmpty()) {
                target.setRevengeTarget(entityList.get(world.rand.nextInt(entityList.size())));
            }
            return true;
        }
    }.setSpellCooldown(15);
    /* ---------------------------------------- VOID BOMB ---------------------------------------- */
    public static Spell spellShadowBomb = new Spell(39, "shadow_bomb", Element.BLACK, 6,
            21, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC) {
        @Override
        public boolean spellEffectOnWorld(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing facing, EnumHand hand, float hitX, float hitY, float hitZ) {
            return SpellEffects.shadowBomb(player, world, pos.offset(facing));
        }
    }.setSpellCooldown(100);

    /* ---------------------------------------- RECOVER ---------------------------------------- */
    public static Spell spellRecover = new Spell(40, "recover", Element.WHITE, 1,
            4, SoundEvents.BLOCK_CLOTH_PLACE, Items.APPLE) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            if (player.getHealth() < player.getMaxHealth()) {
                player.heal(0.5F);
                SpellUtils.makeParticles(player, world, EnumParticleTypes.HEART, 3, true, true);
                return true;
            }
            return false;
        }
    };
    /* ---------------------------------------- RADIANCE ---------------------------------------- */
    public static Spell spellRadiance = new Spell(41, "radiance", Element.WHITE, 2,
            7, Sounds.CAST_RADIANCE, Blocks.DOUBLE_PLANT, 0) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            SpellEffects.radiance(player, world);
            return true;
        }
    }.setSpellCooldown(5);
    /* ---------------------------------------- CIRCLE OF LIFE ---------------------------------------- */
    public static Spell spellCircleOfLife = new Spell(42, "circle_of_life", Element.WHITE, 2,
            12, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, Items.EGG) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            return SpellEffects.summonCloud(PotionTypes.HEALING, 0xf8e0e8, 400, 4F, world, player,
                    player.posX, player.posY, player.posZ);
        }
    }.setSpellUseDuration(HOLD_LENGTH_LONG);
    /* ---------------------------------------- RECALL ---------------------------------------- */
    public static Spell spellRecall = new Spell(43, "recall", Element.WHITE, 3,
            25, null, Items.CAKE) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            return SpellEffects.homeTeleport(player);
        }
    }.setSpellUseDuration(HOLD_LENGTH_LONG);
    /* ---------------------------------------- DISARM---------------------------------------- */
    public static Spell spellDisarm = new SpellProjectile(44, "disarm", Element.WHITE, 4,
            12, Sounds.CAST_PROJECTILE, Blocks.WEB) {
        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            if (target instanceof EntityLivingBase) {
                SpellUtils.attackEntity(2f, target, player, getElement(), world, part);
                if (!world.isRemote) {
                    ItemStack stackMain = ((EntityLivingBase) target).getHeldItemMainhand();
                    if (!stackMain.isEmpty()) {
                        EntityItem item = target.entityDropItem(stackMain, 1);
                        if (item != null) {
                            item.setPickupDelay(30);
                        }
                        target.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    }
                    ItemStack stackOff = ((EntityLivingBase) target).getHeldItemOffhand();
                    if (!stackOff.isEmpty()) {
                        EntityItem item = target.entityDropItem(stackOff, 1);
                        if (item != null) {
                            item.setPickupDelay(30);
                        }
                    }
                }
                SpellUtils.makeParticles(target, world, EnumParticleTypes.FIREWORKS_SPARK, 10, true, true);
                target.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
                target.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
                return true;
            }
            return false;
        }
    }.setSpellCooldown(40);
    /* ---------------------------------------- CLARITY ---------------------------------------- */
    public static Spell spellClarity = new Spell(45, "clarity", Element.WHITE, 4,
            12, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, Items.GOLDEN_CARROT) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            SpellUtils.makeParticles(player, world, EnumParticleTypes.FIREWORKS_SPARK, 10, true, true);
            SpellUtils.addPotionEffect(player, PotionRef.NIGHT_VISION, 400, 0,
                    world, player, getElement());
            return true;
        }
    }.setSpellUseDuration(HOLD_LENGTH_LONG).setSpellCooldown(100);
    /* ---------------------------------------- TRUE SHOT ---------------------------------------- */
    public static Spell spellTrueShot = new SpellProjectile(46, "true_shot", Element.WHITE, 5,
            8, Sounds.CAST_PROJECTILE, Items.GHAST_TEAR) {
        @Override
        public void onProjectileShoot(EntityMagicOrb orb, EntityPlayer player) {
            orb.shoot(player, player.rotationPitch, player.rotationYaw,
                    0.0F, 1.5F, 0);
            orb.setNoGravity(true);
            orb.setLife(150);
        }

        @Override
        public void projectileTick(EntityMagicOrb orb, World world) {
            super.projectileTick(orb, world);
            SpellUtils.makeParticles(orb, world, EnumParticleTypes.FIREWORKS_SPARK, 1, false, false);
        }

        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            SpellUtils.attackEntity(orb.ticksExisted / 2, target, player, getElement(), world, part);
            return true;
        }
    }.setSpellCooldown(40);
    /* ---------------------------------------- CLARITY ---------------------------------------- */
    public static Spell spellAuraVision = new Spell(53, "aura_vision", Element.WHITE, 5,
            15, Sounds.CAST_CORONA, Items.RABBIT_FOOT) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            SpellUtils.makeParticles(player, world, EnumParticleTypes.CRIT_MAGIC, 10, true, true);
            for (EntityLivingBase entity : SpellUtils.getEntitiesInAABB(world, player.posX, player.posY, player.posZ, 25)) {
                if (entity != player) {
                    SpellUtils.addPotionEffect(entity, PotionRef.GLOWING, 400, 1, world, player, getElement());
                }
            }
            return true;
        }
    }.setSpellUseDuration(HOLD_LENGTH_LONG).setSpellCooldown(40);
    /* ---------------------------------------- GUARDIAN STAR ---------------------------------------- */
    public static Spell spellGuardianStar = new SpellProjectile(47, "guardian_star", Element.WHITE, 6,
            25, Sounds.CAST_GUARDIAN_STAR) {
        @Override
        public boolean spellEffect(ItemStack stack, EntityPlayer player, World world) {
            return SpellEffects.guardianStar(player, world, this);
        }

        @Override
        public boolean projectileEffectOnEntity(EntityMagicOrb orb, EntityPlayer player, Entity target, World world, MultiPartEntityPart part) {
            SpellUtils.attackEntity(6F, target, player, getElement(), world, part);
            return true;
        }
    }.setSpellUseDuration(HOLD_LENGTH_SHORT).setSpellCooldown(100);

    /*======================================== END OF SPELLS ========================================*/

    public static int getSpellForCrafting(Item ingredient, int meta) {
        for (int i = 0; i < list.size(); ++i) {
            Spell spell = list.get(i);
            if (ingredient instanceof ItemBlock) {
                if (spell.isIngredientBlock() && ((ItemBlock) ingredient).getBlock() == spell.getIngredientBlock()) {
                    if (spell.hasNoMeta() || meta == spell.getMeta()) {
                        return spell.getIndex();
                    }
                }
            }
            else {
                if (!spell.isIngredientBlock() && ingredient == spell.getIngredientItem()) {
                    if (spell.hasNoMeta() || meta == spell.getMeta()) {
                        return spell.getIndex();
                    }
                }
            }
        }

        return -1;
    }

    public static Spell getFromList(int index) {
        return list.stream().filter(spell -> spell.getIndex() == index).findFirst().orElse(null);
    }

    public static Spell getFromListCreative(int i) {
        return list.get(i);
    }

    public static void addToList(Spell spell) {
        list.add(spell);
    }

    public static void getSpellInfo(List<String> tooltip, Spell spell) {
        int level = spell.getLevel();
        String data;
        if (level == 6) {
            data = StringConstants.LEGENDARY + " ";
        }
        else {
            data = StringConstants.LEVEL + " " + spell.getLevel() + " ";
        }
        data += Element.getTextColor(spell.getElement()) + spell.getElement().getNameProper() +
                TextFormatting.GRAY + " " + StringConstants.SPELL;
        tooltip.add(data);
    }

    public static Spell getFromNBT(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Spell")) {
            int spellIndex = stack.getTagCompound().getInteger("Spell");
            if (spellIndex != -1) {
                return Spells.getFromList(spellIndex);
            }
        }
        return null;
    }

    public static int getListSize() {
        return list.size();
    }
}