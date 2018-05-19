package app.old;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.PlainDocument;

import app.config.Config;
import app.log.OUT;

class Settings extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8634100821050586383L;

	private JCheckBox mMiningMode = new JCheckBox("Mining-Mode");
	private JCheckBox mDebugMode = new JCheckBox("Debug-mode");
	private JCheckBox mAutoNodeDiscovery = new JCheckBox("Auto at Start");

	private JTextField mReceiverPort = new JTextField();
	
	
	
	private JTextField ip1 = new JTextField();
	private JTextField ip2 = new JTextField();
	private JTextField ip3 = new JTextField();
	private JTextField ip4 = new JTextField();
	private JTextField ip5 = new JTextField();
	private JTextField ip6 = new JTextField();
	private JTextField ip7 = new JTextField();
	private JTextField ip8 = new JTextField();
	
	private JTextField port1 = new JTextField();
	private JTextField port2 = new JTextField();
	private JTextField port3 = new JTextField();
	private JTextField port4 = new JTextField();
	private JTextField port5 = new JTextField();
	private JTextField port6 = new JTextField();
	private JTextField port7 = new JTextField();
	private JTextField port8 = new JTextField();

	private JButton save = new JButton("Save");
	private JButton cancel = new JButton("Cancel");
	
	public Settings() {
        setTitle("Settings");
        setSize(500,500);
		buildGUI();
		setModal(true);
	}

	private void buildGUI() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(5, 5, 5, 5);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.BOTH;
		
		add(new JLabel("Mining Active:"),c);
		c.gridx++;
		add(mMiningMode, c);
		c.gridx--;
		c.gridy++;
		
		add(new JLabel("Debug Mode:"),c);
		c.gridx++;
		add(mDebugMode, c);
		c.gridx--;
		c.gridy++;
		
		add(new JLabel("Node Discovery:"),c);
		c.gridx++;
		add(mAutoNodeDiscovery, c);
		c.gridx--;
		c.gridy++;
		
		c.fill = GridBagConstraints.NONE;		
		setDimensionPORT(mReceiverPort);
		add(new JLabel("Receiver Port:"),c);
		c.gridx++;
		add(mReceiverPort, c);
		c.gridx--;
		c.gridy++;
		c.fill = GridBagConstraints.BOTH;

		addPeer("Peer-1:",ip1,"Port-1:",port1,c);
		addPeer("Peer-2:",ip2,"Port-2:",port2,c);
		addPeer("Peer-3:",ip3,"Port-3:",port3,c);
		addPeer("Peer-4:",ip4,"Port-4:",port4,c);
		addPeer("Peer-5:",ip5,"Port-5:",port5,c);
		addPeer("Peer-6:",ip6,"Port-6:",port6,c);
		addPeer("Peer-7:",ip7,"Port-7:",port7,c);
		addPeer("Peer-8:",ip8,"Port-8:",port8,c);
		
		JPanel btns = new JPanel();
		btns.add(save);
		btns.add(cancel);
		c.gridwidth = 5;
		add(btns,c);
		
		setDimensionIP(ip1);
		setDimensionIP(ip2);
		setDimensionIP(ip3);
		setDimensionIP(ip4);
		setDimensionIP(ip5);
		setDimensionIP(ip6);
		setDimensionIP(ip7);
		setDimensionIP(ip8);
		
		setDimensionPORT(port1);
		setDimensionPORT(port2);
		setDimensionPORT(port3);
		setDimensionPORT(port4);
		setDimensionPORT(port5);
		setDimensionPORT(port6);
		setDimensionPORT(port7);
		setDimensionPORT(port8);
		
		
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveConfig();
				OUT.println("CONFIG SAVED!");
				Settings.this.dispose();
			}
		});
		
		cancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Settings.this.dispose();
			}
		});
		loadConfig();
	}
	
	private void addPeer(String lblip, JTextField ip, String lblport, JTextField port,GridBagConstraints pc){
		add(new JLabel(lblip),pc);
		pc.gridx++;
		add(ip, pc);
		pc.gridx++;
		add(new JLabel(lblport),pc);
		pc.gridx++;
		add(port, pc);
		pc.gridy++;
		
		pc.gridx=0;
	}
	
	
	
	public void setDimensionIP(JTextField txt){
		txt.setMinimumSize(new Dimension(250, 25));
		txt.setPreferredSize(new Dimension(250, 25));
		txt.setMaximumSize(new Dimension(250, 25));
	}
	public void setDimensionPORT(JTextField txt){
		txt.setMinimumSize(new Dimension(50, 25));
		txt.setPreferredSize(new Dimension(50, 25));
		txt.setMaximumSize(new Dimension(50, 25));
		
		PlainDocument doc = (PlainDocument) txt.getDocument();
	      doc.setDocumentFilter(new IntegerOnly());
	}

	private String getText(final JTextField txt){
		if(txt.getText().length()>0){
			return txt.getText();
		} else {
			return "0";
		}
	}
	
	private void saveConfig() {
		Config.INSTANCE.setProperty("peer1", ip1.getText());
		Config.INSTANCE.setProperty("peer2", ip2.getText());
		Config.INSTANCE.setProperty("peer3", ip3.getText());
		Config.INSTANCE.setProperty("peer4", ip4.getText());
		Config.INSTANCE.setProperty("peer5", ip5.getText());
		Config.INSTANCE.setProperty("peer6", ip6.getText());
		Config.INSTANCE.setProperty("peer7", ip7.getText());
		Config.INSTANCE.setProperty("peer8", ip8.getText());
		
		Config.INSTANCE.setProperty("port1", getText(port1));
		Config.INSTANCE.setProperty("port2", getText(port2));
		Config.INSTANCE.setProperty("port3", getText(port3));
		Config.INSTANCE.setProperty("port4", getText(port4));
		Config.INSTANCE.setProperty("port5", getText(port5));
		Config.INSTANCE.setProperty("port6", getText(port6));
		Config.INSTANCE.setProperty("port7", getText(port7));
		Config.INSTANCE.setProperty("port8", getText(port8));
		
		Config.INSTANCE.setProperty("RECEIVERPORT", getText(mReceiverPort));
		
		Config.INSTANCE.setProperty("MININGMODE", mMiningMode.isSelected() + "");
		Config.INSTANCE.setProperty("DEBUGMODE", mDebugMode.isSelected() + "");
		Config.INSTANCE.setProperty("NODEDISCOVERY", mAutoNodeDiscovery.isSelected() + "");
		
		Config.saveProperties();
	}

	private void loadConfig() {
		Config.loadProperties();
		
		ip1.setText(Config.INSTANCE.getProperty("peer1"));
		ip2.setText(Config.INSTANCE.getProperty("peer2"));
		ip3.setText(Config.INSTANCE.getProperty("peer3"));
		ip4.setText(Config.INSTANCE.getProperty("peer4"));
		ip5.setText(Config.INSTANCE.getProperty("peer5"));
		ip6.setText(Config.INSTANCE.getProperty("peer6"));
		ip7.setText(Config.INSTANCE.getProperty("peer7"));
		ip8.setText(Config.INSTANCE.getProperty("peer8"));
		
		port1.setText(Config.INSTANCE.getProperty("port1"));
		port2.setText(Config.INSTANCE.getProperty("port2"));
		port3.setText(Config.INSTANCE.getProperty("port3"));
		port4.setText(Config.INSTANCE.getProperty("port4"));
		port5.setText(Config.INSTANCE.getProperty("port5"));
		port6.setText(Config.INSTANCE.getProperty("port6"));
		port7.setText(Config.INSTANCE.getProperty("port7"));
		port8.setText(Config.INSTANCE.getProperty("port8"));
		
		mReceiverPort.setText(Config.INSTANCE.getProperty("RECEIVERPORT"));
		
		mMiningMode.setSelected(Boolean.parseBoolean(Config.INSTANCE.getProperty("MININGMODE")));
		mDebugMode.setSelected(Boolean.parseBoolean(Config.INSTANCE.getProperty("DEBUGMODE")));
		mAutoNodeDiscovery.setSelected(Boolean.parseBoolean(Config.INSTANCE.getProperty("NODEDISCOVERY")));
	}

}

