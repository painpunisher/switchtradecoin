package app.start.beta;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JLabel;

import app.log.OUT;

public class betaphase {
	
	public static boolean checkBetaPhaseOver(){
		 try {
			if (new SimpleDateFormat("dd/MM/yyyy").parse("30/05/2018").after(new Date())) {
			      return false;
			 }
		} catch (ParseException e) {
			OUT.ERROR("", e);
		}
//		 WarningError.showError("Betaphase over! See you Next time!");
		 JDialog dialog = new JDialog();
		 JLabel label = new JLabel("Betaphase over! See you Next time!...");
		 dialog.setLocationRelativeTo(null);
		 dialog.setTitle("Betaphase over! See you Next time!...");
		 dialog.add(label);
		 dialog.pack();
		 dialog.setModal(true);
		 dialog.setVisible(true);
		 return true;
	}
	
}
