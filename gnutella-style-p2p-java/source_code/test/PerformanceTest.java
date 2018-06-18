package test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;

import p2p.ServerInterface;
import peer.Config;
import peer.FileManagerImpl;
import peer.FileManagerInterface;
import peer.QueryMessage;

public class PerformanceTest {
	private static final int numOfRequests = 200;
	public static void main(String[] args) {
		if(args.length==0){
			System.out.println("USAGE : PerformanceTest <filename>");
			System.exit(0);
		}
		try {
			FileManagerImpl fileman = new FileManagerImpl();
			Naming.rebind("filemanager",fileman);
			System.out.println("File manager on the peer has started and ready for requests!!!");
			
			//schedule a periodic task which runs in every 10 seconds to scan
			//the shared directory and register/unregister files with index server
			Config st = new Config(); // Instantiate SheduledTask class
			
			System.out.println("Sending "+numOfRequests+" requests another peers. Let the fun begin!!!: ");
			//start timer to get the time required for download
			final long startTime = System.nanoTime();
			
			for(int j=0;j<numOfRequests;j++){
		    
			String fileToDownload = args[0];
			
			//get the location (peer) for the file from neighbors and download the file
			try {
				//get the best peer for a file from the neighbors
				QueryMessage qmsg = new QueryMessage(Config.myIpAddr +System.nanoTime() , fileToDownload, Config.defaultTtl);
				List<String> availableHosts = fileman.searchForFile(qmsg);
				if(null!= availableHosts && availableHosts.size() > 0)
				{	
					
					//select the first host returned by index server and try to download file
					String url = "rmi://"+availableHosts.get(0)+"/filemanager";
										
					//download the file from the peer returned by the index server
					FileManagerInterface fmi = (FileManagerInterface) Naming.lookup(url);
					byte[] filedata = fmi.retrieve(fileToDownload);
					if(null==filedata){
						System.out.println("The File not present on peer any more. Please try again!!!");
						continue;
					}
					File file = new File(Config.shared_dir + File.separator +fileToDownload);
					BufferedOutputStream output = new
					BufferedOutputStream(new FileOutputStream(file));
					output.write(filedata,0,filedata.length);
					output.flush();
					output.close();
				} 
				else{
					System.out.println("No peer found for the file named "+ fileToDownload +". Please try again!");
					continue;
				}
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				System.out.println("Sorry. File not found on peer. Please try again.");
				continue;
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
			final long duration = System.nanoTime() - startTime;
			System.out.println("Total time taken for "+numOfRequests+" requests= " + duration/1000000.00 +" msecs");
		} catch (RemoteException e) {
				e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		System.exit(0);
			
	}
}
