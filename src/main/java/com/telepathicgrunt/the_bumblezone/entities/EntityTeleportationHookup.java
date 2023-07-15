package com.telepathicgrunt.the_bumblezone.entities;

import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.configs.BzConfig;
import com.telepathicgrunt.the_bumblezone.mixin.entities.PlayerAdvancementsAccessor;
import com.telepathicgrunt.the_bumblezone.modinit.BzCriterias;
import com.telepathicgrunt.the_bumblezone.modinit.BzDimension;
import com.telepathicgrunt.the_bumblezone.modinit.BzTags;
import com.telepathicgrunt.the_bumblezone.utils.EnchantmentUtils;
import com.telepathicgrunt.the_bumblezone.world.dimension.BzWorldSavedData;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntityTeleportationHookup {

    ////////////////////////////////////////////////////////////
    // Methods that setup and call PlayerTeleportationBackend //

    //Notify people of Bumblezone's advancements so they know how to enter dimension
    public static void playerTick(ServerPlayer serverPlayer) {
        Level level = serverPlayer.level;

        Advancement advancement = serverPlayer.server.getAdvancements().getAdvancement(BzCriterias.IS_NEAR_BEEHIVE_ADVANCEMENT);
        Map<Advancement, AdvancementProgress> advancementsProgressMap = ((PlayerAdvancementsAccessor)serverPlayer.getAdvancements()).getAdvancements();
        if (advancement != null &&
                advancementsProgressMap.containsKey(advancement) &&
                advancementsProgressMap.get(advancement).isDone())
        {
            return;
        }

        if (level instanceof ServerLevel serverLevel &&
                (serverLevel.getGameTime() + serverPlayer.getUUID().getLeastSignificantBits()) % 100 == 0 &&
                !serverLevel.dimension().equals(BzDimension.BZ_WORLD_KEY))
        {

            List<PoiRecord> poiInRange = serverLevel.getPoiManager().getInSquare(
                    (pointOfInterestType) -> pointOfInterestType.is(BzTags.IS_NEAR_BEEHIVE_ADVANCEMENT_TRIGGER_POI),
                    serverPlayer.blockPosition(),
                    8,
                    PoiManager.Occupancy.ANY
            ).toList();

            if (poiInRange.size() > 0) {
                BzCriterias.IS_NEAR_BEEHIVE_TRIGGER.trigger(serverPlayer);
                serverPlayer.displayClientMessage(Component.translatable("system.the_bumblezone.advancement_hint"), false);
            }
        }
    }

    //Living Entity ticks
    public static void entityTick(LivingEntity livingEntity) {
        //Makes it so player does not get killed for falling into the void
        if (livingEntity.level.dimension().location().equals(Bumblezone.MOD_DIMENSION_ID)) {
            if (livingEntity.getY() < -2) {
                if(BzConfig.enableExitTeleportation) {
                    if(livingEntity instanceof ServerPlayer) {
                        BzCriterias.TELEPORT_OUT_OF_BUMBLEZONE_TRIGGER.trigger((ServerPlayer) livingEntity);
                    }

                    if (livingEntity.getY() < -4) {
                        livingEntity.moveTo(livingEntity.getX(), -4, livingEntity.getZ());
                        livingEntity.absMoveTo(livingEntity.getX(), -4, livingEntity.getZ());
                        livingEntity.setDeltaMovement(0, 0, 0);
                        if (!livingEntity.level.isClientSide()) {
                            livingEntity.addEffect(new MobEffectInstance(
                                    MobEffects.SLOW_FALLING,
                                    12,
                                    100,
                                    false,
                                    false,
                                    true));
                        }
                    }
                    livingEntity.fallDistance = 0;

                    if(!livingEntity.level.isClientSide()) {
                        teleportOutOfBz(livingEntity);
                    }
                }
            }
            else if (livingEntity.getY() > 255) {
                if(BzConfig.enableExitTeleportation) {
                    if(livingEntity instanceof ServerPlayer) {
                        BzCriterias.TELEPORT_OUT_OF_BUMBLEZONE_TRIGGER.trigger((ServerPlayer) livingEntity);
                    }

                    if (livingEntity.getY() > 257) {
                        livingEntity.moveTo(livingEntity.getX(), 257, livingEntity.getZ());
                        livingEntity.absMoveTo(livingEntity.getX(), 257, livingEntity.getZ());
                    }

                    if (!livingEntity.level.isClientSide()) {
                        teleportOutOfBz(livingEntity);
                    }
                }
            }
        }
    }


    public static void teleportOutOfBz(LivingEntity livingEntity) {
        if (!livingEntity.level.isClientSide()) {
            checkAndCorrectStoredDimension(livingEntity);
            MinecraftServer minecraftServer = livingEntity.getServer(); // the server itself
            ResourceKey<Level> worldKey;

            if (livingEntity.getControllingPassenger() == null) {
                worldKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, Bumblezone.ENTITY_COMPONENT.get(livingEntity).getNonBZDimension());
            }
            else {
                if(livingEntity.getControllingPassenger() instanceof LivingEntity livingEntity2) {
                    checkAndCorrectStoredDimension(livingEntity2);
                }
                worldKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, Bumblezone.ENTITY_COMPONENT.get(livingEntity.getControllingPassenger()).getNonBZDimension());
            }

            ServerLevel serverWorld = minecraftServer.getLevel(worldKey);
            if(serverWorld == null) {
                serverWorld = minecraftServer.getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY,  new ResourceLocation(BzConfig.defaultDimension)));
            }
            BzWorldSavedData.queueEntityToTeleport(livingEntity, serverWorld.dimension());
        }
    }

    // projectile
    public static boolean runTeleportProjectileImpact(HitResult hitResult, Entity thrower, Entity projectile) {
        if (thrower == null || thrower.level == null) {
            return false;
        }

        Level world = thrower.level; // world we threw in

        // Make sure we are on server by checking if thrower is ServerPlayer and that we are not in bumblezone.
        // If onlyOverworldHivesTeleports is set to true, then only run this code in Overworld.
        if (isTeleportAllowedInDimension(world)) {

            // get nearby hives
            BlockPos hivePos = null;
            if (hitResult instanceof BlockHitResult blockHitResult) {
                BlockState block = world.getBlockState(blockHitResult.getBlockPos());
                if(EntityTeleportationBackend.isValidBeeHive(block)) {
                    hivePos = blockHitResult.getBlockPos();
                }
            }

            if(hivePos == null) {
                hivePos = getNearbyHivePos(hitResult.getLocation(), world);
            }

            // if fail, move the hit pos one step based on pearl velocity and try again
            if(hivePos == null) {
                hivePos = getNearbyHivePos(hitResult.getLocation().add(projectile.getDeltaMovement()), world);
            }

            // no hive hit, exit early
            if(hivePos == null) {
                return false;
            }

            //checks if block under hive is correct if config needs one
            boolean validBelowBlock = isValidBelowBlock(world, thrower, hivePos);

            //if the projectile hit a beehive, begin the teleportation.
            if (validBelowBlock) {
                performTeleportation(thrower, projectile);
                return true;
            }
        }
        return false;
    }

    public static boolean runEntityHitCheck(HitResult hitResult, Entity thrower, Entity projectile) {
        if (thrower == null || thrower.level == null) {
            return false;
        }

        Level world = thrower.level; // world we threw in

        // Make sure we are on server by checking if thrower is ServerPlayer and that we are not in bumblezone.
        // If onlyOverworldHivesTeleports is set to true, then only run this code in Overworld.
        if (hitResult instanceof EntityHitResult entityHitResult && isTeleportAllowedInDimension(world)) {
            Entity hitEntity = entityHitResult.getEntity();
            boolean passedCheck = false;

            // Entity type check
            if (hitEntity.getType().is(BzTags.TARGET_ENTITY_HIT_BY_TELEPORT_PROJECTILE_ANYWHERE) ||
                hitEntity.getType().is(BzTags.TARGET_ENTITY_HIT_BY_TELEPORT_PROJECTILE_HIGH) ||
                hitEntity.getType().is(BzTags.TARGET_ENTITY_HIT_BY_TELEPORT_PROJECTILE_LOW))
            {
                Vec3 hitPos = projectile.position();
                AABB boundBox = entityHitResult.getEntity().getBoundingBox();
                double relativeHitY = hitPos.y() - boundBox.minY;
                double entityBoundHeight = boundBox.maxY - boundBox.minY;

                double minYThreshold = Integer.MIN_VALUE;
                double maxYThreshold = Integer.MAX_VALUE;

                if (hitEntity.getType().is(BzTags.TARGET_ENTITY_HIT_BY_TELEPORT_PROJECTILE_HIGH)) {
                    minYThreshold = entityBoundHeight / 2;
                }
                if (hitEntity.getType().is(BzTags.TARGET_ENTITY_HIT_BY_TELEPORT_PROJECTILE_HIGH)) {
                    maxYThreshold = entityBoundHeight / 2;
                }

                if (minYThreshold != maxYThreshold && (relativeHitY > maxYThreshold || relativeHitY < minYThreshold)) {
                    return false;
                }

                passedCheck = true;
            }

            // Held item check
            for (ItemStack stack : hitEntity.getHandSlots()) {
                if (stack == null) {
                    continue;
                }
                if (stack.is(BzTags.TARGET_WITH_HELD_ITEM_HIT_BY_TELEPORT_PROJECTILE)) {
                    passedCheck = true;
                    break;
                }
            }

            // Armor item check
            for (ItemStack stack : hitEntity.getArmorSlots()) {
                if (stack != null && stack.is(BzTags.TARGET_ARMOR_HIT_BY_TELEPORT_PROJECTILE)) {
                    passedCheck = true;
                }
            }

            if (!passedCheck) {
                return false;
            }

            BlockPos hivePos = entityHitResult.getEntity().blockPosition();

            //checks if block under hive is correct if config needs one
            boolean validBelowBlock = isValidBelowBlock(world, thrower, hivePos);

            //if the projectile hit a beehive, begin the teleportation.
            if (validBelowBlock) {
                performTeleportation(thrower, projectile);
                return true;
            }
        }

        return false;
    }

    public static boolean runItemUseOn(Player user, BlockPos clickedPos, BlockState blockstate, ItemStack usingStack) {
        Level world = user.level; // world we use in

        // Make sure we are on server by checking if user is ServerPlayer and that we are not in bumblezone.
        // If onlyOverworldHivesTeleports is set to true, then only run this code in Overworld.
        if (isTeleportAllowedInDimension(world)) {

            if(!EntityTeleportationBackend.isValidBeeHive(blockstate)) {
                return false;
            }

            boolean isAllowTeleportItem = usingStack.is(BzTags.TELEPORT_ITEM_RIGHT_CLICKED_BEEHIVE) ||
                    (usingStack.is(BzTags.TELEPORT_ITEM_RIGHT_CLICKED_BEEHIVE_CROUCHING) && user.isShiftKeyDown());

            if (!isAllowTeleportItem) {
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(usingStack);
                for (Enchantment enchantment : enchantments.keySet()) {
                    if (EnchantmentUtils.isEnchantmentTagged(enchantment, BzTags.ITEM_WITH_TELEPORT_ENCHANT)) {
                        isAllowTeleportItem = true;
                        break;
                    }
                    else if (user.isShiftKeyDown() && EnchantmentUtils.isEnchantmentTagged(enchantment, BzTags.ITEM_WITH_TELEPORT_ENCHANT_CROUCHING)) {
                        isAllowTeleportItem = true;
                        break;
                    }
                }
            }

            if (!isAllowTeleportItem) {
                return false;
            }

            //checks if block under hive is correct if config needs one
            boolean validBelowBlock = isValidBelowBlock(world, user, clickedPos);

            //if the item is valid for teleport on a beehive, begin the teleportation.
            if (validBelowBlock) {
                if (user instanceof ServerPlayer serverPlayer) {
                    BzWorldSavedData.queueEntityToTeleport(serverPlayer, BzDimension.BZ_WORLD_KEY);
                }
                return true;
            }
        }
        return false;
    }

    private static boolean isTeleportAllowedInDimension(Level level) {
        if (!BzConfig.enableEntranceTeleportation || level.dimension().location().equals(Bumblezone.MOD_DIMENSION_ID)) {
            return false;
        }

        if (BzConfig.onlyOverworldHivesTeleports) {
            ResourceLocation defaultDimRL = new ResourceLocation(BzConfig.defaultDimension);
            ResourceKey<Level> worldKey = ResourceKey.create(Registry.DIMENSION_REGISTRY, defaultDimRL);
            return level.dimension().equals(worldKey);
        }

        return true;
    }

    private static void performTeleportation(Entity thrower, Entity projectile) {
        if (thrower instanceof ServerPlayer serverPlayer) {
            if (projectile != null) {
                if (Registry.ENTITY_TYPE.getKey(projectile.getType()).getPath().contains("pearl")) {
                    BzCriterias.TELEPORT_TO_BUMBLEZONE_PEARL_TRIGGER.trigger(serverPlayer);
                }
                projectile.remove(Entity.RemovalReason.DISCARDED);
            }
            BzWorldSavedData.queueEntityToTeleport(serverPlayer, BzDimension.BZ_WORLD_KEY);
        }
    }

    private static boolean isValidBelowBlock(Level world, Entity playerEntity, BlockPos hivePos) {
        Optional<HolderSet.Named<Block>> blockTag = Registry.BLOCK.getTag(BzTags.REQUIRED_BLOCKS_UNDER_HIVE_TO_TELEPORT);
        if (blockTag.isPresent() && blockTag.get().size() != 0) {
            if (world.getBlockState(hivePos.below()).is(BzTags.REQUIRED_BLOCKS_UNDER_HIVE_TO_TELEPORT)) {
                return true;
            }
            else if(BzConfig.warnPlayersOfWrongBlockUnderHive && playerEntity instanceof ServerPlayer serverPlayer) {
                //failed. Block below isn't the required block
                Bumblezone.LOGGER.info("Bumblezone: The attempt to teleport to Bumblezone failed due to not having a block from the following block tag below the hive: the_bumblezone:required_blocks_under_hive_to_teleport");
                Component message = Component.translatable("system.the_bumblezone.require_hive_blocks_failed");
                serverPlayer.displayClientMessage(message, true);
            }
        }
        else {
            return true;
        }
        return false;
    }

    private static BlockPos getNearbyHivePos(Vec3 hitBlockPos, Level world) {
        double checkRadius = 0.5D;
        //check with offset in all direction as the position of exact hit point could barely be outside the hive block
        //even through the pearl hit the block directly.
        for(double offset = -checkRadius; offset <= checkRadius; offset += checkRadius) {
            for(double offset2 = -checkRadius; offset2 <= checkRadius; offset2 += checkRadius) {
                for (double offset3 = -checkRadius; offset3 <= checkRadius; offset3 += checkRadius) {
                    BlockPos offsettedHitPos = new BlockPos(hitBlockPos.add(offset, offset2, offset3));
                    BlockState block = world.getBlockState(offsettedHitPos);
                    if(EntityTeleportationBackend.isValidBeeHive(block)) {
                        return offsettedHitPos;
                    }
                }
            }
        }
        return null;
    }

    public static void runPistonPushed(Direction direction, LivingEntity pushedEntity) {
        if (pushedEntity.level.isClientSide()) {
            return;
        }

        ServerLevel world = (ServerLevel) pushedEntity.level;

        // If onlyOverworldHivesTeleports is set to true, then only run this code in Overworld.
        if (BzConfig.enableEntranceTeleportation &&
            !world.dimension().location().equals(Bumblezone.MOD_DIMENSION_ID) &&
            (!BzConfig.onlyOverworldHivesTeleports || world.dimension().equals(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(BzConfig.defaultDimension)))))
        {
            if(BzWorldSavedData.isEntityQueuedToTeleportAlready(pushedEntity)) return; // Skip checks if entity is teleporting already to Bz.

            BlockPos.MutableBlockPos entityPos = new BlockPos.MutableBlockPos().set(pushedEntity.blockPosition());
            BlockPos[] blockPositions = new BlockPos[]{
                    entityPos,
                    entityPos.relative(direction),
                    entityPos.relative(Direction.UP),
                    entityPos.relative(Direction.UP).relative(direction)
            };
            List<BlockState> belowHiveBlocks = new ArrayList<>();

            // Checks if entity is pushed into hive block
            boolean isPushedIntoBeehive = false;
            for(BlockPos pos : blockPositions) {
                if(EntityTeleportationBackend.isValidBeeHive(world.getBlockState(pos))) {
                    isPushedIntoBeehive = true;
                    belowHiveBlocks.add(world.getBlockState(pos.below()));
                }
            }

            if (isPushedIntoBeehive) {
                //checks if block under hive is correct if config needs one
                boolean validBelowBlock = false;
                Optional<HolderSet.Named<Block>> blockTag = Registry.BLOCK.getTag(BzTags.REQUIRED_BLOCKS_UNDER_HIVE_TO_TELEPORT);
                if(blockTag.isPresent() && blockTag.get().size() != 0) {

                    for(BlockState belowBlock : belowHiveBlocks) {
                        if(belowBlock.is(BzTags.REQUIRED_BLOCKS_UNDER_HIVE_TO_TELEPORT)) {
                            validBelowBlock = true;
                        }
                    }

                    if(!validBelowBlock && BzConfig.warnPlayersOfWrongBlockUnderHive) {
                        if(pushedEntity instanceof Player playerEntity) {
                            //failed. Block below isn't the required block
                            Bumblezone.LOGGER.log(org.apache.logging.log4j.Level.INFO, "Bumblezone: The attempt to teleport to Bumblezone failed due to not having a block from the following block tag below the hive: the_bumblezone:required_blocks_under_hive_to_teleport");
                            Component message = Component.translatable("system.the_bumblezone.require_hive_blocks_failed");
                            playerEntity.displayClientMessage(message, true);
                        }
                        return;
                    }
                }
                else {
                    validBelowBlock = true;
                }

                //if the entity was pushed into a beehive, begin the teleportation.
                if (validBelowBlock) {
                    if(pushedEntity instanceof ServerPlayer) {
                        BzCriterias.TELEPORT_TO_BUMBLEZONE_PISTON_TRIGGER.trigger((ServerPlayer) pushedEntity);
                    }
                    BzWorldSavedData.queueEntityToTeleport(pushedEntity, BzDimension.BZ_WORLD_KEY);
                }
            }
        }
    }


    /**
     * Looks at stored non-bz dimension and changes it to Overworld if it is
     * BZ dimension or the config forces going to Overworld.
     */
    private static void checkAndCorrectStoredDimension(LivingEntity livingEntity) {
        //Error. This shouldn't be. We aren't leaving the bumblezone to go to the bumblezone.
        //Go to Overworld instead as default. Or go to Overworld if config is set.
        if (Bumblezone.ENTITY_COMPONENT.get(livingEntity).getNonBZDimension().equals(Bumblezone.MOD_DIMENSION_ID) || BzConfig.forceExitToOverworld) {
            // go to overworld by default
            //update stored dimension
            Bumblezone.ENTITY_COMPONENT.get(livingEntity).setNonBZDimension(new ResourceLocation(BzConfig.defaultDimension));
        }
    }
}
