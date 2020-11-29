package es.deusto.redes.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import es.deusto.redes.client.Client;

public class ClientLoginJPanel extends JPanel {
	private static final long serialVersionUID = 532698520245969064L;
	
	private JLabel ipJLabel = new JLabel("IP: ");
	private JLabel nickJLabel = new JLabel("Nick: ");
	private JLabel passJLabel = new JLabel("Contraseña: ");
	private JFormattedTextField ipJFormattedTextField = new JFormattedTextField("localhost");
	private JTextField nickJTextField = new JTextField("Asier", 10);
	private JTextField passJTextField = new JTextField("1234", 10);
	private JButton accessJButton = new JButton("Login");
	
	public ClientLoginJPanel() {
		ipJFormattedTextField.setColumns(10);
		//ipJFormattedTextField.setValue("127.0.0.1");
		ipJFormattedTextField.setText("127.0.0.1");
		
		accessJButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!ipJFormattedTextField.getText().isEmpty() || !nickJTextField.getText().isEmpty() || !passJTextField.getText().isEmpty()) {
					ClientWindow.getInstance().setClient(new Client(ipJFormattedTextField.getText(), 4444));
					Client client = ClientWindow.getInstance().getClient();
					
					// ENTRAR EN LA NUEVA VENTANA
					try {					
						if (client.login(nickJTextField.getText(), passJTextField.getText())) {
							JFrame x = (JFrame) getParent().getParent().getParent().getParent();
							x.dispose();
							ClientWindow.getInstance().getCmjpanel().init();
							ClientWindow.getInstance().getCmj().setVisible(true);
							ClientWindow.getInstance().getCpjpanel().init();
							ClientWindow.getInstance().getCpj().setVisible(true);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		add(ipJLabel);
		add(ipJFormattedTextField);
		add(nickJLabel);
		add(nickJTextField);
		add(passJLabel);
		add(passJTextField);
		add(accessJButton);
	}
}
