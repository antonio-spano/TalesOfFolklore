package net.spanoprime.talesoffolklore;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.client.telemetry.events.WorldLoadTimesEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.datagen.DataGenerators;
import net.spanoprime.talesoffolklore.entity.ModBlockEntities;
import net.spanoprime.talesoffolklore.entity.ModEntities;
import net.spanoprime.talesoffolklore.entity.client.ModBoatRenderer;
import net.spanoprime.talesoffolklore.item.ModItems;
import net.spanoprime.talesoffolklore.item.custom.AppalachianMapItem;
import net.spanoprime.talesoffolklore.loot.ModLootTables;
import net.spanoprime.talesoffolklore.util.ModWoodTypes;
import net.spanoprime.talesoffolklore.worldgen.ModConfiguredFeatures;
import net.spanoprime.talesoffolklore.worldgen.ModPlacedFeatures;
import net.spanoprime.talesoffolklore.worldgen.biome.ModBiomes;
import net.spanoprime.talesoffolklore.worldgen.biome.surface.ModSurfaceRules;
import net.spanoprime.talesoffolklore.worldgen.decorators.ModTreeDecoratorTypes;
import net.spanoprime.talesoffolklore.worldgen.decorators.ModWallMossDecorator;
import net.spanoprime.talesoffolklore.worldgen.feature.ModFeatures;
import net.spanoprime.talesoffolklore.worldgen.injector.BiomeInjector;
import org.slf4j.Logger;

