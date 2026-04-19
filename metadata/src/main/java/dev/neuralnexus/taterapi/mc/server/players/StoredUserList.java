/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.mc.server.players;

import static dev.neuralnexus.taterapi.reflecto.MappingClass.builder;
import static dev.neuralnexus.taterapi.reflecto.MappingEntry.entry;
import static dev.neuralnexus.taterapi.reflecto.MappingMember.member;

import dev.neuralnexus.taterapi.Wrapped;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.reflecto.MappingMember;
import dev.neuralnexus.taterapi.reflecto.Reflecto;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodType;
import java.util.Collection;
import java.util.Map;

public sealed class StoredUserList implements Wrapped<Object> permits UserWhiteList {
    public static final String STORED_USER_LIST = "StoredUserList";
    public static final String GET_ENTRIES = "getEntries";

    private static boolean initialized = false;

    // spotless:off
    private static void init() {
        if (initialized) return;
        initialized = true;

        var storedUserListClass = builder(STORED_USER_LIST,
                entry(Mappings.MOJANG, "net.minecraft.server.players.StoredUserList"),
                entry(Mappings.SEARGE, "net.minecraft.server.players.StoredUserList",
                        MinecraftVersions.V17),
                entry(Mappings.LEGACY_SEARGE, "net.minecraft.server.management.UserList",
                        MinecraftVersions.V7, MinecraftVersions.V16_5),
                entry(Mappings.YARN_INTERMEDIARY, "net.minecraft.class_3331"),
                entry(Mappings.CALAMUS, "net.minecraft.unmapped.C_43194647"))
                .build();

        var getEntries = member(GET_ENTRIES, storedUserListClass, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(Collection.class))
                .mappings(
                        entry(Mappings.MOJANG, "getEntries"),
                        entry(Mappings.SEARGE, "m_11395_",
                                MinecraftVersions.V17),
                        entry(Mappings.LEGACY_SEARGE, "func_199043_f",
                                MinecraftVersions.V13, MinecraftVersions.V16_5),
                        entry(Mappings.LEGACY_SEARGE, "func_152688_e",
                                MinecraftVersions.V7, MinecraftVersions.V12_2)
                                .methodType(MethodType.methodType(Map.class)),
                        entry(Mappings.YARN_INTERMEDIARY, "method_14632"),
                        entry(Mappings.CALAMUS, "m_49852985",
                                MinecraftVersions.V13, MinecraftVersions.V14_4),
                        entry(Mappings.CALAMUS, "m_66577243",
                                MinecraftVersions.V7, MinecraftVersions.V12_2)
                                .methodType(MethodType.methodType(Map.class)));

        Reflecto.register(getEntries);
    }
    // spotless:on

    private final Object storedUserList;

    StoredUserList(final @NonNull Object storedUserList) {
        init();
        this.storedUserList = storedUserList;
    }

    public static StoredUserList wrap(final @NonNull Object storedUserList) {
        return new StoredUserList(storedUserList);
    }

    @Override
    public Object unwrap() {
        return this.storedUserList;
    }

    public Collection<? extends @NonNull StoredUserEntry> getEntries() {
        final Object result = Reflecto.invoke(STORED_USER_LIST, GET_ENTRIES, this.storedUserList);
        final Collection<?> entries =
                switch (result) {
                    case Collection<?> collection -> collection;
                    case Map<?, ?> map -> map.values();
                    default ->
                            throw new IllegalStateException(
                                    "Expected getEntries to return a Collection or Map, but got: "
                                            + result.getClass());
                };
        return entries.stream().map(StoredUserEntry::wrap).toList();
    }
}
