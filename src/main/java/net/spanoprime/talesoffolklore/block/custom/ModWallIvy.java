package net.spanoprime.talesoffolklore.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.spanoprime.talesoffolklore.block.ModBlocks;

// Blocco edera normale. Non fa nulla di speciale.
// Viene solo piazzato dal ModWallIvySeed.
public class ModWallIvy extends Block {

    public ModWallIvy(Properties pProperties) {
        super(pProperties);
    }

    // Opzionale: Rende il blocco trasparente alla luce
    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return true; // Lascia passare la luce del cielo
    }

    // Opzionale: Fa sì che non scurisca troppo l'area
    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 0.8F; // Meno scuro di un blocco solido (1.0F è luminosità piena)
    }

    // Opzionale: Lo rende non solido alla vista (es. vedi attraverso se ci sono altri blocchi trasparenti dietro)




    // NON mettiamo canSurvive o randomTick qui. Deve solo esistere.
}