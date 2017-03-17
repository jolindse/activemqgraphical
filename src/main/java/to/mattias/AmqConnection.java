package to.mattias;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by juan on 2017-03-13.
 */

public class AmqConnection {

    private final String url = "tcp://ec2-35-158-24-162.eu-central-1.compute.amazonaws.com:61616";
    private final String topic = "TESTTOPIC";

    private App app;

    private ActiveMQConnection connection;
    private ActiveMQConnectionFactory factory;

    public AmqConnection(App app) {
        this.app = app;
        factory = new ActiveMQConnectionFactory(url);
        init();
    }

    public void sendMsg(String msg) {
            reconnect();
            Session session = null;
            try {
                session = sessionFactory();
                Destination destination = destinationFactory(session);
                TextMessage message = session.createTextMessage(msg);
                MessageProducer producer = session.createProducer(destination);
                producer.send(message);
                producer.close();
                addLogLine("Sending message: "+msg);
            } catch (JMSException e1) {
                e1.printStackTrace();
            } finally {
                try {
                    if (session != null) {
                        session.close();
                    }
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
    }

    public void reconnect() {
        if (!connection.isStarted()) {
            try {
                connection.start();
                app.setTitle(factory.getBrokerURL());
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (connection.isStarted()) {
            try {
                connection.stop();
                addLogLine("Disconnecting...");
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }

    private Session sessionFactory() {
        try {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            return session;
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Destination destinationFactory(Session session) {
        try {
            Destination destination = session.createTopic(topic);
            return destination;
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addLogLine(String msg) {
        app.addLogLine(getTime() + msg);
    }

    private void init() {
        try {
            addLogLine("Connecting...");
            connection = (ActiveMQConnection)factory.createConnection();
            connection.setClientID("GUI client");
            reconnect();

            MessageListener listener = new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    TextMessage text = (TextMessage) message;
                    try {
                        app.output(getTime() + text.getText());
                        addLogLine("Received " + getTime());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            };

            Session session = sessionFactory();
            Destination destination = destinationFactory(session);
            Topic dest = session.createTopic(topic);
            MessageConsumer cons = session.createDurableSubscriber(dest, "guiclient");
            cons.setMessageListener(listener);
            getConnectionInfo(factory);
        } catch (JMSException e) {
            addLogLine("Error connecting to JMS Broker.");
            e.printStackTrace();
        }
    }

    private String getTime() {
        Instant now = Instant.now();
        LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
        return String.format("%02d:%02d:%02d ", ldt.getHour(), ldt.getMinute(), ldt.getSecond());
    }

    private void getConnectionInfo(ActiveMQConnectionFactory factory) throws JMSException {
        String jmsProvidername = connection.getMetaData().getJMSProviderName();
        addLogLine("JMSProviderName: " + jmsProvidername);
    }
}
