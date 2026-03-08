/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.reflecto;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record MappingMember(
        @NonNull String alias,
        @NonNull MappingClass parent,
        @NonNull String mapping,
        @NonNull Type type,
        @NonNull Access access,
        @NonNull Modifier modifier,
        @NonNull MethodType methodType) {
    public @NonNull MethodHandle resolve() {
        return this.resolve(MethodHandles.lookup());
    }

    public @NonNull MethodHandle resolve(MethodHandles.@NonNull Lookup lookup) {
        if (access.equals(Access.PRIVATE) && !lookup.lookupClass().equals(this.parent.clazz())) {
            try {
                lookup = MethodHandles.privateLookupIn(this.parent.clazz(), lookup);
            } catch (final IllegalAccessException e) {
                throw new RuntimeException(
                        "Failed to create private lookup for class: "
                                + this.parent.clazz()
                                + " for Reflecto member: "
                                + this.alias,
                        e);
            }
        }
        return switch (this.type) {
            case FIELD -> resolveField(lookup);
            case METHOD -> resolveMethod(lookup);
            case CONSTRUCTOR -> resolveConstructor(lookup);
        };
    }

    private @NonNull MethodHandle resolveField(final MethodHandles.@NonNull Lookup lookup) {
        throw new UnsupportedOperationException("Field resolution not implemented yet");
    }

    private @NonNull MethodHandle resolveMethod(final MethodHandles.@NonNull Lookup lookup) {
        try {
            return switch (modifier) {
                case NONE, FINAL, SYNTHETIC ->
                        lookup.findVirtual(this.parent.clazz(), this.mapping, this.methodType);
                case STATIC ->
                        lookup.findStatic(this.parent.clazz(), this.mapping, this.methodType);
            };
        } catch (final IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(
                    "Failed to resolve method: "
                            + mapping
                            + " in class: "
                            + parent
                            + " for Reflecto member: "
                            + alias,
                    e);
        }
    }

    private @NonNull MethodHandle resolveConstructor(final MethodHandles.@NonNull Lookup lookup) {
        throw new UnsupportedOperationException("Constructor resolution not implemented yet");
    }

    public static Builder member(
            final @NonNull String alias,
            final @NonNull MappingClass parent,
            final @NonNull Type type) {
        return new Builder(alias, parent, type);
    }

    public static class Builder {
        private final String alias;
        private final MappingClass parent;
        private final Type type;
        private Access access = Access.PUBLIC;
        private Modifier modifier = Modifier.NONE;
        private MethodType methodType;
        private final List<MappingEntry> mappingEntries = new ArrayList<>();

        public Builder(
                final @NonNull String alias,
                final @NonNull MappingClass parent,
                final @NonNull Type type) {
            this.alias = alias;
            this.parent = parent;
            this.type = type;
        }

        public @NonNull Builder access(final @NonNull Access access) {
            this.access = access;
            return this;
        }

        public @NonNull Builder modifier(final @NonNull Modifier modifier) {
            this.modifier = modifier;
            return this;
        }

        public @NonNull Builder methodType(final @NonNull MethodType methodType) {
            this.methodType = methodType;
            return this;
        }

        public @NonNull Builder mappings(final @NonNull MappingEntry... entries) {
            Collections.addAll(this.mappingEntries, entries);
            return this;
        }

        public @NonNull Builder mappings(final MappingEntry.@NonNull Builder... entries) {
            for (final MappingEntry.Builder entry : entries) {
                this.mappingEntries.add(entry.build());
            }
            return this;
        }

        public @NonNull MappingMember build() {
            MappingEntry mEntry = null;
            for (final MappingEntry entry : mappingEntries) {
                if (mEntry != null) {
                    throw new IllegalStateException(
                            "Multiple valid mappings found for member: " + alias);
                }
                if (entry.constraints().result()) {
                    mEntry = entry;
                }
            }
            if (mEntry == null) {
                throw new IllegalStateException("No valid mapping found for member: " + alias);
            }
            MethodType mt =
                    mEntry.methodType() != MethodType.methodType(void.class)
                            ? mEntry.methodType()
                            : methodType;
            return new MappingMember(alias, parent, mEntry.value(), type, access, modifier, mt);
        }
    }

    public enum Type {
        FIELD,
        METHOD,
        CONSTRUCTOR
    }

    public enum Access {
        PUBLIC,
        PRIVATE
    }

    public enum Modifier {
        NONE,
        STATIC,
        FINAL,
        SYNTHETIC
    }
}
