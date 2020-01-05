package service;

import java.io.File;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Geo extends Thread {
	public static PrintStream log = System.out;
	private Socket client;
	public static String home_Dir = System.getProperty("user.home");

	public Geo(Socket client) {
		this.client = client;
	}

	public String getDistance(String lon1Str, String lat1Str, String lon2Str, String lat2Str) {
		float lon1 = Float.parseFloat(lon1Str);
		float lon2 = Float.parseFloat(lon2Str);
		float lat1 = Float.parseFloat(lat1Str);
		float lat2 = Float.parseFloat(lat2Str);
		final int R = 6371; // Radius of the earth

		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c; // convert to meters

		return String.format("%.2f", distance).toString() + " KMS";
	}

	public void run() {
		log.printf("Connected to %s:%d\n", client.getInetAddress(), client.getPort());
		try {
			Scanner in = new Scanner(client.getInputStream());
			PrintStream out = new PrintStream(client.getOutputStream(), true);
			String request = in.nextLine();
			String str[] = request.split("\\s+");
			// double t1 = Double.parseDouble(str[0]);
			// double n1 = Double.parseDouble(str[1]);
			// double t2 = Double.parseDouble(str[2]);
			// double n2 = Double.parseDouble(str[3]);
			// double lat1 = Math.toRadians(t1);
			// double lat2 = Math.toRadians(t2);
			// double long1 = Math.toRadians(n1);
			// double long2 = Math.toRadians(n2);
			String response;
			if (str[0].matches("[+-]?([0-9]*[.])?[0-9]+") && str[1].matches("[+-]?([0-9]*[.])?[0-9]+")
					&& str[2].matches("[+-]?([0-9]*[.])?[0-9]+") && str[3].matches("[+-]?([0-9]*[.])?[0-9]+")) {
				String dist = getDistance(str[0], str[1], str[2], str[3]);
				// double Y = Math.cos(lat1) * Math.cos(lat2);
				// double X = Math.pow(Math.sin(lat2-lat1)/2, 2) + Y *
				// Math.pow(Math.sin((long2-long1)/2), 2);
				// double dist = 12742 * Math.atan2(Math.sqrt(X), Math.sqrt(1-X));
				response = "" + dist;
			} else {
				response = "Don't understand: " + request;
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
		File file = new File(home_Dir + "/4413/ctrl/Geo.txt");
		PrintStream outFile = new PrintStream(file);
		outFile.printf("%s:%d", server.getInetAddress(), server.getLocalPort());
		while (file.exists()) {
			Socket client = server.accept();
			new Geo(client).start();
		}
		server.close();
		log.append(String.format("Server shutdown (subentry: %s)", server.getInetAddress()));
	}

}
