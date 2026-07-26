/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@sperrer.ca
 * The project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLib/blob/dev/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.crossperms.providers;

import dev.neuralnexus.taterapi.crossperms.HasPermission;
import dev.neuralnexus.taterapi.crossperms.PermissionsProvider;
import dev.neuralnexus.taterapi.crossperms.TriState;
import dev.neuralnexus.taterapi.meta.Platform;
import dev.neuralnexus.taterapi.meta.Platforms;

import org.jspecify.annotations.NonNull;
import org.spongepowered.api.service.permission.Subject;

import java.util.Collection;
import java.util.List;

/** Sponge permissions provider */
public class SpongePermissionsProvider implements PermissionsProvider {
    @Override
    public @NonNull String id() {
        return Platforms.SPONGE.name();
    }

    @Override
    public @NonNull Platform platform() {
        return Platforms.SPONGE;
    }

    @Override
    public @NonNull Collection<HasPermission<?, ?>> handlers() {
        return List.of(
                new HasPermission<String, Subject>() {
                    @Override
                    public @NonNull TriState hasPermission(final @NonNull Subject subject, final @NonNull String permission) {
                        return subject.hasPermission(permission) ? TriState.TRUE : TriState.DEFAULT;
                    }
                });
    }
}
