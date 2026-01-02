/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.login.custom;

import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readPayload;

import dev.neuralnexus.taterapi.network.codec.StreamCodec;

import io.netty.buffer.ByteBuf;

import org.jetbrains.annotations.NotNull;

public record CustomQueryAnswerPayloadImpl(@NotNull ByteBuf data)
        implements CustomQueryAnswerPayload {
    public static final StreamCodec<@NotNull ByteBuf, @NotNull CustomQueryAnswerPayloadImpl>
            STREAM_CODEC =
                    CustomQueryAnswerPayload.codec(
                            CustomQueryAnswerPayloadImpl::write,
                            CustomQueryAnswerPayloadImpl::read);

    private static @NotNull CustomQueryAnswerPayloadImpl read(final @NotNull ByteBuf buf) {
        return new CustomQueryAnswerPayloadImpl(readPayload(buf));
    }

    private void write(final @NotNull ByteBuf buf) {
        buf.writeBytes(this.data.slice());
    }

    @Override
    public @NotNull StreamCodec<@NotNull ByteBuf, @NotNull CustomQueryAnswerPayloadImpl> codec() {
        return STREAM_CODEC;
    }
}
