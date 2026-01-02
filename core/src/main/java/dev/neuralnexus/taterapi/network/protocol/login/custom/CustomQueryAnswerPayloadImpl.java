/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.login.custom;

import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readPayload;

import dev.neuralnexus.taterapi.network.codec.StreamCodec;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

public record CustomQueryAnswerPayloadImpl(@NonNull ByteBuf data)
        implements CustomQueryAnswerPayload {
    public static final StreamCodec<@NonNull ByteBuf, @NonNull CustomQueryAnswerPayloadImpl>
            STREAM_CODEC =
                    CustomQueryAnswerPayload.codec(
                            CustomQueryAnswerPayloadImpl::write,
                            CustomQueryAnswerPayloadImpl::read);

    private static @NonNull CustomQueryAnswerPayloadImpl read(final @NonNull ByteBuf buf) {
        return new CustomQueryAnswerPayloadImpl(readPayload(buf));
    }

    private void write(final @NonNull ByteBuf buf) {
        buf.writeBytes(this.data.slice());
    }

    @Override
    public @NonNull StreamCodec<@NonNull ByteBuf, @NonNull CustomQueryAnswerPayloadImpl> codec() {
        return STREAM_CODEC;
    }
}
