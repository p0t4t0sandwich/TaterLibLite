package dev.neuralnexus.taterapi.crossperms;

import java.util.ArrayList;
import java.util.List;

public final class PermissionRegistry {
    private final List<HasPermission<?, ?>> handlers = new ArrayList<>();

    public <P, S extends Permissible> void registerHandler(final HasPermission<P, S> handler) {
        this.handlers.add(handler);
    }
}
