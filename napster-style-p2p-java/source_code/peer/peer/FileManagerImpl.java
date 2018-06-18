package peer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class FileManagerImpl extends UnicastRemoteObject implements FileManagerInterface{

	public FileManagerImpl() throws RemoteException {
		super();
	}

	@Override
	public byte[] retrieve(String fileName) throws RemoteException {
		try {
	         File file = new File(DirectoryScanner.shared_dir + File.separator +fileName);
	         byte buffer[] = new byte[(int)file.length()];
	         BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
	         input.read(buffer,0,buffer.length);
	         input.close();
	         return(buffer);
	      }catch(FileNotFoundException e){
	         return(null);
	      } catch (IOException e) {
		     return(null);
		}
	}


}
