/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.util;

import org.spongepowered.asm.service.MixinService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/** Helper/wrapper class to prevent ClassNotFound errors when Mixin is not present. */
public final class MixinServiceUtil {
    /**
     * Returns the Minecraft asString.
     *
     * @return The Minecraft asString.
     */
    public static String mcVersion() throws ClassNotFoundException, IOException {
        // Fine to do since obfuscated situations are covered by Forge
        // Reflect to get SharedConstants.VERSION_STRING
        // If SharedConstants.VERSION_STRING doesn't exist, fall back to the version.json
        final var ref = new Object() {
            String mcVersion = null;
        };
        MixinService.getService()
                .getBytecodeProvider()
                .getClassNode("net.minecraft.SharedConstants")
                .fields
                .stream()
                .filter(field -> field.name.equals("VERSION_STRING"))
                .findFirst().ifPresentOrElse(fieldNode -> ref.mcVersion = (String) fieldNode.value, () -> {
                    try {
                        ref.mcVersion = parseMcJson();
                    } catch (ClassNotFoundException | IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        return ref.mcVersion;
    }

    private static String parseMcJson() throws ClassNotFoundException, IOException {
        Class<?> sharedConstants = MixinService.getService().getClassProvider().findClass("net.minecraft.SharedConstants");

        // Parse the json included in modern versions of Minecraft
        // SharedConstants is going to get sacrificed, but no one should have a Mixin in that anyway
        try (InputStream is = sharedConstants
                .getClassLoader()
                .getResourceAsStream("version.json")) {

            if (is == null) {
                throw new IllegalStateException("version.json not found in JAR");
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8)
            );

            // Read second line for version id
            reader.readLine();
            String secondLine = reader.readLine();

            if (secondLine == null) {
                throw new IllegalStateException("version.json is malformed");
            }

            int firstQuote = secondLine.indexOf('"', secondLine.indexOf(':'));
            int secondQuote = secondLine.indexOf('"', firstQuote + 1);

            if (firstQuote == -1 || secondQuote == -1) {
                throw new IllegalStateException("Could not parse version id");
            }

            return secondLine.substring(firstQuote + 1, secondQuote);
        }
    }

    /**
     * Checks for a class
     *
     * @param className The class name
     */
    public static void checkForClass(String className) throws IOException, ClassNotFoundException {
        MixinService.getService().getBytecodeProvider().getClassNode(className);
    }

    /**
     * Checks for a method
     *
     * @param className The class name
     * @param methodName The method name
     */
    public static void checkForMethod(String className, String methodName)
            throws IOException, ClassNotFoundException, NoSuchMethodException {
        MixinService.getService().getBytecodeProvider().getClassNode(className).methods.stream()
                .filter(method -> method.name.equals(methodName))
                .findFirst()
                .orElseThrow(() -> new NoSuchMethodException("Method not found"));
    }
}
