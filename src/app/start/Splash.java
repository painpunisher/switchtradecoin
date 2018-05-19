package app.start;

import java.net.URL;

import app.log.OUT;
import app.start.beta.betaphase;
import app.start.beta.killmyself;
import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Splash extends Preloader {

	private Stage preloaderStage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		if(betaphase.checkBetaPhaseOver()){
			try {
				killmyself.selfDestructJARFile();
			} catch (Exception e) {
				OUT.ERROR("", e);
			}
		}
		
		this.preloaderStage = primaryStage;
		primaryStage.initStyle(StageStyle.UNDECORATED);
		VBox loading = new VBox(5);
		ProgressBar p = new ProgressBar();
		ProgressBar p1 = new ProgressBar();
		p.setPrefWidth(525);
		p1.setPrefWidth(525);
		loading.getChildren().add(p);
		// loading.getChildren().add(new Label("Please wait..."));

		URL url = getClass().getResource("/logo.png");
		ImageView splashImage = new ImageView(new Image(url.toString()));
		// splashImage.setEffect(new DropShadow(25, Color.BLACK));
		loading.getChildren().add(splashImage);

		loading.getChildren().add(p1);
		
		BorderPane root = new BorderPane(loading);
		Scene scene = new Scene(root);

		primaryStage.setWidth(525);
		primaryStage.setHeight(520);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
		if (stateChangeNotification.getType() == Type.BEFORE_START) {
			preloaderStage.hide();
		}
	}
}
