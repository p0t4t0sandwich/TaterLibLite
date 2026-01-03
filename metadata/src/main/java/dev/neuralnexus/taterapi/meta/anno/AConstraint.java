/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.anno;

import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.Side;
import dev.neuralnexus.taterapi.meta.enums.Platform;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation version of {@link dev.neuralnexus.taterapi.meta.Constraint}. This is used to
 * define constraints on classes, used with entrypoints or conditional mixins.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface AConstraint {
    /** The dependencies required for this constraint to be met. */
    Dependency[] deps() default {};

    /**
     * The mappings this constraint is valid for. Default returns `Mappings.NONE` to indicate no
     * constraint.
     */
    Mappings mappings() default Mappings.NONE;

    /**
     * The platform this constraint is valid for. Default returns `Platforms.UNKNOWN` to indicate no
     * constraint.
     */
    Platform[] platform() default {};

    /**
     * The side(s) the plugin is supposed to run on. Normally you only use one, but there are case
     * where abstract code can run in many environments.
     */
    Side[] side() default {};

    /** Minecraft versions this constraint is valid for. */
    Versions version() default @Versions;

    /** Whether to invert the constraint logic. */
    boolean invert() default false;
}
