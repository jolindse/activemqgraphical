package to.mattias;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by juan on 2017-03-13.
 */
public class AmqConnection {

    private final String url = "tcp://ec2-35-158-15-121.eu-central-1.compute.amazonaws.com:61616";
    private final String topic = "TESTTOPIC";

    private App app;
    private Connection connection;
    private Session session;
    private Destination dest;
    private MessageProducer producer;
    private MessageConsumer cons;

    public AmqConnection(App app) {
        this.app = app;
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        try {
            connection = factory.createConnection();
            connection.start();


            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            dest = session.createTopic(topic);

            producer = session.createProducer(dest);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            cons = session.createConsumer(dest);

            MessageListener listener = new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    System.out.println("I GOT IT");
                    TextMessage text = (TextMessage) message;
                    try {
                        System.out.println("Message from AMQ: " + message);
                        app.output(text.getText() + " (received " + getTime() + ")" + "\n");
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            };
            cons.setMessageListener(listener);
            app.setTitle("Connected");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public boolean sendMsg(String msg) {
        try {
            System.out.println("Message to send: " + msg);
            TextMessage message = session.createTextMessage(this.getTime() + msg);
            producer.send(message);
            return true;
        } catch (JMSException e1) {
            e1.printStackTrace();
            return false;
        }
    }

    private String getTime() {
        Instant now = Instant.now();
        LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
        return String.format("%02d:%02d:%02d ", ldt.getHour(), ldt.getMinute(), ldt.getSecond());
    }
}
