package com.example.realisticcattle.client;

import com.example.realisticcattle.RealisticCattle;
import com.example.realisticcattle.client.renderer.CattleGeoRenderer;
import com.example.realisticcattle.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RealisticCattle.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {}
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.CATTLE.get(), CattleGeoRenderer::new);
    }
}
