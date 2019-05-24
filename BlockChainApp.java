import java.awt.EventQueue;
import java.security.Security;

public class BlockChainApp {

	public BlockChainApp() {

	}
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {					
					Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncy castle as a Security Provider
					Blockchain myBlockchain = new Blockchain();
					new BlockchainGUI(myBlockchain);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

