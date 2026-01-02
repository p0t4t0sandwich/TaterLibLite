/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.codec;

import org.jspecify.annotations.NonNull;

public interface StreamCodec<B, V> extends StreamDecoder<B, V>, StreamEncoder<B, V> {
    static <B, V> StreamCodec<B, V> of(StreamEncoder<B, V> encoder, StreamDecoder<B, V> decoder) {
        return new StreamCodec<>() {
            @Override
            public @NonNull V decode(final @NonNull B object) {
                return decoder.decode(object);
            }

            @Override
            public void encode(final @NonNull B object, final @NonNull V value) {
                encoder.encode(object, value);
            }
        };
    }

    static <B, V> StreamCodec<B, V> ofMember(
            StreamMemberEncoder<B, V> encoder, StreamDecoder<B, V> decoder) {
        return new StreamCodec<>() {
            @Override
            public @NonNull V decode(final @NonNull B buffer) {
                return decoder.decode(buffer);
            }

            @Override
            public void encode(final @NonNull B buffer, final @NonNull V value) {
                encoder.encode(value, buffer);
            }
        };
    }
}
