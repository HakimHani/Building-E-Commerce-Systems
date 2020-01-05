package service;

import java.io.File;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Old_Gateway extends Thread {
	public static PrintStream log = System.out;
	private Socket client;
	private Socket gateway_client;

	public Old_Gateway(Socket client) {
		this.client = client;
	}

	public void run() {
		log.printf("Connected to %s:%d\n", client.getInetAddress(), client.getPort());
		try {
			Scanner in = new Scanner(client.getInputStream());
			PrintStream out = new PrintStream(client.getOutputStream(), true);
			String request = in.nextLine();
			String response = "";
			String payload = "";

			StringTokenizer parse = new StringTokenizer(request);
			String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
			String path = parse.nextToken();
			String protocol = parse.nextToken();

			for (String tmp = in.nextLine(); tmp.length() != 0; tmp = in.nextLine()) {
				;
			}

			if (!method.equals("GET")) {
				log.println("Invalid request");
			} else {
				String service = path.substring(path.indexOf("/") + 1, path.indexOf("?"));
				String query = path.substring(path.indexOf("?") + 1);
				if (service.contentEquals("Geo")) {
					int port = 0;
					String host = "";
					File file = new File("/home/hakim/4413Go/hr4413/ctrl/Geo.txt");
					boolean exists = file.exists();
					if (exists) {
						String data = new String(
								Files.readAllBytes(Paths.get("/home/hakim/4413Go/hr4413/ctrl/Geo.txt")));
						String[] string_array = data.split("/|:");
						host = string_array[1];
						port = Integer.parseInt(string_array[2]);
						String[] tokens = query.trim().split("=|&");
						String long1 = tokens[1];
						String lat1 = tokens[3];
						String long2 = tokens[5];
						String lat2 = tokens[7];
						String req = long1 + " " + lat1 + " " + long2 + " " + lat2;
						Socket gateway_client = new Socket(host, port);
						new PrintStream(gateway_client.getOutputStream(), true).println(req);
						payload = new Scanner(gateway_client.getInputStream()).nextLine();
					}
				} else if (service.contentEquals("Auth")) {
					int port = 0;
					String host = "";
					File file = new File("/home/hakim/4413Go/hr4413/ctrl/Auth.txt");
					boolean exists = file.exists();
					if (exists) {
						String data = new String(
								Files.readAllBytes(Paths.get("/home/hakim/4413Go/hr4413/ctrl/Auth.txt")));
						String[] string_array = data.split("/|:");
						host = string_array[1];
						port = Integer.parseInt(string_array[2]);
						String[] tokens = query.trim().split("=|&");
						String username = tokens[1];
						String password = tokens[3];
						String req = username + " " + password;
						Socket gateway_client = new Socket(host, port);
						new PrintStream(gateway_client.getOutputStream(), true).println(req);
						payload = new Scanner(gateway_client.getInputStream()).nextLine();
					}
				} else if (service.contentEquals("Qoute")) {
					int port = 0;
					String host = "";
					File file = new File("/home/hakim/4413Go/hr4413/ctrl/Qoute.txt");
					boolean exists = file.exists();
					if (exists) {
						String data = new String(
								Files.readAllBytes(Paths.get("/home/hakim/4413Go/hr4413/ctrl/Qoute.txt")));
						String[] string_array = data.split("/|:");
						host = string_array[1];
						port = Integer.parseInt(string_array[2]);
						String[] tokens = query.trim().split("=|&");
						String product_id = tokens[1];
						// String format = tokens[3];
						String req = product_id;// + " " + format;
						Socket gateway_client = new Socket(host, port);
						new PrintStream(gateway_client.getOutputStream(), true).println(req);
						payload = new Scanner(gateway_client.getInputStream()).nextLine();
					}

				} else if (service.contentEquals("Loc")) {
					int port = 0;
					String host = "";
					File file = new File("/home/hakim/4413Go/hr4413/ctrl/Loc.txt");
					boolean exists = file.exists();
					if (exists) {
						String data = new String(
								Files.readAllBytes(Paths.get("/home/hakim/4413Go/hr4413/ctrl/Loc.txt")));
						String[] string_array = data.split("/|:");
						host = string_array[1];
						port = Integer.parseInt(string_array[2]);
						String[] tokens = query.trim().split("=|&");
						String lon = tokens[1];
						String lat = tokens[3];
						String cockie = tokens[5];
						String req = lon + " " + lat + " " + cockie;
						Socket gateway_client = new Socket(host, port);
						new PrintStream(gateway_client.getOutputStream(), true).println(req);
						payload = new Scanner(gateway_client.getInputStream()).nextLine();
					}
				} else {
					payload = "" + "Service is not available";
				}
			}
			response = protocol + " " + "200 OK" + "\n" + "Content-Type: text/plain\n" + "\n" + payload;
			out.println(response);
			out.flush();
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
		InetAddress host = InetAddress.getLoopbackAddress();// .getLocalHost(); //
		ServerSocket server = new ServerSocket(8080, 0, host);
		log.printf("Server listening on %s:%d\n", server.getInetAddress(), server.getLocalPort());
		File file = new File("/home/hakim/4413Go/hr4413/ctrl/Gateway.txt");
		PrintStream outFile = new PrintStream(file);
		outFile.printf("%s:%d", server.getInetAddress(), server.getLocalPort());
		while (file.exists()) {
			Socket client = server.accept();
			new Old_Gateway(client).start();
		}
		server.close();
		log.append(String.format("Server shutdown (subentry: %s)", server.getInetAddress()));
	}

}