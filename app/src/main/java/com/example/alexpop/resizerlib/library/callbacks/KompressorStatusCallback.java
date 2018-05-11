package com.example.alexpop.resizerlib.library.callbacks;

public interface KompressorStatusCallback {

    /**Invoked when library is busy processing files assigned in the queue
     */
    void onKompressorBusy();

    /**
      Invoked when the thread pools have no assigned tasks to them and can received new files
     */
    void onKompressorResourcesFree();

}
