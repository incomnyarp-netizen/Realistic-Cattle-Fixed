package com.example.realisticcattle.genetics;

/** Breed identity and gameplay profile. Values are design baselines, not real livestock statistics. */
public enum CattleBreed {
    HOLSTEIN(1.08F, 1.00F, 0.90F, 0.75F, true),
    JERSEY(0.82F, 0.88F, 1.08F, 0.85F, true),
    BROWN_SWISS(1.00F, 1.00F, 0.98F, 0.90F, true),
    HIGHLAND(0.94F, 1.08F, 0.72F, 1.35F, false),
    ABERDEEN_ANGUS(1.00F, 1.12F, 0.70F, 0.78F, false),
    HEREFORD(1.00F, 1.08F, 0.72F, 0.82F, false),
    CHAROLAIS(1.16F, 1.24F, 0.68F, 0.78F, false),
    SIMMENTAL(1.08F, 1.12F, 0.82F, 0.85F, true),
    LIMOUSIN(1.02F, 1.18F, 0.70F, 0.76F, false),
    BELGIAN_BLUE(1.12F, 1.32F, 0.62F, 0.72F, false);

    private final float bodyScale;
    private final float muscle;
    private final float milkPotential;
    private final float coldTolerance;
    private final boolean commonlyHorned;

    CattleBreed(float bodyScale, float muscle, float milkPotential, float coldTolerance, boolean commonlyHorned) {
        this.bodyScale = bodyScale;
        this.muscle = muscle;
        this.milkPotential = milkPotential;
        this.coldTolerance = coldTolerance;
        this.commonlyHorned = commonlyHorned;
    }

    public float bodyScale() { return bodyScale; }
    public float muscle() { return muscle; }
    public float milkPotential() { return milkPotential; }
    public float coldTolerance() { return coldTolerance; }
    public boolean commonlyHorned() { return commonlyHorned; }
}
