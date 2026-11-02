package com.example.realisticcattle.event;

import com.example.realisticcattle.RealisticCattle;
import com.example.realisticcattle.entity.CattleEntity;
import com.example.realisticcattle.registry.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RealisticCattle.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModCommonEvents {
    private ModCommonEvents() {}

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.CATTLE.get(), CattleEntity.createAttributes().build());
    }
}
