package app.start.gui;

import java.math.BigInteger;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import app.model.block.Block;
import app.model.transaction.Transaction;
import app.start.util.GO;
import app.util.StringUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class TXTable implements Initializable {

	@FXML
	private TableView<Transaction> tabletransactions;
	@FXML
	private TableColumn<Transaction, String> coldate;
	@FXML
	private TableColumn<Transaction, String> colsender;
	@FXML
	private TableColumn<Transaction, String> colreceiver;
	@FXML
	private TableColumn<Transaction, String> colamount;
	@FXML
	private TableColumn<Transaction, String> coltxid;
	@FXML
	private Button transactionsend;
	@FXML
	private TextField transactionreceiver;
	@FXML
	private TextField transactionamount;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		coldate.setCellValueFactory(new PropertyValueFactory<Transaction, String>("date"));
		colsender.setCellValueFactory(new PropertyValueFactory<Transaction, String>("strSender"));
		colreceiver.setCellValueFactory(new PropertyValueFactory<Transaction, String>("strReciepient"));
		colamount.setCellValueFactory(new PropertyValueFactory<Transaction, String>("value"));
		coltxid.setCellValueFactory(new PropertyValueFactory<Transaction, String>("transactionId"));

		// coldate.setSortable(false);
		colsender.setSortable(false);
		colreceiver.setSortable(false);
		colamount.setSortable(false);
		coltxid.setSortable(false);

		Platform.runLater(new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				tabletransactions.getItems().setAll(getTransactionHistory());
				coldate.setSortType(TableColumn.SortType.DESCENDING);
				tabletransactions.getSortOrder().setAll(coldate);
			}
		});
		
		tabletransactions.setRowFactory((tv) -> {
			TableRow<Transaction> row = new TableRow<Transaction>() {

				@Override
				public void updateItem(Transaction item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null) {
						setStyle("");
					} else if (transactionOwner(item)) {
						setStyle("-fx-background-color: lightgreen;");
					} else {
						setStyle("-fx-background-color: salmon;");
					}
				}
			};
//			row.setOnMouseClicked(event -> {
//				if (event.getClickCount() == 2 && (!row.isEmpty())) {
//					SmartContract rowData = row.getItem();
//					ShowSmartContractController dialog = new ShowSmartContractController();
//					ShowSmartContractController.mycontracts = false;
//					dialog.showSmartContract(rowData);
//				}
//			});
			return row;
		});
	}

	private static boolean transactionOwner(Transaction tx){
		if(tx.sender.equals(GO.clientWalletLocal.publicKey)){
			return false;
		} 
		return true;
	}
	
	static List<Transaction> getTransactionHistory() {
		LinkedList<Transaction> history = new LinkedList<>();
		for (Block curr : GO.BLOCKCHAIN) {
			if (curr.transactions.size() > 0) {
				for (Transaction tx : curr.transactions) {
					if (tx.sender.equals(GO.clientWalletLocal.publicKey)) {
						history.add(tx);
					}
					if (tx.reciepient.equals(GO.clientWalletLocal.publicKey)) {
						history.add(tx);
					}
				}

			}
		}
		
		if(history.size()>26){
			return history.subList(history.size() - 25, history.size());
		} else {
			return history;
		}
	}
	@FXML
	private void sendTransaction() {
		if(StringUtil.getKey(transactionreceiver.getText()) == null){
			return;
		}
		Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to Send?", ButtonType.YES, ButtonType.NO,
				ButtonType.CANCEL);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.YES) {
			Transaction txnew = GO.clientWalletLocal.sendFunds(StringUtil.getKey(transactionreceiver.getText()),
					new BigInteger(transactionamount.getText()));
			GO.mempool.add(txnew);
		}
	}

	// void addObservableDataToTableView(TableView<Transaction> table,
	// ArrayList<Transaction> transaction){
	// private final ObservableList<Transaction> data =
	// FXCollections.observableArrayList(transactions);
	// table.setData(data);
	// }

}
