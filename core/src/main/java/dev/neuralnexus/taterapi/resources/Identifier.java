/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.resources;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class Identifier {
    private static final Logger logger = Logger.create("TaterLibLite/Identifier");

    private static final MethodHandle newIdentifier;

    @SuppressWarnings("unchecked")
    public static <T> @NonNull T identifier(final @NonNull String id) {
        try {
            return (T) newIdentifier.invokeExact(id);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    // spotless:off
    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            final Class<?> clazz;
            if (Constraint.builder().max(MinecraftVersions.V16_5).result()) {
                clazz = Class.forName("net.minecraft.util.ResourceLocation");
                newIdentifier = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V17)
                    .max(MinecraftVersions.V20_4).result()) {
                clazz = Class.forName("net.minecraft.resources.ResourceLocation");
                newIdentifier = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V20_5)
                    .max(MinecraftVersions.V21_10).result()) {
                clazz = Class.forName("net.minecraft.resources.ResourceLocation");
                newIdentifier = lookup.findStatic(clazz, "parse", MethodType.methodType(clazz, String.class));
            } else { // min(MinecraftVersions.V21_11)
                clazz = Class.forName("net.minecraft.resources.Identifier");
                newIdentifier = lookup.findStatic(clazz, "parse", MethodType.methodType(clazz, String.class));
            }
        } catch (final ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            logger.error("Failed to initialize Identifier function", e);
            throw new RuntimeException(e);
        }
    }
    // spotless:on
}
