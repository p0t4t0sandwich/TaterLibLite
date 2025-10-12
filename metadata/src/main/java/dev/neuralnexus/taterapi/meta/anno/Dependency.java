/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Dependency {
    /**
     * The mod id or plugin name of the dependency
     *
     * @return The mod id or plugin name
     */
    String value();

    /**
     * The list of aliases for the dependency, if any of the aliases are found the dependency is
     * considered present
     *
     * @return The list of aliases
     */
    String[] aliases() default {};

    /**
     * The version of the dependency, if empty any version is accepted
     *
     * @return The version of the dependency
     */
    // String version() default "";
}
