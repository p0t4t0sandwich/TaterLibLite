/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.serialization.codecs;

import dev.neuralnexus.taterapi.serialization.Codec;
import dev.neuralnexus.taterapi.serialization.Result;

import java.lang.reflect.ParameterizedType;

public interface ReversibleCodec<A, B> extends Codec<A, B> {
    @SuppressWarnings("unchecked")
    default Class<A> inputType() {
        return (Class<A>)
                ((ParameterizedType) getClass().getGenericInterfaces()[0])
                        .getActualTypeArguments()[0];
    }

    @SuppressWarnings("unchecked")
    default Class<B> outputType() {
        return (Class<B>)
                ((ParameterizedType) getClass().getGenericInterfaces()[0])
                        .getActualTypeArguments()[1];
    }

    default ReversibleCodec<B, A> reverse() {
        return new ReversibleCodec<>() {
            @Override
            public Result<A> encode(final B input) {
                return ReversibleCodec.this.decode(input);
            }

            @Override
            public Result<B> decode(final A input) {
                return ReversibleCodec.this.encode(input);
            }
        };
    }
}
