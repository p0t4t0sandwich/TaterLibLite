/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.serialization;

public interface Codec<A, B> extends Encoder<A, B>, Decoder<A, B> {
    static <A, B> Codec<A, B> of(Encoder<A, B> encoder, Decoder<A, B> decoder) {
        return new Codec<>() {
            @Override
            public Result<B> encode(final A input) {
                return encoder.encode(input);
            }

            @Override
            public Result<A> decode(final B input) {
                return decoder.decode(input);
            }
        };
    }
}
