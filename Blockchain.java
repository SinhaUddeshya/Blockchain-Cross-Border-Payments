import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.*;

public class Blockchain {

	public static ArrayList<Block> AllBlocks;
	public static ArrayList<Peer> AllClients;
	public static ArrayList<Transaction> AllTransactions;

	public static Wallet coinBase;

	static Block LastBlock = null;
	public static Peer miner1;
	public static Peer miner2;
	public static int BlockHeigh = 0;
	static int size = 0;
//	static CoinBaseWallet coinbase;

	public static int difficulty = 5;

	private static Peer ActivePeer;

	public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>(); // list of all
																										// unspent

	public Blockchain() throws Exception {

		AllBlocks = new ArrayList<Block>();
		AllClients = new ArrayList<Peer>();
		AllTransactions = new ArrayList<Transaction>();
		coinBase = new Wallet("GBP(£)");
		Wallet funds = new Wallet("GBP(£)");
		Transaction genesisTransaction;

		genesisTransaction = new Transaction(funds, coinBase, 1000000000f, null);
		genesisTransaction.generateSignature(funds.privateKey); // manually sign the genesis transaction
		genesisTransaction.transactionId = "0"; // manually set the transaction id
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.amount,
				genesisTransaction.transactionId)); // manually add the Transactions Output
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); // its important to store
																							// our first transaction in
																							// the UTXOs list.
		AllTransactions.add(genesisTransaction);
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block(BlockHeigh++, "0", "0");
		LastBlock = genesis;
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);
		Addminers();
		ReadPeers();
		ReadTransactions();
	}

	private void ReadPeers() throws FileNotFoundException {
		// TODO Auto-generated method stub
		Scanner scn = new Scanner(new File("Peers"));
		String[] line;
		try {

			while (scn.hasNext()) {
				line = scn.nextLine().split("\\s+");
				AddnewPeer(line[0], Block.applySha256(line[2]), Integer.parseInt(line[1]), line[3]);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		scn.close();

	}

	private void ReadTransactions() throws FileNotFoundException {
		// TODO Auto-generated method stub
		
		Scanner scn = new Scanner(new File("Transactions"));
		String[] line;
		try {

			while (scn.hasNext()) {
				line = scn.nextLine().split("\\s+");
				sendMoney(lookForReceipter(line[0]),lookForReceipter(line[1]),Float.parseFloat(line[2]));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		scn.close();
		
	}

	private void Addminers() throws Exception {

		char[] password1 = { 'p', 'a', 's', 's' };
		miner1 = new Peer("Miner1", Block.applySha256(String.valueOf(password1)), 1, "GBP(£)");
		UpdateRestPeers(miner1);
		AllClients.add(miner1);
		BroadcastnewC(miner1);

		miner2 = new Peer("Miner2", Block.applySha256(String.valueOf(password1)), 1, "GBP(£)");
		UpdateRestPeers(miner2);
		AllClients.add(miner2);
		BroadcastnewC(miner2);
	}

//	public static void main(String[] args) throws NoSuchProviderException {	
//		//add our blocks to the blockchain ArrayList:
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncy castle as a Security Provider
//		
//		Blockchain myBlockchain = new Blockchain();
//		//walletA = new Wallet();
//		//walletB = new Wallet();	
//		
////		coinBase = new Wallet();
////		
//
////		Wallet wallet1 = new Wallet();
////		Wallet wallet2 = new Wallet();
////		
////		genesisTransaction = new Transaction(coinbase.publicKey, funds.publicKey, 1000000000f, null);
////		genesisTransaction.generateSignature(coinbase.privateKey);	 //manually sign the genesis transaction	
////		genesisTransaction.transactionId = "0"; //manually set the transaction id
////		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.amount, genesisTransaction.transactionId)); //manually add the Transactions Output
////		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
////				
////		System.out.println("Creating and Mining Genesis block... ");
////		Block genesis = new Block(BlockHeigh,"0","0");
////		LastBlock = genesis;
////		genesis.addTransaction(genesisTransaction);
////		addBlock(genesis);
////		
//
//		
////		System.out.println(isChainValid());*/
//		char[] password1 = {'p','a','s','s'};
//		Peer peer1 = new Peer("UD",password1,0,"gbp");
//		
//		char[] password2 = {'p','a','s','s'};
//		Peer peer2 = new Peer("Ky",password2,0,"gbp");
//
//		
//		Block block3 = new Block(BlockHeigh++,"transaction between found and Ky", LastBlock.hash);
//
//		System.out.println("\n UD wants to send 20 GBP to KY");
////		peer1.myWallet = wallet1;
////		peer2.myWallet = wallet2;
//
//		System.out.println(peer1.myWallet.getBalance());
//		System.out.println(peer2.myWallet.getBalance());
//		System.out.println("\n Money Transfered successfully");
//		block3.addTransaction(peer1.myWallet.sendFunds(peer2.myWallet, 20f));
//		addBlock(block3);
//
//		System.out.println("\n UD's new account balance is " +peer1.myWallet.getBalance() + peer1.myWallet.myCurrency);
//		System.out.println("\n Ky's new account balance is " +peer2.myWallet.getBalance()+ peer2.myWallet.myCurrency);
//		
//	}

	public static Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');

		// loop through blockchain to check hashes:
		for (int i = 1; i < AllBlocks.size(); i++) {
			currentBlock = AllBlocks.get(i);
			previousBlock = AllBlocks.get(i - 1);
			// compare registered hash and calculated hash:
			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
				System.out.println("Current Hashes not equal");
				return false;
			}
			// compare previous hash and registered previous hash
			if (!previousBlock.hash.equals(currentBlock.previousHash)) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
			// check if hash is solved
			if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined");
				return false;
			}

		}
		return true;
	}

	public static void addBlock(Block newBlock) {

//		System.out.println(BlockHeigh);
//		if (BlockHeigh == 1 || BlockHeigh == 2) {
		newBlock.mineBlock(difficulty);
		AllBlocks.add(newBlock);
//		} 
//		else {
//			Peer temp = null;
//			while (!miner1.available && !miner2.available) {
//				// JUST WAIT FOR AVAILABLE MINER
//			}
//			if (miner1.available)
//				temp = miner1;
//			else
//				temp = miner2;
//			newBlock.Miner=temp;
//			
//			
//			
//			temp.mineBlock(newBlock, difficulty);
//			AllBlocks.add(newBlock);
//		}
	}

	public ArrayList<Block> getBlocks() {
		return AllBlocks;
	}

	public ArrayList<Peer> getClients() {
		return AllClients;
	}

	public void BroadcastnewC(Peer newClient) {

		// if (AllClients.size()==1) return;

		for (Peer temp : AllClients) {
			if (newClient.equals(temp))
				continue;
			else
				temp.restPeers.add(newClient);
		}
	}

	public static void UpdateRestPeers(Peer newClient) {

		for (Peer temp : AllClients) {
			newClient.restPeers.add(temp);
		}
	}


	public static Peer lookForReceipter(String name) {

		for (Peer temp : AllClients) {
			if (temp.Name.equals(name))
				return temp;
		}
		return null;
	}

	public Peer checkValidUser(String text, String password) {

		for (Peer temp : AllClients) {
			if (text.equals(temp.Name) && validPassword(temp.password, password)) {
				return temp;
			}
		}
		return null;
	}

	private boolean validPassword(String a, String b) {

		return a.equals(b);

	}

	public static Peer activePeer() {
		return ActivePeer;
	}

	public void AddnewPeer(String Name, String pass, int type, String Currency) throws Exception {

		Peer newClient = new Peer(Name, pass, type, Currency);
		UpdateRestPeers(newClient);
		AllClients.add(newClient);
		BroadcastnewC(newClient);

	}

	public static boolean sendMoney(Peer Sender, Peer receipter, float amount) {

		Block newBlock = new Block(BlockHeigh++, "transaction between found and UD", LastBlock.hash);

		float reward = (float) (5.0 * amount / 100);
		amount = amount - reward;

		Peer temp = null;
		while (!miner1.available && !miner2.available) {
			// JUST WAIT FOR AVAILABLE MINER
		}
		if (miner1.available) {
			miner1.available = false;
			temp = miner1;
		} else {
			miner2.available = false;
			temp = miner2;
		}

		newBlock.Miner = temp;
		newBlock.block_reward = reward;

		if (newBlock.addTransaction(Sender.myWallet.sendFunds(receipter.myWallet, amount, reward))) {
			System.out.println("Successful");
			newBlock.addTransaction(Sender.myWallet.sendFunds(newBlock.Miner.myWallet, reward, 0));
			newBlock.addTransaction(coinBase.sendFunds(newBlock.Miner.myWallet, (float) 0.5, 0));
			System.out.println("\n Miner1's new account balance is " + newBlock.Miner.myWallet.getBalance());
		} else {
			newBlock.addTransaction(coinBase.sendFunds(newBlock.Miner.myWallet, (float) 0.5, 0));
			System.out.println("\n Miner1's new account balance is " + newBlock.Miner.myWallet.getBalance());
			Blockchain.addBlock(newBlock);
			LastBlock = newBlock;
			temp.available = true;
			return false;
		}

		Blockchain.addBlock(newBlock);
		Sender.AllBlocks.add(newBlock);
		receipter.AllBlocks.add(newBlock);
		LastBlock = newBlock;
		temp.available = true;
		System.out.println(isChainValid());
		return true;
	}

	public static String getAllBlocks() {
		String Blocks = "";
		String Space = "                          ";
		for (Block temp : AllBlocks) {
			Blocks += "      " + temp.height + Space + (int) ((System.nanoTime() - temp.age) / 1000000000)
					+ "s               " + ((temp.Miner == null) ? temp.Miner : temp.Miner.Name) + "\n";

		}
		return Blocks;
	}

	public static String getAllTransactions() {
		String Transactions = "";
		String Space = "                          ";
		for (Transaction temp : AllTransactions) {
			if (temp.transactionId.equals("0"))
				continue;
			Transactions += temp.transactionId + "\t\t" + temp.initialamount + "\t" + temp.initial_currency + "\t\t"
					+ temp.final_currency + "\n";
		}
		return Transactions;
	}

	public static boolean checkVali(Peer activePeer2) {

		if (activePeer2.AllBlocks.size() != AllBlocks.size())
			return false;

		return true;
	}

	public static void BroadcastBlockchain(Peer activePeer2) {

		for (Block temp : AllBlocks)
			if (!activePeer2.AllBlocks.contains(temp))
				activePeer2.AllBlocks.add(temp);
	}

	public static String lookPublicKey(String selectedItem) {
		
		for (Peer temp : AllClients) {
			if (temp.Name.equals(selectedItem))
				return temp.myWallet.publicKey.toString();
		}
		
		return null;
	}

}