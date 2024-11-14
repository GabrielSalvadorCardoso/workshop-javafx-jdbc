package migration;

import java.sql.Connection;
import java.sql.SQLException;

import db.ConnectionFactory;

public class CreateDatabase {
	public static void main(String[] args) throws SQLException {
		createDepartmentTable();
		createSellerTable();
		insertDepartmentDataTable();
		insertSellerDataTable();
		System.out.println("Done!");
	}
	
	public static void createDepartmentTable() {
		Connection connection = new ConnectionFactory().getConnection();
		String sql = "CREATE TABLE department (\r\n"
				+ "  Id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,\r\n"
				+ "  Name varchar(60) DEFAULT NULL,\r\n"
				+ "  PRIMARY KEY (Id)\r\n"
				+ ");";
		
		try {
			connection.createStatement().execute(sql);
			connection.close();
			System.out.println("Tabela department criada!");
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
	}
	
	public static void createSellerTable() {
		Connection connection = new ConnectionFactory().getConnection();
		String sql = "CREATE TABLE seller (\r\n"
				+ "  Id INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL,\r\n"
				+ "  Name varchar(60) NOT NULL,\r\n"
				+ "  Email varchar(100) NOT NULL,\r\n"
				+ "  BirthDate datetime NOT NULL,\r\n"
				+ "  BaseSalary double NOT NULL,\r\n"
				+ "  DepartmentId INTEGER NOT NULL,\r\n"
				+ "  PRIMARY KEY (Id),\r\n"
				+ "  FOREIGN KEY (DepartmentId) REFERENCES department (id)\r\n"
				+ ");";
		try {
			connection.createStatement().execute(sql);
			connection.close();
			System.out.println("Tabela seller criada!");
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
	}
	
	public static void insertDepartmentDataTable() {
		Connection connection = new ConnectionFactory().getConnection();
		String sql="INSERT INTO department (Name) VALUES \r\n"
				+ "  ('Computers'),\r\n"
				+ "  ('Electronics'),\r\n"
				+ "  ('Fashion'),\r\n"
				+ "  ('Books');";
		try {
			connection.createStatement().execute(sql);
			connection.close();
			System.out.println("Dados de department inseridos!");
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
	}
	
	public static void insertSellerDataTable() {
		Connection connection = new ConnectionFactory().getConnection();
		String sql="INSERT INTO seller (Name, Email, BirthDate, BaseSalary, DepartmentId) VALUES \r\n"
				+ "  ('Bob Brown','bob@gmail.com','1998-04-21 00:00:00',1000,1),\r\n"
				+ "  ('Maria Green','maria@gmail.com','1979-12-31 00:00:00',3500,2),\r\n"
				+ "  ('Alex Grey','alex@gmail.com','1988-01-15 00:00:00',2200,1),\r\n"
				+ "  ('Martha Red','martha@gmail.com','1993-11-30 00:00:00',3000,4),\r\n"
				+ "  ('Donald Blue','donald@gmail.com','2000-01-09 00:00:00',4000,3),\r\n"
				+ "  ('Alex Pink','bob@gmail.com','1997-03-04 00:00:00',3000,2);";
		try {
			connection.createStatement().execute(sql);
			connection.close();
			System.out.println("Dados de department inseridos!");
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
	}
}
