package com.telepathicgrunt.the_bumblezone.entities.nonliving;

import com.telepathicgrunt.the_bumblezone.blocks.blockentities.EssenceBlockEntity;
import com.telepathicgrunt.the_bumblezone.items.essence.EssenceOfTheBees;
import com.telepathicgrunt.the_bumblezone.modinit.BzDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class PurpleSpikeEntity extends Entity {
    private static final EntityDataAccessor<Boolean> DATA_ID_SPIKE_CHARGE = SynchedEntityData.defineId(PurpleSpikeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ID_SPIKE_ACTIVE = SynchedEntityData.defineId(PurpleSpikeEntity.class, EntityDataSerializers.BOOLEAN);

    public int spikeChargeTimer = 0;
    public int spikeTimer = 0;
    public boolean spikeChargeClientPhaseTracker = false;
    public int spikeChargeClientTimeTracker = 0;
    private UUID essenceController = null;
    private BlockPos essenceControllerBlockPos = null;
    private ResourceKey<Level> essenceControllerDimension = null;

    public PurpleSpikeEntity(EntityType<? extends PurpleSpikeEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_ID_SPIKE_CHARGE, spikeChargeTimer > 0);
        this.entityData.define(DATA_ID_SPIKE_ACTIVE, spikeTimer > 0);
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

    public boolean hasSpikeCharge() {
        return this.entityData.get(DATA_ID_SPIKE_CHARGE);
    }

    protected void setHasSpikeCharge(boolean hasSpikeCharge) {
        this.entityData.set(DATA_ID_SPIKE_CHARGE, hasSpikeCharge);
    }

    public boolean hasSpike() {
        return this.entityData.get(DATA_ID_SPIKE_ACTIVE);
    }

    protected void setHasSpike(boolean hasSpike) {
        this.entityData.set(DATA_ID_SPIKE_ACTIVE, hasSpike);
    }

    public int getSpikeChargeTimer() {
        return spikeChargeTimer;
    }

    public void setSpikeChargeTimer(int spikeChargeTimer) {
        // Keep spike active. Don't reset to previous state.
        if (this.getSpikeTimer() > 0 && this.spikeChargeTimer == 0) {
            this.addSpikeTimer(spikeChargeTimer);
            return;
        }
        this.spikeChargeTimer = spikeChargeTimer;
    }

    public int getSpikeTimer() {
        return spikeTimer;
    }

    public void setSpikeTimer(int spikeTimer) {
        this.spikeTimer = spikeTimer;
    }

    public void addSpikeTimer(int spikeTimer) {
        this.spikeTimer += spikeTimer;
    }

    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions entityDimensions) {
        return 0;
    }

    public void tick() {
        super.tick();
        boolean hasSpikeCharge = this.hasSpikeCharge();
        boolean hasSpike = this.hasSpike();

        if (this.level().isClientSide()) {
            if (this.tickCount % 2 == 0 && !hasSpikeCharge && hasSpike){
                this.makeParticle(1, false);
            }

            if (this.spikeChargeClientPhaseTracker != hasSpikeCharge) {
                if (!this.spikeChargeClientPhaseTracker && hasSpike) {
                    this.spikeChargeClientTimeTracker = 0;
                }
                this.spikeChargeClientPhaseTracker = hasSpikeCharge;
            }

            if (!hasSpikeCharge && !hasSpike) {
                this.spikeChargeClientTimeTracker = 0;
            }
            else {
                this.spikeChargeClientTimeTracker++;
            }

        }
        else {
            this.setHasSpike(this.getSpikeTimer() > 0);
            this.setHasSpikeCharge(this.getSpikeChargeTimer() > 0);

            if (this.spikeChargeTimer > 0) {
                this.spikeChargeTimer--;
            }
            else if (this.spikeTimer > 0) {
                this.spikeTimer--;
            }
        }

        if (hasSpike && !hasSpikeCharge && this.tickCount % 3 == 0) {
            List<Entity> list = this.level().getEntities(this, this.getBoundingBox());
            if (!list.isEmpty()) {
                for (Entity entity : list) {
                    if (entity instanceof LivingEntity livingEntity) {
                        float damageAmount;
                        float maxHealth = Math.max(livingEntity.getHealth(), livingEntity.getMaxHealth());

                        if (livingEntity instanceof ServerPlayer serverPlayer) {
                            if (serverPlayer.isCreative()) {
                                continue;
                            }

                            if (EssenceOfTheBees.hasEssence(serverPlayer)) {
                                damageAmount = maxHealth / 5;
                            }
                            else {
                                damageAmount = maxHealth / 3;
                            }
                        }
                        else {
                            damageAmount = maxHealth / 10;
                        }

                        livingEntity.hurt(this.level().damageSources().source(BzDamageSources.SPIKE_TYPE, this), damageAmount);
                        this.makeParticle(1, true);

                        if (this.level().isClientSide()) {
                            for(MobEffect mobEffect : new HashSet<>(livingEntity.getActiveEffectsMap().keySet())) {
                                if (mobEffect.isBeneficial()) {
                                    livingEntity.removeEffect(mobEffect);
                                }
                            }

                            livingEntity.addEffect(new MobEffectInstance(
                                    MobEffects.POISON,
                                    200,
                                    1,
                                    true,
                                    true,
                                    true));
                        }
                    }
                }
            }
        }

        if (!this.level().isClientSide() && this.tickCount % 20 == 0) {
            checkIfStillInEvent();
        }
    }

    private void checkIfStillInEvent() {
        UUID essenceUuid = this.getEssenceController();
        ResourceKey<Level> essenceDimension = this.getEssenceControllerDimension();
        BlockPos essenceBlockPos = this.getEssenceControllerBlockPos();

        if (essenceBlockPos == null || essenceUuid == null || essenceDimension == null) {
            return;
        }

        BlockPos blockPos = this.blockPosition();
        EssenceBlockEntity essenceBlockEntity = EssenceBlockEntity.getEssenceBlockAtLocation(this.level(), essenceDimension, essenceBlockPos, essenceUuid);

        if (essenceBlockEntity != null) {
            BlockPos arenaSize = essenceBlockEntity.getArenaSize();
            if (Math.abs(blockPos.getX() - essenceBlockPos.getX()) > (arenaSize.getX() / 2) ||
                Math.abs(blockPos.getY() - essenceBlockPos.getY()) > (arenaSize.getY() / 2) ||
                Math.abs(blockPos.getZ() - essenceBlockPos.getZ()) > (arenaSize.getZ() / 2))
            {
                //Failed check. Kill mob.
                this.remove(RemovalReason.DISCARDED);
            }
        }
        else {
            //Failed check. Kill mob.
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public void baseTick() {}

    private void makeParticle(int particlesToSpawn, boolean hit) {
        if (particlesToSpawn > 0) {
            if (hit) {
                Vec3 center = this.blockPosition().getCenter();
                for(int i = 0; i < particlesToSpawn; ++i) {
                    this.level().addParticle(
                            ParticleTypes.CRIT,
                            center.x(),
                            center.y() - 0.5,
                            center.z(),
                            (this.random.nextFloat() - 0.5f),
                            (this.random.nextFloat() + 0.5f),
                            (this.random.nextFloat() - 0.5f));
                    this.level().addParticle(
                            ParticleTypes.DAMAGE_INDICATOR,
                            center.x(),
                            center.y() - 0.5,
                            center.z(),
                            (this.random.nextFloat() - 0.5f),
                            (this.random.nextFloat() + 0.5f),
                            (this.random.nextFloat() - 0.5f));
                }
            }
            else {
                double size = this.getBoundingBox().getSize();
                double x = this.getX() + ((this.random.nextFloat() - 0.5f) * size);
                double y = this.getY() + (size / 3) + ((this.random.nextFloat() - 0.5f) * size);
                double z = this.getZ() + ((this.random.nextFloat() - 0.5f) * size);

                for(int i = 0; i < particlesToSpawn; ++i) {
                    this.level().addParticle(
                            ParticleTypes.ENCHANTED_HIT,
                            x,
                            y,
                            z,
                            (this.random.nextFloat() - 0.5f) * 0.05f,
                            (this.random.nextFloat() + 0.5f) * 0.5f,
                            (this.random.nextFloat() - 0.5f) * 0.05f);
                }
            }
        }
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    protected void handleNetherPortal() { }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public Entity changeDimension(ServerLevel serverLevel) {
        return this;
    }

    @Override
    public int getPortalCooldown() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean shouldRender(double x, double y, double z) {
        return this.hasSpike() || this.hasSpikeCharge();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        this.setSpikeTimer(compoundTag.getInt("spike_timer"));
        this.setSpikeChargeTimer(compoundTag.getInt("spike_charge_timer"));

        if (compoundTag.contains("essenceController")) {
            this.setEssenceController(compoundTag.getUUID("essenceController"));
        }
        if (compoundTag.contains("essenceControllerBlockPos")) {
            this.setEssenceControllerBlockPos(NbtUtils.readBlockPos(compoundTag.getCompound("essenceControllerBlockPos")));
        }
        if (compoundTag.contains("essenceControllerDimension")) {
            this.setEssenceControllerDimension(ResourceKey.create(Registries.DIMENSION, new ResourceLocation(compoundTag.getString("essenceControllerDimension"))));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("spike_timer", this.getSpikeTimer());
        compoundTag.putInt("spike_charge_timer", this.getSpikeChargeTimer());

        if (this.getEssenceController() != null) {
            compoundTag.putUUID("essenceController", this.getEssenceController());
        }
        if (this.getEssenceControllerBlockPos() != null) {
            compoundTag.put("essenceControllerBlockPos", NbtUtils.writeBlockPos(this.getEssenceControllerBlockPos()));
        }
        if (this.getEssenceControllerDimension() != null) {
            compoundTag.putString("essenceControllerDimension", this.getEssenceControllerDimension().location().toString());
        }
    }
}