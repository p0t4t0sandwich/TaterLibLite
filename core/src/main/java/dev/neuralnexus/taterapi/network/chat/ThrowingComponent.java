/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.network.chat;

import org.jspecify.annotations.NonNull;

public final class ThrowingComponent extends RuntimeException {
    private final @NonNull Object component;

    public ThrowingComponent(final @NonNull Object component) {
        super(component.toString());
        this.component = component;
    }

    public ThrowingComponent(final @NonNull Object component, final @NonNull Throwable cause) {
        super(component.toString(), cause);
        this.component = component;
    }

    public @NonNull Object getComponent() {
        return this.component;
    }
}
