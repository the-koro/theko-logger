package org.theko.logger.timer;

/**
 * A utility class for benchmarking tasks. This class allows you to run a task multiple times
 * and calculate the average, minimum, and maximum elapsed time.
 */
public class Benchmark {
    /**
     * Runs a task multiple times and returns benchmarking results including average, min, and max times.
     * 
     * @param task The task (Runnable) to benchmark.
     * @param iterations The number of times the task should be executed.
     * @return The benchmark result with average, minimum, and maximum times in nanoseconds.
     */
    public static BenchmarkResult run(Runnable task, int iterations) {
        if (iterations <= 0) {
            throw new IllegalArgumentException("Iterations must be greater than 0.");
        }

        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = Long.MIN_VALUE;
        
        WatchTimer timer = new WatchTimer();

        for (int i = 0; i < iterations; i++) {
            timer.start();
            task.run();
            timer.stop();
            
            long elapsed = timer.getElapsedNanos();
            totalTime += elapsed;
            minTime = Math.min(minTime, elapsed);
            maxTime = Math.max(maxTime, elapsed);
        }

        long averageTime = totalTime / iterations;
        
        return new BenchmarkResult(averageTime, minTime, maxTime, iterations);
    }

    /**
     * Represents the result of a benchmark, including average, minimum, maximum times, and number of iterations.
     */
    public static class BenchmarkResult {
        private final long averageTime;
        private final long minTime;
        private final long maxTime;
        private final int iterations;

        public BenchmarkResult(long averageTime, long minTime, long maxTime, int iterations) {
            this.averageTime = averageTime;
            this.minTime = minTime;
            this.maxTime = maxTime;
            this.iterations = iterations;
        }

        public long getAverageTime() {
            return averageTime;
        }

        public long getMinTime() {
            return minTime;
        }

        public long getMaxTime() {
            return maxTime;
        }

        public int getIterations() {
            return iterations;
        }

        /**
         * Provides a formatted string representation of the benchmark result.
         * 
         * @return The formatted result as a string.
         */
        public String getFormattedReport() {
            return String.format(
                "Benchmark Results (over %d iteration%s):\n" +
                "Average Time: %d ns\n" +
                "Minimum Time: %d ns\n" +
                "Maximum Time: %d ns",
                iterations, (iterations == 1 ? "" : "s"), averageTime, minTime, maxTime
            );
        }

        @Override
        public String toString() {
            return getFormattedReport();
        }
    }
}
