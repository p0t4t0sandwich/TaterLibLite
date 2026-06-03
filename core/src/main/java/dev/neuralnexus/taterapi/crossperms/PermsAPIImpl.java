package dev.neuralnexus.taterapi.crossperms;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/** Permissions API implementation */
public final class PermsAPIImpl implements PermsAPI {
    private static PermsAPIImpl INSTANCE;

    public static PermsAPIImpl getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new PermsAPIImpl();
        }
        return INSTANCE;
    }

    private PermsAPIImpl() {}

    private final Map<Class<?>, List<HasPermission<?, ?>>> providers = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <P, S> Collection<HasPermission<P, S>> providers(
            final @NonNull Class<P> pType, final @NonNull Class<S> sType) {
        Objects.requireNonNull(pType, "Permission type cannot be null");
        Objects.requireNonNull(sType, "Subject type cannot be null");

        // Get providers that extend from subjectType, and then line up with permissionType
        List<HasPermission<P, S>> list = new ArrayList<>();
        for (final Map.Entry<Class<?>, List<HasPermission<?, ?>>> entry : this.providers.entrySet()) {
            if (sType.isAssignableFrom(entry.getKey())) {
                for (final HasPermission<?, ?> hp : entry.getValue()) {
                    if (pType.isAssignableFrom(hp.permissionType())) {
                        list.add((HasPermission<P, S>) hp);
                    }
                }
            }
        }
        return list;
    }

    @Override
    public void registerProvider(final @NonNull PermissionsProvider provider) {
        Objects.requireNonNull(provider, "Provider cannot be null");
        final Map<Class<?>, List<HasPermission<?, ?>>> map = provider.getProviders();
        Objects.requireNonNull(map, "Provider returned no providers");
        map.forEach(
                (key, value) ->
                        this.providers.computeIfAbsent(key, k -> new ArrayList<>()).addAll(value));
    }
}
