import javax.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Topic class which implements MessageListener and is used for standard broadcast messaging
 * 
 * @author gabriel frassl
 * @version 1.0
 *
 */
public class JMSTopic implements MessageListener {
	private String user = null;
	private Session session = null;
	private Connection connection = null;
	private MessageProducer producer = null;
	private Destination destination = null;
	private MessageConsumer consumer = null;

	/**
	 * constructor that creates a connection, a topic session, consumer and producer
	 * @param url connection ip
	 * @param user username
	 * @param topic chattopic
	 */
	public JMSTopic(String url, String user, String topic) {
		this.user = user;
		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, ActiveMQConnection.DEFAULT_PASSWORD, url);
			connection = connectionFactory.createConnection();
			connection.start();

			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			destination = session.createTopic(topic);
			
			producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			consumer = session.createConsumer(destination);
			consumer.setMessageListener(this);

		} catch (Exception e) {
			System.out.println("Connection failed: \n " + e.getMessage());
		}
	}

	/**
	 * method for sending a message
	 * 
	 * @param m the message that will be send
	 */
	public void sendMessage(String m) {
		try {
			TextMessage message;
			message = session.createTextMessage(user + ": " + m);
			producer.send(message);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * method that prints out messages sent by other users
	 */
	public void onMessage(Message message) {
		try {
			System.out.println(((TextMessage) message).getText());
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * drops session
	 */
	public void drop() {
		try {
			producer.close();
			consumer.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
