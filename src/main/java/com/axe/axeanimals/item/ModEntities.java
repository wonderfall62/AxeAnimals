package com.axe.axeanimals.registry;

import com.axe.axeanimals.AxeAnimals;
import com.axe.axeanimals.entity.FerretEntity;
import com.axe.axeanimals.entity.LionEntity;
import com.axe.axeanimals.entity.LionessEntity;
import com.axe.axeanimals.entity.StingrayEntity;
import com.axe.axeanimals.entity.WhaleEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, AxeAnimals.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<FerretEntity>> FERRET = ENTITIES.register("ferret",
            () -> EntityType.Builder.of(FerretEntity::new, MobCategory.CREATURE)
                    .sized(0.65F, 0.5F)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "ferret"))));

    public static final DeferredHolder<EntityType<?>, EntityType<WhaleEntity>> WHALE = ENTITIES.register("whale",
            () -> EntityType.Builder.of(WhaleEntity::new, MobCategory.WATER_CREATURE)
                    .sized(5.6F, 2.8F)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "whale"))));

    public static final DeferredHolder<EntityType<?>, EntityType<StingrayEntity>> STINGRAY = ENTITIES.register("stingray",
            () -> EntityType.Builder.of(StingrayEntity::new, MobCategory.WATER_AMBIENT)
                    .sized(1.6F, 0.35F)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "stingray"))));

    public static final DeferredHolder<EntityType<?>, EntityType<LionEntity>> LION = ENTITIES.register("lion",
            () -> EntityType.Builder.of(LionEntity::new, MobCategory.CREATURE)
                    .sized(1.45F, 1.55F)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "lion"))));

    public static final DeferredHolder<EntityType<?>, EntityType<LionessEntity>> LIONESS = ENTITIES.register("lioness",
            () -> EntityType.Builder.of(LionessEntity::new, MobCategory.CREATURE)
                    .sized(1.35F, 1.45F)
                    .build(ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "lioness"))));

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(FERRET.get(), FerretEntity.createAttributes().build());
        event.put(WHALE.get(), WhaleEntity.createAttributes().build());
        event.put(STINGRAY.get(), StingrayEntity.createAttributes().build());
        event.put(LION.get(), LionEntity.createAttributes().build());
        event.put(LIONESS.get(), LionessEntity.createAttributes().build());
    }

    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(FERRET.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(WHALE.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WhaleEntity::checkWhaleSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(STINGRAY.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(LION.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
        event.register(LIONESS.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules, RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }
}
