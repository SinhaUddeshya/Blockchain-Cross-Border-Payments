import java.security.*;
import java.util.ArrayList;

public class Transaction {
	
	public String transactionId; // this is also the hash of the transaction.
	public PublicKey sender; // senders address/public key.
	public PublicKey reciepient; // Recipients address/public key.
	public float initialamount;
	public float amount;
	public byte[] signature; // this is to prevent anybody else from spending funds in our wallet.
	public Currencies initial_currency;
	public Currencies final_currency;
	public float conversion_rate;
	public Boolean success;
	private long timeStamp; 
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	private static int sequence = 0; // a rough count of how many transactions have been generated. 
	
	// Constructor: 
	public Transaction(Wallet from, Wallet to, float value,  ArrayList<TransactionInput> inputs) {
		this.sender = from.publicKey;
		this.reciepient = to.publicKey;
		this.initial_currency = from.myCurrency;
		this.final_currency = to.myCurrency;
		this.initialamount = value;
		this.amount = Currencies.ConvertingCur(initial_currency, final_currency, value);
		this.inputs = inputs;
		
	}
	
	// This Calculates the transaction hash (which will be used as its Id)
	private String calulateHash() {
		sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
		return Block.applySha256(
				Block.getStringFromKey(sender) +
				Block.getStringFromKey(reciepient) +
				Float.toString(amount) + sequence
);
}
//Signs all the data we dont wish to be tampered with.
	public void generateSignature(PrivateKey privateKey) {
	String data = Block.getStringFromKey(sender) + Block.getStringFromKey(reciepient) + Float.toString(amount)	;
	signature = Block.applyECDSASig(privateKey,data);		
}
//Verifies the data we signed hasnt been tampered with
	public boolean verifiySignature() {
	String data = Block.getStringFromKey(sender) + Block.getStringFromKey(reciepient) + Float.toString(amount)	;
	return Block.verifyECDSASig(sender, data, signature);
}
	
	//Returns true if new transaction could be created.	
	public boolean processTransaction() {
			
			if(!verifiySignature()) {
				System.out.println("#Transaction Signature failed to verify");
				return false;
			}
					
			//gather transaction inputs (Make sure they are unspent):
			for(TransactionInput i : inputs) {
				i.UTXO = Blockchain.UTXOs.get(i.transactionOutputId);
			}

			//check if transaction is valid:
			/*if(getInputsValue() < Blockchain.minimumTransaction) {
				System.out.println("#Transaction Inputs to small: " + getInputsValue());
				return false;
			}*/
			
			//generate transaction outputs:
			float leftOver = getInputsValue() - initialamount; //get value of inputs then the left over change:
			transactionId = calulateHash();
			outputs.add(new TransactionOutput( this.reciepient, amount,transactionId)); //send value to recipient
			outputs.add(new TransactionOutput( this.sender, leftOver,transactionId)); //send the left over 'change' back to sender		
					
			//add outputs to Unspent list
			for(TransactionOutput o : outputs) {
				Blockchain.UTXOs.put(o.id , o);
			}
			
			//remove transaction inputs from UTXO lists as spent:
			for(TransactionInput i : inputs) {
				if(i.UTXO == null) continue; //if Transaction can't be found skip it 
				Blockchain.UTXOs.remove(i.UTXO.id);
			}
			
			return true;
		}
		
	//returns sum of inputs(UTXOs) values
		public float getInputsValue() {
			float total = 0;
			for(TransactionInput i : inputs) {
				if(i.UTXO == null) continue; //if Transaction can't be found skip it 
				total += i.UTXO.value;
			}
			return total;
		}

	//returns sum of outputs:
		public float getOutputsValue() {
			float total = 0;
			for(TransactionOutput o : outputs) {
				total += o.value;
			}
			return total;
	}
}