/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.Dependency;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
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
        Collection<@NonNull String> deps,
        Collection<@NonNull Mappings> mappings,
        Collection<@NonNull Platform> platform,
        Collection<@NonNull Side> side,
        Collection<@NonNull MinecraftVersion> version,
        boolean minInclusive,
        @NonNull MinecraftVersion min,
        boolean maxInclusive,
        @NonNull MinecraftVersion max,
        boolean invert) {

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
    public static @NonNull Constraint from(final @NonNull AConstraint constraint) {
        return builder()
                .deps(
                        Stream.of(constraint.deps())
                                .map(Dependency::value)
                                .collect(Collectors.toUnmodifiableSet()))
                .deps(
                        Stream.of(constraint.deps())
                                .map(Dependency::aliases)
                                .flatMap(Stream::of)
                                .collect(Collectors.toUnmodifiableSet()))
                .mappings(constraint.mappings())
                .platform(constraint.platform())
                .side(constraint.side())
                .version(constraint.version().value())
                .minInclusive(constraint.version().minInclusive())
                .min(constraint.version().min())
                .maxInclusive(constraint.version().maxInclusive())
                .max(constraint.version().max())
                .invert(constraint.invert())
                .build();
    }

    @Override
    public @NonNull String toString() {
        return "Constraint{"
                + "deps="
                + deps
                + ", mappings="
                + mappings
                + ", platform="
                + platform
                + ", side="
                + side
                + ", version="
                + version
                + ", minInclusive="
                + minInclusive
                + ", min="
                + min
                + ", maxInclusive="
                + maxInclusive
                + ", max="
                + max
                + ", invert="
                + invert
                + '}';
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                deps,
                mappings,
                platform,
                side,
                version,
                minInclusive,
                min,
                maxInclusive,
                max,
                invert);
    }

    /** Creates a new {@link Builder} instance for constructing a {@link Constraint}. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder class for constructing {@link Constraint} instances. */
    public static final class Builder {
        private final Set<@NonNull String> deps = new HashSet<>();
        private final Set<@NonNull Mappings> mappings = new HashSet<>();
        private final Set<@NonNull Platform> platform = new HashSet<>();
        private final Set<@NonNull Side> side = new HashSet<>();
        private final Set<@NonNull MinecraftVersion> version = new HashSet<>();
        private boolean minInclusive = true;
        private @NonNull MinecraftVersion min = MinecraftVersions.UNKNOWN;
        private boolean maxInclusive = true;
        private @NonNull MinecraftVersion max = MinecraftVersions.UNKNOWN;
        private boolean invert = false;

        Builder() {}

        /**
         * Adds dependencies that must be present for the constraint to be satisfied.
         *
         * @param deps a collection of dependency identifiers
         * @return the current {@link Builder} instance
         */
        public Builder deps(final Collection<@NonNull String> deps) {
            this.deps.addAll(deps);
            return this;
        }

        /**
         * Adds dependencies that must be present for the constraint to be satisfied.
         *
         * @param deps an array of dependency identifiers
         * @return the current {@link Builder} instance
         */
        public Builder deps(final @NonNull String... deps) {
            return this.deps(Set.of(deps));
        }

        /**
         * Sets the mappings required for the constraint to be satisfied.
         *
         * @param mappings the required {@link Mappings}
         * @return the current {@link Builder} instance
         */
        public Builder mappings(final Collection<@NonNull Mappings> mappings) {
            this.mappings.addAll(mappings);
            return this;
        }

        /**
         * Sets the mappings required for the constraint to be satisfied.
         *
         * @param mappings the required {@link Mappings}
         * @return the current {@link Builder} instance
         */
        public Builder mappings(final @NonNull Mappings... mappings) {
            Collections.addAll(this.mappings, mappings);
            return this;
        }

        /**
         * Adds platforms that must be present for the constraint to be satisfied.
         *
         * @param platform a collection of {@link Platform}
         * @return the current {@link Builder} instance
         */
        public Builder platform(final Collection<@NonNull Platform> platform) {
            this.platform.addAll(platform);
            return this;
        }

        /**
         * Adds platforms that must be present for the constraint to be satisfied.
         *
         * @param platform an array of {@link Platform}
         * @return the current {@link Builder} instance
         */
        public Builder platform(final @NonNull Platform... platform) {
            Collections.addAll(this.platform, platform);
            return this;
        }

        /**
         * Adds platforms that must be present for the constraint to be satisfied.
         *
         * @param platform an array of {@link dev.neuralnexus.taterapi.meta.enums.Platform}
         * @return the current {@link Builder} instance
         */
        public Builder platform(
                final dev.neuralnexus.taterapi.meta.enums.@NonNull Platform... platform) {
            Collections.addAll(
                    this.platform,
                    Stream.of(platform)
                            .map(dev.neuralnexus.taterapi.meta.enums.Platform::ref)
                            .toArray(Platform[]::new));
            return this;
        }

        /**
         * Adds sides that the constraint applies to.
         *
         * @param side a collection of {@link Side}
         * @return the current {@link Builder} instance
         */
        public Builder side(final Collection<@NonNull Side> side) {
            this.side.addAll(side);
            return this;
        }

        /**
         * Adds sides that the constraint applies to.
         *
         * @param side an array of {@link Side}
         * @return the current {@link Builder} instance
         */
        public Builder side(final @NonNull Side... side) {
            Collections.addAll(this.side, side);
            return this;
        }

        /**
         * Adds Minecraft versions that the constraint applies to.
         *
         * @param version a collection of {@link MinecraftVersion}
         * @return the current {@link Builder} instance
         */
        public Builder version(final Collection<@NonNull MinecraftVersion> version) {
            this.version.addAll(version);
            return this;
        }

        /**
         * Adds Minecraft versions that the constraint applies to.
         *
         * @param version an array of {@link MinecraftVersion}
         * @return the current {@link Builder} instance
         */
        public Builder version(final @NonNull MinecraftVersion... version) {
            Collections.addAll(this.version, version);
            return this;
        }

        /**
         * Adds Minecraft versions that the constraint applies to.
         *
         * @param version an array of {@link dev.neuralnexus.taterapi.meta.enums.MinecraftVersion}
         * @return the current {@link Builder} instance
         */
        public Builder version(
                final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion... version) {
            Collections.addAll(
                    this.version,
                    Stream.of(version)
                            .map(dev.neuralnexus.taterapi.meta.enums.MinecraftVersion::ref)
                            .toArray(MinecraftVersion[]::new));
            return this;
        }

        /**
         * Sets whether the minimum version is inclusive.
         *
         * @param inclusive true if the minimum version is inclusive, false otherwise
         * @return the current {@link Builder} instance
         */
        public Builder minInclusive(final boolean inclusive) {
            this.minInclusive = inclusive;
            return this;
        }

        /**
         * Sets the minimum Minecraft version for the constraint to be satisfied.
         *
         * @param min the minimum {@link MinecraftVersion}
         * @return the current {@link Builder} instance
         */
        public Builder min(final @NonNull MinecraftVersion min) {
            this.min = min;
            return this;
        }

        /**
         * Sets the minimum Minecraft version for the constraint to be satisfied.
         *
         * @param min the minimum {@link dev.neuralnexus.taterapi.meta.enums.MinecraftVersion}
         * @return the current {@link Builder} instance
         */
        public Builder min(
                final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion min) {
            this.min = min.ref();
            return this;
        }

        /**
         * Sets whether the maximum version is inclusive.
         *
         * @param inclusive true if the maximum version is inclusive, false otherwise
         * @return the current {@link Builder} instance
         */
        public Builder maxInclusive(final boolean inclusive) {
            this.maxInclusive = inclusive;
            return this;
        }

        /**
         * Sets the maximum Minecraft version for the constraint to be satisfied.
         *
         * @param max the maximum {@link MinecraftVersion}
         * @return the current {@link Builder} instance
         */
        public Builder max(final @NonNull MinecraftVersion max) {
            this.max = max;
            return this;
        }

        /**
         * Sets the maximum Minecraft version for the constraint to be satisfied.
         *
         * @param max the maximum {@link dev.neuralnexus.taterapi.meta.enums.MinecraftVersion}
         * @return the current {@link Builder} instance
         */
        public Builder max(
                final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion max) {
            this.max = max.ref();
            return this;
        }

        /**
         * Sets whether the constraint is invert.
         *
         * @param invert true if the constraint is invert, false otherwise
         * @return the current {@link Builder} instance
         */
        public Builder invert(final boolean invert) {
            this.invert = invert;
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
                    mappings,
                    Collections.unmodifiableCollection(platform),
                    Collections.unmodifiableCollection(side),
                    Collections.unmodifiableCollection(version),
                    minInclusive,
                    min,
                    maxInclusive,
                    max,
                    invert);
        }

        /**
         * Builds and evaluates the {@link Constraint} instance.
         *
         * @return true if the constraint is satisfied, false otherwise
         */
        public boolean result() {
            return this.build().result();
        }
    }

    /**
     * Creates a version range constraint.
     *
     * @param minInclusive whether the minimum version is inclusive
     * @param min the minimum {@link MinecraftVersion}
     * @param maxInclusive whether the maximum version is inclusive
     * @param max the maximum {@link MinecraftVersion}
     * @return a {@link Constraint.Builder} instance representing the version range
     */
    public static Constraint.Builder range(
            final boolean minInclusive,
            final @NonNull MinecraftVersion min,
            final boolean maxInclusive,
            final @NonNull MinecraftVersion max) {
        return builder().minInclusive(minInclusive).min(min).maxInclusive(maxInclusive).max(max);
    }

    /**
     * Creates a version range constraint.
     *
     * @param minInclusive whether the minimum version is inclusive
     * @param min the minimum {@link dev.neuralnexus.taterapi.meta.enums.MinecraftVersion}
     * @param maxInclusive whether the maximum version is inclusive
     * @param max the maximum {@link dev.neuralnexus.taterapi.meta.enums.MinecraftVersion}
     * @return a {@link Constraint.Builder} instance representing the version range
     */
    public static Constraint.Builder range(
            final boolean minInclusive,
            final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion min,
            final boolean maxInclusive,
            final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion max) {
        return builder().minInclusive(minInclusive).min(min).maxInclusive(maxInclusive).max(max);
    }

    /**
     * Creates a version range constraint.
     *
     * @param min the minimum {@link MinecraftVersion}
     * @param max the maximum {@link MinecraftVersion}
     * @return a {@link Constraint.Builder} instance representing the version range
     */
    public static Constraint.Builder range(
            final @NonNull MinecraftVersion min, final @NonNull MinecraftVersion max) {
        return builder().min(min).max(max);
    }

    /**
     * Creates a version range constraint.
     *
     * @param min the minimum {@link dev.neuralnexus.taterapi.meta.enums.MinecraftVersion}
     * @param max the maximum {@link dev.neuralnexus.taterapi.meta.enums.MinecraftVersion}
     * @return a {@link Constraint.Builder} instance representing the version range
     */
    public static Constraint.Builder range(
            final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion min,
            final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion max) {
        return builder().min(min).max(max);
    }

    /**
     * Creates a version greater than constraint.
     *
     * @param min the minimum {@link MinecraftVersion}
     * @return a {@link Constraint.Builder} instance valid for versions greater than the specified
     *     minimum
     */
    public static Constraint.Builder greaterThan(final @NonNull MinecraftVersion min) {
        return builder().min(min).minInclusive(false);
    }

    /**
     * Creates a version greater than constraint.
     *
     * @param min the minimum {@link dev.neuralnexus.taterapi.meta.enums.MinecraftVersion}
     * @return a {@link Constraint.Builder} instance valid for versions greater than the specified
     *     minimum
     */
    public static Constraint.Builder greaterThan(
            final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion min) {
        return builder().min(min).minInclusive(false);
    }

    /**
     * Creates a version no less than constraint.
     *
     * @param min the minimum {@link MinecraftVersion}
     * @return a {@link Constraint.Builder} instance valid for versions no less than the specified
     *     minimum
     */
    public static Constraint.Builder noLessThan(final @NonNull MinecraftVersion min) {
        return builder().min(min);
    }

    /**
     * Creates a version no less than constraint.
     *
     * @param min the minimum {@link dev.neuralnexus.taterapi.meta.enums.MinecraftVersion}
     * @return a {@link Constraint.Builder} instance valid for versions no less than the specified
     *     minimum
     */
    public static Constraint.Builder noLessThan(
            final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion min) {
        return builder().min(min);
    }

    /**
     * Creates a version less than constraint.
     *
     * @param max the maximum {@link MinecraftVersion}
     * @return a {@link Constraint.Builder} instance valid for versions less than the specified
     *     maximum
     */
    public static Constraint.Builder lessThan(final @NonNull MinecraftVersion max) {
        return builder().max(max).maxInclusive(false);
    }

    /**
     * Creates a version less than constraint.
     *
     * @param max the maximum {@link dev.neuralnexus.taterapi.meta.enums.MinecraftVersion}
     * @return a {@link Constraint.Builder} instance valid for versions less than the specified
     *     maximum
     */
    public static Constraint.Builder lessThan(
            final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion max) {
        return builder().max(max).maxInclusive(false);
    }

    /**
     * Creates a version no greater than constraint.
     *
     * @param max the maximum {@link MinecraftVersion}
     * @return a {@link Constraint.Builder} instance valid for versions no greater than the
     *     specified maximum
     */
    public static Constraint.Builder noGreaterThan(final @NonNull MinecraftVersion max) {
        return builder().max(max);
    }

    /**
     * Creates a version no greater than constraint.
     *
     * @param max the maximum {@link dev.neuralnexus.taterapi.meta.enums.MinecraftVersion}
     * @return a {@link Constraint.Builder} instance valid for versions no greater than the
     *     specified maximum
     */
    public static Constraint.Builder noGreaterThan(
            final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion max) {
        return builder().max(max);
    }

    /**
     * Evaluator class for evaluating {@link Constraint} instances against the current environment.
     */
    public static final class Evaluator {
        private static final Map<@NonNull Constraint, @NonNull Boolean> CACHE = new WeakHashMap<>();
        private static final @NonNull MetaAPI META = MetaAPI.instance();
        private static final @NonNull Mappings MAPPINGS = META.mappings();
        private static final @NonNull MinecraftVersion version = META.version();

        public static boolean DEBUG = false;
        private static final Logger logger = Logger.create("taterliblite-meta-constraint");

        private Evaluator() {}

        /**
         * Evaluates the dependency constraints.
         *
         * @param constraint the {@link Constraint} to evaluate
         * @return true if the dependency constraints are satisfied, false otherwise
         */
        public static boolean evalDeps(final @NonNull Constraint constraint) {
            if (constraint.deps().isEmpty()) {
                return true;
            }
            if (!META.isModLoaded(constraint.deps().toArray(String[]::new))) {
                if (DEBUG) {
                    logger.debug(
                            "Dependency constraint failed. Required deps not found: "
                                    + constraint.deps());
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
        public static boolean evalMappings(final @NonNull Constraint constraint) {
            if (constraint.mappings().isEmpty() || constraint.mappings().contains(Mappings.NONE)) {
                return true;
            }
            if (!constraint.mappings().contains(MAPPINGS)) {
                if (DEBUG) {
                    logger.debug(
                            "Mappings constraint failed. Required: "
                                    + constraint.mappings()
                                    + ", Found: "
                                    + MAPPINGS);
                }
                return false;
            }
            if (DEBUG) {
                logger.debug(
                        "Mappings constraint passed. Expected: "
                                + constraint.mappings()
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
        public static boolean evalPlatform(final @NonNull Constraint constraint) {
            if (constraint.platform().isEmpty()) {
                return true;
            }
            if (!META.isPlatformPresent(constraint.platform().toArray(Platform[]::new))) {
                if (DEBUG) {
                    logger.debug(
                            "Platform constraint failed. Required platforms not found: "
                                    + constraint.platform());
                }
                return false;
            }
            if (DEBUG) {
                logger.debug("Platform constraint passed. Expected: " + constraint.platform());
            }
            return true;
        }

        /**
         * Evaluates the side constraints.
         *
         * @param constraint the {@link Constraint} to evaluate
         * @return true if the side constraints are satisfied, false otherwise
         */
        public static boolean evalSide(final @NonNull Constraint constraint) {
            if (constraint.side().isEmpty()) {
                return true;
            }
            if (!constraint.side().contains(META.side())) {
                if (DEBUG) {
                    logger.debug(
                            "Side constraint failed. Required sides not found: "
                                    + constraint.side());
                }
                return false;
            }
            if (DEBUG) {
                logger.debug(
                        "Side constraint passed. Expected: "
                                + constraint.side()
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
        public static boolean evalVersion(final @NonNull Constraint constraint) {
            if (constraint.version().isEmpty()
                    && constraint.min() == MinecraftVersions.UNKNOWN
                    && constraint.max() == MinecraftVersions.UNKNOWN) {
                return true;
            }
            if (!constraint.version().isEmpty() && !constraint.version().contains(version)) {
                if (DEBUG) {
                    logger.debug(
                            "Version constraint failed. Required versions not found: "
                                    + constraint.version()
                                    + ", Found: "
                                    + version);
                }
                return false;
            }
            if (!version.isInRange(
                    constraint.minInclusive(),
                    constraint.min(),
                    constraint.maxInclusive(),
                    constraint.max())) {
                if (DEBUG) {
                    logger.debug(
                            "Version constraint failed. Required range not satisfied: "
                                    + constraint.min()
                                    + " - "
                                    + constraint.max()
                                    + ", Found: "
                                    + version);
                }
                return false;
            }
            if (DEBUG) {
                logger.debug(
                        "Version constraint passed. Expected: "
                                + constraint.version()
                                + ", "
                                + constraint.min()
                                + "-"
                                + constraint.max()
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
        public static boolean evaluate(final @NonNull Constraint constraint) {
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
            if (constraint.invert()) {
                result = !result;
            }
            CACHE.put(constraint, result);
            return result;
        }
    }
}
