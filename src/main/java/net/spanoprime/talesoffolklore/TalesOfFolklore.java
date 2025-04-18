package net.spanoprime.talesoffolklore;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.entity.ModBlockEntities;
import net.spanoprime.talesoffolklore.entity.ModEntities;
import net.spanoprime.talesoffolklore.entity.client.ModBoatRenderer;
import net.spanoprime.talesoffolklore.item.ModItems;
import net.spanoprime.talesoffolklore.util.ModWoodTypes;
import org.slf4j.Logger;

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
        IEventBus modEventBus = context.getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);

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
            //event.accept(ModBlocks.VIRGINIA_PINE_BOAT);
            event.accept(ModBlocks.VIRGINIA_PINE_SIGN);
            event.accept(ModBlocks.VIRGINIA_PINE_HANGING_SIGN);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            EntityRenderers.register(ModEntities.MOD_BOAT.get(), pContext -> new ModBoatRenderer(pContext, false));
            EntityRenderers.register(ModEntities.MOD_CHEST_BOAT.get(), pContext -> new ModBoatRenderer(pContext, true));
            Sheets.addWoodType(ModWoodTypes.VIRGINIA_PINE);
            event.enqueueWork(() -> {
                ItemBlockRenderTypes.setRenderLayer(ModBlocks.VIRGINIA_PINE_DOOR.get(), RenderType.cutout());
            });
        }
    }
}
