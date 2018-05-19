package app.start.gui;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

import app.core.MainMethods;
import app.gui.msg.WarningError;
import app.log.OUT;
import app.model.block.Block;
import app.model.contract.ContractStatus;
import app.model.contract.SmartContract;
import app.model.transaction.Transaction;
import app.start.Main;
import app.start.util.GO;
import app.util.StringUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

public class ContractController implements Initializable {

	private @FXML
	DatePicker datepickercontract;
	private @FXML
	ComboBox<VisibilityContract> visiblecontract;
	private @FXML
	Spinner<Integer> pricecontract;
	private @FXML
	TextField locationcontract;
	private @FXML
	TextArea textcontract;
	@FXML
	Button designcontractbutton;

	private @FXML
	TableView<SmartContract> activecontracts;
	private @FXML
	TableColumn<SmartContract, String> scadesigndate;
	private @FXML
	TableColumn<SmartContract, String> scaexpiredate;
	private @FXML
	TableColumn<SmartContract, String> scalocation;
	private @FXML
	TableColumn<SmartContract, String> scaprice;
	private @FXML
	TableColumn<SmartContract, String> scastatus;
	private @FXML
	TableColumn<SmartContract, String> scabalance;
	// scabalance
	private @FXML
	TableView<SmartContract> historycontracts;
	private @FXML
	TableColumn<SmartContract, String> schdesigndate;
	private @FXML
	TableColumn<SmartContract, String> schexpiredate;
	private @FXML
	TableColumn<SmartContract, String> schlocation;
	private @FXML
	TableColumn<SmartContract, String> schprice;

	private @FXML
	TableView<SmartContract> marketcontracts;
	private @FXML
	TableColumn<SmartContract, String> scmdesigndate;
	private @FXML
	TableColumn<SmartContract, String> scmexpiredate;
	private @FXML
	TableColumn<SmartContract, String> scmlocation;
	private @FXML
	TableColumn<SmartContract, String> scmprice;

