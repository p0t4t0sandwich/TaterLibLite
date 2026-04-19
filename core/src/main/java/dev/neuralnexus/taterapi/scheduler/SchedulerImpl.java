/**
 * Copyright (c) 2026 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.scheduler;

import dev.neuralnexus.taterapi.logger.Logger;
import dev.neuralnexus.taterapi.meta.Constraint;
import dev.neuralnexus.taterapi.meta.Mappings;
import dev.neuralnexus.taterapi.meta.MetaAPI;
import dev.neuralnexus.taterapi.meta.MinecraftVersions;

import org.jspecify.annotations.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Locale;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public final class SchedulerImpl implements Scheduler {
    private final @NonNull String modid;
    private final @NonNull Logger logger;
    private boolean managed = false;
    private ExecutorService defaultScheduler;
    private Supplier<@NonNull ExecutorService> backgroundScheduler;

    public SchedulerImpl(final @NonNull String modid) {
        this.modid = modid;
        this.logger = Logger.create(this.modid + "-scheduler");

        if (Constraint.builder()
                .min(MinecraftVersions.V14)
                .mappings(Mappings.MOJANG, Mappings.SEARGE, Mappings.YARN_INTERMEDIARY)
                .result()) {
            try {
                this.backgroundScheduler = getMCScheduler();
                this.logger.info("Using Minecraft background scheduler");
                return;
            } catch (final Throwable t) {
                this.logger.warn(
                        "Failed to get Minecraft background scheduler, falling back to custom scheduler",
                        t);
            }
        }
        this.managed = true;
        this.defaultScheduler = this.create(new AtomicInteger(1));
        this.backgroundScheduler = () -> this.defaultScheduler;
    }

    @Override
    public @NonNull Logger logger() {
        return this.logger;
    }

    @Override
    public @NonNull ExecutorService backgroundExecutor() {
        return this.backgroundScheduler.get();
    }

    @Override
    public void shutdownBackgroundScheduler() {
        if (this.backgroundScheduler != null && this.managed) {
            this.backgroundScheduler.get().shutdown();
        }
        this.backgroundScheduler = null;
    }

    @Override
    public void replaceBackgroundScheduler(
            final Supplier<ExecutorService> backgroundScheduler, final boolean managed) {
        this.shutdownBackgroundScheduler();
        this.managed = managed;
        this.backgroundScheduler = backgroundScheduler;
    }

    // From net.minecraft.util.Mth.clamp(int, int, int)
    public static int clamp(final int i, final int min, final int max) {
        return Math.min(Math.max(i, min), max);
    }

    private static final int MIN_THREADS = 1;
    private static final int DEFAULT_MAX_THREADS = 255;
    private static final String MAX_THREADS_SYSTEM_PROPERTY = "max.bg.threads";

    // Adapted from net.minecraft.Util.getMaxThreads()
    private int getMaxThreads() {
        final String s = System.getProperty(MAX_THREADS_SYSTEM_PROPERTY);
        if (s != null) {
            try {
                final int threads = Integer.parseInt(s);
                if (threads >= MIN_THREADS && threads <= DEFAULT_MAX_THREADS) {
                    return threads;
                }
                this.logger.error(
                        String.format(
                                Locale.ROOT,
                                "Wrong %s property value '%s'. Should be an integer value between 1 and %d.",
                                MAX_THREADS_SYSTEM_PROPERTY,
                                s,
                                DEFAULT_MAX_THREADS));
            } catch (final NumberFormatException e) {
                this.logger.error(
                        String.format(
                                Locale.ROOT,
                                "Could not parse %s property value '%s'. Should be an integer value between 1 and %d.",
                                MAX_THREADS_SYSTEM_PROPERTY,
                                s,
                                DEFAULT_MAX_THREADS));
            }
        }
        return DEFAULT_MAX_THREADS;
    }

    // Adapted from net.minecraft.Util.maxAllowedExecutorThreads()
    private int maxAllowedExecutorThreads() {
        return clamp(
                Runtime.getRuntime().availableProcessors() - 1, MIN_THREADS, this.getMaxThreads());
    }

    private @NonNull ForkJoinPool create(final @NonNull AtomicInteger workerCount) {
        return new ForkJoinPool(
                maxAllowedExecutorThreads(),
                pool -> {
                    final ForkJoinWorkerThread worker =
                            new ForkJoinWorkerThread(pool) {
                                @Override
                                protected void onTermination(final Throwable throwable) {
                                    if (throwable != null) {
                                        SchedulerImpl.this.logger.warn(
                                                this.getName() + " died", throwable);
                                    } else {
                                        SchedulerImpl.this.logger.debug(
                                                this.getName() + " shutdown");
                                    }
                                    super.onTermination(throwable);
                                }
                            };
                    worker.setName(this.modid + "-" + workerCount.getAndIncrement());
                    worker.setDaemon(true);
                    worker.setUncaughtExceptionHandler(this::onThreadException);
                    return worker;
                },
                this::onThreadException,
                true);
    }

    private void onThreadException(final @NonNull Thread thread, @NonNull Throwable throwable) {
        if (throwable instanceof CompletionException) {
            throwable = throwable.getCause();
        }
        this.logger.error(
                String.format(Locale.ROOT, "Caught exception in thread %s", thread), throwable);
    }

    private static Supplier<ExecutorService> getMCScheduler() throws Exception {
        Class<?> Util;
        String backgroundExecutor;
        Class<?> executorType;
        String service;

        if (Constraint.range(MinecraftVersions.V18, MinecraftVersions.V21_1).result()) {
            executorType = ExecutorService.class;
        } else {
            executorType = Executor.class;
        }

        switch (MetaAPI.instance().mappings()) {
            case MOJANG -> {
                Util = Class.forName("net.minecraft.Util");
                backgroundExecutor = "backgroundExecutor";
            }
            case SEARGE -> {
                if (Constraint.noLessThan(MinecraftVersions.V17).result()) {
                    Util = Class.forName("net.minecraft.Util");
                    backgroundExecutor = "m_438745_";
                } else {
                    Util = Class.forName("net.minecraft.util.Util");
                    backgroundExecutor = "func_215072_e";
                }
            }
            case YARN_INTERMEDIARY -> {
                Util = Class.forName("net.minecraft.class_156");
                backgroundExecutor = "method_18349";
            }
            default ->
                    throw new IllegalStateException(
                            "Unsupported mappings: " + MetaAPI.instance().mappings());
        }

        final MethodHandles.Lookup lookup = MethodHandles.lookup();
        if (Constraint.noLessThan(MinecraftVersions.V21_2).result()) {
            switch (MetaAPI.instance().mappings()) {
                case MOJANG -> {
                    executorType = Class.forName("net.minecraft.TracingExecutor");
                    service = "service";
                }
                case SEARGE -> {
                    executorType = Class.forName("net.minecraft.TracingExecutor");
                    service = "f_347685_";
                }
                case YARN_INTERMEDIARY -> {
                    executorType = Class.forName("net.minecraft.class_10207");
                    service = "comp_3205";
                }
                default ->
                        throw new IllegalStateException(
                                "Unsupported mappings: " + MetaAPI.instance().mappings());
            }
            try {
                final Object tracingExecutorInstance =
                        lookup.findStatic(
                                        Util,
                                        backgroundExecutor,
                                        MethodType.methodType(executorType))
                                .invoke();
                final MethodType methodType = MethodType.methodType(ExecutorService.class);
                final MethodHandle serviceHandle =
                        lookup.findVirtual(executorType, service, methodType)
                                .bindTo(tracingExecutorInstance);
                return () -> {
                    try {
                        return (ExecutorService) serviceHandle.invokeExact();
                    } catch (final Throwable t) {
                        throw new RuntimeException(
                                "Failed to get Minecraft background scheduler service", t);
                    }
                };
            } catch (final Throwable t) {
                throw new RuntimeException("Failed to get Minecraft background scheduler", t);
            }
        } else if (Constraint.range(MinecraftVersions.V18, MinecraftVersions.V21_1).result()) {
            final MethodHandle backgroundExecutorHandle =
                    lookup.findStatic(
                            Util, backgroundExecutor, MethodType.methodType(executorType));
            return () -> {
                try {
                    return (ExecutorService) backgroundExecutorHandle.invokeExact();
                } catch (final Throwable t) {
                    throw new RuntimeException("Failed to get Minecraft background scheduler", t);
                }
            };
        } else {
            final MethodHandle backgroundExecutorHandle =
                    lookup.findStatic(
                            Util, backgroundExecutor, MethodType.methodType(executorType));
            return () -> {
                try {
                    return (ExecutorService) (Executor) backgroundExecutorHandle.invokeExact();
                } catch (final Throwable t) {
                    throw new RuntimeException("Failed to get Minecraft background scheduler", t);
                }
            };
        }
    }
}
