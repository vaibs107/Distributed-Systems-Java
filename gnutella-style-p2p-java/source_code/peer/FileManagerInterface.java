package peer;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FileManagerInterface extends Remote{
	public byte[] retrieve(String fileName) throws RemoteException;
	
	public List<String> searchForFile(QueryMessage qmsg) throws RemoteException;
}
