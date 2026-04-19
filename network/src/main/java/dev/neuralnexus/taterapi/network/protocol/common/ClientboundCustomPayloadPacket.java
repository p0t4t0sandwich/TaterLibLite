/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.common;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.PacketType;
import dev.neuralnexus.taterapi.network.protocol.PacketTypes;
import dev.neuralnexus.taterapi.network.protocol.common.custom.CustomPacketPayload;

import org.jspecify.annotations.NonNull;

public record ClientboundCustomPayloadPacket(@NonNull CustomPacketPayload payload)
        implements Packet {
    public static final StreamCodec<FriendlyByteBuf, ClientboundCustomPayloadPacket> STREAM_CODEC =
            CustomPacketPayload.DEFAULT_CODEC.map(
                    ClientboundCustomPayloadPacket::new, ClientboundCustomPayloadPacket::payload);

    @Override
    public PacketType<ClientboundCustomPayloadPacket> type() {
        return PacketTypes.COMMON.CLIENTBOUND_CUSTOM_PAYLOAD;
    }
}
