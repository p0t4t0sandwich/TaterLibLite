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

import org.jetbrains.annotations.NotNull;

public record CustomQueryPayloadImpl(@NotNull String id, @NotNull ByteBuf data)
        implements CustomQueryPayload {
    public static final StreamCodec<@NotNull ByteBuf, @NotNull CustomQueryPayloadImpl>
            STREAM_CODEC =
                    CustomQueryPayload.codec(
                            CustomQueryPayloadImpl::write, CustomQueryPayloadImpl::read);

    private static @NotNull CustomQueryPayloadImpl read(final @NotNull ByteBuf buf) {
        final String id = readResourceLocation(buf);
        final @NotNull ByteBuf data = readPayload(buf);
        return new CustomQueryPayloadImpl(id, data);
    }

    private void write(final @NotNull ByteBuf buf) {
        writeResourceLocation(buf, this.id());
        buf.writeBytes(this.data.slice());
    }

    @Override
    public @NotNull StreamCodec<@NotNull ByteBuf, @NotNull CustomQueryPayloadImpl> codec() {
        return STREAM_CODEC;
    }
}
