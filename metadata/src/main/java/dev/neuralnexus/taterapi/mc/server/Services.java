/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.mc.server;

import static dev.neuralnexus.taterapi.reflecto.MappingClass.builder;
import static dev.neuralnexus.taterapi.reflecto.MappingEntry.entry;
import static dev.neuralnexus.taterapi.reflecto.MappingMember.member;

import dev.neuralnexus.taterapi.mc.server.players.CachedUserNameToIdResolver;
import dev.neuralnexus.taterapi.mc.server.players.UserNameToIdResolver;
import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.reflecto.MappingMember;
import dev.neuralnexus.taterapi.reflecto.Reflecto;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodType;

public final class Services {
    public static final String SERVICES = "Services";
    public static final String NAME_TO_ID_CACHE = "nameToIdCache";
    public static Class<?> CLASS;

    private static boolean initialized = false;

    // spotless:off
    public static void init() {
        if (initialized) return;
        initialized = true;

        if (Constraint.lessThan(MinecraftVersions.V19).result()) {
            return; // Services doesn't exist
        }

        var services = builder(SERVICES,
                entry(Mappings.MOJANG, "net.minecraft.server.players.GameProfileCache").max(MinecraftVersions.V21_8),
                entry(Mappings.MOJANG, "net.minecraft.server.players.CachedUserNameToIdResolver", MinecraftVersions.V21_9),
                entry(Mappings.LEGACY_SEARGE, "net.minecraft.server.management.PlayerProfileCache", MinecraftVersions.V7_6, MinecraftVersions.V16_5),
                entry(Mappings.SEARGE, "net.minecraft.server.players.GameProfileCache", MinecraftVersions.V17),
                entry(Mappings.YARN_INTERMEDIARY, "net.minecraft.class_3312"),
                entry(Mappings.CALAMUS, "net.minecraft.unmapped.C_09211509", MinecraftVersions.V7_6)
        ).build();
        CLASS = services.clazz();

        UserNameToIdResolver.init();
        var nameToIdCache = member(NAME_TO_ID_CACHE, services, MappingMember.Type.METHOD)
                .methodType(MethodType.methodType(UserNameToIdResolver.CLASS))
                .mappings(
                        entry(Mappings.MOJANG, "profileCache").range(MinecraftVersions.V19, MinecraftVersions.V21_8),
                        entry(Mappings.MOJANG, "nameToIdCache").min(MinecraftVersions.V21_9),
                        entry(Mappings.SEARGE, "f_214336_").range(MinecraftVersions.V19, MinecraftVersions.V21_8),
                        entry(Mappings.SEARGE, "f_412775_").min(MinecraftVersions.V21_9),
                        entry(Mappings.YARN_INTERMEDIARY, "comp_840").range(MinecraftVersions.V19, MinecraftVersions.V21_8),
                        entry(Mappings.YARN_INTERMEDIARY, "comp_4407").min(MinecraftVersions.V21_9));

        Reflecto.register(nameToIdCache);
    }
    // spotless:on

    public static @NonNull CachedUserNameToIdResolver nameToIdCache(final Object services) {
        return CachedUserNameToIdResolver.wrap(
                Reflecto.invoke(SERVICES, NAME_TO_ID_CACHE, services));
    }
}
