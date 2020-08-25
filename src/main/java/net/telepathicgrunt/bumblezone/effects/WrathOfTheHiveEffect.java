package net.telepathicgrunt.bumblezone.effects;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;
import net.telepathicgrunt.bumblezone.Bumblezone;
import net.telepathicgrunt.bumblezone.entities.BeeAggression;

import java.util.List;


public class WrathOfTheHiveEffect extends StatusEffect {
    private final static TargetPredicate SEE_THROUGH_WALLS = (new TargetPredicate()).includeHidden();
    private final static TargetPredicate LINE_OF_SIGHT = (new TargetPredicate());
    public static boolean ACTIVE_WRATH = false;

    public WrathOfTheHiveEffect(StatusEffectType type, int potionColor) {
        super(type, potionColor);
    }

    /**
     * Returns true if the potion has an instant effect instead of a continuous one (eg Harming)
     */
    public boolean isInstant() {
        return true;
    }

    /**
     * checks if Potion effect is ready to be applied this tick.
     */
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration >= 1;
    }

    /**
     * Makes the bees swarm at the entity
     */
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        //Maximum aggression
        if (amplifier >= 2) {
            unBEElievablyHighAggression(entity.world, entity);
        }
        //Anything lower than 2 is medium aggression
        else {
            mediumAggression(entity.world, entity);
        }
    }

    /**
     * Bees are angry but not crazy angry
     */
    public static void mediumAggression(World world, LivingEntity livingEntity) {
        LINE_OF_SIGHT.setBaseMaxDistance(Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.aggressionTriggerRadius).includeHidden();
        List<BeeEntity> beeList = world.getTargets(BeeEntity.class, LINE_OF_SIGHT, livingEntity, livingEntity.getBoundingBox().expand(Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.aggressionTriggerRadius));

        for (BeeEntity bee : beeList) {
            bee.setTarget(livingEntity);

            // weaker potion effects for when attacking bears
            bee.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20, Math.max(Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.speedBoostLevel, 1), false, false));
            bee.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20, Math.max(Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.absorptionBoostLevel / 2, 1), false, false));
            bee.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20, Math.max(Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.strengthBoostLevel / 3, 1), false, true));
        }
    }


    /**
     * Bees are REALLY angry!!! HIGH TAIL IT OUTTA THERE BRUH!!!
     */
    public static void unBEElievablyHighAggression(World world, LivingEntity livingEntity) {
        SEE_THROUGH_WALLS.setBaseMaxDistance(Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.aggressionTriggerRadius);
        List<BeeEntity> beeList = world.getTargets(BeeEntity.class, SEE_THROUGH_WALLS, livingEntity, livingEntity.getBoundingBox().expand(Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.aggressionTriggerRadius));
        for (BeeEntity bee : beeList) {
            bee.setTarget(livingEntity);
            bee.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20, Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.speedBoostLevel, false, false));
            bee.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 20, Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.absorptionBoostLevel, false, false));
            bee.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 20, Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.strengthBoostLevel, false, true));

        }
    }

    /**
     * Calm the bees that are attacking the incoming entity
     */
    public static void calmTheBees(World world, LivingEntity livingEntity)
    {
        SEE_THROUGH_WALLS.setBaseMaxDistance(Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.aggressionTriggerRadius*0.5D);
        List<BeeEntity> beeList = world.getTargets(BeeEntity.class, SEE_THROUGH_WALLS, livingEntity, livingEntity.getBoundingBox().expand(Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.aggressionTriggerRadius*0.5D));
        for (BeeEntity bee : beeList)
        {
            if(bee.getTarget() == livingEntity) {
                bee.setTarget(null);
                bee.setAttacking(false);
                bee.setAngerTime(0);
                bee.removeStatusEffect(StatusEffects.STRENGTH);
                bee.removeStatusEffect(StatusEffects.SPEED);
                bee.removeStatusEffect(StatusEffects.ABSORPTION);
            }
        }
    }

    // Don't remove wrath effect from mobs that bees are to always be angry at (bears, non-bee insects)
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        if(BeeAggression.doesBeesHateEntity(entity)){
            //refresh the bee anger timer
            entity.addStatusEffect(new StatusEffectInstance(
                    BzEffects.WRATH_OF_THE_HIVE,
                    Bumblezone.BZ_CONFIG.BZBeeAggressionConfig.howLongWrathOfTheHiveLasts,
                    1,
                    false,
                    true));
        }
        else{
            // remove the effect like normal
            super.onRemoved(entity, attributes, amplifier);
        }
    }
}
