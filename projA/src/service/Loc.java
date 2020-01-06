package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonParser;

public class Loc extends Thread {
	public static PrintStream log = System.out;
	private Socket client;
	public static String home_Dir = System.getProperty("user.home");
	public static final String url = "https://maps.googleapis.com/maps/api/geocode/json?";
	private static final String APIKey = "";

	public Loc(Socket client) {
		this.client = client;
	}

	private String getRequest(String url) throws Exception {
		final URL obj = new URL(url);
		final HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("GET");
		if (con.getResponseCode() != 200) {
			return null;
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}

	public void run() {
		log.printf("Connected to %s:%d\n", client.getInetAddress(), client.getPort());
		try {
			Scanner in = new Scanner(client.getInputStream());
			PrintStream out = new PrintStream(client.getOutputStream(), true);
			String address_requested = in.nextLine();
			StringBuffer response = new StringBuffer();

			String query = "";
			String address = address_requested.replace(' ', '+');
			String payload = null;
			query = "CA&key=" + APIKey;
			try {
				payload = getRequest(url + "address=" + address + "," + query);
			} catch (Exception e) {
				log.printf("Error when trying to get data with the following query " + query);
			}
			/*
			 * Object obj = JSONValue.parse(queryResult); log.printf("obj=" + obj);
			 * 
			 * if (obj instanceof JSONArray) { JSONArray array = (JSONArray) obj; if
			 * (array.size() > 0) { JSONObject jsonObject = (JSONObject) array.get(0);
			 * 
			 * String lon = (String) jsonObject.get("lon"); String lat = (String)
			 * jsonObject.get("lat"); log.printf("lon=" + lon); log.printf("lat=" + lat);
			 * response.append("lon:" + Double.parseDouble(lon)); response.append("\n lat:"
			 * + Double.parseDouble(lat));
			 * 
			 * } }
			 */
			JsonParser parser = new JsonParser();
			payload = parser.parse(payload).getAsJsonObject().toString();
			out.println(payload);
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
		File file = new File(home_Dir + "/4413/ctrl/Loc.txt");
		PrintStream outFile = new PrintStream(file);
		outFile.printf("%s:%d", server.getInetAddress(), server.getLocalPort());
		while (file.exists()) {
			Socket client = server.accept();
			new Loc(client).start();
		}
		server.close();
		log.append(String.format("Server shutdown (subentry: %s)", server.getInetAddress()));
	}

}
