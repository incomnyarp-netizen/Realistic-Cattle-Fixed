package com.example.realisticcattle.client.model;

import com.example.realisticcattle.RealisticCattle;
import com.example.realisticcattle.entity.CattleEntity;
import com.example.realisticcattle.entity.CattleSex;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.core.animation.AnimationState;

public class CattleGeoModel extends GeoModel<CattleEntity> {
    private static ResourceLocation id(String path) { return new ResourceLocation(RealisticCattle.MOD_ID, path); }

    @Override public ResourceLocation getModelResource(CattleEntity entity) {
        if (entity.isBaby()) return id("geo/holstein_calf.geo.json");
        return entity.getSex() == CattleSex.MALE ? id("geo/holstein_male.geo.json") : id("geo/holstein_female.geo.json");
    }
    @Override public ResourceLocation getTextureResource(CattleEntity entity) {
        String stage = entity.isBaby() ? "calf" : (entity.getSex() == CattleSex.MALE ? "male" : "female");
        String phenotype = entity.getPhenotype().textureKey();
        if ("holstein".equals(phenotype)) return id("textures/entity/holstein_" + stage + ".png");
        return id("textures/entity/" + phenotype + "_" + stage + ".png");
    }
    /** Applies inherited conformation traits to the actual GeckoLib rig. Values are clamped
     * to subtle Minecraft-scale proportions so extreme alleles do not break the silhouette. */
    @Override
    public void setCustomAnimations(CattleEntity entity, long instanceId, AnimationState<CattleEntity> animationState) {
        super.setCustomAnimations(entity, instanceId, animationState);
        var traits = entity.getTraits();
        float body = 0.88F + traits.bodySize() / 100.0F * 0.24F;
        float chest = 0.90F + traits.chestWidth() / 100.0F * 0.22F;
        float legs = 0.92F + traits.legLength() / 100.0F * 0.16F;
        float muscle = 0.94F + traits.muscling() / 100.0F * 0.12F;
        float horns = 0.70F + traits.hornLength() / 100.0F * 0.60F;

        getBone("body").ifPresent(b -> { b.setScaleX(body); b.setScaleY(body); b.setScaleZ(body); });
        getBone("chest").ifPresent(b -> { b.setScaleX(chest * muscle); b.setScaleY(muscle); });
        for (String leg : new String[]{"front_left_upper", "front_right_upper", "back_left_upper", "back_right_upper"})
            getBone(leg).ifPresent(b -> b.setScaleY(legs));
        for (String horn : new String[]{"left_horn_mid", "left_horn_tip", "right_horn_mid", "right_horn_tip"})
            getBone(horn).ifPresent(b -> { b.setScaleX(horns); b.setScaleY(horns); b.setScaleZ(horns); });
    }

    @Override public ResourceLocation getAnimationResource(CattleEntity entity) { return id("animations/cattle.animation.json"); }
}
