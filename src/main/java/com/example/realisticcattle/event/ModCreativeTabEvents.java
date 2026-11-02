package com.example.realisticcattle.event;

import com.example.realisticcattle.RealisticCattle;
import com.example.realisticcattle.registry.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RealisticCattle.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModCreativeTabEvents {
    private ModCreativeTabEvents() {}
    @SubscribeEvent
    public static void addItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.HOLSTEIN_SPAWN_EGG);
            event.accept(ModItems.JERSEY_SPAWN_EGG);
            event.accept(ModItems.BROWN_SWISS_SPAWN_EGG);
            event.accept(ModItems.HIGHLAND_SPAWN_EGG);
            event.accept(ModItems.ABERDEEN_ANGUS_SPAWN_EGG);
            event.accept(ModItems.HEREFORD_SPAWN_EGG);
            event.accept(ModItems.CHAROLAIS_SPAWN_EGG);
            event.accept(ModItems.SIMMENTAL_SPAWN_EGG);
            event.accept(ModItems.LIMOUSIN_SPAWN_EGG);
            event.accept(ModItems.BELGIAN_BLUE_SPAWN_EGG);
        }
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ModItems.MINERAL_SUPPLEMENT);
            event.accept(ModItems.DAIRY_FEED);
            event.accept(ModItems.CALF_MILK_BOTTLE);
        }
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.HUSBANDRY_GUIDE);
            event.accept(ModItems.PEDIGREE_BOOK);
        }
    }
}
