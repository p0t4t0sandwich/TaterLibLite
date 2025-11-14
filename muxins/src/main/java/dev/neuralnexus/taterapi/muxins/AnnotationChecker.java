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
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.AConstraints;
import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.enums.Platform;
import dev.neuralnexus.taterapi.muxins.annotations.ReqDependency;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMCVersion;
import dev.neuralnexus.taterapi.muxins.annotations.ReqMappings;
import dev.neuralnexus.taterapi.muxins.annotations.ReqPlatform;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;

import java.util.List;
import java.util.stream.Collectors;

/** Checks annotations on mixins */
public final class AnnotationChecker {
    private static final dev.neuralnexus.taterapi.meta.Platform.Meta meta =
            MetaAPI.instance().meta();
    private static final dev.neuralnexus.taterapi.meta.MinecraftVersion minecraftVersion =
            MetaAPI.instance().version();
    private static final Mappings mappings = MetaAPI.instance().mappings();

    private static final String CONSTRAINT_DESC = Type.getDescriptor(AConstraint.class);
    private static final String CONSTRAINTS_DESC = Type.getDescriptor(AConstraints.class);
    private static final String REQ_DEPENDENCY_DESC = Type.getDescriptor(ReqDependency.class);
    private static final String REQ_MAPPINGS_DESC = Type.getDescriptor(ReqMappings.class);
    private static final String REQ_PLATFORM_DESC = Type.getDescriptor(ReqPlatform.class);
    private static final String REQ_MC_VERSION_DESC = Type.getDescriptor(ReqMCVersion.class);

    private AnnotationChecker() {}

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
            } else if (REQ_DEPENDENCY_DESC.equals(node.desc)) {
                if (!checkReqDependency(mixinClassName, node, verbose)) {
                    return false;
                }
            } else if (REQ_MAPPINGS_DESC.equals(node.desc)) {
                if (!checkReqMappings(mixinClassName, node, verbose)) {
                    return false;
                }
            } else if (REQ_PLATFORM_DESC.equals(node.desc)) {
                if (!checkReqPlatform(mixinClassName, node, verbose)) {
                    return false;
                }
            } else if (REQ_MC_VERSION_DESC.equals(node.desc)) {
                if (!checkReqMCVersion(mixinClassName, node, verbose)) {
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

    /**
     * Checks the dependencies required by a mixin
     *
     * @param mixinClassName The name of the mixin class
     * @param annotation The annotation to check
     * @param verbose If the method should log the result
     * @return If the mixin should be applied
     */
    public static boolean checkReqDependency(
            String mixinClassName, AnnotationNode annotation, boolean verbose) {
        List<String> reqDependency = getValue(annotation, "value", true);
        if (!reqDependency.isEmpty()) {
            for (String dep : reqDependency) {
                if (dep.startsWith("!")) {
                    String dependency = dep.substring(1);
                    if (meta.isModLoaded(dependency)) {
                        if (verbose) {
                            logger.info(
                                    ansiParser(
                                            "§4Skipping mixin §9"
                                                    + mixinClassName
                                                    + " §4conflicts with dependency: §9"
                                                    + dependency));
                        }
                        return false;
                    }
                } else {
                    if (!meta.isModLoaded(dep)) {
                        if (verbose) {
                            logger.info(
                                    ansiParser(
                                            "§4Skipping mixin §9"
                                                    + mixinClassName
                                                    + " §4missing dependency: §9"
                                                    + dep));
                        }
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks the mappings required by a mixin
     *
     * @param mixinClassName The name of the mixin class
     * @param annotation The annotation to check
     * @param verbose If the method should log the result
     * @return If the mixin should be applied
     */
    public static boolean checkReqMappings(
            String mixinClassName, AnnotationNode annotation, boolean verbose) {
        Mappings mixinMappings = getValue(annotation, "value", Mappings.class, Mappings.NONE);
        if (mixinMappings != Mappings.NONE && !mappings.is(mixinMappings)) {
            if (verbose) {
                logger.info(
                        ansiParser(
                                "§4Skipping mixin §9"
                                        + mixinClassName
                                        + " §4mappings not supported"));
            }
            return false;
        }
        return true;
    }

    /**
     * Checks the platform requirements of a mixin
     *
     * @param mixinClassName The name of the mixin class
     * @param annotation The annotation to check
     * @param verbose If the method should log the result
     * @return If the mixin should be applied
     */
    public static boolean checkReqPlatform(
            String mixinClassName, AnnotationNode annotation, boolean verbose) {
        List<Platform> platforms = getValue(annotation, "value", true, Platform.class);
        if (!platforms.isEmpty()) {
            for (Platform plat : platforms) {
                if (!MetaAPI.instance().isPlatformPresent(plat.ref())) {
                    if (verbose) {
                        logger.info(
                                ansiParser(
                                        "§4Skipping mixin §9"
                                                + mixinClassName
                                                + " §4no supported platform detected"));
                    }
                    return false;
                }
            }
        }
        List<Platform> notPlatforms = getValue(annotation, "not", true, Platform.class);
        if (!notPlatforms.isEmpty()) {
            for (Platform plat : notPlatforms) {
                if (MetaAPI.instance().isPlatformPresent(plat.ref())) {
                    if (verbose) {
                        logger.info(
                                ansiParser(
                                        "§4Skipping mixin §9"
                                                + mixinClassName
                                                + " §4platform not supported"));
                    }
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks the Minecraft version requirements of a mixin
     *
     * @param mixinClassName The name of the mixin class
     * @param annotation The annotation to check
     * @param verbose If the method should log the result
     * @return If the mixin should be applied
     */
    public static boolean checkReqMCVersion(
            String mixinClassName, AnnotationNode annotation, boolean verbose) {
        MinecraftVersion min =
                getValue(annotation, "min", MinecraftVersion.class, MinecraftVersion.UNKNOWN);
        MinecraftVersion max =
                getValue(annotation, "max", MinecraftVersion.class, MinecraftVersion.UNKNOWN);
        if (!(min == MinecraftVersion.UNKNOWN && max == MinecraftVersion.UNKNOWN)) {
            if (min != null && !minecraftVersion.isAtLeast(min.ref())) {
                if (verbose) {
                    logger.info(
                            ansiParser(
                                    "§4Skipping mixin §9"
                                            + mixinClassName
                                            + " §4Minecraft version is too old"));
                }
                return false;
            }
            if (max != null && !minecraftVersion.isAtMost(max.ref())) {
                if (verbose) {
                    logger.info(
                            ansiParser(
                                    "§4Skipping mixin §9"
                                            + mixinClassName
                                            + " §4Minecraft version is too recent"));
                }
                return false;
            }
        }

        List<MinecraftVersion> versions =
                getValue(annotation, "value", true, MinecraftVersion.class);
        if (!versions.isEmpty()) {
            for (MinecraftVersion version : versions) {
                if (minecraftVersion.is(version.ref())) {
                    return true;
                }
            }
            if (verbose) {
                logger.info(
                        ansiParser(
                                "§4Skipping mixin §9"
                                        + mixinClassName
                                        + " §4Minecraft version not supported"));
            }
            return false;
        }
        return true;
    }
}
