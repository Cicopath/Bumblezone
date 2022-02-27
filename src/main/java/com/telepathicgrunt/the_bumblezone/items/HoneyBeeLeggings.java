package com.telepathicgrunt.the_bumblezone.items;

import com.telepathicgrunt.the_bumblezone.blocks.PileOfPollen;
import com.telepathicgrunt.the_bumblezone.mixin.effects.MobEffectInstanceAccessor;
import com.telepathicgrunt.the_bumblezone.modinit.BzBlocks;
import com.telepathicgrunt.the_bumblezone.modinit.BzCriterias;
import com.telepathicgrunt.the_bumblezone.modinit.BzItems;
import com.telepathicgrunt.the_bumblezone.modinit.BzParticles;
import com.telepathicgrunt.the_bumblezone.tags.BzItemTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Random;


public class HoneyBeeLeggings extends BeeArmor {

    public HoneyBeeLeggings(ArmorMaterial material, EquipmentSlot slot, Properties properties, int variant) {
        super(material, slot, properties, variant, false);
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.is(BzItemTags.BEE_ARMOR_REPAIR_ITEMS);
    }

    @Override
    public void onArmorTick(ItemStack itemstack, Level world, Player entity) {
        Random random = world.random;
        boolean isPollinated = isPollinated(itemstack);
        boolean isSprinting = entity.isSprinting();
        boolean isAllBeeArmorOn = StinglessBeeHelmet.isAllBeeArmorOn(entity);

        if(!world.isClientSide()) {
            if(entity.isCrouching() && isPollinated) {
                removeAndSpawnPollen(world, entity.position(), itemstack);
                if(!world.isClientSide() && world.random.nextFloat() < 0.1f) {
                    itemstack.hurtAndBreak(1, entity, (playerEntity) -> {});
                }
            }
            else if(!entity.isCrouching() && !isPollinated && isSprinting) {
                BlockState withinBlock = world.getBlockState(entity.blockPosition());
                if(withinBlock.is(BzBlocks.PILE_OF_POLLEN)) {
                    setPollinated(itemstack);
                    int newLevel = withinBlock.getValue(PileOfPollen.LAYERS) - 1;
                    if(newLevel == 0) {
                        world.setBlock(entity.blockPosition(), Blocks.AIR.defaultBlockState(), 3);
                    }
                    else {
                        world.setBlock(entity.blockPosition(), withinBlock.setValue(PileOfPollen.LAYERS, newLevel), 3);
                    }
                }
                else if(random.nextFloat() < (isAllBeeArmorOn ? 0.01f : 0.003f) && withinBlock.is(BlockTags.FLOWERS)) {
                    setPollinated(itemstack);
                    if(entity instanceof ServerPlayer) {
                        BzCriterias.HONEY_BEE_LEGGINGS_FLOWER_POLLEN_TRIGGER.trigger((ServerPlayer) entity);
                    }
                }
            }

            if(isAllBeeArmorOn) {
                MobEffectInstance slowness = entity.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
                if (slowness != null) {
                    ((MobEffectInstanceAccessor) slowness).callTickDownDuration();
                    if(!world.isClientSide() &&
                        world.random.nextFloat() < 0.004f &&
                        itemstack.getMaxDamage() - itemstack.getDamageValue() > 1)
                    {
                        itemstack.hurtAndBreak(1, entity, (playerEntity) -> {});
                    }
                }
            }
        }

        if(world.isClientSide() && isPollinated && (isSprinting || random.nextFloat() < (isAllBeeArmorOn ? 0.03f : 0.025f))) {
            int particles = isAllBeeArmorOn ? 2 : 1;
            for(int i = 0; i < particles; i++){
                double speedYModifier = isSprinting ? 0.05D : 0.02D;
                double speedXZModifier = isSprinting ? 0.03D : 0.02D;
                double xOffset = (random.nextFloat() * 0.1) - 0.05;
                double yOffset = (random.nextFloat() * 0.1) + 0.25;
                double zOffset = (random.nextFloat() * 0.1) - 0.05;
                Vec3 pos = entity.position();

                world.addParticle(
                        BzParticles.POLLEN,
                        true,
                        pos.x() + xOffset,
                        pos.y() + yOffset,
                        pos.z() + zOffset,
                        random.nextGaussian() * speedXZModifier,
                        ((random.nextGaussian() + 0.25D) * speedYModifier),
                        random.nextGaussian() * speedXZModifier);
            }
        }
    }

    public static ItemStack getEntityBeeLegging(Entity entity) {
        for(ItemStack armor : entity.getArmorSlots()) {
            if(armor.getItem() instanceof HoneyBeeLeggings) {
                return armor;
            }
        }
        return ItemStack.EMPTY;
    }

    public static void setPollinated(ItemStack itemStack) {
        if (itemStack.getItem() instanceof HoneyBeeLeggings) {
            itemStack.getOrCreateTag().putBoolean("pollinated", true);
        }
    }

    public static void clearPollinated(ItemStack itemStack) {
        if (itemStack.getItem() instanceof HoneyBeeLeggings) {
            itemStack.getOrCreateTag().putBoolean("pollinated", false);
        }
    }

    public static boolean isPollinated(ItemStack itemStack) {
        return itemStack.getItem() instanceof HoneyBeeLeggings &&
                itemStack.hasTag() &&
                itemStack.getTag().getBoolean("pollinated");
    }

    public static void removeAndSpawnPollen(Level world, Vec3 position, ItemStack itemStack) {
        if (itemStack.getItem() instanceof HoneyBeeLeggings) {
            PollenPuff.spawnItemstackEntity(world, position.add(new Vec3(0, 0.25f, 0)), new ItemStack(BzItems.POLLEN_PUFF, 1));
            clearPollinated(itemStack);
        }
    }
}