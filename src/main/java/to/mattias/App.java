package to.mattias;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Created by mattias on 2017-03-13.
 */
public class App extends Application {

    private AmqConnection connection;

    private Stage stage;
    private TextArea output;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        connection = new AmqConnection(this);
        stage = primaryStage;
        GridPane rootPane = new GridPane();
        Scene scene = new Scene(rootPane, 600, 300);
        stage.setScene(scene);
        stage.show();

        TextField messageField = new TextField();
        Button button = new Button("Skicka");
        output = new TextArea();

        rootPane.addColumn(0, messageField);
        rootPane.addColumn(1, button);
        rootPane.addRow(1, output);

        button.setOnAction(e -> {
            connection.sendMsg(messageField.getText());
        });

    }

    public void output(String msg) {
        Platform.runLater(() -> {
            this.output.appendText(msg);
        });
    }

    public void setTitle(String msg) {
        Platform.runLater(() -> {
            this.stage.setTitle(msg);
        });
    }


}
