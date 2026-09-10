/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.registries;

import dev.neuralnexus.taterapi.data.Element;
import dev.neuralnexus.taterapi.data.Key;
import dev.neuralnexus.taterapi.data.action.Action;
import dev.neuralnexus.taterapi.data.action.Action1;
import dev.neuralnexus.taterapi.data.action.Action2;
import dev.neuralnexus.taterapi.data.action.Action3;
import dev.neuralnexus.taterapi.data.action.Action4;
import dev.neuralnexus.taterapi.data.action.ActionFactory;
import dev.neuralnexus.taterapi.data.value.Value;
import dev.neuralnexus.taterapi.impl.data.KeyBuilderImpl;
import dev.neuralnexus.taterapi.impl.data.action.ActionFactoryImpl;
import dev.neuralnexus.taterapi.impl.data.value.ValueFactoryImpl;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public final class DataRegistry {
    static {
        BuilderRegistry.register(Key.Builder.class, KeyBuilderImpl::new);
        FactoryRegistry.register(Value.Factory.class, ValueFactoryImpl::new);
        FactoryRegistry.register(ActionFactory.class, ActionFactoryImpl::new);
    }

    private static final Map<Class<?>, Set<Entry<?, ?>>> keyRegistry = new ConcurrentHashMap<>();
    private static final ClassValue<Class<?>[]> HIERARCHY_CACHE =
            new ClassValue<>() {
                @Override
                protected Class<?>[] computeValue(final @NonNull Class<?> type) {
                    final Set<Class<?>> inherited = new HashSet<>();

                    Class<?> current = type.getSuperclass();
                    while (current != null) {
                        inherited.add(current);
                        current = current.getSuperclass();
                    }

                    final List<Class<?>> toVisit =
                            new ArrayList<>(Arrays.asList(type.getInterfaces()));
                    while (!toVisit.isEmpty()) {
                        final Class<?> iface = toVisit.removeLast();
                        if (inherited.add(iface)) {
                            toVisit.addAll(Arrays.asList(iface.getInterfaces()));
                        }
                    }
                    return inherited.toArray(new Class<?>[0]);
                }
            };

    public static <B> Builder<B> register(
            final @NonNull Class<?> iface, final @NonNull Class<B> backer) {
        Objects.requireNonNull(iface, "iface");
        Objects.requireNonNull(backer, "backer");
        return new Builder<>(iface, backer);
    }

    @SuppressWarnings("unchecked")
    public static <B, T> @Nullable Element<T> query(
            final Key<? extends Element<T>> key, final @NonNull B objRef) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(objRef, "objRef");

        final Class<?>[] hierarchy = HIERARCHY_CACHE.get(objRef.getClass());
        for (final Class<?> iface : hierarchy) {
            final Set<Entry<?, ?>> entries = keyRegistry.get(iface);
            if (entries == null) continue;
            for (final Entry<?, ?> entry : entries) {
                if (entry.key().equals(key)) {
                    return ((Initializer<B, T>) entry.get()).init(objRef);
                }
            }
        }
        return null;
    }

    static <B, E> void register(final @NonNull Class<?> iface, final @NonNull Entry<B, E> entry) {
        Objects.requireNonNull(iface, "iface");
        Objects.requireNonNull(entry, "entry");
        keyRegistry.computeIfAbsent(iface, _ -> ConcurrentHashMap.newKeySet()).add(entry);
    }

    public record Entry<B, E>(
            @NonNull Key<? extends Element<E>> key, @NonNull Initializer<B, E> get) {}

    @FunctionalInterface
    public interface Initializer<B, E> {
        Element<E> init(B objRef);
    }

    public static class Builder<B> {
        private final @NonNull Class<?> iface;
        private final @NonNull Class<B> backer;

        public Builder(final @NonNull Class<?> iface, final @NonNull Class<B> backer) {
            Objects.requireNonNull(iface, "iface");
            Objects.requireNonNull(backer, "backer");
            this.iface = iface;
            this.backer = backer;
        }

        public <V extends Value<E>, E> Builder<B> mutable(
                final @NonNull Key<V> key,
                final Value.@NonNull Getter<B, E> GET,
                final Value.@NonNull Setter<B, E> SET) {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(GET, "GET");
            Objects.requireNonNull(SET, "SET");
            DataRegistry.register(this.iface, new Entry<>(key, Value.mutableOf(key, GET, SET)));
            return this;
        }

        public <V extends Value<E>, E> Builder<B> immutable(
                final @NonNull Key<V> key, final Value.@NonNull Getter<B, E> GET) {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(GET, "GET");
            DataRegistry.register(this.iface, new Entry<>(key, Value.immutableOf(key, GET)));
            return this;
        }

        public <R> Builder<B> action(
                final @NonNull Key<? extends Action<R>> key,
                final Action.@NonNull Provider<B, R> provider) {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(provider, "provider");
            DataRegistry.register(this.iface, new Entry<>(key, Action.of(key, provider)));
            return this;
        }

        public <R, A> Builder<B> action(
                final @NonNull Key<? extends Action1<R, A>> key,
                final Action.@NonNull Provider1<B, R, A> provider) {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(provider, "provider");
            DataRegistry.register(this.iface, new Entry<>(key, Action.of(key, provider)));
            return this;
        }

        public <R, A, B1> Builder<B> action(
                final @NonNull Key<? extends Action2<R, A, B1>> key,
                final Action.@NonNull Provider2<B, R, A, B1> provider) {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(provider, "provider");
            DataRegistry.register(this.iface, new Entry<>(key, Action.of(key, provider)));
            return this;
        }

        public <R, A, B1, C> Builder<B> action(
                final @NonNull Key<? extends Action3<R, A, B1, C>> key,
                final Action.@NonNull Provider3<B, R, A, B1, C> provider) {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(provider, "provider");
            DataRegistry.register(this.iface, new Entry<>(key, Action.of(key, provider)));
            return this;
        }

        public <R, A, B1, C, D> Builder<B> action(
                final @NonNull Key<? extends Action4<R, A, B1, C, D>> key,
                final Action.@NonNull Provider4<B, R, A, B1, C, D> provider) {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(provider, "provider");
            DataRegistry.register(this.iface, new Entry<>(key, Action.of(key, provider)));
            return this;
        }
    }
}
