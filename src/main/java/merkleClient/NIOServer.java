package nio.clientServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
 
public class NIOServer {
 
	public static final String END_OF_SESSION = "close";
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {
		Selector selector = Selector.open(); // selector is open here
		ServerSocketChannel serverSocket = ServerSocketChannel.open();
		InetSocketAddress localAddr = new InetSocketAddress("127.0.0.1", 2323);
		serverSocket.bind(localAddr);
		serverSocket.configureBlocking(false);
		int ops = serverSocket.validOps();
		SelectionKey selectKy = serverSocket.register(selector, ops, null);
		
		//Definisco il numero di byte che può ricevere il socket
		serverSocket.socket().setReceiveBufferSize(256);
		 
		while (true) {
			log("i'm a server and i'm waiting for new connection and buffer select...", "out");
			selector.select();
			Set<SelectionKey> activeKeys = selector.selectedKeys();
			Iterator<SelectionKey> keys = activeKeys.iterator();
			
			while (keys.hasNext()) {
				SelectionKey myKey = keys.next();
 
				if (myKey.isAcceptable()) {
					SocketChannel clientSocket = serverSocket.accept();
					clientSocket.configureBlocking(false);
					clientSocket.register(selector, SelectionKey.OP_READ);
					log("Connection Accepted: " + clientSocket.getLocalAddress() + "\n", "err");
				} else if (myKey.isReadable()) {
					SocketChannel clientSocket = (SocketChannel) myKey.channel();
					ByteBuffer buffer = ByteBuffer.allocate(256);
					clientSocket.read(buffer);
					String result = new String(buffer.array()).trim();
					log("--- Message received: " + result, "err" );

					//Esempio di stringa da passare
					String s = new String("639fc2398fd45606ada087e30168287b , 4dde77cd192e5101fe0a317e00ba3827");
					ByteBuffer bufferSend = ByteBuffer.wrap(s.getBytes());
					clientSocket.write(bufferSend);
					
					if (result.equals("close")) {
						clientSocket.close();
						log("\nIt's time to close this connection as we got a close packet", "out");
					}
				}
				//important: should delete, otherwise re-iterated the next turn again.
				keys.remove();
			}
		}
	}
 
	private static void log(String str, String mode) {
		switch(mode) {
			case "out": {System.out.println(str); break;}
			case "err": {System.err.println(str); break;}
			default: {}
		}
	}
}
