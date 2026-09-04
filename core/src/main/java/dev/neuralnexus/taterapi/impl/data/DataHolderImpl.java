/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.impl.data;

import dev.neuralnexus.taterapi.data.DataHolder;
import dev.neuralnexus.taterapi.data.Key;
import dev.neuralnexus.taterapi.data.value.Value;
import dev.neuralnexus.taterapi.registries.DataRegistry;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@ApiStatus.Internal
public class DataHolderImpl implements DataHolder {
    private final Object objRef;
    private final Class<?>[] ifaces;
    private final HashMap<Key<?>, Value<?>> STORE;

    public DataHolderImpl(final @NonNull Object objRef, final @NonNull Class<?>... ifaces) {
        Objects.requireNonNull(objRef, "objRef");
        Objects.requireNonNull(ifaces, "ifaces");
        this.objRef = objRef;
        this.ifaces = ifaces;
        this.STORE = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> Optional<Value<E>> value(@NonNull Key<? extends Value<E>> key) {
        Objects.requireNonNull(key, "key");
        return Optional.ofNullable(
                (Value<E>)
                        this.STORE.computeIfAbsent(
                                key, _ -> DataRegistry.query(key, objRef, this.ifaces)));
    }
}
