package com.example.alexpop.resizerlib.library.callables;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Callable;

/**
   Abstract class that defines a Callable with a HashMap <File , Boolean> that will be used as a basis for both copy and resize operations
 */
public abstract class WorkerTaskCallable implements Callable<HashMap<File, Boolean>> {

}
