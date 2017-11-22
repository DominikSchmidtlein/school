package assign1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * The Host lives in between the client and the server.
 * Currently, the Host simply forwards messages as they
 * arrive.
 */
public class Host {

	/**The port on which the host listens for the client.*/
	public static final int HOST_PORT = 68;
	/**The port to which the host forwards packets for the server.*/
	public static final int SERVER_PORT = 69;
	/**The default size of the buffer inside the packet.*/
	public static final int BUFSIZ = 1024;

	private DatagramSocket receiveSocket;
	private DatagramSocket serverSocket;
	private DatagramSocket clientSocket;

	private int clientPort;

	public Host(){
	}
	
	/**
	 * The run method listens for a packet from the client. It
	 * then prints the contents of the packet and generates a
	 * new packet with the same contents. The new packet is then
	 * forwarded to the server. <br></br>Similarly, the host then listens
	 * for a packet from the server. Upon reception, the contents
	 * of the packet are printed and a packet with the same
	 * contents is sent back to the client on a new socket.
	 */
	private void run(){
		try{
			//create DS to receive at port 68
			receiveSocket = new DatagramSocket(HOST_PORT);
			//create DS to send and receive, port doesn't matter
			serverSocket = new DatagramSocket();

			byte[] buf;
			DatagramPacket packet;

			//repeat forever
			while(true){
				//host waits to receive a request on port 68, buffer is larger than necessary
				buf = new byte[BUFSIZ];
				packet = new DatagramPacket(buf, buf.length);
				receiveSocket.receive(packet);
				
				//host prints out the information received as bytes and string
				buf = new byte[packet.getLength()];
				System.arraycopy(packet.getData(), packet.getOffset(), buf, 0, packet.getLength());
				//System.out.println("Received: " + Arrays.toString(buf) + ", " + new String(buf));
				printDatagram("Received from", packet.getPort(), buf);

				//host records client port for later use
				clientPort = packet.getPort();
				
				//host forms a packet containing identical data, for port 69
				packet = new DatagramPacket(buf, buf.length, InetAddress.getLocalHost(), SERVER_PORT);
				//print out information that is going to be send to server
				//System.out.println("Sending: " + Arrays.toString(buf) + ", " + new String(buf));
				printDatagram("Sending to", packet.getPort(), buf);

				//sends packet on send/receive socket to port 69
				serverSocket.send(packet);

				//wait for response packet
				buf = new byte[BUFSIZ];
				packet = new DatagramPacket(buf, buf.length);
				serverSocket.receive(packet);
				
				//print out information that is received as bytes and string
				buf = new byte[packet.getLength()];
				System.arraycopy(packet.getData(), packet.getOffset(), buf, 0, packet.getLength());
				//System.out.println("Received: " + Arrays.toString(buf) + ", " + new String(buf));
				printDatagram("Received from", packet.getPort(), buf);
				
				//create new packet with same data, to be sent back to client
				packet = new DatagramPacket(buf, buf.length, InetAddress.getLocalHost(), clientPort);
				//print out information to be sent to client
				//System.out.println("Sending: " + Arrays.toString(buf) + ", " + new String(buf));
				printDatagram("Sending to", packet.getPort(), buf);

				//create DS to use to send packet to client
				clientSocket = new DatagramSocket();
				//send packet to client
				clientSocket.send(packet);
				//close socket
				clientSocket.close();
				
			}

		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	/**
	 * Print the mode port and buffer contents as bytes and string.
	 * @param mode either Sending or Receiving
	 * @param port where the packet is received from or sent to
	 * @param buf the contents of the datagram packet
	 */
	private void printDatagram(String mode, int port, byte[] buf){
		System.out.println(mode + ": " + port);
		System.out.println("bytes: " + Arrays.toString(buf));
		System.out.println("string: " + new String(buf));
		System.out.println();
	}

	/**
	 * The main method creates a host instance
	 * and calls the run function.
	 * 
	 * @param args potential command line arguments
	 */
	public static void main(String[] args) {
		new Host().run();
	}

}
