/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.mc.server.players;

import static dev.neuralnexus.taterapi.reflecto.MappingClass.builder;
import static dev.neuralnexus.taterapi.reflecto.MappingEntry.entry;
import static dev.neuralnexus.taterapi.reflecto.MappingMember.member;

import com.mojang.authlib.GameProfile;

import dev.neuralnexus.taterapi.Wrapped;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.reflecto.MappingMember;
import dev.neuralnexus.taterapi.reflecto.Reflecto;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodType;
import java.util.Set;

public final class PlayerList implements Wrapped<Object> {
    public static final String PLAYER_LIST = "PlayerList";
    public static final String IS_OP = "isOp";
    public static final String GET_OPS = "getOps";
    public static final String GET_WHITELIST = "getWhiteList";
    public static Class<?> CLASS;

    private static boolean initialized = false;

    // spotless:off
    public static void init() {
        if (initialized) return;
        initialized = true;

        var playerList = builder(PLAYER_LIST,
                entry(Mappings.MOJANG, "net.minecraft.server.players.PlayerList"),
                entry(Mappings.SEARGE, "net.minecraft.server.players.PlayerList")
                        .min(MinecraftVersions.V17),
                entry(Mappings.SEARGE, "net.minecraft.server.management.PlayerList")
                        .range(MinecraftVersions.V9, MinecraftVersions.V16_5),
                entry(Mappings.SEARGE, "net.minecraft.server.management.ServerConfigurationManager")
                        .range(MinecraftVersions.V7, MinecraftVersions.V8_9),
                entry(Mappings.YARN_INTERMEDIARY, "net.minecraft.class_3324"),
                entry(Mappings.CALAMUS, "net.minecraft.unmapped.C_29639016"))
                .build();
        CLASS = playerList.clazz();

        NameAndId.init();
        var isOp = member(IS_OP, playerList, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(boolean.class, GameProfile.class))
                .mappings(
                        entry(Mappings.MOJANG, "isOp").max(MinecraftVersions.V21_8),
                        entry(Mappings.MOJANG, "isOp")
                                .min(MinecraftVersions.V21_9)
                                .methodType(MethodType.methodType(boolean.class, NameAndId.CLASS)),
                        entry(Mappings.SEARGE, "m_11303").min(MinecraftVersions.V17),
                        entry(Mappings.SEARGE, "func_152596_g")
                                .range(MinecraftVersions.V7_6, MinecraftVersions.V16_5),
                        entry(Mappings.SEARGE, "func_72353_e")
                                .range(MinecraftVersions.V6_4, MinecraftVersions.V7_5)
                                .methodType(MethodType.methodType(boolean.class, String.class)),
                        entry(Mappings.YARN_INTERMEDIARY, "method_14569").max(MinecraftVersions.V21_8),
                        entry(Mappings.YARN_INTERMEDIARY, "method_14569")
                                .min(MinecraftVersions.V21_9)
                                .methodType(MethodType.methodType(boolean.class, NameAndId.CLASS)),
                        entry(Mappings.CALAMUS, "m_55622692").min(MinecraftVersions.V7_6),
                        entry(Mappings.CALAMUS, "m_55622692")
                                .range(MinecraftVersions.V6_4, MinecraftVersions.V7_5)
                                .methodType(MethodType.methodType(boolean.class, String.class)));

        ServerOpList.init();
        var getOps = member(GET_OPS, playerList, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(ServerOpList.CLASS))
                .mappings(
                        entry(Mappings.MOJANG, "getOps"),
                        entry(Mappings.SEARGE, "m_11307_").min(MinecraftVersions.V17),
                        entry(Mappings.SEARGE, "func_152603_m")
                                .range(MinecraftVersions.V7_6, MinecraftVersions.V16_5),
                        entry(Mappings.SEARGE, "func_72376_i")
                                .methodType(MethodType.methodType(Set.class))
                                .range(MinecraftVersions.V6_4, MinecraftVersions.V7_5),
                        entry(Mappings.YARN_INTERMEDIARY, "method_14603"),
                        entry(Mappings.CALAMUS, "m_44723700"));

        UserWhiteList.init();
        var getWhiteList = member(GET_WHITELIST, playerList, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(UserWhiteList.CLASS))
                .mappings(
                        entry(Mappings.MOJANG, "getWhiteList"),
                        entry(Mappings.SEARGE, "m_11305_").min(MinecraftVersions.V17),
                        entry(Mappings.SEARGE, "func_152599_k")
                                .range(MinecraftVersions.V7_6, MinecraftVersions.V16_5),
                        entry(Mappings.SEARGE, "func_72388_h")
                                .methodType(MethodType.methodType(Set.class))
                                .range(MinecraftVersions.V6_4, MinecraftVersions.V7_5),
                        entry(Mappings.YARN_INTERMEDIARY, "method_14590"),
                        entry(Mappings.CALAMUS, "m_12096449"));

        Reflecto.register(isOp, getOps, getWhiteList);
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

    public boolean isOp(final @NonNull Object nameOrProfile) {
        return Reflecto.invoke(PLAYER_LIST, IS_OP, this.playerList, nameOrProfile);
    }

    public ServerOpList getOps() {
        return ServerOpList.wrap(Reflecto.invoke(PLAYER_LIST, GET_OPS, this.playerList));
    }

    public UserWhiteList getWhiteList() {
        return UserWhiteList.wrap(Reflecto.invoke(PLAYER_LIST, GET_WHITELIST, this.playerList));
    }
}
