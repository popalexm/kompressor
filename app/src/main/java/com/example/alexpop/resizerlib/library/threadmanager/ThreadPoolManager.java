package com.example.alexpop.resizerlib.library.threadmanager;

import android.util.Log;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.example.alexpop.resizerlib.library.threadmanager.ThreadPoolFields.KEEP_ALIVE_TIME;
import static com.example.alexpop.resizerlib.library.threadmanager.ThreadPoolFields.KEEP_ALIVE_TIME_UNIT;
import static com.example.alexpop.resizerlib.library.threadmanager.ThreadPoolFields.NUMBER_OF_CORE_THREADS;
import static com.example.alexpop.resizerlib.library.threadmanager.ThreadPoolFields.NUMBER_OF_MAX_THREADS;

public class ThreadPoolManager {

    private String TAG =  ThreadPoolManager.class.getSimpleName();

    private static ThreadPoolManager mInstance;

    private ThreadPoolManager(){}

    public static synchronized ThreadPoolManager getInstance(){
        if(mInstance == null){
            mInstance = new ThreadPoolManager();
        }
        return mInstance;
    }

    /** Returns an Executor Service instance
     * with the number of available cores + 1 as a maximum number of threads / pool
     */
    public ExecutorService createWorkerExecutorService() {
        int availableCpuCores = Runtime.getRuntime().availableProcessors();
        Log.i(TAG , "Found " + availableCpuCores + " available cpu cores for the thread pool");
        Log.d(TAG , new Date() + " -> Creating worker pool executor thread instance");
        NUMBER_OF_CORE_THREADS = availableCpuCores;
        NUMBER_OF_MAX_THREADS = availableCpuCores + 1;
        KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        return new ThreadPoolExecutor(
                NUMBER_OF_CORE_THREADS,
                NUMBER_OF_MAX_THREADS,
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                new LinkedBlockingQueue<>());
    }

    /** Returns a single threaded executor service
     */
    public ExecutorService createMainExecutorService() {
        Log.d(TAG , new Date() + " -> Creating main executor thread instance");
        return Executors.newSingleThreadExecutor();
    }
}
