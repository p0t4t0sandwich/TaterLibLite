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
import dev.neuralnexus.taterapi.meta.anno.Versions;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import org.jspecify.annotations.NonNull;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.List;

/** Checks annotations on mixins */
public final class AnnotationChecker {
    private static final String CONSTRAINT_DESC = Type.getDescriptor(AConstraint.class);
    private static final String CONSTRAINTS_DESC = Type.getDescriptor(AConstraints.class);

    private AnnotationChecker() {}

    public static boolean isConstraintAnnotationNode(final @NonNull AnnotationNode node) {
        return CONSTRAINT_DESC.equals(node.desc) || CONSTRAINTS_DESC.equals(node.desc);
    }

    /**
     * Checks a constraint annotation node
     *
     * @param annotation The annotation node
     * @param verbose If the method should log the result
     * @return If the constraint is met
     */
    public static boolean checkAnnotation(
            final @NonNull AnnotationNode annotation, final boolean verbose) {
        final boolean debug = Constraint.Evaluator.DEBUG;
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
            final List<AnnotationNode> and = getValue(annotation, "value", true);
            final List<AnnotationNode> or = getValue(annotation, "or", true);
            final Constraints constraints =
                    Constraints.builder()
                            .and(
                                    and.stream()
                                            .map(AnnotationChecker::toConstraint)
                                            .toArray(Constraint[]::new))
                            .or(
                                    or.stream()
                                            .map(AnnotationChecker::toConstraint)
                                            .toArray(Constraint[]::new))
                            .build();
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
            final List<@NonNull AnnotationNode> annotations,
            final @NonNull String mixinClassName,
            final boolean verbose) {
        for (final AnnotationNode node : annotations) {
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
            final @NonNull String mixinClassName,
            final @NonNull AnnotationNode annotation,
            final boolean verbose) {
        final boolean debug = Constraint.Evaluator.DEBUG;
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
            final @NonNull String mixinClassName,
            final @NonNull AnnotationNode annotation,
            final boolean verbose) {
        final boolean debug = Constraint.Evaluator.DEBUG;
        Constraint.Evaluator.DEBUG = verbose;

        final List<AnnotationNode> and = getValue(annotation, "value", true);
        final List<AnnotationNode> or = getValue(annotation, "or", true);
        final Constraints constraints =
                Constraints.builder()
                        .and(
                                and.stream()
                                        .map(AnnotationChecker::toConstraint)
                                        .toArray(Constraint[]::new))
                        .or(
                                or.stream()
                                        .map(AnnotationChecker::toConstraint)
                                        .toArray(Constraint[]::new))
                        .build();
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

    private static Constraint toConstraint(final @NonNull AnnotationNode annotation) {
        // deps
        final List<AnnotationNode> depNodes = getValue(annotation, "deps", true);
        final List<String> deps =
                depNodes.stream().<String>map(d -> getValue(d, "value", String.class)).toList();
        final List<String> depsAliases =
                depNodes.stream()
                        .flatMap(
                                d -> {
                                    List<String> aliases = getValue(d, "aliases", true);
                                    return aliases != null ? aliases.stream() : null;
                                })
                        .toList();

        final List<Mappings> maps = getValue(annotation, "mappings", true, Mappings.class);
        final List<Platform> plats = getValue(annotation, "platform", true, Platform.class);
        final List<Side> sides = getValue(annotation, "side", true, Side.class);

        // version
        final AnnotationNode versionNode = getValue(annotation, "version", AnnotationNode.class);
        final List<MinecraftVersion> versions =
                getValue(versionNode, "value", true, MinecraftVersion.class);
        final boolean minInclusive = getValue(versionNode, "minInclusive", Versions.class);
        final MinecraftVersion min =
                getValue(versionNode, "min", MinecraftVersion.class, MinecraftVersion.UNKNOWN);
        final boolean maxInclusive = getValue(versionNode, "maxInclusive", Versions.class);
        final MinecraftVersion max =
                getValue(versionNode, "max", MinecraftVersion.class, MinecraftVersion.UNKNOWN);

        // invert
        final boolean invert = getValue(annotation, "invert", AConstraint.class);

        return Constraint.builder()
                .deps(deps)
                .deps(depsAliases)
                .mappings(maps)
                .platform(plats.toArray(Platform[]::new))
                .side(sides)
                .version(versions.toArray(MinecraftVersion[]::new))
                .minInclusive(minInclusive)
                .min(min)
                .maxInclusive(maxInclusive)
                .max(max)
                .invert(invert)
                .build();
    }
}
