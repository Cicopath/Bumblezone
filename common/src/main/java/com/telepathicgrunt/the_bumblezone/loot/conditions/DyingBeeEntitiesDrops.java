package com.telepathicgrunt.the_bumblezone.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.telepathicgrunt.the_bumblezone.modinit.BzLootConditionTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class DyingBeeEntitiesDrops implements LootItemCondition {
    static final DyingBeeEntitiesDrops INSTANCE = new DyingBeeEntitiesDrops();

    public DyingBeeEntitiesDrops() {
        super();
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        return entity instanceof Bee bee && bee.isDeadOrDying() && !bee.hasStung();
    }

    @Override
    public LootItemConditionType getType() {
        return BzLootConditionTypes.DYING_BEE_ENTITY_DROPS.get();
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<DyingBeeEntitiesDrops> {
        @Override
        public void serialize(JsonObject jsonObject, DyingBeeEntitiesDrops object, JsonSerializationContext jsonSerializationContext) {}

        @Override
        public DyingBeeEntitiesDrops deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return INSTANCE;
        }
    }
}