package com.example.realisticcattle.entity.ai;

import com.example.realisticcattle.entity.CattleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

/** Lets cattle scratch their forehead/horns against nearby logs and wood. */
public class HornRubGoal extends Goal {
    private final CattleEntity cattle;
    private BlockPos targetLog;
    private int actionTicks;
    private int cooldown;

    public HornRubGoal(CattleEntity cattle) {
        this.cattle = cattle;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) { cooldown--; return false; }
        if (cattle.isBaby() || cattle.isVehicle() || cattle.isInLove()) return false;
        if (cattle.getRandom().nextInt(150) != 0) return false;
        targetLog = findNearbyLog();
        return targetLog != null;
    }

    @Override
    public boolean canContinueToUse() {
        return targetLog != null && actionTicks < 100 && cattle.isAlive()
                && cattle.level().getBlockState(targetLog).is(BlockTags.LOGS);
    }

    @Override
    public void start() {
        actionTicks = 0;
        cattle.getNavigation().moveTo(targetLog.getX() + 0.5D, targetLog.getY(), targetLog.getZ() + 0.5D, 0.8D);
    }

    @Override
    public void tick() {
        actionTicks++;
        double distance = cattle.distanceToSqr(targetLog.getX() + 0.5D, targetLog.getY(), targetLog.getZ() + 0.5D);
        if (distance > 3.2D) {
            if (actionTicks % 20 == 0) cattle.getNavigation().moveTo(targetLog.getX() + 0.5D, targetLog.getY(), targetLog.getZ() + 0.5D, 0.8D);
            return;
        }
        cattle.getNavigation().stop();
        cattle.getLookControl().setLookAt(targetLog.getX() + 0.5D, targetLog.getY() + 0.5D, targetLog.getZ() + 0.5D);
        if (actionTicks == 1) cattle.setSocialAction(CattleEntity.ACTION_HORN_RUB);
    }

    @Override
    public void stop() {
        cattle.clearSocialAction();
        cattle.getNavigation().stop();
        targetLog = null;
        cooldown = 500 + cattle.getRandom().nextInt(700);
    }

    private BlockPos findNearbyLog() {
        BlockPos origin = cattle.blockPosition();
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-5, -2, -5), origin.offset(5, 2, 5))) {
            BlockState state = cattle.level().getBlockState(pos);
            if (!state.is(BlockTags.LOGS)) continue;
            double d = cattle.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
            if (d < bestDistance) { bestDistance = d; best = pos.immutable(); }
        }
        return best;
    }
}
