

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;

public class TCPServer {

	static final int counter = 7734;
	static ArrayList<Peer> peerList;
	static ArrayList<Index> rfcList;
	
	TCPServer()
	{
		
	}
	
	
	public static void main(String args[]) throws IOException, InterruptedException {
		peerList = new ArrayList<Peer>();
		rfcList = new ArrayList<Index>();
		
		ServerSocket conn0 = new ServerSocket(counter);
		while (true) {
			Socket sckt0 = conn0.accept();
			TCPServerThread tcpserver = new TCPServerThread(sckt0,counter);
			tcpserver.start(); 
			System.out.println("Server Started :");

			Thread.sleep(100);

			// InputStreamReader inputReader = new
			// InputStreamReader(sckt0.getInputStream());
			// BufferedReader bufferReader = new BufferedReader(inputReader);
//			DataOutputStream outputStream = new DataOutputStream(sckt0.getOutputStream());
			// client_input = bufferReader.readLine();
			// System.out.println("Received: " + client_input);
//			outputStream.writeBytes(counter + "\n");
			
		}

	}
	
	public static void addRFC(int rfcNo,String rfcTitle,Peer peer){
		
		Index in = new Index();
		in.setRfcNo(rfcNo);
		in.setRfcTitle(rfcTitle);
		in.setPeer(peer);
		rfcList.add(in);
	}
	
	public static void addPeer(Peer peer){
		peerList.add(peer);
	}
	
	public static ArrayList<Index> findRFC(int rfcNo){
		
		ArrayList<Index> results = new ArrayList<Index>();
		for(Index i:rfcList){
			if(i.getRfcNo() == rfcNo)
				results.add(i);
		}
		return results;
	}
	
	public static ArrayList<Index> getAllRFCs(){
		return rfcList;
	}
	
	public static void removePeerRFC(String peerName) {
		//System.out.println("Finding entries to remove for :" +peerName);
		Collection<Index> removeList = new ArrayList<Index>();
		for (int i = 0;i<rfcList.size();i++) {
			if(rfcList.get(i).getPeer().getHostname().equals(peerName)) {
				removeList.add(rfcList.get(i));
				//System.out.println("Found one!");
			}
		}
		rfcList.removeAll(removeList);
		//peerList.remove(peer);
	}
}


