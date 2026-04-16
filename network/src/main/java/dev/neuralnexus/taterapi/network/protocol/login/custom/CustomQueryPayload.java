/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.login.custom;

import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readPayload;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.readUtf;
import static dev.neuralnexus.taterapi.network.FriendlyByteBuf.writeUtf;

import dev.neuralnexus.taterapi.network.PayloadRegistry;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.codec.StreamDecoder;
import dev.neuralnexus.taterapi.network.codec.StreamMemberEncoder;
import dev.neuralnexus.taterapi.network.protocol.PacketFlow;
import dev.neuralnexus.taterapi.network.protocol.PayloadType;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

import java.util.Optional;

public interface CustomQueryPayload {
    StreamCodec<@NonNull ByteBuf, @NonNull CustomQueryPayload> DEFAULT_CODEC =
            CustomQueryPayload.codec(CustomQueryPayload::codec);

    @NonNull Type<? extends CustomQueryPayload> type();

    static <B extends @NonNull ByteBuf, T extends @NonNull CustomQueryPayload>
            StreamCodec<B, T> codec(
                    final @NonNull StreamMemberEncoder<B, T> encoder,
                    final @NonNull StreamDecoder<B, T> decoder) {
        return StreamCodec.ofMember(encoder, decoder);
    }

    static <B extends ByteBuf> StreamCodec<B, CustomQueryPayload> codec(
            final @NonNull String identifier) {
        return new StreamCodec<>() {
            @Override
            public CustomQueryPayload decode(final @NonNull B buffer) {
                return new Raw(identifier, readPayload(buffer));
            }

            @Override
            public void encode(
                    final @NonNull ByteBuf buffer, final @NonNull CustomQueryPayload value) {
                buffer.writeBytes(((Raw) value).data().slice());
            }
        };
    }

    static <B extends ByteBuf> StreamCodec<B, CustomQueryPayload> codec(
            final CustomQueryPayload.FallbackProvider<B> fallbackprovider) {
        return new StreamCodec<>() {
            private StreamCodec<? super B, ? extends CustomQueryPayload> findCodec(
                    final @NonNull String identifier) {
                Optional<StreamCodec<? super ByteBuf, ? extends CustomQueryPayload>> codec =
                        PayloadRegistry.query(identifier).map(CustomQueryPayload.Type::codec);
                return codec.orElse(fallbackprovider.create(identifier));
            }

            @SuppressWarnings("unchecked")
            private <T extends CustomQueryPayload> void writeCap(
                    final @NonNull B buffer,
                    final @NonNull String id,
                    final @NonNull CustomQueryPayload payload) {
                writeUtf(buffer, id);
                final StreamCodec<B, T> codec = (StreamCodec<B, T>) this.findCodec(id);
                codec.encode(buffer, (T) payload);
            }

            public void encode(final @NonNull B buffer, final @NonNull CustomQueryPayload payload) {
                this.writeCap(buffer, payload.type().id(), payload);
            }

            public CustomQueryPayload decode(final @NonNull B buffer) {
                final String id = readUtf(buffer);
                return this.findCodec(id).decode(buffer);
            }
        };
    }

    interface Type<T extends CustomQueryPayload> extends PayloadType<T, String> {
        class Definition<T extends CustomQueryPayload> extends Base<T, String> implements Type<T> {
            public Definition(
                    final @NonNull Class<T> clazz,
                    final @NonNull PacketFlow flow,
                    final @NonNull String id,
                    final @NonNull StreamCodec<ByteBuf, T> codec) {
                super(clazz, flow, id, codec);
            }
        }

        class Builder<T extends CustomQueryPayload>
                extends PayloadType.Builder<T, String, Builder<T>> {
            public Builder(final @NonNull Class<T> clazz) {
                super(clazz);
            }

            @SuppressWarnings("unchecked")
            @Override
            public CustomQueryPayload.Type<T> build() {
                if (super.id == null) {
                    throw new IllegalStateException("Identifier must be set");
                }
                if (super.flow == null) {
                    throw new IllegalStateException("Packet flow must be set");
                }
                if (super.codec == null) {
                    throw new IllegalStateException("Codec must be set");
                }
                return new Definition<>(super.clazz, super.flow, super.id, super.codec);
            }
        }
    }

    @SuppressWarnings("unused")
    @FunctionalInterface
    interface FallbackProvider<B extends ByteBuf> {
        StreamCodec<? super ByteBuf, ? extends CustomQueryPayload> create(
                final @NonNull String identifier);
    }

    record Raw(@NonNull String id, @NonNull ByteBuf data) implements CustomQueryPayload {
        @Override
        public @NonNull Type<? extends CustomQueryPayload> type() {
            return PayloadType.query(CustomQueryPayload.class, this.id())
                    .codec(DEFAULT_CODEC)
                    .build();
        }
    }
}
