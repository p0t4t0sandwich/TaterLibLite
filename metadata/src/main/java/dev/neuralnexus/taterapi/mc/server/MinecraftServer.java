/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.mc.server;

import static dev.neuralnexus.taterapi.reflecto.MappingClass.builder;
import static dev.neuralnexus.taterapi.reflecto.MappingEntry.entry;
import static dev.neuralnexus.taterapi.reflecto.MappingMember.member;

import dev.neuralnexus.taterapi.mc.server.players.CachedUserNameToIdResolver;
import dev.neuralnexus.taterapi.mc.server.players.PlayerList;
import dev.neuralnexus.taterapi.mc.server.players.UserNameToIdResolver;
import dev.neuralnexus.taterapi.meta.Constraint;
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
    public static final String GET_SERVICES = "getServices";
    public static final String GET_PROFILE_CACHE = "getProfileCache";
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
                        entry(Mappings.SEARGE, "m_6982_").min(MinecraftVersions.V17),
                        entry(Mappings.LEGACY_SEARGE, "func_71262_S")
                                .range(MinecraftVersions.V7, MinecraftVersions.V16_5),
                        entry(Mappings.YARN_INTERMEDIARY, "method_3816"),
                        entry(Mappings.CALAMUS, "m_45654766").min(MinecraftVersions.V7_2));

        PlayerList.init();
        var getPlayerList = member(GET_PLAYER_LIST, mcServer, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(PlayerList.CLASS))
                .mappings(
                        entry(Mappings.MOJANG, "getPlayerList"),
                        entry(Mappings.SEARGE, "m_6846_").min(MinecraftVersions.V17),
                        entry(Mappings.LEGACY_SEARGE, "func_184103_al")
                                .range(MinecraftVersions.V9, MinecraftVersions.V16_5),
                        entry(Mappings.LEGACY_SEARGE, "func_71203_ab")
                                .range(MinecraftVersions.V7, MinecraftVersions.V8_9),
                        entry(Mappings.YARN_INTERMEDIARY, "method_3760"),
                        entry(Mappings.CALAMUS, "m_49852985").min(MinecraftVersions.V7_2));

        if (Constraint.noLessThan(MinecraftVersions.V19).result()) {
            Services.init();
            var services = member(GET_SERVICES, mcServer, MappingMember.Type.FIELD_GETTER)
                    .access(MappingMember.Access.PROTECTED)
                    .modifier(MappingMember.Modifier.FINAL)
                    .methodType(MethodType.methodType(Services.CLASS))
                    .mappings(
                            entry(Mappings.MOJANG, "services"),
                            entry(Mappings.SEARGE, "f_236721_"),
                            entry(Mappings.YARN_INTERMEDIARY, "field_39440"));

            Reflecto.register(services);
        }

        if (Constraint.noLessThan(MinecraftVersions.V21_9).result()) {
            UserNameToIdResolver.init();

            var getProfileCache = member(GET_PROFILE_CACHE, mcServer, MappingMember.Type.METHOD)
                    .access(MappingMember.Access.PROTECTED)
                    .modifier(MappingMember.Modifier.FINAL)
                    .methodType(MethodType.methodType(UserNameToIdResolver.CLASS))
                    .mappings(
                            entry(Mappings.MOJANG, "getProfileCache").max(MinecraftVersions.V21_8),
                            entry(Mappings.LEGACY_SEARGE, "func_152358_ax").range(MinecraftVersions.V7_6, MinecraftVersions.V16_5),
                            entry(Mappings.SEARGE, "m_129927_").min(MinecraftVersions.V17),
                            entry(Mappings.YARN_INTERMEDIARY, "method_3793").max(MinecraftVersions.V21_8),
                            entry(Mappings.CALAMUS, "m_04499243").min(MinecraftVersions.V7_6));

             Reflecto.register(getProfileCache);
        }

        Reflecto.register(isDedicatedServer, getPlayerList);
    }
    // spotless:on

    public static net.minecraft.server.@NonNull MinecraftServer getServer() {
        return (net.minecraft.server.MinecraftServer) MetaAPI.instance().server();
    }

    public static boolean isDedicatedServer(Object server) {
        init();
        try {
            return (boolean)
                    Reflecto.getHandle(MINECRAFT_SERVER, IS_DEDICATED_SERVER).invokeExact(server);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
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

    public static @NonNull CachedUserNameToIdResolver getProfileCache(Object server) {
        init();
        if (Constraint.noLessThan(MinecraftVersions.V19).result()) {
            return Services.nameToIdCache(Reflecto.invoke(MINECRAFT_SERVER, GET_SERVICES, server));
        }
        return CachedUserNameToIdResolver.wrap(
                Reflecto.invoke(MINECRAFT_SERVER, GET_PROFILE_CACHE, server));
    }

    public static @NonNull CachedUserNameToIdResolver getProfileCache() {
        return getProfileCache(getServer());
    }
}
