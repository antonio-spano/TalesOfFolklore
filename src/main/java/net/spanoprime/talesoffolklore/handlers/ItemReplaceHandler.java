package net.spanoprime.talesoffolklore.handlers;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spanoprime.talesoffolklore.item.ModItems;

@Mod.EventBusSubscriber(modid = "talesoffolklore")
public class ItemReplaceHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        NonNullList<ItemStack> inventory = player.getInventory().items;

        if (player.containerMenu != null) {
            for (Slot slot : player.containerMenu.slots) {
                ItemStack stack = slot.getItem();
                if (stack.getItem() == ModItems.TALES_OF_FOLKLORE_R.get()) {
                    slot.set(new ItemStack(ModItems.TALES_OF_FOLKLORE.get(), stack.getCount()));
                }
            }
        }
    }
}