	private static Stage secondStage;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (visiblecontract != null) {
			visiblecontract.getItems().addAll(VisibilityContract.values());
			visiblecontract.setValue(VisibilityContract.PUBLIC);

			// get a localized format for parsing
			NumberFormat format = NumberFormat.getIntegerInstance();
			UnaryOperator<TextFormatter.Change> filter = c -> {
				if (c.isContentChange()) {
					ParsePosition parsePosition = new ParsePosition(0);
					// NumberFormat evaluates the beginning of the text
					format.parse(c.getControlNewText(), parsePosition);
					if (parsePosition.getIndex() == 0 || parsePosition.getIndex() < c.getControlNewText().length()) {
						// reject parsing the complete text failed
						return null;
					}
				}
				return c;
			};
			TextFormatter<Integer> priceFormatter = new TextFormatter<Integer>(new IntegerStringConverter(), 0, filter);

			pricecontract.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000000000,
					Integer.parseInt(pricecontract.getValue() + "")));
			pricecontract.setEditable(true);
			pricecontract.getEditor().setTextFormatter(priceFormatter);
			pricecontract.focusedProperty().addListener((observable, oldValue, newValue) -> {
				  if (!newValue) {
					  pricecontract.increment(0); // won't change value, but will commit editor
				  }
				});
		}

		// receiverport.setText(Integer.parseInt(Config.INSTANCE.getProperty("RECEIVERPORT"))
		// + "");
		// debugconfig.setSelected(Boolean.parseBoolean(Config.INSTANCE.getProperty("DEBUGMODE")));
		// miningconfig.setSelected(Boolean.parseBoolean(Config.INSTANCE.getProperty("MININGMODE")));
		// nodediscoveryconfig.setSelected(Boolean.parseBoolean(Config.INSTANCE.getProperty("NODEDISCOVERY")));
		// loadConfig();
		if (activecontracts != null) {
			activecontracts.setRowFactory((tv) -> {
				TableRow<SmartContract> row = new TableRow<SmartContract>() {

					@Override
					public void updateItem(SmartContract item, boolean empty) {
						super.updateItem(item, empty);

						if (item == null) {
							setStyle("");
						} else if (checkIfConfirmTurn(item)) {
							setStyle("-fx-background-color: lightgreen;");
						} else {
							setStyle("-fx-background-color: salmon;");
						}
					}
				};
				row.setOnMouseClicked(event -> {
					if (event.getClickCount() == 2 && (!row.isEmpty())) {
						SmartContract rowData = row.getItem();
						ShowSmartContractController dialog = new ShowSmartContractController();
						ShowSmartContractController.mycontracts = true;
						dialog.showSmartContract(rowData);
					}
				});
				return row;
			});

			scadesigndate.setCellValueFactory(new PropertyValueFactory<SmartContract, String>("designedTimestamp"));
			scaexpiredate.setCellValueFactory(new PropertyValueFactory<SmartContract, String>("expireTimestamp"));
			scalocation.setCellValueFactory(new PropertyValueFactory<SmartContract, String>("location"));
			scaprice.setCellValueFactory(new PropertyValueFactory<SmartContract, String>("price"));
			scastatus.setCellValueFactory(new PropertyValueFactory<SmartContract, String>("status"));
			scabalance.setCellValueFactory(new PropertyValueFactory<SmartContract, String>("balance"));

			// coldate.setSortable(false);
			// colip.setSortable(false);
			// colport.setSortable(false);

			Platform.runLater(new Runnable() {
				public void run() {
					activecontracts.getItems().setAll(getMySmartContractsFromBlockchain());
				}
			});
		}

		if (marketcontracts != null) {

			marketcontracts.setRowFactory((tv) -> {
				TableRow<SmartContract> row = new TableRow<SmartContract>() {

					@Override
					public void updateItem(SmartContract item, boolean empty) {
						super.updateItem(item, empty);

						if (item == null) {
							setStyle("");
						} else if (checkIfConfirmTurn(item)) {
							setStyle("-fx-background-color: lightgreen;");
						} else {
							setStyle("-fx-background-color: salmon;");
						}
					}
				};
				row.setOnMouseClicked(event -> {
					if (event.getClickCount() == 2 && (!row.isEmpty())) {
						SmartContract rowData = row.getItem();
						ShowSmartContractController dialog = new ShowSmartContractController();
						ShowSmartContractController.mycontracts = false;
						dialog.showSmartContract(rowData);
					}
				});
				return row;
			});

			scmdesigndate.setCellValueFactory(new PropertyValueFactory<SmartContract, String>("designedTimestamp"));
			scmexpiredate.setCellValueFactory(new PropertyValueFactory<SmartContract, String>("expireTimestamp"));
			scmlocation.setCellValueFactory(new PropertyValueFactory<SmartContract, String>("location"));
			scmprice.setCellValueFactory(new PropertyValueFactory<SmartContract, String>("price"));
			// scmstatus.setCellValueFactory(new
			// PropertyValueFactory<SmartContract, String>("status"));

			// coldate.setSortable(false);
			// colip.setSortable(false);
			// colport.setSortable(false);

			Platform.runLater(new Runnable() {
				public void run() {
					marketcontracts.getItems().setAll(getSmartContractsFromBlockchainMarket());
				}
			});
		}

		if (historycontracts != null) {

			historycontracts.setRowFactory((tv) -> {
				TableRow<SmartContract> row = new TableRow<SmartContract>() {

					@Override
					public void updateItem(SmartContract item, boolean empty) {
						super.updateItem(item, empty);

						if (item == null) {
							setStyle("");
						} else if (checkIfConfirmTurn(item)) {
							setStyle("-fx-background-color: lightgreen;");
						} else {
							setStyle("-fx-background-color: salmon;");
						}
					}
				};
				row.setOnMouseClicked(event -> {
					if (event.getClickCount() == 2 && (!row.isEmpty())) {
						SmartContract rowData = row.getItem();
						ShowSmartContractController dialog = new ShowSmartContractController();
						ShowSmartContractController.mycontracts = true;
						dialog.showSmartContract(rowData);
					}
				});
				return row;
			});

			schdesigndate.setCellValueFactory(new PropertyValueFactory<SmartContract, String>("designedTimestamp"));
			schexpiredate.setCellValueFactory(new PropertyValueFactory<SmartContract, String>("expireTimestamp"));
			schlocation.setCellValueFactory(new PropertyValueFactory<SmartContract, String>("location"));
			schprice.setCellValueFactory(new PropertyValueFactory<SmartContract, String>("price"));
			// scmstatus.setCellValueFactory(new
			// PropertyValueFactory<SmartContract, String>("status"));

			// coldate.setSortable(false);
			// colip.setSortable(false);
			// colport.setSortable(false);

			Platform.runLater(new Runnable() {
				public void run() {
					historycontracts.getItems().setAll(getSmartContractsFromBlockchainHistory());
				}
			});
		}

	}

	@FXML
	public void pushSmartContract() {
		LocalDate localDate = datepickercontract.getValue();
		Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
		Date date = Date.from(instant);
		OUT.DEBUG(localDate + "\n" + instant + "\n" + date);
		OUT.DEBUG(date+"");
		OUT.DEBUG(textcontract.getText());
		OUT.DEBUG(locationcontract.getText());
		OUT.DEBUG(pricecontract.getValue()+"");
		// OUT.DEBUG(new BigInteger(pricecontract.gette + ""));
		OUT.DEBUG(visiblecontract.getValue().isVisible()+"");
		SmartContract sc = new SmartContract(date, textcontract.getText(), locationcontract.getText(),
				new BigInteger(pricecontract.getValue() + ""), visiblecontract.getValue().isVisible());
		OUT.DEBUG(sc+"");

		boolean permissionToDesignAContract = false;
		// check balance
		// are you sure u wanna design
		// check design if correctly
		BigInteger balance = MainMethods.getBalance(GO.clientWalletLocal.publicKey);
		if (balance.compareTo(sc.price.multiply(new BigInteger("3"))) < 0) {
			WarningError.showError("Not enough funds to create this Contract!");
		} else {
			Alert alert = new Alert(AlertType.CONFIRMATION, "Check if Contract is correct?", ButtonType.YES,
					ButtonType.NO);
			alert.showAndWait();
			if (alert.getResult() == ButtonType.YES) {
				Alert alert1 = new Alert(AlertType.CONFIRMATION,
						"Are you sure you want to Design this Contract? \n You will be charged with: "
								+ (sc.price.multiply(new BigInteger("3"))),
						ButtonType.YES, ButtonType.NO);
				alert1.showAndWait();
				if (alert1.getResult() == ButtonType.YES) {
					permissionToDesignAContract = true;
				}
			}
		}

		if (permissionToDesignAContract) {
			final Transaction txForContract = GO.clientWalletLocal.sendFunds(sc.publicKey,
					sc.price.multiply(new BigInteger("3")));
			if (txForContract != null) {
				GO.mempool.add(txForContract);
				OUT.DEBUG("SIZE OF MEMPOOL>>>: " + GO.mempool.size());
				OUT.DEBUG("AFTER ADDING TO MEMPOOL: " + txForContract.transactionId);
				if (GO.sender != null) {
					GO.sender.sendTransaction("", 0, txForContract);
				}
			}
			GO.contractsPool.add(sc);
			closedialog();
		}

	}

	@FXML
	public void designSmartContract() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("fxml/DesignSmartContract.fxml"));
		Parent content = null;
		try {
			content = (Parent) loader.load();
		} catch (IOException e) {
			OUT.ERROR("", e);
		}
		Scene secondScene = new Scene(content, 600, 400);

		secondStage = new Stage();
		secondStage.setTitle("Design Smart Contract");
		secondStage.setScene(secondScene);

		secondStage.initOwner(Main.mainStage);
		secondStage.initModality(Modality.APPLICATION_MODAL);
		secondStage.showAndWait();
	}

	private void closedialog() {
		secondStage.close();
	}

	static LinkedList<SmartContract> getMySmartContractsFromBlockchain() {
		LinkedList<SmartContract> history = new LinkedList<SmartContract>();
		for (Block curr : GO.BLOCKCHAIN) {
			if (curr.contracts.size() > 0) {
				for (SmartContract sc : curr.contracts) {
					if (sc.status.toString().equals(ContractStatus.KILLSELF.toString())) {
						continue;
					}
					if (sc.designer.equals(StringUtil.getStringFromKey(GO.clientWalletLocal.publicKey))) {
						history.add(sc);
					}
					if (sc.acceptor.equals(StringUtil.getStringFromKey(GO.clientWalletLocal.publicKey))) {
						history.add(sc);
					}
				}
			}
		}
		return history;
	}

	static LinkedList<SmartContract> getSmartContractsFromBlockchainMarket() {
		LinkedList<SmartContract> market = new LinkedList<SmartContract>();
		for (Block curr : GO.BLOCKCHAIN) {
			if (curr.contracts.size() > 0) {
				for (SmartContract sc : curr.contracts) {
					if (sc.status.toString().equals(ContractStatus.WAITINGACCEPTOR.toString())) {
						market.add(sc);
					}
				}
			}

		}
		return market;
	}

	static LinkedList<SmartContract> getSmartContractsFromBlockchainHistory() {
		LinkedList<SmartContract> history = new LinkedList<SmartContract>();
		for (Block curr : GO.BLOCKCHAIN) {
			if (curr.contracts.size() > 0) {
				for (SmartContract sc : curr.contracts) {
					if (sc.status.toString().equals(ContractStatus.KILLSELF.toString())) {
						if (sc.designer.equals(StringUtil.getStringFromKey(GO.clientWalletLocal.publicKey))) {
							history.add(sc);
						}
						if (sc.acceptor.equals(StringUtil.getStringFromKey(GO.clientWalletLocal.publicKey))) {
							history.add(sc);
						}
					}
				}
			}
		}
		return history;
	}

	private boolean checkIfConfirmTurn(SmartContract sc) {
		boolean turn = false;
		switch (sc.status) {
		case WAITINGACCEPTOR:
			if (sc.acceptor.equals("")) {
				if (!StringUtil.getStringFromKey(GO.clientWalletLocal.publicKey).equals(sc.designer)) {
					turn = true;
				}
			}
			break;
		case WAITINGACCEPTORCONFIRM:
			if (StringUtil.getStringFromKey(GO.clientWalletLocal.publicKey).equals(sc.acceptor)) {
				turn = true;
			}
			break;
		case WAITINGDESIGNERCONFIRM:
			if (StringUtil.getStringFromKey(GO.clientWalletLocal.publicKey).equals(sc.designer)) {
				turn = true;
			}
			break;
		default:
			break;
		}
		// if(!turn){
		// setBackground(Color.WHITE);
		// } else {
		// setBackground(Color.GREEN);
		// }
		// revalidate();
		// repaint();
		return turn;
	}

}
