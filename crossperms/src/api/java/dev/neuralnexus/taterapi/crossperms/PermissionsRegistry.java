package dev.neuralnexus.taterapi.crossperms;

import dev.neuralnexus.taterapi.meta.Platform;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public final class PermissionsRegistry {
    private final Map<Platform, List<PermissionsProvider>> providers = new ConcurrentHashMap<>();

    public void registerProvider(final @NonNull PermissionsProvider provider) {
        Objects.requireNonNull(provider, "Provider cannot be null");
        this.providers.computeIfAbsent(provider.platform(), k -> new ArrayList<>()).add(provider);
    }
}
