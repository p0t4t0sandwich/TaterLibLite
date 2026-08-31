/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.resources;

import static dev.neuralnexus.taterapi.reflecto.MappingEntry.entry;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;
import dev.neuralnexus.taterapi.reflecto.MappingClass;
import dev.neuralnexus.taterapi.registries.BuilderRegistry;
import dev.neuralnexus.taterapi.registries.FactoryRegistry;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Objects;

public interface Identifier {
    boolean staticInit = staticInit();

    static boolean staticInit() {
        BuilderRegistry.register(Builder.class, Impl.Builder::new);
        FactoryRegistry.register(Factory.class, Impl.Factory::new);
        return true;
    }

    /**
     * Gets the namespace of the identifier.
     *
     * @return The namespace of the identifier
     */
    @NonNull String namespace();

    /**
     * Gets the path of the identifier.
     *
     * @return The path of the identifier
     */
    @NonNull String path();

    /**
     * Gets the full identifier.
     *
     * @return The full identifier
     */
    default @NonNull String asString() {
        return this.namespace() + ":" + this.path();
    }

    static @NonNull Identifier of(final @NonNull String namespace, final @NonNull String path) {
        return FactoryRegistry.get(Factory.class).of(namespace, path);
    }

    static @NonNull Identifier of(final @NonNull String full) {
        return FactoryRegistry.get(Factory.class).of(full);
    }

    // TODO: Review underlying implementations to ensure weird cases like "BungeeCord"
    //  can be supported pre-1.13. May require some cursed field-setter work-around.
    static @NonNull Identifier unsafeOf(final @NonNull String full) {
        return FactoryRegistry.get(Factory.class).unsafeOf(full);
    }

    /**
     * Creates a new builder for resource locations.
     *
     * @return The builder
     */
    static @NonNull Builder builder() {
        return BuilderRegistry.get(Builder.class);
    }

    class MC {
        public static final String IDENTIFIER = "Identifier";
        private static final MethodHandle newIdentifier;

        // spotless:off
        static {
            var identifier = MappingClass.builder(IDENTIFIER,
                    entry(Mappings.MOJANG, "net.minecraft.resources.Identifier")
                            .min(MinecraftVersions.V21_11),
                    entry(Mappings.MOJANG, "net.minecraft.resources.ResourceLocation")
                            .range(MinecraftVersions.V14_4, MinecraftVersions.V21_10),
                    entry(Mappings.SEARGE, "net.minecraft.util.ResourceLocation")
                            .range(MinecraftVersions.V7_6, MinecraftVersions.V16_5),
                    entry(Mappings.SEARGE, "net.minecraft.resources.ResourceLocation")
                            .min(MinecraftVersions.V17)
                    // entry(Mappings.YARN_INTERMEDIARY, "net.minecraft.class_3312"),
                    // entry(Mappings.CALAMUS, "net.minecraft.unmapped.C_09211509").min(MinecraftVersions.V7_6)
            ).build();

            final Class<?> clazz = identifier.clazz();
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            try {
                if (Constraint.noGreaterThan(MinecraftVersions.V20_4).result()) {
                    newIdentifier = lookup.findConstructor(clazz, MethodType.methodType(void.class, String.class));
                } else { // min(MinecraftVersions.V20_5)
                    newIdentifier = lookup.findStatic(clazz, "parse", MethodType.methodType(clazz, String.class));
                }
            } catch (final IllegalAccessException | NoSuchMethodException e) {
                final Logger logger = Logger.create("TaterLibLite/Identifier");
                logger.error("Failed to initialize Identifier function", e);
                throw new RuntimeException(e);
            }
        }
        // spotless:on

