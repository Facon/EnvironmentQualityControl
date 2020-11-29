package es.deusto.redes.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;

import es.deusto.redes.data.dao.Measure;
import es.deusto.redes.data.dao.Sensor;

public class Client {
	private Socket clientSocket = null;
	private DataOutputStream out = null;
	private BufferedReader in = null;
    
    public Client(String ip, int port) {
		try {
			clientSocket = new Socket(ip, port);
			out = new DataOutputStream(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
            System.err.println("Don't know about host: Localhost.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: Localhost.");
            System.exit(1);
        }
    }
    
	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}
	
	public boolean login(String nick, String pass) throws Exception {
		String response;
		int codeOP;
		
		out.writeBytes("USER " + nick + "\r\n");
		response = in.readLine();
		System.out.println(response);
		codeOP = Integer.parseInt(response.split(" ")[0]);
		
		if (codeOP != 201) {
			JOptionPane.showMessageDialog(null, response, "Error!", JOptionPane.ERROR_MESSAGE);
			
			return false;			
		}
		
		out.writeBytes("PASS " + pass + "\r\n");
		response = in.readLine();
		System.out.println(response);
		codeOP = Integer.parseInt(response.split(" ")[0]);
		
		if (codeOP != 202) {
			JOptionPane.showMessageDialog(null, response, "Error!", JOptionPane.ERROR_MESSAGE);
			return false;			
		}
		
		return true;
	}
	
