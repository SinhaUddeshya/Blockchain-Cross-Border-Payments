import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Random;

public class Peer {

	boolean available = false;
	public String Name = null;
	public String password;
	public int UniqueNumber;
	public Wallet myWallet; // keys for security
	public int type; // miner or not

//	private ArrayList<Transaction> myTransactions; // all transaction that he involved in
	public ArrayList<Peer> restPeers; // rest peers of blockchain
	public ArrayList<Block> AllBlocks;		//Copy of whole Blockchain

	
	public Peer(String Name, String pass, int type, String Currency) throws Exception {
		
		AllBlocks= new ArrayList<Block>();
		this.Name = Name;
		password = pass;
		this.type = type;
		if (this.type == 1)
			available = true;

		restPeers = new ArrayList<Peer>();
		UniqueNumber = generateUnN();
		myWallet = new Wallet(Currency);
		Block newBlock = new Block(Blockchain.BlockHeigh++, "transaction between found and UD",
				Blockchain.LastBlock.hash);
		newBlock.addTransaction(Blockchain.coinBase.sendFunds(myWallet, 100f, 0));
		Blockchain.addBlock(newBlock);
		Blockchain.LastBlock = newBlock;

	}

	public static int generateUnN() throws Exception {
		Random generator = new Random();
		generator.setSeed(System.currentTimeMillis());

		int num = generator.nextInt(99999) + 99999;
		if (num < 100000 || num > 999999) {
			num = generator.nextInt(99999) + 99999;
			if (num < 100000 || num > 999999) {
				throw new Exception("Unable to generate PIN at this time..");
			}
		}
		return num;
	}
}