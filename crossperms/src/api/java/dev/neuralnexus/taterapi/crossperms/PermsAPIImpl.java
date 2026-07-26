package dev.neuralnexus.taterapi.crossperms;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    private final Collection<PermissionsProvider> providers = new ArrayList<>();

    @SuppressWarnings("unchecked")
    @Override
    public <P, S> Collection<HasPermission<P, S>> providers(
            final @NonNull Class<P> pType, final @NonNull Class<S> sType) {
        Objects.requireNonNull(pType, "Permission type cannot be null");
        Objects.requireNonNull(sType, "Subject type cannot be null");

        // Get handlers that extend from subjectType, and then line up with permissionType
        List<HasPermission<P, S>> list = new ArrayList<>();
        for (final PermissionsProvider provider : this.providers) {
            for (final HasPermission<?, ?> handler : provider.handlers()) {
                if (sType.isAssignableFrom(handler.subjectType())
                        && pType.isAssignableFrom(handler.permissionType())) {
                    list.add((HasPermission<P, S>) handler);
                }
            }
        }
        return list;
    }

    @Override
    public void register(final @NonNull PermissionsProvider provider) {
        Objects.requireNonNull(provider, "Provider cannot be null");
        this.providers.add(provider);
    }
}
