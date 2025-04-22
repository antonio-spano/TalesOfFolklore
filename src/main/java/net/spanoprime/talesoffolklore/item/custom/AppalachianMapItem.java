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
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3; // Needed for vector math
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import org.slf4j.Logger;

import java.util.Optional; // Still needed for clarity

public class AppalachianMapItem extends Item {

    private static final Logger LOGGER = LogUtils.getLogger();

    // Configuration (Keeping most previous values)
    private static final int SEARCH_RADIUS = 8192; // Single large radius for searches
    private static final int BIOME_SEARCH_RADIUS_VERTICAL = 64;
    private static final int BIOME_SEARCH_INCREMENT_STEP = 32;

    private static final int MIN_BIOME_PATCH_WIDTH = 150;
    private static final int SIZE_CHECK_DISTANCE = (int) (MIN_BIOME_PATCH_WIDTH * 0.5);
    private static final int MIN_REQUIRED_SIZE_CHECKS = 4; // 4 out of 8

    // How far beyond the small biome to start the *second* search
    private static final double SECOND_SEARCH_PUSH_DISTANCE = 1000.0;

    private static final ResourceKey<Biome> TARGET_BIOME_KEY = ResourceKey.create(
            Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(TalesOfFolklore.MOD_ID, "appalachian_forest")
    );

