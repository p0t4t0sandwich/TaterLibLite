/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.anno;

import dev.neuralnexus.taterapi.meta.enums.MinecraftVersion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Versions {
    /** Acceptable Minecraft version for this constraint. */
    MinecraftVersion[] value() default {};

    /** Whether the minimum version is inclusive. Default is true. */
    boolean minInclusive() default true;

    /**
     * The minimum Minecraft version this constraint is supposed to run on (inclusive). Default
     * returns `MinecraftVersions.Unknown` to indicate no bound.
     */
    MinecraftVersion min() default MinecraftVersion.UNKNOWN;

    /** Whether the maximum version is inclusive. Default is true. */
    boolean maxInclusive() default true;

    /**
     * The maximum Minecraft version this constraint is supposed to run on (inclusive). Default
     * returns `MinecraftVersions.Unknown` to indicate no bound.
     */
    MinecraftVersion max() default MinecraftVersion.UNKNOWN;
}
