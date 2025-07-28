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
import net.spanoprime.talesoffolklore.block.custom.ModWallIvySeed; // Importa per usare isValidGroundOrBrick
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
        return ModTreeDecoratorTypes.WALL_IVY.get();
    }

    @Override
    public void place(Context context) {
        RandomSource random = context.random();
        BlockState ivySeedState = ModBlocks.WALL_IVY_SEED.get().defaultBlockState();
/*
        context.logs().stream().findFirst().ifPresent(bp ->
                LOGGER.info("[TOF IvyDecorator] Attempting decoration for tree near {}", bp)
        ); */

        int minY = context.logs().stream()
                .mapToInt(BlockPos::getY)
                .min()
                .orElse(Integer.MAX_VALUE);

        if (minY == Integer.MAX_VALUE) {
            //LOGGER.warn("[TOF IvyDecorator] Could not determine minY for tree.");
            return;
        }

        Object rawLevel = context.level();
        if (!(rawLevel instanceof BlockGetter)) {
            //LOGGER.error("[TOF IvyDecorator] context.level() is not a BlockGetter! Actual type: {}. Cannot check block below.", rawLevel.getClass().getName());
            return;
        }
        BlockGetter blockGetter = (BlockGetter) rawLevel;

        final int finalMinY = minY;

        for (BlockPos logPos : context.logs()) {
            // Considera SOLO il blocco di tronco alla base esatta
            if (logPos.getY() == finalMinY) {
                //LOGGER.debug("[TOF IvyDecorator] Checking base log at {}", logPos);

                for (Direction direction : Direction.Plane.HORIZONTAL) {
                    //LOGGER.debug("[TOF IvyDecorator]  - Checking direction: {}", direction);

                    // Applica la probabilità
                    if (random.nextFloat() < this.probability) {
                        BlockPos targetPos = logPos.relative(direction); // Posizione accanto al tronco base
                        BlockPos belowTargetPos = targetPos.below();     // Posizione sotto quella accanto
                        BlockPos aboveTargetPos = targetPos.above();     // Posizione sopra quella accanto

                        // Ottieni gli stati dei blocchi rilevanti
                        BlockState targetState = blockGetter.getBlockState(targetPos);
                        BlockState belowState = blockGetter.getBlockState(belowTargetPos);

                        // Controlla le condizioni
                        boolean isTargetAir = context.isAir(targetPos);
                        boolean isAboveTargetAir = context.isAir(aboveTargetPos);

                        // Condizione terreno: Sotto il *potenziale* piazzamento
                        boolean isGroundBelowTargetValid = ModWallIvySeed.isValidGroundOrBrick(belowState); // Se piazzo a targetPos
                        boolean isTargetBlockValidGround = ModWallIvySeed.isValidGroundOrBrick(targetState); // Se piazzo a aboveTargetPos (il terreno è targetPos)

                        // Controlla conflitti con l'albero
                        boolean logConflictAtTarget = context.logs().contains(targetPos);
                        boolean leafConflictAtTarget = context.leaves().contains(targetPos);
                        boolean logConflictAboveTarget = context.logs().contains(aboveTargetPos);
                        boolean leafConflictAboveTarget = context.leaves().contains(aboveTargetPos);

                        // Logga le condizioni per debug
                        /*LOGGER.debug("[TOF IvyDecorator]    - Target: {} (State: {}, isAir: {})", targetPos, targetState, isTargetAir);
                        LOGGER.debug("[TOF IvyDecorator]    - BelowTarget: {} (State: {}, isValidGround: {})", belowTargetPos, belowState, isGroundBelowTargetValid);
                        LOGGER.debug("[TOF IvyDecorator]    - AboveTarget: {} (isAir: {})", aboveTargetPos, isAboveTargetAir);
                        LOGGER.debug("[TOF IvyDecorator]    - TargetBlock itself valid ground? {}", isTargetBlockValidGround);*/

                        // --- Logica di Piazzamento Aggiornata ---

                        // 1. Prova a piazzare a livello base (targetPos) se è aria e ha terreno valido sotto
                        if (isTargetAir && isGroundBelowTargetValid && !logConflictAtTarget && !leafConflictAtTarget) {
                            //LOGGER.info("[TOF IvyDecorator]      Scenario 1: PLACING IVY SEED at {} (Standard - target is air)", targetPos);
                            context.setBlock(targetPos, ivySeedState);
                        }
                        // 2. ALTRIMENTI, se il livello base NON è aria, MA lo spazio SOPRA è aria,
                        //    E il blocco al livello base (targetPos) è considerato terreno valido su cui il seme può stare...
                        else if (!isTargetAir && isAboveTargetAir && isTargetBlockValidGround && !logConflictAboveTarget && !leafConflictAboveTarget) {
                            // Aggiungiamo un controllo extra: assicuriamoci che targetPos (che non è aria) non sia esso stesso un log/foglia
                            if (!logConflictAtTarget && !leafConflictAtTarget) {
                                //LOGGER.info("[TOF IvyDecorator]      Scenario 2: Target not air, PLACING IVY SEED at {} (Higher - on top of target)", aboveTargetPos);
                                context.setBlock(aboveTargetPos, ivySeedState);
                            } else {
                                // Se targetPos non era aria MA era un log/foglia, non possiamo piazzarci sopra
                                //LOGGER.debug("[TOF IvyDecorator]      -> Scenario 2 Failed: Target was not air but was log/leaf conflict.");
                            }
                        }
                        // 3. Se nessuna delle due condizioni precedenti è soddisfatta, non piazzare nulla.
                        else {
                            StringBuilder reason = new StringBuilder("-> Placement Failed: ");
                            if (isTargetAir && !isGroundBelowTargetValid) reason.append("TargetAirButInvalidGroundBelow ");
                            if (!isTargetAir && !isAboveTargetAir) reason.append("TargetAndAboveNotEmpty ");
                            if (!isTargetAir && isAboveTargetAir && !isTargetBlockValidGround) reason.append("TargetNotEmptyAboveAirButTargetNotValidGround ");
                            if (logConflictAtTarget) reason.append("LogConflict@Target ");
                            if (leafConflictAtTarget) reason.append("LeafConflict@Target ");
                            if (logConflictAboveTarget) reason.append("LogConflict@Above ");
                            if (leafConflictAboveTarget) reason.append("LeafConflict@Above ");
                            if (reason.length() == "-> Placement Failed: ".length()) reason.append("Unknown combination"); // Fallback
                            //LOGGER.debug("[TOF IvyDecorator]      {}", reason.toString().trim());
                        }

                    } else {
                        //LOGGER.debug("[TOF IvyDecorator]    - Probability check failed.");
                    }
                }
            }
        }
/*
        context.logs().stream().findFirst().ifPresent(bp ->
                LOGGER.info("[TOF IvyDecorator] Finished decoration attempt for tree near {}", bp)
        ); */
    }
}