package com.telepathicgrunt.the_bumblezone.entities.teleportation;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.events.lifecycle.ServerLevelTickEvent;
import com.telepathicgrunt.the_bumblezone.modinit.BzDimension;
import com.telepathicgrunt.the_bumblezone.modules.base.ModuleHelper;
import com.telepathicgrunt.the_bumblezone.modules.registry.ModuleRegistry;
import com.telepathicgrunt.the_bumblezone.utils.PlatformHooks;
import com.telepathicgrunt.the_bumblezone.utils.ThreadExecutor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class BzWorldSavedData extends SavedData {
	private static final String TELEPORTATION_DATA = Bumblezone.MODID + "teleportation";
	private static final BzWorldSavedData CLIENT_DUMMY = new BzWorldSavedData(null);
	private static final List<QueuedEntityData> QUEUED_ENTITIES_TO_TELEPORT_FOR_BUMBLEZONE = new ArrayList<>();
	private static final List<QueuedEntityData> QUEUED_ENTITIES_TO_GENERIC_TELEPORT = new ArrayList<>();
	private static final List<NextTickRunnable> RUNNABLES_FOR_NEXT_TICK = new ArrayList<>();

	public BzWorldSavedData(CompoundTag tag) {}

	public static BzWorldSavedData get(Level world) {
		if (!(world instanceof ServerLevel)) {
			return CLIENT_DUMMY;
		}

		DimensionDataStorage storage = ((ServerLevel)world).getDataStorage();
		return storage.get(BzWorldSavedData::new, TELEPORTATION_DATA);
	}

	@Override
	public CompoundTag save(CompoundTag data)
	{
		return null;
	}

	public static void queueEntityToTeleport(Entity entity, ResourceKey<Level> destination) {
		if(entity != null && !entity.level().isClientSide() && !isEntityQueuedToTeleportAlready(entity)) {
			QUEUED_ENTITIES_TO_TELEPORT_FOR_BUMBLEZONE.add(new QueuedEntityData(entity, destination));
		}
	}

	public static void queueEntityToGenericTeleport(Entity entity, ResourceKey<Level> destination, BlockPos destinationPos, Runnable runnable) {
		if (entity != null && !entity.level().isClientSide() && !isEntityQueuedToGenericTeleportAlready(entity)) {
			QUEUED_ENTITIES_TO_GENERIC_TELEPORT.add(new QueuedEntityData(entity, destination, destinationPos, runnable));
		}
	}

	public static boolean isEntityQueuedToTeleportAlready(Entity entity) {
		return QUEUED_ENTITIES_TO_TELEPORT_FOR_BUMBLEZONE.stream().anyMatch(entry -> entry.getEntity().equals(entity));
	}

	public static boolean isEntityQueuedToGenericTeleportAlready(Entity entity) {
		return QUEUED_ENTITIES_TO_GENERIC_TELEPORT.stream().anyMatch(entry -> entry.getEntity().equals(entity));
	}

	public static void worldTick(ServerLevelTickEvent event){
		if(event.end()){
			BzWorldSavedData.tick((ServerLevel) event.getLevel());
		}
	}

	public static void tick(ServerLevel world) {
		RUNNABLES_FOR_NEXT_TICK.removeIf(r -> r.executeTick(world));

		if(QUEUED_ENTITIES_TO_GENERIC_TELEPORT.size() > 0) {
			Set<Entity> teleportedEntities = new HashSet<>();
			for (QueuedEntityData entry : QUEUED_ENTITIES_TO_GENERIC_TELEPORT) {
				ResourceKey<Level> destinationKey = entry.getDestination();
				BlockPos destinationPos = entry.getDestinationPos();
				Entity entity = entry.getEntity();

				ServerLevel destination = world.getLevel().getServer().getLevel(destinationKey);
				baseTeleporting(entity, destinationPos.getCenter(), destination, teleportedEntities, entity);
				entry.runnable.run();
			}

			// remove all entities that were teleported from the queue
			QUEUED_ENTITIES_TO_GENERIC_TELEPORT.removeIf(entry -> teleportedEntities.contains(entry.getEntity()));
		}

		if(QUEUED_ENTITIES_TO_TELEPORT_FOR_BUMBLEZONE.size() > 0) {
			Set<Entity> teleportedEntities = new HashSet<>();
			for (QueuedEntityData entry : QUEUED_ENTITIES_TO_TELEPORT_FOR_BUMBLEZONE) {
				if (!entry.getIsCurrentTeleporting()) {
					entry.setIsCurrentTeleporting(true);
					ResourceKey<Level> destinationKey = entry.getDestination();
					if (destinationKey.equals(BzDimension.BZ_WORLD_KEY)) {
						if (entry.getEntity() instanceof ServerPlayer serverPlayer) {
							serverPlayer.displayClientMessage(Component.translatable("system.the_bumblezone.teleporting_into_bz"), true);
						}

						ThreadExecutor.dimensionDestinationSearch(world.getServer(), () -> {
								try {
									ServerLevel bumblezoneWorld = world.getServer().getLevel(BzDimension.BZ_WORLD_KEY);
									return Optional.of(EntityTeleportationBackend.getBzCoordinate(entry.getEntity(), world, bumblezoneWorld));
								}
								catch (Throwable e){
									Bumblezone.LOGGER.error("Bumblezone: Failed to teleport entity. Error:", e);
									return Optional.empty();
								}
							})
							.thenOnServerThread(entry::setDestinationPosFound);
					}
					else {
						if (entry.getEntity() instanceof ServerPlayer serverPlayer) {
							serverPlayer.displayClientMessage(Component.translatable("system.the_bumblezone.teleporting_out_of_bz"), true);
						}

						ThreadExecutor.dimensionDestinationSearch(world.getServer(), () -> {
								try {
									ServerLevel destination = world.getLevel().getServer().getLevel(destinationKey);
									return Optional.of(EntityTeleportationBackend.destPostFromOutOfBoundsTeleport(entry.getEntity(), destination));
								}
								catch (Throwable e){
									Bumblezone.LOGGER.error("Bumblezone: Failed to teleport entity. Error:", e);
									return Optional.empty();
								}
							})
							.thenOnServerThread(entry::setDestinationPosFound);
					}
				}
				else if (entry.getDestinationPosFound() != null) {
					// Skip entities that were already teleported due to riding a vehicle that teleported
					Entity entity = entry.getEntity();
					if(teleportedEntities.contains(entity)) continue;

					ResourceKey<Level> destinationKey = entry.getDestination();
					ServerLevel destination = world.getLevel().getServer().getLevel(destinationKey);

					if (entry.getDestinationPosFound().isPresent()) {
						Vec3 destinationPos = entry.getDestinationPosFound().get();
						// Teleport the entity's root vehicle and its passengers to the desired dimension.
						// Also updates teleportedEntities to keep track of which entity was teleported.
						if (destinationKey.equals(BzDimension.BZ_WORLD_KEY)) {
							enteringBumblezone(entity, destinationPos, teleportedEntities);
						}
						else {
							if (entity.getControllingPassenger() != null) {
								exitingBumblezone(entity.getControllingPassenger(), destinationPos, destination, teleportedEntities);
							}
							else {
								exitingBumblezone(entity, destinationPos, destination, teleportedEntities);
							}
						}
					}
					else {
						teleportedEntities.add(entity);
						if (entity instanceof ServerPlayer serverPlayer) {
							serverPlayer.displayClientMessage(Component.translatable("system.the_bumblezone.failed_teleporting"), false);
							Bumblezone.LOGGER.error("Bumblezone: Failed to teleport entity. Aborting teleportation. Please retry. Entity: {}-{} Pos: {} Destination: {}", entity.getClass().getSimpleName(), entity.getName(), entity.position(), destinationKey);
						}
					}
				}
			}

			// remove all entities that were teleported from the queue
			QUEUED_ENTITIES_TO_TELEPORT_FOR_BUMBLEZONE.removeIf(entry -> teleportedEntities.contains(entry.getEntity()));
		}
	}

	public static void enteringBumblezone(Entity entity, Vec3 destinationPosFound, Set<Entity> teleportedEntities) {
		//Note, the player does not hold the previous dimension oddly enough.
		if (!entity.level().isClientSide()) {
			MinecraftServer minecraftServer = entity.getServer(); // the server itself
			ServerLevel bumblezoneWorld = minecraftServer.getLevel(BzDimension.BZ_WORLD_KEY);
			BlockPos blockPos = BlockPos.containing(destinationPosFound);

			if (bumblezoneWorld != null && bumblezoneWorld.getBlockState(blockPos.above()).isSuffocating(bumblezoneWorld, blockPos.above())) {
				RUNNABLES_FOR_NEXT_TICK.add(new NextTickRunnable(bumblezoneWorld.getGameTime() + 5, () -> {
					//We are going to spawn player at exact spot of scaled coordinates by placing air at the spot with honeycomb bottom
					//and honeycomb walls to prevent drowning
					//This is the last resort
					ServerPlayer fakePlayer = createSilkTouchFakePlayer(bumblezoneWorld);
					destroyAndPlaceBlock(bumblezoneWorld, fakePlayer, blockPos, Blocks.AIR);
					destroyAndPlaceBlock(bumblezoneWorld, fakePlayer, blockPos.above(), Blocks.AIR);

					destroyAndPlaceBlock(bumblezoneWorld, fakePlayer, blockPos.below(), Blocks.HONEYCOMB_BLOCK);
					destroyAndPlaceBlock(bumblezoneWorld, fakePlayer, blockPos.above().above(), Blocks.HONEYCOMB_BLOCK);

					destroyAndPlaceBlock(bumblezoneWorld, fakePlayer, blockPos.north(), Blocks.HONEYCOMB_BLOCK);
					destroyAndPlaceBlock(bumblezoneWorld, fakePlayer, blockPos.west(), Blocks.HONEYCOMB_BLOCK);
					destroyAndPlaceBlock(bumblezoneWorld, fakePlayer, blockPos.east(), Blocks.HONEYCOMB_BLOCK);
					destroyAndPlaceBlock(bumblezoneWorld, fakePlayer, blockPos.south(), Blocks.HONEYCOMB_BLOCK);
					destroyAndPlaceBlock(bumblezoneWorld, fakePlayer, blockPos.north().above(), Blocks.HONEYCOMB_BLOCK);
					destroyAndPlaceBlock(bumblezoneWorld, fakePlayer, blockPos.west().above(), Blocks.HONEYCOMB_BLOCK);
					destroyAndPlaceBlock(bumblezoneWorld, fakePlayer, blockPos.east().above(), Blocks.HONEYCOMB_BLOCK);
					destroyAndPlaceBlock(bumblezoneWorld, fakePlayer, blockPos.south().above(), Blocks.HONEYCOMB_BLOCK);
				}));
			}

			ModuleHelper.getModule(entity, ModuleRegistry.ENTITY_POS_AND_DIM).ifPresent(capability -> {
				capability.setNonBZPos(entity.position());
				capability.setNonBZDim(entity.level().dimension().location());

				// Prevent crash due to mojang bug that makes mod's json dimensions not exist upload first creation of world on server. A restart fixes this.
				if (bumblezoneWorld == null) {
					if (entity instanceof ServerPlayer playerEntity) {
						Bumblezone.LOGGER.info("Bumblezone: Please restart the server. The Bumblezone dimension hasn't been made yet due to this bug: https://bugs.mojang.com/browse/MC-195468. A restart will fix this.");
						MutableComponent message = Component.translatable("system.the_bumblezone.missing_dimension", Component.translatable("system.the_bumblezone.missing_dimension_link").withStyle(ChatFormatting.RED));
						playerEntity.displayClientMessage(message, false);
					}
					teleportedEntities.add(entity);
					return;
				}

				Entity baseVehicle = entity.getRootVehicle();
				baseTeleporting(entity, destinationPosFound, bumblezoneWorld, teleportedEntities, baseVehicle);
			});
		}
	}

	public static void exitingBumblezone(Entity entity, Vec3 destinationPosition, ServerLevel destination, Set<Entity> teleportedEntities) {
		BlockPos destBlockPos = BlockPos.containing(destinationPosition);
		if (destination.getBlockState(destBlockPos.above()).isSuffocating(destination, destBlockPos.above())) {
			RUNNABLES_FOR_NEXT_TICK.add(new NextTickRunnable(destination.getGameTime() + 5, () -> {
				ServerPlayer fakePlayer = createSilkTouchFakePlayer(destination);
				destroyAndPlaceBlock(destination, fakePlayer, destBlockPos, Blocks.AIR);
				destroyAndPlaceBlock(destination, fakePlayer, destBlockPos.above(), Blocks.AIR);
			}));
		}
		Entity baseVehicle = entity.getRootVehicle();
		baseTeleporting(entity, destinationPosition, destination, teleportedEntities, baseVehicle);
	}

	private static void destroyAndPlaceBlock(ServerLevel level, ServerPlayer fakePlayer, BlockPos blockPos, Block block) {
		BlockState state = level.getBlockState(blockPos);
		BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(blockPos) : null;
		Block.dropResources(state, level, blockPos, blockEntity, fakePlayer, fakePlayer.getMainHandItem());
		level.setBlockAndUpdate(blockPos, block.defaultBlockState());
	}

	private static ServerPlayer createSilkTouchFakePlayer(ServerLevel level) {
		ServerPlayer serverPlayer = PlatformHooks.getFakePlayer(level, null);
		ItemStack fakeHandItem = Items.STONE_PICKAXE.getDefaultInstance();
		HashMap<Enchantment, Integer> enchantmentHashMap = new HashMap<>();
		enchantmentHashMap.put(Enchantments.SILK_TOUCH, 1);
		EnchantmentHelper.setEnchantments(enchantmentHashMap, fakeHandItem);
		serverPlayer.setItemInHand(InteractionHand.MAIN_HAND, fakeHandItem);
		return serverPlayer;
	}

	private static void baseTeleporting(Entity entity, Vec3 destinationPosition, ServerLevel destination, Set<Entity> teleportedEntities, Entity baseVehicle) {
		teleportEntityAndAssignToVehicle(baseVehicle, null, destination, destinationPosition, teleportedEntities);
		((ServerLevel) entity.level()).resetEmptyTime();
		destination.resetEmptyTime();
	}

	private static void teleportEntityAndAssignToVehicle(Entity entity, Entity vehicle, ServerLevel destination, Vec3 destinationPosition, Set<Entity> teleportedEntities) {
		Entity teleportedEntity;
		List<Entity> passengers = entity.getPassengers();
		entity.ejectPassengers();
		entity.setPortalCooldown();

		if(destination.dimension().equals(BzDimension.BZ_WORLD_KEY)) {
			ModuleHelper.getModule(entity, ModuleRegistry.ENTITY_POS_AND_DIM).ifPresent(capability -> {
				capability.setNonBZPos(entity.position());
				capability.setNonBZDim(entity.level().dimension().location());
			});
		}

		if (entity instanceof ServerPlayer serverPlayer) {

			if (serverPlayer.isSleeping()) {
				serverPlayer.stopSleepInBed(true, true);
			}

			if (PlatformHooks.isDimensionAllowed(serverPlayer, destination.dimension())) {
				serverPlayer.connection.send(new ClientboundRespawnPacket(destination.dimensionTypeId(), destination.dimension(), BiomeManager.obfuscateSeed(destination.getSeed()), serverPlayer.gameMode.getGameModeForPlayer(), serverPlayer.gameMode.getPreviousGameModeForPlayer(), destination.isDebug(), destination.isFlat(), (byte)3, serverPlayer.getLastDeathLocation(), serverPlayer.getPortalCooldown()));
				serverPlayer.connection.send(new ClientboundChangeDifficultyPacket(destination.getDifficulty(), destination.getLevelData().isDifficultyLocked()));
				serverPlayer.teleportTo(destination, destinationPosition.x, destinationPosition.y + 0.1f, destinationPosition.z, serverPlayer.getYRot(), serverPlayer.getXRot());
				serverPlayer.setPortalCooldown();
				serverPlayer.server.getPlayerList().sendLevelInfo(serverPlayer, destination);
				serverPlayer.server.getPlayerList().sendAllPlayerInfo(serverPlayer);
				serverPlayer.addEffect(new MobEffectInstance(
						MobEffects.SLOW_FALLING,
						20,
						1,
						false,
						false,
						false));
				teleportedEntity = destination.getPlayerByUUID(serverPlayer.getUUID());
			}
			else {
				teleportedEntity = null;
			}
		}
		else {
			Entity newEntity = entity;
			newEntity = newEntity.getType().create(destination);
			if (newEntity == null) {
				return;
			}
			newEntity.restoreFrom(entity);
			newEntity.moveTo(destinationPosition.x, destinationPosition.y, destinationPosition.z, entity.getYRot(), entity.getXRot());
			newEntity.setPortalCooldown();
			destination.addDuringTeleport(newEntity);
			teleportedEntity = newEntity;
			entity.remove(Entity.RemovalReason.CHANGED_DIMENSION);
		}

		// update set to keep track of entities teleported
		teleportedEntities.add(entity);

		if(teleportedEntity != null) {
			ChunkPos chunkpos = new ChunkPos(BlockPos.containing(destinationPosition.x, destinationPosition.y, destinationPosition.z));
			destination.getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 1, entity.getId());

			if(vehicle != null) {
				teleportedEntity.startRiding(vehicle);
			}
			if(teleportedEntity instanceof LivingEntity livingEntity) {
				if (livingEntity instanceof ServerPlayer serverPlayer) {
					for(MobEffectInstance effectInstance : new ArrayList<>(livingEntity.getActiveEffects())) {
						serverPlayer.connection.send(new ClientboundUpdateMobEffectPacket(serverPlayer.getId(), effectInstance));
					}
				}
				else {
					reAddStatusEffect(livingEntity);
				}
			}

			passengers.forEach(passenger -> teleportEntityAndAssignToVehicle(passenger, teleportedEntity, destination, destinationPosition, teleportedEntities));
		}
	}


	///////////
	// Utils //

	/**
	 * Temporary fix until Mojang patches the bug that makes potion effect icons disappear when changing dimension.
	 * To fix it ourselves, we remove the effect and re-add it to the player.
	 */
	private static void reAddStatusEffect(LivingEntity livingEntity) {
		//re-adds potion effects so the icon remains instead of disappearing when changing dimensions due to a bug
		ArrayList<MobEffectInstance> effectInstanceList = new ArrayList<>(livingEntity.getActiveEffects());
		for (int i = effectInstanceList.size() - 1; i >= 0; i--) {
			MobEffectInstance effectInstance = effectInstanceList.get(i);
			if (effectInstance != null) {
				livingEntity.removeEffect(effectInstance.getEffect());
				livingEntity.addEffect(
						new MobEffectInstance(
								effectInstance.getEffect(),
								effectInstance.getDuration(),
								effectInstance.getAmplifier(),
								effectInstance.isAmbient(),
								effectInstance.isVisible(),
								effectInstance.showIcon()));
			}
		}
	}

	private record NextTickRunnable(long targetTick, Runnable runnable) {
		public boolean executeTick(ServerLevel serverLevel) {
			if (serverLevel.getGameTime() >= targetTick()) {
				runnable.run();
				return true;
			}
			return false;
		}
	}

	private static final class QueuedEntityData {
		private final Entity entity;
		private final ResourceKey<Level> destination;
		private final BlockPos destinationPos;
		private final Runnable runnable;
		private boolean isCurrentTeleporting = false;
		private Optional<Vec3> destinationPosFound = null;

		public QueuedEntityData(Entity entity, ResourceKey<Level> destination) {
			this.entity = entity;
			this.destination = destination;
			this.destinationPos = null;
			this.runnable = null;
		}

		public QueuedEntityData(Entity entity,
								ResourceKey<Level> destination,
								BlockPos destinationPos,
								Runnable runnable)
		{
			this.entity = entity;
			this.destination = destination;
			this.destinationPos = destinationPos;
			this.runnable = runnable;
		}

		public Entity getEntity() {
			return entity;
		}

		public ResourceKey<Level> getDestination() {
			return destination;
		}

		public BlockPos getDestinationPos() {
			return destinationPos;
		}

		public Runnable getRunnable() {
			return runnable;
		}

		public Optional<Vec3> getDestinationPosFound() {
			return destinationPosFound;
		}

		public void setDestinationPosFound(Optional<Vec3> destinationPosFound) {
			this.destinationPosFound = destinationPosFound;
		}

		public boolean getIsCurrentTeleporting() {
			return isCurrentTeleporting;
		}

		public void setIsCurrentTeleporting(boolean isCurrentTeleporting) {
			this.isCurrentTeleporting = isCurrentTeleporting;
		}
	}
}