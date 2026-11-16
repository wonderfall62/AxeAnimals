package com.axe.axeanimals.registry;

import com.axe.axeanimals.AxeAnimals;
import com.axe.axeanimals.item.MarineNetItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, AxeAnimals.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AxeAnimals.MODID);

    public static final DeferredHolder<Item, MarineNetItem> MARINE_NET = ITEMS.register("marine_net",
            () -> new MarineNetItem(new Item.Properties().stacksTo(1)
                    .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "marine_net")))));

    public static final DeferredHolder<Item, SpawnEggItem> FERRET_SPAWN_EGG = ITEMS.register("ferret_spawn_egg",
            () -> new SpawnEggItem(new Item.Properties().spawnEgg(ModEntities.FERRET.get())
                    .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "ferret_spawn_egg")))));

    public static final DeferredHolder<Item, SpawnEggItem> WHALE_SPAWN_EGG = ITEMS.register("whale_spawn_egg",
            () -> new SpawnEggItem(new Item.Properties().spawnEgg(ModEntities.WHALE.get())
                    .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "whale_spawn_egg")))));

    public static final DeferredHolder<Item, SpawnEggItem> STINGRAY_SPAWN_EGG = ITEMS.register("stingray_spawn_egg",
            () -> new SpawnEggItem(new Item.Properties().spawnEgg(ModEntities.STINGRAY.get())
                    .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "stingray_spawn_egg")))));

    public static final DeferredHolder<Item, SpawnEggItem> LION_SPAWN_EGG = ITEMS.register("lion_spawn_egg",
            () -> new SpawnEggItem(new Item.Properties().spawnEgg(ModEntities.LION.get())
                    .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "lion_spawn_egg")))));

    public static final DeferredHolder<Item, SpawnEggItem> LIONESS_SPAWN_EGG = ITEMS.register("lioness_spawn_egg",
            () -> new SpawnEggItem(new Item.Properties().spawnEgg(ModEntities.LIONESS.get())
                    .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(AxeAnimals.MODID, "lioness_spawn_egg")))));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AXE_ANIMALS_TAB = CREATIVE_TABS.register("axeanimals_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.axeanimals"))
                    .icon(() -> new ItemStack(MARINE_NET.get()))
                    .displayItems((params, output) -> {
                        output.accept(MARINE_NET.get());
                        output.accept(FERRET_SPAWN_EGG.get());
                        output.accept(WHALE_SPAWN_EGG.get());
                        output.accept(STINGRAY_SPAWN_EGG.get());
                        output.accept(LION_SPAWN_EGG.get());
                        output.accept(LIONESS_SPAWN_EGG.get());
                    }).build());
}
