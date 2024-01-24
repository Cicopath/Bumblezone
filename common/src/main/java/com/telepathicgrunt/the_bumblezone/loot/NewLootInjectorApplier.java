package com.telepathicgrunt.the_bumblezone.loot;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.configs.BzGeneralConfigs;
import com.telepathicgrunt.the_bumblezone.mixin.loot.LootContextAccessor;
import com.telepathicgrunt.the_bumblezone.modinit.BzDimension;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

public final class NewLootInjectorApplier {
    private NewLootInjectorApplier() {}

    public static final ResourceLocation VANILLA_FISHING_LOOT_TABLE_RL = new ResourceLocation("minecraft:gameplay/fishing");
    public static final ResourceLocation BZ_DIMENSION_FISHING_LOOT_TABLE_RL = new ResourceLocation(Bumblezone.MODID, "gameplay/fishing");
    public static final ResourceLocation STINGER_DROP_LOOT_TABLE_RL = new ResourceLocation(Bumblezone.MODID, "entities/bee_stinger_drops");

    public static boolean checkIfInjectBeeStingerLoot(LootContext context) {
        if (BzGeneralConfigs.beeLootInjection || BzGeneralConfigs.moddedBeeLootInjection) {
            if(context.hasParam(LootContextParams.THIS_ENTITY)) {
                if (context.getParam(LootContextParams.THIS_ENTITY) instanceof Bee bee) {
                    if (!((LootParamsBzVisitedLootInterface)((LootContextAccessor)context).getParams()).getVisitedBzVisitedLootRL().contains(STINGER_DROP_LOOT_TABLE_RL) &&
                        !((EntityLootDropInterface)bee).thebumblezone_hasPerformedEntityDrops())
                    {
                        ResourceLocation beeRL = BuiltInRegistries.ENTITY_TYPE.getKey(bee.getType());
                        return (BzGeneralConfigs.beeLootInjection && beeRL.getNamespace().equals("minecraft")) ||
                                (BzGeneralConfigs.moddedBeeLootInjection && !beeRL.getNamespace().equals("minecraft"));
                    }
                }
            }
        }

        return false;
    }

    public static boolean checkIfValidForDimensionFishingLoot(LootContext context) {
        if (!((LootParamsBzVisitedLootInterface)((LootContextAccessor)context).getParams()).getVisitedBzVisitedLootRL().contains(NewLootInjectorApplier.BZ_DIMENSION_FISHING_LOOT_TABLE_RL)) {
            return context.getLevel().dimension().equals(BzDimension.BZ_WORLD_KEY);
        }

        return false;
    }

    public static void injectLoot(LootContext context, List<ItemStack> originalLoot, ResourceLocation lootTableToPullFrom) {
        LootTable stingerLootTable = context.getLevel().getServer().getLootData().getLootTable(lootTableToPullFrom);
        ((LootParamsBzVisitedLootInterface)((LootContextAccessor)context).getParams()).addVisitedBzVisitedLootRL(lootTableToPullFrom);
        ObjectArrayList<ItemStack> newItems = new ObjectArrayList<>();
        stingerLootTable.getRandomItems(((LootContextAccessor)context).getParams(), newItems::add);
        originalLoot.addAll(newItems);

        if(context.hasParam(LootContextParams.THIS_ENTITY)) {
            Entity entity = context.getParam(LootContextParams.THIS_ENTITY);
            if (entity != null) {
                ((EntityLootDropInterface)entity).thebumblezone_performedEntityDrops();
            }
        }
    }
}
