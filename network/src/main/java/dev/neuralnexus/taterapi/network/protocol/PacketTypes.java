/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol;

import dev.neuralnexus.taterapi.network.protocol.common.ClientboundCustomPayloadPacket;
import dev.neuralnexus.taterapi.network.protocol.common.ServerboundCustomPayloadPacket;
import dev.neuralnexus.taterapi.network.protocol.handshake.ClientIntentionPacket;
import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.ServerboundCustomQueryAnswerPacket;
import dev.neuralnexus.taterapi.network.protocol.login.ServerboundHelloPacket;
import dev.neuralnexus.taterapi.network.protocol.ping.ClientboundPongResponsePacket;
import dev.neuralnexus.taterapi.network.protocol.ping.ServerboundPingRequestPacket;
import dev.neuralnexus.taterapi.network.protocol.status.ClientboundStatusResponsePacket;
import dev.neuralnexus.taterapi.network.protocol.status.ServerboundStatusRequestPacket;
import dev.neuralnexus.taterapi.registries.AdapterRegistry;

import org.jspecify.annotations.NonNull;

public interface PacketTypes {
    // spotless:off
    interface HANDSHAKING {
        PacketType<ClientIntentionPacket> CLIENT_INTENTION =
                serverbound(ClientIntentionPacket.class, "minecraft:intention")
                        .codec(ClientIntentionPacket.STREAM_CODEC).build();
    }
    interface STATUS {
        PacketType<ClientboundPongResponsePacket> CLIENTBOUND_PONG_RESPONSE =
                clientbound(ClientboundPongResponsePacket.class, "minecraft:pong_response")
                        .codec(ClientboundPongResponsePacket.STREAM_CODEC).build();
        PacketType<ClientboundStatusResponsePacket> CLIENTBOUND_STATUS_RESPONSE =
                clientbound(ClientboundStatusResponsePacket.class, "minecraft:status_response")
                    .codec(ClientboundStatusResponsePacket.STREAM_CODEC).build();

        PacketType<ServerboundPingRequestPacket> SERVERBOUND_PING_REQUEST =
                serverbound(ServerboundPingRequestPacket.class, "minecraft:ping_request")
                        .codec(ServerboundPingRequestPacket.STREAM_CODEC).build();
        PacketType<ServerboundStatusRequestPacket> SERVERBOUND_STATUS_REQUEST =
                serverbound(ServerboundStatusRequestPacket.class, "minecraft:status_request")
                        .codec(ServerboundStatusRequestPacket.STREAM_CODEC).build();
    }
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
