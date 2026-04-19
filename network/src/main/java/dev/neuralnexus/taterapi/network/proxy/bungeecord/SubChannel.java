/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.proxy.bungeecord;

import com.google.common.net.InetAddresses;

import dev.neuralnexus.taterapi.network.FriendlyByteBuf;
import dev.neuralnexus.taterapi.network.protocol.common.custom.CustomPacketPayload;

import io.netty.channel.unix.DomainSocketAddress;

import org.jspecify.annotations.NonNull;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unchecked")
public enum SubChannel {
    Connect("Connect"),
    ConnectOther("ConnectOther"),
    Forward("Forward") {
        @Override
        public @NonNull CustomPacketPayload response(final @NonNull BungeeCordPayload payload) {
            final String channel = payload.data().readUtf();
            final int length = payload.data().readUnsignedShort();
            final FriendlyByteBuf buf = payload.data().readPayload(length);
            return new CustomPacketPayload.Raw(channel, buf);
        }
    },
    ForwardToPlayer("ForwardToPlayer") {
        @Override
        public @NonNull CustomPacketPayload response(final @NonNull BungeeCordPayload payload) {
            return Forward.response(payload);
        }
    },
    GetPlayerServer("GetPlayerServer") {
        @Override
        public @NonNull PlayerResponse<String> response(final @NonNull BungeeCordPayload payload) {
            final String player = payload.data().readUtf();
            final String server = payload.data().readUtf();
            return new PlayerResponse<>(player, server);
        }
    },
    GetServer("GetServer") {
        @Override
        public @NonNull String response(final @NonNull BungeeCordPayload payload) {
            return payload.data().readUtf();
        }
    },
    GetServers("GetServers") {
        @Override
        public @NonNull List<String> response(final @NonNull BungeeCordPayload payload) {
            return List.of(payload.data().readUtf().split(", "));
        }
    },
    IP("IP") {
        @Override
        public @NonNull SocketAddress response(final @NonNull BungeeCordPayload payload) {
            final String ip = payload.data().readUtf();
            final int port = payload.data().readInt();
            if (ip.startsWith("unix://")) {
                return new DomainSocketAddress(ip.substring(7));
            }
            return new InetSocketAddress(InetAddresses.forString(ip), port);
        }
    },
    IPOther("IPOther") {
        @Override
        public @NonNull PlayerResponse<SocketAddress> response(
                final @NonNull BungeeCordPayload payload) {
            final String player = payload.data().readUtf();
            return new PlayerResponse<>(player, IP.response(payload));
        }
    },
    KickPlayer("KickPlayer"),
    KickPlayerRaw("KickPlayerRaw"),
    Message("Message"),
    MessageRaw("MessageRaw"),
    PlayerCount("PlayerCount") {
        @Override
        public @NonNull ServerResponse<Integer> response(final @NonNull BungeeCordPayload payload) {
            final String server = payload.data().readUtf(); // Can be ALL
            final int count = payload.data().readInt();
            return new ServerResponse<>(server, count);
        }
    },
    PlayerList("PlayerList") {
        @Override
        public @NonNull ServerResponse<List<String>> response(
                final @NonNull BungeeCordPayload payload) {
            final String server = payload.data().readUtf(); // Can be ALL
            final String[] playerList = payload.data().readUtf().split(", ");
            return new ServerResponse<>(server, List.of(playerList));
        }
    },
    ServerIP("ServerIP") {
        @Override
        public @NonNull ServerResponse<SocketAddress> response(
                final @NonNull BungeeCordPayload payload) {
            final String server = payload.data().readUtf();
            return new ServerResponse<>(server, IP.response(payload));
        }
    },
    UUID("UUID") {
        @Override
        public @NonNull UUID response(final @NonNull BungeeCordPayload payload) {
            return java.util.UUID.fromString(payload.data().readUtf());
        }
    },
    UUIDOther("UUIDOther") {
        @Override
        public @NonNull PlayerResponse<UUID> response(final @NonNull BungeeCordPayload payload) {
            final String player = payload.data().readUtf();
            final UUID uuid = java.util.UUID.fromString(payload.data().readUtf());
            return new PlayerResponse<>(player, uuid);
        }
    };

    public <T> @NonNull T response(final @NonNull BungeeCordPayload payload) {
        throw new UnsupportedOperationException(
                "SubChannel " + this.id + " does not support responses");
    }

    private final @NonNull String id;

    SubChannel(final @NonNull String id) {
        this.id = id;
    }

    public @NonNull String id() {
        return this.id;
    }

    public static SubChannel fromId(final @NonNull String id) {
        for (final SubChannel subchannel : values()) {
            if (subchannel.id.equals(id)) {
                return subchannel;
            }
        }
        throw new IllegalArgumentException("Unknown subchannel: " + id);
    }
}
