package com.example.realisticcattle.entity.ai;

import com.example.realisticcattle.entity.CattleEntity;
import com.example.realisticcattle.entity.CattleSex;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.Comparator;

/** Calf seeks its recorded dam, settles beside her and nurses in short bouts. */
public class CalfNursingGoal extends Goal {
    private final CattleEntity calf;
    private CattleEntity mother;
    private int elapsed;
    private int cooldown;

    public CalfNursingGoal(CattleEntity calf) {
        this.calf = calf;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override public boolean canUse() {
        if (!calf.isBaby() || calf.isInWaterOrBubble() || calf.getHealth() <= 0) return false;
        if (cooldown > 0) { cooldown--; return false; }
        if (calf.getRandom().nextInt(35) != 0) return false;
        mother = findMother();
        return mother != null && mother.isAlive() && !mother.isBaby();
    }

    private CattleEntity findMother() {
        if (calf.getMotherUuid() != null) {
            return calf.level().getEntitiesOfClass(CattleEntity.class, calf.getBoundingBox().inflate(24),
                    e -> e.getUUID().equals(calf.getMotherUuid())).stream().findFirst().orElse(null);
        }
        return calf.level().getEntitiesOfClass(CattleEntity.class, calf.getBoundingBox().inflate(12),
                e -> e != calf && !e.isBaby() && e.getSex() == CattleSex.FEMALE).stream()
                .min(Comparator.comparingDouble(calf::distanceToSqr)).orElse(null);
    }

    @Override public boolean canContinueToUse() {
        return mother != null && mother.isAlive() && elapsed < 100 && calf.isBaby()
                && calf.distanceToSqr(mother) < 100;
    }

    @Override public void start() {
        elapsed = 0;
        calf.getNavigation().moveTo(mother, 1.05D);
    }

    @Override public void tick() {
        elapsed++;
        if (calf.distanceToSqr(mother) > 3.0D) {
            if (elapsed % 15 == 0) calf.getNavigation().moveTo(mother, 1.05D);
            return;
        }
        calf.getNavigation().stop();
        calf.getLookControl().setLookAt(mother, 20.0F, 20.0F);
        if (elapsed == 1) {
            calf.setSocialAction(CattleEntity.ACTION_NURSE);
            mother.setSocialAction(CattleEntity.ACTION_MOTHER_CARE);
        }
        if (elapsed == 40) {
            calf.setNeeds(Math.min(100, calf.getHunger() + 12), calf.getThirst(), calf.getComfort(), calf.getSocial());
            mother.setNeeds(Math.max(0, mother.getHunger() - 1), mother.getThirst(), mother.getComfort(), mother.getSocial());
            calf.improveTrust(1);
            mother.rememberCompanion(calf);
            calf.rememberCompanion(mother);
        }
    }

    @Override public void stop() {
        calf.clearSocialAction();
        if (mother != null && mother.isAlive()) mother.clearSocialAction();
        calf.getNavigation().stop();
        mother = null;
        cooldown = 240 + calf.getRandom().nextInt(240);
    }
}
