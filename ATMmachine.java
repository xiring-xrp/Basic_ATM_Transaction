import java.util.*;
import java.sql.*;
public class ATMmachine{
	static Scanner sc = new Scanner(System.in);
	static int actualCardNo;
	static int actualPinNo;
	static double balance;
	static double existingBalance;
	static float actualAmount;
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
						System.out.print("Enter your card number :");
						int cardNo = sc.nextInt();
						
						String card = "SELECT acc_no FROM account";
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
			System.out.print("Enter your PIN :");
			int pinNo = sc.nextInt();
			
			String pin = "SELECT pin_no FROM account";
			ResultSet rs = stat.executeQuery(pin);
			/*int actualPinNo = rs.getInt("pin_no");*/
			/*int actualPinNo;*/
			while(rs.next()){
				actualPinNo = rs.getInt("pin_no");
			}
			
			if(pinNo == actualPinNo){
				display(stat);
			}else{
				System.out.println("Invalid PIN number!!!");
			}
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
				pinChange(stat);
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
		String amount = "SELECT balance FROM account";
		/*DBConnection db = new DBConnection();
		Connection conn = db.getConnection();
		Statement stat = conn.createStatement();*/
		ResultSet rs = stat.executeQuery(amount);
		
		while(rs.next()){
			balance = rs.getDouble("balance");
		}
		System.out.println("balance");
		System.out.println("Your current bank balance is :"+ balance);
		display(stat);
	}
	
	static void bankStatement(Statement stat){
		try{
			System.out.println("Bank Statement!!!");
			String query = "SELECT * FROM transaction";
			ResultSet rs = stat.executeQuery(query);
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
	
	static void pinChange(Statement stat){
		System.out.print("Enter your old PIN :");
		int oldPin = sc.nextInt();
		if(oldPin == actualPinNo){
			System.out.print("Enter your new PIN :");
			int newPin = sc.nextInt();
			System.out.print("Confirm PIN :");
			int confirmPin = sc.nextInt();
			if(newPin == confirmPin){
				try{
					/*DBConnection db = new DBConnection();
					Connection conn = db.getConnection();
					Statement stat = conn.createStatement();*/
					String query = "UPDATE account SET pin_no = '"+confirmPin+"'";
					stat.executeUpdate(query);
					}catch(SQLException e){
						e.printStackTrace();
					}
			}else{
				System.out.println("Confirm PIN should be equivalent to new PIN!!!");
				display(stat);
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
			String query = "SELECT balance FROM account";
			ResultSet rs = stat.executeQuery(query);
			while(rs.next()){
				existingBalance = rs.getDouble("balance");
			}
			
			double newBalance = amount + existingBalance;			
			String query1 = "INSERT INTO transaction (acc_no, credit_amount, balance) VALUES ('"+actualCardNo+"', '"+amount+"', '"+newBalance+"')";
			stat.executeUpdate(query1);

			String query2 = "UPDATE account SET balance = '"+newBalance+"'";
			stat.executeUpdate(query2);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	static void withdraw(Statement stat){
		try{
			System.out.println();
			System.out.print("Enter withdrawal amount :");
			float amount = sc.nextFloat();
		
			String query = "SELECT balance FROM transaction";
			ResultSet rs = stat.executeQuery(query);
			while(rs.next()){
				actualAmount = rs.getFloat("balance");
			}
			float newBalanceAfterWithdrawal = actualAmount - amount;
		
			String query1 = "INSERT INTO transaction (acc_no, debit_amount, balance) VALUES ('"+actualCardNo+"', '"+amount+"', '"+newBalanceAfterWithdrawal+"')";
			stat.executeUpdate(query1);
		
			String query2 = "UPDATE account SET balance = '"+newBalanceAfterWithdrawal+"'";
			stat.executeUpdate(query2);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	static void exit(){
		System.exit(0);
	}
}