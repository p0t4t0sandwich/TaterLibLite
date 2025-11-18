/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.muxins;

import static dev.neuralnexus.taterapi.muxins.Muxins.logger;
import static dev.neuralnexus.taterapi.util.TextUtil.ansiParser;

import static org.spongepowered.asm.util.Annotations.getValue;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.Constraints;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.AConstraints;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.List;
import java.util.stream.Collectors;

/** Checks annotations on mixins */
public final class AnnotationChecker {
    private static final String CONSTRAINT_DESC = Type.getDescriptor(AConstraint.class);
    private static final String CONSTRAINTS_DESC = Type.getDescriptor(AConstraints.class);

    private AnnotationChecker() {}

    public static boolean isConstraintAnnotationNode(AnnotationNode node) {
        return CONSTRAINT_DESC.equals(node.desc) || CONSTRAINTS_DESC.equals(node.desc);
    }

    /**
     * Checks a constraint annotation node
     *
     * @param annotation The annotation node
     * @param verbose If the method should log the result
     * @return If the constraint is met
     */
    public static boolean checkAnnotation(AnnotationNode annotation, boolean verbose) {
        boolean debug = Constraint.Evaluator.DEBUG;
        Constraint.Evaluator.DEBUG = verbose;

        if (CONSTRAINT_DESC.equals(annotation.desc)) {
            if (!toConstraint(annotation).result()) {
                if (verbose) {
                    logger.info(ansiParser("§4Skipping mixin §9 constraint not met."));
                }
                Constraint.Evaluator.DEBUG = debug;
                return false;
            }
        } else if (CONSTRAINTS_DESC.equals(annotation.desc)) {
            List<AnnotationNode> constraintNodes = getValue(annotation, "value", true);
            Constraints constraints =
                    new Constraints(
                            constraintNodes.stream()
                                    .map(AnnotationChecker::toConstraint)
                                    .collect(Collectors.toUnmodifiableSet()));
            if (!constraints.result()) {
                if (verbose) {
                    logger.info(ansiParser("§4Skipping mixin §9 constraints not met."));
                }
                Constraint.Evaluator.DEBUG = debug;
                return false;
            }
        }
        Constraint.Evaluator.DEBUG = debug;
        return true;
    }

