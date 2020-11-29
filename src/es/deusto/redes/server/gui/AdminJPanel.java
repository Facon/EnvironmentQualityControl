package es.deusto.redes.server.gui;

import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class AdminJPanel extends JPanel {
	private static final long serialVersionUID = -8234215724559828886L;
	
	AdminMaxConnections amc = new AdminMaxConnections();
	AdminConnectedUsers acu = new AdminConnectedUsers();
	AdminTotalUsers atu = new AdminTotalUsers();

	public AdminJPanel() {
		super(new GridLayout(1, 1));

		final JTabbedPane tabbedPane = new JTabbedPane();
	
		tabbedPane.addTab("Conexiones máximas", amc);
		tabbedPane.addTab("Usuarios conectados", acu);
		tabbedPane.addTab("Usuarios totales", atu);
		
		tabbedPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				switch (tabbedPane.getSelectedIndex()) {
				case 1:
					acu.update();
					break;
				case 2:
					atu.update();
					break;
					
				default:
					break;
				}
			}
		});

		//Add the tabbed pane to this panel.
		add(tabbedPane);

		//The following line enables to use scrolling tabs.
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	}
}
