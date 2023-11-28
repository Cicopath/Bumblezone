package com.telepathicgrunt.the_bumblezone.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.telepathicgrunt.the_bumblezone.modinit.BzDimension;
import com.telepathicgrunt.the_bumblezone.modinit.BzLootConditionTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class BumblezoneDimensionCheck implements LootItemCondition {
    static final BumblezoneDimensionCheck INSTANCE = new BumblezoneDimensionCheck();

    public BumblezoneDimensionCheck() {
        super();
    }

    @Override
    public boolean test(LootContext lootContext) {
        Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        return entity != null && entity.level().dimension().equals(BzDimension.BZ_WORLD_KEY);
    }

    @Override
    public LootItemConditionType getType() {
        return BzLootConditionTypes.BUMBLEZONE_DIMENSION_CHECK.get();
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<BumblezoneDimensionCheck> {
        @Override
        public void serialize(JsonObject jsonObject, BumblezoneDimensionCheck object, JsonSerializationContext jsonSerializationContext) {}

        @Override
        public BumblezoneDimensionCheck deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            return INSTANCE;
        }
    }
}