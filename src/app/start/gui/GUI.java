package app.start.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ResourceBundle;

import app.config.Cons;
import app.core.contract.ContractStatusHandler;
import app.log.OUT;
import app.model.contract.SmartContract;
import app.model.transaction.Transaction;
import app.net.NetUtil;
import app.net.NodeDiscovery;
import app.net.NodeRegister;
import app.old.StartChain;
import app.start.Main;
import app.start.util.GO;
import app.util.KeyLoader;
import app.util.StringUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GUI implements Initializable{

	@FXML private Button accountdetails;
	@FXML private TextArea privatekeytextarea;
	@FXML private TextArea publickeytextarea;
	static Stage secondStage;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (accountdetails != null) {
			InputStream input = StartChain.class.getResourceAsStream("/suitcase.png");
			Image image = new Image(input);
			ImageView imageView = new ImageView(image);
			imageView.setFitHeight(16);
			imageView.setFitWidth(16);
			accountdetails.setGraphic(imageView);
		}
		
		if(publickeytextarea!=null){
			publickeytextarea.setText(StringUtil.getStringFromKey(GO.clientWalletLocal.publicKey));
			privatekeytextarea.setText(StringUtil.getStringFromKey(GO.clientWalletLocal.privateKey));
			try {
				OUT.DEBUG(StringUtil.getStringFromKey(StringUtil.getPublicKeyFromPrivateKey(GO.clientWalletLocal.privateKey)));
			} catch (NoSuchAlgorithmException e) {
				OUT.ERROR("", e);
			} catch (NoSuchProviderException e) {
				OUT.ERROR("", e);
			} catch (InvalidKeySpecException e) {
				OUT.ERROR("", e);
			}
		}
	}
	
	public synchronized static void GUIGlobalChanges(){
		refreshBalances();
		//balances
		//statusbar
		//block size etc.
		// transactions
		calculateBlockheight();
		peersConnected();
		testOnlineConnection();
		refreshTransactions();
		refreshSmartContracts();
		ContractStatusHandler.ProofContracts();
	}

	
	
	private static void calculateBlockheight() {
		if (Main.root == null) {
			return;
		}
		Node n = Main.root.lookup("#blockheight");
		if(n instanceof Label){
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					Label height = (Label) n;
					if(GO.previousBlock!=null){
						height.setText((GO.previousBlock.blockNo+1) + "");
					}
				}
			});
		}
	}
	
	public static void progrssbarRefresh(boolean determined, String work){
		//progressbar
		if (Main.root == null) {
			return;
		}
		Node n = Main.root.lookup("#progressbar");
		Node n1 = Main.root.lookup("#progresstext");
		if(n instanceof ProgressBar){
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					ProgressBar progbar = (ProgressBar) n;
					if(determined){
						progbar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
					} else {
						progbar.setProgress(0);
					}
				}
			});
		}
		if(n1 instanceof Label){
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					Label progtext = (Label) n1;
					progtext.setText(work);
				}
			});
		}
	}

	public static void testOnlineConnection(){
		if (Main.root == null) {
			return;
		}
		boolean ethernet = NetUtil.isReachableByPing();
		Node n = Main.root.lookup("#internetconnection");
		if(n instanceof Label){
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					Label connection = (Label) n;
					if(ethernet){
						connection.setText("Online");
					}else {
						connection.setText("Offline");
					}
				}
			});
		}
	}
	
	private static void peersConnected(){
		if (Main.root == null) {
			return;
		}
		Node n = Main.root.lookup("#peercount");
		if(n instanceof Label){
			Platform.runLater(new Runnable() {
				
				@Override
				public void run() {
					Label peercount = (Label) n;
					int i = 0;
					for(app.net.Node node : NodeRegister.getInstance().getNodes()){
						if(node.isActive()){
							i++;
						}
					}
					peercount.setText(i+" Peers");
				}
			});
		}
	}
	
	/**
	 * Dont forget the hashtag signe  # 
	 * @param nameofcomponent
	 * @param value
	 */
	private static void setText(String nameofcomponent, String value) {
		if (Main.root != null) {
			Node n = Main.root.lookup(nameofcomponent);
			if (n != null) {
				Platform.runLater(new Runnable() {
					public void run() {
						if (n instanceof Label) {
							Label l = (Label) n;
							l.setText(value);
						}
					}
				});
			}
		}
	}
	@FXML
	public void showAccountDetails(){
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("fxml/AccountDetailsDialog.fxml"));
		Parent content = null;
		try {
			content = (Parent) loader.load();
		} catch (IOException e) {
			OUT.ERROR("", e);
		}
        Scene secondScene = new Scene(content, 600, 400);

        Stage secondStage = new Stage();
        secondStage.setTitle("Account Details");
        secondStage.setScene(secondScene);
         
        secondStage.initOwner(Main.mainStage);
        secondStage.initModality(Modality.APPLICATION_MODAL); 
        secondStage.showAndWait();
	}
	
	@FXML
	public void showConfigDialog(){
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("fxml/ConfigDialog.fxml"));
		Parent content = null;
		try {
			content = (Parent) loader.load();
		} catch (IOException e) {
			OUT.ERROR("", e);
		}
        Scene secondScene = new Scene(content, 600, 400);

        secondStage = new Stage();
        secondStage.setTitle("Settings Dialog");
        secondStage.setScene(secondScene);
         
        secondStage.initOwner(Main.mainStage);
        secondStage.initModality(Modality.APPLICATION_MODAL); 
        secondStage.showAndWait();
	}
	
	private static <T> TableColumn<T, ?> getTableColumnByName(TableView<T> tableView, String name) {
	    for (TableColumn<T, ?> col : tableView.getColumns())
	        if (col.getText().equals(name)) return col ;
	    return null ;
	}
	
	private static void refreshTransactions(){
		if (Main.root != null) {
			Node n = Main.root.lookup("#tabletransactions");
			if (n != null) {
				Platform.runLater(new Runnable() {
					@SuppressWarnings("unchecked")
					public void run() {
						if(n instanceof TableView<?>){
							TableView<Transaction> tx = (TableView<Transaction>) n;
							TableColumn<Transaction, ?> date = getTableColumnByName(tx, "Date");;
							
							tx.getItems().setAll(TXTable.getTransactionHistory());
							date.setSortType(TableColumn.SortType.DESCENDING);
							tx.getSortOrder().setAll(date);
						}
					}
				});
			}
		}
	}
	
	private static void refreshSmartContracts() {
		if (Main.root != null) {
			Node n = Main.root.lookup("#activecontracts");
			Node n1 = Main.root.lookup("#marketcontracts");
			Node n2 = Main.root.lookup("#historycontracts");
			if (n != null) {
				Platform.runLater(new Runnable() {
					@SuppressWarnings("unchecked")
					public void run() {
						if(n instanceof TableView<?>){
							TableView<SmartContract> sc = (TableView<SmartContract>) n;
							sc.getItems().setAll(ContractController.getMySmartContractsFromBlockchain());
						}
						if(n1 instanceof TableView<?>){
							TableView<SmartContract> sc = (TableView<SmartContract>) n1;
							sc.getItems().setAll(ContractController.getSmartContractsFromBlockchainMarket());
						}
						if(n2 instanceof TableView<?>){
							TableView<SmartContract> sc = (TableView<SmartContract>) n2;
							sc.getItems().setAll(ContractController.getSmartContractsFromBlockchainHistory());
						}
					}
				});
			}
		}
	}
	
	public static void refreshNodes(){
		if (Main.root != null) {
			Node n = Main.root.lookup("#tablenodes");
			if (n != null) {
				Platform.runLater(new Runnable() {
					@SuppressWarnings("unchecked")
					public void run() {
						if(n instanceof TableView<?>){
							TableView<app.net.Node> nodes = (TableView<app.net.Node>) n;
							nodes.getItems().setAll(NodeRegister.getInstance().getNodes());
						}
					}
				});
			}
		}
	}
	
	private synchronized static void refreshBalances(){
		String balance = GO.clientWalletLocal.getBalance() + "";
		GUI.setText("#availablebalance", "" + balance);
		GUI.setText("#lockedbalance", "" + 0);
		GUI.setText("#unconfirmedbalance", "" + 0);
		GUI.setText("#totalbalance", "" + balance);
	}
	
	public static void ApplicationClose(WindowEvent we){
		Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to Quit?", ButtonType.YES,
				ButtonType.NO, ButtonType.CANCEL);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.YES) {
			GO.closeApplication();
			Platform.exit();
		} else {
			we.consume();
		}
	}
	@FXML
	public void appclosemenubar(){
		Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to Quit?", ButtonType.YES,
				ButtonType.NO, ButtonType.CANCEL);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.YES) {
			GO.closeApplication();
			Platform.exit();
		}
	}
	
	@FXML
	public void importwallet(){
		FileChooser fileChooser = new FileChooser();
//        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
//        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(Main.mainStage);
        
        try {
			KeyPair kp = (KeyLoader.LoadKeyPairViaPrivateKey(file.getAbsolutePath(), Cons.ALGORITHM, false));
			GO.clientWalletLocal.keyPair = kp;
		} catch (NoSuchAlgorithmException e) {
			OUT.ERROR("", e);
		} catch (InvalidKeySpecException e) {
			OUT.ERROR("", e);
		} catch (IOException e) {
			OUT.ERROR("", e);
		}
	}
	@FXML
	public void exportwallet(){
        FileChooser fileChooser = new FileChooser();
        
        //Set extension filter
//        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
//        fileChooser.getExtensionFilters().add(extFilter);
       
        //Show save file dialog
        File file = fileChooser.showSaveDialog(Main.mainStage);
       
        if(file != null){
            SaveFile(GO.clientWalletLocal.privateKey, file);
        }
	}
	
    private void SaveFile(PrivateKey content, File file){
        try {
            FileWriter fileWriter = null;
             
            fileWriter = new FileWriter(file);
//            fileWriter.write(content);
//            fileWriter.w
            
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
            		content.getEncoded());
            FileOutputStream fos = new FileOutputStream(file +"");
            fos.write(pkcs8EncodedKeySpec.getEncoded());
            fos.close();
            
            
            fileWriter.close();
        } catch (IOException ex) {
        	
//            Logger.getLogger(JavaFX_Text.class.getName()).log(Level.SEVERE, null, ex);
        }
         
    }
    @FXML
    public void nodeDiscoveryStarter(){
    	NodeDiscovery.startRefreshNodes();
    }
    
    @FXML
    public void pdfStarter(){
    	new pdfstarter();
    }
	
//    @FXML
//    public void processSmartContract(){
//		ContractStatusHandler.ProofContracts();
////		mycontractView.refresh.doClick();
////		globalcontractView.refresh.doClick();
//    }
	
}