    public AppalachianMapItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);

        if (pLevel instanceof ServerLevel serverLevel) {
            BlockPos playerPos = pPlayer.blockPosition();
            Optional<BlockPos> targetPos = Optional.empty();

            LOGGER.info("--- AppalachianMapItem USE (Find First, Check Size, Jump if Small) ---");
            LOGGER.info("Player Position: {}", playerPos);
            LOGGER.info("Target Biome Key: {}", TARGET_BIOME_KEY.location());

            // 1. Find the very first/closest biome instance
            LOGGER.debug("Step 1: Finding closest biome instance...");
            Pair<BlockPos, Holder<Biome>> firstBiomeInfo = serverLevel.findClosestBiome3d(
                    holder -> holder.is(TARGET_BIOME_KEY),
                    playerPos,
                    SEARCH_RADIUS,
                    BIOME_SEARCH_RADIUS_VERTICAL,
                    BIOME_SEARCH_INCREMENT_STEP
            );

            if (firstBiomeInfo == null) {
                // Truly no biome found at all
                LOGGER.error("Step 1 Failed: No biome instance found within {} blocks.", SEARCH_RADIUS);
                if (!pLevel.isClientSide) {
                    pPlayer.sendSystemMessage(Component.translatable("item.talesoffolklore.appalachian_map.biome_not_found").withStyle(ChatFormatting.RED));
                }
                return InteractionResultHolder.fail(itemstack);
            }

            BlockPos firstBiomePos = firstBiomeInfo.getFirst();
            LOGGER.info("Step 1 Success: Found first candidate at {}", firstBiomePos);

            // 2. Check the size of the first found biome
            LOGGER.debug("Step 2: Checking size of first candidate at {}", firstBiomePos);
            if (isBiomePatchLargeEnough(serverLevel, firstBiomePos, TARGET_BIOME_KEY)) {
                // First one is large enough, use it!
                LOGGER.info("Step 2 Success: First candidate is large enough. Selecting {}.", firstBiomePos);
                targetPos = Optional.of(firstBiomePos);
            } else {
                // First one is too small. Attempt to find a second one further away.
                LOGGER.warn("Step 2 Failed: First candidate at {} is too small. Attempting second search.", firstBiomePos);

                // Calculate a starting point for the second search, pushed far beyond the first small biome
                Vec3 playerVec = Vec3.atCenterOf(playerPos);
                Vec3 firstBiomeVec = Vec3.atCenterOf(firstBiomePos);
                Vec3 direction = firstBiomeVec.subtract(playerVec).normalize();
                // If player is inside the first biome, direction might be zero, pick a default
                if (direction.lengthSqr() < 0.1) { direction = new Vec3(1, 0, 0); }

                // Start search from a point far beyond the first biome in that direction
                Vec3 secondSearchStartVec = firstBiomeVec.add(direction.scale(SECOND_SEARCH_PUSH_DISTANCE));
                BlockPos secondSearchStartPos = BlockPos.containing(secondSearchStartVec.x, playerPos.getY(), secondSearchStartVec.z);

                LOGGER.debug("Step 3: Starting second search from {}", secondSearchStartPos);
                Pair<BlockPos, Holder<Biome>> secondBiomeInfo = serverLevel.findClosestBiome3d(
                        holder -> holder.is(TARGET_BIOME_KEY),
                        secondSearchStartPos, // Start search FROM here
                        SEARCH_RADIUS,       // Use the same large radius relative to the new start
                        BIOME_SEARCH_RADIUS_VERTICAL,
                        BIOME_SEARCH_INCREMENT_STEP
                );

                if (secondBiomeInfo == null) {
                    // No second biome found even after jumping
                    LOGGER.error("Step 3 Failed: No second biome instance found after jumping past the first small one.");
                    // We don't tell the user "not found" here, because we *did* find the first small one.
                    // What to do? Maybe default to the small one? Or fail? Let's fail for now.
                    if (!pLevel.isClientSide) {
                        pPlayer.sendSystemMessage(Component.translatable("item.talesoffolklore.appalachian_map.biome_not_found").withStyle(ChatFormatting.RED)); // Still use not_found
                    }
                    return InteractionResultHolder.fail(itemstack); // Fail completely if second search yields nothing
                } else {
                    // Found a second biome. Assume this is the one we want, *regardless of its size*.
                    BlockPos secondBiomePos = secondBiomeInfo.getFirst();
                    // Optional: Check if secondBiomePos is significantly different from firstBiomePos
                    if (secondBiomePos.distSqr(firstBiomePos) < 100*100) {
                        LOGGER.warn("Step 3 Warning: Second biome found at {} is very close to the first small one {}. Using it anyway.", secondBiomePos, firstBiomePos);
                    } else {
                        LOGGER.info("Step 3 Success: Found second candidate at {}. Selecting this position.", secondBiomePos);
                    }
                    targetPos = Optional.of(secondBiomePos);
                }
            }

            // 3. Process the final target position (if found)
            if (targetPos.isPresent()) {
                BlockPos finalPos = targetPos.get();
                BlockPos surfacePos = serverLevel.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, finalPos);
                LOGGER.info("Final Step: Creating map centered near {}", surfacePos);

                ItemStack mapStack = MapItem.create(serverLevel, surfacePos.getX(), surfacePos.getZ(), (byte) 2, true, true);
                mapStack.setHoverName(Component.translatable("item.talesoffolklore.appalachian_map.revealed_title").withStyle(ChatFormatting.DARK_GREEN));

                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                pPlayer.awardStat(Stats.ITEM_USED.get(this));
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
                // Should only be reached if the first search failed, or the second search failed after the first was small.
                LOGGER.error("Final Step: No suitable target position was determined.");
                if (!pLevel.isClientSide) {
                    pPlayer.sendSystemMessage(Component.translatable("item.talesoffolklore.appalachian_map.biome_not_found").withStyle(ChatFormatting.RED));
                }
                return InteractionResultHolder.fail(itemstack);
            }
        }
        return InteractionResultHolder.pass(itemstack);
    }

    // isBiomePatchLargeEnough remains the same
    private boolean isBiomePatchLargeEnough(ServerLevel level, BlockPos centerPos, ResourceKey<Biome> targetKey) {
        if (!level.getBiome(centerPos).is(targetKey)) {
            LOGGER.warn("isBiomePatchLargeEnough check called on pos {} which is not the target biome {}!", centerPos, targetKey.location());
            return false;
        }
        int matchCount = 0;
        BlockPos[] checkOffsets = { /* ... same 8 offsets ... */
                new BlockPos(SIZE_CHECK_DISTANCE, 0, 0),   // E
                new BlockPos(-SIZE_CHECK_DISTANCE, 0, 0),  // W
                new BlockPos(0, 0, SIZE_CHECK_DISTANCE),   // S
                new BlockPos(0, 0, -SIZE_CHECK_DISTANCE),  // N
                new BlockPos(SIZE_CHECK_DISTANCE, 0, -SIZE_CHECK_DISTANCE),  // NE
                new BlockPos(-SIZE_CHECK_DISTANCE, 0, -SIZE_CHECK_DISTANCE), // NW
                new BlockPos(SIZE_CHECK_DISTANCE, 0, SIZE_CHECK_DISTANCE),   // SE
                new BlockPos(-SIZE_CHECK_DISTANCE, 0, SIZE_CHECK_DISTANCE)   // SW
        };
        for (BlockPos offset : checkOffsets) {
            BlockPos checkPosXZ = centerPos.offset(offset);
            BlockPos surfaceCheckPos = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, checkPosXZ);
            if (level.getBiome(surfaceCheckPos).is(targetKey)) {
                matchCount++;
            }
        }
        boolean largeEnough = matchCount >= MIN_REQUIRED_SIZE_CHECKS;
        LOGGER.debug("Size check around {}: {} out of 8 points matched (required {}). Large enough: {}", centerPos, matchCount, MIN_REQUIRED_SIZE_CHECKS, largeEnough);
        return largeEnough;
    }
}