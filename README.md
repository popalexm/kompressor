                                        Kompressor Beta 0.1 
                                  
                Multithreaded batch image resize / copy / compress Android Java library , currently in beta phase
                  
                ![preview](https://media.giphy.com/media/oOwpZgz8uVn3Q2ZkbO/giphy.gif)

                # Usage
                
                Returns singleton instance of the library 
                Kompressor kompressor = Kompressor.get(); 
                
                Assign a list of images (File objects) that need to either be moved, or resized
                
                kompressor.loadResources(List<File> files); 
                
                Assign callbacks for either the final image list results (the library returns a list of File objects for both                             succesfully copied/ resized / compresssed images, and the failed one) 
                or single image copy / resize / compression callbacks for each image as it is processed
                
                kompressor.withResizeCallback(this);
                kompressor.withSingleImageResizeCallback(this);
                
                Assign a maximum height resolution or a maximum compression ratio (0-100)
                
                kompressor.withCompressionRatio(mCompressionRatio);
                kompressor.withMaxSize(mMaxResizeHeight);
                
                Assign a task type and start the task
                
                kompressor.startTask(TaskType.TASK_RESIZE_AND_COMPRESS_TO_RATIO);
                
                
              
       
        
