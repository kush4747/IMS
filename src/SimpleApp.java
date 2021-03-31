import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SimpleApp {
	
	private static Connection connection = null;	
	private static String databaseName = null;
	private static String portNumber = null;
	
	static Scanner input = new Scanner(System.in);
	
	private static void connectToDB(String DBName, String PortNumber) {		
		try {
			Class.forName("org.postgresql.Driver");
//			connection = DriverManager.getConnection(
//					"jdbc:postgresql://localhost:"+PortNumber+"/"+DBName);
			connection = DriverManager.getConnection(
					"jdbc:postgresql://localhost:5433/database_test");			
			
			if (connection!=null) {
				System.out.println("Connected");
			}
			else {
				System.out.println("Connection Failed");
			}			
			
		} catch (Exception e) {
			System.out.print(e);
		}	
	}
	
	
	public static ResultSet executeQuerySQL(String q) {
		ResultSet rs = null;	   
		try {	
		    Statement stm = connection.createStatement();
		    rs = stm.executeQuery(q);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}	
		return rs;
	}
	
	public static int executeUpdateSQL(String q) {
		int rowCount = -2;
		try {	
		    Statement stm = connection.createStatement();
		    rowCount = stm.executeUpdate(q);		    
		}
		catch(Exception ex) {
			// What do you want to do with this exception?
			ex.printStackTrace();
		}	
		return rowCount;
	}
	
	public static void dropDBTables() {
		
		// Drops tables if they exist
		String q = "DROP TABLE IF EXISTS ItemsInOrder;";
		executeUpdateSQL(q);
		
		q = "DROP TABLE IF EXISTS Items;";
		executeUpdateSQL(q);
		
		q = "DROP TABLE IF EXISTS Orders;";
		executeUpdateSQL(q);
		
		q = "DROP TABLE IF EXISTS Customers;";
		executeUpdateSQL(q);
	}
	
	public static void makeDB() {
		
		// Customers Table
		String q = "CREATE TABLE Customers ("
				+ "CustomerID SERIAL PRIMARY KEY,"
				+ "CustomerName varchar(255) NOT NULL"
				+ ");";
		executeUpdateSQL(q);
		
		// Items Table
		q = "CREATE TABLE Items ("
				+ "ItemID SERIAL PRIMARY KEY,"
				+ "ItemName varchar(255) NOT NULL,"
				+ "ItemCost int NOT NULL"
				+ ");";
		executeUpdateSQL(q);
		
		// Orders Table	
		q = "CREATE TABLE Orders ("
				+ "OrderID SERIAL PRIMARY KEY,"
				+ "CustomerID int NOT NULL,"
				+ "FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID)"
				+ ");";
		executeUpdateSQL(q);
		
		// Items in Items in Order Bridge Table	
		q = "CREATE TABLE ItemsInOrder ("
				+ "OrderID int NOT NULL,"
				+ "ItemID int NOT NULL,"
				+ "FOREIGN KEY (OrderID) REFERENCES Orders(OrderID),"
				+ "FOREIGN KEY (ItemID) REFERENCES Items(ItemID)"
				+ ");";
		executeUpdateSQL(q);
	}
	
	public static void run() throws SQLException {
		
		while (true) {
			System.out.println("Ello! What do you want to do??");
			System.out.println("");

			System.out.println(""
					+ "1) Add a customer to the system\n"
					+ "2) View all customers in the system\n"
					+ "3) Update a customer in the system\n"
					+ "4) Delete a customer in the system\n"
					+ "5) Add an item to the system\n"
					+ "6) View all items in the system\n"
					+ "7) Update an item in the system\n"
					+ "8) Delete an item in the system\n"
					+ "9) Create an Order in the system\n"
					+ "10) View all Orders in the system\n"
					+ "11) Delete an Order in the system\n"
					+ "12) Add an item to an Order\n"
					+ "13) Calculate a cost for an Order\n"
					+ "14) Delete an item in an Order");
			
			// The cost of putting it here is time, plus it will take up memory until garbage colected
			// Plus the processing of creating a new scanner
			// You can measure the additional time by monitoring the difference between creating a new object
			// And leaving it the heap..
			// You do by running, making to the function, th
			Scanner input = new Scanner(System.in);
			System.out.println();
			System.out.println("Enter a number");
			String stringOption = input.nextLine();
			
			try {
				int option = Integer.parseInt(stringOption);
				directToMethod(option);
			} catch (NumberFormatException e) {
				System.out.println("That wasn't a number! Try again");
			}
			System.out.println();
		}	
	}
	
	public static void directToMethod(int option) throws SQLException {
			
			switch (option) {
				case 1:
					addCustomer();
					break;
				case 2:
					viewCustomers();
					break;
				case 3:
					updateCustomer();
					break;
				case 4:
					deleteCustomer();
					break;
				case 5:
					addItem();
					break;
				case 6:
					viewItems();
					break;
				case 7:
					updateItem();
					break;
				case 8:
					deleteItem();
					break;
				case 9:
					createOrder();
				case 10:
					// viewOrders();
				case 11:
					// deleteOrder();
				case 12:
					// addItemToOrder();
				case 13:
					// calculateOrderCost();
				case 14:
					// deleteItemInOrder();
				if (option>14||option<1) {
					System.out.println("Please choose an option from the list");
				}
			}	
			System.out.println();
		}
	
	public static void addCustomer() throws SQLException {		
	
	
		System.out.println("What's the name of the customer?");
		String name = input.nextLine();
		
		String q = "INSERT INTO Customers (CustomerName) VALUES ('"+name+"');";
		
		executeUpdateSQL(q);
		
		System.out.println("Cool, all done, you're added.");
	
	}
	
	
	public static void viewCustomers() {
		String q = "SELECT * FROM Customers;";
		ResultSet rs = executeQuerySQL(q);
		
		System.out.println("");
		System.out.println("---- CUSTOMERS ----");
		System.out.println("");
		
		try {
			while (rs.next()) {
				System.out.println(rs.getString("CustomerID")+" - "+rs.getString("CustomerName"));
			}
			System.out.println("End..");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void updateCustomer() {
		String q = null;
		System.out.println("What's the Customer ID of the customer you'd like to update?");
		
		String id = input.nextLine();
		
		q = "SELECT CustomerID FROM Customers WHERE CustomerID = '"+id+"';";
		ResultSet rs = executeQuerySQL(q);
		try {
			if (!rs.next()) {
				System.out.println();
				System.out.println("There isn't a customer in the DB with this name");
				System.out.println("Try again");
			}
			else {
				System.out.println("Okay what would you like to change the name to?");
				String name2 = input.nextLine();
				q = "UPDATE Customers SET CustomerName = '"+name2+"' WHERE CustomerID ='"+id+"';";
				executeUpdateSQL(q);
				System.out.println("Customer Updated!!");
	
	
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void deleteCustomer() {		
		String q = null;
		System.out.println("What's the customer's CustomerID that you'd like to delete?");
		String id = input.nextLine();
		q = "SELECT CustomerID FROM Customers WHERE CustomerID = '"+id+"';";
		ResultSet rs = executeQuerySQL(q);
		
		try {
			if (!rs.next()) {
				System.out.println("There isn't a customer in the DB with this id");
				System.out.println("Try again");
				System.out.println();
			}
			else {
				q = "DELETE FROM Customers WHERE CustomerID = '"+id+"';";
				executeUpdateSQL(q);
				System.out.println("Customer deleted :)");
	
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addItem() throws SQLException {
		System.out.println("What's the item's name then?");
		String name = input.nextLine();
		
		System.out.println("Yeah and what's the item's cost in pennies?");
		String cost = input.nextLine();
		int costInt = Integer.parseInt(cost);
	
		String q = "INSERT INTO Items (ItemName, ItemCost) VALUES ( '"+name+"', "+costInt+");";
		
		executeUpdateSQL(q);
		
		System.out.println("Cool, item is added");
	}
	
	
	public static void viewItems() {
		String q = "SELECT * FROM Items;";
		ResultSet rs = executeQuerySQL(q);	
		
		System.out.println();
		System.out.println("----- ITEMS -----");
		System.out.println();
		try {
			while (rs.next()) {
				System.out.println(rs.getString("ItemID")+" - "+rs.getString("ItemCost")+"p - "+rs.getString("ItemName"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
	}
	
	
	public static void updateItem() {
		String q = null;
		System.out.println("What's the item ID of the item you'd like update?");
		String ItemID = input.nextLine();
		
		int ItemIDInt = Integer.parseInt(ItemID);
		int pence = 0;
	
		q = "SELECT ItemID FROM Items WHERE ItemID = "+ItemIDInt+";";
		ResultSet rs = executeQuerySQL(q);
		try {
			if (!rs.next()) {
				System.out.println("There isn't a Item in the DB with this ID");
			}
			else {
				System.out.println("Okay what would you like to change, the name or cost?");
				String userInput = input.nextLine();
				
				if (userInput.equals("cost") || userInput.equals("Cost")) {
					System.out.println("Okay what would you like to change the cost to in pence?");
					userInput = input.nextLine();
					pence = Integer.parseInt(userInput);
					q = "UPDATE Items SET ItemCost = '"+pence+"' WHERE ItemID ="+ItemIDInt+";";
					executeUpdateSQL(q);
					System.out.println("Updated..");
	
				}
				else if (userInput.equals("name") || userInput.equals("Name")) {
					System.out.println("Okay what would you like to change the name to?");
					userInput = input.nextLine();
					q = "UPDATE Items SET ItemName = '"+userInput+"' WHERE ItemID ="+ItemIDInt+";";
					executeUpdateSQL(q);
					System.out.println("Updated.. See ya ;)");
	
				}
				else {
					System.out.println("You didn't pick 'cost' or 'name'.. ! Sorry");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void deleteItem() {
		String q = null;
		System.out.println("What's the items ID you'd like to delete?");
		String id = input.nextLine();
		
		q = "SELECT * FROM Items WHERE ItemID = '"+id+"';";
		ResultSet rs = executeQuerySQL(q);
		
		try {
			if (!rs.next()) {
				System.out.println("There isn't a Item in the DB with this ID");
				System.out.println("Try again");
			}
			else {
				q = "DELETE FROM Items WHERE ItemID = '"+id+"';";
				executeUpdateSQL(q);
				System.out.println("Item deleted :)");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createOrder() {
	
		System.out.println("What's your Customer ID?");
		String id = input.nextLine();
		
		String q = "SELECT CustomerID FROM Customers WHERE CustomerID = '"+id+"';";
		ResultSet rs = executeQuerySQL(q);
	
		
		try {
			if (!rs.next()) {
				System.out.println();
				System.out.println("There isn't a customer in the DB with this name");
				System.out.println("Try again");
	
			}
			else {				
				q = "INSERT INTO Orders (CustomerID) VALUES ("+id+") RETURNING OrderID;";
				rs = executeQuerySQL(q);
				rs.next();
				String orderId = rs.getString(1);
				
				
				while (true) {
					
					System.out.println("Okay what items would you like to order?");
					System.out.println("Please provide the Item ID (e.g. 1):");
					String itemID = input.nextLine();
					
					q = "INSERT INTO ItemsInOrder (OrderID, ItemID) VALUES ("+orderId+","+itemID+");";
					executeUpdateSQL(q);
					
					String loop = "";
					
					while (true) {
						System.out.println("Do you want to add another Item to the order?");
						System.out.println("Input: y or n");
						loop = input.nextLine();
						
						if (loop.equals("y")||loop.equals("Y")||loop.equals("n")||loop.equals("N")) {
							break;
						}
						else {
							System.out.println("That isn't either y or n");
						}
						
					}
					if (loop.equals("n")||loop.equals("N")) {
						break;
					}
				}
				System.out.println("You're order has saved. You're order ID is: " + orderId);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void calculateOrder() {
		
	}
	

	public static void main(String[] args) throws SQLException {
	
		// DBName = database_test
		// Port number = 5434
		String databaseName = args[0];
		String portNumber = args[1];

		connectToDB(databaseName, portNumber);
		dropDBTables();
		makeDB();
		run();
	}



}
