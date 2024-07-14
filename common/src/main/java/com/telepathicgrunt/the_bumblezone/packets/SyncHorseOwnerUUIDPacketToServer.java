package com.telepathicgrunt.the_bumblezone.packets;

import com.teamresourceful.resourcefullib.common.network.Packet;
import com.teamresourceful.resourcefullib.common.network.base.PacketType;
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType;
import com.telepathicgrunt.the_bumblezone.Bumblezone;
import com.telepathicgrunt.the_bumblezone.packets.handlers.SyncHorseOwnerUUIDPacketToServerHandleBody;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;
import java.util.function.Consumer;

public record SyncHorseOwnerUUIDPacketToServer(UUID horseUUID) implements Packet<SyncHorseOwnerUUIDPacketToServer> {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Bumblezone.MODID, "sync_horse_owner_uuid_to_server");
    public static final ServerboundPacketType<SyncHorseOwnerUUIDPacketToServer> TYPE = new SyncHorseOwnerUUIDPacketToServer.Handler();

    public static void sendToServer(UUID horseUUID) {
        MessageHandler.DEFAULT_CHANNEL.sendToServer(new SyncHorseOwnerUUIDPacketToServer(horseUUID));
    }

    @Override
    public PacketType<SyncHorseOwnerUUIDPacketToServer> type() {
        return TYPE;
    }

    private static class Handler implements ServerboundPacketType<SyncHorseOwnerUUIDPacketToServer> {

        @Override
        public void encode(SyncHorseOwnerUUIDPacketToServer message, RegistryFriendlyByteBuf buffer) {
            buffer.writeUUID(message.horseUUID());
        }

        @Override
        public SyncHorseOwnerUUIDPacketToServer decode(RegistryFriendlyByteBuf buffer) {
            return new SyncHorseOwnerUUIDPacketToServer(buffer.readUUID());
        }

        @Override
        public Consumer<Player> handle(SyncHorseOwnerUUIDPacketToServer message) {
            return (player) -> SyncHorseOwnerUUIDPacketToServerHandleBody.handle(message, player);
        }

        @Override
        public Class<SyncHorseOwnerUUIDPacketToServer> type() {
            return SyncHorseOwnerUUIDPacketToServer.class;
        }

        @Override
        public ResourceLocation id() {
            return ID;
        }
    }

}