package peer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileManagerInterface extends Remote{
	public byte[] retrieve(String fileName) throws RemoteException;
}
