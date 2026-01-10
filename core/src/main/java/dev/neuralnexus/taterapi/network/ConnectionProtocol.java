/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network;

import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.PacketFlow;
import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.ServerboundCustomQueryAnswerPacket;

import io.netty.buffer.ByteBuf;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

import org.jspecify.annotations.NonNull;

public enum ConnectionProtocol {
    HANDSHAKING("handshake"),
    STATUS("status"),
    LOGIN("login") {
        {
            serverbound.register(
                    0x02,
                    ServerboundCustomQueryAnswerPacket.class,
                    "minecraft:custom_query_answer",
                    ServerboundCustomQueryAnswerPacket.STREAM_CODEC);
            clientbound.register(
                    0x04,
                    ClientboundCustomQueryPacket.class,
                    "minecraft:custom_query",
                    ClientboundCustomQueryPacket.STREAM_CODEC);
        }
    },
    CONFIGURATION("configuration"),
    PLAY("play");

    private final @NonNull String id;

    ConnectionProtocol(@NonNull String id) {
        this.id = id;
    }

    public @NonNull String id() {
        return this.id;
    }

    protected final PacketRegistry clientbound = new PacketRegistry(PacketFlow.CLIENTBOUND, this);
    protected final PacketRegistry serverbound = new PacketRegistry(PacketFlow.SERVERBOUND, this);

    public @NonNull PacketRegistry getProtocolRegistry(final PacketFlow direction) {
        return direction == PacketFlow.CLIENTBOUND ? this.clientbound : this.serverbound;
    }

    public @NonNull StreamCodec<? extends ByteBuf, ? extends Packet> codec(
            final @NonNull PacketFlow direction, final int id) {
        return this.getProtocolRegistry(direction).codec(id);
    }

    public static class PacketRegistry {
        public final @NonNull PacketFlow direction;
        public final @NonNull ConnectionProtocol protocol;
        final IntObjectMap<ProtocolInfo<? extends Packet>> intToProtocolInfo =
                new IntObjectHashMap<>(16, 0.5f);

        public PacketRegistry(
                final @NonNull PacketFlow direction, final @NonNull ConnectionProtocol protocol) {
            this.direction = direction;
            this.protocol = protocol;
        }

        public void register(
                int id,
                @NonNull Class<? extends Packet> clazz,
                @NonNull String identifier,
                @NonNull StreamCodec<? extends ByteBuf, ? extends Packet> codec) {
            this.intToProtocolInfo.put(id, new ProtocolInfo<>(clazz, identifier, codec));
        }

        public @NonNull StreamCodec<? extends ByteBuf, ? extends Packet> codec(int id) {
            ProtocolInfo<? extends Packet> protocolInfo = this.intToProtocolInfo.get(id);
            if (protocolInfo == null) {
                throw new IllegalArgumentException(
                        "No protocol info found for id: "
                                + id
                                + " in "
                                + this.direction.id()
                                + " "
                                + this.protocol.id());
            }
            return protocolInfo.codec();
        }
    }
}
