package assign1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class Client {

	/**The port on which the client contacts the host.*/
	public static final int HOST_PORT = 68;
	/**The default size of the buffer inside the receiver
	 * packets.*/
	public static final int BUFSIZ = 1024;
	
	/**The filename to be sent to the server.*/
	public static final String FILENAME = "test.txt";
	/**The mode included in the packet for the server*/
	public static final String FILEMODE = "ocTEt";

	private DatagramSocket socket;
	private DatagramPacket packet;

	public Client(){
		
	}
	
	/**
	 * The run method sends 11 packets to the server, through
	 * the host. The packets alternate between read and write
	 * requests. After sending a packet, the client listens for
	 * an acknowledgement from the server.
	 */
	private void run(){
		try{
			byte[] buf;
			//create DS for sending and receiving, port is irrelevant
			socket = new DatagramSocket();
			
			//repeat 11 times
			for(int i = 0; i < 11; i++){
			
				//first 2 bytes are 0 1 for read and 0 2 for write
				buf = new byte[]{0, (byte) (i%2+1)};
				//get the bytes from the filename string
				buf = concatenateArrays(buf, FILENAME.getBytes());
				//add 0 byte to indicate end of filename
				buf = concatenateArrays(buf, new byte[]{0});
				//add the bytes from a mode string
				buf = concatenateArrays(buf, FILEMODE.getBytes());
				//add 0 byte to show end of mode, no bytes after this
				buf = concatenateArrays(buf, new byte[]{0});
				//request 11 should be an invalid request, add byte after 3rd 0
				if(i == 10)
					buf = concatenateArrays(buf, new byte[]{0});
				//create DatagramPacket with data and length inside, to port 68 (host)
				packet = new DatagramPacket(buf, buf.length, InetAddress.getLocalHost(), HOST_PORT);

				//print out packet information as bytes and string
				//System.out.println("Sending: " + Arrays.toString(buf) + ", " + new String(buf));
				printDatagram("Sending to", packet.getPort(), buf);

				//send packet to port 68
				socket.send(packet);

				//create a receive packet who's buffer is larger than necessary
				buf = new byte[BUFSIZ];
				packet = new DatagramPacket(buf, buf.length);
				//wait to receive a response packet
				socket.receive(packet);
				
				//print packet data as bytes and string
				buf = new byte[packet.getLength()];
				System.arraycopy(packet.getData(), packet.getOffset(), buf, 0, packet.getLength());
				//System.out.println("Received: " + Arrays.toString(buf) + ", " + new String(buf));
				printDatagram("Received from", packet.getPort(), buf);
			}
			socket.close();
		}catch(Exception e){	
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
	 * Generates one byte array where array a is first
	 * and array b is second.
	 * 
	 * @param a occupies indexes 0 to len(a)-1 of new array
	 * @param b occupies indexes len(a) to len(a)+len(b)-1
	 * @return the concatenation of arrays a and b
	 */
	private byte[] concatenateArrays(byte[] a, byte[] b){
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	/**
	 * The main method creates a client instance and calls
	 * the run method.
	 * @param args command line arguments which are not used
	 */
	public static void main(String[] args) {
		new Client().run();
	}

}
