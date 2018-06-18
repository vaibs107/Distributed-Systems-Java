package p2p;

//Required imports
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The ServerInterface program provides a server interface for P2P Napster-style File
 * transferring application.
 *
 * @author Vaibhav Uday Hongal
 * @version 1.0
 * @since 01-29-2017
 */

public interface ServerInterface extends Remote {
	/**
	 * Method used for register mechanism for a peer client to register 
	 * all its files with the indexing server.
	 */
	public String registry(int peerId, List<String> fileName) throws RemoteException;
	
	/**
	 * Method for searching the indexes and 
	 * returning the list of IP Addresses of the peers to the requester that have the specified file
	 */
	public List<String> lookup(String fileName) throws RemoteException; 
	
	/**
	 * Method implements the polling mechanism, invoked by peer client to delete the files from shared directory
	 */
	public boolean delete(int peerId, List<String> fileName) throws RemoteException; 
}