package com.telepathicgrunt.the_bumblezone.entities.living;

import com.telepathicgrunt.the_bumblezone.client.rendering.boundlesscrystal.BoundlessCrystalState;
import com.telepathicgrunt.the_bumblezone.items.essence.EssenceOfTheBees;
import com.telepathicgrunt.the_bumblezone.mixin.entities.EntityAccessor;
import com.telepathicgrunt.the_bumblezone.mixin.entities.LivingEntityAccessor;
import com.telepathicgrunt.the_bumblezone.modinit.BzDamageSources;
import com.telepathicgrunt.the_bumblezone.modinit.BzParticles;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class BoundlessCrystalEntity extends LivingEntity {
    public static final EntityDataSerializer<BoundlessCrystalState> BOUNDLESS_CRYSTAL_STATE_SERIALIZER = EntityDataSerializer.simpleEnum(BoundlessCrystalState.class);
    private static final EntityDataAccessor<BoundlessCrystalState> BOUNDLESS_CRYSTAL_STATE = SynchedEntityData.defineId(BoundlessCrystalEntity.class, BOUNDLESS_CRYSTAL_STATE_SERIALIZER);
    private static final EntityDataAccessor<BoundlessCrystalState> PREVIOUS_BOUNDLESS_CRYSTAL_STATE = SynchedEntityData.defineId(BoundlessCrystalEntity.class, BOUNDLESS_CRYSTAL_STATE_SERIALIZER);
    private static final EntityDataAccessor<Integer> ANIMATION_END_TIME = SynchedEntityData.defineId(BoundlessCrystalEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STATE_END_TIME = SynchedEntityData.defineId(BoundlessCrystalEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LASER_START_TIME = SynchedEntityData.defineId(BoundlessCrystalEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ACTIVE_TIME = SynchedEntityData.defineId(BoundlessCrystalEntity.class, EntityDataSerializers.INT);

    public final AnimationState idleAnimationState = new AnimationState();

    private final NonNullList<ItemStack> armorItems = NonNullList.withSize(0, ItemStack.EMPTY);
    private UUID essenceController = null;
    private BlockPos essenceControllerBlockPos = null;
    private ResourceKey<Level> essenceControllerDimension = null;
    private UUID targetEntityUUID = null;
    private Entity targetEntity = null;
    public int activeTick = 0;
    public int animationTimeTick = 0;
    public int prevAnimationTick = 0;
    public float visualXRot = 0;
    public float prevVisualXRot = 0;
    public Vec3 prevLookAngle = Vec3.ZERO;

    public BoundlessCrystalEntity(EntityType<? extends BoundlessCrystalEntity> entityType, Level level) {
        super(entityType, level);
        this.idleAnimationState.start(this.activeTick);
        this.noCulling = true;
    }

    public static AttributeSupplier.Builder getAttributeBuilder() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.1D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 50.0D);
    }
    public UUID getEssenceController() {
        return essenceController;
    }

    public void setEssenceController(UUID essenceController) {
        this.essenceController = essenceController;
    }

    public BlockPos getEssenceControllerBlockPos() {
        return essenceControllerBlockPos;
    }

    public void setEssenceControllerBlockPos(BlockPos essenceControllerBlockPos) {
        this.essenceControllerBlockPos = essenceControllerBlockPos;
    }

    public ResourceKey<Level> getEssenceControllerDimension() {
        return essenceControllerDimension;
    }

    public void setEssenceControllerDimension(ResourceKey<Level> essenceControllerDimension) {
        this.essenceControllerDimension = essenceControllerDimension;
    }

    public void setBoundlessCrystalState(BoundlessCrystalState boundlessCrystalState) {
        if(!this.level().isClientSide()) {
            this.animationTimeTick = 0;
            this.prevAnimationTick = 0;

            this.setActiveTick(this.activeTick);
            this.entityData.set(PREVIOUS_BOUNDLESS_CRYSTAL_STATE, getBoundlessCrystalState());
            this.entityData.set(BOUNDLESS_CRYSTAL_STATE, boundlessCrystalState);

            switch (this.getBoundlessCrystalState()) {
                case NORMAL -> {
                    this.setAnimationEndTime(40);
                    this.setStateEndTime(Integer.MAX_VALUE);
                }
                case TRACKING_SMASHING_ATTACK -> this.setAnimationEndTime(200);
                case SPINNER_ATTACK -> this.setAnimationEndTime(200);
                case TRACKING_SPINNING_ATTACK -> this.setAnimationEndTime(200);
                case VERTICAL_LASER -> {
                    this.setAnimationEndTime(40);
                    this.setLaserStartTime(this.activeTick + 60);
                    this.setStateEndTime(this.activeTick + 400);
                }
                case HORIZONTAL_LASER -> {
                    this.setAnimationEndTime(40);
                    this.setLaserStartTime(this.activeTick + 60);
                    this.setStateEndTime(this.activeTick + 400);
                }
                case SWEEP_LASER -> this.setAnimationEndTime(200);
                case TRACKING_LASER -> {
                    this.setAnimationEndTime(40);
                    this.setLaserStartTime(this.activeTick + 60);
                    this.setStateEndTime(this.activeTick + 400);
                }
            }
        }
    }

    public BoundlessCrystalState getBoundlessCrystalState() {
        return this.entityData.get(BOUNDLESS_CRYSTAL_STATE);
    }

    public BoundlessCrystalState getPreviousBoundlessCrystalState() {
        return this.entityData.get(PREVIOUS_BOUNDLESS_CRYSTAL_STATE);
    }

    public int getActiveTime() {
        return this.entityData.get(ACTIVE_TIME);
    }

    public void setActiveTick(int activeTick) {
        this.entityData.set(ACTIVE_TIME, activeTick);
    }

    public void setAnimationEndTime(int animationEndTime) {
        this.entityData.set(ANIMATION_END_TIME, animationEndTime);
    }

    public int getAnimationEndTime() {
        return this.entityData.get(ANIMATION_END_TIME);
    }

    public void setStateEndTime(int stateEndTime) {
        this.entityData.set(STATE_END_TIME, stateEndTime);
    }

    public int getStateEndTime() {
        return this.entityData.get(STATE_END_TIME);
    }

    public void setLaserStartTime(int laserStartTime) {
        this.entityData.set(LASER_START_TIME, laserStartTime);
    }

    public int getLaserStartTime() {
        return this.entityData.get(LASER_START_TIME);
    }

    public void setTargetEntityUUID(UUID targetEntityUUID) {
        this.targetEntityUUID = targetEntityUUID;
    }

    public UUID getTargetEntityUUID() {
        return this.targetEntityUUID;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BOUNDLESS_CRYSTAL_STATE, BoundlessCrystalState.NORMAL);
        this.entityData.define(PREVIOUS_BOUNDLESS_CRYSTAL_STATE, BoundlessCrystalState.NORMAL);
        this.entityData.define(ANIMATION_END_TIME, 0);
        this.entityData.define(STATE_END_TIME, 0);
        this.entityData.define(LASER_START_TIME, 0);
        this.entityData.define(ACTIVE_TIME, 0);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        super.onSyncedDataUpdated(entityDataAccessor);
        if (BOUNDLESS_CRYSTAL_STATE.equals(entityDataAccessor)) {
            this.animationTimeTick = 0;
            this.prevAnimationTick = 0;
        }

        this.activeTick = this.getActiveTime();
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);

        if (compoundTag.contains("essenceController")) {
            this.setEssenceController(compoundTag.getUUID("essenceController"));
        }
        if (compoundTag.contains("essenceControllerBlockPos")) {
            this.setEssenceControllerBlockPos(NbtUtils.readBlockPos(compoundTag.getCompound("essenceControllerBlockPos")));
        }
        if (compoundTag.contains("essenceControllerDimension")) {
            this.setEssenceControllerDimension(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(compoundTag.getString("essenceControllerDimension"))));
        }

        this.setAnimationEndTime(compoundTag.getInt("animationTimeEndTick"));
        this.animationTimeTick = compoundTag.getInt("animationTimeTick");
        this.prevAnimationTick = compoundTag.getInt("prevAnimationTick");
        this.activeTick = compoundTag.getInt("activeTick");
        this.visualXRot = compoundTag.getFloat("visualXRot");
        this.prevVisualXRot = compoundTag.getFloat("prevVisualXRot");

        if (compoundTag.contains("targetEntityUUID")) {
            this.targetEntityUUID = compoundTag.getUUID("targetEntityUUID");
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);

        if (this.getEssenceController() != null) {
            compoundTag.putUUID("essenceController", this.getEssenceController());
        }
        if (this.getEssenceControllerBlockPos() != null) {
            compoundTag.put("essenceControllerBlockPos", NbtUtils.writeBlockPos(this.getEssenceControllerBlockPos()));
        }
        if (this.getEssenceControllerDimension() != null) {
            compoundTag.putString("essenceControllerDimension", this.getEssenceControllerDimension().location().toString());
        }

        compoundTag.putInt("animationTimeEndTick", this.getAnimationEndTime());
        compoundTag.putInt("animationTimeTick", this.animationTimeTick);
        compoundTag.putInt("prevAnimationTick", this.prevAnimationTick);
        compoundTag.putInt("activeTick", this.activeTick);
        compoundTag.putFloat("visualXRot", this.visualXRot);
        compoundTag.putFloat("prevVisualXRot", this.prevVisualXRot);
        if (this.targetEntityUUID != null) {
            compoundTag.putUUID("targetEntityUUID", this.targetEntityUUID);
        }
    }

    public static boolean isOrFromHorizontalState(BoundlessCrystalState boundlessCrystalState) {
        return boundlessCrystalState == BoundlessCrystalState.HORIZONTAL_LASER ||
                boundlessCrystalState == BoundlessCrystalState.TRACKING_LASER ||
                boundlessCrystalState == BoundlessCrystalState.SWEEP_LASER ||
                boundlessCrystalState == BoundlessCrystalState.SPINNER_ATTACK ||
                boundlessCrystalState == BoundlessCrystalState.TRACKING_SPINNING_ATTACK;
    }

    public static boolean isLaserState(BoundlessCrystalState boundlessCrystalState) {
        return boundlessCrystalState == BoundlessCrystalState.VERTICAL_LASER ||
                boundlessCrystalState == BoundlessCrystalState.HORIZONTAL_LASER ||
                boundlessCrystalState == BoundlessCrystalState.TRACKING_LASER ||
                boundlessCrystalState == BoundlessCrystalState.SWEEP_LASER;
    }

    public boolean isLaserFiring() {
        boolean isLaserState = BoundlessCrystalEntity.isLaserState(this.getBoundlessCrystalState());
        return isLaserState && this.activeTick > this.getLaserStartTime() + 40;
    }

    public void tick() {
        super.tick();

        spawnFancyParticlesOnClient();
        laserBreakBlocks();
        incrementAnimationAndRotationTicks();

        this.activeTick++;
    }

    private void spawnFancyParticlesOnClient() {
        if (this.level().isClientSide()) {
            if (this.activeTick % 5 == 0 || this.hurtTime > 0) {
                Vec3 center = this.getBoundingBox().getCenter();
                spawnFancyParticle(center);
                if (this.hurtTime == 8) {
                    for (int i = 0; i < 50; i++) {
                        spawnFancyParticle(center);
                    }
                }
            }

            if (this.isLaserFiring()) {
                spawnFancyParticle(this.getEyePosition().add(this.getLookAngle().scale(1.2f)));
            }
        }
    }

    private void incrementAnimationAndRotationTicks() {
        this.prevAnimationTick = this.animationTimeTick;
        if (this.animationTimeTick < getAnimationEndTime()) {
            this.animationTimeTick++;
        }
        else if(this.getBoundlessCrystalState() != BoundlessCrystalState.NORMAL && this.activeTick >= this.getStateEndTime()) {
            this.setBoundlessCrystalState(BoundlessCrystalState.NORMAL);
        }

        this.refreshDimensions();

        if (this.getBoundlessCrystalState() == BoundlessCrystalState.TRACKING_LASER) {
            if (getTargetEntityUUID() == null || this.targetEntity == null || !this.targetEntity.getUUID().equals(getTargetEntityUUID())) {
                if (getTargetEntityUUID() != null) {
                    this.targetEntity = this.level().getPlayerByUUID(getTargetEntityUUID());
                }
                else {
                    this.targetEntity = this.level().getNearestPlayer(this, 30);
                    if (this.targetEntity != null) {
                        this.setTargetEntityUUID(this.targetEntity.getUUID());
                    }
                }
            }
        }

        this.prevVisualXRot = this.visualXRot;

        float progress;
        if (this.getAnimationEndTime() == 0) {
            progress = 0;
        }
        else {
            progress = (float)this.animationTimeTick / this.getAnimationEndTime();
        }


        float xRot = this.visualXRot;
        if (this.getBoundlessCrystalState() == BoundlessCrystalState.TRACKING_LASER) {
            if (this.targetEntity != null) {
                Vec3 targetPos = this.targetEntity.position().add(this.targetEntity.getDeltaMovement().scale(-10));
                double xDiff = targetPos.x() - this.position().x();
                double yDiff = targetPos.y() - this.position().y();
                double zDiff = targetPos.z() - this.position().z();
                double sqrt = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
                xRot = Mth.wrapDegrees((float)(Mth.atan2(yDiff, sqrt) * 57.2957763671875) + 90.0f) * progress;
            }
        }
        else if (isOrFromHorizontalState(this.getBoundlessCrystalState())) {
            xRot = 90 * progress;
        }
        else if (xRot < 0.001f) {
            xRot = 0;
        }
        else {
            xRot = xRot - (xRot * progress * 0.1f);
        }
        this.visualXRot = xRot;


        xRot = this.getXRot();
        if (this.getBoundlessCrystalState() == BoundlessCrystalState.VERTICAL_LASER) {
            xRot = 90 * progress;
        }
        else if (this.getBoundlessCrystalState() == BoundlessCrystalState.TRACKING_LASER) {
            if (this.targetEntity != null) {
                this.prevLookAngle = this.getLookAngle();
                Vec3 targetPos = this.targetEntity.position().add(this.targetEntity.getDeltaMovement().scale(-10));
                this.lookAt(EntityAnchorArgument.Anchor.FEET, targetPos);
                return;
            }
        }
        else if (xRot < 0.001f) {
            xRot = 0;
        }
        else {
            xRot = xRot - (xRot * progress * 0.1f);
        }
        this.setXRot(xRot);
    }

    private void laserBreakBlocks() {
        if (!this.level().isClientSide() && this.isLaserFiring() && (this.activeTick + this.getUUID().getLeastSignificantBits()) % 3 == 0) {
            HitResult hitResult = ProjectileUtil.getHitResultOnViewVector(this, (entity) -> true, 50);

            if (hitResult instanceof BlockHitResult blockHitResult) {
               BlockState state = this.level().getBlockState(blockHitResult.getBlockPos());
               if (state.getBlock().getExplosionResistance() < 1500) {
                   this.level().destroyBlock(blockHitResult.getBlockPos(), true);
               }
            }
            else if (hitResult instanceof EntityHitResult entityHitResult) {
                Entity entity = entityHitResult.getEntity();
                if (entity instanceof ItemEntity itemEntity) {
                    itemEntity.hurt(this.level().damageSources().source(BzDamageSources.BOUNDLESS_CRYSTAL_TYPE, this), 10);
                }
                else if (entity instanceof Projectile projectile) {
                    projectile.remove(RemovalReason.KILLED);
                }
                else if (entity instanceof LivingEntity livingEntity && !(entity instanceof BoundlessCrystalEntity)) {
                    float damageAmount;
                    float maxHealth = Math.max(livingEntity.getHealth(), livingEntity.getMaxHealth());

                    if (livingEntity instanceof ServerPlayer serverPlayer) {
                        if (serverPlayer.isCreative()) {
                            return;
                        }

                        if (EssenceOfTheBees.hasEssence(serverPlayer)) {
                            damageAmount = maxHealth / 6;
                        }
                        else {
                            damageAmount = maxHealth / 3;
                        }
                    }
                    else {
                        damageAmount = maxHealth / 6;
                    }

                    livingEntity.hurt(this.level().damageSources().source(BzDamageSources.BOUNDLESS_CRYSTAL_TYPE, this), damageAmount);
                }
            }
        }
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }

        this.setBoundlessCrystalState(BoundlessCrystalState.TRACKING_LASER);

        return InteractionResult.PASS;
    }

    @Override
    public void baseTick() {

        if (this.isPassenger()) {
            this.stopRiding();
        }

        this.walkDistO = this.walkDist;
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();

        if (this.level().isClientSide) {
            this.clearFire();
        }
        else if (this.getRemainingFireTicks() > 0) {
            if (this.getRemainingFireTicks() > 25) {
                this.setRemainingFireTicks(25);
            }

            if (this.getRemainingFireTicks() == 1 && !this.isInLava()) {
                this.hurt(this.damageSources().onFire(), 1.0f);
            }
            this.setRemainingFireTicks(this.getRemainingFireTicks() - 1);

            if (this.getTicksFrozen() > 0) {
                this.setTicksFrozen(0);
                this.level().levelEvent(null, 1009, this.blockPosition(), 1);
            }
        }
        else {
            this.setRemainingFireTicks(-1);
        }

        if (!this.level().isClientSide && this.getTicksFrozen() > 0) {
            if (this.wasInPowderSnow && !this.isInPowderSnow && this.canFreeze()) {
                this.setTicksFrozen(39);
            }
            else if (this.isInPowderSnow && this.canFreeze()) {
                if (this.getTicksFrozen() > 39 && this.getTicksFrozen() < 70) {
                    if (this.getTicksFrozen() == 69) {
                        this.hurt(this.damageSources().freeze(), 1.0f);
                    }
                }
                else {
                    this.setTicksFrozen(40);
                }
            }

            if (this.getTicksFrozen() == 1 || this.getTicksFrozen() == 2) {
                this.hurt(this.damageSources().freeze(), 1.0f);
            }
        }

        this.wasInPowderSnow = this.isInPowderSnow;
        this.isInPowderSnow = false;

        if (this.hurtTime > 0) {
            --this.hurtTime;
        }
        if (this.invulnerableTime > 0) {
            --this.invulnerableTime;
        }
        if (this.isDeadOrDying() && this.level().shouldTickDeath(this)) {
            this.tickDeath();
        }
        if (this.lastHurtByPlayerTime > 0) {
            --this.lastHurtByPlayerTime;
        }
        else {
            this.lastHurtByPlayer = null;
        }
        if (this.getLastHurtMob() != null && !this.getLastHurtMob().isAlive()) {
            this.setLastHurtByMob(null);
        }
        if (this.getLastHurtByMob() != null) {
            if (!this.getLastHurtByMob().isAlive()) {
                this.setLastHurtByMob(null);
            }
            else if (this.activeTick - this.getLastHurtByMobTimestamp() > 100) {
                this.setLastHurtByMob(null);
            }
        }
        this.animStepO = this.animStep;
        this.yBodyRotO = this.yBodyRot;
        this.yHeadRotO = this.yHeadRot;
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();

        for (MobEffectInstance mobEffectInstance : this.getActiveEffects()) {
            MobEffect mobEffect = mobEffectInstance.getEffect();
            int currentEffectTick = mobEffectInstance.isInfiniteDuration() ? Integer.MAX_VALUE : mobEffectInstance.getDuration();

            if (mobEffect == MobEffects.POISON) {
                if (currentEffectTick > 45) {
                    this.forceAddEffect(new MobEffectInstance(
                            mobEffect,
                            45,
                            mobEffectInstance.getAmplifier(),
                            mobEffectInstance.isAmbient(),
                            mobEffectInstance.isVisible(),
                            mobEffectInstance.showIcon()
                        ),
                        this);
                }
            }
            else if (mobEffect == MobEffects.WITHER) {
                if (currentEffectTick > 80) {
                    this.forceAddEffect(new MobEffectInstance(
                            mobEffect,
                            80,
                            mobEffectInstance.getAmplifier(),
                            mobEffectInstance.isAmbient(),
                            mobEffectInstance.isVisible(),
                            mobEffectInstance.showIcon()
                        ),
                        this);
                }
            }
            else if (currentEffectTick > 30 && !mobEffectInstance.getEffect().isBeneficial()) {
                this.forceAddEffect(new MobEffectInstance(
                        mobEffect,
                        30,
                        mobEffectInstance.getAmplifier(),
                        mobEffectInstance.isAmbient(),
                        mobEffectInstance.isVisible(),
                        mobEffectInstance.showIcon()
                    ),
                    this);
            }
        }

        this.tickEffects();

        if (!this.level().isClientSide) {
            this.setSharedFlagOnFire(this.getRemainingFireTicks() > 0);
        }

        this.firstTick = false;
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isControlledByLocalInstance()) {
            double d = 0.08;
            boolean bl = this.getDeltaMovement().y <= 0.0;
            if (bl && this.hasEffect(MobEffects.SLOW_FALLING)) {
                d = 0.01;
            }
            BlockPos blockPos = this.getBlockPosBelowThatAffectsMyMovement();
            float p = this.level().getBlockState(blockPos).getBlock().getFriction();
            float f = this.onGround() ? p * 0.91f : 0.91f;
            Vec3 vec37 = this.handleRelativeFrictionAndCalculateMovement(vec3, p);

            double q = vec37.y;
            if (!this.level().isClientSide || this.level().hasChunkAt(blockPos)) {
                if (!this.isNoGravity()) {
                    q -= d;
                }
            }
            else {
                q = this.getY() > (double)this.level().getMinBuildHeight() ? -0.1 : 0.0;
            }

            this.setDeltaMovement(vec37.x * (double)f, q * (double)0.98f, vec37.z * (double)f);
        }

        this.calculateEntityAnimation(true);
    }

    @Override
    protected AABB makeBoundingBox() {
        EntityDimensions entityDimensions = ((EntityAccessor)this).getDimensions();
        float radius = entityDimensions.width / 2.0F;
        float heightRadius = entityDimensions.height / 2.0F;
        float yOffset = 1f;

        float progress = 1 - Mth.abs((this.visualXRot / 90) - 1);

        if (isOrFromHorizontalState(this.getBoundlessCrystalState()) ||
            isOrFromHorizontalState(this.getPreviousBoundlessCrystalState()))
        {
            float yRotRadian = this.getYRot() * Mth.DEG_TO_RAD;
            double yRotSin = Math.abs(Mth.sin(yRotRadian));
            double yRotCos = Math.abs(Mth.cos(yRotRadian));
            double xRadius = radius + (yRotSin * 0.5f * progress);
            double zRadius = radius + (yRotCos * 0.5f * progress);

            heightRadius = heightRadius - (0.5f * progress);

            double xMin = this.getX() - xRadius;
            double yMin = this.getY() + yOffset - heightRadius;
            double zMin = this.getZ() - zRadius;
            double xMax = this.getX() + xRadius;
            double yMax = this.getY() + yOffset + heightRadius;
            double zMax = this.getZ() + zRadius;

            return new AABB(xMin, yMin, zMin, xMax, yMax, zMax);
        }
        else {
            Vec3 vec3 = new Vec3(this.getX() - (double)radius, this.getY() + yOffset - heightRadius, this.getZ() - (double)radius);
            Vec3 vec32 = new Vec3(this.getX() + (double)radius, this.getY() + yOffset + heightRadius, this.getZ() + (double)radius);
            return new AABB(vec3, vec32);
        }
    }

    @Override
    protected void checkInsideBlocks() {
        AABB aABB = this.getBoundingBox();
        BlockPos blockPos = BlockPos.containing(aABB.minX + 1.0E-7, aABB.minY + 1.0E-7, aABB.minZ + 1.0E-7);
        BlockPos blockPos2 = BlockPos.containing(aABB.maxX - 1.0E-7, aABB.maxY - 1.0E-7, aABB.maxZ - 1.0E-7);
        if (this.level().hasChunksAt(blockPos, blockPos2)) {
            BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
            for (int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                for (int j = blockPos.getY(); j <= blockPos2.getY(); ++j) {
                    for (int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                        mutableBlockPos.set(i, j, k);
                        BlockState blockState = this.level().getBlockState(mutableBlockPos);
                        if (!blockState.isAir() &&
                            !blockState.getCollisionShape(this.level(), mutableBlockPos).isEmpty() &&
                            !this.level().isClientSide())
                        {
                            this.level().destroyBlock(mutableBlockPos, true);
                        }
                        else {
                            try {
                                blockState.entityInside(this.level(), mutableBlockPos, this);
                                this.onInsideBlock(blockState);
                            }
                            catch (Throwable throwable) {
                                CrashReport crashReport = CrashReport.forThrowable(throwable, "Colliding entity with block");
                                CrashReportCategory crashReportCategory = crashReport.addCategory("Block being collided with");
                                CrashReportCategory.populateBlockDetails(crashReportCategory, this.level(), mutableBlockPos, blockState);
                                throw new ReportedException(crashReport);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void pushEntities() {
        if (this.level().isClientSide()) {
            this.level().getEntities(EntityTypeTest.forClass(Player.class), this.getBoundingBox(), EntitySelector.pushableBy(this)).forEach(this::doPush);
            return;
        }
        List<Entity> list = this.level().getEntities(this, this.getBoundingBox(), EntitySelector.pushableBy(this));
        if (!list.isEmpty()) {
            int entityIndex;
            int maxCrammingLimit = this.level().getGameRules().getInt(GameRules.RULE_MAX_ENTITY_CRAMMING);
            if (maxCrammingLimit > 0 && list.size() > maxCrammingLimit - 1 && this.random.nextInt(4) == 0) {
                entityIndex = 0;
                for (Entity entity : list) {
                    if (entity.isPassenger()) continue;
                    ++entityIndex;
                }
            }

            for (entityIndex = 0; entityIndex < list.size(); ++entityIndex) {
                Entity entity = list.get(entityIndex);
                this.doPush(entity);

                if (entity instanceof LivingEntity livingEntity && !(entity instanceof BoundlessCrystalEntity)) {
                    float damageAmount;
                    float maxHealth = Math.max(livingEntity.getHealth(), livingEntity.getMaxHealth());

                    if (livingEntity instanceof ServerPlayer serverPlayer) {
                        if (serverPlayer.isCreative()) {
                            continue;
                        }

                        if (EssenceOfTheBees.hasEssence(serverPlayer)) {
                            damageAmount = maxHealth / 8;
                        }
                        else {
                            damageAmount = maxHealth / 4;
                        }
                    }
                    else {
                        damageAmount = maxHealth / 8;
                    }

                    livingEntity.hurt(this.level().damageSources().source(BzDamageSources.BOUNDLESS_CRYSTAL_TYPE, this), damageAmount);

                    for(MobEffect mobEffect : new HashSet<>(livingEntity.getActiveEffectsMap().keySet())) {
                        if (mobEffect.isBeneficial()) {
                            livingEntity.removeEffect(mobEffect);
                        }
                    }

                    Vec3 center = livingEntity.getBoundingBox().getCenter();
                    ((ServerLevel)this.level()).sendParticles(
                            ParticleTypes.END_ROD,
                            center.x() + this.random.nextGaussian() / 5,
                            center.y() + this.random.nextGaussian() / 2.5,
                            center.z() + this.random.nextGaussian() / 5,
                            15,
                            (this.random.nextFloat() * this.random.nextGaussian() / 15),
                            (this.random.nextFloat() * this.random.nextGaussian() / 15),
                            (this.random.nextFloat() * this.random.nextGaussian() / 15),
                            (this.random.nextFloat() * 0.035) + 0.085);
                }
            }
        }
    }

    @Override
    protected void onEffectAdded(MobEffectInstance mobEffectInstance, @Nullable Entity entity) {
        super.onEffectAdded(mobEffectInstance, entity);

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.players().forEach(p -> p.connection.send(new ClientboundUpdateMobEffectPacket(this.getId(), mobEffectInstance)));
        }
    }

    @Override
    protected void onEffectRemoved(MobEffectInstance mobEffectInstance) {
        super.onEffectRemoved(mobEffectInstance);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.players().forEach(p -> p.connection.send(new ClientboundRemoveMobEffectPacket(this.getId(), mobEffectInstance.getEffect())));
        }
    }

    @Override
    public void kill() {
        this.setHealth(0);
        this.die(this.damageSources().genericKill());
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damageAmount) {
        if (damageAmount > 1) {
            damageAmount = 1;
        }

        Entity entity2;
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (this.level().isClientSide) {
            return false;
        }
        if (this.isDeadOrDying()) {
            return false;
        }
        if (damageSource.is(DamageTypeTags.IS_FIRE) && this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return false;
        }
        if (this.isSleeping() && !this.level().isClientSide) {
            this.stopSleeping();
        }
        this.noActionTime = 0;

        boolean bl = false;

        if (this.invulnerableTime > 0) {
            return false;
        }
        else {
            this.lastHurt = damageAmount;
            this.invulnerableTime = 20;
            this.actuallyHurt(damageSource, damageAmount);
            this.hurtTime = this.hurtDuration = 10;
        }

        if (damageSource.is(DamageTypeTags.DAMAGES_HELMET) && !this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            this.hurtHelmet(damageSource, damageAmount);
            damageAmount *= 0.75f;
        }

        if ((entity2 = damageSource.getEntity()) != null) {
            if (entity2 instanceof LivingEntity livingEntity2) {
                if (!damageSource.is(DamageTypeTags.NO_ANGER)) {
                    this.setLastHurtByMob(livingEntity2);
                }
            }

            if (entity2 instanceof Player player) {
                this.lastHurtByPlayerTime = 100;
                this.lastHurtByPlayer = player;
            }
            else if (entity2 instanceof TamableAnimal tamableAnimal && tamableAnimal.isTame()) {
                this.lastHurtByPlayerTime = 100;
                LivingEntity livingEntity = tamableAnimal.getOwner();
                this.lastHurtByPlayer = livingEntity instanceof Player ? (Player)livingEntity : null;
            }
        }

        this.level().broadcastDamageEvent(this, damageSource);
        if (!damageSource.is(DamageTypeTags.NO_IMPACT)) {
            this.markHurt();
        }

        if (entity2 != null && !damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
            double xDiff = entity2.getX() - this.getX();
            double zDiff = entity2.getZ() - this.getZ();
            while (xDiff * xDiff + zDiff * zDiff < 1.0E-4) {
                xDiff = (Math.random() - Math.random()) * 0.01;
                zDiff = (Math.random() - Math.random()) * 0.01;
            }
            if (!bl) {
                this.indicateDamage(xDiff, zDiff);
            }
        }

        if (this.isDeadOrDying()) {
            SoundEvent soundEvent = this.getDeathSound();
            if (soundEvent != null) {
                this.playSound(soundEvent, this.getSoundVolume(), this.getVoicePitch());
            }
            this.die(damageSource);
        }
        else {
            this.playHurtSound(damageSource);
        }

        boolean dealtDamage = damageAmount > 0;
        if (dealtDamage) {
            ((LivingEntityAccessor)this).setLastDamageSource(damageSource);
            ((LivingEntityAccessor)this).setLastDamageStamp(this.level().getGameTime());
        }

        if (entity2 instanceof ServerPlayer) {
            CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer)entity2, this, damageSource, damageAmount, damageAmount, bl);
        }

        return damageAmount > 0;
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return entityDimensions.height / 2f;
    }


    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance mobEffectInstance) {
        return true;
    }

    @Override
    protected void dropExperience() {}

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return armorItems;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot equipmentSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {}

    @Override
    public boolean shouldShowName() {
        return false;
    }

    @Override
    public boolean canDisableShield() {
        return true;
    }

    @Override
    public boolean isOnPortalCooldown() {
        return true;
    }

    @Override
    public boolean fireImmune() {
        return false;
    }

    @Override
    public void lavaHurt() {}

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean isInWall() {
        return false;
    }

    @Override
    protected void handleNetherPortal() { }

    @Override
    public boolean isCurrentlyGlowing() {
        return true;
    }

    @Override
    public Entity changeDimension(ServerLevel serverLevel) {
        return this;
    }

    private void spawnFancyParticle(Vec3 center) {
        this.level().addParticle(
                ParticleTypes.END_ROD,
                center.x() + this.random.nextGaussian() / 5,
                center.y() + this.random.nextGaussian() / 2.5,
                center.z() + this.random.nextGaussian() / 5,
                (this.random.nextFloat() * this.random.nextGaussian() / 15),
                (this.random.nextFloat() * this.random.nextGaussian() / 15),
                (this.random.nextFloat() * this.random.nextGaussian() / 15));

        this.level().addParticle(
                BzParticles.SPARKLE_PARTICLE.get(),
                center.x() + this.random.nextGaussian() / 4,
                center.y() + this.random.nextGaussian() / 2.5,
                center.z() + this.random.nextGaussian() / 4,
                (this.random.nextFloat() * this.random.nextGaussian() / 15),
                (this.random.nextFloat() * this.random.nextGaussian() / 15),
                (this.random.nextFloat() * this.random.nextGaussian() / 15));
    }
}