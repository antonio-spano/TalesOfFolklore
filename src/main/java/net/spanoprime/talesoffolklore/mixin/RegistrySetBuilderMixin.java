package net.spanoprime.talesoffolklore.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.Biome;
import net.spanoprime.talesoffolklore.worldgen.biome.ModBiomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RegistrySetBuilder.class)
public class RegistrySetBuilderMixin {

    // Creiamo un campo statico per salvare il nostro Holder.
    // È 'public' così l'altro Mixin può accedervi facilmente.
    @Unique
    public static Holder<Biome> talesoffolklore$appalachianHolder = null;

    /**
     * Questo Mixin intercetta il momento in cui vengono costruiti i registri del mondo.
     * A questo punto, il registro BIOME esiste e possiamo ottenere il nostro Holder in modo sicuro.
     */
    @Inject(method = "build", at = @At("HEAD"))
    private void talesoffolklore$captureBiomeHolder(CallbackInfoReturnable<RegistrySetBuilder.BuildResult> cir) {
        // Controlliamo se lo abbiamo già fatto per evitare di ripeterlo.
        if (talesoffolklore$appalachianHolder == null) {

            // Questo è un trucco un po' sporco ma efficace per accedere al builder.
            RegistrySetBuilder self = (RegistrySetBuilder) (Object) this;

            // Creiamo un lookup temporaneo per il registro dei biomi.
            RegistrySetBuilder.UniversalLookup lookup = self.buildUniversalLookup();
            Registry<Biome> biomeRegistry = lookup.registry(Registries.BIOME);

            // Prendiamo l'Holder dal registro e lo salviamo nel nostro campo statico.
            biomeRegistry.getHolder(ModBiomes.APPALACHIAN_FOREST).ifPresent(holder -> {
                talesoffolklore$appalachianHolder = holder;
                System.out.println("[Tales of Folklore] Mixin Success: Captured Appalachian Forest Holder during registry build.");
            });
        }
    }
}