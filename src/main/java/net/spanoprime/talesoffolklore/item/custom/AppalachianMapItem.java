package net.spanoprime.talesoffolklore.item.custom;

import com.mojang.datafixers.util.Pair;
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
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.spanoprime.talesoffolklore.TalesOfFolklore;

import java.util.List;
import java.util.Optional;

public class AppalachianMapItem extends Item { // Nota: è meglio estendere Item, non MapItem direttamente per questo scopo
    public static int coordX = 0;
    public static int coordZ = 0;
    // Definiamo una chiave per il nostro bioma. È più sicuro che usare riferimenti diretti.
    // Assicurati che il nome "appalachian_forest" corrisponda a quello che hai registrato.
    public static final ResourceKey<Biome> TARGET_BIOME = ResourceKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath(TalesOfFolklore.MOD_ID, "appalachian_forest"));

    public AppalachianMapItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pUsedHand);

        // La logica deve essere eseguita solo sul server
        if (pLevel instanceof ServerLevel serverLevel) {

            // Troviamo il bioma più vicino al giocatore

            // Controlliamo se il bioma è stato trovato

            // Creiamo la mappa che punta alle coordinate del bioma trovato
            ItemStack mapStack = MapItem.create(serverLevel, coordX, coordZ, (byte) 2, true, true);

            MapItem.renderBiomePreviewMap(serverLevel, mapStack);
            mapStack.setHoverName(Component.translatable("item.talesoffolklore.appalachian_map.revealed_title").withStyle(ChatFormatting.DARK_GREEN));

            MapItemSavedData mapData = MapItem.getSavedData(mapStack, serverLevel);

            // Consumiamo l'oggetto originale (se non in creative)
            if (!pPlayer.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            pPlayer.awardStat(Stats.ITEM_USED.get(this));
            pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.PLAYERS, 1.0F, 1.0F);

            // Diamo la nuova mappa al giocatore
            return InteractionResultHolder.success(mapStack);
        }
        return InteractionResultHolder.pass(itemstack);
    }
}