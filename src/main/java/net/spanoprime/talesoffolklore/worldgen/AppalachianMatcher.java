package net.spanoprime.talesoffolklore.worldgen;

import com.terraformersmc.biolith.api.biome.BiomePlacement;
import com.terraformersmc.biolith.api.biome.SubBiomeMatcher;
import com.terraformersmc.biolith.impl.biome.BiolithFittestNodes;
import com.terraformersmc.biolith.impl.biome.DimensionBiomePlacement;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;

import javax.annotation.Nullable;

public class AppalachianMatcher extends SubBiomeMatcher {

    private final int minDistanceFromSpawn; // blocchi
    private final int minPatchSize;         // patch size minima (in blocchi o chunk, vedi sotto)

    public AppalachianMatcher(int minDistanceFromSpawn, int minPatchSize) {
        this.minDistanceFromSpawn = minDistanceFromSpawn;
        this.minPatchSize = minPatchSize;
    }

    @Override
    public SubBiomeMatcher addCriterion(Criterion criterion) {
        return null;
    }

    @Override
    public void sort() {

    }

    @Override
    public boolean matches(
            BiolithFittestNodes<Holder<Biome>> biomes,
            DimensionBiomePlacement placement,
            Climate.TargetPoint point,
            @Nullable org.joml.Vector2fc pos,
            float weirdness
    ) {
        double px = 0, pz = 0;
        boolean hasPos = false;

        if (pos != null) {
            px = pos.x();
            pz = pos.y();
            hasPos = true;
        } else {
            // fallback: PROVA A RICOSTRUIRE coordinate da uno dei climate param
            // (NON PRECISO, va bene solo come fallback. Qui uso temperature/humidity a caso)
            px = net.minecraft.world.level.biome.Climate.unquantizeCoord(point.temperature()) * 4000; // world range ±8000
            pz = net.minecraft.world.level.biome.Climate.unquantizeCoord(point.humidity()) * 4000;
        }

        double distance = Math.sqrt(px * px + pz * pz);
        if (distance < minDistanceFromSpawn)
            return false;

        if (pos != null) {
            double distFromPatchCenter = Math.sqrt(Math.pow(px - pos.x(), 2) + Math.pow(pz - pos.y(), 2));
            // Patch radius = minPatchSize / 2
            return !(distFromPatchCenter > minPatchSize / 2.0);
        }


        // Patch size: non hai dati, quindi controlla solo distanza
        return true;
    }

}
