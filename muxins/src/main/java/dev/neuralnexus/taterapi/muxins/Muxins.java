/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.muxins;

import static dev.neuralnexus.taterapi.util.TextUtil.ansiParser;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.muxins.mixin.MixinHacks;
import dev.neuralnexus.taterapi.muxins.mixin.MuxinExtension;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.service.MixinService;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** Utility class for users that what to use muxins allong with their own mixin plugin */
public final class Muxins {
    public static final Logger logger = Logger.create("muxins");

    private Muxins() {}

    private static final Set<String> initializedMixinPackages = new HashSet<>();

    public static void bootstrap(String mixinPackage, boolean verbose) {
        if (initializedMixinPackages.contains(mixinPackage)) return;

        initializedMixinPackages.add(mixinPackage);
        MixinHacks.registerMixinExtension(new MuxinExtension(mixinPackage, verbose));
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
        for (String disabledMixin : disabledMixins) {
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
            ClassNode classNode =
                    MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName);

            if (classNode.visibleAnnotations != null) {
                result =
                        AnnotationChecker.checkAnnotations(
                                classNode.visibleAnnotations, mixinClassName, verbose);
            }
        } catch (ClassNotFoundException | IOException e) {
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
