/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.mixin;

import dev.neuralnexus.taterapi.Wrapped;
import dev.neuralnexus.taterapi.event.Cancellable;

/**
 * Wrapper for mixin events that implement {@link
 * org.spongepowered.asm.mixin.injection.callback.Cancellable}.
 */
public class CancellableMixin
        implements Cancellable,
                Wrapped<org.spongepowered.asm.mixin.injection.callback.Cancellable> {
    private final org.spongepowered.asm.mixin.injection.callback.Cancellable ci;

    public CancellableMixin(
            org.spongepowered.asm.mixin.injection.callback.Cancellable ci) {
        this.ci = ci;
    }

    @Override
    public boolean cancelled() {
        return this.ci.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancelled) {
        if (cancelled) {
            this.ci.cancel();
        }
    }

    @Override
    public org.spongepowered.asm.mixin.injection.callback.Cancellable unwrap() {
        return this.ci;
    }
}
