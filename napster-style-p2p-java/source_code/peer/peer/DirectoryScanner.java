package peer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimerTask;

import p2p.ServerInterface;

public class DirectoryScanner extends TimerTask {
	private Set<String> cachedFileList;
	public final int peerId;
	public final String indexServerUrl;
	public static String shared_dir;
	public DirectoryScanner() {
		this.cachedFileList = new HashSet<>();
		
		//load the peer id from properties file
		Properties prop = new Properties();
		try {
			InputStream input = new FileInputStream("config.properties");
			// load a properties file
			prop.load(input);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.peerId = prop.getProperty("peer_id")!=null ? Integer.parseInt(prop.getProperty("peer_id")) : 0;
		this.indexServerUrl = "rmi://" + (prop.getProperty("index_server_ip_addr")!=null ?
				prop.getProperty("index_server_ip_addr").trim() : "127.0.0.1")+ "/RMIServer";
		DirectoryScanner.shared_dir = prop.getProperty("shared_dir")!=null ? prop.getProperty("shared_dir") : "~/";
	}
	
	@Override
	public void run() {
		File sharedDir = new File(this.shared_dir);
		Set<String> currentFiles = new HashSet<>();
		for(File filename: sharedDir.listFiles()){
			currentFiles.add(filename.getName());
		}
		//check the files which has been newly added to shared directory
		List<String> newAddedFiles = new ArrayList<>(currentFiles);
		newAddedFiles.removeAll(cachedFileList);
		
		//check the files which has been deleted from the shared directory
		List<String> deletedFiles = new ArrayList<>(cachedFileList);
		deletedFiles.removeAll(currentFiles);
		try {
		if(newAddedFiles.size()>0)
		{
			ServerInterface indexServer = (ServerInterface) Naming.lookup(this.indexServerUrl);
			System.out.println(indexServer.registry(this.peerId, newAddedFiles));
			
			//cache the newly added files
			cachedFileList.addAll(newAddedFiles);
		}
		
		//Unregister the files deleted from the shared directory
		if(deletedFiles.size()>0){
			ServerInterface indexServer = (ServerInterface) Naming.lookup(this.indexServerUrl);
			indexServer.delete(this.peerId, deletedFiles);
			cachedFileList.removeAll(deletedFiles);
		}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		
		
	}

}
