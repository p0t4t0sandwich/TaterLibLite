/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.wrap.client;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.reflecto.two.MappingEntry;
import dev.neuralnexus.taterapi.reflecto.two.Member;
import dev.neuralnexus.taterapi.reflecto.two.Parent;
import dev.neuralnexus.taterapi.reflecto.two.Reflecto2;

import java.lang.invoke.MethodType;

public final class Minecraft {
    static {
        var mcClient =
                Parent.builder(
                                "Minecraft",
                                MappingEntry.builder(
                                        "net.minecraft.client.Minecraft",
                                        Mappings.MOJANG,
                                        Mappings.SEARGE,
                                        Mappings.LEGACY_SEARGE),
                                MappingEntry.builder(
                                        Mappings.YARN_INTERMEDIARY, "net.minecraft.class_310"),
                                MappingEntry.builder(
                                        Mappings.LEGACY_INTERMEDIARY, "net.minecraft.class_1600"))
                        .build();

        var isDedicatedServer =
                Member.builder("isDedicatedServer", mcClient, Member.Type.METHOD)
                        .methodType(MethodType.methodType(boolean.class))
                        .mappings(
                                MappingEntry.builder(Mappings.MOJANG, "isDedicatedServer"),
                                MappingEntry.builder(Mappings.SEARGE, "m_6982_"),
                                MappingEntry.builder(Mappings.LEGACY_SEARGE, "func_71262_S"),
                                MappingEntry.builder(Mappings.YARN_INTERMEDIARY, "method_3816"),
                                MappingEntry.builder(Mappings.LEGACY_INTERMEDIARY, "method_2983"));

        Reflecto2.register(isDedicatedServer);
    }
}
