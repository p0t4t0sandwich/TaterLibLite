/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.login;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.PacketType;
import dev.neuralnexus.taterapi.network.protocol.PacketTypes;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryAnswerPayload;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public record ServerboundCustomQueryAnswerPacket(
        int transactionId, @Nullable CustomQueryAnswerPayload payload) implements Packet {
    public static final StreamCodec<FriendlyByteBuf, ServerboundCustomQueryAnswerPacket>
            STREAM_CODEC =
                    Packet.codec(
                            ServerboundCustomQueryAnswerPacket::write,
                            ServerboundCustomQueryAnswerPacket::read);

    public ServerboundCustomQueryAnswerPacket(final int transactionId) {
        this(transactionId, null);
    }

    private static ServerboundCustomQueryAnswerPacket read(final @NonNull FriendlyByteBuf input) {
        final int transactionId = input.readVarInt();
        final CustomQueryAnswerPayload payload =
                input.readNullable(CustomQueryAnswerPayload.codec(transactionId));
        return new ServerboundCustomQueryAnswerPacket(transactionId, payload);
    }

    private void write(final @NonNull FriendlyByteBuf output) {
        output.writeVarInt(this.transactionId);
        output.writeNullable(this.payload, CustomQueryAnswerPayload.codec(this.transactionId));
    }

    @Override
    public PacketType<ServerboundCustomQueryAnswerPacket> type() {
        return PacketTypes.LOGIN.SERVERBOUND_CUSTOM_QUERY_ANSWER;
    }
}
