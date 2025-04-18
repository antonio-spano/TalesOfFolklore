package net.spanoprime.talesoffolklore.item;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SignItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.block.ModBlocks;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TalesOfFolklore.MOD_ID);

    public static final RegistryObject<Item> APPALACHIAN_MAP_PIECE = ITEMS.register("appalachian_map_piece",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SPIRITS_MAP_PIECE = ITEMS.register("spirits_map_piece",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> APPALACHIAN_FOREST_MAP = ITEMS.register("appalachian_forest_map",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> SPIRITS_FOREST_MAP = ITEMS.register("spirits_forest_map",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TALES_OF_FOLKLORE_R = ITEMS.register("tales_of_folklore_r",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TALES_OF_FOLKLORE = ITEMS.register("tales_of_folklore",
            () -> new Item(new Item.Properties().stacksTo(1).craftRemainder(ModItems.TALES_OF_FOLKLORE_R.get())));

    //SIGNS
    public static final RegistryObject<Item> VIRGINIA_PINE_SIGN = ITEMS.register("virginia_pine_sign",
            () -> new SignItem(new Item.Properties().stacksTo(16),
                    ModBlocks.VIRGINIA_PINE_SIGN.get(), ModBlocks.VIRGINIA_PINE_WALL_SIGN.get()));

    public static final RegistryObject<Item> VIRGINIA_PINE_HANGING_SIGN = ITEMS.register("virginia_pine_hanging_sign",
            () -> new HangingSignItem(ModBlocks.VIRGINIA_PINE_HANGING_SIGN.get(), ModBlocks.VIRGINIA_PINE_WALL_HANGING_SIGN.get(),
                    new Item.Properties().stacksTo(16)));

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
