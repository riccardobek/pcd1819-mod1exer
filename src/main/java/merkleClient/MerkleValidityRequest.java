package merkleClient;

import java.io.IOException;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;
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
	 * 	<p>Uses the utility method {@link #isTransactionValid(String, List<String>) isTransactionValid} </p>
	 * 	<p>method to check whether the current transaction is valid or not.</p>
	 * */
	public Map<Boolean, List<String>> checkWhichTransactionValid() throws IOException, InterruptedException{
		//Creata la connessione con il server
		InetSocketAddress remoteAddress = new InetSocketAddress(authIPAddr,authPort);
		SocketChannel client = SocketChannel.open(remoteAddress);

		List<String> serverValue;
		List<String> trueValue = new ArrayList<>();
		List<String> falseValue = new ArrayList<>();

		//Passo al server le transazioni richieste e mi aspetto che mi restituisca una lista di stringhe
		//per poter procedere con la validazione mediante il metodo isTransationValid(--,--).
		for (String currentTransaction : mRequests) {

			//Inserisco nel buffer la mia transazione corrente e attendo risposta dal server
			byte[] message = currentTransaction.getBytes();
			ByteBuffer buffer = ByteBuffer.wrap(message);
			client.write(buffer);
			buffer.clear();

			/*
			* Attendere che il server carichi i dati al client
			* Controllare che non si siano verificati errori: superato tempo limite o buffer pieno
			* Procedere con l'inserimento di quanto ottenuto in una lista
			*/
			client.read(buffer);
			message = buffer.array();

			//Faccio in modo che il messagio che gli passo sia composto da strighe concatenate separate
			//da una virgola senza spazi
			serverValue = Stream.of(message.toString().split(",")).collect(Collectors.toList());
			buffer.clear();

			if(isTransactionValid(currentTransaction,serverValue))
				trueValue.add(currentTransaction);
			else
				falseValue.add(currentTransaction);

			Thread.sleep(10000);
		}

		//Chiusa la connessione
		client.close();


        Map<Boolean,List<String>> result = new HashMap<>();

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
	 *  @return: boolean value indicating whether this transaction was validated or not.
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