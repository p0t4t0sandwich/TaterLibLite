/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.network.codec.StreamCodec;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.PacketFlow;
import dev.neuralnexus.taterapi.network.protocol.PacketType;

import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public final class PacketRegistry {
    public final @NonNull PacketFlow direction;
    public final @NonNull Protocol protocol;
    // TODO: Find a way to replace with IntObjectHashMap and similar,
    //  as the Netty version 1.7 (and probably 1.12) uses doesn't have such classes
    final Map<Integer, PacketType<Packet>> idToProtocolInfo = new HashMap<>(16, 0.5f);
    final Map<Class<Packet>, Integer> classToId = new HashMap<>(16, 0.5f);

    public PacketRegistry(final @NonNull PacketFlow direction, final @NonNull Protocol protocol) {
        this.direction = direction;
        this.protocol = protocol;
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> void register(
            @NonNull PacketType<T> protocolInfo, @NonNull Mapping... mappings) {
        if (mappings.length == 0) {
            throw new IllegalArgumentException("At least one mapping must be provided");
        }
        final int id =
                Stream.of(mappings)
                        .map(Mapping::resolve)
                        .flatMap(Optional::stream)
                        .findFirst()
                        .orElse(-1);
        if (id == -1) {
            return;
        }
        this.idToProtocolInfo.put(id, (PacketType<Packet>) protocolInfo);
        this.classToId.put((Class<Packet>) protocolInfo.clazz(), id);
    }

    public PacketType<Packet> info(final int id) {
        return this.idToProtocolInfo.get(id);
    }

    public int id(final @NonNull Class<? extends Packet> clazz) {
        return this.classToId.get(clazz);
    }

    public Optional<StreamCodec<FriendlyByteBuf, Packet>> codec(final int id) {
        PacketType<Packet> protocolInfo = this.idToProtocolInfo.get(id);
        if (protocolInfo == null) {
            return Optional.empty();
        }
        return Optional.of(protocolInfo.codec());
    }

    public record Mapping(
            int id, @NonNull MinecraftVersion since, @NonNull MinecraftVersion until) {
        public Mapping(final int id, final @NonNull MinecraftVersion since) {
            this(id, since, MinecraftVersions.UNKNOWN);
        }

        public Mapping(final int id) {
            this(id, MinecraftVersions.UNKNOWN, MinecraftVersions.UNKNOWN);
        }

        public Optional<Integer> resolve() {
            if (Constraint.range(this.since, this.until).result()) {
                return Optional.of(this.id);
            }
            return Optional.empty();
        }
    }
}