	public List<Sensor> getListSensors() throws Exception {
		String response = "";
		String receive = "";
		int codeOP;
		List<Sensor> sensors = null;
		
		out.writeBytes("LISTSENSOR" + "\r\n");
		
		while (!in.ready()) {
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		while (!receive.startsWith("212")) {
			receive = in.readLine();
			response += receive + "\n";
		}
		
		System.out.print(response);
		codeOP = Integer.parseInt(response.split(" ")[0]);
		
		if (codeOP != 112) {
			JOptionPane.showMessageDialog(null, response, "Error!", JOptionPane.ERROR_MESSAGE);		
		}
		else {
			sensors = new ArrayList<Sensor>(10);
			String[] strings = response.split("\n");
			String[] substrings;
			
			for (int i = 1; i < strings.length - 1; i++) {
				substrings = strings[i].split(";");
				
				String name = substrings[1];
				boolean state = false;
				
				if (substrings[2].equals("ON")) {
					state = true;
				}
				
				sensors.add(new Sensor(name, state, null));
			}
		}
		
		return sensors;
	}
	
	public List<Measure> getSensorMeasures(int id) throws Exception {
		String response = "";
		String receive = "";
		int codeOP;
		List<Measure> measures = null;
		
		out.writeBytes("HISTORICO " + Integer.valueOf(id) + "\r\n");
		
		while (!in.ready()) {
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		while (!receive.startsWith("212")) {
			receive = in.readLine();
			response += receive + "\n";
		}
		
		System.out.print(response);
		codeOP = Integer.parseInt(response.split(" ")[0]);
		
		if (codeOP != 113) {
			JOptionPane.showMessageDialog(null, response, "Error!", JOptionPane.ERROR_MESSAGE);		
		}
		else {
			measures = new ArrayList<Measure>(10);
			String[] strings = response.split("\n");
			String[] substrings;
			
			for (int i = 1; i < strings.length - 1; i++) {
				substrings = strings[i].split(";");
				
				SimpleDateFormat parserSDF = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.US);
				Date date = parserSDF.parse(substrings[0]);
				String coordinates = substrings[1];
				int value = Integer.parseInt(substrings[2]);
				
				measures.add(new Measure(date, coordinates,  value));
			}
		}
		
		return measures;
	}
	
	public byte[] getPhoto() throws Exception {
		String response = "";
		byte[] photo;
		int codeOP;
		photo = null;
		ServerSocket serversock = new ServerSocket(4446);
		
		out.writeBytes("GET_FOTO" + "\r\n");
		response = in.readLine();
		System.out.println(response);
		codeOP = Integer.parseInt(response.split(" ")[0]);
		
		if (codeOP != 207) {
			JOptionPane.showMessageDialog(null, response, "Error!", JOptionPane.ERROR_MESSAGE);	
		}
		else {
			Socket sock2 = serversock.accept();
			DataInputStream in2 = new DataInputStream(sock2.getInputStream());
			DataOutputStream out2 = new DataOutputStream(sock2.getOutputStream());
			
			photo = new byte[Integer.parseInt(response.split(" ")[2])];
			
			in2.readFully(photo);
			
			out2.close();
			in2.close();
			sock2.close();
			serversock.close();
			
			// Create file
			DataOutputStream os = new DataOutputStream(new FileOutputStream("out.jpg"));
			os.write(photo);
			// Close the output stream
			os.close();
			
			in.readLine();
		}
		
		return photo;
	}
	
	public String getLoc() throws Exception {
		String response, loc;
		int codeOP;
		
		loc = null;
		
		out.writeBytes("GET_LOC" + "\r\n");
		response = in.readLine();
		System.out.println(response);
		
		codeOP = Integer.parseInt(response.split(" ")[0]);
		
		if (codeOP != 115) {
			JOptionPane.showMessageDialog(null, response, "Error!", JOptionPane.ERROR_MESSAGE);	
		}
		else {
			loc = response.split(" ")[2];
		}
		
		return loc;
	}
	
	public boolean onGPS() throws Exception {
		String response;
		int codeOP;
		
		out.writeBytes("ONGPS" + "\r\n");
		response = in.readLine();
		System.out.println(response);
		codeOP = Integer.parseInt(response.split(" ")[0]);
		
		if (codeOP != 205) {
			JOptionPane.showMessageDialog(null, response, "Error!", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean offGPS() throws Exception {
		String response;
		int codeOP;
		
		out.writeBytes("OFFGPS" + "\r\n");
		response = in.readLine();
		System.out.println(response);
		codeOP = Integer.parseInt(response.split(" ")[0]);
		
		if (codeOP != 206) {
			JOptionPane.showMessageDialog(null, response, "Error!", JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		else {
			return true;
		}
	}
	
	public Sensor getSensor(int id) throws Exception {
		List<Sensor> ss1 = getListSensors();
		Sensor s1 = ss1.get(id);
		return s1;
	}

	public void setSensorState(int id, boolean b) throws IOException {
		String response;
		int codeOP;
		
		if (b) {
			out.writeBytes("ON " + id + "\r\n");
			response = in.readLine();
			System.out.println(response);
			codeOP = Integer.parseInt(response.split(" ")[0]);

			if (codeOP != 203) {
				JOptionPane.showMessageDialog(null, response, "Error!", JOptionPane.ERROR_MESSAGE);
			}
		}
		else {
			out.writeBytes("OFF " + id + "\r\n");
			response = in.readLine();
			System.out.println(response);
			codeOP = Integer.parseInt(response.split(" ")[0]);

			if (codeOP != 204) {
				JOptionPane.showMessageDialog(null, response, "Error!", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	public Measure getSensorActualMeasure(int id) throws Exception {
		String response;
		int codeOP;
		Measure measure = null;
		
		out.writeBytes("GET_VALACT " + Integer.valueOf(id) + "\r\n");
		response = in.readLine();
		System.out.println(response);
		codeOP = Integer.parseInt(response.split(" ")[0]);
		
		if (codeOP != 114) {
			JOptionPane.showMessageDialog(null, response, "Error!", JOptionPane.ERROR_MESSAGE);		
		}
		else {
			String substring = response.substring(7);
			String[] substrings;
			
			substrings = substring.split(";");
				
			SimpleDateFormat parserSDF = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.US);
			Date date = parserSDF.parse(substrings[0]);
			String coordinates = substrings[1];
			int value = Integer.parseInt(substrings[2].substring(0, 1));
				
			measure = new Measure(date, coordinates,  value);
		}
		
		return measure;
	}
	
	public void close() {
    	try {
			clientSocket.close();
			out.close();
			in.close();
		} catch (IOException e) {
			System.err.println("Couldn't close Socket.");
		}
    }
}
