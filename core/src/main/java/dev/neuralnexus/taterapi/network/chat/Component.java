/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.chat;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class Component {
    private static final Logger logger = Logger.create("TaterLibLite/Component");

    private static final MethodHandle newLiteral;
    private static final MethodHandle newTranslatable;

    @SuppressWarnings("unchecked")
    public static <T> @NonNull T literal(final @NonNull String text) {
        try {
            return (T) newLiteral.invokeExact(text);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> @NonNull T translatable(final @NonNull String key) {
        try {
            // Can't invokeExact here because of varargs constructor in older versions
            return (T) newTranslatable.invoke(key);
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
    }

    // spotless:off
    static {
        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        Class<?> clazz;
        Class<?> rType;
        try { // Component.literal(String)
            if (Constraint.builder().max(MinecraftVersions.V8_9).build().result()) {
                clazz = Class.forName("net.minecraft.util.ChatComponentText");
                newLiteral = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V9)
                    .max(MinecraftVersions.V13_2).build().result()) {
                clazz = Class.forName("net.minecraft.util.text.TextComponentString");
                newLiteral = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V14)
                    .max(MinecraftVersions.V16_5).build().result()) {
                clazz = Class.forName("net.minecraft.util.text.StringTextComponent");
                newLiteral = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V17)
                    .max(MinecraftVersions.V18_2).build().result()) {
                clazz = Class.forName("net.minecraft.network.chat.TextComponent");
                newLiteral = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class));
            } else if (Constraint.builder()
                    .mappings(Mappings.SEARGE)
                    .min(MinecraftVersions.V19)
                    .max(MinecraftVersions.V20_4).build().result()) {
                clazz = Class.forName("net.minecraft.network.chat.Component");
                rType = Class.forName("net.minecraft.network.chat.MutableComponent");
                newLiteral = lookup.findStatic(clazz, "m_237113_", MethodType.methodType(rType, String.class));
            } else if (Constraint.builder()
                    .mappings(Mappings.MOJANG)
                    .min(MinecraftVersions.V19).build().result()) {
                clazz = Class.forName("net.minecraft.network.chat.Component");
                rType = Class.forName("net.minecraft.network.chat.MutableComponent");
                newLiteral = lookup.findStatic(clazz, "literal", MethodType.methodType(rType, String.class));
            } else {
                throw new RuntimeException("No matching version for Component.literal");
            }
        } catch (final ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            logger.error("Failed to initialize Component.literal function", e);
            throw new RuntimeException(e);
        }

        try { // Component.translatable(String)
            if (Constraint.builder().max(MinecraftVersions.V8_9).build().result()) {
                clazz = Class.forName("net.minecraft.util.ChatComponentTranslation");
                newTranslatable = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class, Object[].class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V9)
                    .max(MinecraftVersions.V13_2).build().result()) {
                clazz = Class.forName("net.minecraft.util.text.TextComponentTranslation");
                newTranslatable = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class, Object[].class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V14)
                    .max(MinecraftVersions.V16_5).build().result()) {
                clazz = Class.forName("net.minecraft.util.text.TranslationTextComponent");
                newTranslatable = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class, Object[].class));
            } else if (Constraint.builder()
                    .min(MinecraftVersions.V17)
                    .max(MinecraftVersions.V18_2).build().result()) {
                clazz = Class.forName("net.minecraft.network.chat.TranslatableComponent");
                newTranslatable = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class));
            } else if (Constraint.builder()
                    .mappings(Mappings.SEARGE)
                    .min(MinecraftVersions.V19)
                    .max(MinecraftVersions.V20_4).build().result()) {
                clazz = Class.forName("net.minecraft.network.chat.Component");
                rType = Class.forName("net.minecraft.network.chat.MutableComponent");
                newTranslatable = lookup.findStatic(clazz, "m_237115_", MethodType.methodType(rType, String.class));
            } else if (Constraint.builder()
                    .mappings(Mappings.MOJANG)
                    .min(MinecraftVersions.V19).build().result()) {
                clazz = Class.forName("net.minecraft.network.chat.Component");
                rType = Class.forName("net.minecraft.network.chat.MutableComponent");
                newTranslatable = lookup.findStatic(clazz, "translatable", MethodType.methodType(rType, String.class));
            } else {
                throw new RuntimeException("No matching version for Component.translatable");
            }
        } catch (final ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            logger.error("Failed to initialize Component.translatable function", e);
            throw new RuntimeException(e);
        }
    }
    // spotless:on
}
