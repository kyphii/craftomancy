package com.cactuscoffee.magic.entity;

import com.cactuscoffee.magic.data.Element;
import com.cactuscoffee.magic.data.SpellEffects;
import com.cactuscoffee.magic.utils.SpellUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityStealthMine extends Entity
{
    private static final int AABB_SIZE = 3;
    private static final int LIFE_MAX = 3600;
    private final EntityPlayer creator;

    public EntityStealthMine(World world) {
        super(world);
        this.creator = null;
        this.ticksExisted = 0;
    }

    public EntityStealthMine(World world, EntityPlayer player) {
        super(world);
        this.creator = player;
        this.ticksExisted = 0;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (ticksExisted >= LIFE_MAX) {
            setDead();
        }

        if (world.isRemote) {
            if (ticksExisted % 8 == 0 && rand.nextInt(6) == 0) {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
                        posX + (rand.nextFloat() / 2) - 0.25F,
                        posY,
                        posZ + (rand.nextFloat() / 2) - 0.25F,
                        0, 0.02F, 0);
            }
        }
        if (canTrigger()) {
            SpellUtils.customExplosion(world, this.creator, Element.BLACK, posX, posY + 1F, posZ,
                    2.8F, 0F, false, false);
            setDead();
        }
    }

    private boolean canTrigger() {
        for (EntityLivingBase e : SpellUtils.getEntitiesInAABB(world, posX, posY, posZ, AABB_SIZE)) {
            if (!e.isSneaking() && e.getDistance(this) < 1.5F) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void entityInit() {
    }

    @Override
    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Override
    protected void readEntityFromNBT(@Nonnull NBTTagCompound compound) {}

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound compound) {}
}