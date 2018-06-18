package peer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
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

public class Config{
	public final int peerId;
	public static String[] neighbours;
	public static String shared_dir;
	public static String myIpAddr;
	public static int defaultTtl;
	public Config() {		
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
		Config.shared_dir = prop.getProperty("shared_dir")!=null ? prop.getProperty("shared_dir") : "~/";
		
		//get all neighbours
		Config.neighbours = prop.getProperty("neighbours")!=null ? prop.getProperty("neighbours").split(","):null;
		
		//get default time to live for query messages
		
		this.defaultTtl = prop.getProperty("default_ttl")!=null ? Integer.parseInt(prop.getProperty("default_ttl")) : 3;
				
		//load my own IP address
		try {
			InetAddress ip = InetAddress.getLocalHost();
			Config.myIpAddr = ip.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
	}
}
