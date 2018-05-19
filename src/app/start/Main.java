package app.start;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import com.sun.javafx.application.LauncherImpl;

import app.config.Config;
import app.log.Log;
import app.log.OUT;
import app.old.StartChain;
import app.start.gui.GUI;
import app.start.util.GO;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

@SuppressWarnings("restriction")
public class Main extends Application implements Initializable {

	public static Parent root;
	public static Stage mainStage;

	public static boolean preloaded = false;

	@FXML
	private TabPane tabpane;
	@FXML
	private Tab tabtransaction;
	@FXML
	private Tab tabmarket;
	@FXML
	private Tab tabmycontracts;

	@Override
	public void init() throws Exception {
		// Do some heavy lifting
		Config.init();
		Log.init();
		if (GO.initializeApplication()) {
			preloaded = true;
		}
	}

	@Override
	public void start(Stage primaryStage) {
		mainStage = primaryStage;
		STARTAPP(primaryStage);
		Platform.setImplicitExit(false);
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				GUI.ApplicationClose(we);
			}
		});
	}

	public static void main(String[] args) {
		System.out.println("---------------------------------------");
		System.out.println("----------- PROGRAMM STARTS -----------");
		System.out.println("---------------------------------------");
		LauncherImpl.launchApplication(Main.class, Splash.class, args);
//		launch(args);
	}

	private boolean STARTAPP(Stage primaryStage) {
		if (preloaded) {
			try {
				root = FXMLLoader.load(getClass().getResource("gui/fxml/Main.fxml"));
				URL url = getClass().getResource("/logo.png");
				primaryStage.getIcons().add(new Image(url.toString()));
				primaryStage.setTitle("Tripple Hexa Coin - Core Application - V.0.5");
				primaryStage.setScene(new Scene(root, 800, 600));
				primaryStage.setResizable(false);
				primaryStage.show();
				GUI.GUIGlobalChanges();
			} catch (Exception e) {
				OUT.ERROR("", e);
			}
			return true;
		} else {
			Config.init();
			Log.init();
			if (GO.initializeApplication()) {
				try {
					root = FXMLLoader.load(getClass().getResource("gui/fxml/Main.fxml"));
					primaryStage.setScene(new Scene(root, 800, 600));
					primaryStage.setResizable(false);
					primaryStage.show();
					GUI.GUIGlobalChanges();
				} catch (Exception e) {
					OUT.ERROR("", e);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (tabtransaction != null) {
			InputStream input = StartChain.class.getResourceAsStream("/transaction.png");
			Image image = new Image(input);
			ImageView imageView = new ImageView(image);
			imageView.setFitHeight(20);
			imageView.setFitWidth(20);
			tabtransaction.setGraphic(imageView);
		}
		if (tabmarket != null) {
			InputStream input = StartChain.class.getResourceAsStream("/shopping-cart.png");
			Image image = new Image(input);
			ImageView imageView = new ImageView(image);
			imageView.setFitHeight(20);
			imageView.setFitWidth(20);
			tabmarket.setGraphic(imageView);
		}
		if (tabmycontracts != null) {
			InputStream input = StartChain.class.getResourceAsStream("/document.png");
			Image image = new Image(input);
			ImageView imageView = new ImageView(image);
			imageView.setFitHeight(20);
			imageView.setFitWidth(20);
			tabmycontracts.setGraphic(imageView);
		}
	}
}
