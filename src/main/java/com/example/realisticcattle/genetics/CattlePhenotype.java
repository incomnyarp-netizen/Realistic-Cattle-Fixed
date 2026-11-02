package com.example.realisticcattle.genetics;

import java.util.Random;

/** Visible coat/horn traits. This is the expressed phenotype; the parents' values are inherited at birth. */
public enum CattlePhenotype {
    HOLSTEIN_BLACK_WHITE("Чёрно-пёстрая", "holstein", true),
    RED_WHITE("Красно-пёстрая", "red_white", true),
    JERSEY_FAWN("Палевая", "jersey_fawn", false),
    SOLID_BLACK("Чёрная", "solid_black", false),
    GREY_ROAN("Серо-чалая", "grey_roan", true),
    BROWN_WHITE("Коричнево-белая", "brown_white", true);

    private final String displayName;
    private final String textureKey;
    private final boolean patterned;
    CattlePhenotype(String displayName, String textureKey, boolean patterned) {
        this.displayName = displayName; this.textureKey = textureKey; this.patterned = patterned;
    }
    public String displayName() { return displayName; }
    public String textureKey() { return textureKey; }
    public boolean patterned() { return patterned; }
    public static CattlePhenotype byId(int id) {
        CattlePhenotype[] all = values(); return all[Math.max(0, Math.min(id, all.length - 1))];
    }
    public static CattlePhenotype randomFor(CattleBreed breed, Random random) {
        if (breed == CattleBreed.JERSEY) return random.nextFloat() < .82f ? JERSEY_FAWN : BROWN_WHITE;
        if (breed == CattleBreed.HIGHLAND) return random.nextBoolean() ? BROWN_WHITE : RED_WHITE;
        if (breed == CattleBreed.ABERDEEN_ANGUS) return random.nextFloat() < .9f ? SOLID_BLACK : RED_WHITE;
        if (breed == CattleBreed.HEREFORD) return random.nextFloat() < .8f ? RED_WHITE : BROWN_WHITE;
        if (breed == CattleBreed.BROWN_SWISS) return random.nextFloat() < .75f ? BROWN_WHITE : GREY_ROAN;
        return random.nextFloat() < .78f ? HOLSTEIN_BLACK_WHITE : (random.nextBoolean() ? RED_WHITE : GREY_ROAN);
    }
    /** Simplified Mendelian inheritance: one expressed parental coat is passed on, with rare variation. */
    public static CattlePhenotype inherit(CattlePhenotype mother, CattlePhenotype father, CattleBreed breed, Random random) {
        if (random.nextFloat() < .06f) return randomFor(breed, random);
        return random.nextBoolean() ? mother : father;
    }
}
