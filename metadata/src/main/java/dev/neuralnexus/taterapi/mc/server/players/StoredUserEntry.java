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

public sealed class StoredUserEntry implements Wrapped<Object> permits UserWhiteListEntry {
    public static final String STORED_USER_ENTRY = "StoredUserEntry";
    public static final String GET_USER = "getUser";

    private static boolean initialized = false;

    // spotless:off
    private static void init() {
        if (initialized) return;
        initialized = true;

        var storedUserEntryClass = builder(STORED_USER_ENTRY,
                entry(Mappings.MOJANG, "net.minecraft.server.players.StoredUserEntry"),
                entry(Mappings.SEARGE, "net.minecraft.server.players.StoredUserEntry")
                        .min(MinecraftVersions.V17),
                entry(Mappings.SEARGE, "net.minecraft.server.management.UserListEntry")
                        .range(MinecraftVersions.V7, MinecraftVersions.V16_5),
                entry(Mappings.YARN_INTERMEDIARY, "net.minecraft.class_3330"),
                entry(Mappings.CALAMUS, "net.minecraft.unmapped.C_02601387"))
                .build();

        var getUser = member(GET_USER, storedUserEntryClass, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(Object.class))
                .mappings(
                        entry(Mappings.MOJANG, "getUser"),
                        entry(Mappings.SEARGE, "m_11373_").min(MinecraftVersions.V17),
                        entry(Mappings.SEARGE, "func_152640_f")
                                .range(MinecraftVersions.V7, MinecraftVersions.V16_5),
                        entry(Mappings.YARN_INTERMEDIARY, "method_14626"),
                        entry(Mappings.CALAMUS, "m_05084164")
                                .range(MinecraftVersions.V7, MinecraftVersions.V14_4));

        Reflecto.register(getUser);
    }
    // spotless:on

    private final Object storedUserEntry;

    StoredUserEntry(final @NonNull Object storedUserList) {
        init();
        this.storedUserEntry = storedUserList;
    }

    public static StoredUserEntry wrap(final @NonNull Object storedUserEntry) {
        return new StoredUserEntry(storedUserEntry);
    }

    @Override
    public Object unwrap() {
        return this.storedUserEntry;
    }

    public @NonNull Object getUser() {
        return Reflecto.invoke(STORED_USER_ENTRY, GET_USER, this.storedUserEntry);
    }
}
