/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.login.custom;

import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readPayload;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readResourceLocation;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.writeResourceLocation;

import dev.neuralnexus.taterapi.network.NetworkRegistry;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.codec.StreamDecoder;
import dev.neuralnexus.taterapi.network.codec.StreamMemberEncoder;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

public interface CustomQueryPayload {
    StreamCodec<@NonNull ByteBuf, @NonNull CustomQueryPayload> DEFAULT_CODEC =
            CustomQueryPayload.codec(
                    (final String identifier) ->
                            new StreamCodec<>() {
                                @Override
                                public CustomQueryPayload decode(final @NonNull ByteBuf buffer) {
                                    final ByteBuf data = readPayload(buffer);
                                    return new CustomQueryPayload() {
                                        @Override
                                        public @NonNull String id() {
                                            return identifier;
                                        }

                                        @Override
                                        public @NonNull ByteBuf data() {
                                            return data;
                                        }
                                    };
                                }

                                @Override
                                public void encode(
                                        final @NonNull ByteBuf buffer,
                                        final @NonNull CustomQueryPayload value) {
                                    buffer.writeBytes(value.data().slice());
                                }
                            });

    @NonNull String id();

    @NonNull ByteBuf data();

    static <B extends @NonNull ByteBuf, T extends @NonNull CustomQueryPayload>
            StreamCodec<B, T> codec(
                    final @NonNull StreamMemberEncoder<B, T> encoder,
                    final @NonNull StreamDecoder<B, T> decoder) {
        return StreamCodec.ofMember(encoder, decoder);
    }

    static <B extends ByteBuf> StreamCodec<B, CustomQueryPayload> codec(
            final CustomQueryPayload.FallbackProvider<B> fallbackprovider) {
        return new StreamCodec<>() {
            private StreamCodec<? super B, ? extends CustomQueryPayload> findCodec(
                    final @NonNull String identifier) {
                return NetworkRegistry.getQueryPayloadCodec(identifier)
                        .orElse(fallbackprovider.create(identifier));
            }

            @SuppressWarnings("unchecked")
            private <T extends CustomQueryPayload> void writeCap(
                    final @NonNull B buffer,
                    final @NonNull String id,
                    final @NonNull CustomQueryPayload payload) {
                writeResourceLocation(buffer, id);
                final StreamCodec<B, T> codec = (StreamCodec<B, T>) this.findCodec(id);
                codec.encode(buffer, (T) payload);
            }

            public void encode(@NonNull B buffer, @NonNull CustomQueryPayload payload) {
                this.writeCap(buffer, payload.id(), payload);
            }

            public CustomQueryPayload decode(@NonNull B buffer) {
                final String id = readResourceLocation(buffer);
                return this.findCodec(id).decode(buffer);
            }
        };
    }

    @FunctionalInterface
    interface FallbackProvider<B extends ByteBuf> {
        StreamCodec<? super ByteBuf, ? extends CustomQueryPayload> create(final String identifier);
    }
}
