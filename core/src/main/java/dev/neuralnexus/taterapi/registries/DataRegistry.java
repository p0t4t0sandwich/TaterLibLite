/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.registries;

import dev.neuralnexus.taterapi.data.Key;
import dev.neuralnexus.taterapi.data.value.Value;

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
    public static <B, E> @Nullable Value<E> query(
            final Key<? extends Value<E>> key, final @NonNull B objRef) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(objRef, "objRef");

        final Class<?>[] hierarchy = HIERARCHY_CACHE.get(objRef.getClass());
        for (final Class<?> iface : hierarchy) {
            final Set<Entry<?, ?>> entries = keyRegistry.get(iface);
            if (entries == null) continue;
            for (final Entry<?, ?> entry : entries) {
                if (entry.key().equals(key)) {
                    return ((Value.Initializer<B, E>) entry.get()).init(objRef);
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
            @NonNull Key<? extends Value<E>> key, Value.@NonNull Initializer<B, E> get) {}

    public static class EntryBuilder<B, E> {
        private final @NonNull Class<?> iface;
        private final @NonNull Class<B> backer;
        private Key<? extends Value<E>> key;
        private Boolean mutable;
        private Value.Getter<B, E> GET;
        private Value.Setter<B, E> SET;

        public EntryBuilder(final @NonNull Class<?> iface, final @NonNull Class<B> backer) {
            Objects.requireNonNull(iface, "iface");
            Objects.requireNonNull(backer, "backer");
            this.iface = iface;
            this.backer = backer;
        }

        @SuppressWarnings("unchecked")
        public <V> EntryBuilder<B, V> key(final @NonNull Key<? extends Value<V>> key) {
            Objects.requireNonNull(key, "key");
            this.key = (Key<? extends Value<E>>) key;
            return (EntryBuilder<B, V>) this;
        }

        public EntryBuilder<B, E> mutable(final boolean mutable) {
            this.mutable = mutable;
            return this;
        }

        public EntryBuilder<B, E> get(final Value.@NonNull Getter<B, E> GET) {
            Objects.requireNonNull(GET, "GET");
            this.GET = GET;
            return this;
        }

        public EntryBuilder<B, E> set(final Value.@NonNull Setter<B, E> SET) {
            Objects.requireNonNull(SET, "SET");
            this.SET = SET;
            return this;
        }

        public Entry<B, E> build() {
            Objects.requireNonNull(this.iface, "iface");
            Objects.requireNonNull(this.backer, "backer");
            Objects.requireNonNull(this.mutable, "mutable");
            Objects.requireNonNull(this.key, "key");
            Objects.requireNonNull(this.GET, "GET");
            if (this.mutable) {
                Objects.requireNonNull(this.SET, "SET");
                return new Entry<>(this.key, Value.mutableOf(this.key, this.GET, this.SET));
            }
            return new Entry<>(this.key, Value.immutableOf(this.key, this.GET));
        }
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
            final EntryBuilder<B, ?> builder =
                    new EntryBuilder<>(this.iface, this.backer)
                            .mutable(true)
                            .key(key)
                            .get(GET)
                            .set(SET);
            DataRegistry.register(this.iface, builder.build());
            return this;
        }

        public <V extends Value<E>, E> Builder<B> immutable(
                final @NonNull Key<V> key, final Value.@NonNull Getter<B, E> GET) {
            Objects.requireNonNull(key, "key");
            Objects.requireNonNull(GET, "GET");
            final EntryBuilder<B, ?> builder =
                    new EntryBuilder<>(this.iface, this.backer).mutable(false).key(key).get(GET);
            DataRegistry.register(this.iface, builder.build());
            return this;
        }
    }
}
