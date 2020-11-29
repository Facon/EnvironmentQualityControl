package es.deusto.redes.server.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AdminMaxConnections extends JPanel {
	private static final long serialVersionUID = 3724236723125573162L;

	private JLabel label = new JLabel("Nº conexiones máximas: ");
	private JFormattedTextField field = new JFormattedTextField();
	private JButton button = new JButton("Aceptar");
	
	public AdminMaxConnections() {
		setLayout(new GridLayout(1, 2, 5, 5));
		
		label.setPreferredSize(new Dimension(150, 25));
		field.setPreferredSize(new Dimension(50, 25));
		
		field.setValue(new Integer(10));
		field.setColumns(10);
			
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String value = field.getText();
				
				if (!value.isEmpty()) {
					AdminWindow.getVehicleServer().setNconnections(Integer.valueOf(value));
				}
			}
		});
		
		add(label);
		add(field);
		add(button);
	}

}
