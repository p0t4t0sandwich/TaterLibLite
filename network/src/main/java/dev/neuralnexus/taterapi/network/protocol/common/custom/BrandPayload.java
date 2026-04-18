/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.common.custom;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.PayloadTypes;

import org.jspecify.annotations.NonNull;

public record BrandPayload(@NonNull String brand) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, BrandPayload> STREAM_CODEC =
            CustomPacketPayload.codec(BrandPayload::encode, BrandPayload::decode);

    private void encode(final @NonNull FriendlyByteBuf output) {
        output.writeUtf(this.brand);
    }

    private static BrandPayload decode(final @NonNull FriendlyByteBuf input) {
        return new BrandPayload(input.readUtf());
    }

    @Override
    public @NonNull Type<BrandPayload> type() {
        return PayloadTypes.CUSTOM.BRAND;
    }
}
