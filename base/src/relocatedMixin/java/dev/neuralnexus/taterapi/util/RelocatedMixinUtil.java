/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.util;

import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;

/**
 * Class to access mixin runtimes that use a relocated copy of ASM. <br>
 * TODO: Find a method to copy MixinServiceUtil before relocating for easier parity
 */
public final class RelocatedMixinUtil {
    /**
     * Checks for a class
     *
     * @param className The class name
     */
    public static void checkForClass(@NonNull final String className)
            throws IOException, ClassNotFoundException {
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
        MixinService.getService().getBytecodeProvider().getClassNode(className).methods.stream()
                .filter(method -> method.name.equals(methodName))
                .findFirst()
                .orElseThrow(() -> new NoSuchMethodException("Method not found"));
    }
}
