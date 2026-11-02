package com.example.realisticcattle.registry;

import com.example.realisticcattle.RealisticCattle;
import com.example.realisticcattle.entity.CattleEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RealisticCattle.MOD_ID);

    public static final RegistryObject<EntityType<CattleEntity>> CATTLE = ENTITY_TYPES.register("cattle",
            () -> EntityType.Builder.of(CattleEntity::new, MobCategory.CREATURE)
                    .sized(1.35F, 1.55F)
                    .clientTrackingRange(10)
                    .build("realisticcattle:cattle"));

    private ModEntities() {}
}
