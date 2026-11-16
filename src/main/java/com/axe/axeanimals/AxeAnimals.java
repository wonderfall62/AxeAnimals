package com.axe.axeanimals;

import com.axe.axeanimals.compat.ModCompatibilityEvents;
import com.axe.axeanimals.registry.ModEntities;
import com.axe.axeanimals.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(AxeAnimals.MODID)
public class AxeAnimals {
    public static final String MODID = "axeanimals";

    public AxeAnimals(IEventBus modEventBus) {
        ModEntities.ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModItems.CREATIVE_TABS.register(modEventBus);

        modEventBus.addListener(ModEntities::registerAttributes);
        modEventBus.addListener(ModEntities::registerSpawnPlacements);

        NeoForge.EVENT_BUS.register(ModCompatibilityEvents.class);
    }
}