import java.awt.*;
import java.util.Optional;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TalesOfFolklore.MOD_ID)
public class TalesOfFolklore
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "talesoffolklore";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public TalesOfFolklore(FMLJavaModLoadingContext context)
    {

// Inserisci nell'overworld
        //BiomePlacement.replaceOverworld(Biomes.MUSHROOM_FIELDS, ModBiomes.APPALACHIAN_FOREST, .1);
        //BiomePlacement.replaceOverworld(Biomes.MUSHROOM_FIELDS, ModBiomes.APPALACHIAN_FOREST, .5);
        //BiomePlacement.replaceOverworld(Biomes.JUNGLE, ModBiomes.APPALACHIAN_FOREST, 1f);

        // Esempio: un matcher che accetta solo condizioni climatiche estreme (bioma rarissimo)


        IEventBus modEventBus = context.getModEventBus();

        modEventBus.register(DataGenerators.class);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(ModLootTables.class);
        MinecraftForge.EVENT_BUS.register(ModWallMossDecorator.class);
        //MinecraftForge.EVENT_BUS.register(DataGenerators.class);
        ModTreeDecoratorTypes.register(modEventBus);
        //ModTreeDecoratorTypes.TREE_DECORATORS.register(modEventBus);
        //ModTerrablender.registerBiomes();

        ModFeatures.register(modEventBus);
        // Dentro il costruttore di TalesOfFolklore
        MinecraftForge.EVENT_BUS.register(BiomeInjector.class);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            //((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(ModBlocks.VIRGINIA_PINE_SAPLING.getId(), ModBlocks.);
            //SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, MOD_ID, ModSurfaceRules.makeRules());
        });
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
        {
            event.accept(ModItems.APPALACHIAN_MAP_PIECE);
            event.accept(ModItems.SPIRITS_MAP_PIECE);
            event.accept(ModItems.APPALACHIAN_FOREST_MAP);
            event.accept(ModItems.SPIRITS_FOREST_MAP);
            event.accept(ModItems.TALES_OF_FOLKLORE);
            event.accept(ModItems.VIRGINIA_PINE_BOAT);
            event.accept(ModItems.VIRGINIA_PINE_CHEST_BOAT);
        }

        if(event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
        {
            event.accept(ModBlocks.VIRGINIA_PINE_PLANKS);
            event.accept(ModBlocks.VIRGINIA_PINE_STAIRS);
            event.accept(ModBlocks.VIRGINIA_PINE_DOOR);
            event.accept(ModBlocks.VIRGINIA_PINE_TRAPDOOR);
            event.accept(ModBlocks.VIRGINIA_PINE_SLAB);
            event.accept(ModBlocks.VIRGINIA_PINE_FENCE);
            event.accept(ModBlocks.VIRGINIA_PINE_FENCE_GATE);
            event.accept(ModBlocks.VIRGINIA_PINE_BUTTON);
            event.accept(ModBlocks.VIRGINIA_PINE_PRESSURE_PLATE);
            event.accept(ModBlocks.VIRGINIA_PINE_SIGN);
            event.accept(ModBlocks.VIRGINIA_PINE_HANGING_SIGN);
            event.accept(ModBlocks.VIRGINIA_PINE_LOG);
            event.accept(ModBlocks.VIRGINIA_PINE_WOOD);
            event.accept(ModBlocks.VIRGINIA_PINE_LEAVES);
            event.accept(ModBlocks.STRIPPED_VIRGINIA_PINE_WOOD);
            event.accept(ModBlocks.STRIPPED_VIRGINIA_PINE_LOG);
            event.accept(ModBlocks.VIRGINIA_PINE_SCAFFOLDING);
            event.accept(ModBlocks.APPALACHIAN_STONE);
            event.accept(ModBlocks.DAMP_DIRT);
            event.accept(ModBlocks.DAMP_GRASS_BLOCK);
            event.accept(ModBlocks.STREAMBED_ROCKS);
            event.accept(ModBlocks.MOSSY_STREAMBANK_ROCKS);
            event.accept(ModBlocks.DAMP_PODZOL);
            event.accept(ModBlocks.DAMP_COARSE_DIRT);
        }

        if(event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS)
        {
            event.accept(ModBlocks.VIRGINIA_PINE_LADDER);
        }

        if(event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS)
        {
            event.accept(ModBlocks.VIRGINIA_PINE_SAPLING);
            event.accept(ModBlocks.WALL_MOSS);
            event.accept(ModBlocks.WALL_IVY);
            event.accept(ModBlocks.PINE_NEEDLES);
            event.accept(ModBlocks.UNDERGROWTH);
            event.accept(ModBlocks.FERN);
            event.accept(ModBlocks.SPRITE);
            event.accept(ModBlocks.FIREFLIES_BUSH);
        }

        if(event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES)
        {
            event.accept(ModItems.VIRGINIA_PINECONE);
        }

        if(event.getTabKey() == CreativeModeTabs.INGREDIENTS)
        {
            event.accept(ModBlocks.YELLOW_FUNGUS);
            event.accept(ModBlocks.RED_FUNGUS);
            event.accept(ModBlocks.WALL_IVY_SEED);
            event.accept(ModBlocks.WHITE_FUNGUS);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        //Level level = event.getServer().getLevel(event.getServer().overworld().dimension());
        if (event.getEntity() instanceof ServerPlayer player)
        {
            ServerLevel serverLevel = player.serverLevel();

            Optional<Pair<BlockPos, Holder<Biome>>> closestBiome3d = Optional.ofNullable(serverLevel.findClosestBiome3d(
                    biomeHolder -> biomeHolder.is(ModBiomes.APPALACHIAN_FOREST),
                    player.blockPosition(),
                    6400,
                    32,
                    64
            ));

            closestBiome3d.ifPresent(pair -> {
                BlockPos biomePos = closestBiome3d.get().getFirst();
                AppalachianMapItem.coordX = biomePos.getX();
                AppalachianMapItem.coordZ = biomePos.getZ();
            });
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

            Sheets.addWoodType(ModWoodTypes.VIRGINIA_PINE);
            EntityRenderers.register(ModEntities.MOD_BOAT.get(), pContext -> new ModBoatRenderer(pContext, false));
            EntityRenderers.register(ModEntities.MOD_CHEST_BOAT.get(), pContext -> new ModBoatRenderer(pContext, true));

            event.enqueueWork(() -> {
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.VIRGINIA_PINE_DOOR.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.VIRGINIA_PINE_SAPLING.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.WALL_MOSS.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.YELLOW_FUNGUS.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.RED_FUNGUS.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.WALL_IVY_SEED.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.WALL_IVY.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.DAMP_GRASS_BLOCK.get(), RenderType.cutoutMipped());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.UNDERGROWTH.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.FERN.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.PINE_NEEDLES.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.WHITE_FUNGUS.get(), RenderType.cutout());
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.FIREFLIES_BUSH.get(), RenderType.cutout());
            });
        }
    }
}
