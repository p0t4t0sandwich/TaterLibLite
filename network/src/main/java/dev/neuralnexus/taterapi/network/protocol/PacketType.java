/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol;

import dev.neuralnexus.taterapi.network.codec.StreamCodec;

import io.netty.buffer.ByteBuf;

import org.jspecify.annotations.NonNull;

public interface PacketType<T extends Packet> {
    @NonNull Class<T> clazz();

    @NonNull PacketFlow flow();

    @NonNull String identifier();

    @NonNull StreamCodec<ByteBuf, Packet> codec();

    record Definition<T extends Packet>(
            @NonNull Class<T> clazz,
            @NonNull PacketFlow flow,
            @NonNull String identifier, // TODO: Change to Identifier abstraction
            @NonNull StreamCodec<ByteBuf, Packet> codec)
            implements PacketType<T> {

        @Override
        public @NonNull String toString() {
            return this.flow.name()
                    + "/"
                    + this.identifier
                    + " ("
                    + this.clazz.getSimpleName()
                    + ")";
        }
    }

    static <T extends Packet> Builder<T> builder(final Class<T> clazz) {
        return new Builder<>(clazz);
    }

    final class Builder<T extends Packet> {
        private final Class<T> clazz;
        private PacketFlow flow;
        private String identifier;
        private StreamCodec<ByteBuf, Packet> codec;

        public Builder(final @NonNull Class<T> clazz) {
            this.clazz = clazz;
        }

        public Builder<T> flow(final @NonNull PacketFlow flow) {
            this.flow = flow;
            return this;
        }

        public Builder<T> identifier(final @NonNull String identifier) {
            if (!identifier.contains(":")) {
                this.identifier = "minecraft:" + identifier;
            } else {
                this.identifier = identifier;
            }
            return this;
        }

        public Builder<T> identifier(final @NonNull String path, final @NonNull String name) {
            this.identifier = path + ":" + name;
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder<T> codec(final @NonNull StreamCodec<ByteBuf, T> codec) {
            this.codec = (StreamCodec<ByteBuf, Packet>) codec;
            return this;
        }

        public PacketType.Definition<T> build() {
            if (this.flow == null || this.identifier == null || this.codec == null) {
                throw new IllegalStateException("All fields must be set");
            }
            return new PacketType.Definition<>(this.clazz, this.flow, this.identifier, this.codec);
        }
    }
}
