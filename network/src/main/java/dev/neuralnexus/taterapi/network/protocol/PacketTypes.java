/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol;

import dev.neuralnexus.taterapi.network.protocol.common.ClientboundCustomPayloadPacket;
import dev.neuralnexus.taterapi.network.protocol.common.ServerboundCustomPayloadPacket;
import dev.neuralnexus.taterapi.network.protocol.login.ClientboundCustomQueryPacket;
import dev.neuralnexus.taterapi.network.protocol.login.ServerboundCustomQueryAnswerPacket;

import org.jspecify.annotations.NonNull;

public interface PacketTypes {
    final class LOGIN {
        public static final PacketType<ClientboundCustomQueryPacket> CLIENTBOUND_CUSTOM_QUERY =
                createClientbound(ClientboundCustomQueryPacket.class, "custom_query")
                        .codec(ClientboundCustomQueryPacket.STREAM_CODEC)
                        .build();
        public static final PacketType<ServerboundCustomQueryAnswerPacket>
                SERVERBOUND_CUSTOM_QUERY_ANSWER =
                        createServerbound(
                                        ServerboundCustomQueryAnswerPacket.class,
                                        "custom_query_answer")
                                .codec(ServerboundCustomQueryAnswerPacket.STREAM_CODEC)
                                .build();
    }

    class COMMON {
        public static final PacketType<ClientboundCustomPayloadPacket> CLIENTBOUND_CUSTOM_PAYLOAD =
                createClientbound(ClientboundCustomPayloadPacket.class, "custom_payload")
                        .codec(ClientboundCustomPayloadPacket.STREAM_CODEC)
                        .build();

        public static final PacketType<ServerboundCustomPayloadPacket> SERVERBOUND_CUSTOM_PAYLOAD =
                createServerbound(ServerboundCustomPayloadPacket.class, "custom_payload")
                        .codec(ServerboundCustomPayloadPacket.STREAM_CODEC)
                        .build();
    }

    static <T extends Packet> PacketType.Builder<T> createClientbound(
            final @NonNull Class<T> clazz, final @NonNull String id) {
        return PacketType.builder(clazz).flow(PacketFlow.CLIENTBOUND).identifier(id);
    }

    static <T extends Packet> PacketType.Builder<T> createServerbound(
            final @NonNull Class<T> clazz, final @NonNull String id) {
        return PacketType.builder(clazz).flow(PacketFlow.SERVERBOUND).identifier(id);
    }
}
