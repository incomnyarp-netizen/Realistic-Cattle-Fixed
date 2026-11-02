package com.example.realisticcattle.entity.ai;

import com.example.realisticcattle.entity.CattleEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/** Social grooming: calm, trusting cattle approach a companion and groom one another. */
public class MutualGroomGoal extends Goal {
    private final CattleEntity cattle;
    private CattleEntity partner;
    private int duration;
    private int cooldown;

    public MutualGroomGoal(CattleEntity cattle) {
        this.cattle = cattle;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) { cooldown--; return false; }
        if (cattle.isBaby() || cattle.getTarget() != null || cattle.isInLove()) return false;
        if (cattle.getPersonality().calmness() < 40 || cattle.getPersonality().sociability() < 45) return false;
        if (cattle.getRandom().nextInt(180) != 0) return false;
        partner = cattle.findPreferredCompanion(8.0D);
        return partner != null && partner.getTarget() == null && !partner.isInLove()
                && cattle.distanceToSqr(partner) > 2.0D;
    }

    @Override
    public boolean canContinueToUse() {
        return partner != null && partner.isAlive() && duration < 100
                && cattle.getTarget() == null && cattle.distanceToSqr(partner) < 9.0D;
    }

    @Override
    public void start() {
        duration = 0;
        cattle.getNavigation().moveTo(partner, 0.75D);
    }

    @Override
    public void tick() {
        duration++;
        if (cattle.distanceToSqr(partner) > 3.0D) {
            if (duration % 20 == 0) cattle.getNavigation().moveTo(partner, 0.75D);
            return;
        }
        cattle.getNavigation().stop();
        cattle.getLookControl().setLookAt(partner, 25.0F, 20.0F);
        if (duration == 1) {
            cattle.setSocialAction(CattleEntity.ACTION_GROOM);
            partner.setSocialAction(CattleEntity.ACTION_GROOM);
        }
        if (duration == 60) {
            cattle.rememberCompanion(partner);
            partner.rememberCompanion(cattle);
            cattle.improveSocialComfort(2);
            partner.improveSocialComfort(2);
            cattle.improveTrust(1);
            partner.improveTrust(1);
        }
    }

    @Override
    public void stop() {
        cattle.clearSocialAction();
        if (partner != null) partner.clearSocialAction();
        cattle.getNavigation().stop();
        partner = null;
        cooldown = 600 + cattle.getRandom().nextInt(600);
    }
}
