package service;

import java.io.ByteArrayOutputStream;
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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.google.gson.Gson;

public class Qoute extends Thread {
	public static PrintStream log = System.out;
	private Socket client;
	private static Connection connection;
	public static String home_Dir = System.getProperty("user.home");

	public Qoute(Socket client) {
		this.client = client;
	}

	public static Connection connect() {
		try {
			String url = "jdbc:derby://localhost:64413/EECS";
			// create a connection to the database
			connection = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			System.out.println("Error");
		}
		return connection;
	}

	public void run() {
		log.printf("Connected to %s:%d\n", client.getInetAddress(), client.getPort());
		try {
			Scanner in = new Scanner(client.getInputStream());
			PrintStream out = new PrintStream(client.getOutputStream(), true);
			out.println("Please Enter the Id and format:");
			String request = in.nextLine();
			String[] token = request.split("\\s+");
			String format = token[1];
			String id = token[0];
			String response = "";
			if (token.length == 2) {
				Class.forName("org.apache.derby.jdbc.ClientDriver");
				Connection connection = connect();
				Statement statement = connection.createStatement();

				// SQL query to obtain the NAME and PRICE given the id of the product
				String query = "SELECT * FROM HR.PRODUCT WHERE ID='" + id + "'";
				ResultSet rs = statement.executeQuery(query);
				ProductBean bean = new ProductBean();
				if (rs.next()) {
					bean.setName(rs.getString("NAME"));
					bean.setId(rs.getString("ID"));
					bean.setPrice(rs.getDouble("COST"));
				} else {
					bean.setName(" ");
					bean.setId("Not Found");
					bean.setPrice(0.0);
				}
				if (format.equals("xml")) {
					JAXBContext context = JAXBContext.newInstance(ProductBean.class);
					Marshaller m = context.createMarshaller();
					m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					m.marshal(bean, baos);
					response = baos.toString();
				} else {
					Gson gson = new Gson();
					response = gson.toJson(bean);
				}
				rs.close();
				statement.close();
				connection.close();

			} else {
				response = "Don't understand: " + request;
			}
			// send response back to client
			out.println(response);
			out.println("connection was closed by forign server");
		} catch (Exception e) {
			log.println("Error: " + e);
		}
		try {
			client.close();
		} catch (Exception e) {
			log.print(e);
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
		File file = new File(home_Dir + "/4413/ctrl/Qoute.txt");
		PrintStream outFile = new PrintStream(file);
		outFile.printf("%s:%d", server.getInetAddress(), server.getLocalPort());
		while (file.exists()) {
			Socket client = server.accept();
			new Qoute(client).start();
		}
		server.close();
		log.append(String.format("Server shutdown (subentry: %s)", server.getInetAddress()));
	}

}