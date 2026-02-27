/**
 * Copyright (c) 2025 Dylan Sperrer - dylan@neuralnexus.dev
 * This project is Licensed under <a href="https://github.com/p0t4t0sandwich/TaterLibLite/blob/main/LICENSE">MIT</a>
 */
package dev.neuralnexus.taterapi.scheduler;

import dev.neuralnexus.taterapi.logger.Logger;

import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/** Scheduler abstraction */
public interface Scheduler {
    static @NonNull Scheduler create(final @NonNull String modid) {
        return new SchedulerImpl(modid);
    }

    @ApiStatus.Internal
    @NonNull Logger logger();

    @ApiStatus.Internal
    @NonNull ExecutorService backgroundExecutor();

    @ApiStatus.Internal
    void shutdownBackgroundScheduler();

    @ApiStatus.Internal
    void replaceBackgroundScheduler(
            final Supplier<ExecutorService> backgroundScheduler, final boolean managed);

    /**
     * Run a task asynchronously.
     *
     * @param callable: The task to run asynchronously.
     */
    @SuppressWarnings("resource")
    default <T> Future<T> runAsync(final @NonNull Callable<T> callable) {
        return this.backgroundExecutor().submit(callable);
    }

    /**
     * Run a task asynchronously.
     *
     * @param run: The task to run asynchronously.
     */
    @SuppressWarnings({"resource", "unchecked"})
    default Future<@Nullable Object> runAsync(final @NonNull Runnable run) {
        return (Future<Object>) this.backgroundExecutor().submit(run);
    }

    /**
     * Run a task asynchronously, after a delay.
     *
     * @param callable: The task to run asynchronously.
     * @param delay: The delay in ticks to wait before running the task.
     */
    @SuppressWarnings("resource")
    default <T> Future<T> runLaterAsync(final @NonNull Callable<T> callable, final long delay) {
        return this.backgroundExecutor()
                .submit(
                        () -> {
                            try {
                                Thread.sleep(delay * 1000 / 20);
                            } catch (final InterruptedException e) {
                                this.logger()
                                        .error(
                                                "Something went wrong while executing an async task",
                                                e);
                            }
                            return callable.call();
                        });
    }

    /**
     * Run a task asynchronously, after a delay.
     *
     * @param run: The task to run asynchronously.
     * @param delay: The delay in ticks to wait before running the task.
     */
    default Future<@Nullable Object> runLaterAsync(final @NonNull Runnable run, final long delay) {
        return this.runLaterAsync(Executors.callable(run), delay);
    }

    /**
     * Run a task asynchronously, repeating it every period seconds. <br>
     * TODO: May want to change it so that it re-schedules itself, if it's not too much overhead.
     *
     * @param run The task to run asynchronously.
     * @param delay The delay in seconds to wait before running the task.
     * @param period The period in seconds to repeat the task.
     */
    @SuppressWarnings({"BusyWait", "InfiniteLoopStatement", "resource"})
    default Future<@Nullable Object> repeatAsync(
            final @NonNull Runnable run, final long delay, final long period) {
        return this.backgroundExecutor()
                .submit(
                        () -> {
                            try {
                                Thread.sleep(delay * 1000 / 20);
                            } catch (final InterruptedException e) {
                                this.logger()
                                        .error(
                                                "Something went wrong while executing an async task",
                                                e);
                            }
                            while (true) {
                                try {
                                    Thread.sleep(period * 1000 / 20);
                                } catch (final InterruptedException e) {
                                    this.logger()
                                            .error(
                                                    "Something went wrong while executing an async task",
                                                    e);
                                }
                                run.run();
                            }
                        });
    }
}
