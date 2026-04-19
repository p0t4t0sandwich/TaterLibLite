/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.protocol.handshake;

import static dev.neuralnexus.taterapi.network.Protocol.LOGIN_ID;
import static dev.neuralnexus.taterapi.network.Protocol.STATUS_ID;
import static dev.neuralnexus.taterapi.network.Protocol.TRANSFER_ID;

public enum ClientIntent {
    STATUS(STATUS_ID),
    LOGIN(LOGIN_ID),
    TRANSFER(TRANSFER_ID);

    private final int id;

    ClientIntent(int id) {
        this.id = id;
    }

    public int id() {
        return this.id;
    }

    public static ClientIntent byId(int id) {
        return switch (id) {
            case STATUS_ID -> STATUS;
            case LOGIN_ID -> LOGIN;
            case TRANSFER_ID -> TRANSFER;
            default -> throw new IllegalArgumentException("Unknown connection intent: " + id);
        };
    }
}
