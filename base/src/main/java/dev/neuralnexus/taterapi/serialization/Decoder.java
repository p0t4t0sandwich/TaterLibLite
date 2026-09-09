/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.serialization;

import dev.neuralnexus.taterapi.Result;

@FunctionalInterface
public interface Decoder<A, B> {
    Result<A> decode(final B input);
}
