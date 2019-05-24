import java.awt.*;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.Security;

public class BlockchainGUI {

	private Blockchain blockchain;
	private JFrame login;
	private JFrame SignUp;

	/**
	 * Create the application.
	 */
	public BlockchainGUI(Blockchain myblockch) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		blockchain = myblockch;
		Login();
		login.setVisible(true);
	}

	private void SignUpScreen() {

		SignUp = new JFrame();
		SignUp.setTitle("Sign Up");
		SignUp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SignUp.setBounds(100, 100, 303, 270);
		SignUp.getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("Name:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel.setBounds(22, 25, 89, 28);
		SignUp.getContentPane().add(lblNewLabel);

		JLabel lblNewLabel_2 = new JLabel("At least 4 letters");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 10));
		lblNewLabel_2.setEnabled(false);
		lblNewLabel_2.setBounds(122, 51, 115, 16);
		SignUp.getContentPane().add(lblNewLabel_2);

		JLabel lblpass = new JLabel("Password:");
		lblpass.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblpass.setBounds(22, 79, 89, 16);
		SignUp.getContentPane().add(lblpass);

		JLabel lblCurrency = new JLabel("Currency:");
		lblCurrency.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblCurrency.setBounds(22, 123, 79, 16);
		SignUp.getContentPane().add(lblCurrency);

		JTextField textField = new JTextField();
		textField.setBounds(123, 26, 131, 28);
		SignUp.getContentPane().add(textField);
		textField.setColumns(10);

		JPasswordField passwordField = new JPasswordField();
		passwordField.setBounds(123, 74, 131, 28);
		SignUp.getContentPane().add(passwordField);

		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setBounds(123, 118, 131, 28);
		SignUp.getContentPane().add(comboBox);
		comboBox.addItem("GBP(£)");
		comboBox.addItem("EUR(€)");
		comboBox.addItem("USD($)");
		comboBox.addItem("CHF(F)");
		comboBox.addItem("NZD($)");

		JRadioButton rdbtnNewRadioButton = new JRadioButton("I would like to work as miner");
		rdbtnNewRadioButton.setBounds(22, 155, 228, 25);
		SignUp.getContentPane().add(rdbtnNewRadioButton);
		
		JButton btnNewButton = new JButton("Cancel");
		btnNewButton.setBounds(38, 185, 97, 25);
		SignUp.getContentPane().add(btnNewButton);
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				passwordField.setText("");
				textField.setText("");
				SignUp.setVisible(false);
				login.setVisible(true);
			}
		});
		JButton btnFinish = new JButton("Finish");
		btnFinish.setBounds(157, 185, 97, 25);
		SignUp.getContentPane().add(btnFinish);
		SignUp.setLocationRelativeTo(null);
		btnFinish.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (passwordField.getPassword().length == 0 || lblNewLabel.getText().isEmpty()) {
					JOptionPane.showMessageDialog(new JFrame(), "Please fill all fields", "Dialog",
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					try {
						int selected = 0;
						if (rdbtnNewRadioButton.isSelected())selected=1;
						
						blockchain.AddnewPeer(textField.getText(),
								Block.applySha256(String.valueOf(passwordField.getPassword())), selected ,
								(String) comboBox.getSelectedItem());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					passwordField.setText("");
					textField.setText("");
					SignUp.setVisible(false);
					login.setVisible(true);

				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void Login() {

		login = new JFrame();
		login.setTitle("Login");
		login.setBounds(100, 100, 317, 237);
		login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login.getContentPane().setLayout(null);
		JPasswordField passwordField;
		JTextField textField;

		passwordField = new JPasswordField();
		passwordField.setBounds(118, 87, 145, 30);
		login.getContentPane().add(passwordField);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblPassword.setBounds(23, 90, 80, 23);
		login.getContentPane().add(lblPassword);

		JLabel lblUserName = new JLabel("User Name:");
		lblUserName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblUserName.setBounds(23, 45, 95, 23);
		login.getContentPane().add(lblUserName);

		textField = new JTextField();
		textField.setBounds(118, 42, 145, 30);
		login.getContentPane().add(textField);
		textField.setColumns(10);

		JButton btnNewButton = new JButton("Login");
		btnNewButton.setBounds(140, 134, 115, 30);
		login.getContentPane().add(btnNewButton);

		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				Peer temp = blockchain.checkValidUser(textField.getText(),
						Block.applySha256(String.valueOf(passwordField.getPassword())));
				if (temp != null) {
					login.setVisible(false);
					PeerThread user = new PeerThread();
					user.activePeer = temp;
					user.start();
					login.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(new JFrame(), "Username or Password are not valid.", "Dialog",
							JOptionPane.INFORMATION_MESSAGE);
				}

				textField.setText("");
				passwordField.setText("");
			}
		});

		JButton btnsingup = new JButton("Sign up");
		btnsingup.setBounds(15, 134, 115, 30);
		login.getContentPane().add(btnsingup);

		btnsingup.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				login.setVisible(false);
				SignUpScreen();
				SignUp.setVisible(true);

			}
		});
	}
	
}

