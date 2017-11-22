package assign1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * The server listens on a public port. Depending on
 * the type of incoming message the server returns a 
 * unique packet. Apart from the first incoming packet,
 * all other packets are sent from sockets which are 
 * created as needed.
 */
public class Server {

	/**A constant to represent the reception
	 * of a read request.*/
	public static final int RDQ = 1;
	/**A constant to represent the reception
	 * of a write request.*/
	public static final int WRQ = 2;
	/**The constant to represent an invalid
	 * packet contents*/
	public static final int INV = -1;
	
	/**The public port on which the server listens.*/
	public static final int SERVER_PORT = 69;
	
	/**The default size for the buffer inside the
	 * datagram packet.*/
	public static final int BUFSIZ = 1024;

	private DatagramSocket socket;

	public Server(){
		
	}
	/**
	 * The run method listens on the public server port.
	 * When a packet is received, the server prints out
	 * the contents of the packet. If the packet is a read
	 * request, the bytes 0301 are sent back to the client.
	 * If the packet is a write request, the bytes 0400 are
	 * sent back to the client. In case of an invalid packet,
	 * an exception is thrown.
	 */
	private void run() {
		try {

			//create DS for receiving at port 69
			socket = new DatagramSocket(SERVER_PORT);
			int result;
			byte[] buf;

			//repeat forever
			while(true){

				//wait to receive request on port 69
				buf = new byte[BUFSIZ];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);

				//verify that packet is either read or write request
				buf = new byte[packet.getLength()];
				System.arraycopy(packet.getData(), packet.getOffset(), buf, 0, packet.getLength());
				result = verifyData(buf);
				//print out information received as bytes and string
				//System.out.println("Received: " + Arrays.toString(buf) + ", " + new String(buf));
				printDatagram("Received from", packet.getPort(), buf);

				//if request is to read, prepare 0301 byte response
				if(result == RDQ)
					buf = new byte[]{0,3,0,1};
				//if request is to write, prepare 0400 byte response
				else if(result == WRQ)
					buf = new byte[]{0,4,0,0};
				//if invalid, throw exception and quit
				else
					throw new Exception("invalid packet");

				//create response packet with appropriate byte response
				packet = new DatagramPacket(buf, buf.length, InetAddress.getLocalHost(), packet.getPort());
				//print response information in bytes and string
				//System.out.println("Sending: " + Arrays.toString(buf) + ", " + new String(buf));
				printDatagram("Sending to", packet.getPort(), buf);

				//create DS for use on just this request
				DatagramSocket tempSocket = new DatagramSocket();
				//send packet via new socket to port received in request
				tempSocket.send(packet);
				//close socket that was just created
				tempSocket.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
			socket.close();
			System.out.println(e.getMessage());
		}

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
	 * The data verification class checks that the
	 * data is consistent with the format presented in
	 * the assignment description. The byte array must
	 * start with 01 or 02, then it must be followed by 
	 * a file name, then a 0, then a mode, then a 0 and
	 * then nothing else.
	 * 
	 *  @param	data	the byte array to be verified
	 *  @return			-1 (invalid),1 (read),2 (write) 
	 *  				depending on verification result
	 */
	private int verifyData(byte[] data){
		//check is data is null
		if(data == null)
			return INV;
		//check is data has minimum acceptable length 01n0m0 or 02n0m0 where n is filename and m is mode
		if(data.length < 6)
			return INV;
		//check first byte is 0
		if(data[0] != 0)
			return INV;
		//check second byte is 1 or 2
		if(data[1] != RDQ && data[1] != WRQ)
			return INV;

		//extract filename while checking for second 0 byte
		byte[] filename = new byte[0];
		int i;
		for(i = 2; i < data.length && data[i] != 0; i++){
			filename = concatenateArrays(filename, new byte[]{data[i]});
		}

		//check that filename has minimum length 1
		if(filename.length == 0)
			return INV;
		//check that there are enough bytes after second 0 for valid mode and end 0
		if(i >= data.length - 2)
			return INV;

		//extract mode while checking for final 0 byte
		byte[] filemode = new byte[0];
		for(i = i + 1; i < data.length && data[i] != 0; i++){
			filemode = concatenateArrays(filemode, new byte[]{data[i]});
		}

		//check that filemode is minimum length 1
		if(filemode.length == 0)
			return INV;
		//check that the 3rd 0 byte is the last byte in array
		if(data.length - 1 != i)
			return INV;
		//check that array ends in 0
		if(data[i] != 0)
			return INV;

		return data[1];		
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
	 * The main method initializes the server class
	 * and calls its run method.
	 * 
	 * @param args potential command line arguments
	 */
	public static void main(String[] args) {
		new Server().run();
	}

}
