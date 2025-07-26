package net.spanoprime.talesoffolklore.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Biome;
import net.spanoprime.talesoffolklore.holder.ModBiomeHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiNoiseBiomeSource.class)
public abstract class MultiNoiseBiomeSourceMixin {
    @Unique
    private static final int CENTER_X = 1000;
    @Unique
    private static final int CENTER_Z = 1000;
    @Unique
    private static final int RADIUS   = 128;

    @Inject(
            method = "getNoiseBiome(IIILnet/minecraft/world/level/biome/Climate$Sampler;)Lnet/minecraft/core/Holder;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onGetNoiseBiome(int xQuart, int yQuart, int zQuart, Climate.Sampler sampler, CallbackInfoReturnable<Holder<Biome>> cir) {
        int blockX = xQuart << 2;
        int blockZ = zQuart << 2;
        int dx = blockX - CENTER_X;
        int dz = blockZ - CENTER_Z;

        if (dx*dx + dz*dz <= RADIUS*RADIUS) {
            cir.setReturnValue(ModBiomeHolder.APPALACHIAN_HOLDER);
        }
    }
}
