/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.meta.impl.platform.meta.forge;

import static dev.neuralnexus.taterapi.util.ReflectionUtil.checkForClass;

import dev.neuralnexus.taterapi.meta.Platform;

import java.lang.reflect.InvocationTargetException;

/** Stores data about the Forge platform */
public final class ForgeData {
    public static Platform.Meta create() {
        if (checkForClass("net.minecraftforge.fml.loading.FMLLoader")) {
            try { // LoadingModList was changed to an interface in 26.1, use as version check
                final Class<?> loadingModList =
                        Class.forName("net.minecraftforge.fml.loading.LoadingModList");
                if (loadingModList.isInterface()) {
                    try { // TODO: Restructure project
                        final Class<?> clazz =
                                Class.forName(
                                        "dev.neuralnexus.taterapi.meta.impl.platform.meta.forge.FMLLoaderMeta_26");
                        return (Platform.Meta) clazz.getDeclaredConstructor().newInstance();
                    } catch (final ClassNotFoundException
                            | IllegalAccessException
                            | InstantiationException
                            | InvocationTargetException
                            | NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    return new FMLLoaderMeta();
                }
            } catch (final ClassNotFoundException e) {
                throw new RuntimeException(e); // Shouldn't actually happen
            }
        } else if (checkForClass("net.minecraftforge.fml.common.Loader")) {
            return new MCFLoaderMeta();
        } else if (checkForClass("cpw.mods.fml.common.Loader")) {
            return new CPWLoaderMeta();
        } else {
            return null;
        }
    }
}
