/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.mc.server.players;

import static dev.neuralnexus.taterapi.reflecto.MappingClass.builder;
import static dev.neuralnexus.taterapi.reflecto.MappingEntry.entry;
import static dev.neuralnexus.taterapi.reflecto.MappingMember.member;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.reflecto.MappingMember;
import dev.neuralnexus.taterapi.reflecto.Reflecto;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

public final class ServerOpListEntry extends StoredUserEntry {
    public static final String SERVER_OP_LIST_ENTRY = "ServerOpListEntry";
    public static final String GET_LEVEL = "getLevel";

    private static boolean initialized = false;

    // spotless:off
    private static void init() {
        if (initialized) return;
        initialized = true;

        var serverOpListEntry = builder(SERVER_OP_LIST_ENTRY,
                entry(Mappings.MOJANG, "net.minecraft.server.players.ServerOpListEntry"),
                entry(Mappings.SEARGE, "net.minecraft.server.players.ServerOpListEntry").min(MinecraftVersions.V17),
                entry(Mappings.SEARGE, "net.minecraft.server.management.OpEntry")
                        .range(MinecraftVersions.V14, MinecraftVersions.V16_5),
                entry(Mappings.SEARGE, "net.minecraft.server.management.UserListOpsEntry")
                        .range(MinecraftVersions.V7, MinecraftVersions.V13_2),
                entry(Mappings.YARN_INTERMEDIARY, "net.minecraft.class_3327"),
                entry(Mappings.CALAMUS, "net.minecraft.unmapped.C_59165424"))
                .build();

        var getLevel = member(GET_LEVEL, serverOpListEntry, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(int.class))
                .mappings(
                        entry(Mappings.MOJANG, "getLevel"),
                        entry(Mappings.SEARGE, "m_11363_").min(MinecraftVersions.V17),
                        entry(Mappings.SEARGE, "func_152644_a")
                                .range(MinecraftVersions.V14, MinecraftVersions.V16_5),
                        entry(Mappings.SEARGE, "func_152644_a")
                                .range(MinecraftVersions.V7, MinecraftVersions.V13_2),
                        entry(Mappings.YARN_INTERMEDIARY, "method_14623"),
                        entry(Mappings.CALAMUS, "m_35425402"));

        // TODO: permissions;()Lnet/minecraft/server/permissions/LevelBasedPermissionSet;

        Reflecto.register(getLevel);
    }
    // spotless:on

    private final Object serverOpListEntry;

    ServerOpListEntry(final @NonNull Object serverOpListEntry) {
        super(serverOpListEntry);
        init();
        this.serverOpListEntry = serverOpListEntry;
    }

    public static ServerOpListEntry wrap(final @NonNull Object serverOpListEntry) {
        return new ServerOpListEntry(serverOpListEntry);
    }

    @Override
    public Object unwrap() {
        return this.serverOpListEntry;
    }

    @Override
    public @NonNull NameAndId getUser() {
        final Object result = Reflecto.invoke(STORED_USER_ENTRY, GET_USER, this.serverOpListEntry);
        if (result instanceof GameProfile gameProfile) {
            return new NameAndId(gameProfile);
        } else {
            return NameAndId.wrap(result);
        }
    }

    public int getLevel() {
        final MethodHandle mh = Reflecto.getHandle(SERVER_OP_LIST_ENTRY, GET_LEVEL);
        try {
            return (int) mh.invokeExact(this.serverOpListEntry);
        } catch (final Throwable t) {
            throw new RuntimeException("Failed to invoke getLevel on ServerOpListEntry", t);
        }
    }
}
