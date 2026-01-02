/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.login.custom;

import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readPayload;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readResourceLocation;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.writeResourceLocation;

import dev.neuralnexus.taterapi.network.codec.StreamCodec;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

public record CustomQueryPayloadImpl(@NonNull String id, @NonNull ByteBuf data)
        implements CustomQueryPayload {
    public static final StreamCodec<@NonNull ByteBuf, @NonNull CustomQueryPayloadImpl>
            STREAM_CODEC =
                    CustomQueryPayload.codec(
                            CustomQueryPayloadImpl::write, CustomQueryPayloadImpl::read);

    private static @NonNull CustomQueryPayloadImpl read(final @NonNull ByteBuf buf) {
        final String id = readResourceLocation(buf);
        final ByteBuf data = readPayload(buf);
        return new CustomQueryPayloadImpl(id, data);
    }

    private void write(final @NonNull ByteBuf buf) {
        writeResourceLocation(buf, this.id());
        buf.writeBytes(this.data.slice());
    }

    @Override
    public @NonNull StreamCodec<@NonNull ByteBuf, @NonNull CustomQueryPayloadImpl> codec() {
        return STREAM_CODEC;
    }
}
