package com.example.realisticcattle.entity.ai;

import com.example.realisticcattle.entity.CattleEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/** Optional social behavior: sociable cattle sometimes approach nearby companions, but can live alone. */
public class SeekHerdGoal extends Goal {
    private final CattleEntity cattle;
    private CattleEntity target;
    private int repathDelay;

    public SeekHerdGoal(CattleEntity cattle) {
        this.cattle = cattle;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (cattle.isBaby() || cattle.getPersonality().sociability() < 42 || cattle.isInLove()) return false;
        if (cattle.getRandom().nextInt(5) != 0) return false;
        target = cattle.findNearbyCompanion(12.0D);
        return target != null && cattle.distanceToSqr(target) > 16.0D;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && target.isAlive() && !cattle.isBaby()
                && cattle.distanceToSqr(target) > 6.25D
                && cattle.getNavigation().isInProgress();
    }

    @Override
    public void start() {
        repathDelay = 0;
        moveTowardCompanion();
    }

    @Override
    public void tick() {
        if (--repathDelay <= 0) {
            repathDelay = 20;
            moveTowardCompanion();
        }
    }

    @Override
    public void stop() {
        target = null;
        cattle.getNavigation().stop();
    }

    private void moveTowardCompanion() {
        if (target != null && target.isAlive()) {
            cattle.getNavigation().moveTo(target, 0.85D);
        }
    }
}
