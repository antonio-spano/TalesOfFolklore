package net.spanoprime.talesoffolklore.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.spanoprime.talesoffolklore.TalesOfFolklore;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TalesOfFolklore.MOD_ID);

    public static final RegistryObject<Item> APPALACHIAN_MAP_PIECE = ITEMS.register("appalachian_map_piece",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SPIRITS_MAP_PIECE = ITEMS.register("spirits_map_piece",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> APPALACHIAN_FOREST_MAP = ITEMS.register("appalachian_forest_map",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SPIRITS_FOREST_MAP = ITEMS.register("spirits_forest_map",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TALES_OF_FOLKLORE = ITEMS.register("tales_of_folklore",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