    /**
     * Checks the annotations on a mixin
     *
     * @param annotations The annotations to check
     * @param mixinClassName The name of the mixin class
     * @param verbose If the method should log the result
     * @return If the mixin should be applied
     */
    public static boolean checkAnnotations(
            List<AnnotationNode> annotations, String mixinClassName, boolean verbose) {
        for (AnnotationNode node : annotations) {
            if (CONSTRAINT_DESC.equals(node.desc)) {
                if (!checkConstraint(mixinClassName, node, verbose)) {
                    return false;
                }
            } else if (CONSTRAINTS_DESC.equals(node.desc)) {
                if (!checkConstraints(mixinClassName, node, verbose)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks a constraint required by a mixin
     *
     * @param mixinClassName The name of the mixin class
     * @param annotation The annotation to check
     * @param verbose If the method should log the result
     * @return If the mixin should be applied
     */
    public static boolean checkConstraint(
            String mixinClassName, AnnotationNode annotation, boolean verbose) {
        boolean debug = Constraint.Evaluator.DEBUG;
        Constraint.Evaluator.DEBUG = verbose;

        if (!toConstraint(annotation).result()) {
            if (verbose) {
                logger.info(
                        ansiParser(
                                "§4Skipping mixin §9" + mixinClassName + " §4constraint not met."));
            }
            Constraint.Evaluator.DEBUG = debug;
            return false;
        }
        Constraint.Evaluator.DEBUG = debug;
        return true;
    }

    /**
     * Checks multiple constraints required by a mixin
     *
     * @param mixinClassName The name of the mixin class
     * @param annotation The annotation to check
     * @param verbose If the method should log the result
     * @return If the mixin should be applied
     */
    public static boolean checkConstraints(
            String mixinClassName, AnnotationNode annotation, boolean verbose) {
        boolean debug = Constraint.Evaluator.DEBUG;
        Constraint.Evaluator.DEBUG = verbose;

        List<AnnotationNode> constraintNodes = getValue(annotation, "value", true);
        Constraints constraints =
                new Constraints(
                        constraintNodes.stream()
                                .map(AnnotationChecker::toConstraint)
                                .collect(Collectors.toUnmodifiableSet()));
        if (!constraints.result()) {
            if (verbose) {
                logger.info(
                        ansiParser(
                                "§4Skipping mixin §9"
                                        + mixinClassName
                                        + " §4constraints not met."));
            }
            Constraint.Evaluator.DEBUG = debug;
            return false;
        }
        Constraint.Evaluator.DEBUG = debug;
        return true;
    }

    private static Constraint toConstraint(AnnotationNode annotation) {
        // deps
        List<AnnotationNode> depNodes = getValue(annotation, "deps", true);
        List<String> deps =
                depNodes.stream().<String>map(d -> getValue(d, "value", String.class)).toList();
        List<String> depsAliases =
                depNodes.stream()
                        .flatMap(
                                d -> {
                                    List<String> aliases = getValue(d, "aliases", true);
                                    return aliases != null ? aliases.stream() : null;
                                })
                        .toList();

        // notDeps
        List<AnnotationNode> notDepNodes = getValue(annotation, "notDeps", true);
        List<String> notDeps =
                notDepNodes.stream().<String>map(d -> getValue(d, "value", String.class)).toList();
        List<String> notDepsAliases =
                notDepNodes.stream()
                        .flatMap(
                                d -> {
                                    List<String> aliases = getValue(d, "aliases", true);
                                    return aliases != null ? aliases.stream() : null;
                                })
                        .toList();

        Mappings maps = getValue(annotation, "mappings", Mappings.class, Mappings.NONE);
        List<Platform> plats = getValue(annotation, "platform", true, Platform.class);
        List<Platform> notPlats = getValue(annotation, "notPlatform", true, Platform.class);
        List<Side> sides = getValue(annotation, "side", true, Side.class);

        // version
        AnnotationNode versionNode = getValue(annotation, "version", AnnotationNode.class);
        List<MinecraftVersion> versions =
                getValue(versionNode, "value", true, MinecraftVersion.class);
        MinecraftVersion min =
                getValue(versionNode, "min", MinecraftVersion.class, MinecraftVersion.UNKNOWN);
        MinecraftVersion max =
                getValue(versionNode, "max", MinecraftVersion.class, MinecraftVersion.UNKNOWN);

        // notVersion
        AnnotationNode notVersionNode = getValue(annotation, "notVersion", AnnotationNode.class);
        List<MinecraftVersion> notVersions =
                getValue(notVersionNode, "value", true, MinecraftVersion.class);
        MinecraftVersion notMin =
                getValue(notVersionNode, "min", MinecraftVersion.class, MinecraftVersion.UNKNOWN);
        MinecraftVersion notMax =
                getValue(notVersionNode, "max", MinecraftVersion.class, MinecraftVersion.UNKNOWN);

        return Constraint.builder()
                .deps(deps)
                .deps(depsAliases)
                .notDeps(notDeps)
                .notDeps(notDepsAliases)
                .mappings(maps)
                .platform(plats.stream().map(Platform::ref).toList())
                .notPlatform(notPlats.stream().map(Platform::ref).toList())
                .side(sides)
                .version(versions.stream().map(MinecraftVersion::ref).toList())
                .min(min.ref())
                .max(max.ref())
                .notVersion(notVersions.stream().map(MinecraftVersion::ref).toList())
                .notMin(notMin.ref())
                .notMax(notMax.ref())
                .build();
    }
}
