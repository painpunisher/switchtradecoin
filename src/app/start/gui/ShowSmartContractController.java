package app.start.gui;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ResourceBundle;

import app.core.contract.ContractUtil;
import app.gui.msg.WarningError;
import app.log.OUT;
import app.model.contract.SmartContract;
import app.model.transaction.Transaction;
import app.start.Main;
import app.start.util.GO;
import app.util.StringUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ShowSmartContractController implements Initializable {
	private static Stage secondStage;
	private static SmartContract mSC;
	static boolean mycontracts = false;
	private @FXML TextField contractid;
	private @FXML TextField designdate;
	private @FXML TextField expiredate;
	private @FXML TextField location;
	private @FXML TextField price;
	private @FXML TextField status;
	private @FXML TextField totalfunds;
	private @FXML TextArea textarea;
	private @FXML Button acceptcontract;
	private @FXML Button confirmcontract;
	
	@Override
	public void initialize(URL location_2, ResourceBundle resources) {
		contractid.setText(mSC.id);
		designdate.setText(mSC.getDesignedTimestamp());
		expiredate.setText(mSC.getExpireTimestamp());
		location.setText(mSC.location);
		price.setText(mSC.price+"");
		status.setText(mSC.status.toString());
		totalfunds.setText(ContractUtil.getBalance(mSC.publicKey) + "");
		textarea.setText(mSC.contractDescription);
		
		switch (mSC.status) {
		case WAITINGACCEPTOR:
			if(mSC.designer.equals(StringUtil.getStringFromKey(GO.clientWalletLocal.publicKey))){
				acceptcontract.setDisable(true);
				confirmcontract.setDisable(true);				
			} else {
				confirmcontract.setDisable(true);	
			}
			break;
		case WAITINGACCEPTORCONFIRM:
			if(mSC.designer.equals(StringUtil.getStringFromKey(GO.clientWalletLocal.publicKey))){
				acceptcontract.setDisable(true);
				confirmcontract.setDisable(true);				
			} else {
				acceptcontract.setDisable(true);
			}
			break;
		case WAITINGDESIGNERCONFIRM:
			if(mSC.designer.equals(StringUtil.getStringFromKey(GO.clientWalletLocal.publicKey))){
				acceptcontract.setDisable(true);
			} else {
				acceptcontract.setDisable(true);
				confirmcontract.setDisable(true);	
			}
			break;
		case KILLSELF:
			acceptcontract.setDisable(true);
			confirmcontract.setDisable(true);
		default:
			break;
		}
	}
	
	//@FXML
	void showSmartContract(SmartContract sc) {
		mSC = sc;
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("fxml/ShowSmartContract.fxml"));
		Parent content = null;
		try {
			content = (Parent) loader.load();
		} catch (IOException e) {
			OUT.ERROR("", e);
		}
		Scene secondScene = new Scene(content, 600, 400);

		secondStage = new Stage();
		secondStage.setTitle("Display Smart Contract");
		secondStage.setScene(secondScene);

		secondStage.initOwner(Main.mainStage);
		secondStage.initModality(Modality.APPLICATION_MODAL);
		secondStage.showAndWait();
	}
	@FXML
	public void closeDialog(){
		secondStage.close();
	}

	@FXML
	public void acceptcontract(){
		BigInteger bal = GO.clientWalletLocal.getBalance();
		if (bal.compareTo(mSC.price) < -1) {
			WarningError.showError("Not enough funds to Accept this Contract!");
			return;
		}
		
		//ask sure?
		Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to Accept this Contract? \n You will be charged with: " + (mSC.price), ButtonType.YES,
				ButtonType.NO);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.YES) {
			final Transaction txForContract = GO.clientWalletLocal.sendFunds(mSC.publicKey,
					mSC.price.multiply(new BigInteger("1")));
			
			if (txForContract != null) {
				GO.mempool.add(txForContract);
				OUT.DEBUG("SIZE OF MEMPOOL>>>: " + GO.mempool.size());
				OUT.DEBUG("AFTER ADDING TO MEMPOOL: " + txForContract.transactionId);
				if (GO.sender != null) {
					GO.sender.sendTransaction("", 0, txForContract);
				}
			}
//			GO.contractsPool.add(sc);
			secondStage.close();
		}
	}
	
	@FXML
	public void confirmcontract(){
		//ask sure?
		Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to confirm?", ButtonType.YES,
				ButtonType.NO);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.YES) {
			final Transaction txForContract = GO.clientWalletLocal.sendFunds(mSC.publicKey,
					mSC.price.multiply(new BigInteger("1")));
			
			if (txForContract != null) {
				GO.mempool.add(txForContract);
				OUT.DEBUG("SIZE OF MEMPOOL>>>: " + GO.mempool.size());
				OUT.DEBUG("AFTER ADDING TO MEMPOOL: " + txForContract.transactionId);
				if (GO.sender != null) {
					GO.sender.sendTransaction("", 0, txForContract);
				}
			}
			secondStage.close();
		}
	}
}
