package com.cactuscoffee.magic.data;

import com.cactuscoffee.magic.entity.*;
import com.cactuscoffee.magic.item.ItemModArmor;
import com.cactuscoffee.magic.utils.SpellUtils;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.PotionType;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class SpellEffects {
    static void flintAndSteel(World world, BlockPos pos, EnumFacing facing) {
        pos = pos.offset(facing);

        if (world.isAirBlock(pos)) {
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS,
                    1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
            world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 11);
        }
    }

    static boolean boneMeal(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand) {
        boolean worked = false;

        if (world instanceof net.minecraft.world.WorldServer) {
            IBlockState iblockstate = world.getBlockState(pos);

            int hook = net.minecraftforge.event.ForgeEventFactory.onApplyBonemeal(player, world, pos, iblockstate, stack, hand);
            if (hook != 0) return hook > 0;

            if (iblockstate.getBlock() instanceof IGrowable) {
                IGrowable igrowable = (IGrowable) iblockstate.getBlock();

                if (igrowable.canGrow(world, pos, iblockstate, world.isRemote)) {
                    if (!world.isRemote) {
                        if (igrowable.canUseBonemeal(world, world.rand, pos, iblockstate)) {
                            igrowable.grow(world, world.rand, pos, iblockstate);
                        }
                    }

                    worked = true;
                }
            }
        }

        if (worked) {
            if (!world.isRemote) {
                world.playEvent(2005, pos, 0);
            }
            return true;
        }
        return false;
    }

    static boolean shadowBomb(EntityPlayer player, World world, BlockPos pos) {
        if (!world.isRemote) {
            EntityStealthMine mine = new EntityStealthMine(world, player);
            mine.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

            world.spawnEntity(mine);
            return true;
        }
        else {
            for (int i = 0; i < 4; ++i) {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                        pos.getX() + (world.rand.nextFloat() / 2) + 0.25F,
                        pos.getY(),
                        pos.getZ() + (world.rand.nextFloat() / 2) + 0.25F,
                        (world.rand.nextFloat() / 2) - 0.25F, 0.02F, (world.rand.nextFloat() / 2) - 0.25F);
            }
            return false;
        }
    }

    static void overgrow(BlockPos pos, World world) {
        for (BlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-1.5, -1, -1.5), pos.add(1.5, 3, 1.5))) {
            if (world.rand.nextFloat() < 0.8 &&
                    blockpos.distanceSq(pos.getX(), pos.getY(), pos.getZ()) < 5 &&
                    world.getBlockState(blockpos).getMaterial().isReplaceable() &&
                    !world.getBlockState(blockpos).getMaterial().isLiquid()) {
                BlockPlanks.EnumType type = BlockPlanks.EnumType.OAK;
                switch (world.rand.nextInt(4)) {
                    case 1:
                        type = BlockPlanks.EnumType.BIRCH;
                        break;
                    case 2:
                        type = BlockPlanks.EnumType.SPRUCE;
                        break;
                    case 3:
                        type = BlockPlanks.EnumType.JUNGLE;
                        break;
                }
                world.setBlockState(blockpos, Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, type));
            }
        }
    }

    static boolean blink(EntityPlayer player, World world) {
        Vec3d vec = player.getLookVec();
        double x = player.getPositionVector().x - 0.5F;
        double y = player.getPositionVector().y + 1;
        double z = player.getPositionVector().z - 0.5F;

        for (int i = 0; i < 8; ++i) {
            double xi = x + vec.x;
            double yi = y + vec.y;
            double zi = z + vec.z;

            if (!world.getBlockState(new BlockPos(xi, yi, zi)).getMaterial().blocksMovement()) {
                x = xi;
                y = yi;
                z = zi;
            }
            else {
                break;
            }
        }

        int yi = (int) y;

        for (int i = 0; i < 8; i++) {
            if (world.getBlockState(new BlockPos(x, yi, z)).getMaterial().blocksMovement()) {
                y = yi + 1;
                break;
            }
            else {
                --yi;
            }
        }

        return SpellEffects.teleport(player, x, y, z);
    }

    private static boolean teleport(EntityPlayer player, double x, double y, double z) {
        if (player.onGround || player.isInWater()) {
            double d0 = player.posX;
            double d1 = player.posY;
            double d2 = player.posZ;
            boolean flag = false;
            BlockPos blockpos = new BlockPos(x, y, z);
            World world = player.world;
            Random random = player.getRNG();

            if (world.getBlockState(blockpos).getMaterial().blocksMovement()) {
                return false;
            }

            player.posX = x + 0.5;
            player.posY = y;
            player.posZ = z + 0.5;
            world.playSound(null, x, y, z, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT,
                    SoundCategory.PLAYERS, 1.0F, 1F);
            player.setPositionAndUpdate(player.posX, player.posY, player.posZ);

            if (world.getCollisionBoxes(player, player.getEntityBoundingBox()).isEmpty()) {
                flag = true;
            }

            if (!flag) {
                return false;
            } else {
                for (int j = 0; j < 128; ++j) {
                    double d6 = (double) j / 127.0D;
                    float f = (random.nextFloat() - 0.5F) * 0.2F;
                    float f1 = (random.nextFloat() - 0.5F) * 0.2F;
                    float f2 = (random.nextFloat() - 0.5F) * 0.2F;
                    double d3 = d0 + (player.posX - d0) * d6 + (random.nextDouble() - 0.5D) * (double) player.width * 2.0D;
                    double d4 = d1 + (player.posY - d1) * d6 + random.nextDouble() * (double) player.height;
                    double d5 = d2 + (player.posZ - d2) * d6 + (random.nextDouble() - 0.5D) * (double) player.width * 2.0D;
                    world.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, (double) f, (double) f1, (double) f2);
                }

                return true;
            }
        }
        return false;
    }

    static boolean homeTeleport(EntityPlayer player) {
        boolean flag = false;
        if (!player.isInWater() && player.world.provider.getDimensionType() == DimensionType.OVERWORLD) {
            if (player.bedLocation != null) {
                IBlockState iblockstate = player.world.getBlockState(player.bedLocation);
                if (iblockstate.getBlock().isBed(iblockstate, player.world, player.bedLocation, player)) {
                    BlockPos blockpos = iblockstate.getBlock().getBedSpawnPosition(iblockstate, player.world, player.bedLocation, player);

                    if (blockpos == null) {
                        blockpos = player.bedLocation.up();
                    }

                    flag = SpellEffects.teleport(player, blockpos.getX(), blockpos.getY(), blockpos.getZ());
                }
            }
            if (!flag) {
                BlockPos pos = player.world.getSpawnPoint();
                while (player.world.getBlockState(pos).getMaterial().blocksMovement()) {
                    pos = pos.up();
                }
                flag = SpellEffects.teleport(player, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        if (flag) {
            SpellUtils.makeParticles(player, player.world, EnumParticleTypes.CLOUD, 12, true, true);
        }
        return flag;
    }

    static void radiance(EntityPlayer player, World world) {
        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;

        int size = 5;

        for (Entity entity : SpellUtils.getEntitiesInAABB(world, player.posX, player.posY, player.posZ, 5)) {
            if (player.getDistanceSq(entity) < size * size) {
                if (SpellUtils.isEnemy(entity, player, null)) {
                    entity.attackEntityFrom(SpellUtils.magicDamageSource(entity, player, false),
                            SpellUtils.getDamageWithBonus(5F, player, Element.WHITE));
                }
            }
        }
        for (int i = -size; i <= size; i++) {
            for (int j = -size; j <= size; j++) {
                for (int k = -size; k <= size; k++) {
                    if (player.getDistance(x + i, y + j, z + k) < size && world.rand.nextInt(9) == 0) {
                        world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK,
                                x + i + ((world.rand.nextDouble() * 2) - 1),
                                y + j + ((world.rand.nextDouble() * 2) - 1),
                                z + k + ((world.rand.nextDouble() * 2) - 1),
                                0, 0, 0);
                    }
                }
            }
        }
    }

    static boolean guardianStar(EntityPlayer player, World world, SpellProjectile spell) {
        if (!world.isRemote && player != null) {

            AxisAlignedBB aabb = new AxisAlignedBB(
                    player.posX - 1, player.posY - 2, player.posZ - 1,
                    player.posX + 1, player.posY + 2, player.posZ + 1);
            List<EntityGuardStar> entityList = world.getEntitiesWithinAABB(EntityGuardStar.class, aabb);

            if (entityList.isEmpty()) {
                EntityGuardStar entity = new EntityGuardStar(world, player, spell);
                world.spawnEntity(entity);
                return true;
            }
        }
        return false;
    }

    static void seeker(EntityMagicOrb orb, World world) {
        float AABB_SIZE = 8;
        Entity closest = null;
        float dist = AABB_SIZE;
        for (Entity target : SpellUtils.getEntitiesInAABB(world, orb.posX, orb.posY, orb.posZ, AABB_SIZE)) {
            if (SpellUtils.isEnemy(target, orb.getThrower(), null) && target != orb.getThrower()) {
                float d = target.getDistance(orb);
                if (d < dist) {
                    dist = d;
                    closest = target;
                }
            }
        }

        if (closest != null) {
            double d1 = closest.posX - orb.posX;
            double d2 = (closest.posY + closest.getEyeHeight()) - orb.posY;
            double d3 = closest.posZ - orb.posZ;

            orb.setNoGravity(true);
            orb.shoot(d1, d2, d3, 1.5F, 0F);

            orb.setPositionAndUpdate(orb.posX, orb.posY, orb.posZ);
        }
    }

    static boolean summonCloud(PotionType potionType, int color, int duration, float size,
                               World world, EntityLivingBase owner, double x, double y, double z) {
        if (!world.isRemote) {
            EntityAreaEffectCloud entity = new EntityAreaEffectCloud(world, x, y, z);
            entity.setRadius(size);
            entity.setDuration(duration);
            entity.setPotion(potionType);
            entity.setOwner(owner);
            entity.setWaitTime(10);
            entity.setColor(color);

            world.spawnEntity(entity);
            return true;
        }
        return false;
    }

    static void erupt(EntityPlayer player, World world) {
        double y = player.posY;

        player.posY += 16;

        SpellUtils.customExplosion(world, player, Element.RED,
                player.posX, y, player.posZ, 3F, 0F, true, false);

        player.posY = y;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    BlockPos pos = new BlockPos(player.posX + i, player.posY + j, player.posZ + k);
                    if (world.getBlockState(pos).getBlock().equals(Blocks.FIRE)) {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                }
            }
        }
    }

    static void tailwind(EntityPlayer player, World world) {
        for (Entity target : SpellUtils.getEntitiesInAABB(world, player.posX, player.posY, player.posZ, 6)) {
            if (!SpellUtils.isEnemy(target, player, null)) {
                float bonus = ItemModArmor.getElementalBonus(player)[Element.YELLOW.getMeta()];
                SpellUtils.addPotionEffect(target, PotionRef.SPEED, 400, 0, world, bonus);
                SpellUtils.addPotionEffect(target, PotionRef.HASTE, 400, 2, world, bonus);
                if (world.isRemote) {
                    for (int i = 0; i < 4; ++i) {
                        world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK,
                                target.posX + (world.rand.nextFloat() / 2) + 0.25F,
                                target.posY,
                                target.posZ + (world.rand.nextFloat() / 2) + 0.25F,
                                (world.rand.nextFloat() / 2) - 0.25F, 0.02F, (world.rand.nextFloat() / 2) - 0.25F);
                    }
                }
            }
        }
    }

    static boolean smelt(EntityPlayer player, World world) {
        if (!world.isRemote) {
            ItemStack result = ItemStack.EMPTY;
            ItemStack smeltStack = ItemStack.EMPTY;
            for (ItemStack stack : player.inventory.mainInventory) {
                result = FurnaceRecipes.instance().getSmeltingResult(stack);
                if (!result.isEmpty()) {
                    smeltStack = stack;
                    break;
                }
            }

            if (!smeltStack.isEmpty()) {
                player.dropItem(result, false);
                smeltStack.shrink(1);
                return true;
            }
        }
        SpellUtils.makeParticles(player, world, EnumParticleTypes.FLAME, 6, true, true);
        return false;
    }

    static boolean tidalWave(EntityPlayer player, World world, SpellProjectile spell) {
        if (!world.isRemote) {
            int number = 4;
            float speed = 0.6F;
            int spreadMult = 16;
            byte data = 0;
            if (player.isInWater()) {
                number = 8;
                speed = 1.0F;
                spreadMult = 8;
                data = 1;
            }
            for (int i = 0; i < number; ++i) {
                EntityMagicOrb entity = new EntityMagicOrb(world, player, spell);
                entity.data = data;
                entity.setNoGravity(false);
                entity.shoot(player,
                        player.rotationPitch + ((world.rand.nextFloat() - 0.5F) * spreadMult),
                        player.rotationYaw + ((world.rand.nextFloat() - 0.5F) * spreadMult),
                        0.0F, speed, 0F);
                world.spawnEntity(entity);
            }
            return true;
        }
        return false;
    }

    static boolean makeLiquid(World world, BlockPos blockPos, BlockDynamicLiquid liquid) {
        if (!world.isRemote) {
            world.setBlockState(blockPos, liquid.getStateFromMeta(1), 11);
            List<EnumFacing> sides = Arrays.asList(EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH);
            for (EnumFacing side : sides) {
                BlockPos b = blockPos.offset(side);
                if (world.getBlockState(b).getBlock().isReplaceable(world, b)) {
                    world.setBlockState(b, liquid.getStateFromMeta(2), 11);
                }
            }
            return true;
        }
        return false;
    }

    static void excavate(World world, BlockPos pos) {
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    BlockPos p = pos.add(i, j, k);
                    IBlockState state = world.getBlockState(p);
                    if (state.getBlock().getHarvestLevel(state) == 0 &&
                            state.getBlockHardness(world, pos) < 1.9F &&
                            (state.getBlock().isToolEffective("pickaxe", state) || state.getBlock().isToolEffective("shovel", state)))
                        world.destroyBlock(p, world.rand.nextBoolean());
                }
            }
        }
    }

    static void absoluteZero(Entity source, EntityPlayer player, World world, SpellProjectile spell, int data) {
        if (!world.isRemote && player != null) {
            int YAW_INC = 60;
            double y = source.posY + (source.getEyeHeight() / 4);

            for (float i = 0; i < 360; i += YAW_INC) {
                EntityMagicOrb entity = new EntityMagicOrb(world, player, spell);
                entity.setNoGravity(true);
                entity.setPosition(source.posX, y, source.posZ);
                entity.data = (byte) data;
                entity.ignoreEntity = source;

                float yawRad = i * 0.017453292F;
                float mx = -MathHelper.sin(yawRad);
                float mz = MathHelper.cos(yawRad);
                entity.shoot(mx, 0, mz, 0.5F, 0F);
                world.spawnEntity(entity);
            }
            world.playSound(null, source.posX, source.posY, source.posZ,
                    Sounds.CAST_AVALANCHE, SoundCategory.PLAYERS,
                    0.4F, world.rand.nextFloat() * 0.1F + 0.9F);
        }
    }

    static void chillStride(EntityPlayer player, World world) {
        if (!world.isRemote && player.onGround) {
            float f = 2;
            BlockPos posCenter = player.getPosition();

            for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(
                    posCenter.add(-f, -1, -f),
                    posCenter.add(f, 1, f))) {
                if (pos.distanceSqToCenter(player.posX, player.posY, player.posZ) <= 4) {
                    IBlockState state = world.getBlockState(pos);

                    if (state.getMaterial() == Material.AIR) {
                        BlockPos pos2 = pos.down();
                        IBlockState state2 = world.getBlockState(pos2);

                        if (state2.getMaterial() == Material.WATER && state2.getValue(BlockLiquid.LEVEL) == 0 &&
                                world.mayPlace(Blocks.FROSTED_ICE, pos, false, EnumFacing.DOWN, null)) {
                            world.setBlockState(pos2, Blocks.FROSTED_ICE.getDefaultState());
                            world.scheduleUpdate(pos2.toImmutable(), Blocks.FROSTED_ICE,
                                    MathHelper.getInt(player.getRNG(), 60, 120));
                        }
                        else if (state2.getMaterial() == Material.LAVA && state2.getValue(BlockLiquid.LEVEL) == 0 &&
                                world.mayPlace(Blocks.OBSIDIAN, pos, false, EnumFacing.DOWN, null)) {
                            world.setBlockState(pos2, Blocks.OBSIDIAN.getDefaultState());
                        }
                        else if (!world.provider.doesWaterVaporize() && Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos)
                                && state2.getMaterial() != Material.ICE) {
                            world.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState());
                        }
                    }
                    else if (state.getMaterial() == Material.FIRE) {
                        world.playEvent(player, 1009, pos, 0);
                        world.setBlockToAir(pos);
                    }
                }
            }
        }
    }

    static boolean fissure(EntityPlayer player, World world) {
        if (!world.isRemote) {
            float yawRad = player.rotationYaw * 0.017453292F;

            for (int i = 1; i < 10; ++i) {
                double d = i * 1.25;

                double x = player.posX - (MathHelper.sin(player.rotationYaw * 0.017453292F) * d);
                double z = player.posZ + (MathHelper.cos(player.rotationYaw * 0.017453292F) * d);
                BlockPos blockpos = new BlockPos(x, player.posY + 8, z);

                double d0 = 0.0D;

                while (true) {
                    if (!world.isBlockNormalCube(blockpos, true) && world.isBlockNormalCube(blockpos.down(), true)) {
                        if (!world.isAirBlock(blockpos)) {
                            IBlockState iblockstate = world.getBlockState(blockpos);
                            AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(world, blockpos);

                            if (axisalignedbb != null) {
                                d0 = axisalignedbb.maxY;
                            }
                        }

                        EntityEvokerFangs entity = new EntityEvokerFangs(
                                world, x, (double) blockpos.getY() + d0, z, yawRad, (i * 2) - 2, player);
                        world.spawnEntity(entity);
                        break;
                    }

                    blockpos = blockpos.down();

                    if (blockpos.getY() < player.posY - 8) {
                        break;
                    }
                }
            }
            return true;
        }
        return false;
    }
}
