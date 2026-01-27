/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta;

import dev.neuralnexus.taterapi.meta.anno.AConstraints;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public final class Constraints {
    private final Collection<@NonNull Constraint> and;
    private final Collection<@NonNull Constraint> or;
    private boolean evaluated = false;
    private boolean result;

    public Constraints(
            final Collection<@NonNull Constraint> and, final Collection<@NonNull Constraint> or) {
        this.and = Collections.unmodifiableCollection(and);
        this.or = Collections.unmodifiableCollection(or);
    }

    public static @NonNull Constraints from(final @NonNull AConstraints constraints) {
        return builder()
                .and(
                        Stream.of(constraints.value())
                                .map(Constraint::from)
                                .toArray(Constraint[]::new))
                .or(Stream.of(constraints.or()).map(Constraint::from).toArray(Constraint[]::new))
                .build();
    }

    /**
     * Evaluates the constraints against the current environment.
     *
     * @return true if the constraints are satisfied, false otherwise.
     */
    public boolean result() {
        if (!this.evaluated) {
            final boolean andResult = this.and.stream().allMatch(Constraint::result);
            final boolean orResult =
                    this.or.isEmpty() || this.or.stream().anyMatch(Constraint::result);
            this.result = andResult && orResult;
            this.evaluated = true;
        }
        return this.result;
    }

    /** Creates a new {@link Builder} instance for constructing a {@link Constraints}. */
    public static Builder builder() {
        return new Builder();
    }

    /** Builder class for constructing {@link Constraints} instances. */
    public static class Builder {
        private final Set<@NonNull Constraint> and =
                Collections.newSetFromMap(new ConcurrentHashMap<>());
        private final Set<@NonNull Constraint> or =
                Collections.newSetFromMap(new ConcurrentHashMap<>());

        /**
         * Adds "and" constraints to the builder.
         *
         * @param constraints the constraints to add
         * @return the builder
         */
        public Builder and(final Collection<@NonNull Constraint> constraints) {
            this.and.addAll(constraints);
            return this;
        }

        /**
         * Adds "and" constraints to the builder.
         *
         * @param constraints the constraints to add
         * @return the builder
         */
        public Builder and(final @NonNull Constraint... constraints) {
            Collections.addAll(this.and, constraints);
            return this;
        }

        /**
         * Adds "and" constraints to the builder.
         *
         * @param constraints the constraints to add
         * @return the builder
         */
        public Builder and(final Constraint.@NonNull Builder... constraints) {
            Collections.addAll(
                    this.and,
                    Stream.of(constraints)
                            .map(Constraint.Builder::build)
                            .toArray(Constraint[]::new));
            return this;
        }

        /**
         * Adds "or" constraints to the builder.
         *
         * @param constraints the constraints to add
         * @return the builder
         */
        public Builder or(final Collection<@NonNull Constraint> constraints) {
            this.or.addAll(constraints);
            return this;
        }

        /**
         * Adds "or" constraints to the builder.
         *
         * @param constraints the constraints to add
         * @return the builder
         */
        public Builder or(final @NonNull Constraint... constraints) {
            Collections.addAll(this.or, constraints);
            return this;
        }

        /**
         * Adds "or" constraints to the builder.
         *
         * @param constraints the constraints to add
         * @return the builder
         */
        public Builder or(final Constraint.@NonNull Builder... constraints) {
            Collections.addAll(
                    this.or,
                    Stream.of(constraints)
                            .map(Constraint.Builder::build)
                            .toArray(Constraint[]::new));
            return this;
        }

        /**
         * Builds the Constraints object.
         *
         * @return the Constraints object
         */
        public Constraints build() {
            return new Constraints(this.and, this.or);
        }

        /**
         * Builds the Constraints object and evaluates the result.
         *
         * @return the result of the Constraints evaluation
         */
        public boolean result() {
            return this.build().result();
        }
    }
}
