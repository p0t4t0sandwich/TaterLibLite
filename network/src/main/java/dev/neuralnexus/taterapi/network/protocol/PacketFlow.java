/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol;

import org.jspecify.annotations.NonNull;

public enum PacketFlow {
    SERVERBOUND("serverbound") {
        @Override
        public int maxPayloadSize() {
            return MAX_SERVERBOUND_PAYLOAD_SIZE;
        }
    },
    CLIENTBOUND("clientbound") {
        @Override
        public int maxPayloadSize() {
            return MAX_CLIENTBOUND_PAYLOAD_SIZE;
        }
    },
    BIDIRECTIONAL("bidirectional") {
        @Override
        public int maxPayloadSize() {
            return Math.min(MAX_SERVERBOUND_PAYLOAD_SIZE, MAX_CLIENTBOUND_PAYLOAD_SIZE);
        }
    };

    private final String id;

    PacketFlow(final @NonNull String name) {
        this.id = name;
    }

    public @NonNull String id() {
        return this.id;
    }

    public static final int MAX_SERVERBOUND_PAYLOAD_SIZE = Short.MAX_VALUE; // 32 KiB
    public static final int MAX_CLIENTBOUND_PAYLOAD_SIZE = 1048576; // 1 MiB

    public abstract int maxPayloadSize();

    public PacketFlow getOpposite() {
        return this == CLIENTBOUND ? SERVERBOUND : CLIENTBOUND;
    }
}
