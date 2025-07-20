package net.spanoprime.talesoffolklore.handlers;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.level.GrassColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.block.ModBlocks;

@Mod.EventBusSubscriber(modid = TalesOfFolklore.MOD_ID, value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ColorHandlers {
    // blocco piazzato nel mondo
    @SubscribeEvent
    public static void blocks(RegisterColorHandlersEvent.Block e) {
        e.register((state, world, pos, tint) -> {
            return (world != null && pos != null)
                    ? BiomeColors.getAverageGrassColor(world, pos)       // stesso verde del bioma
                    : GrassColor.getDefaultColor();                      // inventario / world assente
        }, ModBlocks.APPALACHIAN_GRASS_BLOCK.get());
    }

    // item in mano / inventario
    @SubscribeEvent
    public static void items(RegisterColorHandlersEvent.Item e) {
        e.register((stack, tint) -> GrassColor.getDefaultColor(),
                ModBlocks.APPALACHIAN_GRASS_BLOCK.get());
    }
}

