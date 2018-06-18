package p2p;

//Required imports
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The ServerInterfaceImpl program implements a server in P2P Napster-style File
 * transferring application, to which the clients connect and register their
 * filenames from directory.
 * 
 * It provides a lookup module where in a client can search filenames, and
 * gets in return the list of peerIds of the peers which have the file.
 * 
 * It also provides a delete mechanism using which clients can delete the files 
 * in their shared folder and indexing server is notified of the same.
 *
 * @author Vaibhav Uday Hongal
 * @version 1.0
 * @since 01-29-2017
 */

public class ServerInterfaceImpl extends UnicastRemoteObject implements ServerInterface{
	
	private static final long serialVersionUID = 1L;
	public static ConcurrentHashMap<String, ArrayList<Integer>> listOfFiles = new ConcurrentHashMap<String, ArrayList<Integer>>(); //A ConcurrentHashMap to store the filenames and list of peerIds, needed for the lookup module 
	public static ArrayList<Integer> peerList; //An ArrayList to hold the peerIds corresponding to each filename which are used as values in above HashMap
	public static ConcurrentHashMap<Integer, String> ipAddress = new ConcurrentHashMap<Integer, String>(); //A ConcurrentHashMap that maps the peerIds and ipAddresses of the respective peers
	
	//Default constructor
	protected ServerInterfaceImpl() throws RemoteException {
		super();
	}
	
	/**
	 * Method implements the peer client registration process
	 * @param peerId
	 * @param list of fileNames to be registered with indexing server
	 * @return confirmation message	
	 */
	@Override
	public String registry(int peerId, List<String> fileNames) throws RemoteException { 
		try {
			if(!ipAddress.containsKey(peerId))
				ipAddress.put(peerId, RemoteServer.getClientHost()); //Get the ipAddress of the peer currently registering
		} catch (ServerNotActiveException e) {
			e.printStackTrace();
		}
		for(int i = 0; i<fileNames.size(); i++){
			if(listOfFiles.containsKey(fileNames.get(i))){ //Check if the files list with server has the incoming filename registered with it
				if(!listOfFiles.get(fileNames.get(i)).contains(peerId)){ //If the filename is already registered, check if the peerId is registered against the filename
					listOfFiles.get(fileNames.get(i)).add(peerId);
				}
			}else{
				peerList = new ArrayList<Integer>(); //Create and add the new peerIds if not already present in the peer list for that file
				peerList.add(peerId);
				listOfFiles.put(fileNames.get(i), peerList);
			}
		}
		System.out.println("\nTotal " + fileNames.size() + " files registered with the Indexing Server from peer"
				+ " with peerId: " + peerId);
		
		/*System.out.println("HashMap: \n");
		for(Map.Entry e : listOfFiles.entrySet())
			System.out.println(e.getKey() + " : " + e.getValue());
		
		System.out.println("Number of files for each peer are:\n");
		for(Map.Entry e : listOfFiles.entrySet()){
			List<Integer> list = new ArrayList<Integer>();
			list = (List<Integer>) e.getValue();
			for(int z = 0; z < list.size() ; z++)
				System.out.println(Collections.frequency(list, list.get(z)));
		}*/
		
		return "PeerId and Filenames registered with Indexing Server"; //return the control back to peer with confirm message
	}

	/**
	 * Method implementing the file lookup functionality
	 * @param fileName to be searched
	 * @return list of IP Addresses of the peers that have the file being searched 
	 * 		   or NULL if no peer has the file specified
	 * @throws RemoteException
	 */
	@Override
	public List<String> lookup(String fileName) throws RemoteException { 
		List<String> ipAddresses = new ArrayList<String>(); //a local ArrayList of String to hold the IP Addresses of the matching peers
		if(listOfFiles.containsKey(fileName))
		{
			for(int j = 0; j<listOfFiles.get(fileName).size(); j++)
				ipAddresses.add(ipAddress.get(listOfFiles.get(fileName).get(j))); //get the IP Address of the peer which has the file and add to the list
			return ipAddresses; //return all the peers' IP Addresses
		}
		return null; //if the file does not exist return null to the requester
	}
	
	/**
	 * Method implementing the delete mechanism used for removing the files from shared directory of peer client
	 * @param peerId and fileName to be removed
	 * @return status of delete process (true - success, false - failed)
	 * @throws RemoteException
	 */
	public boolean delete(int peerId, List<String> fileNames) throws RemoteException
	{
	    if(fileNames.size() > 0){
	    	System.out.println("\nList of files deleted from server for peerId " + peerId +" are:\n");
		    for(int i = 0; i<fileNames.size(); i++){
				if(listOfFiles.get(fileNames.get(i)).size() >= 1) //if the file has more than one peer, then verify the peerId requesting for deletion and remove only that peer from the list
				{
					System.out.println(fileNames.get(i) + "\n");
					try{
						listOfFiles.get(fileNames.get(i)).remove(listOfFiles.get(fileNames.get(i)).indexOf(peerId)); //if a peer tries to delete the file which is not registered with its id, an exception is thrown
					}catch(Exception e){
						//System.out.println("\nThe file does not belong to you. Please select a file that belongs to you for deletion..");
						return false;
					}
					if(listOfFiles.get(fileNames.get(i)).size() == 0) //check if, for the file specified there are no more peers left and remove the file entry from the server
						listOfFiles.remove(fileNames.get(i));
					return true; //successful deletion
				    }
				} 
	    }
		return false; //deletion failed
	}
}
