package app.gui.msg;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public class WarningError {

	public static void showInfo(String information) {

		Alert alert = new Alert(AlertType.INFORMATION, information, ButtonType.OK);
		alert.showAndWait();
		// JOptionPane.showMessageDialog(StartChain.programm, information,
		// "Information",
		// JOptionPane.INFORMATION_MESSAGE);
	}

	// JOptionPane.showMessageDialog(frame, "Eggs are not supposed to be
	// green.");
	public static void showError(String errmsg) {
		// TODO Auto-generated method stub
		Alert alert = new Alert(AlertType.ERROR, errmsg, ButtonType.OK);
		alert.showAndWait();

		// JOptionPane.showMessageDialog(StartChain.programm, errmsg,
		// "Warning/Error",
		// JOptionPane.ERROR_MESSAGE);
	}

}
