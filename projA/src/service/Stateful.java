package service;

import java.io.File;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Stateful extends Thread {
	public static PrintStream log = System.out;
	public static Random rand = new Random();
	public static String home_Dir = System.getProperty("user.home");
	public static HashMap<Integer, ArrayList<String>> hashmap = new HashMap<Integer, ArrayList<String>>();

	private Socket client;
	private Socket geo_client;

	public Stateful(Socket client) {
		this.client = client;
	}

	public void run() {
		log.printf("Connected to %s:%d\n", client.getInetAddress(), client.getPort());
		try {
			Scanner in = new Scanner(client.getInputStream());
			PrintStream out = new PrintStream(client.getOutputStream(), true);
			String response = "";
			int port = 0;
			String host = "";
			File file = new File(home_Dir + "/4413/ctrl/Geo.txt");
			boolean exists = file.exists();
			if (exists) {
				String data = new String(Files.readAllBytes(Paths.get(home_Dir + "/4413/ctrl/Geo.txt")));
				String[] string_array = data.split("/|:");
				host = string_array[1];
				port = Integer.parseInt(string_array[2]);
				String request = in.nextLine();
				String str[] = request.split("\\s+");
				if (Float.parseFloat(str[2]) == 0) {
					ArrayList<String> arraylist = new ArrayList<String>();
					arraylist.add(str[0]);
					arraylist.add(str[1]);
					int rand_value = rand.nextInt(100000);
					hashmap.put(rand_value, arraylist);
					out.println(rand_value);
				} else {
					for (Integer key : hashmap.keySet()) {
						if (key.equals(Integer.parseInt(str[2]))) {
							String long1 = hashmap.get(key).get(0);
							String lat1 = hashmap.get(key).get(1);
							String long2 = str[0];
							String lat2 = str[1];
							String req = long1 + " " + lat1 + " " + long2 + " " + lat2;
							Socket stateful_client = new Socket(host, port);
							new PrintStream(stateful_client.getOutputStream(), true).println(req);
							response = new Scanner(stateful_client.getInputStream()).nextLine();
							try {
								stateful_client.close();
							} catch (Exception e) {
								log.print(e);
							}
						}
					}
				}
			} else {
				response = "" + "Service is not available";
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
		File file = new File(home_Dir + "/4413/ctrl/Stateful.txt");
		PrintStream outFile = new PrintStream(file);
		outFile.printf("%s:%d", server.getInetAddress(), server.getLocalPort());
		while (file.exists()) {
			Socket client = server.accept();
			new Stateful(client).start();
		}
		server.close();
		log.append(String.format("Server shutdown (subentry: %s)", server.getInetAddress()));
	}

}