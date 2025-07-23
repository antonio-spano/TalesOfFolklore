package net.spanoprime.talesoffolklore.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import net.spanoprime.talesoffolklore.block.ModBlocks;

public class ModBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TalesOfFolklore.MOD_ID);

    public static final RegistryObject<BlockEntityType<ModSignBlockEntity>> MOD_SIGN =
            BLOCK_ENTITIES.register("mod_sign",
                    () -> BlockEntityType.Builder.of(ModSignBlockEntity::new,
                            ModBlocks.VIRGINIA_PINE_SIGN.get(),
                            ModBlocks.VIRGINIA_PINE_WALL_SIGN.get()).build(null));

    public static final RegistryObject<BlockEntityType<ModHangingSignBlockEntity>> MOD_HANGING_SIGN =
            BLOCK_ENTITIES.register("mod_hanging_sign",
                    () -> BlockEntityType.Builder.of(ModHangingSignBlockEntity::new,
                            ModBlocks.VIRGINIA_PINE_HANGING_SIGN.get(),
                            ModBlocks.VIRGINIA_PINE_WALL_HANGING_SIGN.get()).build(null));

    public static final RegistryObject<BlockEntityType<ModSpriteBlockEntity>> MOD_SPRITE =
            BLOCK_ENTITIES.register("mod_sprite",
                    () -> BlockEntityType.Builder.of(ModSpriteBlockEntity::new,
                            ModBlocks.SPRITE.get()).build(null));



    public static void register(IEventBus eventBus) { BLOCK_ENTITIES.register(eventBus); }
}
