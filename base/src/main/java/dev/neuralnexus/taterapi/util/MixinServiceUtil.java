/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.MixinService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.charset.StandardCharsets;

/** Helper/wrapper class to prevent ClassNotFound errors when Mixin is not present. */
public final class MixinServiceUtil {
    /**
     * Returns the Minecraft version.
     *
     * @return The Minecraft version.
     */
    public static String mcVersion() throws ClassNotFoundException, IOException {
        // Fine to do since obfuscated situations are covered by Forge
        // Reflect to get SharedConstants.VERSION_STRING
        // If SharedConstants.VERSION_STRING doesn't exist, fall back to the version.json
        final var ref =
                new Object() {
                    String mcVersion = null;
                };
        MixinService.getService()
                .getBytecodeProvider()
                .getClassNode("net.minecraft.SharedConstants")
                .fields
                .stream()
                .filter(field -> field.name.equals("VERSION_STRING"))
                .findFirst()
                .ifPresentOrElse(
                        fieldNode -> ref.mcVersion = (String) fieldNode.value,
                        () -> {
                            try {
                                ref.mcVersion = parseMcJson();
                            } catch (final ClassNotFoundException | IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
        return ref.mcVersion;
    }

    private static final Gson GSON = new GsonBuilder().create();

    private static String parseMcJson() throws ClassNotFoundException, IOException {
        // TODO: Update to use reflection as a fallback if Mixin is not present
        Class<?> sharedConstants =
                MixinService.getService()
                        .getClassProvider()
                        .findClass("net.minecraft.SharedConstants", false);

        // Parse the json included in modern versions of Minecraft
        // SharedConstants is going to get sacrificed, but no one should have a Mixin in that anyway
        try (final InputStream is =
                sharedConstants.getClassLoader().getResourceAsStream("version.json")) {
            if (is == null) {
                throw new IllegalStateException("version.json not found in JAR");
            }

            // Get "id" from the json
            try (final BufferedReader reader =
                    new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                JsonObject jsonObject = GSON.fromJson(reader, JsonObject.class);
                return jsonObject.get("id").getAsString();
            } catch (final IOException e) {
                throw new IOException("Failed to read version.json", e);
            }
        }
    }

    /**
     * Checks for a class
     *
     * @param className The class name
     */
    public static void checkForClass(@NonNull final String className)
            throws IOException, ClassNotFoundException {
        if (checkForOldMixin()) {
            try {
                checkForOldMixinClass(className);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            return;
        }
        MixinService.getService().getBytecodeProvider().getClassNode(className);
    }

    /**
     * Checks for a method
     *
     * @param className The class name
     * @param methodName The method name
     */
    public static void checkForMethod(
            @NonNull final String className, @NonNull final String methodName)
            throws IOException, ClassNotFoundException, NoSuchMethodException {
        if (checkForOldMixin()) {
            throw new UnsupportedOperationException(
                    "Old Mixin versions do not support checking a ClassNode's fields/methods");
        }
        MixinService.getService().getBytecodeProvider().getClassNode(className).methods.stream()
                .filter(method -> method.name.equals(methodName))
                .findFirst()
                .orElseThrow(() -> new NoSuchMethodException("Method not found"));
    }

    private static MethodHandle getClassNodeHandle;

    /** Check for old Mixin versions */
    @SuppressWarnings("JavaLangInvokeHandleSignature")
    public static boolean checkForOldMixin() {
        try {
            Class<?> clazz = Class.forName("org.spongepowered.asm.lib.tree.ClassNode");
            if (getClassNodeHandle == null) {
                MethodType methodType = MethodType.methodType(clazz, String.class);
                getClassNodeHandle =
                        MethodHandles.lookup()
                                .findVirtual(
                                        IClassBytecodeProvider.class, "getClassNode", methodType);
            }
            return true;
        } catch (final ClassNotFoundException | IllegalAccessException | NoSuchMethodException e) {
            return false;
        }
    }

    /** Check for classes using old Mixin versions */
    public static void checkForOldMixinClass(@NonNull final String className) throws Throwable {
        getClassNodeHandle.invoke(MixinService.getService().getBytecodeProvider(), className);
    }
}
