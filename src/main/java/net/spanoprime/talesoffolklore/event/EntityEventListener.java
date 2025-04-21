package net.spanoprime.talesoffolklore.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spanoprime.talesoffolklore.item.ModItems;

@Mod.EventBusSubscriber
public class EntityEventListener {

    public static void register(IEventBus eventBus) {
        eventBus.register(EntityEventListener.class);
    }

    @SubscribeEvent
    public static void onSnowballHit(ProjectileImpactEvent event) {
        if (!(event.getProjectile() instanceof Snowball snowball)) return;

        // Verifica che chi ha lanciato la snowball sia un giocatore
        if (snowball.getOwner() instanceof Player player) {
            // Controlla se il giocatore stava usando effettivamente il pinecone (main hand)
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();

            if (main.getItem() != ModItems.VIRGINIA_PINECONE.get() && off.getItem() != ModItems.VIRGINIA_PINECONE.get()) {
                return; // Non è un pinecone in nessuna mano
            }

            // Se ha colpito un blocco, ottieni la posizione
            if (event.getRayTraceResult() instanceof BlockHitResult hitResult) {
                BlockPos pos = hitResult.getBlockPos();
                System.out.println("Pinecone landed at: " + pos);
            }
        }
    }
}