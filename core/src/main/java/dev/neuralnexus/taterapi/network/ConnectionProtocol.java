/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.PacketFlow;
import dev.neuralnexus.taterapi.network.protocol.common.ClientboundCustomPayloadPacket;
import dev.neuralnexus.taterapi.network.protocol.common.ServerboundCustomPayloadPacket;
import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.ServerboundCustomQueryAnswerPacket;

import io.netty.buffer.ByteBuf;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

import org.jspecify.annotations.NonNull;

import java.util.Optional;
import java.util.stream.Stream;

public enum ConnectionProtocol {
    HANDSHAKING("handshake"),
    STATUS("status"),
    LOGIN("login") {
        {
            serverbound.register(
                    ServerboundCustomQueryAnswerPacket.class,
                    "minecraft:custom_query_answer",
                    ServerboundCustomQueryAnswerPacket.STREAM_CODEC,
                    map(0x02));
            clientbound.register(
                    ClientboundCustomQueryPacket.class,
                    "minecraft:custom_query",
                    ClientboundCustomQueryPacket.STREAM_CODEC,
                    map(0x04));
        }
    },
    CONFIGURATION("configuration") {
        {
            serverbound.register(
                    ServerboundCustomPayloadPacket.class,
                    "minecraft:custom_payload",
                    ServerboundCustomPayloadPacket.STREAM_CODEC,
                    map(0x01, MinecraftVersions.V20_2),
                    map(0x02, MinecraftVersions.V20_5));
            clientbound.register(
                    ClientboundCustomPayloadPacket.class,
                    "minecraft:custom_payload",
                    ClientboundCustomPayloadPacket.STREAM_CODEC,
                    map(0x00, MinecraftVersions.V20_2),
                    map(0x01, MinecraftVersions.V20_5));
        }
    },
    PLAY("play") {
        {
            serverbound.register(
                    ServerboundCustomPayloadPacket.class,
                    "minecraft:custom_payload",
                    ServerboundCustomPayloadPacket.STREAM_CODEC,
                    map(0x17, MinecraftVersions.V7_2), // TODO: Backport to older
                    map(0x09, MinecraftVersions.V9),
                    map(0x0A, MinecraftVersions.V12),
                    map(0x09, MinecraftVersions.V12_1),
                    map(0x0A, MinecraftVersions.V13),
                    map(0x0B, MinecraftVersions.V14),
                    map(0x0A, MinecraftVersions.V17),
                    map(0x0C, MinecraftVersions.V19),
                    map(0x0D, MinecraftVersions.V19_1),
                    map(0x0C, MinecraftVersions.V19_3),
                    map(0x0D, MinecraftVersions.V19_4),
                    map(0x0F, MinecraftVersions.V20_2),
                    map(0x10, MinecraftVersions.V20_3),
                    map(0x12, MinecraftVersions.V20_5),
                    map(0x14, MinecraftVersions.V21_2),
                    map(0x15, MinecraftVersions.V21_6));
            clientbound.register(
                    ClientboundCustomPayloadPacket.class,
                    "minecraft:custom_payload",
                    ClientboundCustomPayloadPacket.STREAM_CODEC,
                    map(0x3F, MinecraftVersions.V7_2), // TODO: Backport to older
                    map(0x18, MinecraftVersions.V9),
                    map(0x19, MinecraftVersions.V13),
                    map(0x18, MinecraftVersions.V14),
                    map(0x19, MinecraftVersions.V15),
                    map(0x18, MinecraftVersions.V16),
                    map(0x17, MinecraftVersions.V16_2),
                    map(0x18, MinecraftVersions.V17),
                    map(0x15, MinecraftVersions.V19),
                    map(0x16, MinecraftVersions.V19_1),
                    map(0x15, MinecraftVersions.V19_3),
                    map(0x17, MinecraftVersions.V19_4),
                    map(0x18, MinecraftVersions.V20_2),
                    map(0x19, MinecraftVersions.V20_5),
                    map(0x18, MinecraftVersions.V21_5));
        }
    };

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
                @NonNull Class<? extends Packet> clazz,
                @NonNull String identifier,
                @NonNull StreamCodec<? extends ByteBuf, ? extends Packet> codec,
                @NonNull PacketMapping... mappings) {
            if (mappings.length == 0) {
                throw new IllegalArgumentException("At least one mapping must be provided");
            }
            Integer id =
                    Stream.of(mappings)
                            .map(PacketMapping::resolve)
                            .flatMap(Optional::stream)
                            .findFirst()
                            .orElse(null);
            if (id == null) {
                return;
            }
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

    public static @NonNull PacketMapping map(
            final int id,
            final @NonNull MinecraftVersion since,
            final @NonNull MinecraftVersion until) {
        return new PacketMapping(id, since, until);
    }

    public static @NonNull PacketMapping map(final int id, final @NonNull MinecraftVersion since) {
        return new PacketMapping(id, since);
    }

    public static @NonNull PacketMapping map(final int id) {
        return new PacketMapping(id);
    }

    public record PacketMapping(
            int id, @NonNull MinecraftVersion since, @NonNull MinecraftVersion until) {
        public PacketMapping(int id, @NonNull MinecraftVersion since) {
            this(id, since, MinecraftVersions.UNKNOWN);
        }

        public PacketMapping(int id) {
            this(id, MinecraftVersions.UNKNOWN, MinecraftVersions.UNKNOWN);
        }

        public Optional<Integer> resolve() {
            if (Constraint.range(this.since, this.until).result()) {
                return Optional.of(this.id);
            }
            return Optional.empty();
        }
    }
}
