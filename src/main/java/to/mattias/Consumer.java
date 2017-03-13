package to.mattias;

import javafx.scene.control.TextArea;

import javax.jms.*;

/**
 * Created by mattias on 2017-03-13.
 */
public class Consumer implements Runnable {

    private Session session;
    private TextArea output;
    private MessageConsumer cons;


    public Consumer(TextArea output, Session session, Destination destination) throws JMSException {
        this.output = output;
        this.session = session;
        this.cons = session.createConsumer(destination);
    }

    @Override
    public void run() {
        while(true) {
            try {
                Message message = cons.receive(1000);
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    output.appendText(textMessage.getText() + "\n");
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }

    }
}
