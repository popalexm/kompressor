package com.example.alexpop.resizerlib.kompressorLib.tasks;

import android.util.Pair;

import java.io.File;
import java.util.concurrent.Callable;

/**
 * Abstract class that defines a Callable with a Pair<<File , Boolean> that will be used as a basis for both copy and resize operations
 */
abstract class BaseWorkerTaskCallable implements Callable<Pair<File, Boolean>> {

}
