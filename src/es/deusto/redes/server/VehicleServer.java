package es.deusto.redes.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.swing.JOptionPane;

import net.sf.ehcache.store.chm.ConcurrentHashMap;
import es.deusto.redes.VehicleState;
import es.deusto.redes.data.dao.Measure;
import es.deusto.redes.data.dao.Sensor;
import es.deusto.redes.data.dao.User;
import es.deusto.redes.data.dao.Vehicle;
import es.deusto.redes.protocol.MAProtocol;

public class VehicleServer implements Runnable {
	private int port = 4444;
	private int nconnections = 10;
	private ExecutorService es = Executors.newFixedThreadPool(nconnections);
	private ServerSocket serverSocket = null;
	private Thread runningThread = null;
	private boolean isStopped = false;
	private PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
	private int id = 1;
	private List<Handler> handlers = new LinkedList<Handler>();
	private ConcurrentMap<String, User> users = new ConcurrentHashMap<String, User>(nconnections);

	public VehicleServer(int id) {
		openServerSocket();
		this.id = id;
	}

	public int getNconnections() {
		return nconnections;
	}

	public void setNconnections(int nconnections) {
		this.nconnections = nconnections;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public ExecutorService getEs() {
		return es;
	}

	public void setEs(ExecutorService es) {
		this.es = es;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ConcurrentMap<String, User> getUsers() {
		return users;
	}

	public void setUsers(ConcurrentMap<String, User> users) {
		this.users = users;
	}
	
	public void removeHandler(String user) {
		Iterator<Handler> iter = handlers.iterator();
		Handler handler;
		
		while (iter.hasNext()) {
			handler = iter.next();
			
			if (user.equals(handler.getUser().getNick())) {
				handler.close();
				handlers.remove(handler);
				break;
			}
		}		
	}

	public Thread getRunningThread() {
		return runningThread;
	}

	public void setRunningThread(Thread runningThread) {
		this.runningThread = runningThread;
	}

	private synchronized boolean isStopped() {
		return this.isStopped;
	}

	public synchronized void stop(){
		this.isStopped = true;
		try {
			this.serverSocket.close();
			es.shutdownNow();
		} catch (IOException e) {
			throw new RuntimeException("Error closing server", e);
		}
	}

	private void openServerSocket() {
		try {
			this.serverSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open port 4444", e);
		}
	}
	
	public Set<User> getUsersFromDB() {
		// Perform some query operations
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		Set<User> users = new HashSet<User>(20);
		
		try {
			tx.begin();
			Extent<User> e = pm.getExtent(User.class);
            Iterator<User> iter = e.iterator();

			while (iter.hasNext())
			{
				users.add(iter.next());
			}

			tx.commit();
		}
		catch (NullPointerException e)
		{
			users = null;
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
		
		return users;
	}
	
	public User getUserFromDB(String nick) {
		// Perform some query operations
		User user;
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		try {
			tx.begin();
			Extent<User> e = pm.getExtent(User.class);
			Query q = pm.newQuery(e, "nick == " + "\"" + nick + "\"");
			q.setUnique(true);
			user = (User) q.execute();

			tx.commit();
		}
		catch (NullPointerException e)
		{
			user = null;
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			pm.close();
		}
		
		return user;
	}
	
	public boolean addUserToDB(User user) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		try
		{
		    tx.begin();
		    
		    pm.makePersistent(user);
		    
		    tx.commit();
		}
		finally
		{
		    if (tx.isActive())
		    {
		        tx.rollback();
		        
		        return false;
		    }
		    pm.close();
		}
		
		return true;
	}
	
	public boolean modUserToDB(User user1, User user2) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		try
		{
		    tx.begin();
		    
		    Query q = pm.newQuery(User.class, "nick == " + "\"" + user1.getNick() + "\"");
			q.setUnique(true);
			q.deletePersistentAll();
			
			pm.makePersistent(user2);
		    
		    tx.commit();
		}
		finally
		{
		    if (tx.isActive())
		    {
		        tx.rollback();
		        
		        return false;
		    }
		    pm.close();
		}
		
		return true;
	}

	public boolean delUserFromDB(User user) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		
		try
		{
		    tx.begin();
		    
		    Query q = pm.newQuery(User.class, "nick == " + "\"" + user.getNick() + "\"");
			q.setUnique(true);
			q.deletePersistentAll();
		    
		    tx.commit();
		}
		finally
		{
		    if (tx.isActive())
		    {
		        tx.rollback();
		        
		        return false;
		    }
		    pm.close();
		}
		
		return true;
	}
	
