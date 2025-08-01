package net.spanoprime.talesoffolklore.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.block.custom.*;
import net.spanoprime.talesoffolklore.item.ModItems;
import net.spanoprime.talesoffolklore.item.custom.FuelBlockItem;
import net.spanoprime.talesoffolklore.util.ModWoodTypes;
import net.spanoprime.talesoffolklore.worldgen.tree.VirginiaPineTreeGrower;

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
/*
    public static final RegistryObject<Block> VIRGINIA_PINE_LEAVES = registerBlock("virginia_pine_leaves",
            () -> new ModLeavesBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_LEAVES).randomTicks())); */

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

    //CUSTOM LADDER
    public static final RegistryObject<Block> VIRGINIA_PINE_LADDER = registerBlock("virginia_pine_ladder",
            () -> new ModLadderBlock(BlockBehaviour.Properties.copy(Blocks.LADDER))); //metti LADDER

    //SAPLING TODO: (ADD TO POT)
    public static final RegistryObject<Block> VIRGINIA_PINE_SAPLING = registerBlock("virginia_pine_sapling",
            () -> new SaplingBlock(new VirginiaPineTreeGrower(), BlockBehaviour.Properties.copy(Blocks.ACACIA_SAPLING)));

    public static final RegistryObject<Block> VIRGINIA_PINE_SCAFFOLDING = registerBlock("virginia_pine_scaffolding",
            () -> new GlassBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> WALL_MOSS = registerBlock("wall_moss",
            () -> new ModWallMossBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.MOSS_CARPET)));

    public static final RegistryObject<Block> WALL_IVY_SEED = registerBlock("wall_ivy_seed",
            () -> new ModWallIvySeed(BlockBehaviour.Properties.copy(Blocks.VINE).randomTicks().instabreak()));

    public static final RegistryObject<Block> WALL_IVY = registerBlock("wall_ivy",
            () -> new ModWallIvy(BlockBehaviour.Properties.copy(Blocks.VINE).randomTicks().instabreak()));

    public static final RegistryObject<Block> YELLOW_FUNGUS = registerBlock("yellow_fungus",
            () -> new ModYellowFungusBlock(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.MOSS_CARPET)));

    public static final RegistryObject<Block> WHITE_FUNGUS = registerBlock("white_fungus",
            () -> new ModYellowFungusBlock(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.MOSS_CARPET)));

    public static final RegistryObject<Block> RED_FUNGUS = registerBlock("red_fungus",
            () -> new ModRedFungusBlock(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.MOSS_CARPET)));

    public static final RegistryObject<Block> APPALACHIAN_STONE = registerBlock("appalachian_stone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .sound(SoundType.STONE)));

    public static final RegistryObject<Block> DAMP_GRASS_BLOCK = registerBlock("damp_grass_block",
            () -> new ModAppalachianGrassBlock(BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK)
                    .sound(SoundType.GRASS)));

    public static final RegistryObject<Block> DAMP_DIRT = registerBlock("damp_dirt",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIRT)
                    .sound(SoundType.GRASS)));

    public static final RegistryObject<Block> STREAMBED_ROCKS = registerBlock("streambed_rocks",
            () -> new GravelBlock(BlockBehaviour.Properties.copy(Blocks.GRAVEL)
                    .sound(SoundType.GRAVEL)));

    public static final RegistryObject<Block> MOSSY_STREAMBANK_ROCKS = registerBlock("mossy_streambank_rocks",
            () -> new ModMossyStreambankRocksBlock(BlockBehaviour.Properties.copy(Blocks.MOSSY_COBBLESTONE)
                    .sound(SoundType.STONE)));

    public static final RegistryObject<Block> PINE_NEEDLES = registerBlock("pine_needles",
            () -> new ModPineNeedlesBlock(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)
                    .sound(SoundType.SWEET_BERRY_BUSH)
                    .instabreak()));
    
    public static final RegistryObject<Block> UNDERGROWTH = registerBlock("undergrowth",
            () -> new ModUndergrowthBlock(BlockBehaviour.Properties.copy(Blocks.SWEET_BERRY_BUSH)
                    .sound(SoundType.SWEET_BERRY_BUSH)
                    .instabreak()));

    public static final RegistryObject<Block> DAMP_PODZOL = registerBlock("damp_podzol",
            () -> new GrassBlock(BlockBehaviour.Properties.copy(Blocks.PODZOL)));

    public static final RegistryObject<Block> DAMP_COARSE_DIRT = registerBlock("damp_coarse_dirt",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.COARSE_DIRT)));

    public static final RegistryObject<Block> FERN = registerBlock("fern",
            () -> new ModFernBlock(BlockBehaviour.Properties.copy(Blocks.FERN)
                    .instabreak()));

    public static final RegistryObject<Block> FIREFLIES_BUSH = registerBlock("fireflies_bush",
            () -> new ModFirefliesBushBlock(
                    BlockBehaviour.Properties
                            .copy(Blocks.SWEET_BERRY_BUSH)
                            .instabreak()
                            .randomTicks()
                            // ← Qui aggiungi la lightLevel:
                            .lightLevel(state -> state.getValue(ModFirefliesBushBlock.VARIANT) == 1 ? 7 : 0)
            )
    );

    public static final RegistryObject<Block> CATTAIL = registerBlock("cattail",
            () -> new SeagrassBlock(BlockBehaviour.Properties.copy(Blocks.TALL_SEAGRASS)
                    .instabreak()));

    public static final RegistryObject<Block> SPRITE = registerBlock("sprite",
            () -> new ModSpriteBlock(BlockBehaviour.Properties.copy(Blocks.FERN)));

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