package app.start.beta;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.apache.commons.io.FilenameUtils;

public class killmyself {
	private static void selfDestructWindowsJARFile() throws Exception
	{
	    String resourceName = "self-destruct.bat";
	    File scriptFile = File.createTempFile(FilenameUtils.getBaseName(resourceName), "." + FilenameUtils.getExtension(resourceName));

	    try (FileWriter fileWriter = new FileWriter(scriptFile);
	         PrintWriter printWriter = new PrintWriter(fileWriter))
	    {
	        printWriter.println("taskkill /F /IM \"java.exe\"");
	        printWriter.println("DEL /F \"" + ProgramDirectoryUtilities.getCurrentJARFilePath() + "\"");
	        printWriter.println("start /b \"\" cmd /c del \"%~f0\"&exit /b");
	    }

	    Desktop.getDesktop().open(scriptFile);
	}

	public static void selfDestructJARFile() throws Exception
	{
		String os = System.getProperty("os.name");
		
	    if (os.startsWith("Windows"))
	    {
	        selfDestructWindowsJARFile();
	    } else
	    {
	        // Unix does not lock the JAR file so we can just delete it
	        File directoryFilePath = ProgramDirectoryUtilities.getCurrentJARFilePath();
	        Files.delete(directoryFilePath.toPath());
	    }

	    System.exit(0);
	}
}
