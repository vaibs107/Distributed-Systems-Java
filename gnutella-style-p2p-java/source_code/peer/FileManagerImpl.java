package peer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileManagerImpl extends UnicastRemoteObject implements FileManagerInterface{
	private List<String> msgRecord;
	public FileManagerImpl() throws RemoteException {
		super();
		msgRecord = new AssociativeArray<>(5);
	}

	@Override
	public byte[] retrieve(String fileName) throws RemoteException {
		try {
	         File file = new File(Config.shared_dir + File.separator +fileName);
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

	public List<String> searchForFile(QueryMessage qmsg) throws RemoteException{
		
		if(qmsg.getTtl()<=0){
			return null; //message has expired. do not process.
		}
		if(msgRecord.contains(qmsg.getMsgId())){
			return null;
		}
		else{
			msgRecord.add(qmsg.getMsgId());
		}
		
		File reqdFile = new File(Config.shared_dir + File.separator + qmsg.getFileName());
		
		List<String> availableHosts = new ArrayList<>();

		//check if the file exists in the local shared directory
		if(reqdFile.exists()){
			availableHosts.add(Config.myIpAddr);
		}
		
		//send query to neighbors and check if they have it.
		qmsg.decrementTtl();
		for(String neighbor : Config.neighbours){
			try {
				FileManagerInterface fmi = (FileManagerInterface) Naming.lookup("rmi://"+neighbor+"/filemanager");
				List<String> retList = fmi.searchForFile((QueryMessage) qmsg.duplicate());
				if(null!=retList){
					retList.removeAll(availableHosts);
					availableHosts.addAll(retList);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				e.printStackTrace();
			}
		}
		
		return availableHosts;
	}

}
