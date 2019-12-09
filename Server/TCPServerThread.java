
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;






public class TCPServerThread extends Thread{

	private Socket clientSocket;
	String hostNameFinal = null;
	public TCPServerThread(Socket clientSocket, int port) {

		this.clientSocket = clientSocket;
	}

	@Override
	public void run() {

		try {

			Socket sckt1 = clientSocket;
			

			
			// Set Input Output Streams and Buffer
			
			ObjectInputStream  ois = null; 
		    ObjectOutputStream oos = null;
			ois = new ObjectInputStream (sckt1.getInputStream());
			oos = new ObjectOutputStream(sckt1.getOutputStream());

			
			// Getting Client's hostname 
			String clienthostname = null;
			clienthostname = (ois.readObject()).toString();
			String clientInfo[] = clienthostname.split(" ");
			hostNameFinal = clientInfo[0];

			
		

				System.out.println("Connected to: " + " IP "+ clientInfo[0]+" Port No "+ clientInfo[1] );
				
				//Send message to Client about successful connection
				oos.writeObject("HOST ADDED SUCCESSFULLY" + "\n");

				
				
				// Register client/Peer with the Server
				int clientPort =  Integer.parseInt(clientInfo[1]);
				//int port =1555;
				TCPServer.addPeer(new Peer(hostNameFinal, clientPort));
				
				// Request Received from Client
				
				


				while (!sckt1.isClosed()) {
					String ClientRequest;
					try {
						ClientRequest = (String) ois.readObject();
					} catch (SocketException se) {
						System.out.println("Client closed Connection!");
						TCPServer.removePeerRFC(hostNameFinal);
						break;
					}
					//System.out.print(ClientRequest);
					String request[] = ClientRequest.split(" ");
						// // Process client input according to the request
						// command
					System.out.println("Received :" + request[0]);
						switch (request[0]){
						
					case "CLOSE":
					{
											
						System.out.println("Client Closing Socket ....");
						TCPServer.removePeerRFC(clienthostname);
						//sckt1.close();
						System.out.println("Removed entries.");
						return;
					}
						
						case "ADD":

						{
							int rfcNo = Integer.parseInt((String) ois.readObject());
							String version	= (String) ois.readObject();
							String hostName = (String) ois.readObject();
							int portNo = Integer.parseInt((String)ois.readObject());
							String rcfTitle = (String) ois.readObject();
							//System.out.println("Test : rcf Title - " + rcfTitle );

							//clientPort = Integer.parseInt((String) ois.readObject());
							System.out.println("RFC : "+rfcNo+" "+ rcfTitle + " Added successfully");
							oos.writeObject(version+ " " +"200 OK" +"\n"+ "RFC"+ " "+rfcNo+" "+ rcfTitle+" "+ hostName+" "+portNo );
							//oos.writeObject("RFC Added successfully");
							oos.flush();
							
							// Update peer List at the Server 
							Peer p = new Peer(hostName, portNo);
							TCPServer.addRFC(rfcNo,rcfTitle,p);
						


						} 
						break;
						
						case "LIST":
						{	
							System.out.println("Received LIST command");
							
							ArrayList<Index> indexList = TCPServer.getAllRFCs();
							
							Iterator<Index> indexItr=indexList.iterator();

							if (!(indexList.isEmpty()))
										
							  {
									oos.writeObject("P2P-CI/1.0 200" + "\n");
		
									 while(indexItr.hasNext())  
								        {
										 	Index index = indexItr.next();
										 	oos.writeObject(index.getRfcNo()+" "+ index.getRfcTitle()+ " "+ index.getPeer().getHostname()+" "+index.getPeer().getPort());
		
								        }
									 oos.writeObject("end");
							  }
							else 
							{
								oos.writeObject("P2P-CI/1.0 404 Not Found" + "\n");
								//oos.writeObject("Error : 404 Not Found");
							}
							
						}
						break;
						case "LOOKUP" :
						{
							
							System.out.println("Received LOOKUP command");
							String rfcName = (String) ois.readObject();
							int rfcNo =  Integer.parseInt(rfcName);
							//String rfcTitle = (String) ois.readObject();
							ArrayList<Index> resList = TCPServer.findRFC(rfcNo);
							
							
							if (!(resList.isEmpty()))
								{
									oos.writeObject("P2P-CI/1.0 200 OK" + "\n");
									//Changes :  removed RFC Title
									for(Index i:resList){
									 	oos.writeObject(i.getRfcNo()+" "+ i.getRfcTitle()+ " "+ i.getPeer().getHostname()+" "+i.getPeer().getPort());
										//oos.writeObject(rfcName+" "+ " "+p.getHostname()+" "+Integer.toString(p.getPort()));
									}
									 oos.writeObject("end");
							    }
							else 
								{
								//System.out.println("Debug : No Item LOOKUP command");

										oos.writeObject("P2P-CI/1.0 404 Not Found" + "\n");
										//oos.writeObject("Error : 404 Not Found");
								}
							
							
						} 
						break;
						default : 
							sckt1.close();

													
						}

				}

			
		} catch (IOException | ClassNotFoundException e1) {

			e1.printStackTrace();
		}
	}

}

