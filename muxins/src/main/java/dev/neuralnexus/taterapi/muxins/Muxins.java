/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.muxins;

import static dev.neuralnexus.taterapi.util.TextUtil.ansiParser;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.muxins.mixin.MixinHacks;
import dev.neuralnexus.taterapi.muxins.mixin.MuxinExtension;
import dev.neuralnexus.taterapi.util.MixinServiceUtil;

import org.spongepowered.asm.mixin.transformer.ext.IExtension;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** Utility class for users that what to use muxins allong with their own mixin plugin */
public final class Muxins {
    public static final Logger logger = Logger.create("muxins");

    private static boolean mixinTransformerPresent = true;
    private static MethodHandle registerMixinExtensionHandle;
    private static MethodHandle muxinExtensionConstructorHandle;
    private static MethodHandle checkAnnotationsHandle;

    static {
        //noinspection ConstantValue
        if (!Muxins.class.getName().contains(".muxins.shaded.")) {
            try {
                Class.forName("org.spongepowered.asm.mixin.transformer.IMixinTransformer");
            } catch (final ClassNotFoundException e) {
                mixinTransformerPresent = false;
                // TODO: Learn alternate transformation methods
                logger.warn(
                        "Mixin transformer not found, field/method annotations will not function as expected");
            }

            if (MixinServiceUtil.checkForShadedASM()) {
                try {
                    final Class<?> mixinHacks =
                            Class.forName(
                                    MixinHacks.class
                                            .getName()
                                            .replace(".muxins.", ".muxins.shaded."));
                    final Method registerMethod =
                            mixinHacks.getDeclaredMethod(
                                    "registerMixinExtension", IExtension.class);
                    registerMixinExtensionHandle =
                            MethodHandles.publicLookup().unreflect(registerMethod);

                    final Class<?> muxinExtension =
                            Class.forName(
                                    MuxinExtension.class
                                            .getName()
                                            .replace(".muxins.", ".muxins.shaded."));
                    final Constructor<?> constructor =
                            muxinExtension.getConstructor(String.class, boolean.class);
                    muxinExtensionConstructorHandle =
                            MethodHandles.publicLookup().unreflectConstructor(constructor);

                    final Class<?> annotationChecker =
                            Class.forName(
                                    AnnotationChecker.class
                                            .getName()
                                            .replace(".muxins.", ".muxins.shaded."));
                    final Method checkAnnotationsMethod =
                            annotationChecker.getDeclaredMethod(
                                    "checkAnnotations", String.class, boolean.class);
                    checkAnnotationsHandle =
                            MethodHandles.publicLookup().unreflect(checkAnnotationsMethod);
                } catch (final ClassNotFoundException
                        | IllegalAccessException
                        | NoSuchMethodException e) {
                    logger.error("Failed to load shaded muxin classes", e);
                }
            }
        }
    }

    private Muxins() {}

    private static final Set<String> initializedMixinPackages = new HashSet<>();

    public static void bootstrap(String mixinPackage, boolean verbose) {
        if (!mixinTransformerPresent) return;
        if (initializedMixinPackages.contains(mixinPackage)) return;

        initializedMixinPackages.add(mixinPackage);
        if (MixinServiceUtil.checkForShadedASM()) {
            try {
                Object muxinExtension =
                        muxinExtensionConstructorHandle.invoke(mixinPackage, verbose);
                registerMixinExtensionHandle.invoke(muxinExtension);
            } catch (final Throwable e) {
                logger.error("Failed to register mixin extension", e);
            }
        } else {
            MixinHacks.registerMixinExtension(new MuxinExtension(mixinPackage, verbose));
        }
    }

    public static void bootstrap(String mixinPackage) {
        bootstrap(mixinPackage, false);
    }

    /**
     * Checks if a mixin should be applied based on its annotations
     *
     * @param mixinClassName The name of the mixin class
     * @param disabledMixins A list of disabled mixins
     * @param verbose If the method should log the result
     * @return If the mixin should be applied
     */
    public static boolean shouldApplyMixin(
            String mixinClassName, Collection<String> disabledMixins, boolean verbose) {
        boolean result = true;
        for (final String disabledMixin : disabledMixins) {
            if (mixinClassName.endsWith(disabledMixin)) {
                if (verbose) {
                    logger.info(
                            ansiParser(
                                    "§4Skipping mixin §9"
                                            + mixinClassName
                                            + " §4disabled in config"));
                }
                result = false;
            }
        }
        try {
            if (MixinServiceUtil.checkForShadedASM()) {
                try {
                    result = (boolean) checkAnnotationsHandle.invoke(mixinClassName, verbose);
                } catch (final Throwable e) {
                    logger.error("Failed to check annotations for mixin: " + mixinClassName, e);
                }
            } else {
                result = AnnotationChecker.checkAnnotations(mixinClassName, verbose);
            }
        } catch (final ClassNotFoundException | IOException e) {
            if (verbose) {
                logger.error("Failed to load mixin class: " + mixinClassName, e);
            }
        }
        if (result && verbose) {
            logger.info(ansiParser("§2Applying mixin §9" + mixinClassName));
        }
        return result;
    }

    /**
     * Checks if a mixin should be applied based on its annotations
     *
     * @param mixinClassName The name of the mixin class
     * @return If the mixin should be applied
     */
    public static boolean shouldApplyMixin(String mixinClassName) {
        return shouldApplyMixin(mixinClassName, Collections.emptyList(), false);
    }

    /**
     * Checks if a mixin should be applied based on its annotations
     *
     * @param mixinClassName The name of the mixin class
     * @param verbose If the method should log the result
     * @return If the mixin should be applied
     */
    public static boolean shouldApplyMixin(String mixinClassName, boolean verbose) {
        return shouldApplyMixin(mixinClassName, Collections.emptyList(), verbose);
    }

    /**
     * Checks if a mixin should be applied based on its annotations
     *
     * @param mixinClassName The name of the mixin class
     * @param disabledMixins A list of disabled mixins
     * @return If the mixin should be applied
     */
    public static boolean shouldApplyMixin(
            String mixinClassName, Collection<String> disabledMixins) {
        return shouldApplyMixin(mixinClassName, disabledMixins, false);
    }
}
