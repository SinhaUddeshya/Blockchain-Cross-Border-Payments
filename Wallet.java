import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

	public PrivateKey privateKey; // Sign transactions
	public PublicKey publicKey; // Address
	public Currencies myCurrency;

	public double conversion_rate;
	public HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>(); // only UTXOs owned by

	public Wallet(String Currency) throws NoSuchProviderException {
		myCurrency = FindCurrency(Currency);
		generateKeyPair();
	}

	public Wallet() throws NoSuchProviderException {
		generateKeyPair();
	}

	public void generateKeyPair() throws NoSuchProviderException {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// Initialize the key generator and generate a KeyPair
			keyGen.initialize(ecSpec, random); // 256 bytes provides an acceptable security level
			KeyPair keyPair = keyGen.generateKeyPair();
			// Set the public and private keys from the keyPair
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// returns balance and stores the UTXO's owned by this wallet in this.UTXOs
	public float getBalance() {
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item : Blockchain.UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			if (UTXO.isMine(publicKey)) { // if output belongs to me ( if coins belong to me )
				UTXOs.put(UTXO.id, UTXO); // add it to our list of unspent transactions.
				total += UTXO.value;
			}
		}
		return total;
	}

	// Generates and returns a new transaction from this wallet.
	public Transaction sendFunds(Wallet _recipient, float value, float reward) {

		if (getBalance() < value+reward) { // gather balance and check funds.
			System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
			return null;
		}

		// create array list of inputs
		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

		float total = 0;
		for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			total += UTXO.value;
			inputs.add(new TransactionInput(UTXO.id));
			if (total > value)
				break;
		}
		for (TransactionInput input : inputs) {
			UTXOs.remove(input.transactionOutputId);
		}
		
		Transaction newTransaction = new Transaction( this, _recipient, value, inputs);
		newTransaction.generateSignature(privateKey);
		Blockchain.AllTransactions.add(newTransaction);

	
		return newTransaction;
	}

	private Currencies FindCurrency(String currency) {

		if (currency.equals("GBP(£)"))
			return Currencies.GDB;
		else if (currency.equals("EUR(€)"))
			return Currencies.EUR;
		else if (currency.equals("USD($)"))
			return Currencies.USD;
		else if (currency.equals("CHF(F)"))
			return Currencies.CHF;
		else
			return Currencies.NZD;

	}

}