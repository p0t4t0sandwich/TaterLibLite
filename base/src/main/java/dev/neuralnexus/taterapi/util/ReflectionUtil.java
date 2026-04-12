/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.util;

import org.jspecify.annotations.NonNull;

import java.io.IOException;

/** Utility class for reflection operations */
public final class ReflectionUtil {
    private static boolean isMixinPresent = false;

    // ModernMixin's ByteCodeProvider always gives a ClassNode even for classes that don't exist
    // meaning the usual "soft" reflection checks are no longer valid
    private static boolean isModernMixinsModPresent = false;

    static {
        try {
            Class.forName("org.redlance.dima_dencep.mods.modernmixins.ModernMixinsMod");
            isModernMixinsModPresent = true;
        } catch (final ClassNotFoundException ignored) {
        }

        try {
            Class.forName("org.spongepowered.asm.service.MixinService");
            isMixinPresent = true;
        } catch (final ClassNotFoundException ignored) {
        }
    }

    /**
     * Check if a class exists
     *
     * @param className The class(es) to check
     * @return True if one of the classes exists
     */
    public static boolean checkForClass(final @NonNull String... className) {
        for (final String s : className) {
            try {
                if (isMixinPresent && !isModernMixinsModPresent) {
                    MixinServiceUtil.checkForClass(s);
                } else {
                    Class.forName(s);
                }
                return true;
            } catch (final ClassNotFoundException | IOException ignored) {
            }
        }
        return false;
    }

    /**
     * Check if a method exists.
     *
     * @param className The class to check.
     * @param methodName The method to check.
     * @return Whether the method exists.
     */
    public static boolean checkForMethod(
            final @NonNull String className, final @NonNull String methodName) {
        try {
            if (isMixinPresent && !isModernMixinsModPresent) {
                MixinServiceUtil.checkForMethod(className, methodName);
            } else {
                Class.forName(className).getDeclaredMethod(methodName);
            }
            return true;
        } catch (final ClassNotFoundException | NoSuchMethodException | IOException e) {
            return false;
        }
    }
}
