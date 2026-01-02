/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.resources;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Function;

public final class Identifier {
    private static final Logger logger = Logger.create("TaterLibLite/Identifier");

    private static MethodHandle newIdentifier;

    public static final Function<String, ?> IDENTIFIER =
            id -> {
                try {
                    return newIdentifier.invoke(id);
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            };

    // spotless:off
    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            final Class<?> clazz;
            if (Constraint.builder().max(MinecraftVersions.V16_5).build().result()) {
                clazz = Class.forName("net.minecraft.util.ResourceLocation");
                newIdentifier = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V17)
                    .max(MinecraftVersions.V20_4).build().result()) {
                clazz = Class.forName("net.minecraft.resources.ResourceLocation");
                newIdentifier = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V20_5)
                    .max(MinecraftVersions.V21_10).build().result()) {
                clazz = Class.forName("net.minecraft.resources.ResourceLocation");
                newIdentifier = lookup.findStatic(clazz, "parse", MethodType.methodType(clazz, String.class));
            } else if (Constraint.builder().min(MinecraftVersions.V21_11).build().result()) {
                clazz = Class.forName("net.minecraft.resources.Identifier");
                newIdentifier = lookup.findStatic(clazz, "parse", MethodType.methodType(clazz, String.class));
            }
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            logger.error("Failed to initialize Identifier function", e);
        }
    }
    // spotless:on

    @SuppressWarnings("unchecked")
    public static <T> T identifier(String id) {
        return (T) IDENTIFIER.apply(id);
    }
}
