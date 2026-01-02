/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta;

import dev.neuralnexus.taterapi.meta.anno.AConstraint;
import dev.neuralnexus.taterapi.meta.anno.AConstraints;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

public final class Constraints {
    private final Collection<@NonNull Constraint> constraints;
    private boolean evaluated = false;
    private boolean result;

    public Constraints(final Collection<@NonNull Constraint> constraints) {
        this.constraints = Collections.unmodifiableCollection(constraints);
    }

    public Constraints(final @NonNull Constraint... constraints) {
        this.constraints = Set.of(constraints);
    }

    public static Constraints from(final @NonNull AConstraint... constraints) {
        return new Constraints(Stream.of(constraints).map(Constraint::from).toList());
    }

    public static Constraints from(final @NonNull AConstraints constraints) {
        return Constraints.from(constraints.value());
    }

    public boolean result() {
        if (!this.evaluated) {
            this.result = constraints.stream().allMatch(Constraint::result);
            this.evaluated = true;
        }
        return this.result;
    }
}
