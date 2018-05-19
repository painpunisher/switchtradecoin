package app.start.gui;

import java.net.URL;
import java.util.ResourceBundle;

import app.config.Config;
import app.net.Node;
import app.net.NodeRegister;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class NodeTable implements Initializable {

	@FXML
	private TableView<Node> tablenodes;
	@FXML
	private TableColumn<Node, String> colip;
	@FXML
	private TableColumn<Node, String> colport;
	@FXML
	private Button addnode;
	@FXML
	private Button deletenode;
	@FXML
	private TextField nodeip;
	@FXML
	private TextField nodeport;
	@FXML
	private Button configsave;
	@FXML
	private Button configcancel;
	@FXML
	private CheckBox miningconfig;
	@FXML
	private CheckBox debugconfig;
	@FXML
	private CheckBox nodediscoveryconfig;
	@FXML
	private TextField receiverport;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		receiverport.setText(Integer.parseInt(Config.INSTANCE.getProperty("RECEIVERPORT"))+"");
		 debugconfig.setSelected(Boolean.parseBoolean(Config.INSTANCE.getProperty("DEBUGMODE")));
		 miningconfig.setSelected(Boolean.parseBoolean(Config.INSTANCE.getProperty("MININGMODE")));
		 nodediscoveryconfig.setSelected(Boolean.parseBoolean(Config.INSTANCE.getProperty("NODEDISCOVERY")));
//		loadConfig();
		
		colip.setCellValueFactory(new PropertyValueFactory<Node, String>("IP"));
		colport.setCellValueFactory(new PropertyValueFactory<Node, String>("Port"));

		// coldate.setSortable(false);
		colip.setSortable(false);
		colport.setSortable(false);

		Platform.runLater(new Runnable() {
			public void run() {
				tablenodes.getItems().setAll(NodeRegister.getInstance().getNodes());
			}
		});
	}
	@FXML
	public void addnodemethod(){
		NodeRegister.getInstance().addNode(nodeip.getText(), Integer.parseInt(nodeport.getText()));
		refreshNodes();
	}
	@FXML
	public void deletenodemethod(){
		Node remove = null;
		for(Node cur : NodeRegister.getInstance().getNodes()){
			if(cur.getIP().equals(nodeip.getText())){
				if(cur.getPort() == (Integer.parseInt(nodeport.getText()))){
					remove = cur;
					break;
				}
			}
		}
		NodeRegister.getInstance().getNodes().remove(remove);
		refreshNodes();
	}

	@FXML
	public void selectedNode(){
		Node node = tablenodes.getSelectionModel().getSelectedItem();
		Platform.runLater(new Runnable() {
			public void run() {
				nodeip.setText(node.getIP());
				nodeport.setText(node.getPort()+"");
			}
		});
	}
	@FXML
	public void configsave(){
		Config.INSTANCE.setProperty("RECEIVERPORT", (receiverport.getText()));
		Config.INSTANCE.setProperty("MININGMODE", miningconfig.isSelected() + "");
		Config.INSTANCE.setProperty("DEBUGMODE", debugconfig.isSelected() + "");
		Config.INSTANCE.setProperty("NODEDISCOVERY", nodediscoveryconfig.isSelected() + "");
		Config.saveProperties();
		NodeRegister.getInstance().saveNodes();
		GUI.secondStage.close();
	}
	@FXML
	public void configcancel(){
		GUI.secondStage.close();
	}
	@FXML
	private void loadConfig(){
		 receiverport.setText(Integer.parseInt(Config.INSTANCE.getProperty("RECEIVERPORT"))+"");
		 debugconfig.setSelected(Boolean.parseBoolean(Config.INSTANCE.getProperty("DEBUGMODE")));
		 miningconfig.setSelected(Boolean.parseBoolean(Config.INSTANCE.getProperty("MININGMODE")));
		 nodediscoveryconfig.setSelected(Boolean.parseBoolean(Config.INSTANCE.getProperty("NODEDISCOVERY")));
	}
	
	private void refreshNodes(){
		Platform.runLater(new Runnable() {
			public void run() {
				tablenodes.getItems().setAll(NodeRegister.getInstance().getNodes());
			}
		});
	}
	
//	public static List<Transaction> getTransactionHistory() {
//		LinkedList<Transaction> history = new LinkedList<>();
//		for (Block curr : GO.BLOCKCHAIN) {
//			if (curr.transactions.size() > 0) {
//				for (Transaction tx : curr.transactions) {
//					if (tx.sender == null) {
//						OUT.DEBUG("stahp");
//						return null;
//					}
//					if (tx.sender.equals(GO.clientWalletLocal.publicKey)) {
//						history.add(tx);
//					}
//					if (tx.reciepient.equals(GO.clientWalletLocal.publicKey)) {
//						history.add(tx);
//					}
//				}
//
//			}
//		}
//		
//		if(history.size()>26){
//			return history.subList(history.size() - 25, history.size());
//		} else {
//			return history;
//		}
//	}
//	@FXML
//	private void sendTransaction() {
//		if(StringUtil.getKey(transactionreceiver.getText()) == null){
//			return;
//		}
//		Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to Send?", ButtonType.YES, ButtonType.NO,
//				ButtonType.CANCEL);
//		alert.showAndWait();
//		if (alert.getResult() == ButtonType.YES) {
//			Transaction txnew = GO.clientWalletLocal.sendFunds(StringUtil.getKey(transactionreceiver.getText()),
//					new BigInteger(transactionamount.getText()));
//			GO.mempool.add(txnew);
//		}
//	}

	// void addObservableDataToTableView(TableView<Transaction> table,
	// ArrayList<Transaction> transaction){
	// private final ObservableList<Transaction> data =
	// FXCollections.observableArrayList(transactions);
	// table.setData(data);
	// }

}
