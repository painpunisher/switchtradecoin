package app.old;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.security.KeyPair;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicProgressBarUI;

import app.config.Cons;
import app.core.contract.ContractStatusHandler;
import app.log.OUT;
import app.net.NodeDiscovery;
import app.start.util.GO;

public class MainFrame extends JFrame implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4321930765260890728L;
//	private Container container;
	
	private JMenuBar menuBar;
	private JMenu data;
	private JMenu settings;
	private JMenu help;
	private JMenuItem importWallet;
	private JMenuItem exit;
	private JMenuItem subSettings;
	private JMenuItem nodeDiscovery;
	private JMenuItem exportGsonChain;
	private JMenuItem exportMongoDB;
	private JMenuItem processSmartContract;
	//exportMongoDB
	private JMenuItem faq;
	private JMenuItem about;
	private JLabel status;

	public JProgressBar progressBar = new JProgressBar(0, 100);
	
    private JPanel overview = new JPanel();
    private JPanel send = new JPanel();
    private JPanel walletManagement = new JPanel();
//    JPanel transactions = new JPanel();
//    JPanel consoleoutput = new JPanel();
//    JPanel globalcontracts = new JPanel();
//    JPanel mycontracts = new JPanel();
//    JPanel miningpan = new JPanel();
//    Send sendPanel;
//    Balance bal;
//    TransactionsOverView txPanel;
//    GlobalContractView globalcontractView;
//    MyContractView mycontractView;
//    private ConsoleOutputView consolePanel;
//    MiningPanel minePanel;
    private JTabbedPane tabpane = new JTabbedPane
        (JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );
    
	public MainFrame() {
		super("TrippleHexaCoin Core - Wallet" + "" + " - Port: " + Cons.RECEIVERPORT);
//		applikation = new JFrame();
		this.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                	closeOperation();
                }
            });
		
//		container = this.getContentPane();
		
        overview.setBackground(Color.WHITE);
        send.setBackground(Color.WHITE);
        walletManagement.setBackground(Color.WHITE);
//        transactions.setBackground(Color.WHITE);
//        consoleoutput.setBackground(Color.WHITE);
//        globalcontracts.setBackground(Color.WHITE);
//        mycontracts.setBackground(Color.WHITE);
        
//        miningpan.setBackground(Color.WHITE);
		
		menuBar = new JMenuBar();
		
		data = new JMenu("File");
		settings = new JMenu("Settings");
		help = new JMenu("Help");
		
		importWallet = new JMenuItem("Import Wallet");
		importWallet.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ImportKeyDialog dlg = new ImportKeyDialog();
				KeyPair kp = dlg.showDialog();
				if(kp != null){
					Wallet clientWalletLocal = new Wallet();
					GO.clientWalletLocal = clientWalletLocal;
					GO.clientWalletLocal.setKeyPair(kp);
//					bal.refresh.doClick();
////					txPanel.refresh.doClick();
//					mycontractView.refresh.doClick();
//					globalcontractView.refresh.doClick();
				}
			}
		});
		exit = new JMenuItem("Quit");
		exit.addActionListener(this);
		subSettings = new JMenuItem("Settings");
		subSettings.addActionListener(this);
		nodeDiscovery = new JMenuItem("Node Discovery");
		nodeDiscovery.addActionListener(this);
		exportGsonChain= new JMenuItem("Export JSON");
		exportGsonChain.addActionListener(this);
		exportMongoDB= new JMenuItem("Export MongoDB");
		exportMongoDB.addActionListener(this);
		processSmartContract = new JMenuItem("Process SmartContract");
		processSmartContract.addActionListener(this);
		//exportMongoDB
		faq = new JMenuItem("Manual PDF");
		faq.addActionListener(this);
		about = new JMenuItem("About Us");
		about.addActionListener(this);
		
		menuBar.add(data);
		menuBar.add(settings);
		menuBar.add(help);
		
		data.add(importWallet);
		data.add(exit);
		settings.add(subSettings);
//		settings.add(exportGsonChain);
//		settings.add(exportMongoDB);
		//exportMongoDB
		settings.add(nodeDiscovery);
		settings.add(processSmartContract);
		help.add(faq);
		help.add(about);

