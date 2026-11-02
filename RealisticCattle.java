package com.example.realisticcattle;

import com.example.realisticcattle.registry.ModEntities;
import com.example.realisticcattle.registry.ModItems;
import com.example.realisticcattle.network.CattleNetwork;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib.GeckoLib;

@Mod(RealisticCattle.MOD_ID)
public class RealisticCattle {
    public static final String MOD_ID = "realisticcattle";

    public RealisticCattle() {
        GeckoLib.initialize();
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITY_TYPES.register(modBus);
        ModItems.ITEMS.register(modBus);
        CattleNetwork.register();
    }
}
