/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol;

import org.jspecify.annotations.NonNull;

public enum PacketFlow {
    SERVERBOUND("serverbound"),
    CLIENTBOUND("clientbound");

    private final String id;

    PacketFlow(final @NonNull String name) {
        this.id = name;
    }

    public @NonNull String id() {
        return this.id;
    }

    public PacketFlow getOpposite() {
        return this == CLIENTBOUND ? SERVERBOUND : CLIENTBOUND;
    }
}
