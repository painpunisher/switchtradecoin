package app.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import app.config.FilePathCollection;

public class Log {
	public static Logger logger;
	public static void init(){
	    logger = Logger.getLogger("logger");  
	    logger.setUseParentHandlers(false);
	    FileHandler fh;  
	    boolean success = false;
	    try {  
	        // This block configure the logger with handler and formatter  
	    	success = (new File(FilePathCollection.LOG_PATH)).mkdirs();
	        fh = new FileHandler(FilePathCollection.LOG_FILE);
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  
	        // the following statement is used to log any messages  
	        logger.info("### LOGGER STARTED ###");  
	    } catch (SecurityException e) { 
			if(!success){
				OUT.ERROR("Cannot create log Path, check logfiles!", e);
			}
	    } catch (IOException e) {  
			if(!success){
				OUT.ERROR("Cannot create log Path, check logfiles!", e);
			}
	    }  
	}
}
