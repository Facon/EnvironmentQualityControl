package es.deusto.redes.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import net.sf.ehcache.store.chm.ConcurrentHashMap;
import es.deusto.redes.LocationState;
import es.deusto.redes.data.dao.Cell;
import es.deusto.redes.data.dao.User;
import es.deusto.redes.protocol.LocationProtocol;

public class LocationServer implements Runnable {
	private LocationState state = LocationState.WAITING;
	private int port = 4445;
	private int nconnections = 10;
	private ExecutorService es = Executors.newFixedThreadPool(nconnections);
	private ServerSocket serverSocket = null;
	private Thread runningThread = null;
	private boolean isStopped = false;
	private PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
	private List<Handler> handlers = new LinkedList<Handler>();
	private ConcurrentMap<String, User> users = new ConcurrentHashMap<String, User>(nconnections);

	public LocationServer() {
		openServerSocket();
	}

	public LocationState getState() {
		return state;
	}

	public void setState(LocationState ready) {
		this.state = ready;
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

	public ConcurrentMap<String, User> getUsers() {
		return users;
	}

	public void setUsers(ConcurrentMap<String, User> users) {
		this.users = users;
	}

	public User getUser(String nick) {
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

	public Thread getRunningThread() {
		return runningThread;
	}

	public void setRunningThread(Thread runningThread) {
		this.runningThread = runningThread;
	}

	private synchronized boolean isStopped() {
		return this.isStopped;
	}

	public synchronized void stop() {
		this.isStopped = true;
		try {
			this.serverSocket.close();
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

			Handler handler = new Handler(clientSocket);

			handlers.add(handler);

			this.es.execute(handler);
		}

		this.es.shutdownNow();
		System.out.println("Server Stopped.") ;
	}

	class Handler implements Runnable {
		private LocationState state = LocationState.WAITING;
		private final Socket socket;
		private PrintWriter out = null;
		private BufferedReader in = null;
		private PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
		private User user;

		Handler(Socket socket) {
			this.socket = socket;
			try {
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {
				System.err.println("Couldn't get I/O for the connection to: Localhost.");
			}
		}

		public void close() {
			try {
				socket.close();
				out.close();
				in.close();
			} catch (IOException e) {
				System.err.println("Couldn't close Socket.");
			}
		}
		
		public String processInput(String theInput) throws Exception {
			String theOutput = LocationProtocol.getCode(-1, null);
			String[] command = theInput.split(" ");
			String codeOP = command[0];

			ArrayList<String> arg0 = new ArrayList<String>(6);

			switch (state) {
			case WAITING:
				if (codeOP.equals("USER")) {
					if (command.length > 1) {
						user = getUser(command[1]);

						arg0.add(command[1]);

						theOutput = LocationProtocol.getCode(201, arg0);
						state = LocationState.AUTH;
					}
					else {
						theOutput = LocationProtocol.getCode(401, null);
					}
				}
				else {
					theOutput = LocationProtocol.getCode(-1, null);
				}

				break;

			case AUTH:
				if (codeOP.equals("PASS")) {
					if (command.length > 1) {
						if (user != null && user.getPassword().equals(command[1])) {
							theOutput = LocationProtocol.getCode(202, null);
							state = LocationState.READY;
						}
						else {
							user = null;
							theOutput = LocationProtocol.getCode(402, null);
							state = LocationState.WAITING;
						}
					}
					else {
						theOutput = LocationProtocol.getCode(403, null);
						state = LocationState.WAITING;
					}
				}
				else {
					theOutput = LocationProtocol.getCode(-1, null);
					state = LocationState.WAITING;
				}

				break;

			case READY:
				if (codeOP.equals("GET_COOR")) {
					if (command.length > 1) {

						String coordinate = getCell(Integer.parseInt(command[1])).getCoordinate();

						if (!theOutput.isEmpty()) {
							arg0.add(coordinate);
							theOutput = LocationProtocol.getCode(114, arg0);
						}
						else {
							theOutput = LocationProtocol.getCode(417, null);
						}
					}
					else {
						theOutput = LocationProtocol.getCode(418, null);
					}
				}
				else {
					theOutput = LocationProtocol.getCode(-1, null);
					state = LocationState.READY;
				}

				break;

			default:
				break;
			}

			arg0.clear();

			return theOutput;
		}

		private Cell getCell(int cell_id) {
			// Perform some query operations
			PersistenceManager pm = pmf.getPersistenceManager();
			Transaction tx = pm.currentTransaction();
			Cell cell = null;

			try {
				tx.begin();

				Extent<Cell> e = pm.getExtent(Cell.class);
				Query q = pm.newQuery(e, "cell_id == " + String.valueOf(cell_id));
				q.setUnique(true);
				cell = (Cell) q.execute();

				tx.commit();
			}
			catch (NullPointerException e)
			{
				cell = null;
			}
			finally {
				if (tx.isActive()) {
					tx.rollback();
				}
				pm.close();
			}

			return cell;
		}

		public void run() {
			// read and service request on socket
			// Usar LocationProtocol fijo
			int code = 0;
			String command, output;
			String[] command2;

			while (code != 208) {
				try {
					command = in.readLine();

					output = processInput(command);

					out.println(output);

					command2 = output.split(" ");
					code = Integer.parseInt(command2[0]);
				} catch (SocketException e) {
					if (!e.getMessage().equals("Connection reset")) {
						e.printStackTrace();
					}
					code = 208;
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Couldn't read Socket.");	
					code = 208;
				}
			}

			close();

			return;
		}
	}

	public static void main(String[] args) {
		LocationServer s = new LocationServer();

		new Thread(s).start();

		try {
			Thread.sleep(50 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Parando servidor.");
		s.stop();

	}
}
