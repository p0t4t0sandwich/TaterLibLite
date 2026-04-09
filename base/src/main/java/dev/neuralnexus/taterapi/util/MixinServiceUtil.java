/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.service.MixinService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        MixinService.getService() // TODO: Add shaded ASM fallback
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
        final Class<?> sharedConstants =
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
                final JsonObject jsonObject = GSON.fromJson(reader, JsonObject.class);
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
    public static void checkForClass(final @NonNull String className)
            throws IOException, ClassNotFoundException {
        if (checkForShadedASM()) {
            RelocatedMixinUtil.checkForClass(className);
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
            final @NonNull String className, final @NonNull String methodName)
            throws IOException, ClassNotFoundException, NoSuchMethodException {
        if (checkForShadedASM()) {
            RelocatedMixinUtil.checkForMethod(className, methodName);
            return;
        }
        MixinService.getService().getBytecodeProvider().getClassNode(className).methods.stream()
                .filter(method -> method.name.equals(methodName))
                .findFirst()
                .orElseThrow(() -> new NoSuchMethodException("Method not found"));
    }

    public static boolean shadedASM = false;

    /** Some mixin runtimes use a shaded copy of ASM */
    public static boolean checkForShadedASM() {
        if (shadedASM) return true;
        try {
            // Check for:
            // org.spongepowered.asm.lib.tree.ClassNode
            // org.spongepowered.asm.service.IClassBytecodeProvider.getClassNode(java.lang.String)
            final Class<?> iType =
                    Class.forName("org.spongepowered.asm.service.IClassBytecodeProvider");
            final Class<?> rType = Class.forName("org.spongepowered.asm.lib.tree.ClassNode");
            shadedASM = iType.getMethod("getClassNode", String.class).getReturnType().equals(rType);
        } catch (final ClassNotFoundException | NoSuchMethodException e) {
            shadedASM = false;
        }
        return shadedASM;
    }
}
