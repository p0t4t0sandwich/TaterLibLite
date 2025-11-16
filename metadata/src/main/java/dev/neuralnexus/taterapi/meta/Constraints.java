/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta;

import dev.neuralnexus.taterapi.meta.anno.AConstraints;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

public final class Constraints {
    private final Collection<Constraint> constraints;
    private boolean evaluated = false;
    private boolean result;

    public Constraints(Collection<Constraint> constraints) {
        this.constraints = Collections.unmodifiableCollection(constraints);
    }

    public Constraints(Constraint... constraints) {
        this.constraints = Set.of(constraints);
    }

    public static Constraints from(AConstraints constraints) {
        return new Constraints(Stream.of(constraints.value()).map(Constraint::from).toList());
    }

    public boolean result() {
        if (!this.evaluated) {
            this.result = constraints.stream().allMatch(Constraint::result);
            this.evaluated = true;
        }
        return this.result;
    }
}
