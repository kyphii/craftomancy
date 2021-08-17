package com.cactuscoffee.magic.entity;

import com.cactuscoffee.magic.data.Sounds;
import com.cactuscoffee.magic.data.SpellProjectile;
import com.cactuscoffee.magic.utils.SpellUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class EntityGuardStar extends Entity {
    private final EntityPlayer player;
    private SpellProjectile spell;

    private static final int AABB_SIZE = 16;

    public EntityGuardStar(World world) {
        super(world);
        player = null;
    }

    @Override
    protected void entityInit() {

    }

    public EntityGuardStar(World world, EntityPlayer player, SpellProjectile spell) {
        super(world);
        this.player = player;
        this.spell = spell;
        this.update();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (player != null) {
            update();
        }
        else if (!world.isRemote) {
            setDead();
        }
        if (ticksExisted > 1200) {
            spawnAnim(world);
            setDead();
        }
    }

    private void update() {
        this.setPosition(player.posX, player.posY, player.posZ);
        this.startRiding(player);

        if (ticksExisted % 10 == 0) {
            EntityLivingBase closest = null;
            float dist = AABB_SIZE;
            for (EntityLivingBase target : SpellUtils.getEntitiesInAABB(world, posX, posY, posZ, AABB_SIZE)) {
                if (SpellUtils.isEnemy(target, player, null)) {
                    float d = target.getDistance(player);
                    if (d < dist) {
                        dist = d;
                        closest = target;
                    }
                }
            }

            EntityMagicOrb orb = new EntityMagicOrb(world, player, this.spell);
            orb.posY += 1F;

            if (closest != null) {
                double d1 = closest.posX - this.posX;
                double d2 = (closest.posY + (closest.getEyeHeight() / 2F)) - orb.posY;
                double d3 = closest.posZ - this.posZ;

                orb.shoot(d1, d2, d3, 1F, 0F);
                this.world.spawnEntity(orb);

                playSound(Sounds.CAST_PROJECTILE, 0.3F, world.rand.nextFloat() * 0.1F + 0.9F);
            }
        }
    }

    public void spawnAnim(World world) {
        playSound(Sounds.CAST_GUARDIAN_STAR, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        for (int i = 0; i < 8; ++i) {
            float dir = world.rand.nextFloat() * 6.28F;
            world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK,
                    posX, posY + 1F, posZ,
                    Math.cos(dir) / 4, 0, Math.sin(dir) / 4);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {

    }
}
