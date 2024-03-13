package com.telepathicgrunt.the_bumblezone.modules;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.configs.BzGeneralConfigs;
import com.telepathicgrunt.the_bumblezone.modules.base.Module;
import com.telepathicgrunt.the_bumblezone.modules.base.ModuleSerializer;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public class PlayerDataModule implements Module<PlayerDataModule> {

    public static final ModuleSerializer<PlayerDataModule> SERIALIZER = new Serializer();

    public boolean isBeeEssenced = false;
    public boolean gottenWelcomed = false;
    public boolean gottenWelcomedInDimension = false;
    public boolean receivedEssencePrize = false;
    public long tradeResetPrimedTime = -1000;
    public int craftedBeehives = 0;
    public int beesBred = 0;
    public int flowersSpawned = 0;
    public int honeyBottleDrank = 0;
    public int beeStingersFired = 0;
    public int beeSaved = 0;
    public int pollenPuffHits = 0;
    public int honeySlimeBred = 0;
    public int beesFed = 0;
    public int queenBeeTrade = 0;
    public final Map<ResourceLocation, Integer> mobsKilledTracker = new Object2IntOpenHashMap<>();

    public void resetAllTrackerStats() {
        receivedEssencePrize = false;
        tradeResetPrimedTime = -1000;
        craftedBeehives = 0;
        beesBred = 0;
        flowersSpawned = 0;
        honeyBottleDrank = 0;
        beeStingersFired = 0;
        beeSaved = 0;
        pollenPuffHits = 0;
        honeySlimeBred = 0;
        beesFed = 0;
        queenBeeTrade = 0;
        mobsKilledTracker.clear();
    }

    @Override
    public ModuleSerializer<PlayerDataModule> serializer() {
        return SERIALIZER;
    }

    private static final class Serializer implements ModuleSerializer<PlayerDataModule> {

        @Override
        public Class<PlayerDataModule> moduleClass() {
            return PlayerDataModule.class;
        }

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Bumblezone.MODID, "player_data");
        }

        @Override
        public void read(PlayerDataModule module, CompoundTag tag) {
            module.mobsKilledTracker.clear();
            module.isBeeEssenced = tag.getBoolean("is_bee_essenced");
            module.gottenWelcomed = tag.getBoolean("gotten_welcomed");
            module.gottenWelcomedInDimension = tag.getBoolean("gotten_welcomed_in_dimension");
            module.receivedEssencePrize = tag.getBoolean("received_essence_prize");
            module.tradeResetPrimedTime = tag.getLong("trade_reset_primed_time");
            module.craftedBeehives = tag.getInt("crafted_beehives");
            module.beesBred = tag.getInt("bees_bred");
            module.flowersSpawned = tag.getInt("flowers_spawned");
            module.honeyBottleDrank = tag.getInt("honey_bottle_drank");
            module.beeStingersFired = tag.getInt("bee_stingers_fired");
            module.beeSaved = tag.getInt("bee_saved");
            module.pollenPuffHits = tag.getInt("pollen_puff_hits");
            module.honeySlimeBred = tag.getInt("honey_slime_bred");
            module.beesFed = tag.getInt("bees_fed");
            module.queenBeeTrade = tag.getInt("queen_bee_trade");

            ListTag mapList = tag.getList("mobs_killed_tracker", Tag.TAG_COMPOUND);
            for (int i = 0; i < mapList.size(); i++) {
                CompoundTag compoundTag = mapList.getCompound(i);
                module.mobsKilledTracker.put(
                        new ResourceLocation(compoundTag.getString("id")),
                        compoundTag.getInt("count")
                );
            }
        }

        @Override
        public void write(CompoundTag tag, PlayerDataModule module) {
            tag.putBoolean("is_bee_essenced", module.isBeeEssenced);
            tag.putBoolean("gotten_welcomed", module.gottenWelcomed);
            tag.putBoolean("gotten_welcomed_in_dimension", module.gottenWelcomedInDimension);
            tag.putBoolean("received_essence_prize", module.receivedEssencePrize);
            tag.putLong("trade_reset_primed_time", module.tradeResetPrimedTime);
            tag.putInt("crafted_beehives", module.craftedBeehives);
            tag.putInt("bees_bred", module.beesBred);
            tag.putInt("flowers_spawned", module.flowersSpawned);
            tag.putInt("honey_bottle_drank", module.honeyBottleDrank);
            tag.putInt("bee_stingers_fired", module.beeStingersFired);
            tag.putInt("bee_saved", module.beeSaved);
            tag.putInt("pollen_puff_hits", module.pollenPuffHits);
            tag.putInt("honey_slime_bred", module.honeySlimeBred);
            tag.putInt("bees_fed", module.beesFed);
            tag.putInt("queen_bee_trade", module.queenBeeTrade);

            ListTag mapList = new ListTag();
            for (Map.Entry<ResourceLocation, Integer> entry : module.mobsKilledTracker.entrySet()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putString("id", entry.getKey().toString());
                compoundTag.putInt("count", entry.getValue());
                mapList.add(compoundTag);
            }
            tag.put("mobs_killed_tracker", mapList);
        }

        @Override
        public void onPlayerCopy(PlayerDataModule oldModule, PlayerDataModule thisModule, ServerPlayer player, boolean isPersistent) {
            if (isPersistent) {
                ModuleSerializer.super.onPlayerCopy(oldModule, thisModule, player, true);
            }
            else if (BzGeneralConfigs.keepEssenceOfTheBeesOnRespawning) {
                thisModule.isBeeEssenced = oldModule.isBeeEssenced;
            }
            else {
                thisModule.isBeeEssenced = false;
                Component message = Component.translatable("system.the_bumblezone.lost_bee_essence").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.RED);
                player.displayClientMessage(message, true);
            }

            thisModule.gottenWelcomed = oldModule.gottenWelcomed;
            thisModule.gottenWelcomedInDimension = oldModule.gottenWelcomedInDimension;
        }
    }
}
