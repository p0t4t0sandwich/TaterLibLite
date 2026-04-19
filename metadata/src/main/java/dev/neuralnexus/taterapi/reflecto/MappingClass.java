/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.reflecto;

import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public record MappingClass(@NonNull String alias, @NonNull Class<?> clazz) {
    public static @NonNull Builder builder(
            final @NonNull String alias, final MappingEntry.@NonNull Builder... builders) {
        List<MappingEntry> entries = new ArrayList<>();
        for (final MappingEntry.Builder builder : builders) {
            entries.add(builder.build());
        }
        return new Builder(alias).entries(entries);
    }

    public static @NonNull Builder builder(
            final @NonNull String alias, final @NonNull MappingEntry... entries) {
        return new Builder(alias).entries(entries);
    }

    public static class Builder {
        private final String alias;
        private final List<MappingEntry> entries = new ArrayList<>();

        public Builder(final @NonNull String alias) {
            this.alias = alias;
        }

        public @NonNull Builder entries(final @NonNull MappingEntry... entries) {
            Collections.addAll(this.entries, entries);
            return this;
        }

        public @NonNull Builder entries(final @NonNull Collection<MappingEntry> entries) {
            this.entries.addAll(entries);
            return this;
        }

        public @NonNull MappingClass build() {
            String entryValue = null;
            for (final MappingEntry entry : entries) {
                if (entryValue != null) {
                    throw new IllegalStateException(
                            "Multiple valid mappings found for member: " + alias);
                }
                if (entry.constraints().result()) {
                    entryValue = entry.value();
                }
            }
            if (entryValue == null) {
                throw new IllegalStateException("No valid mapping found for member: " + alias);
            }
            try {
                return new MappingClass(alias, Class.forName(entryValue));
            } catch (final ClassNotFoundException e) {
                throw new RuntimeException("Failed to load class for parent: " + alias, e);
            }
        }
    }
}
