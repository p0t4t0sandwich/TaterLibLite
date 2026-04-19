/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.mc.server.players;

import static dev.neuralnexus.taterapi.reflecto.MappingClass.builder;
import static dev.neuralnexus.taterapi.reflecto.MappingEntry.entry;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.reflecto.Reflecto;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Map;

public final class UserWhiteList extends StoredUserList {
    public static final String USER_WHITE_LIST = "UserWhiteList";
    public static Class<?> CLASS;

    private static boolean initialized = false;

    // spotless:off
    public static void init() {
        if (initialized) return;
        initialized = true;

        CLASS = builder(USER_WHITE_LIST,
                entry(Mappings.MOJANG, "net.minecraft.server.players.PlayerList"),
                entry(Mappings.SEARGE, "net.minecraft.server.players.PlayerList",
                        MinecraftVersions.V17),
                entry(Mappings.LEGACY_SEARGE, "net.minecraft.server.management.PlayerList",
                        MinecraftVersions.V9, MinecraftVersions.V16_5),
                entry(Mappings.LEGACY_SEARGE, "net.minecraft.server.management.ServerConfigurationManager",
                        MinecraftVersions.V7, MinecraftVersions.V8_9),
                entry(Mappings.YARN_INTERMEDIARY, "net.minecraft.class_3324"),
                entry(Mappings.CALAMUS, "net.minecraft.unmapped.C_29639016"))
                .build().clazz();
    }
    // spotless:on

    private final Object userWhiteList;

    private UserWhiteList(final @NonNull Object userWhiteList) {
        super(userWhiteList);
        init();
        this.userWhiteList = userWhiteList;
    }

    public static UserWhiteList wrap(final @NonNull Object userWhiteList) {
        return new UserWhiteList(userWhiteList);
    }

    @Override
    public Object unwrap() {
        return this.userWhiteList;
    }

    @Override
    public Collection<@NonNull UserWhiteListEntry> getEntries() {
        final Object result = Reflecto.invoke(STORED_USER_LIST, GET_ENTRIES, this.userWhiteList);
        final Collection<?> entries =
                switch (result) {
                    case Collection<?> collection -> collection;
                    case Map<?, ?> map -> map.values();
                    default ->
                            throw new IllegalStateException(
                                    "Expected getEntries to return a Collection or Map, but got: "
                                            + result.getClass());
                };
        return entries.stream().map(UserWhiteListEntry::wrap).toList();
    }
}
