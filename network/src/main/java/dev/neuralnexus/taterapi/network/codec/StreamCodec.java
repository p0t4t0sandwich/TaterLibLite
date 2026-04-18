/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.codec;

import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.version.Versioned;

import org.jspecify.annotations.NonNull;

import java.util.function.Function;

public interface StreamCodec<B, V> extends StreamDecoder<B, V>, StreamEncoder<B, V> {
    static <B, V> StreamCodec<B, V> of(StreamEncoder<B, V> encoder, StreamDecoder<B, V> decoder) {
        return new StreamCodec<>() {
            @Override
            public @NonNull V decode(final @NonNull B input) {
                return decoder.decode(input);
            }

            @Override
            public void encode(final @NonNull B output, final @NonNull V value) {
                encoder.encode(output, value);
            }
        };
    }

    static <B, V> StreamCodec<B, V> ofMember(
            StreamMemberEncoder<B, V> encoder, StreamDecoder<B, V> decoder) {
        return new StreamCodec<>() {
            @Override
            public @NonNull V decode(final @NonNull B input) {
                return decoder.decode(input);
            }

            @Override
            public void encode(final @NonNull B output, final @NonNull V value) {
                encoder.encode(value, output);
            }
        };
    }

    default <O> StreamCodec<B, O> map(
            final Function<? super V, ? extends O> mapDecode,
            final Function<? super O, ? extends V> mapEncode) {
        return new StreamCodec<>() {
            @Override
            public O decode(@NonNull B input) {
                return mapDecode.apply(StreamCodec.this.decode(input));
            }

            @Override
            public void encode(@NonNull B output, @NonNull O value) {
                StreamCodec.this.encode(output, mapEncode.apply(value));
            }
        };
    }

    /**
     * Creates a new versioned codec builder. If there are overlapping version ranges, the codec
     * with the highest version will be used. If no codec matches the current Minecraft version, an
     * exception will be thrown.
     *
     * @return a new versioned codec builder
     * @param <B> the buffer type
     * @param <V> the value type
     */
    static <B, V> VersionedCodecBuilder<B, V> versioned() {
        return new VersionedCodecBuilder<>(false);
    }

    static <B, V> VersionedCodecBuilder<B, V> versioned(final boolean strict) {
        return new VersionedCodecBuilder<>(strict);
    }

    class VersionedCodecBuilder<B, V>
            extends Versioned.Builder<StreamCodec<B, V>, VersionedCodecBuilder<B, V>> {
        public VersionedCodecBuilder(final boolean strict) {
            super(strict);
        }

        public VersionedCodecBuilder<B, V> add(
                final @NonNull StreamMemberEncoder<B, V> encoder,
                final @NonNull StreamDecoder<B, V> decoder,
                final @NonNull MinecraftVersion since) {
            return this.add(StreamCodec.ofMember(encoder, decoder), since);
        }

        public VersionedCodecBuilder<B, V> add(
                final @NonNull StreamMemberEncoder<B, V> encoder,
                final @NonNull StreamDecoder<B, V> decoder,
                final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion since) {
            return this.add(StreamCodec.ofMember(encoder, decoder), since);
        }

        public VersionedCodecBuilder<B, V> add(
                final @NonNull StreamMemberEncoder<B, V> encoder,
                final @NonNull StreamDecoder<B, V> decoder,
                final @NonNull MinecraftVersion since,
                final @NonNull MinecraftVersion until) {
            return this.add(StreamCodec.ofMember(encoder, decoder), since, until);
        }

        public VersionedCodecBuilder<B, V> add(
                final @NonNull StreamMemberEncoder<B, V> encoder,
                final @NonNull StreamDecoder<B, V> decoder,
                final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion since,
                final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion until) {
            return this.add(StreamCodec.ofMember(encoder, decoder), since, until);
        }

        public VersionedCodecBuilder<B, V> add(
                final @NonNull StreamEncoder<B, V> encoder,
                final @NonNull StreamDecoder<B, V> decoder,
                final @NonNull MinecraftVersion since) {
            return this.add(StreamCodec.of(encoder, decoder), since);
        }

        public VersionedCodecBuilder<B, V> add(
                final @NonNull StreamEncoder<B, V> encoder,
                final @NonNull StreamDecoder<B, V> decoder,
                final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion since) {
            return this.add(StreamCodec.of(encoder, decoder), since);
        }

        public VersionedCodecBuilder<B, V> add(
                final @NonNull StreamEncoder<B, V> encoder,
                final @NonNull StreamDecoder<B, V> decoder,
                final @NonNull MinecraftVersion since,
                final @NonNull MinecraftVersion until) {
            return this.add(StreamCodec.of(encoder, decoder), since, until);
        }

        public VersionedCodecBuilder<B, V> add(
                final @NonNull StreamEncoder<B, V> encoder,
                final @NonNull StreamDecoder<B, V> decoder,
                final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion since,
                final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion until) {
            return this.add(StreamCodec.of(encoder, decoder), since, until);
        }
    }
}
