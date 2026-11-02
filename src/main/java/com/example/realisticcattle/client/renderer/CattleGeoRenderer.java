package com.example.realisticcattle.client.renderer;

import com.example.realisticcattle.client.model.CattleGeoModel;
import com.example.realisticcattle.entity.CattleEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CattleGeoRenderer extends GeoEntityRenderer<CattleEntity> {
    public CattleGeoRenderer(EntityRendererProvider.Context context) {
        super(context, new CattleGeoModel());
        this.shadowRadius = 0.65F;
    }
}