class PeerThread extends Thread{
	public Peer activePeer = null;
	public Peer ReceipterPeer = null;
	private JFrame MainScreen;

	public void run() {

		Mainscreen();
		MainScreen.setVisible(true);
	}
	
	private void Mainscreen() {
		MainScreen = new JFrame();
		MainScreen.setTitle("Main Screen");
		MainScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		MainScreen.setBounds(100, 100, 677, 376);
		MainScreen.getContentPane().setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 667, 337);
		MainScreen.getContentPane().add(tabbedPane);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Payments", null, panel, null);
		panel.setLayout(null);

		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(26, 13, 38, 16);
		panel.add(lblName);

		JLabel lblUniqueNumber = new JLabel("Unique Number:");
		lblUniqueNumber.setBounds(26, 42, 103, 26);
		panel.add(lblUniqueNumber);

		JLabel lblWallet = new JLabel("Wallet:");
		lblWallet.setBounds(26, 81, 103, 26);
		panel.add(lblWallet);

		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblNewLabel_1.setBounds(146, 13, 124, 16);
		panel.add(lblNewLabel_1);
		lblNewLabel_1.setText(activePeer.Name);

		JLabel label = new JLabel("");
		label.setFont(new Font("Tahoma", Font.BOLD, 14));
		label.setBounds(146, 47, 140, 16);
		panel.add(label);
		label.setText(String.valueOf(activePeer.UniqueNumber));

		JLabel label_2 = new JLabel("");
		label_2.setFont(new Font("Tahoma", Font.BOLD, 14));
		label_2.setBounds(146, 86, 140, 16);
		panel.add(label_2);
		label_2.setText(
				String.valueOf(activePeer.myWallet.getBalance() + " " + activePeer.myWallet.myCurrency.getValue()));

		JButton btnNewButton_3 = new JButton("\u21BB");
		btnNewButton_3.setFont(new Font("Times New  Roman", Font.BOLD, 12));
		btnNewButton_3.setBounds(238, 81, 45, 25);
		panel.add(btnNewButton_3);
		
		JLabel lblAmountToSend = new JLabel("Amount to send");
		lblAmountToSend.setBounds(26, 146, 103, 26);
		panel.add(lblAmountToSend);
		lblAmountToSend.setToolTipText("Amount to Request");

		JTextField textField_1 = new JTextField();
		textField_1.setBounds(127, 148, 116, 22);
		panel.add(textField_1);
		textField_1.setColumns(10);

		JLabel label_1 = new JLabel("Public Key:");
		label_1.setBounds(352, 101, 103, 26);
		panel.add(label_1);

		JTextArea textField_2 = new JTextArea();
		textField_2.setBounds(352, 124, 236, 115);
		textField_2.setFont(new Font("Tahoma", Font.BOLD, 13));
		JScrollPane scrolltxt=new JScrollPane(textField_2);
	    scrolltxt.setBounds(352, 124, 236, 115);		
	    panel.add(scrolltxt);
		
		
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.setBounds(127, 185, 140, 26);
		panel.add(comboBox);

		for (Peer temp : activePeer.restPeers) {
			comboBox.addItem(temp.Name);
		}
		comboBox.setSelectedIndex(-1);
		
		comboBox.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    	textField_2.setText(Blockchain.lookPublicKey(comboBox.getSelectedItem().toString()));
		    }
		});
		// comboBox.setModel(null);

