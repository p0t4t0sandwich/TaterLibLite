/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol;

import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.common.custom.CustomPacketPayload;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryAnswerPayload;
import dev.neuralnexus.taterapi.network.protocol.login.custom.CustomQueryPayload;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

import java.util.Optional;

public interface PayloadType<T, I> {
    @NonNull Class<T> clazz();

    @NonNull PacketFlow flow();

    @NonNull I id();

    @NonNull StreamCodec<ByteBuf, T> codec();

    class Base<T, I> implements PayloadType<T, I> {
        private final Class<T> clazz;
        private final PacketFlow flow;
        private final I id;
        private final StreamCodec<ByteBuf, T> codec;

        public Base(
                final @NonNull Class<T> clazz,
                final @NonNull PacketFlow flow,
                final @NonNull I id,
                final @NonNull StreamCodec<ByteBuf, T> codec) {
            this.clazz = clazz;
            this.flow = flow;
            this.id = id;
            this.codec = codec;
        }

        @Override
        public @NonNull String toString() {
            return this.id.toString();
        }

        @Override
        public @NonNull Class<T> clazz() {
            return this.clazz;
        }

        @Override
        public @NonNull PacketFlow flow() {
            return this.flow;
        }

        @Override
        public @NonNull I id() {
            return this.id;
        }

        @Override
        public @NonNull StreamCodec<ByteBuf, T> codec() {
            return this.codec;
        }
    }

    static <T extends CustomPacketPayload> CustomPacketPayload.Type.Builder<T> custom(
            final @NonNull Class<T> clazz, final @NonNull String id) {
        return new CustomPacketPayload.Type.Builder<>(clazz).id(id);
    }

    static <T extends CustomQueryPayload> CustomQueryPayload.Type.Builder<T> query(
            final @NonNull Class<T> clazz, final @NonNull String id) {
        return new CustomQueryPayload.Type.Builder<>(clazz).id(id).flow(PacketFlow.CLIENTBOUND);
    }

    static <T extends CustomQueryAnswerPayload> CustomQueryAnswerPayload.Type.Builder<T> answer(
            final @NonNull Class<T> clazz, final int id) {
        return new CustomQueryAnswerPayload.Type.Builder<>(clazz)
                .id(Optional.of(id))
                .flow(PacketFlow.SERVERBOUND);
    }

    static <T extends CustomQueryAnswerPayload> CustomQueryAnswerPayload.Type<T> answer(
            final @NonNull Class<T> clazz, final @NonNull StreamCodec<ByteBuf, T> codec) {
        return new CustomQueryAnswerPayload.Type.Builder<>(clazz)
                .id(Optional.empty())
                .flow(PacketFlow.SERVERBOUND)
                .codec(codec)
                .build();
    }

    @SuppressWarnings("unchecked")
    abstract class Builder<T, I, B extends Builder<T, I, B>> {
        protected final Class<T> clazz;
        protected PacketFlow flow;
        protected I id;
        protected StreamCodec<ByteBuf, T> codec;

        public Builder(final @NonNull Class<T> clazz) {
            this.clazz = clazz;
        }

        public B flow(final @NonNull PacketFlow flow) {
            this.flow = flow;
            return (B) this;
        }

        public B id(final @NonNull I identifier) {
            this.id = identifier;
            return (B) this;
        }

        public B id(final @NonNull String path, final @NonNull String name) {
            this.id = (I) (path + ":" + name);
            return (B) this;
        }

        public B id(
                final @NonNull String path,
                final @NonNull String name,
                final @NonNull String resource) {
            this.id = (I) (path + ":" + name + "/" + resource);
            return (B) this;
        }

        public B codec(final @NonNull StreamCodec<ByteBuf, T> codec) {
            this.codec = codec;
            return (B) this;
        }

        public abstract <P extends PayloadType<T, I>> P build();
    }
}
