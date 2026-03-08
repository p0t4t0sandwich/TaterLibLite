/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.wrap.server;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.reflecto.two.MappingEntry;
import dev.neuralnexus.taterapi.reflecto.two.Member;
import dev.neuralnexus.taterapi.reflecto.two.Parent;
import dev.neuralnexus.taterapi.reflecto.two.Reflecto2;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodType;

public final class MinecraftServer {

    static {
        var mcServer =
                Parent.builder(
                                "MinecraftServer",
                                MappingEntry.builder("net.minecraft.server.MinecraftServer")
                                        .constant(true))
                        .build();
        var isDedicatedServer =
                Member.builder("isDedicatedServer", mcServer, Member.Type.METHOD)
                        .methodType(MethodType.methodType(boolean.class))
                        .mappings(
                                MappingEntry.builder(Mappings.MOJANG, "isDedicatedServer"),
                                MappingEntry.builder(Mappings.SEARGE, "m_6982_"),
                                MappingEntry.builder(Mappings.LEGACY_SEARGE, "func_71262_S"),
                                MappingEntry.builder(Mappings.YARN_INTERMEDIARY, "method_3816"),
                                MappingEntry.builder(Mappings.LEGACY_INTERMEDIARY, "method_2983"));
        Reflecto2.register(isDedicatedServer);
    }

    @SuppressWarnings("unchecked")
    public static <T> @NonNull T getServer() {
        return (T) MetaAPI.instance().server();
    }

    public static boolean isDedicatedServer(Object server) {
        return Reflecto2.invoke("MinecraftServer", "isDedicatedServer", server);
    }
}
