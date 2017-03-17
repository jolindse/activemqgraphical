package to.mattias;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Created by mattias on 2017-03-13.
 */
public class App extends Application {

    private AmqConnection connection;

    private Stage stage;
    private TextArea output, logarea;


    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {

        connection = new AmqConnection(this);

        stage = primaryStage;
        GridPane rootPane = new GridPane();
        VBox buttonPane = new VBox();
        Scene scene = new Scene(rootPane, 700, 400);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("Disconnected");

        TextField messageField = new TextField();
        messageField.setPrefWidth(550);

        Button sendButton = new Button("Send msg");
        sendButton.setPrefWidth(150);
        Button disconnectButton = new Button("Disconnect");
        disconnectButton.setPrefWidth(150);
        Button reconnectButton = new Button("Reconnect");
        reconnectButton.setPrefWidth(150);

        output = new TextArea();
        output.setEditable(false);

        logarea = new TextArea();
        logarea.setEditable(false);
        logarea.appendText("LOG:"+"\n");

        buttonPane.getChildren().add(sendButton);
        buttonPane.getChildren().add(reconnectButton);
        buttonPane.getChildren().add(disconnectButton);

        rootPane.addColumn(0, messageField);
        rootPane.addColumn(1, buttonPane);
        rootPane.addRow(1, output);
        rootPane.addRow(2, logarea);

        sendButton.setOnAction(e -> {
            connection.sendMsg(messageField.getText());
        });

        reconnectButton.setOnAction(e -> {
            connection.reconnect();
        });

        disconnectButton.setOnAction(e -> {
            connection.disconnect();
            stage.setTitle("Disconnected");
        });

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                connection.disconnect();
                System.exit(0);
            }
        });
    }

    public void output(String msg) {
        Platform.runLater(() -> {
            this.output.appendText(msg+"\n");
        });
    }

    public void setTitle(String msg) {
        Platform.runLater(() -> {
            this.stage.setTitle(msg);
        });
    }

    public void addLogLine(String msg) {
        Platform.runLater(() ->{
            this.logarea.appendText(msg+"\n");
        });
    }
}
