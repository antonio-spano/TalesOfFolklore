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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3; // Needed for vector math
import net.spanoprime.talesoffolklore.TalesOfFolklore;
import org.slf4j.Logger;

import java.util.Optional; // Still needed for clarity

public class AppalachianMapItem extends MapItem {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static int coordX = 0;
    public static int coordZ = 0;

    public AppalachianMapItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);

        if (pLevel instanceof ServerLevel serverLevel) {

            ItemStack mapStack = MapItem.create(serverLevel, coordX, coordZ, (byte) 2, true, true);
            mapStack.setHoverName(Component.translatable("item.talesoffolklore.appalachian_map.revealed_title").withStyle(ChatFormatting.DARK_GREEN));

            MapItem.renderBiomePreviewMap(serverLevel, mapStack);

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
        }
        return InteractionResultHolder.pass(itemstack);
    }
}