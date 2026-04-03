package dev.neuralnexus.taterapi.crossperms;

import org.jspecify.annotations.NonNull;

@FunctionalInterface
public interface Permissible {
    <P> TriState hasPermission(final @NonNull P permission);
}
