/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.version;

import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.MinecraftVersion;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import org.jspecify.annotations.NonNull;

import java.util.HashSet;
import java.util.Set;

public final class Versioned {
    /**
     * Creates a new versioned builder, which can be used to create versioned objects. If {@link
     * Builder#strict}=false, and there are overlapping version ranges, the first entry will be
     * used. Otherwise, if {@link Builder#strict}=true, and there are overlapping version ranges, an
     * exception will be thrown. If no entries are found for the current Minecraft version, an
     * exception will be thrown.
     *
     * @param strict whether to throw an exception if there are overlapping version ranges
     * @return a new versioned builder
     * @param <T> the type of the versioned object
     */
    static <T, B extends Builder<T, B>> Builder<T, B> versioned(final boolean strict) {
        return new Builder<>(strict);
    }

    @SuppressWarnings("unchecked")
    public static class Builder<T, B extends Builder<T, B>> {
        private final Set<Entry<T>> entries = new HashSet<>();
        private final boolean strict;

        public Builder(final boolean strict) {
            this.strict = strict;
        }

        public B add(final @NonNull T object, final @NonNull MinecraftVersion since) {
            this.entries.add(new Entry<>(object, since));
            return (B) this;
        }

        public B add(
                final @NonNull T object,
                final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion since) {
            this.entries.add(new Entry<>(object, since.ref()));
            return (B) this;
        }

        public B add(
                final @NonNull T object,
                final @NonNull MinecraftVersion since,
                final @NonNull MinecraftVersion until) {
            this.entries.add(new Entry<>(object, since, until));
            return (B) this;
        }

        public B add(
                final @NonNull T object,
                final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion since,
                final dev.neuralnexus.taterapi.meta.enums.@NonNull MinecraftVersion until) {
            this.entries.add(new Entry<>(object, since.ref(), until.ref()));
            return (B) this;
        }

        public T build() {
            T resolved = null;
            for (final Entry<T> entry : entries) {
                if (entry.resolve()) {
                    if (this.strict && resolved != null) {
                        throw new IllegalStateException(
                                "Multiple entries found for the current Minecraft version!");
                    }
                    resolved = entry.value;
                }
            }

            if (resolved == null) {
                throw new IllegalStateException(
                        "No entries found for the current Minecraft version!");
            }
            return resolved;
        }
    }

    record Entry<T>(
            @NonNull T value, @NonNull MinecraftVersion since, @NonNull MinecraftVersion until) {
        public Entry(final @NonNull T object, final @NonNull MinecraftVersion since) {
            this(object, since, MinecraftVersions.UNKNOWN);
        }

        public boolean resolve() {
            return Constraint.range(this.since, this.until).result();
        }
    }
}
