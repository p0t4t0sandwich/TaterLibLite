/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.status;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.PacketType;
import dev.neuralnexus.taterapi.network.protocol.PacketTypes;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import org.jspecify.annotations.NonNull;

public record ClientboundStatusResponsePacket(@NonNull ServerStatus status) implements Packet {
    public static final StreamCodec<FriendlyByteBuf, ClientboundStatusResponsePacket> STREAM_CODEC =
            Packet.codec(
                    ClientboundStatusResponsePacket::encode,
                    ClientboundStatusResponsePacket::decode);

    private static ClientboundStatusResponsePacket decode(@NonNull FriendlyByteBuf input) {
        final String json = input.readUtf();
        final ServerStatus status =
                ServerStatus.CODEC
                        .decode(json)
                        .result()
                        .orElseThrow(
                                () ->
                                        new DecoderException(
                                                "Failed to decode ServerStatus from JSON: "
                                                        + json));
        return new ClientboundStatusResponsePacket(status);
    }

    private void encode(@NonNull FriendlyByteBuf output) {
        final String json =
                ServerStatus.CODEC
                        .encode(this.status)
                        .result()
                        .orElseThrow(
                                () ->
                                        new EncoderException(
                                                "Failed to encode ServerStatus to JSON: "
                                                        + this.status));
        output.writeUtf(json);
    }

    @Override
    public PacketType<ClientboundStatusResponsePacket> type() {
        return PacketTypes.STATUS.CLIENTBOUND_STATUS_RESPONSE;
    }
}
