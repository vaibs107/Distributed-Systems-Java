package p2p;

//Required imports
import java.rmi.Naming;


/**
 * The IndexingServer program provides a server for RMI invocation by the clients.
 * 
 * @author Vaibhav Uday Hongal
 * @version 1.0
 * @since 01-29-2017
 */

public class IndexingServer {

	public static void main(String[] args) throws Exception {
		ServerInterfaceImpl server = new ServerInterfaceImpl(); //Create a new instance of the server
		Naming.rebind("RMIServer", server); //bind the server with name RMIServer used in URL on peer client
		System.out.println("\nIndexing server is ready to take the requests.."); //display the server ready message
	}
}
