/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.mc.server.players;

import static dev.neuralnexus.taterapi.reflecto.MappingClass.builder;
import static dev.neuralnexus.taterapi.reflecto.MappingEntry.entry;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

public final class UserNameToIdResolver {
    public static final String USER_NAME_TO_ID_RESOLVER = "UserNameToIdResolver";
    public static Class<?> CLASS;

    private static boolean initialized = false;

    // spotless:off
    public static void init() {
        if (initialized) return;
        initialized = true;

        var userNameToIdResolver = builder(USER_NAME_TO_ID_RESOLVER,
                entry(Mappings.MOJANG, "net.minecraft.server.players.GameProfileCache").max(MinecraftVersions.V21_8),
                entry(Mappings.MOJANG, "net.minecraft.server.players.UserNameToIdResolver").min(MinecraftVersions.V21_9),
                entry(Mappings.LEGACY_SEARGE, "net.minecraft.server.management.PlayerProfileCache").range(MinecraftVersions.V7_6, MinecraftVersions.V16_5),
                entry(Mappings.SEARGE, "net.minecraft.server.players.GameProfileCache").min(MinecraftVersions.V17),
                entry(Mappings.YARN_INTERMEDIARY, "net.minecraft.class_3312").max(MinecraftVersions.V21_8),
                entry(Mappings.YARN_INTERMEDIARY, "net.minecraft.class_11561").min(MinecraftVersions.V21_9),
                entry(Mappings.CALAMUS, "net.minecraft.unmapped.C_09211509").min(MinecraftVersions.V7_6)
        ).build();
        CLASS = userNameToIdResolver.clazz();
    }
    // spotless:on
}
