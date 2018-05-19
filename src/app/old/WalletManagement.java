package app.old;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyPair;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class WalletManagement extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7216283689316559485L;

	public WalletManagement() {
		buildGui();
	}

	JLabel lblSending = new JLabel("Wallet-Factory Station Sector");
	Dimension size = new Dimension(300, 40);
	JButton generateWallet = new JButton("Generate Wallet");
	JButton removeWallet = new JButton("Remove Wallet");
	JTextArea myPublicKeyLBL = new JTextArea("");
	JButton save = new JButton("Save this Wallet");
	JButton load = new JButton("Import a Wallet");
	static Wallet randomWallet = new Wallet();
	private JTextField filename = new JTextField(), dir = new JTextField();
	JButton showKey = new JButton("Show Keys");

	public void buildGui() {
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.insets = new Insets(10, 10, 10, 10);

		generateWallet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				randomWallet = new Wallet();
				checkWalletNull();
			}
		});

		removeWallet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				randomWallet = null;
				checkWalletNull();
			}
		});

		add(lblSending, c);
		c.gridy++;
		add(removeWallet, c);
		c.gridy++;
		add(generateWallet, c);
		c.gridy++;
		c.gridwidth = 5;
		add(showKey, c);
		c.gridy++;
		add(load, c);

		dir.setEditable(false);
		filename.setEditable(false);
		load.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImportKeyDialog d = new ImportKeyDialog();
				KeyPair kp = d.showDialog();
				if (kp != null) {
					if (randomWallet == null) {
						randomWallet = new Wallet();
					}
					randomWallet.setKeyPair(kp);
					checkWalletNull();
				}
			}
		});

		showKey.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShowKeysDialog dialog = new ShowKeysDialog(randomWallet.keyPair);
				dialog.setLocationRelativeTo(StartChain.programm);
				dialog.setVisible(true);
			}
		});
	}

	private void checkWalletNull() {
		if (randomWallet == null) {
			showKey.setEnabled(false);
			save.setEnabled(false);
		}
		if (randomWallet != null) {
			showKey.setEnabled(true);
			save.setEnabled(true);
		}
	}
}
