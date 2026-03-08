/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
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
            final @NonNull String parent,
            final @NonNull String alias,
            final Object target,
            final Object... args) {
        final MethodHandle mh = handles.get(parent + "." + alias);
        if (mh == null) {
            throw new IllegalStateException(
                    "No MethodHandle registered for: " + parent + "." + alias);
        }
        try {
            return (T) mh.invokeWithArguments(target, args);
        } catch (final Throwable e) {
            throw new RuntimeException(
                    "Failed to invoke MethodHandle for: " + parent + "." + alias, e);
        }
    }

    public static void register(final MappingMember.@NonNull Builder builder) {
        register(builder.build());
    }

    public static void register(final @NonNull MappingMember member) {
        final MethodHandle mh = member.resolve();
        handles.put(member.parent().alias() + "." + member.alias(), mh);
    }
}
