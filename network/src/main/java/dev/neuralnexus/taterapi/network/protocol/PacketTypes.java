/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol;

import dev.neuralnexus.taterapi.network.protocol.common.ClientboundCustomPayloadPacket;
import dev.neuralnexus.taterapi.network.protocol.common.ServerboundCustomPayloadPacket;
import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import dev.neuralnexus.taterapi.network.protocol.login.ServerboundHelloPacket;
import dev.neuralnexus.taterapi.registries.AdapterRegistry;

import org.jspecify.annotations.NonNull;

public interface PacketTypes {
    // spotless:off
    interface HANDSHAKING {}
    interface STATUS {}
    interface LOGIN {
        PacketType<ClientboundCustomQueryPacket> CLIENTBOUND_CUSTOM_QUERY =
                clientbound(ClientboundCustomQueryPacket.class, "minecraft:custom_query")
                        .codec(ClientboundCustomQueryPacket.STREAM_CODEC).build();

        PacketType<ServerboundCustomQueryAnswerPacket> SERVERBOUND_CUSTOM_QUERY_ANSWER =
                serverbound(ServerboundCustomQueryAnswerPacket.class, "minecraft:custom_query_answer")
                        .codec(ServerboundCustomQueryAnswerPacket.STREAM_CODEC).build();
        PacketType<ServerboundHelloPacket> SERVERBOUND_HELLO =
                serverbound(ServerboundHelloPacket.class, "minecraft:hello")
                        .codec(ServerboundHelloPacket.STREAM_CODEC).build();
    }
    interface CONFIGURATION {}
    interface PLAY {}
    interface COMMON {
        PacketType<ClientboundCustomPayloadPacket> CLIENTBOUND_CUSTOM_PAYLOAD =
                clientbound(ClientboundCustomPayloadPacket.class, "minecraft:custom_payload")
                        .codec(ClientboundCustomPayloadPacket.STREAM_CODEC).build();

        PacketType<ServerboundCustomPayloadPacket> SERVERBOUND_CUSTOM_PAYLOAD =
                serverbound(ServerboundCustomPayloadPacket.class, "minecraft:custom_payload")
                        .codec(ServerboundCustomPayloadPacket.STREAM_CODEC).build();
    }
    // spotless:on

    AdapterRegistry ADAPTERS = new AdapterRegistry();

    static <T extends Packet> PacketType.Builder<T> clientbound(
            final @NonNull Class<T> clazz, final @NonNull String id) {
        return PacketType.builder(clazz).flow(PacketFlow.CLIENTBOUND).identifier(id);
    }

    static <T extends Packet> PacketType.Builder<T> serverbound(
            final @NonNull Class<T> clazz, final @NonNull String id) {
        return PacketType.builder(clazz).flow(PacketFlow.SERVERBOUND).identifier(id);
    }
}
