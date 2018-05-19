package app.old;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyPair;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import app.util.StringUtil;

class ShowKeysDialog extends JDialog{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9098203819686288262L;

	private JTextField publickey = new JTextField();
	private JTextField privatekey = new JTextField();
	
	private JButton showPrivateKey = new JButton("Show Private-Key");
	
	private JButton save = new JButton("Save");
	private JButton cancel = new JButton("Cancel");
	
	private JTextField filename = new JTextField(), dir = new JTextField();
	
	private KeyPair mKP;
	
	
	public ShowKeysDialog(final KeyPair kp) {
		mKP = kp;
        setTitle("Settings");
        setSize(500,500);
		buildGUI();
		setModal(true);
	}
	
	private void buildGUI() {
		setLayout(new GridBagLayout());
		getContentPane().setBackground(Color.WHITE);
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.BOTH;
		
		publickey.setText(StringUtil.getStringFromKey(mKP.getPublic()));
		publickey.setEditable(false);
		privatekey.setEditable(false);
		
		showPrivateKey.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				privatekey.setText(StringUtil.getStringFromKey(mKP.getPrivate()));
			}
		});
		
		add(new JLabel("Click to Show Private-Key:"),c);
		c.gridx++;
		add(showPrivateKey, c);
		c.gridx--;
		c.gridy++;

		add(new JLabel("Public-Key:"),c);
		c.gridx++;
		add(publickey, c);
		c.gridx--;
		c.gridy++;

		add(new JLabel("Private-Key:"),c);
		c.gridx++;
		add(privatekey, c);
		c.gridx--;
		c.gridy++;
		
		JPanel btns = new JPanel();
		btns.setBackground(Color.WHITE);
		btns.add(save);
		btns.add(cancel);
		c.gridwidth = 2;
		add(btns,c);
		
		save.addActionListener(new SaveKeyListener(ShowKeysDialog.this, filename, dir, mKP));
		
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShowKeysDialog.this.dispose();
			}
		});
		
		setDimension(publickey);
		setDimension(privatekey);
		
	}
	
	public void setDimension(JTextField txt){
		txt.setMinimumSize(new Dimension(300, 25));
		txt.setPreferredSize(new Dimension(300, 25));
		txt.setMaximumSize(new Dimension(300, 25));
	}
}
