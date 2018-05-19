package app.old;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

import app.util.KeySaver;

class SaveKeyListener implements ActionListener{

	private Component mComp;
	private JTextField filename;
	private JTextField dir;
	private KeyPair mKP;
	public SaveKeyListener(Component comp, JTextField fn, JTextField dirr, KeyPair kp ) {
		mComp = comp;
		filename = fn;
		dir = dirr;
		mKP = kp;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JFileChooser c = new JFileChooser();
		// Demonstrate "Save" dialog:
		c.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int rVal = c.showSaveDialog(mComp);
		if (rVal == JFileChooser.APPROVE_OPTION) {
			String file = c.getSelectedFile().getName();
			String directory = c.getCurrentDirectory().toString();
			filename.setText(file);
			dir.setText(directory);
			saveMyWallet(directory + File.separator + file);
		}
		if (rVal == JFileChooser.CANCEL_OPTION) {
			filename.setText("You pressed cancel");
			dir.setText("");
		}
	}

	private void saveMyWallet(String path) {
		try {
			KeySaver.SaveKeyPair(path, mKP, false);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
}
