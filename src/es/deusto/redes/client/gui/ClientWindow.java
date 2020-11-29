package es.deusto.redes.client.gui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import es.deusto.redes.client.Client;
import es.deusto.redes.server.VehicleServer;
import es.deusto.redes.server.gui.AdminWindow;

public class ClientWindow {
	private static final ClientWindow instance = new ClientWindow();

	private Client client;
	private JFrame clj = new JFrame("Login");
	private JFrame cmj = new JFrame("Medidor Medioambiental");
	private JFrame cpj = new JFrame("Foto");
	private ClientLoginJPanel cljpanel = new ClientLoginJPanel();
	private ClientMenuJPanel cmjpanel = new ClientMenuJPanel();
	private ClientPhotoJPanel cpjpanel = new ClientPhotoJPanel();
	
	private ClientWindow() {
		clj.setVisible(true);
		cmj.setVisible(false);
		cpj.setVisible(false);
		cmj.setSize(500, 500);
		clj.setResizable(false);
		clj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cmj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cpj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		clj.add(cljpanel);
		cmj.add(cmjpanel);
		cpj.add(cpjpanel);
		clj.pack();
		cpj.pack();
	}

	public static ClientWindow getInstance() {
		return instance;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public JFrame getClj() {
		return clj;
	}

	public void setClj(JFrame clj) {
		this.clj = clj;
	}

	public JFrame getCmj() {
		return cmj;
	}

	public void setCmj(JFrame cmj) {
		this.cmj = cmj;
	}

	public ClientLoginJPanel getCljpanel() {
		return cljpanel;
	}

	public void setCljpanel(ClientLoginJPanel cljpanel) {
		this.cljpanel = cljpanel;
	}

	public ClientMenuJPanel getCmjpanel() {
		return cmjpanel;
	}

	public void setCmjpanel(ClientMenuJPanel cmjpanel) {
		this.cmjpanel = cmjpanel;
	}

	public JFrame getCpj() {
		return cpj;
	}

	public void setCpj(JFrame cpj) {
		this.cpj = cpj;
	}

	public ClientPhotoJPanel getCpjpanel() {
		return cpjpanel;
	}

	public void setCpjpanel(ClientPhotoJPanel cpjpanel) {
		this.cpjpanel = cpjpanel;
	}

	public static void main(String[] args) {
		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				//Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				
				ClientWindow.getInstance();
			}
		});
	}
}
