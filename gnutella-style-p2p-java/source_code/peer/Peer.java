package peer;

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

public class Peer {

	public static void main(String[] args) {
		try {
			FileManagerImpl fileman = new FileManagerImpl();
			Naming.rebind("filemanager",fileman);
			System.out.println("File manager on the peer has started and ready for requests!!!");
			
			Config st = new Config(); // load the config
			
			while(true){
			//get the file name to download from the user:
			System.out.println("Please enter the name of the file to download or enter Q/q to exit: ");
		    Scanner scanner = new Scanner(System.in);			
		    String userResp = scanner.next();
			
		    if("q".equalsIgnoreCase(userResp)){
		    	System.out.println("Thank you for using our Peer to Peer file transfer "
		    			+ "system. Have a good day.");
		    	System.exit(0);
		    }
		    
			String fileToDownload = userResp;
			//get the location (peer) for the file from the index server and download the file
				//get the best peer for a file from the index server
				
				//start timer to get the time required for download
				final long lookupStartTime = System.nanoTime();
				
				//try to search the available hosts for the file
				QueryMessage qmsg = new QueryMessage(Config.myIpAddr +lookupStartTime , fileToDownload, Config.defaultTtl);
				List<String> availableHosts = fileman.searchForFile(qmsg);
				System.out.println(availableHosts);
				final long lookupDuration = System.nanoTime() - lookupStartTime;
				
				if(null!= availableHosts && availableHosts.size() > 0)
				{	
					//print the list of peers on console and ask user which peer to use
					System.out.println("The Index server returned following peers for this file. Please select one:");
					for(int i=0;i<availableHosts.size();i++){
						System.out.println(i+1+") "+availableHosts.get(i));
					}
					
					String peerToUse;
					//get the host from user which should be used
					while(true){
						System.out.println("Enter the number of the peer you want to use for downloading: ");
						int choice = scanner.nextInt();
						if(choice > availableHosts.size()){
							System.out.println("Incorrect choice. Please try again.");
						}
						else{
							peerToUse = availableHosts.get(choice-1);
							break;
						}
					}
					
					//select the first host returned by index server and try to download file
					String url = "rmi://"+peerToUse+"/filemanager";
					
					System.out.println("File Downloading started from " + peerToUse +".......");
					
					//start timer to get the time required for download
					final long downloadStartTime = System.nanoTime();
					
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
					final long downloadDuration = System.nanoTime() - downloadStartTime;
					System.out.println("File named "+fileToDownload+" downloaded successfully in the shared "
							+ "directory in "+(downloadDuration/1000000.00)+" msecs!!!\n\n");
					
					System.out.println("Time taken for lookup = " + lookupDuration/1000000.00 + " msecs.");
					System.out.println("Time taken for download = " + downloadDuration/1000000.00 + " msecs.");
					System.out.println("Total time taken = " + (lookupDuration/1000000.00 + downloadDuration/1000000.00) + " msecs.");

				} 
				else{
					System.out.println("No peer found for the file named "+ fileToDownload +". Please try again!");
					continue;
				}
				
				
			}
		} catch (RemoteException e) {
				e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}

}
