package net.spanoprime.talesoffolklore.block;

import net.minecraft.client.resources.model.Material;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.block.custom.*;
import net.spanoprime.talesoffolklore.item.ModItems;
import net.spanoprime.talesoffolklore.item.custom.FuelBlockItem;
import net.spanoprime.talesoffolklore.util.ModWoodTypes;

import java.util.function.Supplier;

public class ModBlocks {
    public static final String virginia_pine_type = "virginia_pine";
    public static final String silver_pine_type = "silver_pine";

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TalesOfFolklore.MOD_ID);

    public static final RegistryObject<Block> VIRGINIA_PINE_PLANKS = registerBlock("virginia_pine_planks",
            () -> new ModPlanksBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS)));

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
            () -> new FenceGateBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_FENCE_GATE),
                    WoodType.ACACIA));

    public static final RegistryObject<Block> VIRGINIA_PINE_BUTTON = registerBlock("virginia_pine_button",
            () -> new ButtonBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_BUTTON),
                    BlockSetType.ACACIA, 30, true));

    public static final RegistryObject<Block> VIRGINIA_PINE_PRESSURE_PLATE = registerBlock("virginia_pine_pressure_plate",
            () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING,
                    BlockBehaviour.Properties.copy(Blocks.ACACIA_PRESSURE_PLATE),
                    BlockSetType.ACACIA));

    public static final RegistryObject<Block> VIRGINIA_PINE_SIGN = BLOCKS.register("virginia_pine_sign",
            () -> new ModStandingSignBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_SIGN), ModWoodTypes.VIRGINIA_PINE));

    public static final RegistryObject<Block> VIRGINIA_PINE_WALL_SIGN = BLOCKS.register("virginia_pine_wall_sign",
            () -> new ModWallSignBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_WALL_SIGN), ModWoodTypes.VIRGINIA_PINE));

    public static final RegistryObject<Block> VIRGINIA_PINE_HANGING_SIGN = BLOCKS.register("virginia_pine_hanging_sign",
            () -> new ModHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_HANGING_SIGN), ModWoodTypes.VIRGINIA_PINE));

    public static final RegistryObject<Block> VIRGINIA_PINE_WALL_HANGING_SIGN = BLOCKS.register("virginia_pine_wall_hanging_sign",
            () -> new ModWallHangingSignBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_WALL_HANGING_SIGN), ModWoodTypes.VIRGINIA_PINE));

    public static final RegistryObject<Block> VIRGINIA_PINE_LEAVES = registerBlock("virginia_pine_leaves",
            () -> new ModLeavesBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_LEAVES)));

    public static final RegistryObject<Block> VIRGINIA_PINE_LOG = registerBlock("virginia_pine_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_LOG)));

    public static final RegistryObject<Block> VIRGINIA_PINE_WOOD = registerBlock("virginia_pine_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_WOOD)));

    public static final RegistryObject<Block> STRIPPED_VIRGINIA_PINE_WOOD = registerBlock("stripped_virginia_pine_wood",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_ACACIA_WOOD)));

    public static final RegistryObject<Block> STRIPPED_VIRGINIA_PINE_LOG = registerBlock("stripped_virginia_pine_log",
            () -> new ModFlammableRotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.STRIPPED_ACACIA_LOG)));

    //CUSTOM BLOCK
    public static final RegistryObject<Block> PIOLI_LADDER = registerBlock("pioli_ladder",
            () -> new ModLadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER))); //metti LADDER

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block)
    {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);

        return toReturn;
    }

    private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block)
    {
        if (name.contains(virginia_pine_type) && !name.contains("leaves"))
        {
            return ModItems.ITEMS.register(name, () -> new FuelBlockItem(block.get(), new Item.Properties(), 300));
        }
        else
        {
            return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        }
    }

    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }
}