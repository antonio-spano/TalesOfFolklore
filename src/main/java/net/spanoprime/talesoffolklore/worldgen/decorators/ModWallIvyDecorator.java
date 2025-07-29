package net.spanoprime.talesoffolklore.worldgen.decorators;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.block.custom.ModWallIvySeed;
import org.slf4j.Logger;

public class ModWallIvyDecorator extends TreeDecorator {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<ModWallIvyDecorator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.FLOAT.fieldOf("probability").forGetter(d -> d.probability)
            ).apply(instance, ModWallIvyDecorator::new)
    );

    private final float probability;

    public ModWallIvyDecorator(float probability) {
        this.probability = probability;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return ModTreeDecoratorTypes.WALL_IVY.get(); // Assicurati che questo sia corretto
    }

    @Override
    public void place(Context context) {
        RandomSource random = context.random();

        int minY = context.logs().stream()
                .mapToInt(BlockPos::getY)
                .min()
                .orElse(Integer.MAX_VALUE);

        if (minY == Integer.MAX_VALUE) {
            return;
        }

        BlockGetter blockGetter = (BlockGetter) context.level();
        final int finalMinY = minY;

        for (BlockPos logPos : context.logs()) {
            if (logPos.getY() == finalMinY) {
                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    if (random.nextFloat() < this.probability) {
                        BlockPos targetPos = logPos.relative(direction);
                        BlockPos belowTargetPos = targetPos.below();
                        BlockPos aboveTargetPos = targetPos.above();

                        BlockState targetState = blockGetter.getBlockState(targetPos);
                        BlockState belowState = blockGetter.getBlockState(belowTargetPos);

                        boolean isTargetAir = context.isAir(targetPos);
                        boolean isAboveTargetAir = context.isAir(aboveTargetPos);
                        boolean isGroundBelowTargetValid = ModWallIvySeed.isValidGroundOrBrick(belowState);
                        boolean isTargetBlockValidGround = ModWallIvySeed.isValidGroundOrBrick(targetState);

                        boolean logConflictAtTarget = context.logs().contains(targetPos);
                        boolean leafConflictAtTarget = context.leaves().contains(targetPos);
                        boolean logConflictAboveTarget = context.logs().contains(aboveTargetPos);
                        boolean leafConflictAboveTarget = context.leaves().contains(aboveTargetPos);

                        BlockPos finalPlacementPos = null;

                        if (isTargetAir && isGroundBelowTargetValid && !logConflictAtTarget && !leafConflictAtTarget) {
                            finalPlacementPos = targetPos;
                        }
                        else if (!isTargetAir && isAboveTargetAir && isTargetBlockValidGround && !logConflictAboveTarget && !leafConflictAboveTarget) {
                            if (!logConflictAtTarget && !leafConflictAtTarget) {
                                finalPlacementPos = aboveTargetPos;
                            }
                        }

                        // --- ECCO IL FIX ---
                        if (finalPlacementPos != null) {
                            // Non piazziamo più lo stato di default.
                            // Chiediamo al blocco ModWallIvySeed di calcolare il suo stato corretto
                            // in base ai muri adiacenti ALLA POSIZIONE FINALE.
                            // Usiamo il suo metodo getStateForPlacement, che è perfetto per questo.
                            BlockState defaultSeedState = ModBlocks.WALL_IVY_SEED.get().defaultBlockState();

                            // getStateForPlacement in realtà non usa il contesto, ma per sicurezza...
                            // La cosa importante è che calcola le facce.
                            // Lo adattiamo un po' per il nostro contesto.
                            boolean north = ModWallIvySeed.isWall(((BlockGetter) context.level()).getBlockState(finalPlacementPos.north()));
                            boolean east = ModWallIvySeed.isWall(((BlockGetter) context.level()).getBlockState(finalPlacementPos.east()));
                            boolean south = ModWallIvySeed.isWall(((BlockGetter) context.level()).getBlockState(finalPlacementPos.south()));
                            boolean west = ModWallIvySeed.isWall(((BlockGetter) context.level()).getBlockState(finalPlacementPos.west()));

                            BlockState correctState = defaultSeedState
                                    .setValue(ModWallIvySeed.NORTH, north)
                                    .setValue(ModWallIvySeed.EAST, east)
                                    .setValue(ModWallIvySeed.SOUTH, south)
                                    .setValue(ModWallIvySeed.WEST, west);

                            // Piazziamo lo stato corretto, non quello di default.
                            context.setBlock(finalPlacementPos, correctState);
                        }
                    }
                }
            }
        }
    }
}