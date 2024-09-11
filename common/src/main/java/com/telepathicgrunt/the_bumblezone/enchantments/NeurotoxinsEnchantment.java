package com.telepathicgrunt.the_bumblezone.enchantments;

import com.telepathicgrunt.the_bumblezone.configs.BzGeneralConfigs;
import com.telepathicgrunt.the_bumblezone.entities.nonliving.ThrownStingerSpearEntity;
import com.telepathicgrunt.the_bumblezone.events.entity.EntityAttackedEvent;
import com.telepathicgrunt.the_bumblezone.mixin.entities.MobAccessor;
import com.telepathicgrunt.the_bumblezone.mixin.entities.ThrownTridentAccessor;
import com.telepathicgrunt.the_bumblezone.modinit.BzCriterias;
import com.telepathicgrunt.the_bumblezone.modinit.BzEffects;
import com.telepathicgrunt.the_bumblezone.modinit.BzEnchantments;
import com.telepathicgrunt.the_bumblezone.modinit.BzItems;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import com.telepathicgrunt.the_bumblezone.modules.LivingEntityDataModule;
import com.telepathicgrunt.the_bumblezone.modules.base.ModuleHelper;
import com.telepathicgrunt.the_bumblezone.modules.registry.ModuleRegistry;
import com.telepathicgrunt.the_bumblezone.platform.BzEnchantment;
import com.telepathicgrunt.the_bumblezone.utils.OptionalBoolean;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Optional;

public class NeurotoxinsEnchantment extends BzEnchantment {

    public NeurotoxinsEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.TRIDENT, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinCost(int level) {
        if (level > BzGeneralConfigs.neurotoxinMaxLevel) {
            return 201;
        }

        return 14 * level;
    }

    @Override
    public int getMaxCost(int level) {
        if (level > BzGeneralConfigs.neurotoxinMaxLevel) {
            return 200;
        }

        return super.getMinCost(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        return BzGeneralConfigs.neurotoxinMaxLevel;
    }

    public static void entityHurtEvent(EntityAttackedEvent event) {
        if(event.entity().level().isClientSide()) {
            return;
        }

        ItemStack attackingItem = null;
        LivingEntity attacker = null;
        if(event.source().getEntity() instanceof LivingEntity livingEntity) {
            attacker = livingEntity;
            attackingItem = attacker.getMainHandItem();
        }

        if(event.source().is(DamageTypeTags.IS_PROJECTILE)) {
           Entity projectile = event.source().getDirectEntity();
           if(projectile instanceof ThrownTrident thrownTrident) {
               attackingItem = ((ThrownTridentAccessor)thrownTrident).getTridentItem();
           }
           else if (projectile instanceof ThrownStingerSpearEntity thrownStingerSpearEntity) {
               attackingItem = thrownStingerSpearEntity.getSpearItemStack();
           }
        }

        if(attackingItem != null && !attackingItem.isEmpty()) {
            applyNeurotoxins(attacker, event.entity(), attackingItem);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void applyNeurotoxins(Entity attacker, Entity victim, ItemStack itemStack) {
        int level = EnchantmentHelper.getItemEnchantmentLevel(BzEnchantments.NEUROTOXINS.get(), itemStack);
        level = Math.min(level, BzGeneralConfigs.neurotoxinMaxLevel);

        if(level > 0 && victim instanceof LivingEntity livingEntity && livingEntity.getMobType() != MobType.UNDEAD) {
            if (livingEntity.hasEffect(BzEffects.PARALYZED.get())) {
                return;
            }

            float applyChance = 1.0f;
            LivingEntityDataModule capability = null;

            if(attacker != null) {
                Optional<LivingEntityDataModule> capOptional = ModuleHelper.getModule(attacker, ModuleRegistry.LIVING_ENTITY_DATA);
                if (capOptional.isPresent()) {
                    capability = capOptional.orElseThrow(RuntimeException::new);
                    float healthModifier = Math.max(100 - livingEntity.getHealth(), 10) / 100f;
                    applyChance = (healthModifier * level) * (capability.getMissedParalysis() + 1);
                }
            }

            if(livingEntity.getRandom().nextFloat() < applyChance) {
                livingEntity.addEffect(new MobEffectInstance(
                        BzEffects.PARALYZED.get(),
                        Math.min(100 * level, BzGeneralConfigs.paralyzedMaxTickDuration),
                        level,
                        false,
                        true,
                        true));

                if (attacker instanceof LivingEntity livingAttacker && livingEntity instanceof Mob mob) {
                    mob.setLastHurtByMob(livingAttacker);
                    ((MobAccessor)mob).getTargetSelector().tick();
                }

                if (itemStack.is(BzItems.STINGER_SPEAR.get()) && attacker instanceof ServerPlayer serverPlayer) {
                    BzCriterias.STINGER_SPEAR_PARALYZING_TRIGGER.trigger(serverPlayer);

                    if (livingEntity.getHealth() > 70) {
                        BzCriterias.STINGER_SPEAR_PARALYZE_BOSS_TRIGGER.trigger(serverPlayer);
                    }
                }

                if(capability != null) {
                    capability.setMissedParalysis(0);
                }
            }
            else {
                if(capability != null) {
                    capability.setMissedParalysis(capability.getMissedParalysis() + 1);
                }
            }
        }
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.is(BzTags.ENCHANTABLE_NEUROTOXINS) || stack.is(Items.BOOK);
    }

    @Override
    public OptionalBoolean bz$canApplyAtEnchantingTable(ItemStack stack) {
        return OptionalBoolean.of(this.canEnchant(stack));
    }
}
