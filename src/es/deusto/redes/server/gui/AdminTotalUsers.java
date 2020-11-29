package es.deusto.redes.server.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import es.deusto.redes.data.dao.User;

public class AdminTotalUsers extends JPanel {
	private static final long serialVersionUID = 1540923285606862132L;

	private DefaultListModel<String> lm = new DefaultListModel<String>();
	private JList<String> list = new JList<String>();
	private JLabel nickJLabel = new JLabel("Nick: ");
	private JLabel passJLabel = new JLabel("Contraseña: ");
	private JLabel adminJLabel = new JLabel("¿Admin?: ");
	private JTextField nickJTextField = new JTextField(10);
	private JTextField passJTextField = new JTextField(10);
	private JCheckBox adminJCheckBox = new JCheckBox();
	private JButton addUserButton = new JButton("Añadir");
	private JButton modUserButton = new JButton("Modificar");
	private JButton delUserButton = new JButton("Borrar");
	private User user1;
	
	public AdminTotalUsers () {
		//AdminWindow.getVehicleServer()
		
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				try {
					User user = AdminWindow.getVehicleServer().getUserFromDB(list.getSelectedValue());
					
					user1 = user;
					
					nickJTextField.setText(user.getNick());
					passJTextField.setText(user.getPassword());
					adminJCheckBox.setSelected(user.isType());
				} catch (NullPointerException e1) {
				}
			}
		});
		
		addUserButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AdminWindow.getVehicleServer().addUserToDB(new User(nickJTextField.getText(), passJTextField.getText(), adminJCheckBox.isSelected()));
				update();
			}
		});
		
		modUserButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AdminWindow.getVehicleServer().modUserToDB(user1, new User(nickJTextField.getText(), passJTextField.getText(), adminJCheckBox.isSelected()));
				update();
			}
		});
		
		delUserButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AdminWindow.getVehicleServer().delUserFromDB(new User(nickJTextField.getText(), passJTextField.getText(), adminJCheckBox.isSelected()));
				update();
			}
		});
		
		list.setModel(lm);		
		add(list);
		add(nickJLabel);
		add(nickJTextField);
		add(passJLabel);
		add(passJTextField);
		add(adminJLabel);
		add(adminJCheckBox);
		add(addUserButton);
		add(modUserButton);
		add(delUserButton);
	}

	public void update() {
		lm.clear();
		
		Set<User> users = AdminWindow.getVehicleServer().getUsersFromDB();
		Iterator<User> iter = users.iterator();
		
		while (iter.hasNext()) {
			lm.addElement(iter.next().getNick());
		}
	}

}
