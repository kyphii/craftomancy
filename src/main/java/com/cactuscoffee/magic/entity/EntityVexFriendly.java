package com.cactuscoffee.magic.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.world.World;

public class EntityVexFriendly extends EntityVex {
    private EntityLivingBase owner;

    public EntityVexFriendly(World worldIn) {
        super(worldIn);
        this.onInitialSpawn(worldIn.getDifficultyForLocation(getPosition()), null);
        this.setLimitedLife(600);
    }

    public EntityVexFriendly(World world, EntityLivingBase owner) {
        this(world);
        this.setLimitedLife(600);
        this.owner = owner;
        initEntityAI();
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        targetTasks.taskEntries.clear();
        if (owner != null) {
            targetTasks.addTask(0, new CustomAITargetEnemy(this, owner));
            tasks.taskEntries.removeIf(task -> task.priority == 8);
            tasks.addTask(5, new CustomAIFollowOwner(this, owner, 3));
        }
    }
}
