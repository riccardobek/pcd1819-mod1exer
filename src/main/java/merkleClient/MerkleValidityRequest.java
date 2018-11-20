package merkleClient;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.stream.Stream;


public class MerkleValidityRequest {

	/**
	 * IP address of the authority
	 * */
	private final String authIPAddr;
	/**
	 * Port number of the authority
	 * */
	private final int  authPort;
	/**
	 * Hash value of the merkle tree root. 
	 * Known before-hand.
	 * */
	private final String mRoot;
	/**
	 * List of transactions this client wants to verify 
	 * the existence of.
	 * */
	private List<String> mRequests;
	
	/**
	 * Sole constructor of this class - marked private.
	 * */
	private MerkleValidityRequest(Builder b){
		this.authIPAddr = b.authIPAddr;
		this.authPort = b.authPort;
		this.mRoot = b.mRoot;
		this.mRequests = b.mRequest;
	}
	
	/**
	 * <p>Method implementing the communication protocol between the client and the authority.</p>
	 * <p>The steps involved are as follows:</p>
	 * 		<p>0. Opens a connection with the authority</p>
	 * 	<p>For each transaction the client does the following:</p>
	 * 		<p>1.: asks for a validityProof for the current transaction</p>
	 * 		<p>2.: listens for a list of hashes which constitute the merkle nodes contents</p>
	 * 	<p>Uses the utility method {@link @code #isTransactionValid(String, List<String>) isTransactionValid}</p>
	 * 	<p>method to check whether the current transaction is valid or not.</p>
	 * */
	public Map<Boolean, List<String>> checkWhichTransactionValid() throws IOException, InterruptedException{


		//Creata la connessione con il server
		InetSocketAddress remoteAddress = new InetSocketAddress(authIPAddr, authPort);
		SocketChannel client = SocketChannel.open(remoteAddress);

		//Inizializzo le variabili necessarie
		List<String> serverValues;
		Map<Boolean,List<String>> result = new HashMap<>();
		List<String> trueValue = new ArrayList<>();
		List<String> falseValue = new ArrayList<>();

		//Passo al server le transazioni richieste e mi aspetto che mi restituisca una lista di stringhe
		//per poter procedere con la validazione mediante il metodo isTransationValid(--,--).
		for (String currentTransaction : mRequests) {

			//trasformo in byte il messaggio da passare al server
			byte[] message = currentTransaction.getBytes();

			/*
			* Specifico la dimensione del buffer in base a quanto specificato dal server a cui sono connesso
			* in questo modo è il server a doversi preoccupare di quanti byte passare per poter procedere alla
			* validazione e non il client che non sa quanto aspettarsi.
			*/
			ByteBuffer buffer = ByteBuffer.allocate(client.socket().getSendBufferSize());
			//Inserisco il messaggio nel buffer
			buffer.put(message);
			//Lo scrivo nel SocketChannel in modo che il server possa legerlo
			client.write(buffer);
			//Reindicizzo il buffer
			buffer.clear();

			//Leggo la risposta del server
			client.read(buffer);
			/*Prendo il messaggio in byte, lo converto in stringa, dividendolo in presenza di " , " e le metto in lista.
			* Le stringhe nel serverValues sono tutte già codificate in md5
			*/
			serverValues = (Stream.of(Arrays.toString(buffer.array()).split(" , ")).collect(Collectors.toList()));

			//Individuo a quale lista appartiene la mia transazione corrente
			if(isTransactionValid(currentTransaction,serverValues))
				trueValue.add(currentTransaction);
			else
				falseValue.add(currentTransaction);

			Thread.sleep(5000);

		}
		//Chiusa la connessione
		client.close();

		//Inserisco il risultato di quanto ho ottenuto dalla comunicazione con il server
        result.put(true,trueValue);
		result.put(false,falseValue);

		return result;
	}
	
	/**
	 * 	Checks whether a transaction 'merkleTx' is part of the merkle tree.
	 * 
	 *  @param merkleTx String: the transaction we want to validate
	 *  @param merkleNodes String: the hash codes of the merkle nodes required to compute 
	 *  the merkle root
	 *  
	 *  @return : boolean value indicating whether this transaction was validated or not.
	 * */
	private boolean isTransactionValid(String merkleTx, List<String> merkleNodes) {
		String computedRoot = merkleTx;
		for (String merkleNode : merkleNodes) {
			computedRoot += merkleNode;
			computedRoot = HashUtil.md5Java(computedRoot);
		}

		return  mRoot.equals(computedRoot);
	}

	/**
	 * Builder for the MerkleValidityRequest class. 
	 * */
	public static class Builder {
		private String authIPAddr;
		private int authPort;
		private String mRoot;
		private List<String> mRequest;	
		
		public Builder(String authorityIPAddr, int authorityPort, String merkleRoot) {
			this.authIPAddr = authorityIPAddr;
			this.authPort = authorityPort;
			this.mRoot = merkleRoot;
			mRequest = new ArrayList<>();
		}
				
		public Builder addMerkleValidityCheck(String merkleHash) {
			mRequest.add(merkleHash);
			return this;
		}
		
		public MerkleValidityRequest build() {
			return new MerkleValidityRequest(this);
		}
	}
}