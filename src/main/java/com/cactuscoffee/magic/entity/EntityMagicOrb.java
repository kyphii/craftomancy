package com.cactuscoffee.magic.entity;

import com.cactuscoffee.magic.data.Element;
import com.cactuscoffee.magic.data.SpellProjectile;
import com.cactuscoffee.magic.data.Spells;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nonnull;
import java.util.List;


public class EntityMagicOrb extends EntityThrowable implements IEntityAdditionalSpawnData {

    private SpellProjectile spell;
    public byte data;
    private int ignoreTime;
    private int life;

    public EntityMagicOrb(World world) {
        super(world);

        this.life = 100;
    }

    public EntityMagicOrb(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);

        this.life = 100;
    }

    public EntityMagicOrb(World world, EntityLivingBase player, SpellProjectile spell) {
        super(world);

        if (player != null) {
            this.setPosition(player.posX, player.posY + player.getEyeHeight() - 0.1, player.posZ);
            this.thrower = player;
            this.ignoreEntity = player;
        }
        this.spell = spell;
        this.life = 100;
    }

    @Override
    protected void onImpact(@Nonnull RayTraceResult result) {
        boolean kill = false;
        if (result.typeOfHit == RayTraceResult.Type.ENTITY) {
            if (!result.entityHit.isDead && (ticksExisted > 3 || !(result.entityHit instanceof EntityPlayer))) {
                Entity targetEntity = result.entityHit;
                MultiPartEntityPart part = null;

                if (targetEntity instanceof MultiPartEntityPart) {
                    part = (MultiPartEntityPart) targetEntity;
                    IEntityMultiPart ientitymultipart = part.parent;
                    if (ientitymultipart instanceof EntityLivingBase) {
                        targetEntity = (EntityLivingBase) ientitymultipart;
                    }
                }

                if (spell.projectileEffectOnEntity(this, (EntityPlayer) this.getThrower(),
                        targetEntity, this.getEntityWorld(), part)) {
                    kill = true;
                }
            }
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            if (spell.projectileEffectOnBlock(this, (EntityPlayer) this.getThrower(),
                    this.getEntityWorld(), result.getBlockPos(), result.sideHit)) {
                kill = true;
            }
        } else {
            spell.projectileEffect(this, (EntityPlayer) this.getThrower(), this.getEntityWorld());
            kill = true;
        }
        if (kill && !world.isRemote) {
            this.setDead();
        }
    }

    @Override
    public void onUpdate() {
        if (spell == null) {
            setDead();
        }
        else {
            spell.projectileTick(this, this.world);
        }
    }

    public void adjustStartPos() {
        this.posX += motionX;
        this.posY += motionY;
        this.posZ += motionZ;

        this.setPositionAndUpdate(posX, posY, posZ);
    }

    public Element getElement() {
        return spell.getElement();
    }

    @Override
    protected float getGravityVelocity() {
        return 0.02F;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("Spell", (byte) spell.getIndex());
        nbt.setByte("Data", data);
        nbt.setShort("Life", (short) life);
        nbt.setBoolean("Invisible", isInvisible());
        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        spell = (SpellProjectile) Spells.getFromList(nbt.getByte("Spell"));
        data = nbt.getByte("Data");
        life = nbt.getShort("Life");
        setInvisible(nbt.getBoolean("Invisible"));
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        if (spell != null) {
            buffer.writeInt(spell.getIndex());
        }
        else {
            buffer.writeInt(-1);
        }
        buffer.writeByte(data);
        buffer.writeShort(life);
        buffer.writeBoolean(isInvisible());
    }
    @Override
    public void readSpawnData(ByteBuf additionalData) {
        spell = (SpellProjectile) Spells.getFromList(additionalData.readInt());
        data = additionalData.readByte();
        life = additionalData.readShort();
        setInvisible(additionalData.readBoolean());
    }

    public void setLife(int life) {
        this.life = life;
    }

    public void processProjectile() {
        if (this.ticksExisted > life || this.inGround) {
            this.setDead();
        }
        else if (ticksExisted == 1) {
            adjustStartPos();
        }
        else {

            this.lastTickPosX = this.posX;
            this.lastTickPosY = this.posY;
            this.lastTickPosZ = this.posZ;

            if (!this.world.isRemote) {
                this.setFlag(6, this.isGlowing());
            }
            this.onEntityUpdate();

            Vec3d vec3d = new Vec3d(this.posX, this.posY, this.posZ);
            Vec3d vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d, vec3d1, false, true, false);
            vec3d = new Vec3d(this.posX, this.posY, this.posZ);
            vec3d1 = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (raytraceresult != null) {
                vec3d1 = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y, raytraceresult.hitVec.z);
            }

            Entity entity = null;
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(this.motionX, this.motionY, this.motionZ).grow(1.0D));
            double d0 = 0.0D;
            boolean flag = false;

            for (Entity entity1 : list) {
                if (entity1.canBeCollidedWith()) {
                    if (entity1 == this.ignoreEntity) {
                        flag = true;
                    } else if (this.thrower != null && this.ticksExisted < 2 && this.ignoreEntity == null) {
                        this.ignoreEntity = entity1;
                        flag = true;
                    } else {
                        flag = false;
                        AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow(0.3D);
                        RayTraceResult raytraceresult1 = axisalignedbb.calculateIntercept(vec3d, vec3d1);

                        if (raytraceresult1 != null) {
                            double d1 = vec3d.squareDistanceTo(raytraceresult1.hitVec);
                            if (d1 < d0 || d0 == 0.0D) {
                                entity = entity1;
                                d0 = d1;
                            }
                        }
                        else if (axisalignedbb.contains(vec3d)) {
                            entity = entity1;
                        }
                    }
                }
            }

            if (this.ignoreEntity != null) {
                if (flag) {
                    this.ignoreTime = 2;
                } else if (this.ignoreTime-- <= 0) {
                    this.ignoreEntity = null;
                }
            }

            if (entity != null) {
                raytraceresult = new RayTraceResult(entity);
            }

            if (raytraceresult != null) {
                if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK && this.world.getBlockState(raytraceresult.getBlockPos()).getBlock() == Blocks.PORTAL) {
                    this.setPortal(raytraceresult.getBlockPos());
                } else if (!net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
                    this.onImpact(raytraceresult);
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            this.rotationYaw = (float) (MathHelper.atan2(this.motionX, this.motionZ) * (180D / Math.PI));

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
                this.prevRotationPitch += 360.0F;
            }
            while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
                this.prevRotationYaw -= 360.0F;
            }
            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

            if (!this.hasNoGravity()) {
                this.motionY -= this.getGravityVelocity();
            }

            this.setPosition(this.posX, this.posY, this.posZ);
        }
    }
}