	public void run() {
		synchronized(this){
			this.setRunningThread(Thread.currentThread());
		}

		while(!isStopped()){
			Socket clientSocket = null;
			try {
				clientSocket = this.serverSocket.accept();
			} catch (IOException e) {
				if(isStopped()) {
					System.err.println("Server Stopped.") ;
					return;
				}
				throw new RuntimeException("Error accepting client connection", e);
			}
			
			Handler handler = new Handler(clientSocket, id);
			
			handlers.add(handler);
			
			this.es.execute(handler);
		}

		this.es.shutdownNow();
		System.out.println("Server Stopped.") ;
	}

	class Handler implements Runnable {
		private VehicleState state = VehicleState.WAITING;
		private final Socket socket;
		private DataOutputStream out = null;
		private BufferedReader in = null;
		private PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
		private int id;
		private User user;
		private Vehicle vehicle;

		Handler(Socket socket, int id) {
			this.socket = socket;
			try {
				out = new DataOutputStream(socket.getOutputStream());
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				this.id = id;
				this.vehicle = getVehicleFromDB();
			} catch (IOException e) {
				System.err.println("Couldn't get I/O for the connection to: Localhost.");
			}
		}
		
		public VehicleState getState() {
			return state;
		}

		public void setState(VehicleState ready) {
			this.state = ready;
		}
		
		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
		
		User getUser() {
			return user;
		}

		private Set<Sensor> getSensorsFromDB() {
			// Perform some query operations
			PersistenceManager pm = pmf.getPersistenceManager();
			Transaction tx = pm.currentTransaction();
			Set<Sensor> sensors = new HashSet<Sensor>(20);
			
			try {
				tx.begin();
				Extent<Sensor> e = pm.getExtent(Sensor.class);
	            Iterator<Sensor> iter = e.iterator();

				while (iter.hasNext())
				{
					sensors.add(iter.next());
				}

				tx.commit();
			}
			catch (NullPointerException e)
			{
				sensors = null;
			}
			finally {
				if (tx.isActive()) {
					tx.rollback();
				}
				pm.close();
			}
			
			return sensors;
		}
		
		public void setSensor(int id) {
			PersistenceManager pm = pmf.getPersistenceManager();
			Transaction tx = pm.currentTransaction();
			
			try
			{
			    tx.begin();
			    
			    pm.makePersistent(vehicle.getSensor(id));
			    
			    tx.commit();
			}
			finally
			{
			    if (tx.isActive())
			    {
			        tx.rollback();
			    }
			    pm.close();
			}
		}

		public User getUserFromDB(String nick) {
			// Perform some query operations
			User user;
			PersistenceManager pm = pmf.getPersistenceManager();
			Transaction tx = pm.currentTransaction();
			
			try {
				tx.begin();
				Extent<User> e = pm.getExtent(User.class);
				Query q = pm.newQuery(e, "nick == " + "\"" + nick + "\"");
				q.setUnique(true);
				user = (User) q.execute();

				tx.commit();
			}
			catch (NullPointerException e)
			{
				user = null;
			}
			finally {
				if (tx.isActive()) {
					tx.rollback();
				}
				pm.close();
			}
			
			return user;
		}
		
