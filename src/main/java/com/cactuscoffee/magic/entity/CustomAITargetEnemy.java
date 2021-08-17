package com.cactuscoffee.magic.entity;

import com.cactuscoffee.magic.utils.SpellUtils;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class CustomAITargetEnemy extends EntityAITarget {
    private final EntityLivingBase owner;
    private EntityLivingBase target;

    public CustomAITargetEnemy(EntityCreature self, EntityLivingBase owner) {
        super(self, false);
        this.owner = owner;
    }

    @Override
    public boolean shouldExecute() {
        if (owner != null) {
            for (EntityLivingBase entity :
                    SpellUtils.getEntitiesInAABB(taskOwner.world, owner.posX, owner.posY, owner.posZ, 12)) {
                if (SpellUtils.isEnemy(entity, owner, taskOwner.getClass())) {
                    target = entity;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void startExecuting() {
        if (target != null) {
            taskOwner.setAttackTarget(target);
            super.startExecuting();
        }
    }
}