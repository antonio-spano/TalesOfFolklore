package net.spanoprime.talesoffolklore.worldgen.decorators;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTreeDecoratorTypes {
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATORS =
            DeferredRegister.create(Registries.TREE_DECORATOR_TYPE, "talesoffolklore");

    public static final RegistryObject<TreeDecoratorType<ModWallMossDecorator>> WALL_MOSS =
            TREE_DECORATORS.register("wall_moss", () -> new TreeDecoratorType<>(ModWallMossDecorator.CODEC));

    public static final RegistryObject<TreeDecoratorType<ModWallIvyDecorator>> WALL_IVY =
            TREE_DECORATORS.register("wall_ivy_seed", () -> new TreeDecoratorType<>(ModWallIvyDecorator.CODEC));

    public static final RegistryObject<TreeDecoratorType<ModPineNeedlesDecorator>> PINE_NEEDLES =
            TREE_DECORATORS.register("pine_needles", () -> new TreeDecoratorType<>(ModPineNeedlesDecorator.CODEC));

    public static final RegistryObject<TreeDecoratorType<ModYellowFungusDecorator>> YELLOW_FUNGUS =
            TREE_DECORATORS.register("yellow_fungus", () -> new TreeDecoratorType<>(ModYellowFungusDecorator.CODEC));

    public static final RegistryObject<TreeDecoratorType<ModUndergrowthDecorator>> UNDERGROWTH =
            TREE_DECORATORS.register("undergrowth", () -> new TreeDecoratorType<>(ModUndergrowthDecorator.CODEC));

    public static void register(IEventBus eventBus) {
        TREE_DECORATORS.register(eventBus);
    }
}