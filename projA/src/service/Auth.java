package service;

import java.io.File;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Auth extends Thread {
	public static PrintStream log = System.out;
	private Socket client;
	private static Connection conDB;
	public static String home_Dir = System.getProperty("user.home");

	public Auth(Socket client) {
		this.client = client;
	}

	public static Connection connect() {
		try {
			String url = "jdbc:sqlite:" + home_Dir + "/4413/pkg/sqlite/Models_R_US.db";
			// create a connection to the database
			conDB = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Error");
		}
		return conDB;
	}

	public void run() {
		Statement statement = null;
		ResultSet rs = null;
		log.printf("Connected to %s:%d\n", client.getInetAddress(), client.getPort());
		try {
			Scanner in = new Scanner(client.getInputStream());
			PrintStream out = new PrintStream(client.getOutputStream(), true);
			String request = in.nextLine();
			String str[] = request.split("\\s+");
			String username = str[0];
			String password = str[1];
			String response = "";
			String hash = "";
			String salt = "";
			int count = 0;

			String sql = "SELECT name, count, hash, salt FROM CLIENT";
			try {
				conDB = connect();
				statement = conDB.createStatement();
				rs = statement.executeQuery(sql);
				while (rs.next()) {
					String name = rs.getString("name");
					if (name.equals(username)) {
						hash = rs.getString("hash");
						salt = rs.getString("salt");
						count = rs.getInt("count");
						String answer = g.Util.hash(password, salt, count);
						if (answer.contentEquals(hash))
							response = "OK";
						else
							response = "FAILURE";
						break;
					}
				}
				if (salt.contentEquals("") && hash.contentEquals(""))
					response = "FAILURE";

			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}

			out.println(response);

		} catch (Exception e) {
			log.println("Error: " + e);
		}
		try {
			client.close();
		} catch (Exception e) {
			log.print(e);
		}

		// close the db connection
		finally {
			try {
				if (conDB != null)
					conDB.close();
			} catch (SQLException e) {
				System.err.println(e);
			}
		}

		log.printf("Dis-Connected to %s:%d\n", client.getInetAddress(), client.getPort());
	}

	public static void main(String[] args) throws Exception {
		int port = 0;
		InetAddress host;
		if (args.length > 2 && args[2].contentEquals("l")) {
			host = InetAddress.getLocalHost();
		} else {
			host = InetAddress.getLoopbackAddress();
		}
		ServerSocket server = new ServerSocket(port, 0, host);
		log.printf("Server listening on %s:%d\n", server.getInetAddress(), server.getLocalPort());
		File file = new File(home_Dir + "/4413/ctrl/Auth.txt");
		PrintStream outFile = new PrintStream(file);
		outFile.printf("%s:%d", server.getInetAddress(), server.getLocalPort());
		while (file.exists()) {
			Socket client = server.accept();
			new Auth(client).start();
		}
		server.close();
		log.append(String.format("Server shutdown (subentry: %s)", server.getInetAddress()));
	}

}