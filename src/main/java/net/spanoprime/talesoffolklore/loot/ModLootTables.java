package net.spanoprime.talesoffolklore.loot;

import com.mojang.serialization.Lifecycle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftStructure;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.spanoprime.talesoffolklore.item.ModItems;

import java.lang.reflect.Array;
import java.util.*;

public class ModLootTables {

    // Usa un Set per un accesso più rapido ai nomi delle loot table
    private static final Set<ResourceLocation> LOOT_TABLES = new HashSet<>();
    static {
        LOOT_TABLES.add(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/abandoned_mineshaft"));
        LOOT_TABLES.add(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/buried_treasure"));
        LOOT_TABLES.add(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/stronghold_library"));
        LOOT_TABLES.add(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/igloo_chest"));
        LOOT_TABLES.add(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/pillager_outpost"));
        LOOT_TABLES.add(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/ruined_portal"));
        LOOT_TABLES.add(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/simple_dungeon"));
        LOOT_TABLES.add(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/shipwreck_supply"));
        LOOT_TABLES.add(ResourceLocation.fromNamespaceAndPath("minecraft", "chests/shipwreck_treasure"));
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        // Controlla se il nome della loot table è presente nel Set
        if (LOOT_TABLES.contains(event.getName())) {
            Random random = new Random();
            int rolls = random.nextInt(4);  // Numero casuale da 0 a 3

            event.getTable().addPool(
                    LootPool.lootPool()
                            .setRolls(ConstantValue.exactly(rolls))
                            .add(LootItem.lootTableItem(ModItems.APPALACHIAN_MAP_PIECE.get()).setWeight(50))  // Peso aumentato
                            .add(LootItem.lootTableItem(ModItems.SPIRITS_MAP_PIECE.get()).setWeight(50))  // Peso aumentato
                            .build()
            );
        }
    }
}
