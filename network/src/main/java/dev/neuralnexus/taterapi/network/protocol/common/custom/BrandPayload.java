/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.common.custom;

import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readUtf;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.writeUtf;

import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.PayloadTypes;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

public record BrandPayload(@NonNull String brand) implements CustomPacketPayload {
    public static final StreamCodec<ByteBuf, BrandPayload> STREAM_CODEC =
            CustomPacketPayload.codec(BrandPayload::write, BrandPayload::new);

    private BrandPayload(final @NonNull ByteBuf input) {
        this(readUtf(input));
    }

    private void write(final @NonNull ByteBuf output) {
        writeUtf(output, this.brand);
    }

    @Override
    public @NonNull Type<BrandPayload> type() {
        return PayloadTypes.CUSTOM.BRAND;
    }
}
