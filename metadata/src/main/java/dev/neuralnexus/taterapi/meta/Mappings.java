/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
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
    SPIGOT("spigot"), // Spigot 1.18+
    LEGACY_SPIGOT("legacy spigot"), // Spigot 1.17-
    SEARGE("searge"), // Forge 1.17.1+
    LEGACY_SEARGE("legacy searge"), // Forge 1.16.5-
    MCP("mcp"),
    YARN("yarn"),
    YARN_INTERMEDIARY("yarn intermediary"), // Fabric 1.14+
    // Deprecated by LegacyFabric, they now use Orinthe (Calamus) intermediary mappings
    LEGACY_INTERMEDIARY("legacy intermediary"),
    BABRIC_INTERMEDIARY("babric intermediary"),
    CALAMUS("calamus"), // Fabric 1.13-
    HASHED("hashed");

    private final String name;

    Mappings(@NonNull final String name) {
        this.name = name;
    }

    public boolean is(@NonNull final Mappings mappings) {
        return this == mappings;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
