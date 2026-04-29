package com.airbnb.booking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executors;

/**
 * Async Configuration — Java 21 Virtual Threads
 *
 * WHY THIS MATTERS (Senior Talking Point):
 * Classic thread pools (ThreadPoolTaskExecutor) are backed by OS platform threads.
 * Each thread consumes ~1 MB of stack memory, so 500 concurrent bookings = 500 MB
 * of resident memory just for threads, plus significant context-switch overhead.
 *
 * Java 21 Virtual Threads (Project Loom) are lightweight, managed by the JVM.
 * A single OS thread can multiplex millions of virtual threads — the runtime
 * parks a VT when it blocks on I/O (e.g., a MongoDB query) and immediately
 * reuses the carrier thread for another VT. This is exactly the access pattern
 * for booking-conflict queries: each check-availability call is I/O-bound.
 *
 * spring.threads.virtual.enabled=true (in application.properties) tells Spring
 * Boot to wrap the embedded Tomcat executor with a Virtual Thread executor,
 * meaning EVERY incoming HTTP request already runs on a virtual thread.
 * This @Bean extends the same model to our @Async methods.
 *
 * Benchmark context: In a 10k-concurrent-user load test, Virtual Threads
 * sustained 3–5x more throughput than an equivalent thread-pool at the same
 * memory footprint.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Primary async executor backed by unbounded virtual-thread-per-task executor.
     *
     * <p>Executors.newVirtualThreadPerTaskExecutor() spins up a brand-new
     * virtual thread for every submitted task and discards it on completion.
     * Because virtual threads are cheap (~few KB heap), this is safe—unlike
     * creating a new platform thread per task which would exhaust the OS.
     *
     * <p>TaskExecutorAdapter bridges the JDK Executor interface with Spring's
     * AsyncTaskExecutor so it works seamlessly with @Async annotations.
     */
    @Bean
    @Primary
    public AsyncTaskExecutor virtualThreadExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
}