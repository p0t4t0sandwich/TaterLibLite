/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.adapter;

import org.jspecify.annotations.NonNull;

import java.lang.reflect.ParameterizedType;

@SuppressWarnings("unchecked")
public interface AdapterCodec<A, B> extends AdapterDecoder<A, B>, AdapterEncoder<A, B> {
    default Class<A> toClass() {
        return (Class<A>)
                ((ParameterizedType) getClass().getGenericInterfaces()[0])
                        .getActualTypeArguments()[0];
    }

    default Class<B> fromClass() {
        return (Class<B>)
                ((ParameterizedType) getClass().getGenericInterfaces()[0])
                        .getActualTypeArguments()[1];
    }

    default AdapterCodec<B, A> reverse() {
        return new AdapterCodec<>() {
            @Override
            public @NonNull B to(final @NonNull A object) {
                return AdapterCodec.this.from(object);
            }

            @Override
            public @NonNull A from(final @NonNull B object) {
                return AdapterCodec.this.to(object);
            }
        };
    }
}
