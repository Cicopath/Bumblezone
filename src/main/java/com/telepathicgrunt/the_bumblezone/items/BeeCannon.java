package com.telepathicgrunt.the_bumblezone.items;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.modinit.BzCriterias;
import com.telepathicgrunt.the_bumblezone.modinit.BzEffects;
import com.telepathicgrunt.the_bumblezone.modinit.BzEntities;
import com.telepathicgrunt.the_bumblezone.modinit.BzItems;
import com.telepathicgrunt.the_bumblezone.modinit.BzSounds;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BeeCannon extends Item implements Vanishable {
    public static final String TAG_BEES = "BeesStored";

    public BeeCannon(Properties properties) {
        super(properties.durability(50));
    }

    @Override
    public void releaseUsing(ItemStack beeCannon, Level level, LivingEntity livingEntity, int currentDuration) {
        if (!level.isClientSide() && livingEntity instanceof Player player) {
            ItemStack mutableBeeCannon = player.getItemInHand(InteractionHand.MAIN_HAND);

            int numberOfBees = getNumberOfBees(mutableBeeCannon);
            int remainingDuration = this.getUseDuration(mutableBeeCannon) - currentDuration;
            if (remainingDuration >= 10 && numberOfBees > 0) {
                List<Entity> bees = tryReleaseBees(level, mutableBeeCannon);
                bees.forEach(bee -> {
                    Vec3 playerEyePos = new Vec3(
                            player.getX(),
                            player.getEyeY() - 0.25f,
                            player.getZ());
                    bee.moveTo(
                            playerEyePos.x(),
                            playerEyePos.y() - 0.5d,
                            playerEyePos.z(),
                            player.getYRot(),
                            player.getXRot());
                    bee.setDeltaMovement(player.getLookAngle().multiply(2.5d, 2.5d, 2.5d));
                    level.addFreshEntity(bee);

                    if(bee instanceof NeutralMob) {
                        float maxDistance = 15;
                        Vec3 maxDistanceDirection = player.getLookAngle().multiply(maxDistance, maxDistance, maxDistance);
                        Vec3 finalPos = playerEyePos.add(maxDistanceDirection);

                        HitResult hitResult = level.clip(new ClipContext(
                                playerEyePos,
                                finalPos,
                                ClipContext.Block.COLLIDER,
                                ClipContext.Fluid.NONE,
                                player));
                        if (hitResult.getType() != HitResult.Type.MISS) {
                            finalPos = hitResult.getLocation();
                        }

                        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
                                level,
                                player,
                                playerEyePos,
                                finalPos,
                                player.getBoundingBox().expandTowards(maxDistanceDirection),
                                entity -> !entity.isInvisibleTo(player),
                                1);

                        if(entityHitResult != null &&
                            entityHitResult.getType() != HitResult.Type.MISS &&
                            entityHitResult.getEntity() instanceof LivingEntity targetEntity
                            && !(targetEntity instanceof Bee))
                        {
                            ((NeutralMob)bee).setRemainingPersistentAngerTime(60);
                            ((NeutralMob)bee).setPersistentAngerTarget(targetEntity.getUUID());
                            if(bee instanceof Bee trueBee) {
                                trueBee.setTarget(targetEntity);
                            }
                        }
                    }

                    level.playSound(null, player.blockPosition(), BzSounds.BEE_CANNON_FIRES, SoundSource.PLAYERS, 1.0F, (level.getRandom().nextFloat() * 0.2F) + 0.6F);
                    mutableBeeCannon.hurtAndBreak(1, player, playerEntity -> playerEntity.broadcastBreakEvent(EquipmentSlot.MAINHAND));

                    if(numberOfBees >= 3 && player instanceof ServerPlayer serverPlayer) {
                        BzCriterias.BEE_CANNON_FULL_TRIGGER.trigger(serverPlayer);
                    }
                });
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack beeCannon = player.getItemInHand(interactionHand);
        if (getNumberOfBees(beeCannon) == 0) {
            return InteractionResultHolder.fail(beeCannon);
        }
        else {
            player.startUsingItem(interactionHand);
            return InteractionResultHolder.consume(beeCannon);
        }
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack beeCannon, Player playerEntity, LivingEntity entity, InteractionHand playerHand) {
        if(playerEntity.level.isClientSide() ||
            !(entity instanceof Bee bee) ||
            bee.isAngry() ||
            bee.getType().is(BzTags.BLACKLISTED_BEE_CANNON_BEES))
        {
            return InteractionResult.PASS;
        }

        ItemStack mutableBeeCannon = playerEntity.getItemInHand(playerHand);
        boolean addedBee = tryAddBee(mutableBeeCannon, entity);
        if(addedBee) {
            playerEntity.swing(playerHand, true);
            return InteractionResult.SUCCESS;
        }
        else {
            return InteractionResult.PASS;
        }
    }

    public static List<Entity> tryReleaseBees(Level level, ItemStack beeCannonItem) {
        if(getNumberOfBees(beeCannonItem) > 0) {
            CompoundTag tag = beeCannonItem.getOrCreateTag();
            ListTag beeList = tag.getList(TAG_BEES, ListTag.TAG_COMPOUND);
            List<Entity> releasedBees = new ObjectArrayList<>();
            for(int i = beeList.size() - 1; i >= 0; i--) {
                CompoundTag beeTag = beeList.getCompound(0);
                beeList.remove(0);
                releasedBees.add(EntityType.loadEntityRecursive(beeTag, level, entityx -> entityx));
            }
            return releasedBees;
        }
        return new ObjectArrayList<>();
    }

    public static boolean tryAddBee(ItemStack beeCannonItem, Entity bee) {
        if(getNumberOfBees(beeCannonItem) < 3) {
            CompoundTag cannonTag = beeCannonItem.getOrCreateTag();
            ListTag beeList = cannonTag.getList(TAG_BEES, ListTag.TAG_COMPOUND);
            CompoundTag beeTag = new CompoundTag();

            bee.save(beeTag);
            bee.stopRiding();
            bee.ejectPassengers();

            beeTag.remove("UUID");
            beeList.add(beeTag);

            bee.discard();
            return true;
        }
        return false;
    }

    public static int getNumberOfBees(ItemStack beeCannonItem) {
        if(beeCannonItem.hasTag()) {
            CompoundTag tag = beeCannonItem.getTag();
            if(tag.contains(TAG_BEES)) {
                ListTag beeList = tag.getList(TAG_BEES, ListTag.TAG_COMPOUND);
                return beeList.size();
            }
            else {
                ListTag listTag = new ListTag();
                tag.put(TAG_BEES, listTag);
            }
        }
        return 0;
    }

    /**
     * Return whether this item is repairable in an anvil.
     */
    @Override
    public boolean isValidRepairItem(ItemStack toRepair, ItemStack repair) {
        return repair.is(BzTags.BEE_CANNON_REPAIR_ITEMS);
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BOW;
    }
}
