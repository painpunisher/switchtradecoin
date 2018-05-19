package app.start.gui;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import app.config.FilePathCollection;
import app.log.OUT;
import app.old.StartChain;

public class pdfstarter {
	public pdfstarter(){
		  if (Desktop.isDesktopSupported())   
	        {   
	            InputStream jarPdf = StartChain.class.getResourceAsStream("/readme.pdf");

	            try {
	            	boolean success = (new File(FilePathCollection.HELP_PATH)).mkdirs();
	            	System.out.println(success);
	                File pdfTemp = new File(FilePathCollection.HELP_FILE);
	                // Extraction du PDF qui se situe dans l'archive
	                FileOutputStream fos = new FileOutputStream(pdfTemp);
	                while (jarPdf.available() > 0) {
	                      fos.write(jarPdf.read());
	                }   // while (pdfInJar.available() > 0)
	                fos.close();
	                // Ouverture du PDF
	                Desktop.getDesktop().open(pdfTemp);
	            }   // try

	            catch (IOException e) {
	                OUT.ERROR("Opening readme.pdf caused an error!", e);
	            }   // catch (IOException e)
	        }
		  
	}
}
