/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.mc.server;

import static dev.neuralnexus.taterapi.reflecto.MappingClass.builder;
import static dev.neuralnexus.taterapi.reflecto.MappingEntry.entry;
import static dev.neuralnexus.taterapi.reflecto.MappingMember.member;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.reflecto.MappingMember;
import dev.neuralnexus.taterapi.reflecto.Reflecto;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodType;

public final class MinecraftServer {
    public static final String MINECRAFT_SERVER = "MinecraftServer";
    public static final String IS_DEDICATED_SERVER = "isDedicatedServer";

    private static boolean initialized = false;

    private static void init() {
        initialized = true;

        var mcServer =
                builder(
                                MINECRAFT_SERVER,
                                entry("net.minecraft.server.MinecraftServer").constant(true))
                        .build();

        var mcServer_isDedicatedServer =
                member(IS_DEDICATED_SERVER, mcServer, MappingMember.Type.METHOD)
                        .methodType(MethodType.methodType(boolean.class))
                        .mappings(
                                entry(Mappings.MOJANG, "isDedicatedServer"),
                                entry(Mappings.SEARGE, "m_6982_"),
                                entry(Mappings.LEGACY_SEARGE, "func_71262_S"),
                                entry(Mappings.YARN_INTERMEDIARY, "method_3816"),
                                entry(Mappings.CALAMUS, "m_45654766"));

        Reflecto.register(mcServer_isDedicatedServer);
    }

    @SuppressWarnings("unchecked")
    public static <T> @NonNull T getServer() {
        return (T) MetaAPI.instance().server();
    }

    public static boolean isDedicatedServer(Object server) {
        if (!initialized) init();
        return Reflecto.invoke(MINECRAFT_SERVER, IS_DEDICATED_SERVER, server);
    }
}
