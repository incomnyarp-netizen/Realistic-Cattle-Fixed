package com.example.realisticcattle.genetics;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

/** Compact diploid toy-genetics model. Each locus stores two alleles (0..3); values are game rules, not real breeding predictions. */
public final class CattleGenome {
    // coat: 0 black, 1 red, 2 fawn, 3 brown; pattern: 0 solid, 1 spotted, 2 roan; horn: 0 horned, 1 polled.
    private final int coatA, coatB, patternA, patternB, hornA, hornB;

    public CattleGenome(int coatA, int coatB, int patternA, int patternB, int hornA, int hornB) {
        this.coatA = allele(coatA, 3); this.coatB = allele(coatB, 3);
        this.patternA = allele(patternA, 2); this.patternB = allele(patternB, 2);
        this.hornA = allele(hornA, 1); this.hornB = allele(hornB, 1);
    }
    private static int allele(int v, int max) { return Math.max(0, Math.min(max, v)); }
    public static CattleGenome randomFor(CattleBreed breed, RandomSource r) {
        int coat = breed == CattleBreed.JERSEY ? 2 : breed == CattleBreed.HEREFORD || breed == CattleBreed.HIGHLAND ? 1 : breed == CattleBreed.ABERDEEN_ANGUS ? 0 : 0;
        int pattern = breed == CattleBreed.ABERDEEN_ANGUS ? 0 : 1;
        if (r.nextFloat() < .18f) coat = r.nextInt(4);
        if (r.nextFloat() < .12f) pattern = r.nextInt(3);
        int secondaryCoat = r.nextFloat() < .22f ? r.nextInt(4) : coat;
        int secondaryPattern = r.nextFloat() < .16f ? r.nextInt(3) : pattern;
        int hornA = r.nextFloat() < .65f ? 0 : 1;
        int hornB = r.nextFloat() < .65f ? 0 : 1;
        return new CattleGenome(coat, secondaryCoat, pattern, secondaryPattern, hornA, hornB);
    }
    public static CattleGenome fromPhenotype(CattlePhenotype p, boolean horned) {
        int coat = switch (p) { case RED_WHITE -> 1; case JERSEY_FAWN -> 2; case BROWN_WHITE -> 3; default -> 0; };
        int pattern = switch (p) { case HOLSTEIN_BLACK_WHITE, RED_WHITE, GREY_ROAN, BROWN_WHITE -> p == CattlePhenotype.GREY_ROAN ? 2 : 1; default -> 0; };
        int horn = horned ? 0 : 1;
        return new CattleGenome(coat, coat, pattern, pattern, horn, horn);
    }
    public static CattleGenome inherit(CattleGenome mother, CattleGenome father, RandomSource r) {
        return new CattleGenome(pick(mother.coatA, mother.coatB, r), pick(father.coatA, father.coatB, r),
                pick(mother.patternA, mother.patternB, r), pick(father.patternA, father.patternB, r),
                pick(mother.hornA, mother.hornB, r), pick(father.hornA, father.hornB, r));
    }
    private static int pick(int a, int b, RandomSource r) { return r.nextBoolean() ? a : b; }
    public CattlePhenotype express(CattleBreed breed) {
        int coat = Math.max(coatA, coatB); // game dominance order: brown > fawn > red > black
        int pattern = Math.max(patternA, patternB);
        if (pattern == 2) return CattlePhenotype.GREY_ROAN;
        if (pattern == 1) return switch (coat) { case 1 -> CattlePhenotype.RED_WHITE; case 2 -> CattlePhenotype.JERSEY_FAWN; case 3 -> CattlePhenotype.BROWN_WHITE; default -> CattlePhenotype.HOLSTEIN_BLACK_WHITE; };
        return switch (coat) { case 1 -> CattlePhenotype.RED_WHITE; case 2 -> CattlePhenotype.JERSEY_FAWN; case 3 -> CattlePhenotype.BROWN_WHITE; default -> CattlePhenotype.SOLID_BLACK; };
    }
    public boolean isHorned() { return hornA == 0 && hornB == 0; }
    public CattleGenome withHorned(boolean value) {
        int allele = value ? 0 : 1;
        return new CattleGenome(coatA, coatB, patternA, patternB, allele, allele);
    }
    public String describe() {
        return "Шерсть " + coatA + "/" + coatB + ", рисунок " + patternA + "/" + patternB +
                ", рога " + (hornA == 0 ? "H" : "p") + "/" + (hornB == 0 ? "H" : "p");
    }
    public boolean hasBreedStandardPhenotype(CattleBreed breed) {
        CattlePhenotype p = express(breed);
        return switch (breed) {
            case HOLSTEIN -> p == CattlePhenotype.HOLSTEIN_BLACK_WHITE || p == CattlePhenotype.RED_WHITE;
            case JERSEY -> p == CattlePhenotype.JERSEY_FAWN || p == CattlePhenotype.BROWN_WHITE;
            case BROWN_SWISS -> p == CattlePhenotype.BROWN_WHITE || p == CattlePhenotype.GREY_ROAN;
            case HIGHLAND -> p == CattlePhenotype.RED_WHITE || p == CattlePhenotype.BROWN_WHITE || p == CattlePhenotype.GREY_ROAN;
            case ABERDEEN_ANGUS -> p == CattlePhenotype.SOLID_BLACK;
            case HEREFORD -> p == CattlePhenotype.RED_WHITE || p == CattlePhenotype.BROWN_WHITE;
            case CHAROLAIS, SIMMENTAL, LIMOUSIN, BELGIAN_BLUE -> p == CattlePhenotype.BROWN_WHITE || p == CattlePhenotype.RED_WHITE || p == CattlePhenotype.SOLID_BLACK;
        };
    }
    public CompoundTag save() {
        CompoundTag t = new CompoundTag(); t.putInt("CoatA", coatA); t.putInt("CoatB", coatB);
        t.putInt("PatternA", patternA); t.putInt("PatternB", patternB); t.putInt("HornA", hornA); t.putInt("HornB", hornB); return t;
    }
    public static CattleGenome load(CompoundTag t) {
        return new CattleGenome(t.getInt("CoatA"), t.getInt("CoatB"), t.getInt("PatternA"), t.getInt("PatternB"), t.getInt("HornA"), t.getInt("HornB"));
    }
}
