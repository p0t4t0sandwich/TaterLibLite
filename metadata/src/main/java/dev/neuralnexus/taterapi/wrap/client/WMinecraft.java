/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.wrap.client;

import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.reflecto.Reflecto;

import org.jspecify.annotations.NonNull;

public final class WMinecraft {
    public static final String MINECRAFT = "Minecraft";
    public static final String GET_INSTANCE = "getInstance";
    public static final String HAS_SERVER = "hasServer";
    public static final String GET_SERVER = "getServer";

    public static <T> @NonNull T getInstance() {
        return Reflecto.invoke(MINECRAFT, GET_INSTANCE, null);
    }

    public static boolean hasServer() {
        return Reflecto.invoke(MINECRAFT, HAS_SERVER, getInstance());
    }

    public static <T> @NonNull T getServer() {
        return Reflecto.invoke(MINECRAFT, GET_SERVER, getInstance());
    }

    /**
     * Get the "side" the server is running on
     *
     * @param isClient If the current environment is a client
     * @return The side
     */
    public static Side determineSide(boolean isClient) {
        Side side = Side.SERVER;
        if (isClient) {
            if (hasServer()) {
                side = Side.INTEGRATED;
            } else {
                side = Side.CLIENT;
            }
        }
        return side;
    }
}
