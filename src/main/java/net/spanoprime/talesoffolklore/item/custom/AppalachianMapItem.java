package net.spanoprime.talesoffolklore.item.custom;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome; // Import Biome
import net.minecraft.world.level.levelgen.Heightmap; // Import Heightmap
import net.spanoprime.talesoffolklore.TalesOfFolklore; // Your main mod class for MOD_ID
import org.slf4j.Logger;

import java.util.Optional; // Import Optional

public class AppalachianMapItem extends Item {

    private static final Logger LOGGER = LogUtils.getLogger();
    // Biome Search Parameters
    private static final int BIOME_SEARCH_RADIUS_HORIZONTAL = 6400; // Blocks (e.g., 100 chunks) - adjust as needed
    private static final int BIOME_SEARCH_RADIUS_VERTICAL = 64;    // Blocks vertically
    private static final int BIOME_SEARCH_INCREMENT = 32;          // Search step size (smaller is more accurate but slower)

    // Key for your target biome
    private static final ResourceKey<Biome> TARGET_BIOME_KEY = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(TalesOfFolklore.MOD_ID, "appalachian_forest") // *** MAKE SURE THIS MATCHES YOUR BIOME'S REGISTERED NAME ***
    );

    public AppalachianMapItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);

        if (pLevel instanceof ServerLevel serverLevel) {
            BlockPos playerPos = pPlayer.blockPosition();

            // 1. Find the closest BIOME matching the key
            // We need the Biome registry to create the predicate
            var biomeRegistry = serverLevel.registryAccess().registryOrThrow(Registries.BIOME);

            // Search for the biome
            Pair<BlockPos, Holder<Biome>> nearestBiomeInfo = serverLevel.findClosestBiome3d(
                    holder -> holder.is(TARGET_BIOME_KEY), // 1. Predicate<Holder<Biome>>
                    playerPos,                             // 2. BlockPos
                    BIOME_SEARCH_RADIUS_HORIZONTAL,        // 3. int horizontal radius
                    BIOME_SEARCH_RADIUS_VERTICAL,          // 4. int vertical radius
                    BIOME_SEARCH_INCREMENT                 // 5. int increment
            );

            if (nearestBiomeInfo != null) {
                // Corrected: Access Pair elements directly, NO .get() needed
                BlockPos biomePos = nearestBiomeInfo.getFirst();
                Holder<Biome> biomeHolder = nearestBiomeInfo.getSecond();
                LOGGER.info("Found target biome {} nearby at {}", biomeHolder.unwrapKey().map(ResourceKey::location).orElse(TARGET_BIOME_KEY.location()), biomePos);

                // Find the surface Y level at the biome's X/Z coordinates for a better map center
                BlockPos surfacePos = serverLevel.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, biomePos); // WG = World Gen surface

                // 2. Create a standard MAP centered on the biome's surface position
                ItemStack mapStack = MapItem.create(serverLevel, surfacePos.getX(), surfacePos.getZ(), (byte) 2, true, true);

                // Add a custom name
                mapStack.setHoverName(Component.translatable("item.talesoffolklore.appalachian_map.revealed_title").withStyle(ChatFormatting.DARK_GREEN));

                // 3. Handle inventory, sound, stats
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                pPlayer.awardStat(Stats.ITEM_USED.get(this));
                // Use the map filling sound, or book sound if you prefer
                pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.PLAYERS, 1.0F, 1.0F);

                if (itemstack.isEmpty()) {
                    return InteractionResultHolder.success(mapStack);
                } else {
                    if (!pPlayer.getInventory().add(mapStack.copy())) {
                        pPlayer.drop(mapStack, false);
                    }
                    return InteractionResultHolder.success(itemstack);
                }

            } else {
                // Biome not found within the search radius
                LOGGER.warn("Could not find biome with key {} near {}", TARGET_BIOME_KEY.location(), playerPos);
                if (!pLevel.isClientSide) {
                    // *** Use a new translation key for biome not found ***
                    pPlayer.sendSystemMessage(Component.translatable("item.talesoffolklore.appalachian_map.biome_not_found").withStyle(ChatFormatting.RED));
                }
                return InteractionResultHolder.fail(itemstack);
            }
        }
        // Client side, do nothing special
        return InteractionResultHolder.pass(itemstack);
    }
}