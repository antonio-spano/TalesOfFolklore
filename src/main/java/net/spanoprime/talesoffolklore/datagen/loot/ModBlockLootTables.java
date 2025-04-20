package net.spanoprime.talesoffolklore.datagen.loot;

import net.spanoprime.talesoffolklore.block.ModBlocks;
import net.spanoprime.talesoffolklore.item.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.VIRGINIA_PINE_PLANKS.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_STAIRS.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_DOOR.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_SAPLING.get());
        //this.dropSelf(ModBlocks.VIRGINIA_PINE_LEAVES.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_WALL_SIGN.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_LOG.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_TRAPDOOR.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_BUTTON.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_FENCE.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_FENCE_GATE.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_HANGING_SIGN.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_LADDER.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_PRESSURE_PLATE.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_SCAFFOLDING.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_SIGN.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_SLAB.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_WALL_HANGING_SIGN.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_WALL_SIGN.get());
        this.dropSelf(ModBlocks.VIRGINIA_PINE_WOOD.get());
        this.dropSelf(ModBlocks.STRIPPED_VIRGINIA_PINE_WOOD.get());
        this.dropSelf(ModBlocks.STRIPPED_VIRGINIA_PINE_LOG.get());
        this.dropSelf(ModBlocks.WALL_MOSS.get());

        this.add(ModBlocks.VIRGINIA_PINE_LEAVES.get(),
                createLeavesDrops(ModBlocks.VIRGINIA_PINE_LEAVES.get(), ModBlocks.VIRGINIA_PINE_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
