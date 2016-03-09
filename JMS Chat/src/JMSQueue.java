import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * Queue class which is used for user to user private messaging
 * 
 * @author gabriel frassl
 * @version 1.0
 *
 */
public class JMSQueue {
	private static Session session = null;
	private static Connection connection = null;
	private static MessageConsumer mailbox = null;
	private static MessageProducer mail = null;

	/**
	 * constructor that creates a connection, a Queue session, consumer and producer
	 * @param url connection ip
	 * @param user username
	 * @param topic chattopic
	 */
	public JMSQueue(String url, String user, String topic) {
		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, ActiveMQConnection.DEFAULT_PASSWORD, url);
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Destination destination = session.createQueue(user);
			mail = session.createProducer(destination);
			mailbox = session.createConsumer(destination);

		} catch (Exception e) {
			System.out.println("Queue connection failed: " + e.getMessage());
		}
	}
	
	
	/**
	 * method to display all the mails in the mailbox
	 */
	public void mailbox() {
		try {
			System.out.println("Mailbox:");
			TextMessage message;
			while ((message = (TextMessage) mailbox.receive(500)) != null) {
				System.out.println(message.getText());
			}
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * method for sending a message
	 * 
	 * @param r message receiver
	 * @param m the message text
	 */
	public void sendMail(String r, String m) {
		TextMessage message;
		try {
			Destination destination = session.createQueue(r);// queue is created with the param for the receiver
			System.out.println("mail sent");
			message = session.createTextMessage(m);
			mail.send(message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}


	/**
	 * droping the connection,session and mailbox
	 */
	public void close() {
		try {
			mailbox.close();
			session.close();
			connection.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
