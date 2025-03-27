
import java.io.*;
import java.net.*;
import java.util.*;
public class EmailClient
{
	private static InetAddress host;
	private static final int PORT = 1234;
	private static String name;
	private static Scanner networkInput, userEntry;
	private static PrintWriter networkOutput;
	public static void main(String[] args)
			throws IOException
	{
		try
		{
			host = InetAddress.getLocalHost();
		}
		catch(UnknownHostException uhEx)
		{
			System.out.println("Host ID not found!");
			System.exit(1);
		}
		userEntry = new Scanner(System.in);
		do
		{
			System.out.print(
					"\nEnter name ('Dave' or 'Karen'): ");
			name = userEntry.nextLine();
		}while (!name.equals("Dave")
				&& !name.equals("Karen"));
		talkToServer();
	}
	private static void talkToServer() throws IOException
	{
		String option, message, response;
		do
		{
			/*******************************************************
	CREATE A SOCKET, SET UP INPUT AND OUTPUT STREAMS,
	ACCEPT THE USER'S REQUEST, CALL UP THE APPROPRIATE
	METHOD (doSend OR doRead), CLOSE THE LINK AND THEN
	ASK IF USER WANTS TO DO ANOTHER READ/SEND.
			 *******************************************************/
			Socket link = new Socket(host, PORT);
			networkInput = new Scanner(link.getInputStream());
			networkOutput = new PrintWriter(link.getOutputStream(), true);

			System.out.print("Enter option - send (s) or read (r): ");
			option = userEntry.nextLine();

			if (option.equalsIgnoreCase("s")) {
				doSend();
			} else if (option.equalsIgnoreCase("r")) {
				doRead();
			}

			link.close();

			System.out.print("Do you want to do another read/send? (y/n): ");
			option = userEntry.nextLine();
		}
		while (!option.equalsIgnoreCase("n"));
	}
	private static void doSend()
	{
		System.out.println("\nEnter 1-line message: ");
		String message = userEntry.nextLine();
		networkOutput.println(name);
		networkOutput.println("send");
		networkOutput.println(message);
	}
	private static void doRead() throws IOException
	{
		/*********************************
	BODY OF THIS METHOD REQUIRED
		 *********************************/
		networkOutput.println(name);
		networkOutput.println("read");
		int messageCount = Integer.parseInt(networkInput.nextLine());
		System.out.println("\nMessages for " + name + ":");
		for (int i = 0; i < messageCount; i++) {
			String response = networkInput.nextLine();
			System.out.println(response);
		}
	}
}
