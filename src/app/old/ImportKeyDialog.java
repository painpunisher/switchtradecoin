package app.old;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import app.config.Cons;
import app.gui.msg.WarningError;
import app.log.OUT;
import app.util.KeyLoader;

class ImportKeyDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1567972006738931358L;

	private JButton privateKeyLoader = new JButton("Select Private Key");
	private JButton publicKeyLoader = new JButton("Select Public Key");
	private JButton importer = new JButton("IMPORT");
	private JButton cancel = new JButton("CANCEL");
	private JPanel main = new JPanel();

	private String privateKeyPath;
	private String publicKeyPath;

	private KeyPair loadedKeyPair = null;

	public ImportKeyDialog() {
		build();
		add(main);
		setTitle("Import Wallet");
		setSize(200, 200);
		setModal(true);
		pack();
		setLocationRelativeTo(null);
	}

	KeyPair showDialog() {
		setVisible(true);
		return loadedKeyPair;
	}

	private void build() {
		privateKeyLoader.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser c = new JFileChooser();
				// Demonstrate "Open" dialog:
				int rVal = c.showOpenDialog(ImportKeyDialog.this);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					String file = c.getSelectedFile().getName();
					String directory = c.getCurrentDirectory().toString();
					privateKeyPath = directory + File.separator + file;
				}
				if (rVal == JFileChooser.CANCEL_OPTION) {
				}
			}
		});

		publicKeyLoader.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser c = new JFileChooser();
				// Demonstrate "Open" dialog:
				int rVal = c.showOpenDialog(ImportKeyDialog.this);
				if (rVal == JFileChooser.APPROVE_OPTION) {
					String file = c.getSelectedFile().getName();
					String directory = c.getCurrentDirectory().toString();
					publicKeyPath = directory + File.separator + file;
				}
				if (rVal == JFileChooser.CANCEL_OPTION) {
				}
			}
		});

		importer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					loadedKeyPair = (KeyLoader.LoadKeyPair(privateKeyPath, publicKeyPath, Cons.ALGORITHM, false));
					cancel.doClick();
				} catch (NoSuchAlgorithmException e1) {
					WarningError.showError("Key-Pair could not be loaded!");
					OUT.ERROR("Keypair Loading ERROR: ", e1);
				} catch (InvalidKeySpecException e1) {
					WarningError.showError("Key-Pair could not be loaded!");
					OUT.ERROR("Keypair Loading ERROR: ", e1);
				} catch (IOException e1) {
					WarningError.showError("Key-Pair could not be loaded!");
					OUT.ERROR("Keypair Loading ERROR: ", e1);
				}
			}
		});

		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ImportKeyDialog.this.dispose();
			}
		});
		main.add(privateKeyLoader);
		main.add(publicKeyLoader);
		main.add(importer);
		main.add(cancel);
	}
}
