package org.theko.logger.timer;

/**
 * A utility class for measuring elapsed time in nanoseconds and milliseconds.
 * This class allows you to start, stop, and calculate elapsed time for a given task.
 * The time measurements are based on {@link System#nanoTime()}.
 */
public class WatchTimer {
    /** Start time of the timer in nanoseconds. */
    protected volatile long startTime;
    
    /** End time of the timer in nanoseconds. */
    protected volatile long endTime;

    /**
     * Constructs a new WatchTimer instance.
     * The initial start and end times are set to -1, indicating that the timer hasn't been started yet.
     */
    public WatchTimer() {
        this.startTime = -1;
        this.endTime = -1;
    }

    /**
     * Starts the timer by recording the current time in nanoseconds.
     * Resets the end time to -1, indicating that the timer is in progress.
     */
    public void start() {
        this.startTime = System.nanoTime(); // Record the start time.
        this.endTime = -1; // Reset the end time to indicate the timer is running.
    }

    /**
     * Stops the timer by recording the current time in nanoseconds.
     * Throws an exception if the timer was not started before stopping.
     */
    public void stop() {
        if (startTime == -1) {
            throw new IllegalStateException("Timer wasn't started.");
        }
        this.endTime = System.nanoTime(); // Record the end time.
    }
    
    /**
     * Returns the elapsed time in nanoseconds between the start and stop times.
     * @return The elapsed time in nanoseconds.
     * @throws IllegalStateException if the timer hasn't been started and stopped properly.
     */
    public long getElapsedNanos() {
        if (startTime == -1 || endTime == -1) {
            throw new IllegalStateException("Timer hasn't been started and stopped properly.");
        }
        return this.endTime - this.startTime; // Calculate and return the elapsed time.
    }

    /**
     * Returns the elapsed time in milliseconds between the start and stop times.
     * The conversion is done by dividing the elapsed nanoseconds by 1,000,000.
     * @return The elapsed time in milliseconds.
     * @throws IllegalStateException if the timer hasn't been started and stopped properly.
     */
    public long getElapsedMillis() {
        return getElapsedNanos() / 1_000_000; // Convert nanoseconds to milliseconds.
    }

    /**
     * Measures the elapsed time (in nanoseconds) for a given task, represented by a {@link Runnable}.
     * @param runnable The task to measure.
     * @return The elapsed time in nanoseconds for the provided task.
     */
    public static long calculateElapsedNanos(Runnable runnable) {
        WatchTimer timerElapsed = new WatchTimer(); // Create a new timer instance.
        timerElapsed.start(); // Start the timer.
        runnable.run(); // Execute the task.
        timerElapsed.stop(); // Stop the timer after execution.
        return timerElapsed.getElapsedNanos(); // Return the elapsed time in nanoseconds.
    }

    /**
     * Measures the elapsed time (in milliseconds) for a given task, represented by a {@link Runnable}.
     * The conversion to milliseconds is done by dividing the elapsed nanoseconds by 1,000,000.
     * @param runnable The task to measure.
     * @return The elapsed time in milliseconds for the provided task.
     */
    public static long calculateElapsedMillis(Runnable runnable) {
        return calculateElapsedNanos(runnable) / 1_000_000; // Convert nanoseconds to milliseconds.
    }
}
