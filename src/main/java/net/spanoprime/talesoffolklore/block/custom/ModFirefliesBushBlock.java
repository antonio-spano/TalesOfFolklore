package net.spanoprime.talesoffolklore.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.particles.ParticleTypes;

public class ModFirefliesBushBlock extends BushBlock {
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 1);
    private static final VoxelShape SHAPE = Block.box(5, 0, 5, 11, 6, 11);

    public ModFirefliesBushBlock(Properties props) {
        super(props);
        // nulla qui: il lightLevel lo gestiamo in registrazione
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VARIANT);
    }

    // Al posizionamento pianifichiamo subito il nostro primo tick
    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!world.isClientSide()) {
            world.scheduleTick(pos, this, 1);
        }
        super.onPlace(state, world, pos, oldState, isMoving);
    }

    // Tick server programmato
    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        long dayTime = world.getDayTime() % 24000;
        boolean isNight = (dayTime >= 13000 && dayTime < 23000);
        int desired = isNight ? 1 : 0;

        if (state.getValue(VARIANT) != desired) {
            world.setBlock(pos, state.setValue(VARIANT, desired), 3);
        }

        // Ripianifica il prossimo controllo fra 100 tick (~5s)
        world.scheduleTick(pos, this, 100);
    }

    // Client-side: particelle notturne “lucciole”
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if (state.getValue(VARIANT) == 1) {
            // ogni ~10 tick spawna una particella
            if (rand.nextInt(10) == 0) {
                double x = pos.getX() + rand.nextDouble();
                double y = pos.getY() + 0.5 + rand.nextDouble() * 0.5;
                double z = pos.getZ() + rand.nextDouble();
                // usa END_ROD per un effetto glow, verso l’alto leggero
                world.addParticle(ParticleTypes.END_ROD, x, y, z, 0, 0.04, 0);
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE;
    }
}
