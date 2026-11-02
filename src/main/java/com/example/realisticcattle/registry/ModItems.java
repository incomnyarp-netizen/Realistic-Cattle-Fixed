package com.example.realisticcattle.registry;

import com.example.realisticcattle.RealisticCattle;
import com.example.realisticcattle.genetics.CattleBreed;
import com.example.realisticcattle.item.CattleBreedSpawnEggItem;
import com.example.realisticcattle.item.PedigreeBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RealisticCattle.MOD_ID);

    public static final RegistryObject<Item> HUSBANDRY_GUIDE = ITEMS.register("husbandry_guide",
            () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PEDIGREE_BOOK = ITEMS.register("pedigree_book",
            () -> new PedigreeBookItem(new Item.Properties().stacksTo(1)));
    // Kept registered for worlds created with the earlier generic egg ID.
    public static final RegistryObject<Item> CATTLE_SPAWN_EGG = ITEMS.register("cattle_spawn_egg",
            () -> new CattleBreedSpawnEggItem(ModEntities.CATTLE.get(), 0x171714, 0xE8E3D6, new Item.Properties(), CattleBreed.HOLSTEIN));

    // One egg per registered breed. Egg colors are deliberately distinct in the vanilla spawn-egg style.
    public static final RegistryObject<Item> HOLSTEIN_SPAWN_EGG = egg("holstein_spawn_egg", 0x282724, 0xF1EEE5, CattleBreed.HOLSTEIN);
    public static final RegistryObject<Item> JERSEY_SPAWN_EGG = egg("jersey_spawn_egg", 0xA86B3C, 0xE9C18A, CattleBreed.JERSEY);
    public static final RegistryObject<Item> BROWN_SWISS_SPAWN_EGG = egg("brown_swiss_spawn_egg", 0x67452F, 0xB58A62, CattleBreed.BROWN_SWISS);
    public static final RegistryObject<Item> HIGHLAND_SPAWN_EGG = egg("highland_spawn_egg", 0xA34E2D, 0xD49A56, CattleBreed.HIGHLAND);
    public static final RegistryObject<Item> ABERDEEN_ANGUS_SPAWN_EGG = egg("aberdeen_angus_spawn_egg", 0x171615, 0x51433A, CattleBreed.ABERDEEN_ANGUS);
    public static final RegistryObject<Item> HEREFORD_SPAWN_EGG = egg("hereford_spawn_egg", 0xA94A32, 0xF0E5CE, CattleBreed.HEREFORD);
    public static final RegistryObject<Item> CHAROLAIS_SPAWN_EGG = egg("charolais_spawn_egg", 0xD9C8A6, 0xF3EBDD, CattleBreed.CHAROLAIS);
    public static final RegistryObject<Item> SIMMENTAL_SPAWN_EGG = egg("simmental_spawn_egg", 0xB66A42, 0xE9D8B8, CattleBreed.SIMMENTAL);
    public static final RegistryObject<Item> LIMOUSIN_SPAWN_EGG = egg("limousin_spawn_egg", 0x8B3329, 0xC47B58, CattleBreed.LIMOUSIN);
    public static final RegistryObject<Item> BELGIAN_BLUE_SPAWN_EGG = egg("belgian_blue_spawn_egg", 0x8C9DAA, 0xDCE3E7, CattleBreed.BELGIAN_BLUE);

    public static final RegistryObject<Item> MINERAL_SUPPLEMENT = ITEMS.register("mineral_supplement",
            () -> new Item(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> DAIRY_FEED = ITEMS.register("dairy_feed",
            () -> new Item(new Item.Properties().stacksTo(32)));
    public static final RegistryObject<Item> CALF_MILK_BOTTLE = ITEMS.register("calf_milk_bottle",
            () -> new Item(new Item.Properties().stacksTo(16)));

    private static RegistryObject<Item> egg(String id, int background, int spots, CattleBreed breed) {
        return ITEMS.register(id, () -> new CattleBreedSpawnEggItem(
                ModEntities.CATTLE.get(), background, spots, new Item.Properties(), breed));
    }

    private ModItems() {}
}
