![giphy](https://user-images.githubusercontent.com/3145845/39960197-0d88e4fa-5627-11e8-8c0a-ff8c9ecf289a.gif)


                                Kompressor Beta 0.4
                              
            Batch image resize / copy / compress / resize & compress Android Java library , currently in beta phase, designed for mass manipulation of an large number of image files.
           
            # Usage
            
            # Returns the singleton instance of the library.
            Kompressor kompressor = Kompressor.get(); 
            
            # Create KompressorParameters object for passing the files to be processed, using the provided builder class
            
            KompressorParameters parameters = KompressorParameters.MainTaskParametersBuilder()
             /* An List<File> object containing the images which need to be processsed */
             .setImageFiles(imageFileList)              
             
             /* A TaskType object defining the action to be taken upon the image files */
             .setTaskType(TaskType.TASK_COPY_TO_DIRECTORY) 
             
             /* Optional parameter , only used in conjuction with TaskType.COPY_TO_DIRECTORY , the directory where the images files 
             will be copied */
             .setToCopyDestinationDirectory(toCopyDestinationDirectory) 
             
              /* Optional Param, the maximum compression ratio (must be a compression ratio between 0-100, 
              expressed as an integer variable) */
             .setCompressionRatio(int compressionRatio)
             
             /* Optional Param, maximum height in pixels which will be used when resizing the image files */
             .setMaximumResizeWidth(int maximumResizeWidth) 
             
             /* Creates the parameters object */
             .createMainTaskParameters(); 
            
            # Start the task by calling the public startTask() method and assigining the previously created KompressorParameters object
            kompressor.startTask(parameters);
            
            # Callbacks from the library 
            
            /* Set of callbacks that returns the list of sucessfully resized files, and the list of failed ones
            kompressor.withBatchResizeCallbacks(EntireBatchResizeCallback uiCallback);
       
            /* Set of callbacks that returns the list of sucessfully copied files, and the list of failed ones
            kompressor.withBatchCopyCallbacks(@NonNull EntireBatchCopyCallback uiCallback) 
            
            /* Callbacks for each individual file, called upon once each file is copied to the destination directory */
            kompressor.withSingleItemCopyCallbacks(@NonNull IndividualItemCopyCallback uiCallback) 

            /* Same as above, but for each individual resize operation */
            kompressor.withSingleItemResizeCallbacks(@NonNull IndividualItemResizeCallback uiCallback) 
           
            # Gradle import release 
            Will be relased as a gradle package once it has been fully tested.
                
                
              
       
        
