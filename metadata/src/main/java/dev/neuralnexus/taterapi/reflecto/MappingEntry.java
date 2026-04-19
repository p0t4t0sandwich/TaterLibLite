/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.reflecto;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.Constraints;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record MappingEntry(
        @NonNull String value, @NonNull MethodType methodType, @NonNull Constraints constraints) {
    public static @NonNull Builder builder() {
        return new Builder();
    }

    public static Builder entry(final @NonNull String value) {
        return new Builder().value(value);
    }

    public static Builder entry(final @NonNull Mappings mappings, final @NonNull String value) {
        return new Builder().value(value).mappings(mappings);
    }

    //    public static Builder entry(
    //            final @NonNull Mappings mappings,
    //            final @NonNull String value,
    //            final @NonNull MinecraftVersion min) {
    //        return new Builder().value(value).mappings(mappings).min(min);
    //    }

    //    public static Builder entry(
    //            final @NonNull Mappings mappings,
    //            final @NonNull String value,
    //            final @NonNull MinecraftVersion min,
    //            final @NonNull MinecraftVersion max) {
    //        return new Builder().value(value).mappings(mappings).range(min, max);
    //    }

    public static class Builder {
        private String value;
        private MethodType methodType = MethodType.methodType(void.class);
        private final List<Constraint> constraints = new ArrayList<>();
        private final Set<Mappings> mappings = new HashSet<>();
        private MinecraftVersion[] version = null;
        private MinecraftVersion min = null;
        private MinecraftVersion max = null;
        private boolean constant = false;

        private Builder() {}

        public @NonNull Builder value(final @NonNull String value) {
            this.value = value;
            return this;
        }

        public @NonNull Builder methodType(final @NonNull MethodType methodType) {
            this.methodType = methodType;
            return this;
        }

        public @NonNull Builder mappings(final @NonNull Mappings... mapping) {
            Collections.addAll(this.mappings, mapping);
            return this;
        }

        public @NonNull Builder version(final @NonNull MinecraftVersion... versions) {
            this.version = versions;
            return this;
        }

        public @NonNull Builder range(
                final @NonNull MinecraftVersion min, final @NonNull MinecraftVersion max) {
            this.min = min;
            this.max = max;
            return this;
        }

        public @NonNull Builder min(final @NonNull MinecraftVersion min) {
            this.min = min;
            return this;
        }

        public @NonNull Builder max(final @NonNull MinecraftVersion max) {
            this.max = max;
            return this;
        }

        public @NonNull Builder constant(final boolean constant) {
            this.constant = constant;
            return this;
        }

        public @NonNull Builder constraint(final @NonNull Constraint... constraint) {
            Collections.addAll(this.constraints, constraint);
            return this;
        }

        public @NonNull MappingEntry build() {
            // Throw if value is null or empty
            if (value == null || value.isEmpty()) {
                throw new IllegalStateException("Mapping cannot be null or empty");
            }
            // Build helper constraint
            if (!mappings.isEmpty() || version != null || min != null || max != null) {
                final Constraint.Builder cb = Constraint.builder();
                if (!mappings.isEmpty()) {
                    cb.mappings(mappings);
                }
                if (version != null) {
                    cb.version(version);
                }
                if (min != null && max != null) {
                    cb.version(min, max);
                } else if (min != null) {
                    cb.min(min);
                } else if (max != null) {
                    cb.max(max);
                }
                this.constraints.add(cb.build());
            }
            // Throw if there are no constraints and the mapping is not constant
            if (constraints.isEmpty() && !constant) {
                throw new IllegalStateException(
                        "At least one constraint must be added for mapping: " + value);
            }
            return new MappingEntry(
                    value, methodType, Constraints.builder().or(constraints).build());
        }
    }
}
