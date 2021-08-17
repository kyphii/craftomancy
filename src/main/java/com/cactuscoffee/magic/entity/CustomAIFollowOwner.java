package com.cactuscoffee.magic.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class CustomAIFollowOwner extends EntityAIBase {
    private final EntityCreature self;
    private final EntityLivingBase owner;
    private int timeToRecalcPath;
    private final float stopDistance;

    public CustomAIFollowOwner(EntityCreature self, EntityLivingBase owner, float stopDistance) {
        this.self = self;
        this.owner = owner;
        this.stopDistance = stopDistance;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        return owner != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return owner != null && self.getDistanceSq(owner) > stopDistance * stopDistance;
    }

    @Override
    public void startExecuting() {
        this.timeToRecalcPath = 0;
    }

    @Override
    public void updateTask() {
        if (owner != null) {
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                double d0 = self.posX - owner.posX;
                double d1 = self.posY - owner.posY;
                double d2 = self.posZ - owner.posZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d3 > stopDistance * stopDistance) {
                    self.getMoveHelper().setMoveTo(owner.posX, owner.posY + 0.5, owner.posZ, 1);
                    self.getLookHelper().setLookPositionWithEntity(owner,
                            self.getHorizontalFaceSpeed(), self.getVerticalFaceSpeed());
                }
                else {
                    self.getMoveHelper().setMoveTo(self.posX, self.posY, self.posZ, 0);
                }
            }
        }
    }
}