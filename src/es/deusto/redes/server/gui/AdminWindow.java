package es.deusto.redes.server.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import es.deusto.redes.server.VehicleServer;

public class AdminWindow extends JFrame {
	private static final long serialVersionUID = 8444695254826001779L;
	
	private AdminJPanel panel = new AdminJPanel();
	private static VehicleServer vs = null;
	
	public AdminWindow(final VehicleServer vs) {
		AdminWindow.vs = vs;
		new Thread(vs).start();
		
		setVisible(true);
		setSize(500, 500);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Administrador del Servidor");
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				vs.stop();				
			}
			
			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		add(panel, BorderLayout.CENTER);
		//pack();
	}
	
	public static VehicleServer getVehicleServer() {
		return vs;
	}

	public static void main(final String[] args)
	{
		//Schedule a job for the event dispatch thread:
		//creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				VehicleServer vs;
				
				//Turn off metal's use of bold fonts
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				
				if (args.length > 1) {
					vs = new VehicleServer(Integer.parseInt(args[1]));
				}
				else {
					vs = new VehicleServer(1); 
				}
				
				new AdminWindow(vs);
			}
		});
	}
}
