/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.mc.server;

import static dev.neuralnexus.taterapi.reflecto.MappingClass.builder;
import static dev.neuralnexus.taterapi.reflecto.MappingEntry.entry;
import static dev.neuralnexus.taterapi.reflecto.MappingMember.member;

import dev.neuralnexus.taterapi.mc.server.players.PlayerList;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.reflecto.MappingMember;
import dev.neuralnexus.taterapi.reflecto.Reflecto;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodType;

public final class MinecraftServer {
    public static final String MINECRAFT_SERVER = "MinecraftServer";
    public static final String IS_DEDICATED_SERVER = "isDedicatedServer";
    public static final String GET_PLAYER_LIST = "getPlayerList";
    public static Class<?> CLASS;

    private static boolean initialized = false;

    // spotless:off
    public static void init() {
        if (initialized) return;
        initialized = true;

        var mcServer = builder(MINECRAFT_SERVER,
                entry("net.minecraft.server.MinecraftServer").constant(true))
                .build();
        CLASS = mcServer.clazz();

        var isDedicatedServer = member(IS_DEDICATED_SERVER, mcServer, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(boolean.class))
                .mappings(
                        entry(Mappings.MOJANG, "isDedicatedServer"),
                        entry(Mappings.SEARGE, "m_6982_",
                                MinecraftVersions.V17),
                        entry(Mappings.LEGACY_SEARGE, "func_71262_S",
                                MinecraftVersions.V7, MinecraftVersions.V16_5),
                        entry(Mappings.YARN_INTERMEDIARY, "method_3816"),
                        entry(Mappings.CALAMUS, "m_45654766"));

        PlayerList.init();
        var getPlayerList = member(GET_PLAYER_LIST, mcServer, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(PlayerList.CLASS))
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

        Reflecto.register(isDedicatedServer, getPlayerList);
    }
    // spotless:on

    public static net.minecraft.server.@NonNull MinecraftServer getServer() {
        return (net.minecraft.server.MinecraftServer) MetaAPI.instance().server();
    }

    public static boolean isDedicatedServer(Object server) {
        init();
        return Reflecto.invoke(MINECRAFT_SERVER, IS_DEDICATED_SERVER, server);
    }

    public static boolean isDedicatedServer() {
        return isDedicatedServer(getServer());
    }

    public static PlayerList getPlayerList(Object server) {
        init();
        return PlayerList.wrap(Reflecto.invoke(MINECRAFT_SERVER, GET_PLAYER_LIST, server));
    }

    public static PlayerList getPlayerList() {
        return getPlayerList(getServer());
    }
}
