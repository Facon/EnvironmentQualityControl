package es.deusto.redes.server.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.concurrent.ConcurrentMap;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;

import es.deusto.redes.data.dao.User;

public class AdminConnectedUsers extends JPanel {
	private static final long serialVersionUID = -8513609425994216570L;

	DefaultListModel<String> lm = new DefaultListModel<String>();
	JList<String> list = new JList<String>();
	JButton button = new JButton("Desconectar");
	
	public AdminConnectedUsers() {		
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String user = list.getSelectedValue();
				try {
					AdminWindow.getVehicleServer().getUsers().remove(user);
					AdminWindow.getVehicleServer().removeHandler(user);
					lm.remove(list.getSelectedIndex());
				} catch (NullPointerException e) {
					// No hace nada
				} catch (ArrayIndexOutOfBoundsException e) {
					// TODO: handle exception
				}
							
			}
		});
		
		list.setModel(lm);
		add(list);
		add(button);
	}
	
	public void update() {
		lm.clear();
		
		ConcurrentMap<String, User> users = AdminWindow.getVehicleServer().getUsers();
		Iterator<String> iter = users.keySet().iterator();
		
		while (iter.hasNext()) {
			lm.addElement(iter.next());
		}
	}
}
