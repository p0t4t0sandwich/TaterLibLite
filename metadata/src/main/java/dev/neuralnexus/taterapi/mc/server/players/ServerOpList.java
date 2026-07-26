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

public final class ServerOpList extends StoredUserList {
    public static final String SERVER_OP_LIST = "ServerOpList";
    public static Class<?> CLASS;

    private static boolean initialized = false;

    // spotless:off
    public static void init() {
        if (initialized) return;
        initialized = true;

        CLASS = builder(SERVER_OP_LIST,
                entry(Mappings.MOJANG, "net.minecraft.server.players.ServerOpList"),
                entry(Mappings.SEARGE, "net.minecraft.server.players.ServerOpList").min(MinecraftVersions.V17),
                entry(Mappings.SEARGE, "net.minecraft.server.management.OpList")
                        .range(MinecraftVersions.V14, MinecraftVersions.V16_5),
                entry(Mappings.SEARGE, "net.minecraft.server.management.UserListOps")
                        .range(MinecraftVersions.V7_6, MinecraftVersions.V13_2),
                entry(Mappings.YARN_INTERMEDIARY, "net.minecraft.class_3326"),
                entry(Mappings.CALAMUS, "net.minecraft.unmapped.C_48150417"))
                .build().clazz();
    }
    // spotless:on

    private final Object serverOpList;

    private ServerOpList(final @NonNull Object serverOpList) {
        super(serverOpList);
        init();
        this.serverOpList = serverOpList;
    }

    public static ServerOpList wrap(final @NonNull Object serverOpList) {
        return new ServerOpList(serverOpList);
    }

    @Override
    public Object unwrap() {
        return this.serverOpList;
    }

    @Override
    public Collection<@NonNull ServerOpListEntry> getEntries() {
        final Object result = Reflecto.invoke(STORED_USER_LIST, GET_ENTRIES, this.serverOpList);
        final Collection<?> entries =
                switch (result) {
                    case Collection<?> collection -> collection;
                    case Map<?, ?> map -> map.values();
                    default ->
                            throw new IllegalStateException(
                                    "Expected getEntries to return a Collection or Map, but got: "
                                            + result.getClass());
                };
        return entries.stream().map(ServerOpListEntry::wrap).toList();
    }
}
