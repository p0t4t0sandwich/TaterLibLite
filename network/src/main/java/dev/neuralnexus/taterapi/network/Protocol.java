/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network;

import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.network.protocol.Packet;
import dev.neuralnexus.taterapi.network.protocol.PacketFlow;
import dev.neuralnexus.taterapi.network.protocol.PacketType;
import dev.neuralnexus.taterapi.network.protocol.PacketTypes;
import dev.neuralnexus.taterapi.network.protocol.PayloadTypes;

import org.jspecify.annotations.NonNull;

public enum Protocol {
    HANDSHAKING("handshake"),
    STATUS("status"),
    LOGIN("login") {
        {
            // TODO: A soft lower bound of 1.13 that's overridable in some way
            serverbound.register(PacketTypes.LOGIN.SERVERBOUND_CUSTOM_QUERY_ANSWER, map(0x02));
            clientbound.register(PacketTypes.LOGIN.CLIENTBOUND_CUSTOM_QUERY, map(0x04));
        }
    },
    CONFIGURATION("configuration") {
        {
            serverbound.register(
                    PacketTypes.COMMON.SERVERBOUND_CUSTOM_PAYLOAD,
                    map(0x01, MinecraftVersions.V20_2),
                    map(0x02, MinecraftVersions.V20_5));
            clientbound.register(
                    PacketTypes.COMMON.CLIENTBOUND_CUSTOM_PAYLOAD,
                    map(0x00, MinecraftVersions.V20_2),
                    map(0x01, MinecraftVersions.V20_5));
        }
    },
    PLAY("play") {
        {
            serverbound.register(
                    PacketTypes.COMMON.SERVERBOUND_CUSTOM_PAYLOAD,
                    map(0x17, MinecraftVersions.V7_2), // TODO: Backport to older
                    map(0x09, MinecraftVersions.V9),
                    map(0x0A, MinecraftVersions.V12),
                    map(0x09, MinecraftVersions.V12_1),
                    map(0x0A, MinecraftVersions.V13),
                    map(0x0B, MinecraftVersions.V14),
                    map(0x0A, MinecraftVersions.V17),
                    map(0x0C, MinecraftVersions.V19),
                    map(0x0D, MinecraftVersions.V19_1),
                    map(0x0C, MinecraftVersions.V19_3),
                    map(0x0D, MinecraftVersions.V19_4),
                    map(0x0F, MinecraftVersions.V20_2),
                    map(0x10, MinecraftVersions.V20_3),
                    map(0x12, MinecraftVersions.V20_5),
                    map(0x14, MinecraftVersions.V21_2),
                    map(0x15, MinecraftVersions.V21_6));
            clientbound.register(
                    PacketTypes.COMMON.CLIENTBOUND_CUSTOM_PAYLOAD,
                    map(0x3F, MinecraftVersions.V7_2), // TODO: Backport to older
                    map(0x18, MinecraftVersions.V9),
                    map(0x19, MinecraftVersions.V13),
                    map(0x18, MinecraftVersions.V14),
                    map(0x19, MinecraftVersions.V15),
                    map(0x18, MinecraftVersions.V16),
                    map(0x17, MinecraftVersions.V16_2),
                    map(0x18, MinecraftVersions.V17),
                    map(0x15, MinecraftVersions.V19),
                    map(0x16, MinecraftVersions.V19_1),
                    map(0x15, MinecraftVersions.V19_3),
                    map(0x17, MinecraftVersions.V19_4),
                    map(0x18, MinecraftVersions.V20_2),
                    map(0x19, MinecraftVersions.V20_5),
                    map(0x18, MinecraftVersions.V21_5));
        }
    };

    static {
        PayloadRegistry.register(PayloadTypes.CUSTOM.BRAND, map(MinecraftVersions.V13));
    }

    private final @NonNull String id;

    Protocol(final @NonNull String id) {
        this.id = id;
    }

    public @NonNull String id() {
        return this.id;
    }

    public static Protocol fromId(final @NonNull String id) {
        return switch (id) {
            case "handshake" -> HANDSHAKING;
            case "status" -> STATUS;
            case "login" -> LOGIN;
            case "configuration" -> CONFIGURATION;
            case "play" -> PLAY;
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
    }

    public static Protocol fromLegacyId(final int id) {
        return switch (id) {
            case -1 -> HANDSHAKING;
            case 0 -> PLAY;
            case 1 -> STATUS;
            case 2 -> LOGIN;
            default -> throw new IllegalStateException("Unexpected value: " + id);
        };
    }

    protected final PacketRegistry clientbound = new PacketRegistry(PacketFlow.CLIENTBOUND, this);
    protected final PacketRegistry serverbound = new PacketRegistry(PacketFlow.SERVERBOUND, this);

    public @NonNull PacketRegistry getProtocolRegistry(final PacketFlow direction) {
        return direction == PacketFlow.CLIENTBOUND ? this.clientbound : this.serverbound;
    }

    public @NonNull PacketType<Packet> info(final @NonNull PacketFlow direction, final int id) {
        return this.getProtocolRegistry(direction).info(id);
    }

    public int id(
            final @NonNull PacketFlow direction, final @NonNull Class<? extends Packet> clazz) {
        return this.getProtocolRegistry(direction).id(clazz);
    }

    public static PacketRegistry.@NonNull Mapping map(
            final int id,
            final @NonNull MinecraftVersion since,
            final @NonNull MinecraftVersion until) {
        return new PacketRegistry.Mapping(id, since, until);
    }

    public static PacketRegistry.@NonNull Mapping map(
            final int id, final @NonNull MinecraftVersion since) {
        return new PacketRegistry.Mapping(id, since);
    }

    public static PacketRegistry.@NonNull Mapping map(final int id) {
        return new PacketRegistry.Mapping(id);
    }

    public static PayloadRegistry.@NonNull Mapping map(
            final @NonNull String identifier,
            final @NonNull MinecraftVersion since,
            final @NonNull MinecraftVersion until) {
        return new PayloadRegistry.Mapping(identifier, since, until);
    }

    public static PayloadRegistry.@NonNull Mapping map(
            final @NonNull MinecraftVersion since, final @NonNull MinecraftVersion until) {
        return new PayloadRegistry.Mapping(null, since, until);
    }

    public static PayloadRegistry.@NonNull Mapping map(
            final @NonNull String identifier, final @NonNull MinecraftVersion since) {
        return new PayloadRegistry.Mapping(identifier, since);
    }

    public static PayloadRegistry.@NonNull Mapping map(final @NonNull MinecraftVersion since) {
        return new PayloadRegistry.Mapping(null, since);
    }

    public static PayloadRegistry.@NonNull Mapping map(final @NonNull String identifier) {
        return new PayloadRegistry.Mapping(identifier);
    }
}
