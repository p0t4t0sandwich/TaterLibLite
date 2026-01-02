/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.login;

import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readNullable;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readVarInt;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.writeNullable;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.writeVarInt;

import dev.neuralnexus.taterapi.adapter.AdapterCodec;
import dev.neuralnexus.taterapi.network.NetworkAdapters;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryAnswerPayload;

import io.netty.buffer.ByteBuf;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public record ServerboundCustomQueryAnswerPacket(
        int transactionId, @Nullable CustomQueryAnswerPayload payload) implements Packet {
    public static final StreamCodec<ByteBuf, ServerboundCustomQueryAnswerPacket> STREAM_CODEC =
            Packet.codec(
                    ServerboundCustomQueryAnswerPacket::write,
                    ServerboundCustomQueryAnswerPacket::new);
    public static final AdapterCodec<?, ServerboundCustomQueryAnswerPacket> ADAPTER_CODEC =
            NetworkAdapters.registry().getTo(ServerboundCustomQueryAnswerPacket.class).orElse(null);

    public ServerboundCustomQueryAnswerPacket(final int transactionId) {
        this(transactionId, null);
    }

    private ServerboundCustomQueryAnswerPacket(final @NotNull ByteBuf buf) {
        this(readVarInt(buf), readNullable(buf, CustomQueryAnswerPayload.DEFAULT_CODEC));
    }

    @SuppressWarnings("DataFlowIssue")
    private void write(final @NotNull ByteBuf buf) {
        writeVarInt(buf, this.transactionId);
        writeNullable(
                buf,
                this.payload,
                ((StreamCodec<ByteBuf, CustomQueryAnswerPayload>) this.payload.codec()));
    }

    public static <T> @NotNull ServerboundCustomQueryAnswerPacket fromMC(final @NotNull T obj) {
        return ((AdapterCodec<T, ServerboundCustomQueryAnswerPacket>) ADAPTER_CODEC).from(obj);
    }

    public <T> @NotNull T toMC() {
        return ((AdapterCodec<T, ServerboundCustomQueryAnswerPacket>) ADAPTER_CODEC).to(this);
    }
}
