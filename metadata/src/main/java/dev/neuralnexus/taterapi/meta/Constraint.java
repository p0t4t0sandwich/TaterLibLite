/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Dependency;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a set of constraints that can be evaluated against the current environment.
 * Constraints include dependencies, mappings, platform, side, and Minecraft version.
 */
public record Constraint(
        Collection<String> deps,
        Collection<String> notDeps,
        Mappings mappings,
        Collection<Platform> platform,
        Collection<Platform> notPlatform,
        Collection<Side> side,
        Collection<MinecraftVersion> version,
        MinecraftVersion min,
        MinecraftVersion max,
        Collection<MinecraftVersion> notVersion,
        MinecraftVersion notMin,
        MinecraftVersion notMax) {

    /**
     * Evaluates the constraint against the current environment.
     *
     * @return true if the constraint is satisfied, false otherwise.
     */
    public boolean result() {
        return Evaluator.evaluate(this);
    }

    /**
     * Creates a Constraint instance from an {@link AConstraint} annotation.
     *
     * @param constraint the {@link AConstraint} annotation
     * @return a {@link Constraint} instance
     */
    public static Constraint from(final AConstraint constraint) {
        return builder()
                .deps(Stream.of(constraint.deps()).map(Dependency::value).toList())
                .deps(
                        Stream.of(constraint.deps())
                                .map(Dependency::aliases)
                                .flatMap(Stream::of)
                                .collect(Collectors.toSet()))
                .notDeps(Stream.of(constraint.notDeps()).map(Dependency::value).toList())
                .notDeps(
                        Stream.of(constraint.notDeps())
                                .map(Dependency::aliases)
                                .flatMap(Stream::of)
                                .collect(Collectors.toSet()))
                .mappings(constraint.mappings())
                .platform(
                        Stream.of(constraint.platform())
                                .map(dev.neuralnexus.taterapi.meta.enums.Platform::ref)
                                .toList())
                .notPlatform(
                        Stream.of(constraint.notPlatform())
                                .map(dev.neuralnexus.taterapi.meta.enums.Platform::ref)
                                .toList())
                .side(Stream.of(constraint.side()).toList())
                .version(
                        Stream.of(constraint.version().value())
                                .map(dev.neuralnexus.taterapi.meta.enums.MinecraftVersion::ref)
                                .toList())
                .min(constraint.version().min().ref())
                .max(constraint.version().max().ref())
                .notVersion(
                        Stream.of(constraint.notVersion().value())
                                .map(dev.neuralnexus.taterapi.meta.enums.MinecraftVersion::ref)
                                .toList())
                .notMin(constraint.notVersion().min().ref())
                .notMax(constraint.notVersion().max().ref())
                .build();
    }

    @Override
    public @NonNull String toString() {
        return "Constraint{"
                + "deps="
                + deps
                + ", notDeps="
                + notDeps
                + ", mappings="
                + mappings
                + ", platform="
                + platform
                + ", notPlatform="
                + notPlatform
                + ", side="
                + side
                + ", version="
                + version
                + ", min="
                + min
                + ", max="
                + max
                + ", notVersion="
                + notVersion
                + ", notMin="
                + notMin
                + ", notMax="
                + notMax
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                deps,
                notDeps,
                mappings,
                platform,
                notPlatform,
                side,
                version,
                min,
                max,
                notVersion,
                notMin,
                notMax);
    }

    /** Creates a new {@link Builder} instance for constructing a {@link Constraint}. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder class for constructing {@link Constraint} instances. */
    public static final class Builder {
        private final Set<String> deps = new HashSet<>();
        private final Set<String> notDeps = new HashSet<>();
        private Mappings mappings = Mappings.NONE;
        private final Set<Platform> platform = new HashSet<>();
        private final Set<Platform> notPlatform = new HashSet<>();
        private final Set<Side> side = new HashSet<>();
        private final Set<MinecraftVersion> version = new HashSet<>();
        private MinecraftVersion min = MinecraftVersions.UNKNOWN;
        private MinecraftVersion max = MinecraftVersions.UNKNOWN;
        private final Set<MinecraftVersion> notVersion = new HashSet<>();
        private MinecraftVersion notMin = MinecraftVersions.UNKNOWN;
        private MinecraftVersion notMax = MinecraftVersions.UNKNOWN;

        private Builder() {}

        /**
         * Adds dependencies that must be present for the constraint to be satisfied.
         *
         * @param deps a collection of dependency identifiers
         * @return the current {@link Builder} instance
         */
        public Builder deps(Collection<String> deps) {
            this.deps.addAll(deps);
            return this;
        }

        /**
         * Adds dependencies that must be present for the constraint to be satisfied.
         *
         * @param deps an array of dependency identifiers
         * @return the current {@link Builder} instance
         */
        public Builder deps(String... deps) {
            return this.deps(List.of(deps));
        }

        /**
         * Adds dependencies that must NOT be present for the constraint to be satisfied.
         *
         * @param notDeps a collection of dependency identifiers
         * @return the current {@link Builder} instance
         */
        public Builder notDeps(Collection<String> notDeps) {
            this.notDeps.addAll(notDeps);
            return this;
        }

        /**
         * Adds dependencies that must NOT be present for the constraint to be satisfied.
         *
         * @param notDeps an array of dependency identifiers
         * @return the current {@link Builder} instance
         */
        public Builder notDeps(String... notDeps) {
            return this.notDeps(List.of(notDeps));
        }

        /**
         * Sets the mappings required for the constraint to be satisfied.
         *
         * @param mappings the required {@link Mappings}
         * @return the current {@link Builder} instance
         */
        public Builder mappings(Mappings mappings) {
            this.mappings = mappings;
            return this;
        }

        /**
         * Adds platforms that must be present for the constraint to be satisfied.
         *
         * @param platform a collection of {@link Platform}
         * @return the current {@link Builder} instance
         */
        public Builder platform(Collection<Platform> platform) {
            this.platform.addAll(platform);
            return this;
        }

        /**
         * Adds platforms that must be present for the constraint to be satisfied.
         *
         * @param platform an array of {@link Platform}
         * @return the current {@link Builder} instance
         */
        public Builder platform(Platform... platform) {
            return this.platform(List.of(platform));
        }

        /**
         * Adds platforms that must NOT be present for the constraint to be satisfied.
         *
         * @param notPlatform a collection of {@link Platform}
         * @return the current {@link Builder} instance
         */
        public Builder notPlatform(Collection<Platform> notPlatform) {
            this.notPlatform.addAll(notPlatform);
            return this;
        }

        /**
         * Adds platforms that must NOT be present for the constraint to be satisfied.
         *
         * @param notPlatform an array of {@link Platform}
         * @return the current {@link Builder} instance
         */
        public Builder notPlatform(Platform... notPlatform) {
            return this.notPlatform(List.of(notPlatform));
        }

        /**
         * Adds sides that the constraint applies to.
         *
         * @param side a collection of {@link Side}
         * @return the current {@link Builder} instance
         */
        public Builder side(Collection<Side> side) {
            this.side.addAll(side);
            return this;
        }

        /**
         * Adds sides that the constraint applies to.
         *
         * @param side an array of {@link Side}
         * @return the current {@link Builder} instance
         */
        public Builder side(Side... side) {
            return this.side(List.of(side));
        }

        /**
         * Adds Minecraft versions that the constraint applies to.
         *
         * @param version a collection of {@link MinecraftVersion}
         * @return the current {@link Builder} instance
         */
        public Builder version(Collection<MinecraftVersion> version) {
            this.version.addAll(version);
            return this;
        }

        /**
         * Adds Minecraft versions that the constraint applies to.
         *
         * @param version an array of {@link MinecraftVersion}
         * @return the current {@link Builder} instance
         */
        public Builder version(MinecraftVersion... version) {
            return this.version(List.of(version));
        }

        /**
         * Sets the minimum Minecraft version for the constraint to be satisfied.
         *
         * @param min the minimum {@link MinecraftVersion}
         * @return the current {@link Builder} instance
         */
        public Builder min(MinecraftVersion min) {
            this.min = min;
            return this;
        }

        /**
         * Sets the maximum Minecraft version for the constraint to be satisfied.
         *
         * @param max the maximum {@link MinecraftVersion}
         * @return the current {@link Builder} instance
         */
        public Builder max(MinecraftVersion max) {
            this.max = max;
            return this;
        }

        /**
         * Adds Minecraft versions that must NOT be present for the constraint to be satisfied.
         *
         * @param notVersion a collection of {@link MinecraftVersion}
         * @return the current {@link Builder} instance
         */
        public Builder notVersion(Collection<MinecraftVersion> notVersion) {
            this.notVersion.addAll(notVersion);
            return this;
        }

        /**
         * Adds Minecraft versions that must NOT be present for the constraint to be satisfied.
         *
         * @param notVersion an array of {@link MinecraftVersion}
         * @return the current {@link Builder} instance
         */
        public Builder notVersion(MinecraftVersion... notVersion) {
            return this.notVersion(List.of(notVersion));
        }

        /**
         * Sets the minimum Minecraft version that must NOT be present for the constraint to be
         * satisfied.
         *
         * @param notMin the minimum {@link MinecraftVersion} that must NOT be present
         * @return the current {@link Builder} instance
         */
        public Builder notMin(MinecraftVersion notMin) {
            this.notMin = notMin;
            return this;
        }

        public Builder notMax(MinecraftVersion notMax) {
            this.notMax = notMax;
            return this;
        }

        /**
         * Builds the {@link Constraint} instance.
         *
         * @return a new {@link Constraint} instance
         */
        public Constraint build() {
            return new Constraint(
                    Collections.unmodifiableCollection(deps),
                    Collections.unmodifiableCollection(notDeps),
                    mappings,
                    Collections.unmodifiableCollection(platform),
                    Collections.unmodifiableCollection(notPlatform),
                    Collections.unmodifiableCollection(side),
                    Collections.unmodifiableCollection(version),
                    min,
                    max,
                    Collections.unmodifiableCollection(notVersion),
                    notMin,
                    notMax);
        }
    }

    /**
     * Evaluator class for evaluating {@link Constraint} instances against the current environment.
     */
    public static final class Evaluator {
        private static final Map<Constraint, Boolean> CACHE = new WeakHashMap<>();
        private static final MetaAPI META = MetaAPI.instance();
        private static final Mappings MAPPINGS = META.mappings();
        private static final MinecraftVersion version = META.version();

        public static boolean DEBUG = false;
        private static final Logger logger = Logger.create("taterliblite-meta-constraint");

        private Evaluator() {}

        /**
         * Evaluates the dependency constraints.
         *
         * @param constraint the {@link Constraint} to evaluate
         * @return true if the dependency constraints are satisfied, false otherwise
         */
        public static boolean evalDeps(Constraint constraint) {
            if (!constraint.deps.isEmpty()
                    && !META.isModLoaded(constraint.deps.toArray(String[]::new))) {
                if (DEBUG) {
                    logger.debug(
                            "Dependency constraint failed. Required deps not found: "
                                    + constraint.deps);
                }
                return false;
            }
            if (!constraint.notDeps.isEmpty()
                    && META.isModLoaded(constraint.notDeps.toArray(String[]::new))) {
                if (DEBUG) {
                    logger.debug(
                            "Dependency constraint failed. Forbidden deps found: "
                                    + constraint.notDeps);
                }
                return false;
            }
            if (DEBUG) {
                logger.debug("Dependency constraint passed.");
            }
            return true;
        }

        /**
         * Evaluates the mappings constraints.
         *
         * @param constraint the {@link Constraint} to evaluate
         * @return true if the mappings constraints are satisfied, false otherwise
         */
        public static boolean evalMappings(Constraint constraint) {
            if (constraint.mappings == Mappings.NONE) {
                return true;
            }
            if (MAPPINGS != constraint.mappings) {
                if (DEBUG) {
                    logger.debug(
                            "Mappings constraint failed. Required: "
                                    + constraint.mappings
                                    + ", Found: "
                                    + MAPPINGS);
                }
                return false;
            }
            if (DEBUG) {
                logger.debug(
                        "Mappings constraint passed. Expected: "
                                + constraint.mappings
                                + ", Found: "
                                + MAPPINGS);
            }
            return true;
        }

        /**
         * Evaluates the platform constraints.
         *
         * @param constraint the {@link Constraint} to evaluate
         * @return true if the platform constraints are satisfied, false otherwise
         */
        public static boolean evalPlatform(Constraint constraint) {
            if (!constraint.platform.isEmpty()
                    && !META.isPlatformPresent(constraint.platform.toArray(Platform[]::new))) {
                if (DEBUG) {
                    logger.debug(
                            "Platform constraint failed. Required platforms not found: "
                                    + constraint.platform);
                }
                return false;
            }
            if (!constraint.notPlatform.isEmpty()
                    && META.isPlatformPresent(constraint.platform.toArray(Platform[]::new))) {
                if (DEBUG) {
                    logger.debug(
                            "Platform constraint failed. Forbidden platforms found: "
                                    + constraint.notPlatform);
                }
                return false;
            }
            if (DEBUG) {
                logger.debug("Platform constraint passed. Expected: " + constraint.platform);
            }
            return true;
        }

        /**
         * Evaluates the side constraints.
         *
         * @param constraint the {@link Constraint} to evaluate
         * @return true if the side constraints are satisfied, false otherwise
         */
        public static boolean evalSide(Constraint constraint) {
            if (constraint.side.isEmpty()) {
                return true;
            }
            if (!constraint.side.contains(META.side())) {
                if (DEBUG) {
                    logger.debug(
                            "Side constraint failed. Required sides not found: " + constraint.side);
                }
                return false;
            }
            if (DEBUG) {
                logger.debug(
                        "Side constraint passed. Expected: "
                                + constraint.side
                                + ", Found: "
                                + META.side());
            }
            return true;
        }

        /**
         * Evaluates the Minecraft version constraints.
         *
         * @param constraint the {@link Constraint} to evaluate
         * @return true if the version constraints are satisfied, false otherwise
         */
        public static boolean evalVersion(Constraint constraint) {
            if (!constraint.version.isEmpty() && !constraint.version.contains(version)) {
                if (DEBUG) {
                    logger.debug(
                            "Version constraint failed. Required versions not found: "
                                    + constraint.version
                                    + ", Found: "
                                    + version);
                }
                return false;
            }
            if (constraint.min != MinecraftVersions.UNKNOWN
                    && constraint.max != MinecraftVersions.UNKNOWN
                    && !version.isInRange(constraint.min, constraint.max)) {
                if (DEBUG) {
                    logger.debug(
                            "Version constraint failed. Required range not satisfied: "
                                    + constraint.min
                                    + " - "
                                    + constraint.max
                                    + ", Found: "
                                    + version);
                }
                return false;
            }
            if (!constraint.notVersion.isEmpty() && constraint.notVersion.contains(version)) {
                if (DEBUG) {
                    logger.debug(
                            "Version constraint failed. Forbidden versions found: "
                                    + constraint.notVersion
                                    + ", Found: "
                                    + version);
                }
                return false;
            }
            if (constraint.notMin != MinecraftVersions.UNKNOWN
                    && constraint.notMax != MinecraftVersions.UNKNOWN
                    && version.isInRange(constraint.notMin, constraint.notMax)) {
                if (DEBUG) {
                    logger.debug(
                            "Version constraint failed. Forbidden range satisfied: "
                                    + constraint.notMin
                                    + " - "
                                    + constraint.notMax
                                    + ", Found: "
                                    + version);
                }
                return false;
            }
            if (DEBUG) {
                logger.debug(
                        "Version constraint passed. Expected: "
                                + constraint.version
                                + ", "
                                + constraint.min
                                + "-"
                                + constraint.max
                                + ", Found: "
                                + version);
            }
            return true;
        }

        /**
         * Evaluates the given {@link Constraint} against the current environment. Results are
         * cached for performance.
         *
         * @param constraint the {@link Constraint} to evaluate
         * @return true if the constraint is satisfied, false otherwise
         */
        public static boolean evaluate(Constraint constraint) {
            if (CACHE.containsKey(constraint)) {
                return CACHE.get(constraint);
            }
            if (DEBUG) {
                logger.debug("Evaluating constraint: " + constraint);
            }
            boolean result =
                    evalDeps(constraint)
                            && evalMappings(constraint)
                            && evalPlatform(constraint)
                            && evalSide(constraint)
                            && evalVersion(constraint);
            CACHE.put(constraint, result);
            return result;
        }
    }
}
