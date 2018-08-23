package com.example.alexpop.resizerlib.kompressorLib.threadmanager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ThreadPoolCreator {

    private final String TAG = ThreadPoolCreator.class.getSimpleName();

    /**
     * Returns an Executor Service instance
     * with the number of available cores + 1 as a maximum number of threads / pool
     */
    public static ExecutorService createWorkerExecutorService() {
        int availableCpuCores = Runtime.getRuntime()
                .availableProcessors();
        int NUMBER_OF_CORE_THREADS = availableCpuCores;
        int NUMBER_OF_MAX_THREADS = availableCpuCores + 1;
        TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        int KEEP_ALIVE_TIME = 6000;

        return new ThreadPoolExecutor(NUMBER_OF_CORE_THREADS, NUMBER_OF_MAX_THREADS, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<>());
    }

    /**
     * Returns a single threaded executor service
     */
    public static ExecutorService createMainExecutorService() {
        return Executors.newSingleThreadExecutor();
    }
}
