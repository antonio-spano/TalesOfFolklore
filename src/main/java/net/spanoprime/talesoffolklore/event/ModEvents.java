package net.spanoprime.talesoffolklore.event;

import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.spanoprime.talesoffolklore.item.ModItems;

@Mod.EventBusSubscriber(modid = "talesoffolklore")
public class ModEvents {

    @SubscribeEvent
    public static void onFuelBurn(FurnaceFuelBurnTimeEvent event) {
        Item item = event.getItemStack().getItem();

        if (item == ModItems.VIRGINIA_PINECONE.get()) {
            event.setBurnTime(100); // Durata in tick (1 secondo = 20 tick)
        }
    }
}