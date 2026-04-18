/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.login.custom;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;
import dev.neuralnexus.taterapi.network.PayloadRegistry;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.codec.StreamDecoder;
import dev.neuralnexus.taterapi.network.codec.StreamMemberEncoder;
import dev.neuralnexus.taterapi.network.protocol.PacketFlow;
import dev.neuralnexus.taterapi.network.protocol.PayloadType;
import dev.neuralnexus.taterapi.network.protocol.login.ServerboundCustomQueryAnswerPacket;

import org.jspecify.annotations.NonNull;

import java.util.Optional;

public interface CustomQueryAnswerPayload {
    StreamCodec<@NonNull FriendlyByteBuf, @NonNull CustomQueryAnswerPayload> DEFAULT_CODEC =
            CustomQueryAnswerPayload.codec(
                    (value, output) -> output.writeBytes(((Raw) value).data().slice()),
                    // TODO: Discover why simplifying this further causes deserialization issues
                    // TODO: Test to see if it still happens after the introduction of Raw record
                    input -> {
                        final FriendlyByteBuf data = input.readPayload();
                        return new Raw(data);
                    });

    /**
     * Don't rely on the {@link Type#id()} if you are not the "owner" of the type, as it defaults to
     * {@link Optional#empty()} when not defined manually. Use the encapsulating {@link
     * ServerboundCustomQueryAnswerPacket#transactionId()} instead as a source of truth.
     *
     * @return the type information for this payload
     */
    @NonNull Type<? extends CustomQueryAnswerPayload> type();

    static <B extends @NonNull FriendlyByteBuf, T extends @NonNull CustomQueryAnswerPayload>
            StreamCodec<B, T> codec(
                    final @NonNull StreamMemberEncoder<B, T> encoder,
                    final @NonNull StreamDecoder<B, T> decoder) {
        return StreamCodec.ofMember(encoder, decoder);
    }

    static <B extends FriendlyByteBuf>
            StreamCodec<? super B, ? extends CustomQueryAnswerPayload> codec(
                    final int transactionId) {
        final Optional<StreamCodec<? super FriendlyByteBuf, ? extends CustomQueryAnswerPayload>>
                codec =
                        PayloadRegistry.answer(transactionId)
                                .map(CustomQueryAnswerPayload.Type::codec);
        return codec.orElse(DEFAULT_CODEC);
    }

    interface Type<T extends CustomQueryAnswerPayload> extends PayloadType<T, Optional<Integer>> {
        class Definition<T extends CustomQueryAnswerPayload> extends Base<T, Optional<Integer>>
                implements Type<T> {
            public Definition(
                    final @NonNull Class<T> clazz,
                    final @NonNull PacketFlow flow,
                    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
                            final @NonNull Optional<Integer> id,
                    final @NonNull StreamCodec<FriendlyByteBuf, T> codec) {
                super(clazz, flow, id, codec);
            }
        }

        class Builder<T extends CustomQueryAnswerPayload>
                extends PayloadType.Builder<
                        T, Optional<Integer>, CustomQueryAnswerPayload.Type.Builder<T>> {
            public Builder(final @NonNull Class<T> clazz) {
                super(clazz);
            }

            @SuppressWarnings("unchecked")
            @Override
            public CustomQueryAnswerPayload.Type<T> build() {
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

    record Raw(@NonNull FriendlyByteBuf data) implements CustomQueryAnswerPayload {
        @Override
        public @NonNull Type<CustomQueryAnswerPayload> type() {
            return PayloadType.answer(CustomQueryAnswerPayload.class, DEFAULT_CODEC);
        }
    }
}
