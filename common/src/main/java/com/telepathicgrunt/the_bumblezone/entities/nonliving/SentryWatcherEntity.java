package com.telepathicgrunt.the_bumblezone.entities.nonliving;

import com.telepathicgrunt.the_bumblezone.client.rendering.rootmin.RootminPose;
import com.telepathicgrunt.the_bumblezone.entities.BeeAggression;
import com.telepathicgrunt.the_bumblezone.entities.mobs.RootminEntity;
import com.telepathicgrunt.the_bumblezone.items.BeeArmor;
import com.telepathicgrunt.the_bumblezone.items.SentryWatcherSpawnEgg;
import com.telepathicgrunt.the_bumblezone.items.essence.EssenceOfTheBees;
import com.telepathicgrunt.the_bumblezone.mixin.entities.EntityAccessor;
import com.telepathicgrunt.the_bumblezone.modinit.BzDamageSources;
import com.telepathicgrunt.the_bumblezone.modinit.BzEntities;
import com.telepathicgrunt.the_bumblezone.modinit.BzItems;
import com.telepathicgrunt.the_bumblezone.modinit.BzSounds;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import com.telepathicgrunt.the_bumblezone.utils.GeneralUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SentryWatcherEntity extends Entity implements Enemy {
   private static final EntityDataAccessor<Boolean> DATA_ID_ACTIVATED = SynchedEntityData.defineId(SentryWatcherEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_ID_SHAKING = SynchedEntityData.defineId(SentryWatcherEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_ID_NO_AI = SynchedEntityData.defineId(SentryWatcherEntity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Direction> DATA_ID_TARGET_FACING = SynchedEntityData.defineId(SentryWatcherEntity.class, EntityDataSerializers.DIRECTION);
   private static final EntityDataAccessor<Optional<UUID>> DATA_ID_OWNER = SynchedEntityData.defineId(SentryWatcherEntity.class, EntityDataSerializers.OPTIONAL_UUID);

   public float xxa;
   public float zza;
   protected int lerpSteps;
   protected double lerpX;
   protected double lerpY;
   protected double lerpZ;
   protected double lerpYRot;
   protected double lerpXRot;
   private int shakingTime = 0;
   private Direction targetFacing;
   private boolean explosionPrimed = false;
   private boolean prevShaking;
   private int shakeStartTick;
   private Vec3 prevVelocity;
   private Vec3 activatedStart;
   private int lastRightClicked = -100;

   private static final int MAX_CHARGING_DISTANCE = 80;
   private static final int SIGHT_RANGE = 36;
   private static final int UNABLE_TO_DESTROY_TOTAL_BLOCK_HARDNESS = 20;
   private static final float MAX_STEP_UP = 0.75f;
   private static final float ROTATION_SPEED = 1.5f;
   private static final float ACCELERATION_FLUID = 0.95f;
   private static final float ACCELERATION_GRAVITY = 0.9800000190734863F;
   private static final float MAX_SPEED_CAP = 2f;

   public SentryWatcherEntity(Level worldIn) {
      super(BzEntities.SENTRY_WATCHER.get(), worldIn);
      this.setMaxUpStep(MAX_STEP_UP);
      this.noCulling = true;
   }

   public SentryWatcherEntity(EntityType<? extends SentryWatcherEntity> type, Level worldIn) {
      super(type, worldIn);
      this.setMaxUpStep(MAX_STEP_UP);
   }

   public int getShakingTime() {
      return shakingTime;
   }

   public void setShakingTime(int shakingTime) {
      this.shakingTime = shakingTime;
   }

   public Direction getTargetFacing() {
      if (targetFacing == null) {
         targetFacing = this.getDirection();
      }

      return targetFacing;
   }

   public void setTargetFacing(Direction targetFacing) {
      this.targetFacing = targetFacing;
      setTargetFacingForSync();
   }

   @Override
   protected void defineSynchedData() {
      this.entityData.define(DATA_ID_ACTIVATED, false);
      this.entityData.define(DATA_ID_SHAKING, false);
      this.entityData.define(DATA_ID_NO_AI, false);
      this.entityData.define(DATA_ID_TARGET_FACING, this.getTargetFacing());
      this.entityData.define(DATA_ID_OWNER, Optional.empty());
   }

   public boolean hasActivated() {
      return this.entityData.get(DATA_ID_ACTIVATED);
   }

   protected void setHasActivated(boolean activated) {
      this.entityData.set(DATA_ID_ACTIVATED, activated);
   }

   public boolean hasShaking() {
      return this.entityData.get(DATA_ID_SHAKING);
   }

   protected void setHasShaking(boolean isShaking) {
      this.entityData.set(DATA_ID_SHAKING, isShaking);
   }

   public boolean hasNoAI() {
      return this.entityData.get(DATA_ID_NO_AI);
   }

   protected void setNoAI(boolean noAI) {
      this.entityData.set(DATA_ID_NO_AI, noAI);
   }

   public Direction getTargetFacingFromSync() {
      return this.entityData.get(DATA_ID_TARGET_FACING);
   }

   public void setTargetFacingForSync() {
      this.entityData.set(DATA_ID_TARGET_FACING, this.getTargetFacing());
   }

   public Optional<UUID> getOwner() {
      return this.entityData.get(DATA_ID_OWNER);
   }

   public void setOwner(Optional<UUID> owner) {
      this.entityData.set(DATA_ID_OWNER, owner);
   }

   @Override
   public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
      this.refreshDimensions();
      if (this.isInWater() && this.random.nextInt(20) == 0) {
         this.doWaterSplashEffect();
      }
      super.onSyncedDataUpdated(key);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag compoundTag) {
      compoundTag.putBoolean("explosionPrimed", explosionPrimed);
      if (this.activatedStart != null) {
         compoundTag.putDouble("activatedStartX", this.activatedStart.x());
         compoundTag.putDouble("activatedStartZ", this.activatedStart.z());
      }
      compoundTag.putBoolean("activated", this.hasActivated());
      compoundTag.putBoolean("shaking", this.hasShaking());
      compoundTag.putInt("shakingTime", this.getShakingTime());

      String targetFacingName = this.getTargetFacing().getName();
      compoundTag.putString("targetFacing", targetFacingName);

      if (compoundTag.contains("noAi")) {
         compoundTag.putBoolean("noAi", this.hasNoAI());
      }
      else if (compoundTag.contains("noAI")) {
         compoundTag.putBoolean("noAI", this.hasNoAI());
      }
      else {
         compoundTag.putBoolean("NoAI", this.hasNoAI());
      }

      if (this.getOwner().isPresent()) {
         compoundTag.putUUID("owner", this.getOwner().get());
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag compoundTag) {
      this.explosionPrimed = compoundTag.getBoolean("explosionPrimed");
      this.activatedStart = new Vec3(compoundTag.getDouble("activatedStartX"), 0, compoundTag.getDouble("activatedStartZ"));
      this.setHasActivated(compoundTag.getBoolean("activated"));
      this.setHasShaking(compoundTag.getBoolean("shaking"));
      this.setShakingTime(compoundTag.getInt("shakingTime"));
      this.setHasShaking(this.getShakingTime() > 0);

      String targetFacingName = compoundTag.getString("targetFacing");
      Direction targetDirection = Direction.byName(targetFacingName);
      this.setTargetFacing(targetDirection);

      this.setNoAI(compoundTag.getBoolean("NoAI") || compoundTag.getBoolean("noAI") || compoundTag.getBoolean("noAi"));

      if (compoundTag.contains("owner")) {
         this.setOwner(Optional.of(compoundTag.getUUID("owner")));
      }
   }

   @Override
   public void remove(RemovalReason removalReason) {
      super.remove(removalReason);

      if (this.explosionPrimed && !this.level().isClientSide() && removalReason == RemovalReason.KILLED) {
         largeExplosion();
      }
   }

   private void largeExplosion() {
      this.level().explode(this, this.getX(), this.getY(), this.getZ(), 6, Level.ExplosionInteraction.MOB);
      this.level().explode(this, this.getX(), this.getY(), this.getZ(), 9, Level.ExplosionInteraction.MOB);
   }

   @Override
   public boolean isSpectator() {
      return false;
   }

   @Override
   public boolean isPickable() {
      return true;
   }

   @Override
   public ItemStack getPickResult() {
      return BzItems.SENTRY_WATCHER_SPAWN_EGG.get().getDefaultInstance();
   }

   @Override
   public void refreshDimensions() {
      double x = this.getX();
      double y = this.getY();
      double z = this.getZ();
      super.refreshDimensions();
      this.absMoveTo(x, y, z);
   }

   @Override
   public float rotate(Rotation rotation) {
      this.setTargetFacing(rotation.rotate(this.getTargetFacing()));

      float f = Mth.wrapDegrees(this.getYRot());
      return switch (rotation) {
         case CLOCKWISE_180 -> f + 180.0F;
         case COUNTERCLOCKWISE_90 -> f + 270.0F;
         case CLOCKWISE_90 -> f + 90.0F;
         default -> f;
      };
   }

   @Override
   public float mirror(Mirror mirror) {
      this.setTargetFacing(mirror.mirror(this.getTargetFacing()));

      float f = Mth.wrapDegrees(this.getYRot());
      return switch (mirror) {
         case FRONT_BACK -> -f;
         case LEFT_RIGHT -> 180.0F - f;
         default -> f;
      };
   }

   @Override
   public Iterable<ItemStack> getArmorSlots() {
      return new ArrayList<>();
   }

   @Override
   public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {}

   @Override
   public boolean causeFallDamage(float f, float g, DamageSource arg) {
      if (f > 1.5) {
         this.playSound(SoundEvents.GENERIC_BIG_FALL, 1.0f, 0.5f);
         this.playBlockFallSound();
         return true;
      }
      return false;
   }

   protected void playBlockFallSound() {
      if (!this.isSilent()) {
         int i = Mth.floor(this.getX());
         int j = Mth.floor(this.getY() - (double)0.2f);
         int k = Mth.floor(this.getZ());
         BlockPos pos = new BlockPos(i, j, k);
         BlockState blockstate = this.level().getBlockState(pos);
         if (!blockstate.isAir()) {
            SoundType soundtype = blockstate.getSoundType();
            this.playSound(soundtype.getFallSound(), soundtype.getVolume() * 0.5f, soundtype.getPitch() * 0.75f);
         }
      }
   }

   @Override
   public boolean isPushable() {
      return false;
   }

   public float getFluidSpeed() {
      return ACCELERATION_FLUID;
   }

   @Override
   public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> tagKey, double d) {
      if (this.touchingUnloadedChunk()) {
         return false;
      }
      else {
         AABB aABB = this.getBoundingBox().deflate(0.001);
         int i = Mth.floor(aABB.minX);
         int j = Mth.ceil(aABB.maxX);
         int k = Mth.floor(aABB.minY);
         int l = Mth.ceil(aABB.maxY);
         int m = Mth.floor(aABB.minZ);
         int n = Mth.ceil(aABB.maxZ);
         double e = 0.0;
         boolean bl2 = false;
         BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

         for(int p = i; p < j; ++p) {
            for(int q = k; q < l; ++q) {
               for(int r = m; r < n; ++r) {
                  mutableBlockPos.set(p, q, r);
                  FluidState fluidState = this.level().getFluidState(mutableBlockPos);
                  if (fluidState.is(tagKey)) {
                     double f = (float)q + fluidState.getHeight(this.level(), mutableBlockPos);
                     if (f >= aABB.minY) {
                        bl2 = true;
                        e = Math.max(f - aABB.minY, e);
                     }
                  }
               }
            }
         }

         this.fluidHeight.put(tagKey, e);
         return bl2;
      }
   }

   @Override
   public boolean canCollideWith(Entity entity) {
      if (entity instanceof RootminEntity rootminEntity && rootminEntity.getRootminPose() == RootminPose.ENTITY_TO_BLOCK) {
         return false;
      }

      return entity.canBeCollidedWith() && !this.isPassengerOfSameVehicle(entity);
   }

   @Override
   public InteractionResult interact(Player player, InteractionHand interactionHand) {
      if (player != null &&
           interactionHand == InteractionHand.MAIN_HAND &&
           this.getOwner().isPresent() &&
           (this.getOwner().get().equals(player.getUUID()) || this.getOwner().get().equals(SentryWatcherSpawnEgg.DISPENSER_OWNER_UUID)))
      {
         if (this.tickCount - this.lastRightClicked < 40) {
            GeneralUtils.givePlayerItem(player, interactionHand, BzItems.SENTRY_WATCHER_SPAWN_EGG.get().getDefaultInstance(), false, false);
            this.remove(RemovalReason.DISCARDED);
         }
         else {
            this.lastRightClicked = this.tickCount;
            this.setHasShaking(true);
            this.setShakingTime(40);
         }

         return InteractionResult.SUCCESS;
      }

      return InteractionResult.PASS;
   }

   @Override
   public void tick() {
      super.tick();

      if (!this.isRemoved()) {
         this.aiStep();
      }

      if (this.hasActivated() &&
           this.activatedStart != null &&
           Math.abs(this.position().horizontalDistance() - this.activatedStart.horizontalDistance()) > MAX_CHARGING_DISTANCE)
      {
         deactivate();
      }

      this.level().getProfiler().push("rangeChecks");

      while(this.getYRot() - this.yRotO < -180.0F) {
         this.yRotO -= 360.0F;
      }

      while(this.getYRot() - this.yRotO >= 180.0F) {
         this.yRotO += 360.0F;
      }

      while(this.getXRot() - this.xRotO < -180.0F) {
         this.xRotO -= 360.0F;
      }

      while(this.getXRot() - this.xRotO >= 180.0F) {
         this.xRotO += 360.0F;
      }

      this.level().getProfiler().pop();
   }

   public void travel() {
      if (this.isControlledByLocalInstance()) {
         double gravityModifier = 0.08;
         boolean isFalling = this.getDeltaMovement().y <= 0.0;

         double e;
         if (this.isInWater() || this.isInLava()) {
            e = this.getY();
            float speed = this.getFluidSpeed();
            float g = 0.02F;
            float h = 0;
            if (!this.onGround()) {
               h *= 0.5F;
            }

            if (h > 0.0F) {
               speed += (0.54600006F - speed) * h / 3.0F;
               g += (this.getFluidSpeed() - g) * h / 3.0F;
            }

            this.moveRelative(g, Vec3.ZERO);
            this.move(MoverType.SELF, this.getDeltaMovement());
            Vec3 vec32 = this.getDeltaMovement();

            this.setDeltaMovement(vec32.multiply(speed, 0.800000011920929, speed));
            Vec3 vec33 = this.getFluidFallingAdjustedMovement(gravityModifier, isFalling, this.getDeltaMovement());
            this.setDeltaMovement(vec33);
            if (this.horizontalCollision && this.isFree(vec33.x, vec33.y + 0.6000000238418579 - this.getY() + e, vec33.z)) {
               this.setDeltaMovement(vec33.x, 0.30000001192092896, vec33.z);
            }
         }
         else {
            BlockPos blockPos = this.getBlockPosBelowThatAffectsMyMovement();
            Vec3 vec37 = this.handleRelativeFrictionAndCalculateMovement();
            double ySpeed = vec37.y;
            if (this.level().isClientSide && !this.level().hasChunkAt(blockPos)) {
               if (this.getY() > (double)this.level().getMinBuildHeight()) {
                  ySpeed = -0.1;
               }
               else {
                  ySpeed = 0.0;
               }
            }
            else if (!this.isNoGravity()) {
               ySpeed -= gravityModifier;
            }

            this.setDeltaMovement(vec37.x, ySpeed * ACCELERATION_GRAVITY, vec37.z);
         }
      }
      else if (this.level().isClientSide()) {
         if (this.hasActivated() && this.onGround() && (Math.abs(this.getDeltaMovement().x()) > 0.001d || Math.abs(this.getDeltaMovement().z()) > 0.001d)) {
            int particlesToSpawn = (int) (1 + Math.abs(this.getDeltaMovement().x() * 50) + Math.abs(this.getDeltaMovement().z() + 50));
            for (int i = 0; i < particlesToSpawn; i++) {
               this.level().addParticle(ParticleTypes.SMOKE,
                       this.position().x() + random.nextGaussian() * 0.6d,
                       this.position().y() + random.nextGaussian() * 0.1d + 0.2d,
                       this.position().z() + random.nextGaussian() * 0.6d,
                       random.nextGaussian() * 0.01d + 0.01d,
                       random.nextGaussian() * 0.01d + 0.01d,
                       random.nextGaussian() * 0.01d + 0.01d);
            }
         }

         if (this.hasShaking() && (!prevShaking || (this.tickCount - this.shakeStartTick) % 10 == 0)) {
            this.level().playLocalSound(
                    this.blockPosition(),
                    BzSounds.SENTRY_WATCHER_ACTIVATING.get(),
                    SoundSource.NEUTRAL,
                    2.5F,
                    1.0f,
                    false);
         }
         else if (this.hasActivated() && !this.hasShaking() && this.tickCount % 10 == 0) {
            this.level().playLocalSound(
                    this.blockPosition(),
                    BzSounds.SENTRY_WATCHER_MOVING.get(),
                    SoundSource.NEUTRAL,
                    2.0F,
                    1.0f,
                    false);
         }

         if (!this.prevShaking && this.hasShaking()) {
            this.shakeStartTick = this.tickCount;
         }
         this.prevShaking = this.hasShaking();
      }

      double xSpeed = this.getDeltaMovement().x();
      double zSpeed = this.getDeltaMovement().z();
      this.setDeltaMovement(
              GeneralUtils.capBetween(xSpeed, -MAX_SPEED_CAP, MAX_SPEED_CAP),
              this.getDeltaMovement().y(),
              GeneralUtils.capBetween(zSpeed, -MAX_SPEED_CAP, MAX_SPEED_CAP));

      this.pushEntities();
   }

   protected void serverAiStep() {
      if (this.getShakingTime() > 0) {
         //play shake animation
         this.setShakingTime(this.getShakingTime() - 1);

         if (this.getShakingTime() <= 0) {
            this.setHasShaking(false);
         }
      }
      if (this.hasActivated()) {
         if (this.horizontalCollision && this.getDeltaMovement().horizontalDistance() < 0.0001f) {
            deactivate();

            double pastSpeed = this.prevVelocity.horizontalDistance();
            if (pastSpeed > 0.01d) {
               List<Entity> list = this.level().getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));

               for (Entity entity : list) {
                  if (!entity.getType().is(BzTags.SENTRY_WATCHER_CANNOT_DAMAGE)) {
                     entity.hurt(this.level().damageSources().source(BzDamageSources.SENTRY_WATCHER_CRUSHING_TYPE, this), 1);
                     if (entity instanceof LivingEntity livingEntity) {
                        float oldHealth = livingEntity.getHealth();
                        float maxhealth = Math.max(livingEntity.getHealth(), livingEntity.getMaxHealth());
                        double healthToLose = Mth.clampedLerp(maxhealth / 3f, maxhealth - 1, pastSpeed - 0.2d);
                        double possibleNewHealth = oldHealth - healthToLose;
                        double newHealth = Math.max(possibleNewHealth, 1);
                        livingEntity.setHealth((float) newHealth);

                        if (livingEntity instanceof Player player) {
                           double armorDamage = Mth.clampedLerp(1, 8, pastSpeed - 0.2d);
                           player.getInventory().hurtArmor(
                                   this.level().damageSources().source(BzDamageSources.SENTRY_WATCHER_CRUSHING_TYPE, this),
                                   (float) armorDamage,
                                   Inventory.ALL_ARMOR_SLOTS);
                        }
                     }
                  }
               }
            }
         }
         else if (!this.hasNoAI() && this.getShakingTime() <= 0) {
            Vec3 currentVelocity = this.getDeltaMovement();
            double newX = currentVelocity.x();
            double newY = currentVelocity.y();
            double newZ = currentVelocity.z();

            Direction currentDirection = this.getTargetFacing();
            if (currentDirection.getStepX() != 0) {
               newX += (currentDirection.getStepX() / 200f);
               newX *= 1.05;
            }
            else if (currentDirection.getStepZ() != 0) {
               newZ += (currentDirection.getStepZ() / 200f);
               newZ *= 1.05;
            }

            this.setDeltaMovement(newX, newY, newZ);
         }
      }
      else if (!this.hasNoAI() && this.tickCount % 10 == 0 && this.getYRot() == this.getTargetFacing().toYRot()) {
         Vec3 offset = Vec3.atLowerCornerOf(Rotation.CLOCKWISE_90.rotate(this.getTargetFacing()).getNormal()).scale(0.5D);
         if (!scanAndBeginActivationIfEnemyFound(offset)) {
            scanAndBeginActivationIfEnemyFound(offset.scale(-1));
         }
      }

      if (!this.hasNoAI() && this.explosionPrimed && this.tickCount % 20 == 0 && this.level() instanceof ServerLevel serverLevel) {
         StructureStart structureStart = serverLevel.structureManager().getStructureWithPieceAt(this.blockPosition(), BzTags.SEMPITERNAL_SANCTUMS);
         if (structureStart == null || !structureStart.isValid()) {
            this.kill();
         }
      }
   }

   private boolean scanAndBeginActivationIfEnemyFound(Vec3 offset) {
      Vec3 eyePosition = this.getEyePosition().add(offset);
      Vec3 finalPos = eyePosition.add(Vec3.atLowerCornerOf(this.getTargetFacing().getNormal().multiply(SIGHT_RANGE)));
      AABB boundsForChecking = this.getBoundingBox().inflate(SIGHT_RANGE);

      EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
              this.level(),
              this,
              eyePosition,
              finalPos,
              boundsForChecking,
              this::canSeeEntity
      );

      if (entityHitResult != null) {
         this.setHasActivated(true);
         this.setShakingTime(40);
         this.setHasShaking(true);
         this.activatedStart = this.position();
         return true;
      }
      else {
         finalPos = this.position().add(0, 0.1d, 0).add(Vec3.atLowerCornerOf(this.getTargetFacing().getNormal().multiply(SIGHT_RANGE)));
         boundsForChecking = this.getBoundingBox().inflate(SIGHT_RANGE);

         EntityHitResult entityHitResult2 = ProjectileUtil.getEntityHitResult(
                 this.level(),
                 this,
                 eyePosition,
                 finalPos,
                 boundsForChecking,
                 this::canSeeEntity
         );

         if (entityHitResult2 != null) {
            this.setHasActivated(true);
            this.setShakingTime(40);
            this.setHasShaking(true);
            this.activatedStart = this.position();
            return true;
         }
      }

      return false;
   }

   private void deactivate() {
      this.setHasActivated(false);
      this.setTargetFacing(this.getTargetFacing().getOpposite());
      this.setDeltaMovement(0, this.getDeltaMovement().y(), 0);

      if (this.level() instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                 this.getX(),
                 this.getY() + 0.2d,
                 this.getZ(),
                 40,
                 1,
                 1,
                 1,
                 0.1D);
         serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                 this.getX(),
                 this.getY() + 0.5d,
                 this.getZ(),
                 40,
                 1,
                 1,
                 1,
                 0.1D);
         serverLevel.sendParticles(ParticleTypes.CRIT,
                 this.getX(),
                 this.getY() + 1,
                 this.getZ(),
                 40,
                 1,
                 1,
                 1,
                 0.1D);

         serverLevel.playSound(
                 this,
                 this.blockPosition(),
                 BzSounds.SENTRY_WATCHER_CRASH.get(),
                 SoundSource.NEUTRAL,
                 2.5F,
                 1.0f);
      }
   }

   private boolean canSeeEntity(Entity entity) {
      if (entity.getType().is(BzTags.SENTRY_WATCHER_FORCED_NEVER_ACTIVATES_WHEN_SEEN) || entity.isSpectator()) {
         return false;
      }
      else if (entity.getType().is(BzTags.SENTRY_WATCHER_ACTIVATES_WHEN_SEEN)) {
         return true;
      }
      else if (entity instanceof Player player && (player.isCreative() || player.getUUID().equals(this.getOwner().orElse(null)))) {
         return false;
      }

      return (entity instanceof LivingEntity) && !BeeAggression.isBeelikeEntity(entity);
   }

   public void aiStep() {
      if (this.hasNoAI()) {
         return;
      }

      if (this.isControlledByLocalInstance()) {
         this.lerpSteps = 0;
         this.syncPacketPositionCodec(this.getX(), this.getY(), this.getZ());
      }

      if (this.lerpSteps > 0) {
         double d = this.getX() + (this.lerpX - this.getX()) / (double)this.lerpSteps;
         double e = this.getY() + (this.lerpY - this.getY()) / (double)this.lerpSteps;
         double f = this.getZ() + (this.lerpZ - this.getZ()) / (double)this.lerpSteps;
         double g = Mth.wrapDegrees(this.lerpYRot - (double)this.getYRot());
         this.setYRot(this.getYRot() + (float)g / (float)this.lerpSteps);
         this.setXRot(this.getXRot() + (float)(this.lerpXRot - (double)this.getXRot()) / (float)this.lerpSteps);
         --this.lerpSteps;
         this.setPos(d, e, f);
         this.setRot(this.getYRot(), this.getXRot());
      }

      turnToTargetFacing();

      Vec3 vec3 = this.getDeltaMovement();
      double newX = vec3.x;
      double newY = vec3.y;
      double newZ = vec3.z;
      if (Math.abs(vec3.x) < 0.003) {
         newX = 0.0;
      }

      if (Math.abs(vec3.y) < 0.003) {
         newY = 0.0;
      }

      if (Math.abs(vec3.z) < 0.003) {
         newZ = 0.0;
      }

      this.setDeltaMovement(newX, newY, newZ);
      this.level().getProfiler().push("ai");
      if (this.isImmobile()) {
         this.xxa = 0.0F;
         this.zza = 0.0F;
      }
      else if (this.isEffectiveAi()) {
         this.level().getProfiler().push("newAi");
         this.serverAiStep();
         this.level().getProfiler().pop();
      }

      this.level().getProfiler().pop();

      this.level().getProfiler().push("travel");

      this.travel();

      this.level().getProfiler().pop();
      this.level().getProfiler().push("freezing");
      if (!this.level().isClientSide) {
         int m = this.getTicksFrozen();
         if (this.isInPowderSnow && this.canFreeze()) {
            this.setTicksFrozen(Math.min(this.getTicksRequiredToFreeze(), m + 1));
         } else {
            this.setTicksFrozen(Math.max(0, m - 2));
         }
      }

      this.level().getProfiler().pop();
   }

   private void turnToTargetFacing() {
      if (!this.hasNoAI() && !this.hasActivated() && this.getYRot() != this.getTargetFacingFromSync().toYRot()) {
         double targetY = this.getTargetFacing().toYRot();
         double currentY = this.getYRot();
         double diff = targetY - currentY;
         double diff2 = targetY - (currentY + 360d);
         double diffToUse = diff;
         if (Math.abs(diff) > Math.abs(diff2)) {
            diffToUse = diff2;
         }
         double newYDiff = Math.max(Math.min(diffToUse, ROTATION_SPEED), -ROTATION_SPEED);
         double newY = currentY + newYDiff;
         if (newY < 0) {
            newY += 360;
         }
         else if (newY >= 360) {
            newY -= 360;
         }
         this.setYRot((float)newY);

         if (this.tickCount % 20 == 0) {
            this.level().playLocalSound(
                    this.blockPosition(),
                    BzSounds.SENTRY_WATCHER_MOVING.get(),
                    SoundSource.NEUTRAL,
                    0.4F,
                    0.2f,
                    false);
         }
      }
   }

   protected void pushEntities() {
      if (this.level().isClientSide()) {
         this.level().getEntities(EntityTypeTest.forClass(Player.class), this.getBoundingBox(), EntitySelector.pushableBy(this)).forEach(this::doPush);
      }
      else {
         List<Entity> list = this.level().getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));
         if (!list.isEmpty()) {
            int i = this.level().getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
            int j;
            if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
               j = 0;

               for (Entity entity : list) {
                  if (!entity.isPassenger()) {
                     ++j;
                  }
               }
            }

            for(j = 0; j < list.size(); ++j) {
               Entity entity = list.get(j);
               this.doPush(entity);
            }
         }
      }
   }

   protected void doPush(Entity entity) {
      if (entity instanceof LivingEntity) {
         Vec3 currentVelocity = this.getDeltaMovement();
         Vec3 victimVelocity = entity.getDeltaMovement();
         Vec3 diffVelocity = currentVelocity.subtract(victimVelocity);
         double speedDiff = this.getTargetFacing().getStepX() != 0 ? Math.abs(diffVelocity.x()) : Math.abs(diffVelocity.z());
         if (speedDiff > 0.2d) {
            speedDiff -= 0.1d;

            double pushEffect = 1.1d;
            entity.setDeltaMovement(0, entity.getDeltaMovement().y(), 0);
            entity.push(this.getDeltaMovement().x() * pushEffect, 0, this.getDeltaMovement().z() * pushEffect);

            AABB sentryBounds = this.getBoundingBox();
            Vec3 pushToSpot = entity.position();
            switch (this.getTargetFacing()) {
               case NORTH -> pushToSpot = new Vec3(pushToSpot.x(), pushToSpot.y(), sentryBounds.minZ);
               case SOUTH -> pushToSpot = new Vec3(pushToSpot.x(), pushToSpot.y(), sentryBounds.maxZ);
               case WEST -> pushToSpot = new Vec3(sentryBounds.minX, pushToSpot.y(), pushToSpot.z());
               case EAST -> pushToSpot = new Vec3(sentryBounds.maxX, pushToSpot.y(), pushToSpot.z());
            }
            entity.setPos(pushToSpot);

            if (!this.level().isClientSide() && !entity.getType().is(BzTags.SENTRY_WATCHER_CANNOT_DAMAGE)) {
               float damageMultiplier = 30;
               if (entity instanceof ServerPlayer serverPlayer && EssenceOfTheBees.hasEssence(serverPlayer)) {
                  damageMultiplier = 16;
               }

               int beeArmorOn = BeeArmor.getBeeThemedWearablesCount(entity);
               damageMultiplier -= (beeArmorOn * 1.333333f);

               entity.hurt(this.level().damageSources().source(BzDamageSources.SENTRY_WATCHER_CRUSHING_TYPE, this), (float) (speedDiff * damageMultiplier));
            }
         }
         else {
            super.push(entity);
         }
      }
      else if (entity instanceof SentryWatcherEntity) {
         deactivate();
      }
      else {
         super.push(entity);
      }
   }

   @Override
   public void push(double d, double e, double f) {}

   public Vec3 handleRelativeFrictionAndCalculateMovement() {
      Vec3 deltaMovement = this.getDeltaMovement();
      if (!this.hasActivated()) {
         deltaMovement = deltaMovement.multiply(0.9d, 1, 0.9d);
      }

      this.move(MoverType.SELF, deltaMovement);

      deltaMovement = this.getDeltaMovement();
      if (this.horizontalCollision && (this.getFeetBlockState().is(Blocks.POWDER_SNOW) && PowderSnowBlock.canEntityWalkOnPowderSnow(this))) {
         deltaMovement = new Vec3(deltaMovement.x, 0.2, deltaMovement.z);
      }

      return deltaMovement;
   }

   public Vec3 getFluidFallingAdjustedMovement(double d, boolean bl, Vec3 vec3) {
      if (!this.isNoGravity() && !this.isSprinting()) {
         double e;
         if (bl && Math.abs(vec3.y - 0.005) >= 0.003 && Math.abs(vec3.y - d / 16.0) < 0.003) {
            e = -0.003;
         }
         else {
            e = vec3.y - d / 16.0;
         }

         return new Vec3(vec3.x, e, vec3.z);
      }
      else {
         return vec3;
      }
   }

   @Override
   public void move(MoverType moverType, Vec3 vec3) {
      if (this.noPhysics) {
         this.setPos(this.getX() + vec3.x, this.getY() + vec3.y, this.getZ() + vec3.z);
      }
      else {
         this.wasOnFire = this.isOnFire();
         if (moverType == MoverType.PISTON) {
            vec3 = this.limitPistonMovement(vec3);
            if (vec3.equals(Vec3.ZERO)) {
               return;
            }
         }

         this.level().getProfiler().push("move");
         if (this.stuckSpeedMultiplier.lengthSqr() > 1.0E-7) {
            vec3 = vec3.multiply(this.stuckSpeedMultiplier);
            this.stuckSpeedMultiplier = Vec3.ZERO;
            this.setDeltaMovement(Vec3.ZERO);
         }

         vec3 = this.maybeBackOffFromEdge(vec3, moverType);
         Vec3 collision = this.collide(vec3);
         double d = collision.lengthSqr();
         if (d > 1.0E-7) {
            if (this.fallDistance != 0.0F && d >= 1.0) {
               BlockHitResult blockHitResult = this.level().clip(new ClipContext(this.position(), this.position().add(collision), ClipContext.Block.FALLDAMAGE_RESETTING, net.minecraft.world.level.ClipContext.Fluid.WATER, this));
               if (blockHitResult.getType() != HitResult.Type.MISS) {
                  this.resetFallDistance();
               }
            }

            this.setPos(this.getX() + collision.x, this.getY() + collision.y, this.getZ() + collision.z);
         }

         this.level().getProfiler().pop();
         this.level().getProfiler().push("rest");
         this.prevVelocity = vec3;
         boolean loseXSpeed = !Mth.equal(vec3.x, collision.x);
         boolean loseZSpeed = !Mth.equal(vec3.z, collision.z);
         this.horizontalCollision = loseXSpeed || loseZSpeed;
         this.verticalCollision = vec3.y != collision.y;
         this.verticalCollisionBelow = this.verticalCollision && vec3.y < 0.0;

         Vec3 deltaMovement = this.getDeltaMovement();
         if (this.horizontalCollision && (Math.abs(deltaMovement.x()) + Math.abs(deltaMovement.z()) > 0.01)) {
            destroyBlocksInWay();
         }

         if (this.horizontalCollision) {
            this.minorHorizontalCollision = this.isHorizontalCollisionMinor(collision);
         }
         else {
            this.minorHorizontalCollision = false;
         }

         this.setOnGroundWithKnownMovement(this.verticalCollisionBelow, collision);
         BlockPos blockPos = this.getOnPosLegacy();
         BlockState blockState = this.level().getBlockState(blockPos);
         this.checkFallDamage(collision.y, this.onGround(), blockState, blockPos);
         if (this.isRemoved()) {
            this.level().getProfiler().pop();
         }
         else {
            if (this.horizontalCollision) {
               Vec3 vec33 = this.getDeltaMovement();
               this.setDeltaMovement(loseXSpeed ? 0.0 : vec33.x, vec33.y, loseZSpeed ? 0.0 : vec33.z);
            }

            Block block = blockState.getBlock();
            if (vec3.y != collision.y) {
               block.updateEntityAfterFallOn(this.level(), this);
            }

            if (this.onGround()) {
               block.stepOn(this.level(), blockPos, blockState, this);
            }

            MovementEmission movementEmission = this.getMovementEmission();
            if (movementEmission.emitsAnything() && !this.isPassenger()) {
               double e = collision.x;
               double f = collision.y;
               double g = collision.z;
               this.flyDist += (float)(collision.length() * 0.6);
               BlockPos blockPos2 = this.getOnPos();
               BlockState blockState2 = this.level().getBlockState(blockPos2);

               this.walkDist += (float)collision.horizontalDistance() * 0.6F;
               this.moveDist += (float)Math.sqrt(e * e + f * f + g * g) * 0.6F;
               if (this.moveDist > ((EntityAccessor) this).getNextStep() && !blockState2.isAir()) {
                  boolean bl4 = blockPos2.equals(blockPos);
                  boolean bl5 = ((EntityAccessor)this).callVibrationAndSoundEffectsFromBlock(blockPos, blockState, movementEmission.emitsSounds(), bl4, vec3);
                  if (!bl4) {
                     bl5 |= ((EntityAccessor)this).callVibrationAndSoundEffectsFromBlock(blockPos2, blockState2, false, movementEmission.emitsEvents(), vec3);
                  }

                  if (bl5) {
                     ((EntityAccessor) this).setNextStep(this.nextStep());
                  }
                  else if (this.isInWater()) {
                     ((EntityAccessor) this).setNextStep(this.nextStep());
                     if (movementEmission.emitsSounds()) {
                        this.waterSwimSound();
                     }

                     if (movementEmission.emitsEvents()) {
                        this.gameEvent(GameEvent.SWIM);
                     }
                  }
               }
               else if (blockState2.isAir()) {
                  this.processFlappingMovement();
               }
            }

            this.tryCheckInsideBlocks();
            if (this.level().getBlockStatesIfLoaded(this.getBoundingBox().deflate(1.0E-6)).noneMatch((blockStatex) -> blockStatex.is(BlockTags.FIRE) || blockStatex.is(Blocks.LAVA))) {
               if (this.getRemainingFireTicks() <= 0) {
                  this.setRemainingFireTicks(-this.getFireImmuneTicks());
               }

               if (this.wasOnFire && (this.isInPowderSnow || this.isInWaterRainOrBubble())) {
                  this.playEntityOnFireExtinguishedSound();
               }
            }

            if (this.isOnFire() && (this.isInPowderSnow || this.isInWaterRainOrBubble())) {
               this.setRemainingFireTicks(-this.getFireImmuneTicks());
            }

            this.level().getProfiler().pop();
         }
      }
   }

   private void destroyBlocksInWay() {
      Direction facing = this.getTargetFacing();
      AABB aabb = this.getBoundingBox();
      BlockPos min = null;
      BlockPos max = null;
      double xStep = (facing.getStepX() / 3d);
      double zStep = (facing.getStepZ() / 3d);
      switch (facing) {
         case NORTH -> {
            min = new BlockPos((int) Math.floor(aabb.minX + xStep + 0.0001d), (int)Math.floor(aabb.minY), (int)Math.floor(aabb.minZ + zStep + 0.0001d));
            max = new BlockPos((int) Math.floor(aabb.maxX + xStep - 0.0001f), (int)Math.floor(aabb.minY), (int)Math.floor(aabb.minZ + zStep + 0.0001d));
         }
         case SOUTH -> {
            min = new BlockPos((int) Math.floor(aabb.minX + xStep + 0.0001d), (int)Math.floor(aabb.minY), (int)Math.floor(aabb.maxZ + zStep - 0.0001d));
            max = new BlockPos((int) Math.floor(aabb.maxX + xStep - 0.0001d), (int)Math.floor(aabb.minY), (int)Math.floor(aabb.maxZ + zStep - 0.0001d));
         }
         case WEST -> {
            min = new BlockPos((int) Math.floor(aabb.minX + xStep + 0.0001d), (int)Math.floor(aabb.minY), (int)Math.floor(aabb.minZ + zStep + 0.0001d));
            max = new BlockPos((int) Math.floor(aabb.minX + xStep + 0.0001d), (int)Math.floor(aabb.minY), (int)Math.floor(aabb.maxZ + zStep - 0.0001d));
         }
         case EAST -> {
            min = new BlockPos((int) Math.floor(aabb.maxX + xStep - 0.0001d), (int)Math.floor(aabb.minY), (int)Math.floor(aabb.minZ + zStep + 0.0001d));
            max = new BlockPos((int) Math.floor(aabb.maxX + xStep - 0.0001d), (int)Math.floor(aabb.minY), (int)Math.floor(aabb.maxZ + zStep - 0.0001d));
         }
      }

      if (min != null && this.getOwner().isEmpty()) {
         boolean canDemolish = true;
         double totalhardness = 0;
         int alwaysDestroyCounter = 0;
         List<BlockPos> demolishPos = new ArrayList<>();
         for (BlockPos pos : BlockPos.betweenClosed(min, max)) {

            BlockState state = this.level().getBlockState(pos);
            VoxelShape blockShape = state.getCollisionShape(this.level(), pos);
            if (!blockShape.isEmpty() || state.is(BzTags.SENTRY_WATCHER_ALWAYS_DESTROY)) {
               if (blockShape.max(Direction.Axis.Y) > this.getY() - this.getBlockY()) {
                  if (state.is(BzTags.SENTRY_WATCHER_FORCED_NEVER_DESTROY)) {
                     canDemolish = false;
                     break;
                  }
                  else {
                     demolishPos.add(pos.immutable());
                     totalhardness += state.getBlock().getExplosionResistance();
                     if (state.is(BzTags.SENTRY_WATCHER_ALWAYS_DESTROY)) {
                        alwaysDestroyCounter++;
                     }
                  }
               }
            }

            BlockPos abovePos = pos.above();
            BlockState aboveState = this.level().getBlockState(abovePos);
            if (!aboveState.getCollisionShape(this.level(), abovePos).isEmpty() || aboveState.is(BzTags.SENTRY_WATCHER_ALWAYS_DESTROY)) {
               if (aboveState.is(BzTags.SENTRY_WATCHER_FORCED_NEVER_DESTROY)) {
                  canDemolish = false;
                  break;
               }
               else {
                  demolishPos.add(abovePos);
                  totalhardness += aboveState.getBlock().getExplosionResistance();
                  if (aboveState.is(BzTags.SENTRY_WATCHER_ALWAYS_DESTROY)) {
                     alwaysDestroyCounter++;
                  }
               }
            }
         }

         if (canDemolish && (alwaysDestroyCounter == demolishPos.size() || totalhardness < UNABLE_TO_DESTROY_TOTAL_BLOCK_HARDNESS)) {
            for (BlockPos pos : demolishPos) {
               this.level().destroyBlock(pos, true);
            }

            this.horizontalCollision = false;
         }
      }
   }

   private Vec3 collide(Vec3 incomingSpeed) {
      AABB sentryBoundingBox = this.getBoundingBox();
      List<VoxelShape> shapesCollidedWith = new ArrayList<>();
      boolean isNotMoving = incomingSpeed.lengthSqr() == 0.0;

      Vec3 collidedVelocity = isNotMoving ?
              incomingSpeed :
              collideBoundingBox(
                      this,
                      incomingSpeed,
                      sentryBoundingBox,
                      this.level(),
                      shapesCollidedWith);

      boolean xCollided = incomingSpeed.x != collidedVelocity.x;
      boolean zCollided = incomingSpeed.z != collidedVelocity.z;

      if (this.maxUpStep() > 0.0F && (xCollided || zCollided)) {
         Vec3 vec33 = collideBoundingBox(
                 this,
                 new Vec3(incomingSpeed.x, this.maxUpStep(), incomingSpeed.z),
                 sentryBoundingBox,
                 this.level(),
                 shapesCollidedWith);

         Vec3 vec34 = collideBoundingBox(
                 this,
                 new Vec3(0.0, this.maxUpStep(), 0.0),
                 sentryBoundingBox.expandTowards(incomingSpeed.x, 0.0, incomingSpeed.z),
                 this.level(),
                 shapesCollidedWith);

         if (vec34.y < (double)this.maxUpStep()) {

            Vec3 vec35 = collideBoundingBox(
                    this,
                    new Vec3(incomingSpeed.x, 0.0, incomingSpeed.z),
                    sentryBoundingBox.move(vec34),
                    this.level(),
                    shapesCollidedWith).add(vec34);

            if (vec35.horizontalDistanceSqr() > vec33.horizontalDistanceSqr()) {
               vec33 = vec35;
            }
         }

         if (vec33.horizontalDistanceSqr() > collidedVelocity.horizontalDistanceSqr()) {
            return vec33.add(collideBoundingBox(
                    this,
                    new Vec3(0.0, -vec33.y + incomingSpeed.y, 0.0),
                    sentryBoundingBox.move(vec33),
                    this.level(),
                    shapesCollidedWith));
         }
      }

      return collidedVelocity;
   }

   public void lerpTo(double d, double e, double f, float g, float h, int i, boolean bl) {
      this.lerpX = d;
      this.lerpY = e;
      this.lerpZ = f;
      this.lerpYRot = g;
      this.lerpXRot = h;
      this.lerpSteps = i;
   }

   @Override
   public boolean canChangeDimensions() {
      return false;
   }

   @Override
   public float getVisualRotationYInDegrees() {
      return this.getYRot();
   }

   protected boolean isImmobile() {
      return false;
   }
}