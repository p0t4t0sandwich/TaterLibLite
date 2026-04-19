/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta;

import org.jspecify.annotations.NonNull;

/** Enum for platform runtime mappings */
public enum Mappings {
    UNKNOWN("unknown"),
    NONE("none"),
    OFFICIAL("official"),
    MOJANG("mojang"),
    SPIGOT("spigot"), // Spigot mappings have a divide starting at 1.18
    SEARGE("searge"), // Forge mappings have a divide starting at 1.17.1
    MCP("mcp"),
    YARN("yarn"),
    YARN_INTERMEDIARY("yarn intermediary"), // Fabric 1.14+
    // Deprecated by LegacyFabric, they now use Orinthe (Calamus) intermediary mappings
    LEGACY_INTERMEDIARY("legacy intermediary"),
    BABRIC_INTERMEDIARY("babric intermediary"),
    CALAMUS("calamus"), // Fabric 1.14.4-
    HASHED("hashed");

    private final String name;

    Mappings(final @NonNull String name) {
        this.name = name;
    }

    public boolean is(final @NonNull Mappings mappings) {
        return this == mappings;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
