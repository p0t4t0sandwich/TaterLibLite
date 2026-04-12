/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.common.custom;

import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readPayload;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readResourceLocation;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.writeResourceLocation;

import dev.neuralnexus.taterapi.network.NetworkRegistry;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.codec.StreamDecoder;
import dev.neuralnexus.taterapi.network.codec.StreamMemberEncoder;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

public interface CustomPacketPayload {
    StreamCodec<@NonNull ByteBuf, @NonNull CustomPacketPayload> DEFAULT_CODEC =
            CustomPacketPayload.codec(CustomPacketPayload::codec);

    @NonNull String id();

    @NonNull ByteBuf data();

    static <B extends @NonNull ByteBuf, T extends @NonNull CustomPacketPayload>
            StreamCodec<B, T> codec(
                    final @NonNull StreamMemberEncoder<B, T> encoder,
                    final @NonNull StreamDecoder<B, T> decoder) {
        return StreamCodec.ofMember(encoder, decoder);
    }

    static <B extends ByteBuf> StreamCodec<B, CustomPacketPayload> codec(
            final @NonNull String identifier) {
        return new StreamCodec<>() {
            @Override
            public CustomPacketPayload decode(final @NonNull B buffer) {
                final ByteBuf data = readPayload(buffer);
                return new CustomPacketPayload() {
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
                    final @NonNull ByteBuf buffer, final @NonNull CustomPacketPayload value) {
                buffer.writeBytes(value.data().slice());
            }
        };
    }

    static <B extends ByteBuf> StreamCodec<B, CustomPacketPayload> codec(
            final CustomPacketPayload.FallbackProvider<B> fallbackprovider) {
        return new StreamCodec<>() {
            private StreamCodec<? super B, ? extends CustomPacketPayload> findCodec(
                    final @NonNull String identifier) {
                return NetworkRegistry.getCustomPayloadCodec(identifier)
                        .orElse(fallbackprovider.create(identifier));
            }

            @SuppressWarnings("unchecked")
            private <T extends CustomPacketPayload> void writeCap(
                    final @NonNull B buffer,
                    final @NonNull String id,
                    final @NonNull CustomPacketPayload payload) {
                writeResourceLocation(buffer, id);
                final StreamCodec<B, T> codec = (StreamCodec<B, T>) this.findCodec(id);
                codec.encode(buffer, (T) payload);
            }

            public void encode(
                    final @NonNull B buffer, final @NonNull CustomPacketPayload payload) {
                this.writeCap(buffer, payload.id(), payload);
            }

            public CustomPacketPayload decode(final @NonNull B buffer) {
                final String id = readResourceLocation(buffer);
                return this.findCodec(id).decode(buffer);
            }
        };
    }

    @SuppressWarnings("unused")
    @FunctionalInterface
    interface FallbackProvider<B extends ByteBuf> {
        StreamCodec<? super ByteBuf, ? extends CustomPacketPayload> create(
                final @NonNull String identifier);
    }
}