//		textField_2.setText(comboBox.getSelectedItem().toString());

		JLabel lblFriends = new JLabel("Contacts:");
		lblFriends.setBounds(26, 185, 103, 26);
		panel.add(lblFriends);
		JButton btnLogOut = new JButton("Log out");
		btnLogOut.setBounds(358, 252, 97, 25);
		panel.add(btnLogOut);

		JButton btnSend = new JButton("Send");
		btnSend.setBounds(478, 252, 97, 25);
		panel.add(btnSend);

		JButton btnNewButton_1 = new JButton("Check Validation");
		btnNewButton_1.setBounds(352, 32, 130, 25);
		panel.add(btnNewButton_1);

		JLabel lblNewLabel_4 = new JLabel();
		lblNewLabel_4.setBounds(500, 25, 200, 33);
		panel.add(lblNewLabel_4);

		JButton btnNewButton_2 = new JButton("Update");
		btnNewButton_2.setBounds(352, 72, 97, 25);
		panel.add(btnNewButton_2);
		btnNewButton_2.setEnabled(false);

		JLabel lblNewLabel_5 = new JLabel();
		lblNewLabel_5.setBounds(463, 65, 200, 33);
		panel.add(lblNewLabel_5);
		
		btnNewButton_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				Blockchain.BroadcastBlockchain(activePeer);
				lblNewLabel_5.setText("Done.");
			}
		});
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Blocks", null, panel_1, null);
		panel_1.setLayout(null);

		JTextArea TextArea_2 = new JTextArea();
		TextArea_2.setBounds(12, 44, 624, 236);
		TextArea_2.setFont(new Font("Tahoma", Font.PLAIN, 16));
        JScrollPane scrolltxt_2=new JScrollPane(TextArea_2);
        scrolltxt_2.setBounds(12,44,630,236);		
        panel_1.add(scrolltxt_2);
		
		TextArea_2.setEditable(false);
		TextArea_2.setText(Blockchain.getAllBlocks());

		JLabel lblNewLabel_3 = new JLabel("Height");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblNewLabel_3.setBounds(25, 13, 71, 18);
		panel_1.add(lblNewLabel_3);

		JLabel lblAge = new JLabel("Age");
		lblAge.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblAge.setBounds(169, 13, 71, 18);
		panel_1.add(lblAge);

		JLabel lblMiner = new JLabel("Miner");
		lblMiner.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblMiner.setBounds(279, 13, 71, 18);
		panel_1.add(lblMiner);

		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Transactions", null, panel_2, null);
		panel_2.setLayout(null);

		JLabel lblTransactionHash = new JLabel("Transaction Hash");
		lblTransactionHash.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblTransactionHash.setBounds(33, 13, 153, 18);
		panel_2.add(lblTransactionHash);

		JLabel lblAge_1 = new JLabel("Amount");
		lblAge_1.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblAge_1.setBounds(217, 13, 71, 18);
		panel_2.add(lblAge_1);

		JLabel lblAmount = new JLabel("Currency(First)");
		lblAmount.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblAmount.setBounds(317, 13, 124, 18);
		panel_2.add(lblAmount);

		JLabel lblAmountfinal = new JLabel("Currency(Final)");
		lblAmountfinal.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblAmountfinal.setBounds(469, 13, 124, 18);
		panel_2.add(lblAmountfinal);

		JTextArea TextArea_4 = new JTextArea();
		TextArea_4.setBounds(12, 44, 624, 236);
		TextArea_4.setFont(new Font("Times New Roman", Font.PLAIN, 13));
		TextArea_4.setTabSize(3);
        JScrollPane scrolltxt_1=new JScrollPane(TextArea_4);
        scrolltxt_1.setBounds(12,44,630,236);		
        panel_2.add(scrolltxt_1);
		
		TextArea_4.setEditable(false);
//		TextArea_2.setText(Blockchain.getAllBlocks());
		
//		TextArea_4.setEditable(false);
		TextArea_4.setText(Blockchain.getAllTransactions());
		MainScreen.setLocationRelativeTo(null);		
		
		btnNewButton_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				label_2.setText(
						String.valueOf(activePeer.myWallet.getBalance() + " " + activePeer.myWallet.myCurrency.getValue()));
				TextArea_4.setText(Blockchain.getAllTransactions());
				TextArea_2.setText(Blockchain.getAllBlocks());
			}
		});
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if (Blockchain.checkVali(activePeer))
				lblNewLabel_4.setText("No Update");
				else {lblNewLabel_4.setText("There were new Blocks");		btnNewButton_2.setEnabled(true);
				}

			}
		});

		btnSend.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {

				if (textField_1.getText().isEmpty()) {
					JOptionPane.showMessageDialog(new JFrame(), "Please insert an amount and select an user", "Dialog",
							JOptionPane.INFORMATION_MESSAGE);

				} else {
					String temp = (String) comboBox.getSelectedItem();
					ReceipterPeer = Blockchain.lookForReceipter(temp);

					JFrame frame2 = new JFrame();
					frame2.setBounds(450, 300, 300, 200);
					frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame2.getContentPane().setLayout(null);

					float amount = Float.parseFloat(textField_1.getText());

					JLabel lblabel = new JLabel("Are you sure to send " + textField_1.getText()
							+ activePeer.myWallet.myCurrency.getValue() + " to " + ReceipterPeer.Name);
					lblabel.setBounds(30, 20, 210, 32);
					frame2.getContentPane().add(lblabel);

					JButton btnRemove = new JButton("Confirm");
					btnRemove.setBounds(30, 110, 100, 32);
					frame2.getContentPane().add(btnRemove);

					JButton btnCancel = new JButton("Cancel");
					btnCancel.setBounds(145, 110, 100, 32);
					frame2.getContentPane().add(btnCancel);
					frame2.setVisible(true);
					btnCancel.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent arg0) {
							// frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
							MainScreen.setVisible(true);
						}
					});
					btnRemove.addMouseListener(new MouseAdapter() {
						public void mouseClicked(MouseEvent arg0) {

							if (Blockchain.sendMoney(activePeer, ReceipterPeer, amount)) {
								label_2.setText(String.valueOf(activePeer.myWallet.getBalance() + " "
										+ activePeer.myWallet.myCurrency.getValue()));

							} else {
								JOptionPane.showMessageDialog(new JFrame(), "You have not enough funds", "Dialog",
										JOptionPane.ERROR_MESSAGE);

							}
							TextArea_4.setText(Blockchain.getAllTransactions());
							TextArea_2.setText(Blockchain.getAllBlocks());
							frame2.setVisible(false);
							MainScreen.setVisible(true);
						}
					});
				}
			}
		});

		btnLogOut.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				MainScreen.setVisible(false);
				
			}
		});

	}
	
}
