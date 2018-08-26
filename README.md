![giphy](https://user-images.githubusercontent.com/3145845/39960197-0d88e4fa-5627-11e8-8c0a-ff8c9ecf289a.gif)


                                Kompressor Beta 0.2 
                              
            Batch image resize / copy / compress / resize & compress Android Java library , currently in beta phase, designed for mass manipulation of an large number of image files.
           
            # Usage
            
            # Returns the singleton instance of the library.
            Kompressor kompressor = Kompressor.get(); 
            
            # Create KompressorParameters object for passing the files to be processed, using the provided builder class
            
            KompressorParameters parameters = KompressorParameters.MainTaskParametersBuilder()
                .setImageFiles(imageFileList)              // An List<File> object containing the images which need to be processsed
                .setTaskType(TaskType.TASK_COPY_TO_DIRECTORY) // A TaskType object
                .setToCopyDestinationDirectory(toCopyDestinationDirectory) // Optional Param , the directory wehere the files will be copied
                .setCompressionRatio(int compressionRatio) // Optional Param,a maximum compression ratio (must be a compression ratio between 0-100, expressed as an int variable).
                .setMaximumResizeWidth(int maximumResizeWidth) // Optional Param, maximum height in pixels which will be used when resizing the image files.
                .createMainTaskParameters(); // Creates the parameters object
            
         
  
            # Start the task by calling the publi startTask method and assigining the previously created KompressorParameters object
            kompressor.startTask(parameters);

            # Gradle import release 
            Will be relased as a gradle package once it has been fully tested.
                
                
              
       
        