//		bal = new Balance("somerandomadress");
//		overview.add(bal);
//		
//		sendPanel = new Send();
//		send.add(sendPanel);
		
//		txPanel = new TransactionsOverView();
//		transactions.add(txPanel);
		
//		consolePanel = new ConsoleOutputView();
//		consoleoutput.add(consolePanel);
		
//		globalcontractView = new GlobalContractView();
//		globalcontracts.add(globalcontractView);
//		
//		mycontractView = new MyContractView();
//		mycontracts.add(mycontractView);
//		
////		minePanel = new MiningPanel();
////		miningpan.add(minePanel);
//		
//		WalletManagement walletPanel = new WalletManagement();
//		walletManagement.add(walletPanel);
		
//		tabpane.addTab("Mining", miningpan);
        tabpane.addTab("OverView", overview);
        tabpane.addTab("Send", send);
        tabpane.addTab("Wallet Management", walletManagement);
//        tabpane.addTab("Transactions", transactions);
//        tabpane.addTab("Console View", consoleoutput);
//        tabpane.addTab("Global Contracts", globalcontracts);
//        tabpane.addTab("My Contracts", mycontracts);
        //consoleoutput
		
        progressBar.setStringPainted(true);
        status = new JLabel("Starting");
        
		this.add(menuBar, BorderLayout.NORTH);
		this.add(new JScrollPane(tabpane), BorderLayout.CENTER);
		JPanel south = new JPanel(new BorderLayout());
		south.setBackground(Color.WHITE);
		Dimension SIZEPROGRESS = new Dimension(400, 25);
		Dimension STATUS = new Dimension(180, 25);
		Font f = new Font( Font.SERIF, Font.PLAIN, 9 );
		progressBar.setFont(f);
		progressBar.setUI(new BasicProgressBarUI() {
		      protected Color getSelectionBackground() { return Color.black; }
		      protected Color getSelectionForeground() { return Color.white; }
		    });
		progressBar.setMinimumSize(SIZEPROGRESS);
		progressBar.setMaximumSize(SIZEPROGRESS);
		progressBar.setPreferredSize(SIZEPROGRESS);
		status.setMinimumSize(STATUS);
		status.setMaximumSize(STATUS);
		status.setPreferredSize(STATUS);
		status.setHorizontalAlignment(SwingConstants.LEFT);
		south.add(progressBar,BorderLayout.NORTH);
//		south.add(status, BorderLayout.SOUTH);
		this.add(south, BorderLayout.SOUTH);
		
		this.setSize(600, 610);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		initPanels();
//		bal.calculateBalance();
	}
	
	private void closeOperation(){
        int i=JOptionPane.showConfirmDialog(StartChain.programm, "Are you sure you want to Quit?");
        if(i==0){
        	GO.closeApplication();
        }
	}
	
	private void initPanels(){
//		bal.calculateBalance();
//		txPanel.refresh.doClick();
	}
	
	public void actionPerformed(ActionEvent object) {
		if (object.getSource() == importWallet){
			OUT.DEBUG("open clicked");
		}
		if (object.getSource() == exit){
			closeOperation();
		}
		if (object.getSource() == subSettings){
			Settings dialog = new Settings();
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		}
		if (object.getSource() == nodeDiscovery){
			NodeDiscovery.startRefreshNodes();
		}
		if(object.getSource() == exportGsonChain){
		}
		if(object.getSource() == exportMongoDB){
//			ExportToMongoDB.init();
//			ExportToMongoDB.export(GO.BLOCKCHAIN.get(GO.BLOCKCHAIN.size()-1));
		}
		if(object.getSource() == processSmartContract){
			ContractStatusHandler.ProofContracts();
//			mycontractView.refresh.doClick();
//			globalcontractView.refresh.doClick();
		}
		if (object.getSource() == faq){
//			new pdfstarter();
			OUT.DEBUG("faq clicked");
		}
		if (object.getSource() == about){
//			AboutDialog dialog = new AboutDialog();
//			dialog.setVisible(true);
			OUT.DEBUG("about us clicked");
		}
	}
}
