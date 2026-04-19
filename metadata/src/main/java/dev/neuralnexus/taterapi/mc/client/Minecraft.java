/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.mc.client;

import static dev.neuralnexus.taterapi.reflecto.MappingClass.builder;
import static dev.neuralnexus.taterapi.reflecto.MappingEntry.entry;
import static dev.neuralnexus.taterapi.reflecto.MappingMember.member;

import dev.neuralnexus.taterapi.mc.server.MinecraftServer;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.reflecto.MappingMember;
import dev.neuralnexus.taterapi.reflecto.Reflecto;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodType;

public final class Minecraft {
    public static final String MINECRAFT = "Minecraft";
    public static final String GET_INSTANCE = "getInstance";
    public static final String HAS_SERVER = "hasServer";
    public static final String GET_SERVER = "getServer";

    private static boolean initialized = false;

    // spotless:off
    private static void init() {
        if (initialized) return;
        initialized = true;

        var mcClient = builder(MINECRAFT,
                entry(Mappings.MOJANG, "net.minecraft.client.Minecraft"),
                entry(Mappings.SEARGE, "net.minecraft.client.Minecraft",
                        MinecraftVersions.V17),
                entry(Mappings.LEGACY_SEARGE, "net.minecraft.client.Minecraft"),
                entry(Mappings.CALAMUS, "net.minecraft.client.Minecraft"),
                entry(Mappings.YARN_INTERMEDIARY, "net.minecraft.class_310"))
                .build();

        var getInstance = member(GET_INSTANCE, mcClient, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(mcClient.clazz()))
                .mappings(
                        entry(Mappings.MOJANG, "getInstance"),
                        entry(Mappings.SEARGE, "m_91087_",
                                MinecraftVersions.V17),
                        entry(Mappings.LEGACY_SEARGE, "func_71410_x"),
                        entry(Mappings.YARN_INTERMEDIARY, "method_1551"),
                        entry(Mappings.CALAMUS, "m_20213497"));

        var hasServer = member(HAS_SERVER, mcClient, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(boolean.class))
                .mappings(
                        entry(Mappings.MOJANG, "hasSingleplayerServer"),
                        entry(Mappings.SEARGE, "m_91091_"),
                        entry(Mappings.LEGACY_SEARGE, "func_71356_B"),
                        entry(Mappings.YARN_INTERMEDIARY, "method_1496"),
                        entry(Mappings.CALAMUS, "m_10057689"));

        MinecraftServer.init();
        var getServer = member(GET_SERVER, mcClient, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(MinecraftServer.CLASS))
                .mappings(
                        entry(Mappings.MOJANG, "getSingleplayerServer"),
                        entry(Mappings.SEARGE, "m_91092_"),
                        entry(Mappings.LEGACY_SEARGE, "func_71401_C"),
                        entry(Mappings.YARN_INTERMEDIARY, "method_1576"),
                        entry(Mappings.CALAMUS, "m_37046522"));

        Reflecto.register(getInstance, hasServer, getServer);
    }
    // spotless:on

    public static <T> @NonNull T getInstance() {
        init();
        return Reflecto.invoke(MINECRAFT, GET_INSTANCE);
    }

    public static boolean hasServer() {
        return Reflecto.invoke(MINECRAFT, HAS_SERVER, getInstance());
    }

    /**
     * It's fine to use a raw MinecraftServer reference, since it's not remapped.
     *
     * @return The MinecraftServer instance
     */
    public static net.minecraft.server.MinecraftServer getServer() {
        return Reflecto.invoke(MINECRAFT, GET_SERVER, getInstance());
    }

    /**
     * Get the "side" the server is running on
     *
     * @param isClient If the current environment is a client
     * @return The side
     */
    public static Side determineSide(boolean isClient) {
        Side side = Side.SERVER;
        if (isClient) {
            if (hasServer()) {
                side = Side.INTEGRATED;
            } else {
                side = Side.CLIENT;
            }
        }
        return side;
    }
}
