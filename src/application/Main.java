package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {
	public static customerController customerC;
	public static dashboardController dashboardC;
	public static warehousePickerController warehousePickerC;
	
	
	
    /**
     * Overridden from the Application class, is called when the application is launched.
     * Opens three UI windows.
     */
	@Override
	public void start(Stage primaryStage) {
		openUI("dashboard.fxml");
		openUI("customer.fxml");
		openUI("warehousePicker.fxml");

	}
	
    /**
     * Launches the JavaFX application.
     */
	public static void main(String[] args) {
		launch(args);
	}
	
    /**
     * Opens a new window of the specified FXML file.
     *
     * @param fileName The name of the FXML file to load.
     */
	public void openUI(String fileName) {
		
		try {
			Parent root = FXMLLoader.load(getClass().getResource(fileName));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage stage = new Stage();
			stage.setTitle(fileName);
			stage.setResizable(false);
			stage.setScene(scene);
			stage.show();
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

