package net.spanoprime.talesoffolklore.datagen;

import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.spanoprime.talesoffolklore.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, TalesOfFolklore.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.VIRGINIA_PINE_STAIRS.get(),
                        ModBlocks.VIRGINIA_PINE_PLANKS.get(),
                        ModBlocks.VIRGINIA_PINE_DOOR.get(),
                        ModBlocks.VIRGINIA_PINE_WALL_SIGN.get(),
                        ModBlocks.VIRGINIA_PINE_LOG.get(),
                        ModBlocks.VIRGINIA_PINE_BUTTON.get(),
                        ModBlocks.VIRGINIA_PINE_FENCE.get(),
                        ModBlocks.VIRGINIA_PINE_TRAPDOOR.get(),
                        ModBlocks.VIRGINIA_PINE_FENCE_GATE.get(),
                        ModBlocks.VIRGINIA_PINE_HANGING_SIGN.get(),
                        ModBlocks.VIRGINIA_PINE_LADDER.get(),
                        ModBlocks.VIRGINIA_PINE_PRESSURE_PLATE.get(),
                        ModBlocks.VIRGINIA_PINE_SCAFFOLDING.get(),
                        ModBlocks.VIRGINIA_PINE_SIGN.get(),
                        ModBlocks.VIRGINIA_PINE_SLAB.get(),
                        ModBlocks.VIRGINIA_PINE_WALL_HANGING_SIGN.get(),
                        ModBlocks.VIRGINIA_PINE_WALL_SIGN.get(),
                        ModBlocks.VIRGINIA_PINE_WOOD.get(),
                        ModBlocks.STRIPPED_VIRGINIA_PINE_WOOD.get(),
                        ModBlocks.STRIPPED_VIRGINIA_PINE_LOG.get(),
                        ModBlocks.YELLOW_FUNGUS.get(),
                        ModBlocks.RED_FUNGUS.get(),
                        ModBlocks.WHITE_FUNGUS.get());

        this.tag(BlockTags.MINEABLE_WITH_HOE)
                .add(ModBlocks.VIRGINIA_PINE_LEAVES.get(),
                        ModBlocks.WALL_IVY_SEED.get(),
                        ModBlocks.WALL_IVY.get(),
                        ModBlocks.PINE_NEEDLES.get(),
                        ModBlocks.UNDERGROWTH.get(),
                        ModBlocks.FERN.get());

        this.tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(ModBlocks.DAMP_GRASS_BLOCK.get(),
                        ModBlocks.DAMP_DIRT.get(),
                        ModBlocks.STREAMBED_ROCKS.get(),
                        ModBlocks.DAMP_PODZOL.get(),
                        ModBlocks.DAMP_COARSE_DIRT.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.APPALACHIAN_STONE.get(),
                        ModBlocks.MOSSY_STREAMBANK_ROCKS.get());

        this.tag(BlockTags.DIRT)
                .add(ModBlocks.DAMP_GRASS_BLOCK.get(),
                        ModBlocks.DAMP_DIRT.get(),
                        ModBlocks.DAMP_PODZOL.get(),
                        ModBlocks.DAMP_COARSE_DIRT.get());
    }
}
