                                        Kompressor Beta 0.1 
                                  
                Multithreaded batch image resize / copy / compress Android Java library , currently in beta phase

                # Usage
                // Returns singleton instance of the library 
                Kompressor kompressor = Kompressor.get(); 
                
                // Assign a list of File object that need to either be moved or resized
                kompressor.loadResources(List<File> files); 
                
                // Asign callbacks for either final list or single image copy / resize / compression 
                kompressor.withResizeCallback(this);
                kompressor.withSingleImageResizeCallback(this);
                
                //* Assign a maximum height resolution or a maximum compression ratio (0-100)
                kompressor.withCompressionRatio(mCompressionRatio);
                kompressor.withMaxSize(mMaxResizeHeight);
                
                // * Assign a task type
                kompressor.startTask(TaskType.TASK_RESIZE_AND_COMPRESS_TO_RATIO);
                
              ![Kompressor](https://user-images.githubusercontent.com/3145845/39837998-b5668d6a-53e0-11e8-87f4-6ec3622ed4e7.gif)
