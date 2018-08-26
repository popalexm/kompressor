![giphy](https://user-images.githubusercontent.com/3145845/39960197-0d88e4fa-5627-11e8-8c0a-ff8c9ecf289a.gif)


                                Kompressor Beta 0.2 
                              
            Batch image resize / copy / compress / resize & compress Android Java library , currently in beta phase, designed for mass manipulation of an large number of image files.
           
            # Usage
            
            # Returns singleton instance of the library.
            Kompressor kompressor = Kompressor.get(); 
            
            # Create KompressorParameters object for passing the files to be processed, using the provided builder
            
             KompressorParameters.MainTaskParametersBuilder()
                .setImageFiles(imageFileList)              // An List<File> object containing the images which need to be processsed
                .setTaskType(TaskType.TASK_COPY_TO_DIRECTORY) // A TaskType object
                .setToCopyDestinationDirectory(toCopyDestinationDirectory) // Optional Param , the directory wehere the files will be copied
                .setCompressionRatio(int compressionRatio) // Optional Param, the compression ratio for files that need to be compressed , values from 0 to 100.
                .setMaximumResizeWidth(int maximumResizeWidth) // Optional Param, Maximum Height which will be used when resizing the image files
                .createMainTaskParameters(); // Creates the parameters object
            
         
            kompressor.loadResources(List<File> imgFiles); 
            
            # Assign callbacks for either the final image list results (the library returns a list of File objects for both                             succesfully copied/ resized / compresssed images, and the failed one) or single image copy / resize / compression callbacks for each image as it is processed.
            kompressor.withResizeCallback(this);
            kompressor.withSingleImageResizeCallback(this);
            
            # Assign a maximum height resolution (value expressed as an int variable, corresponding to the desired maximum height in pixels of the desired resulted images), or a maximum compression ratio (must be a compression ratio between 0-100, expressed as an int variable).
            kompressor.withCompressionRatio(90);
            kompressor.withMaxSize(720;
            
            # Start the task by calling the publi startTask method and assigining a TaskType, there are 4 currently available in the library     
            kompressor.startTask(TaskType.TASK_RESIZE_AND_COMPRESS_TO_RATIO);

            # Gradle import release 
            Will be relased as a gradle package once it has been fully tested.
                
                
              
       
        
