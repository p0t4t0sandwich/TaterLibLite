/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;

import org.jspecify.annotations.NonNull;

public interface PacketType<T extends Packet> {
    @NonNull Class<T> clazz();

    @NonNull PacketFlow flow();

    @NonNull String id();

    @NonNull StreamCodec<FriendlyByteBuf, Packet> codec();

    record Definition<T extends Packet>(
            @NonNull Class<T> clazz,
            @NonNull PacketFlow flow,
            @NonNull String id, // TODO: Change to Identifier abstraction
            @NonNull StreamCodec<FriendlyByteBuf, Packet> codec)
            implements PacketType<T> {

        @Override
        public @NonNull String toString() {
            return this.flow.name() + "/" + this.id + " (" + this.clazz.getSimpleName() + ")";
        }
    }

    static <T extends Packet> Builder<T> builder(final Class<T> clazz) {
        return new Builder<>(clazz);
    }

    final class Builder<T extends Packet> {
        private final Class<T> clazz;
        private PacketFlow flow;
        private String id;
        private StreamCodec<FriendlyByteBuf, Packet> codec;

        public Builder(final @NonNull Class<T> clazz) {
            this.clazz = clazz;
        }

        public Builder<T> flow(final @NonNull PacketFlow flow) {
            this.flow = flow;
            return this;
        }

        public Builder<T> identifier(final @NonNull String identifier) {
            this.id = identifier;
            return this;
        }

        public Builder<T> identifier(final @NonNull String path, final @NonNull String name) {
            this.id = path + ":" + name;
            return this;
        }

        public Builder<T> identifier(
                final @NonNull String path,
                final @NonNull String name,
                final @NonNull String resource) {
            this.id = path + ":" + name + "/" + resource;
            return this;
        }

        @SuppressWarnings("unchecked")
        public Builder<T> codec(final @NonNull StreamCodec<FriendlyByteBuf, T> codec) {
            this.codec = (StreamCodec<FriendlyByteBuf, Packet>) codec;
            return this;
        }

        public PacketType.Definition<T> build() {
            if (this.flow == null || this.id == null || this.codec == null) {
                throw new IllegalStateException("All fields must be set");
            }
            return new PacketType.Definition<>(this.clazz, this.flow, this.id, this.codec);
        }
    }
}
