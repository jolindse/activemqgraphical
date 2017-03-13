package to.mattias;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;


/**
 * Created by mattias on 2017-03-13.
 */
public class App extends Application {
    private Stage stage;
    private Session session;
    private Connection connection;
    private Destination dest;
    private MessageProducer producer;
    private TextArea output;
    private final String url = "tcp://ec2-35-158-15-121.eu-central-1.compute.amazonaws.com:61616";
    private final String queue = "TESTQUEUE";

    public void start(Stage primaryStage) throws Exception {
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

        connectToBroker();

        button.setOnAction(e -> {
            try {
                TextMessage message = session.createTextMessage(messageField.getText());
                producer.send(message);
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
        });

    }

    public static void main(String[] args) {
        launch(args);
    }

    private void connectToBroker() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        try {
            connection = factory.createConnection();
            connection.start();
            stage.setTitle("Connected to " + factory.getBrokerURL());

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            dest = session.createQueue(queue);

            producer = session.createProducer(dest);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);



            Platform.runLater(() -> {
                try {
                    Thread t = new Thread(new Consumer(output, session, dest));
                    t.start();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            });



        } catch (JMSException e) {
            stage.setTitle("Could not connect");
            e.printStackTrace();
        }




    }
}
