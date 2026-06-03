package dev.neuralnexus.taterapi.crossperms;

import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Map;

/** A generic provider for permissions plugins */
public interface PermissionsProvider {
    /**
     * Get the providers for the provider
     *
     * @return The providers for the provider
     */
    @NonNull Map<Class<?>, List<HasPermission<?, ?>>> getProviders();
}
