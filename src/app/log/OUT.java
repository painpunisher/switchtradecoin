package app.log;

import java.text.SimpleDateFormat;
import java.util.Date;

import app.config.Cons;
import javafx.application.Platform;

public class OUT {
	
	public static void print(String msg){
		setStatus(msg);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		System.out.print("[" +sdf.format(new Date()) + "] " + msg);
		Log.logger.info("[" +sdf.format(new Date()) + "] " + msg);
		writeConsole("[" + sdf.format(new Date()) + "] " + msg);
	}
	
	public static void println(String msg){
		setStatus(msg);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		System.out.println("[PRINT][" + sdf.format(new Date()) + "] " + msg);
		Log.logger.info("[PRINT][" + sdf.format(new Date()) + "] " + msg);
		writeConsole("[PRINT][" + sdf.format(new Date()) + "] " + msg);
	}
	
	public static void DEBUG(String msg){
		setStatus(msg);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		if(Cons.DEBUGMODE){
			System.out.println("[DEBUG][" + sdf.format(new Date()) + "] " + msg);
			Log.logger.warning("[DEBUG][" + sdf.format(new Date()) + "] " + msg);
		}
		writeConsole("[DEBUG][" + sdf.format(new Date()) + "] " + msg);
	}
	
	public static void ERROR(String msg){
		setStatus("[ERROR] " + msg);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		System.err.println("[ERROR][" + sdf.format(new Date()) + "] " + msg);
		Log.logger.severe("[ERROR][" + sdf.format(new Date()) + "] " + msg);
		writeConsole("[ERROR][" + sdf.format(new Date()) + "] " + msg);
	}
	
	public static void ERROR(String msg, Exception e){
		setStatus("[ERROR] "+msg);
		writeConsole(msg);
		System.err.println("[ERROR]"+msg + " - " +e.getMessage());
		Log.logger.severe("[ERROR]"+msg + " - " +e.getMessage());
		if(Cons.DEBUGMODE){
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
//					WarningError.showError(e.getMessage());
					e.printStackTrace();
				}
			});
		}
	}
	
	private static void setStatus(String msg){
//		if(StartChain.programm != null){
//			if(!GO.mBuildingBlockchain){
//				StartChain.programm.progressBar.setString(msg);
//			}
//		}
	}
//	
//	static String collect = "start";
	private static void writeConsole(String msg){
//		if(collect.length()>1){
//			collect = collect + "" + msg + System.getProperty("line.separator");
//		}
//		if(StartChain.programm != null){
//			StartChain.programm.consolePanel.consoleTextField.append(collect);
//			collect = "";
//			StartChain.programm.consolePanel.consoleTextField.append(msg + System.getProperty("line.separator"));
//			StartChain.programm.consolePanel.consoleTextField.setCaretPosition(StartChain.programm.consolePanel.consoleTextField.getDocument().getLength());
//		}
	}
	
}
