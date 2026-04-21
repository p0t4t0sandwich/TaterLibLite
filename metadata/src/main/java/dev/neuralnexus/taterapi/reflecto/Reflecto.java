/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.reflecto;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Note: Does not support setting final fields */
public final class Reflecto {
    private static final Map<String, MethodHandle> handles = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> @NonNull T invoke(
            final @NonNull String parent, final @NonNull String alias, final Object... args) {
        final MethodHandle mh = getHandle(parent, alias);
        try {
            return (T) mh.invokeWithArguments(args);
        } catch (final Throwable e) {
            throw new RuntimeException(
                    "Failed to invoke MethodHandle for: " + parent + "." + alias, e);
        }
    }

    public static @NonNull MethodHandle getHandle(
            final @NonNull String parent, final @NonNull String alias) {
        final MethodHandle mh = handles.get(parent + "." + alias);
        if (mh == null) {
            throw new IllegalStateException(
                    "No MethodHandle registered for: " + parent + "." + alias);
        }
        return mh;
    }

    public static void register(final MappingMember.@NonNull Builder... builders) {
        for (final MappingMember.Builder builder : builders) {
            register(builder.build());
        }
    }

    public static void register(final @NonNull MappingMember... members) {
        for (final MappingMember member : members) {
            final MethodHandle mh = member.resolve();
            handles.put(member.parent().alias() + "." + member.alias(), mh);
        }
    }
}
