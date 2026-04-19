/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.mc.server.players;

import static dev.neuralnexus.taterapi.reflecto.MappingClass.builder;
import static dev.neuralnexus.taterapi.reflecto.MappingEntry.entry;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.reflecto.Reflecto;

import org.jspecify.annotations.NonNull;

public final class UserWhiteListEntry extends StoredUserEntry {
    public static final String USER_WHITE_LIST_ENTRY = "UserWhiteListEntry";

    private static boolean initialized = false;

    // spotless:off
    private static void init() {
        if (initialized) return;
        initialized = true;

        var userWhiteListEntry = builder(USER_WHITE_LIST_ENTRY,
                entry(Mappings.MOJANG, "net.minecraft.server.players.UserWhiteListEntry"),
                entry(Mappings.SEARGE, "net.minecraft.server.players.UserWhiteListEntry").min(MinecraftVersions.V17),
                entry(Mappings.SEARGE, "net.minecraft.server.management.WhitelistEntry")
                        .range(MinecraftVersions.V14, MinecraftVersions.V16_5),
                entry(Mappings.SEARGE, "net.minecraft.server.management.UserListWhitelistEntry")
                        .range(MinecraftVersions.V7, MinecraftVersions.V13_2),
                entry(Mappings.YARN_INTERMEDIARY, "net.minecraft.class_3340"),
                entry(Mappings.CALAMUS, "net.minecraft.unmapped.C_02369546"))
                .build();
    }
    // spotless:on

    private final Object userWhiteListEntry;

    UserWhiteListEntry(final @NonNull Object userWhiteListEntry) {
        super(userWhiteListEntry);
        init();
        this.userWhiteListEntry = userWhiteListEntry;
    }

    public static UserWhiteListEntry wrap(final @NonNull Object userWhiteListEntry) {
        return new UserWhiteListEntry(userWhiteListEntry);
    }

    @Override
    public Object unwrap() {
        return this.userWhiteListEntry;
    }

    @Override
    public @NonNull NameAndId getUser() {
        final Object result = Reflecto.invoke(STORED_USER_ENTRY, GET_USER, this.userWhiteListEntry);
        if (result instanceof GameProfile gameProfile) {
            return new NameAndId(gameProfile);
        } else {
            return NameAndId.wrap(result);
        }
    }
}
