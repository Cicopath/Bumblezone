package com.telepathicgrunt.the_bumblezone.utils;

import com.mojang.datafixers.util.Pair;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.items.functions.PrefillMap;
import dev.architectury.registry.registries.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

// Source: https://github.com/thebrightspark/AsyncLocator/blob/master/src/main/java/brightspark/asynclocator/AsyncLocator.java
public class ThreadExecutor {
    private static ExecutorService LOCATING_EXECUTOR_SERVICE = null;
    private static final AtomicInteger runningSearches = new AtomicInteger(0);
    private static final AtomicInteger queuedSearches = new AtomicInteger(0);
    private static final LinkedHashMap<UUID, Optional<BlockPos>> SEARCH_RESULTS = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<UUID, Optional<BlockPos>> eldest) {
            return size() >= 50;
        }
    };

    private static void setupExecutorService() {
        shutdownExecutorService();
        LOCATING_EXECUTOR_SERVICE = Executors.newFixedThreadPool(
                3,
                new ThreadFactory() {
                    private static final AtomicInteger poolNum = new AtomicInteger(1);
                    private final AtomicInteger threadNum = new AtomicInteger(1);
                    private final String namePrefix = "bumblezone_locator_and_teleportation-" + poolNum.getAndIncrement() + "-thread-";

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, namePrefix + threadNum.getAndIncrement());
                    }
                }
        );
    }

    private static void shutdownExecutorService() {
        if (LOCATING_EXECUTOR_SERVICE != null) {
            LOCATING_EXECUTOR_SERVICE.shutdown();
        }
    }

    public static void handleServerAboutToStartEvent() {
        setupExecutorService();
    }

    public static void handleServerStoppingEvent() {
        shutdownExecutorService();
    }

    public static boolean isRunningASearch() {
        return runningSearches.get() > 0;
    }

    public static boolean hasQueuedSearch() {
        return queuedSearches.get() > 0;
    }

    public static Optional<BlockPos> getSearchResult(UUID searchId) {
        synchronized (SEARCH_RESULTS) {
            if (!SEARCH_RESULTS.containsKey(searchId)) {
                return null;
            }

            return SEARCH_RESULTS.get(searchId);
        }
    }

    public static void removeSearchResult(UUID searchId) {
        synchronized (SEARCH_RESULTS) {
            SEARCH_RESULTS.remove(searchId);
        }
    }

    public static LocateTask<BlockPos> locate(
            ServerLevel level,
            TagKey<Structure> structureTag,
            BlockPos pos,
            int searchRadius,
            boolean skipKnownStructures)
    {
        queuedSearches.getAndIncrement();
        CompletableFuture<BlockPos> completableFuture = new CompletableFuture<>();
        Future<?> future = LOCATING_EXECUTOR_SERVICE.submit(
                () ->  {
                    try {
                        runningSearches.getAndIncrement();
                        queuedSearches.getAndDecrement();
                        doLocateLevel(completableFuture, level, structureTag, pos, searchRadius, skipKnownStructures);
                    }
                    catch (Exception e) {
                        Bumblezone.LOGGER.error("Off thread structure locating crashed. Exception is: ", e);
                    }
                }
        );
        return new LocateTask<>(level.getServer(), completableFuture, future);
    }

    public static LocateTask<BlockPos> locate(
            ServerLevel level,
            UUID searchId,
            ResourceKey<Structure> structureKey,
            BlockPos pos,
            int searchRadius,
            boolean skipKnownStructures)
    {
        queuedSearches.getAndIncrement();
        synchronized (SEARCH_RESULTS) {
            SEARCH_RESULTS.put(searchId, Optional.empty());
        }
        CompletableFuture<BlockPos> completableFuture = new CompletableFuture<>();
        Future<?> future = LOCATING_EXECUTOR_SERVICE.submit(
                () ->  {
                    try {
                        runningSearches.getAndIncrement();
                        queuedSearches.getAndDecrement();
                        doLocateLevel(completableFuture, level, searchId, structureKey, pos, searchRadius, skipKnownStructures);
                    }
                    catch (Exception e) {
                        Bumblezone.LOGGER.error("Off thread structure locating crashed. Exception is: ", e);
                    }
                }
        );
        return new LocateTask<>(level.getServer(), completableFuture, future);
    }

    public static LocateTask<Optional<Vec3>> dimensionDestinationSearch(MinecraftServer minecraftServer, Supplier<Optional<Vec3>> searchFunction) {
        CompletableFuture<Optional<Vec3>> completableFuture = new CompletableFuture<>();
        Future<?> future = LOCATING_EXECUTOR_SERVICE.submit(
                () ->  {
                    try {
                        completableFuture.complete(searchFunction.get());
                    }
                    catch (Exception e) {
                        Bumblezone.LOGGER.error("Off thread teleport destination search crashed. Exception is: ", e);
                    }
                }
        );
        return new LocateTask<>(minecraftServer, completableFuture, future);
    }

    private static void doLocateLevel(
            CompletableFuture<BlockPos> completableFuture,
            ServerLevel level,
            TagKey<Structure> structureTag,
            BlockPos pos,
            int searchRadius,
            boolean skipExistingChunks)
    {
        BlockPos foundPos = level.findNearestMapStructure(structureTag, pos, searchRadius, skipExistingChunks);
        completableFuture.complete(foundPos);
        runningSearches.getAndDecrement();
    }

    private static void doLocateLevel(
            CompletableFuture<BlockPos> completableFuture,
            ServerLevel level,
            UUID searchId,
            ResourceKey<Structure> structureKey,
            BlockPos pos,
            int searchRadius,
            boolean skipExistingChunks)
    {
        Registry<Structure> structureRegistry = level.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY);
        Optional<Holder<Structure>> structureRegistryHolder = structureRegistry.getHolder(structureKey);
        if (structureRegistryHolder.isEmpty()) {
            completableFuture.complete(null);
            runningSearches.getAndDecrement();
            synchronized (SEARCH_RESULTS) {
                SEARCH_RESULTS.remove(searchId);
            }
            return;
        }

        Pair<BlockPos, Holder<Structure>> foundPos = level.getChunkSource().getGenerator().findNearestMapStructure(
                level,
                HolderSet.direct(structureRegistryHolder.get()),
                pos,
                searchRadius,
                skipExistingChunks);

        completableFuture.complete(foundPos != null ? foundPos.getFirst() : null);
        runningSearches.getAndDecrement();
        synchronized (SEARCH_RESULTS) {
            if (foundPos == null) {
                SEARCH_RESULTS.remove(searchId);
            }
            else {
                SEARCH_RESULTS.put(searchId, Optional.of(foundPos.getFirst()));
            }
        }
    }

    public static void mapFilling(
            ServerLevel level,
            BlockPos pos,
            MapItemSavedData data)
    {
        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        Future<?> future = LOCATING_EXECUTOR_SERVICE.submit(
                () -> {
                    try {
                        PrefillMap.update(level, pos, data);
                    }
                    catch (Exception e) {
                        Bumblezone.LOGGER.error("Off thread map filling crashed. Exception is: ", e);
                    }
                }
        );
        new LocateTask<>(level.getServer(), completableFuture, future);
    }

    public record LocateTask<T>(MinecraftServer server, CompletableFuture<T> completableFuture, Future<?> taskFuture) {
        /**
         * Helper function that calls {@link CompletableFuture#thenAccept(Consumer)} with the given action.
         * Bear in mind that the action will be executed from the task's thread. If you intend to change any game data,
         * it's strongly advised you use {@link #thenOnServerThread(Consumer)} instead so that it's queued and executed
         * on the main server thread instead.
         */
        public LocateTask<T> then(Consumer<T> action) {
            completableFuture.thenAccept(action);
            return this;
        }

        /**
         * Helper function that calls {@link CompletableFuture#thenAccept(Consumer)} with the given action on the server
         * thread.
         */
        public LocateTask<T> thenOnServerThread(Consumer<T> action) {
            completableFuture.thenAccept(pos -> server.submit(() -> action.accept(pos)));
            return this;
        }

        /**
         * Helper function that cancels both completableFuture and taskFuture.
         */
        public void cancel() {
            taskFuture.cancel(true);
            completableFuture.cancel(false);
        }
    }
}