		private Vehicle getVehicleFromDB() {
			// Perform some query operations
			PersistenceManager pm = pmf.getPersistenceManager();
			Transaction tx = pm.currentTransaction();
			Vehicle vehicle = null;
			
			try {
				tx.begin();
				
				Extent<Vehicle> e = pm.getExtent(Vehicle.class);
	            Iterator<Vehicle> iter = e.iterator();
	            
				for (int i = 1; i < id && iter.hasNext(); i++) {
					iter.next();
				}
				
				vehicle = iter.next();
				
				tx.commit();
			}
			catch (NullPointerException e)
			{
				vehicle = null;
			}
			finally {
				if (tx.isActive()) {
					tx.rollback();
				}
				pm.close();
			}
			
			return vehicle;
		}
		
		public void setVehicle() {
			PersistenceManager pm = pmf.getPersistenceManager();
			Transaction tx = pm.currentTransaction();
			
			try
			{
			    tx.begin();
			    
			    pm.makePersistent(vehicle);
			    
			    tx.commit();
			}
			finally
			{
			    if (tx.isActive())
			    {
			        tx.rollback();
			    }
			    pm.close();
			}
		}
		
		public long getPhotoSize() {			
			return vehicle.getPhoto().length;
		}
		
		public String getPhoto() {
			String photo = null;
			
			try
			{				
				if (vehicle.getPhoto() != null) {
					photo = new String(vehicle.getPhoto());
					
					//TODO
					// Create file
					DataOutputStream os = new DataOutputStream(new FileOutputStream("out1.jpg"));
					os.writeBytes(photo);
					// Close the output stream
					os.close();
				}
				
				return photo;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			return photo;
		}

		public void close() {
			try {
				if (users.containsKey(user.getNick())) {
					users.remove(user.getNick());
				}
				
				socket.close();
				out.close();
				in.close();
			} catch (IOException e) {
				System.err.println("Couldn't close Socket.");
			}
		}
		
		public boolean processInput(String theInput) throws Exception {
			boolean quit = false;
			String[] command = theInput.split(" ");
			String codeOP = command[0];

			ArrayList<String> arg0 = new ArrayList<String>(6);

			switch (state) {
			case WAITING:
				if (codeOP.equals("USER")) {
					if (command.length > 1) {
						user = getUserFromDB(command[1]);
						
						arg0.add(command[1]);
						
						state = VehicleState.AUTH;
						out.writeBytes(MAProtocol.getCode(201, arg0) + "\r\n");
					}
					else {
						out.writeBytes(MAProtocol.getCode(401, null) + "\r\n");
					}
				}
				else {
					state = VehicleState.WAITING;
					out.writeBytes(MAProtocol.getCode(-1, null) + "\r\n");
				}
				
				break;
				
			case AUTH:
				if (codeOP.equals("PASS")) {
					if (command.length > 1) {
						if (user != null && user.getPassword().equals(command[1])) {
							users.put(user.getNick(), user);
							state = VehicleState.READY;
							out.writeBytes(MAProtocol.getCode(202, null) + "\r\n");
						}
						else {
							user = null;
							state = VehicleState.WAITING;
							out.writeBytes(MAProtocol.getCode(402, null) + "\r\n");
						}
					}
					else {
						state = VehicleState.WAITING;
						out.writeBytes(MAProtocol.getCode(403, null) + "\r\n");
					}
				}
				else {
					state = VehicleState.WAITING;
					out.writeBytes(MAProtocol.getCode(-1, null) + "\r\n");
				}
				
				break;

			case READY:
				if (codeOP.equals("LISTSENSOR")) {
					Set<Sensor> sensors = vehicle.getSensors();
					Iterator<Sensor> iter = sensors.iterator();
					
					out.writeBytes(MAProtocol.getCode(112, null) + "\n");
					
					int i = 0;
					
					while (iter.hasNext()) {
						Sensor s1 = iter.next();
						String theOutput = String.valueOf(i++) + ";";
						theOutput += s1.getDescription() + ";";
						
						if (s1.isState()) {
							theOutput += "ON";
						}
						else {
							theOutput += "OFF";
						}
						
						out.writeBytes(theOutput + "\n");
					}
					 
					out.writeBytes(MAProtocol.getCode(212, null) + "\r\n");
				}
				else if (codeOP.equals("HISTORICO")) {
					if (command.length > 1) {
						try {
							Set<Measure> measures = vehicle.getSensor(Integer.parseInt(command[1])).getMeasures();
							Iterator<Measure> iter = measures.iterator();
							
							out.writeBytes(MAProtocol.getCode(113, null) + "\n");
							
							while (iter.hasNext()) {
								Measure m1 = iter.next();
								String theOutput = m1.getDate() + ";";
								theOutput += m1.getCoordinate() + ";";
								theOutput += m1.getValue();
								
								out.writeBytes(theOutput + "\n");
							}

							out.writeBytes(MAProtocol.getCode(212, null) + "\r\n");
						} catch (NullPointerException e) {
							out.writeBytes(MAProtocol.getCode(414, null) + "\r\n");
						}
					}
					else {
						out.writeBytes(MAProtocol.getCode(415, null) + "\r\n");
					}
				}
				else if (codeOP.equals("ON")) {
					try {
						Sensor sensor = vehicle.getSensor(Integer.parseInt(command[1]));

						if (!sensor.isState()) {
							sensor.setState(true);
							setSensor(Integer.parseInt(command[1]));
							out.writeBytes(MAProtocol.getCode(203, null) + "\r\n");
						} else {
							out.writeBytes(MAProtocol.getCode(418, null) + "\r\n");
						}

					} catch (NullPointerException e) {
						out.writeBytes(MAProtocol.getCode(417, null) + "\r\n");
					}
				}
				else if (codeOP.equals("OFF")) {
					try {
						Sensor sensor = vehicle.getSensor(Integer.parseInt(command[1]));

						if (sensor.isState()) {
							sensor.setState(false);
							setSensor(Integer.parseInt(command[1]));
							out.writeBytes(MAProtocol.getCode(204, null) + "\r\n");
						} else {
							out.writeBytes(MAProtocol.getCode(419, null) + "\r\n");
						}

					} catch (NullPointerException e) {
						out.writeBytes(MAProtocol.getCode(417, null) + "\r\n");
					}
				}
				else if (codeOP.equals("ONGPS")) {
					if (!vehicle.isState_gps()) {
						vehicle.setState_gps(true);
						setVehicle();
						out.writeBytes(MAProtocol.getCode(205, null) + "\r\n");
					}
					else {
						out.writeBytes(MAProtocol.getCode(419, null) + "\r\n");
					}
				}
				else if (codeOP.equals("OFFGPS")) {
					if (vehicle.isState_gps()) {
						vehicle.setState_gps(false);
						setVehicle();
						out.writeBytes(MAProtocol.getCode(206, null) + "\r\n");
					}
					else {
						out.writeBytes(MAProtocol.getCode(420, null) + "\r\n");
					}
				}
				else if (codeOP.equals("GET_VALACT")) {
					if (command.length > 1) {
						try {
							Sensor sensor = vehicle.getSensor(Integer.parseInt(command[1]));
							
							if (sensor.isState()) {
								Date date = Calendar.getInstance(Locale.FRANCE).getTime();
								String coordinates = vehicle.getLocation();
								int value = (new Random()).nextInt(30);
								
								arg0.add(date.toString()); // Fecha ahora
								arg0.add(coordinates); // Coordenadas del coche, retocar para hacer el cliente de localización
								arg0.add(String.valueOf(value)); // Valor random
								
								Measure measure = new Measure(date, coordinates, value);
								sensor.addMeasure(measure);
								
								setVehicle();

								out.writeBytes(MAProtocol.getCode(114, arg0) + "\r\n");
							}
							else {
								out.writeBytes(MAProtocol.getCode(416, null) + "\r\n");
							}
						} catch (NullPointerException e) {
							out.writeBytes(MAProtocol.getCode(414, null) + "\r\n");
						}						
					}
					else {
						out.writeBytes(MAProtocol.getCode(415, null) + "\r\n");
					}
				}
				else if (codeOP.equals("GET_FOTO")) {
					if (vehicle.getPhoto() != null) {
						byte[] photo = vehicle.getPhoto();
						arg0.add(String.valueOf(getPhotoSize()));
						state = VehicleState.PHOTO;
						out.writeBytes(MAProtocol.getCode(207, arg0) + "\r\n");
						
						Socket sock2 = new Socket(socket.getInetAddress(), 4446);
						DataInputStream in2 = new DataInputStream(sock2.getInputStream());
						DataOutputStream out2 = new DataOutputStream(sock2.getOutputStream());
						
						out2.write(photo, 0, photo.length);
						out2.flush();
						
						out2.close();
						in2.close();
						sock2.close();
					}
					else {
						out.writeBytes(MAProtocol.getCode(421, null) + "\r\n");
					}
				}
				else if (codeOP.equals("SALIR")) {
					state = VehicleState.BYE;
					out.writeBytes(MAProtocol.getCode(208, null) + "\r\n");
					quit = true;
				}
				else {
					state = VehicleState.READY;
					out.writeBytes(MAProtocol.getCode(-1, null) + "\r\n");
				}
				
				break;

			case PHOTO:
				if (codeOP.equals("GET_LOC")) {
					if (vehicle.isState_gps()) {
						arg0.add(vehicle.getLocation());
					}
					else {
						String response = null;
						Socket sock2 = new Socket("localhost", 4445);
						DataOutputStream out = new DataOutputStream(sock2.getOutputStream());
						BufferedReader in = new BufferedReader(new InputStreamReader(sock2.getInputStream()));
						
						out.writeBytes("USER " + user.getNick() + "\r\n");
						response = in.readLine();
						
						int codeOP2 = Integer.parseInt(response.split(" ")[0]);
						
						if (codeOP2 != 201) {
							JOptionPane.showMessageDialog(null, response, "Error!", JOptionPane.ERROR_MESSAGE);			
						}
						else {
							out.writeBytes("PASS " + user.getPassword() + "\r\n");
							response = in.readLine();

							System.out.println(response);
							codeOP2 = Integer.parseInt(response.split(" ")[0]);

							if (codeOP2 != 202) {
								JOptionPane.showMessageDialog(null, response, "Error!", JOptionPane.ERROR_MESSAGE);			
							}
							else {
								out.writeBytes("GET_COOR " + vehicle.getCell().getCell_id() + "\r\n");
								response = in.readLine();
								
								System.out.println(response);
								codeOP2 = Integer.parseInt(response.split(" ")[0]);
								
								if (codeOP2 != 114) {
									JOptionPane.showMessageDialog(null, response, "Error!", JOptionPane.ERROR_MESSAGE);			
								}
								else {
									arg0.add(response.split(" ")[2]);
								}
							}
						}
						
						in.close();
						out.close();
						sock2.close();
					}
					
					state = VehicleState.READY;
					out.writeBytes(MAProtocol.getCode(115, arg0) + "\r\n");
				}
				else {
					state = VehicleState.READY;
					out.writeBytes(MAProtocol.getCode(-1, null) + "\r\n");
				}
				
				break;
				
			default:
				out.writeBytes(MAProtocol.getCode(-1, null) + "\r\n");
				break;
			}

			arg0.clear();
			
			out.flush();

			return quit;
		}

		public void run() {
			// read and service request on socket
			// Usar MAProtocol fijo
			String command;
			boolean quit = false;

			while (!quit) {
				try {
					command = in.readLine();

					quit = processInput(command);
				} catch (SocketException e) {
					if (!e.getMessage().equals("Connection reset")) {
						e.printStackTrace();
					}
					
					quit = true;
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Couldn't read Socket.");	
					
					quit = true;
				}
			}

			close();
			
			return;
		}
	}
	
	public static void main(String[] args) {
		VehicleServer s = new VehicleServer(1);
		
		s.run();
		
		System.out.println("Parando servidor.");
		s.stop();
	}
}
