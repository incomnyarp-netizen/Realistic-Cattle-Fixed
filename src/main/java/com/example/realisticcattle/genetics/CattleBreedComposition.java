package com.example.realisticcattle.genetics;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Expected ancestry profiles. Composite breeds can be registered with their official founding-breed proportions. */
public final class CattleBreedComposition {
    private static final Map<String, Map<String, Double>> PROFILES = new ConcurrentHashMap<>();
    static {
        for (CattleBreed breed : CattleBreed.values()) PROFILES.put(breed.name(), Map.of(breed.name(), 100.0));
    }
    private CattleBreedComposition() { }
    public static void registerComposite(CattleBreed breed, Map<CattleBreed, Double> founders) {
        if (breed == null || founders == null || founders.isEmpty()) throw new IllegalArgumentException("Invalid composite breed profile");
        double total = founders.values().stream().mapToDouble(v -> Math.max(0, v)).sum();
        if (total <= 0) throw new IllegalArgumentException("Founder proportions must be positive");
        Map<String, Double> normalized = new LinkedHashMap<>();
        founders.forEach((founderBreed, share) -> { if (share > 0) normalized.put(founderBreed.name(), share * 100.0 / total); });
        PROFILES.put(breed.name(), Map.copyOf(normalized));
    }
    public static boolean matches(String breedId, Map<String, Double> actual, double tolerance) {
        Map<String, Double> expected = PROFILES.get(breedId);
        if (expected == null) return false;
        for (Map.Entry<String, Double> e : expected.entrySet()) if (Math.abs(actual.getOrDefault(e.getKey(), 0.0) - e.getValue()) > tolerance) return false;
        for (Map.Entry<String, Double> e : actual.entrySet()) if (!expected.containsKey(e.getKey()) && e.getValue() > tolerance) return false;
        return true;
    }
}
