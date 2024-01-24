package com.telepathicgrunt.the_bumblezone.loot;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.configs.BzGeneralConfigs;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Registry;
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

    public static final ResourceLocation STINGER_DROP_LOOT_TABLE_RL = new ResourceLocation(Bumblezone.MODID, "entities/bee_stinger_drops");

    public static boolean checkIfInjectLoot(LootContext context) {
        if (BzGeneralConfigs.beeLootInjection.get() || BzGeneralConfigs.moddedBeeLootInjection.get()) {
            if (context.hasParam(LootContextParams.THIS_ENTITY)) {
                if (context.getParam(LootContextParams.THIS_ENTITY) instanceof Bee bee) {
                    if (!((EntityLootDropInterface)bee).thebumblezone_hasPerformedEntityDrops()) {
                        ResourceLocation beeRL = Registry.ENTITY_TYPE.getKey(bee.getType());
                        return (BzGeneralConfigs.beeLootInjection.get() && beeRL.getNamespace().equals("minecraft")) ||
                                (BzGeneralConfigs.moddedBeeLootInjection.get() && !beeRL.getNamespace().equals("minecraft"));
                    }
                }
            }
        }
        return false;
    }

    public static void injectLoot(LootContext context, List<ItemStack> originalLoot) {
        LootTable stingerLootTable = context.getLevel().getServer().getLootTables().get(STINGER_DROP_LOOT_TABLE_RL);
        ObjectArrayList<ItemStack> newItems = new ObjectArrayList<>();
        stingerLootTable.getRandomItems(context, newItems::add);
        originalLoot.addAll(newItems);

        if(context.hasParam(LootContextParams.THIS_ENTITY)) {
            Entity entity = context.getParam(LootContextParams.THIS_ENTITY);
            if (entity != null) {
                ((EntityLootDropInterface)entity).thebumblezone_performedEntityDrops();
            }
        }
    }
}
