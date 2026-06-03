package dev.neuralnexus.taterapi.crossperms;

import dev.neuralnexus.taterapi.meta.Platform;
import org.jspecify.annotations.NonNull;

import java.util.Collection;

/** A generic provider for permissions plugins */
public interface PermissionsProvider {
    @NonNull String id();

    @NonNull Platform platform();

    /**
     * Get the providers for the provider
     *
     * @return The providers for the provider
     */
    @NonNull Collection<HasPermission<?, ?>> providers();
}
