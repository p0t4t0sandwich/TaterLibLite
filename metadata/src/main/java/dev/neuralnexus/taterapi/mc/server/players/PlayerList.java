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

public final class PlayerList implements Wrapped<Object> {
    public static final String PLAYER_LIST = "PlayerList";
    public static final String GET_WHITELIST = "getWhiteList";
    public static Class<?> CLASS;

    private static boolean initialized = false;

    // spotless:off
    public static void init() {
        if (initialized) return;
        initialized = true;

        var playerList = builder(PLAYER_LIST,
                entry(Mappings.MOJANG, "net.minecraft.server.players.PlayerList"),
                entry(Mappings.SEARGE, "net.minecraft.server.players.PlayerList",
                        MinecraftVersions.V17),
                entry(Mappings.LEGACY_SEARGE, "net.minecraft.server.management.PlayerList",
                        MinecraftVersions.V9, MinecraftVersions.V16_5),
                entry(Mappings.LEGACY_SEARGE, "net.minecraft.server.management.ServerConfigurationManager",
                        MinecraftVersions.V7, MinecraftVersions.V8_9),
                entry(Mappings.YARN_INTERMEDIARY, "net.minecraft.class_3324"),
                entry(Mappings.CALAMUS, "net.minecraft.unmapped.C_29639016"))
                .build();
        CLASS = playerList.clazz();

        UserWhiteList.init();
        var getWhiteList = member(GET_WHITELIST, playerList, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(UserWhiteList.CLASS))
                .mappings(
                        entry(Mappings.MOJANG, "getPlayerList"),
                        entry(Mappings.SEARGE, "m_6846_",
                                MinecraftVersions.V17),
                        entry(Mappings.LEGACY_SEARGE, "func_184103_al",
                                MinecraftVersions.V9, MinecraftVersions.V16_5),
                        entry(Mappings.LEGACY_SEARGE, "func_71203_ab",
                                MinecraftVersions.V7, MinecraftVersions.V8_9),
                        entry(Mappings.YARN_INTERMEDIARY, "method_3760"),
                        entry(Mappings.CALAMUS, "m_49852985"));

        Reflecto.register(getWhiteList);
    }
    // spotless:on

    private final Object playerList;

    private PlayerList(final @NonNull Object playerList) {
        init();
        this.playerList = playerList;
    }

    public static PlayerList wrap(final @NonNull Object playerList) {
        return new PlayerList(playerList);
    }

    @Override
    public Object unwrap() {
        return this.playerList;
    }

    public UserWhiteList getWhiteList() {
        return UserWhiteList.wrap(Reflecto.invoke(PLAYER_LIST, GET_WHITELIST, this.playerList));
    }
}
