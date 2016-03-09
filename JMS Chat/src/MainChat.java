import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main class that reads connection arguments from user and to create instances of JMSTopic and JMSQueue
 * 
 * @author gabriel frassl
 * @version 1.0
 *
 */
public class MainChat {

	/**
	 * checks for the right amount of args and creates instances of JMSTopic and JMSQueue.
	 * then defines sequence of actions after every user CLI input
	 * 
	 * @param args arguments that are needed for chat connection
	 */
	public static void main(String[] args) {
		
		if (args.length != 3) {
			System.err.println("missing arguments! <ip_message_broker> <benutzername> <chatroom>");
		} else {
			JMSTopic topic = new JMSTopic("tcp://" + args[0] + ":61616", args[1], args[2]);
			JMSQueue queue = new JMSQueue("tcp://" + args[0] + ":61616", args[1], args[2]);

			System.out.println("Welcome to JMS chat \n"
					+ "Chat usage:\n"
					+ "to check mailbox: /mailbox\n"
					+ "to send private mail: /mail <destination_username> <message>");
			try {
			BufferedReader commandLine = new java.io.BufferedReader(new InputStreamReader(System.in)); //Reads CLI input of User
				while (true) { //programm sequence until /exit is being called
					String input;
					input = commandLine.readLine();
					String[] splitInput = input.split("\\s+");
					String[] ms = null;

					if (splitInput[0].equalsIgnoreCase("/exit")) { //when /exit close topic,queue and exit programm
						topic.drop();
						queue.close();
						System.exit(0);
					} else if (splitInput[0].equalsIgnoreCase("/mail")) {//when /mail call queue methods to send mail
						if (input.matches("/\\S+\\s+\\S+\\s+\\S+.+")) {
							ms = input.split("/\\S+\\s+\\S+\\s+");
							queue.sendMail(splitInput[1], ms[1]);
						} else {//if /mail is not correctly used
							System.err.println("Wrong Usage! \n"
						+"do : /mail <destination_username> <message>");
						}

					} else if (splitInput[0].equalsIgnoreCase("/mailbox")) {//if /mailbox call queue mailbox method
						queue.mailbox();

					} else { // in any other cases its a simple topic message -> call topic send method
						topic.sendMessage(input.toString());
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
