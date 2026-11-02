package com.example.realisticcattle.entity;

import net.minecraft.util.RandomSource;

/** Persistent individual temperament. Values are 0..100; generated once and inherited softly. */
public record CattlePersonality(int calmness, int sociability, int curiosity, int fearfulness,
                                int stubbornness, int maternalInstinct, int activity, int trust) {
    public static CattlePersonality random(RandomSource random) {
        return new CattlePersonality(roll(random), roll(random), roll(random), roll(random),
                roll(random), roll(random), roll(random), roll(random));
    }
    public static CattlePersonality inherit(CattlePersonality a, CattlePersonality b, RandomSource random) {
        return new CattlePersonality(mix(a.calmness,b.calmness,random), mix(a.sociability,b.sociability,random),
                mix(a.curiosity,b.curiosity,random), mix(a.fearfulness,b.fearfulness,random),
                mix(a.stubbornness,b.stubbornness,random), mix(a.maternalInstinct,b.maternalInstinct,random),
                mix(a.activity,b.activity,random), mix(a.trust,b.trust,random));
    }
    private static int roll(RandomSource r) { return 25 + r.nextInt(51); }
    private static int mix(int a, int b, RandomSource r) { return clamp((a+b)/2 + r.nextInt(21)-10); }
    private static int clamp(int v) { return Math.max(0, Math.min(100,v)); }
}
