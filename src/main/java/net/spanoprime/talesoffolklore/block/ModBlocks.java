package net.spanoprime.talesoffolklore.block;

import net.minecraft.client.resources.model.Material;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TalesOfFolklore.MOD_ID);

    public static final RegistryObject<Block> VIRGINIA_PINE_PLANKS = registerBlock("virginia_pine_planks",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));

    public static final RegistryObject<Block> VIRGINIA_PINE_STAIRS = registerBlock("virginia_pine_stairs",
            () -> new StairBlock(() -> ModBlocks.VIRGINIA_PINE_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.copy(Blocks.OAK_STAIRS)));

    public static final RegistryObject<Block> VIRGINIA_PINE_DOOR = registerBlock("virginia_pine_door",
            () -> new DoorBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_DOOR),
                    BlockSetType.ACACIA));

    public static final RegistryObject<Block> VIRGINIA_PINE_TRAPDOOR = registerBlock("virginia_pine_trapdoor",
            () -> new TrapDoorBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_TRAPDOOR),
                    BlockSetType.ACACIA));

    public static final RegistryObject<Block> VIRGINIA_PINE_SLAB = registerBlock("virginia_pine_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_SLAB)));

    public static final RegistryObject<Block> VIRGINIA_PINE_FENCE = registerBlock("virginia_pine_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_FENCE)));

    public static final RegistryObject<Block> VIRGINIA_PINE_FENCE_GATE = registerBlock("virginia_pine_fence_gate",
            () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_FENCE_GATE), WoodType.ACACIA));

    public static final RegistryObject<Block> VIRGINIA_PINE_BUTTON = registerBlock("virginia_pine_button",
            () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_BUTTON), BlockSetType.ACACIA,
                    30, true));

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block)
    {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);

        return toReturn;
    }

    private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block)
    {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }
}