        @SuppressWarnings("unchecked")
        public static <T> @NonNull T of(final @NonNull String id) {
            try {
                // Can't invokeExact because of the duck typing
                return (T) newIdentifier.invoke(id);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    /** A builder for resource identifiers. */
    interface Builder extends dev.neuralnexus.taterapi.util.Builder<Identifier, Builder> {
        /**
         * Sets the namespace of the identifier.
         *
         * @param namespace The namespace of the identifier
         * @return This builder
         */
        Builder namespace(final @NonNull String namespace);

        /**
         * Sets the path of the identifier.
         *
         * @param path The path of the identifier
         * @return This builder
         */
        Builder path(final @NonNull String path);

        /**
         * Builds the identifier.
         *
         * @return The identifier
         */
        @NonNull Identifier build();
    }

    /** A factory for identifiers. */
    interface Factory {
        @NonNull Identifier of(final @NonNull String namespace, final @NonNull String path);

        @NonNull Identifier of(final @NonNull String full);

        @NonNull Identifier unsafeOf(final @NonNull String full);
    }

    record Impl(@NonNull String namespace, @NonNull String path) implements Identifier {
        @Override
        public @NonNull String toString() {
            return this.asString();
        }

        static class Builder implements Identifier.Builder {
            private String namespace;
            private String path;

            @Override
            public Identifier.Builder namespace(final @NonNull String namespace) {
                this.namespace = Objects.requireNonNull(namespace, "namespace");
                return this;
            }

            @Override
            public Identifier.Builder path(final @NonNull String path) {
                this.path = Objects.requireNonNull(path, "path");
                return this;
            }

            @Override
            public @NonNull Identifier build() {
                return new Impl(
                        assertValidNamespace(this.namespace, this.path),
                        assertValidPath(this.namespace, this.path));
            }
        }

        static class Factory implements Identifier.Factory {
            @Override
            public @NonNull Identifier of(
                    final @NonNull String namespace, final @NonNull String path) {
                return new Impl(
                        assertValidNamespace(namespace, path), assertValidPath(namespace, path));
            }

            @Override
            public @NonNull Identifier of(final @NonNull String full) {
                final String namespace;
                final String path;
                if (full.contains(":")) {
                    namespace = full.split(":")[0];
                    path = full.split(":")[1];
                } else {
                    namespace = "minecraft";
                    path = full;
                }
                return new Impl(
                        assertValidNamespace(namespace, path), assertValidPath(namespace, path));
            }

            @Override
            public @NonNull Identifier unsafeOf(final @NonNull String full) {
                final String namespace;
                final String path;
                if (full.contains(":")) {
                    final String[] split = full.split(":");
                    namespace = split[0];
                    path = split[1];
                } else {
                    namespace = "";
                    path = full;
                }
                return new Impl(namespace, path);
            }
        }
    }

    private static boolean validNamespaceChar(final char namespaceChar) {
        return namespaceChar == '_'
                || namespaceChar == '-'
                || namespaceChar >= 'a' && namespaceChar <= 'z'
                || namespaceChar >= '0' && namespaceChar <= '9'
                || namespaceChar == '.';
    }

    private static boolean isValidNamespace(final @NonNull String namespace) {
        for (int i = 0; i < namespace.length(); ++i) {
            if (!validNamespaceChar(namespace.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static @NonNull String assertValidNamespace(
            final @NonNull String namespace, final @NonNull String path) {
        if (!isValidNamespace(namespace)) {
            throw new IllegalArgumentException(
                    "Non [a-z0-9_.-] character in namespace of location: "
                            + namespace
                            + ":"
                            + path);
        } else {
            return namespace;
        }
    }

    private static boolean validPathChar(final char pathChar) {
        return pathChar == '_'
                || pathChar == '-'
                || pathChar >= 'a' && pathChar <= 'z'
                || pathChar >= '0' && pathChar <= '9'
                || pathChar == '/'
                || pathChar == '.';
    }

    private static boolean isValidPath(final @NonNull String path) {
        for (int i = 0; i < path.length(); ++i) {
            if (!validPathChar(path.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static @NonNull String assertValidPath(
            final @NonNull String namespace, final @NonNull String path) {
        if (!isValidPath(path)) {
            throw new IllegalArgumentException(
                    "Non [a-z0-9/._-] character in path of location: " + namespace + ":" + path);
        } else {
            return path;
        }
    }
}
