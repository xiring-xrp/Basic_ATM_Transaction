import java.util.*;
import java.sql.*;
public class ATMmachine{
	static Scanner sc = new Scanner(System.in);
	/*static int accountNumber;*/
	static int id;
	static int actualCardNo;
	static int displayAcc;
	static String displayTransactionDate;
	static float displayDebitAmount;
	static float displayCreditAmount;
	static double displayBalance;
	
	public static void main(String[] args){
		try{
			DBConnection db = new DBConnection();
			Connection conn = db.getConnection();
			Statement stat = conn.createStatement();
			System.out.println();
			System.out.println("Welcome to NIC Asia Bank Limited!!!");
			System.out.println("1. Create an Account?");
			System.out.println("2. Already have an Account?");
			System.out.println();
			System.out.print("Enter your choice :");
			
			int option = Integer.parseInt(sc.nextLine());
			switch(option){
				case 1:
					System.out.println("Registration form!!!");
					createAccount(stat);
					break;
				case 2:
					try{
						System.out.print("Enter your ID :");
						id = sc.nextInt();
						
						System.out.print("Enter your card number :");
						int cardNo = sc.nextInt();
						
						String card = "SELECT acc_no FROM account WHERE id = '"+id+"'";
						ResultSet rs = stat.executeQuery(card);
						
						while(rs.next()){
							actualCardNo = rs.getInt("acc_no");
						}
						
						if(cardNo == actualCardNo){
							validate(stat);
						}else{
							System.out.println("Invalid Card Number!!");
						}
						break;
					}catch(SQLException e){
						e.printStackTrace();
					}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	static void createAccount(Statement stat){
		try{
			System.out.println();
			System.out.print("Enter your name :");
			String name = sc.nextLine();
			System.out.print("Enter your account number :");
			int accNo = sc.nextInt();
			System.out.print("Create your PIN no :");
			int pinNo = sc.nextInt();
			System.out.print("Enter your balance :");
			double balance = sc.nextDouble();
		
			String query = "INSERT INTO account (name, acc_no, pin_no, balance) VALUES ('"+name+"', '"+accNo+"', '"+pinNo+"', '"+balance+"')";
			stat.executeUpdate(query);
		}catch(SQLException e){
			e.printStackTrace();
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
	}
	
	static void validate(Statement stat){
		try{			
			String pin = "SELECT pin_no FROM account Where id = '"+id+"'";
			ResultSet rs = stat.executeQuery(pin);
			/*int actualPinNo = rs.getInt("pin_no");*/
			int existedPin = 0;
			while(rs.next()){
				existedPin = rs.getInt("pin_no");
			}
			
			int attempt = 3;
			for(int i = 0; i <= attempt; attempt--){
				System.out.print("Enter your PIN :");
				int pinNo = sc.nextInt();
				
				if(pinNo == existedPin){
					display(stat);
					break;
				}else{
					System.out.println("Invalid PIN number!!!");
					System.out.println("Attmept left :" + attempt);
				}
			}
			System.out.print("Your card has been blocked!!, Please contact your nearest bank");
			/*System.out.print(actualPinNo);*/
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	static void display(Statement stat){
		System.out.println();
		System.out.println("Menu");
		System.out.println("1. Balance enquiry.");
		System.out.println("2. Bank statement");
		System.out.println("3. PIN change");
		System.out.println("4. Deposit");
		System.out.println("5. Withdraw");
		System.out.println("6. Exit");
		
		System.out.print("Enter your transaction :");
		int choice = sc.nextInt();
		switch(choice){
			case 1:
				try{
					balanceEnquiry(stat);
				}catch(SQLException e){
					e.printStackTrace();
				}
				break;
			case 2:
				bankStatement(stat);
				break;
			case 3:
				try{
					pinChange(stat);
				}catch(SQLException e){
					e.printStackTrace();
				}
				break;
			case 4:
				deposit(stat);
				break;
			case 5:
				withdraw(stat);
				break;
			case 6:
				exit();
				break;
			default:
				System.out.println("Invalid Choice!");
				display(stat);
		}
	}
	
	static void balanceEnquiry(Statement stat) throws SQLException{
		String amount = "SELECT balance FROM account WHERE id = '"+id+"'";
		ResultSet rs = stat.executeQuery(amount);
		
		double balance = 0;
		while(rs.next()){
			balance = rs.getDouble("balance");
		}
		System.out.println("Your current bank balance is :"+ balance);
		display(stat);
	}
	
	static void bankStatement(Statement stat){
		try{
			System.out.println("Bank Statement!!!");
			String query1 = "SELECT acc_no FROM account WHERE id = '"+id+"'";
			ResultSet rs = stat.executeQuery(query1);
			
			int accountNumber = 0;
			while(rs.next()){
				accountNumber = rs.getInt("acc_no");
			}
			System.out.print(accountNumber);
			
			String query = "SELECT * FROM transaction WHERE acc_no = '"+accountNumber+"'";
			rs = stat.executeQuery(query);
			
			System.out.println("Account Number		Transaction Date		Debit Amount		Credit Amount		Balance");
			while(rs.next()){
				displayAcc = rs.getInt("acc_no");
				displayTransactionDate = rs.getString("transaction_date");
				displayDebitAmount = rs.getFloat("debit_amount");
				displayCreditAmount = rs.getFloat("credit_amount");
				displayBalance = rs.getDouble("balance");
				System.out.println(" "+displayAcc+"              "+displayTransactionDate+"             "+displayDebitAmount+"                     "+displayCreditAmount+"                 "+displayBalance+"");
			}
			display(stat);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	static void pinChange(Statement stat) throws SQLException{
		String query = "SELECT pin_no FROM account WHERE id = '"+id+"'";
		ResultSet rs = stat.executeQuery(query);
		
		int existedPin = 0;
		while(rs.next()){
			existedPin = rs.getInt("pin_no");
		}
		
		System.out.print("Enter your old PIN :");
		int oldPin = sc.nextInt();
		if(oldPin == existedPin){
			System.out.print("Enter your new PIN :");
			int newPin = sc.nextInt();
			System.out.print("Confirm PIN :");
			int confirmPin = sc.nextInt();
			if(newPin == confirmPin){
				try{
					/*DBConnection db = new DBConnection();
					Connection conn = db.getConnection();
					Statement stat = conn.createStatement();*/
					String query1 = "UPDATE account SET pin_no = '"+confirmPin+"' WHERE id = '"+id+"'";
					stat.executeUpdate(query1);
					}catch(SQLException e){
						e.printStackTrace();
					}
			}else{
				System.out.println("Confirm PIN should be equivalent to new PIN!!!");
			}
		}else{
			System.out.println("Invalid old PIN!!!");
			display(stat);
		}		
	}
	
	static void deposit(Statement stat){
		try{
			System.out.print("Deposit into bank account :");
			float amount = sc.nextFloat();
			
			String query3 = "SELECT acc_no FROM  account WHERE id = '"+id+"'";
			ResultSet rs = stat.executeQuery(query3);
			
			int accountNumber = 0;
			while(rs.next()){
				accountNumber = rs.getInt("acc_no");
			}
			
			String query = "SELECT balance FROM account WHERE acc_no = '"+accountNumber+"'";
			rs = stat.executeQuery(query);
			
			double existingBalance = 0;
			while(rs.next()){
				existingBalance = rs.getDouble("balance");
			}

			double newBalance = existingBalance + amount;			
			String query1 = "INSERT INTO transaction (acc_no, credit_amount, balance) VALUES ('"+actualCardNo+"', '"+amount+"', '"+newBalance+"')";
			stat.executeUpdate(query1);

			String query2 = "UPDATE account SET balance = '"+newBalance+"' WHERE id = '"+id+"'";
			stat.executeUpdate(query2);
			
			System.exit(0);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	static void withdraw(Statement stat){
		try{
			System.out.println();
			System.out.print("Enter withdrawal amount :");
			float amount = sc.nextFloat();
			
			String query3 = "SELECT acc_no FROM  account WHERE id = '"+id+"'";
			ResultSet rs = stat.executeQuery(query3);	
			
			int accountNumber = 0;
			while(rs.next()){
				accountNumber = rs.getInt("acc_no");
			}
		
			String query = "SELECT balance FROM transaction WHERE acc_no = '"+accountNumber+"'";
			rs = stat.executeQuery(query);
			
			double actualAmount = 0;
			while(rs.next()){
				actualAmount = rs.getDouble("balance");
			}
			double newBalanceAfterWithdrawal = actualAmount - amount;
		
			String query1 = "INSERT INTO transaction (acc_no, debit_amount, balance) VALUES ('"+actualCardNo+"', '"+amount+"', '"+newBalanceAfterWithdrawal+"')";
			stat.executeUpdate(query1);
		
			String query2 = "UPDATE account SET balance = '"+newBalanceAfterWithdrawal+"' WHERE id = '"+id+"'";
			stat.executeUpdate(query2);
			
			System.exit(0);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	static void exit(){
		System.exit(0);
	}
}