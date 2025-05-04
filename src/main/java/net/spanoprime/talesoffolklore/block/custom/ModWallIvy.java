package net.spanoprime.talesoffolklore.block.custom;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.VineBlock; // Usato solo per le proprietà
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Blocco edera normale. Non si espande da solo.
 * Viene piazzato solo da ModWallIvySeed.
 * Deve avere un muro valido adiacente per sopravvivere.
 * Si attacca visivamente ai muri con texture piane.
 * Ha una selection box sottile ma è attraversabile.
 */
public class ModWallIvy extends Block {

    // Proprietà booleane per i lati orizzontali
    public static final BooleanProperty NORTH = VineBlock.NORTH;
    public static final BooleanProperty EAST = VineBlock.EAST;
    public static final BooleanProperty SOUTH = VineBlock.SOUTH;
    public static final BooleanProperty WEST = VineBlock.WEST;

    // Mappa statica per accedere facilmente alla proprietà dalla direzione
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = VineBlock.PROPERTY_BY_DIRECTION.entrySet().stream()
            .filter(entry -> entry.getKey().getAxis().isHorizontal())
            .collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));

    // --- Definizioni delle forme sottili per la SELECTION BOX ---
    // Spessore di 1 pixel (1/16 di blocco)
    protected static final double THICKNESS = 1.0D; // Spessore in pixel
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, THICKNESS, 16.0D, 16.0D);   // Attaccato a Ovest, si estende verso Est
    protected static final VoxelShape WEST_AABB = Block.box(16.0D - THICKNESS, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D); // Attaccato a Est, si estende verso Ovest
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, THICKNESS);  // Attaccato a Nord, si estende verso Sud
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 16.0D - THICKNESS, 16.0D, 16.0D, 16.0D); // Attaccato a Sud, si estende verso Nord

    public ModWallIvy(BlockBehaviour.Properties pProperties) {
        // Proprietà: attraversabile (.noCollission), si rompe subito, suono di pianta
        // noOcclusion aiuta con il rendering
        super(pProperties.noOcclusion().noCollission().instabreak().sound(SoundType.VINE));
        // Stato di default: nessun lato attaccato
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, Boolean.valueOf(false))
                .setValue(EAST, Boolean.valueOf(false))
                .setValue(SOUTH, Boolean.valueOf(false))
                .setValue(WEST, Boolean.valueOf(false)));
    }

    // Registra le proprietà dello stato del blocco
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(NORTH, EAST, SOUTH, WEST);
    }

    // --- Forma del Blocco ---

    /**
     * Determina la SELECTION BOX (contorno nero).
     * Questa forma è composta dalle facce sottili corrispondenti ai lati attaccati.
     * NON influisce sulla collisione fisica grazie a .noCollission().
     */
    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        VoxelShape shape = Shapes.empty();
        // Combina le forme dei lati attivi
        if (pState.getValue(SOUTH)) { shape = Shapes.or(shape, NORTH_AABB); }
        if (pState.getValue(NORTH)) { shape = Shapes.or(shape, SOUTH_AABB); }
        if (pState.getValue(WEST)) { shape = Shapes.or(shape, EAST_AABB); }
        if (pState.getValue(EAST)) { shape = Shapes.or(shape, WEST_AABB); }
        // Ritorna la forma combinata (o vuota se nessun lato è attivo,
        // anche se non dovrebbe succedere con la logica di sopravvivenza)
        return shape.isEmpty() ? Shapes.block() : shape; // Ritorna block() se vuota per sicurezza, ma idealmente non serve
        // Potremmo anche semplicemente ritornare `shape;`
        // return shape;
    }

    // NOTA: Non sovrascriviamo getCollisionShape.
    // Poiché abbiamo usato .noCollission() nelle proprietà, getCollisionShape
    // restituirà automaticamente Shapes.empty(), rendendo il blocco attraversabile.

    // --- Proprietà Visive ---
    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return 1.0F;
    }

    // --- Logica di Sopravvivenza e Piazzamento ---

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return !getStateBasedOnAdjacentWalls(pLevel, pPos, this.defaultBlockState()).isAir();
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        BlockState newState = getStateBasedOnAdjacentWalls(pLevel, pCurrentPos, this.defaultBlockState());
        if (newState.isAir()) {
            return Blocks.AIR.defaultBlockState();
        }
        return newState;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        BlockState state = getStateBasedOnAdjacentWalls(level, pos, this.defaultBlockState());
        return state.isAir() ? null : state;
    }

    // --- Metodi Helper Statici --- (Usati internamente e da ModWallIvySeed)

    private static boolean canAttachTo(LevelReader pLevel, BlockPos pPos, Direction pDirection) {
        if (!PROPERTY_BY_DIRECTION.containsKey(pDirection)) {
            return false;
        }
        BlockPos neighborPos = pPos.relative(pDirection);
        BlockState neighborState = pLevel.getBlockState(neighborPos);
        return ModWallIvySeed.isWall(neighborState) && neighborState.isFaceSturdy(pLevel, neighborPos, pDirection.getOpposite());
    }

    public static BlockState getStateBasedOnAdjacentWalls(LevelReader pLevel, BlockPos pPos, BlockState ivyBaseState) {
        BlockState state = ivyBaseState;
        boolean foundSupport = false;

        // Reset all properties before checking, starting from the base state
        state = state.setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false);


        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (canAttachTo(pLevel, pPos, direction)) {
                BooleanProperty property = PROPERTY_BY_DIRECTION.get(direction);
                if (property != null) {
                    state = state.setValue(property, Boolean.valueOf(true));
                    foundSupport = true;
                }
            }
        }
        return foundSupport ? state : Blocks.AIR.defaultBlockState();
    }

    // Non ha isRandomlyTicking()
}