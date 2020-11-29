package es.deusto.redes.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import es.deusto.redes.data.dao.Measure;
import es.deusto.redes.data.dao.Sensor;
import javax.swing.JButton;

public class ClientMenuJPanel extends JPanel {
	private static final long serialVersionUID = 7080413320975978935L;
	
	private DefaultListModel<String> dlmsensors = new DefaultListModel<String>();
	private DefaultListModel<String> dlmmeasures = new DefaultListModel<String>();
	private JList<String> list_1;
	private JList<String> list_2;
	private JTextArea textArea;
	private JButton btnApagar = new JButton("Apagar");
	private JLabel lblGps = new JLabel("GPS: ");
	private JButton btnGps = new JButton("GPS");

	public ClientMenuJPanel() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(10, 36, 131, 169);
		add(scrollPane_2);
		
		list_1 = new JList<String>();
		scrollPane_2.setViewportView(list_1);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(309, 36, 131, 169);
		add(scrollPane_3);
		
		list_2 = new JList<String>();
		scrollPane_3.setViewportView(list_2);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 241, 430, 92);
		add(scrollPane);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JLabel lblMeasuremntDetails = new JLabel("Detalles de la medida");
		lblMeasuremntDetails.setHorizontalAlignment(SwingConstants.CENTER);
		lblMeasuremntDetails.setBounds(10, 216, 430, 14);
		add(lblMeasuremntDetails);
		
		JLabel lblSensors = new JLabel("Sensores");
		lblSensors.setHorizontalAlignment(SwingConstants.CENTER);
		lblSensors.setBounds(10, 11, 131, 14);
		add(lblSensors);
		
		JLabel lblMeasures = new JLabel("Hist\u00F3rico de medidas");
		lblMeasures.setHorizontalAlignment(SwingConstants.CENTER);
		lblMeasures.setBounds(309, 11, 131, 14);
		add(lblMeasures);
		
		list_1.setModel(dlmsensors);
		list_2.setModel(dlmmeasures);
		
		lblGps.setBounds(10, 348, 46, 14);
		add(lblGps);
		
		btnGps.setBounds(52, 344, 89, 23);
		add(btnGps);
		
		btnApagar.setEnabled(false);
		btnApagar.setBounds(151, 34, 89, 23);
		add(btnApagar);
		
		btnApagar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (ClientWindow.getInstance().getClient().getSensor(list_1.getSelectedIndex()).isState()) {
						Measure actualmeasure = ClientWindow.getInstance().getClient().getSensorActualMeasure(list_1.getSelectedIndex());
						ClientWindow.getInstance().getClient().setSensorState(list_1.getSelectedIndex(), false);
						btnApagar.setText("Encender");
					}
					else {
						ClientWindow.getInstance().getClient().setSensorState(list_1.getSelectedIndex(), true);
						btnApagar.setText("Apagar");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		btnGps.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (btnGps.getText().equals("OFF")) {
						ClientWindow.getInstance().getClient().onGPS();
						btnGps.setText("ON");
					}
					else {
						ClientWindow.getInstance().getClient().offGPS();
						btnGps.setText("OFF");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		list_1.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					try {
						btnApagar.setEnabled(true);
						dlmmeasures.clear();
						textArea.setText("");
						if (ClientWindow.getInstance().getClient().getSensor(list_1.getSelectedIndex()).isState()) {
							btnApagar.setText("Apagar");
						}
						else {
							btnApagar.setText("Encender");
						}
						
						List<Measure> measures = ClientWindow.getInstance().getClient().getSensorMeasures(list_1.getSelectedIndex());
						Iterator<Measure> iter = measures.iterator();

						while (iter.hasNext()) {
							dlmmeasures.addElement(iter.next().getDate().toString());
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		list_2.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				if (!arg0.getValueIsAdjusting()) {
					try {
						Measure measure = ClientWindow.getInstance().getClient().getSensorMeasures(list_1.getSelectedIndex()).get(list_2.getSelectedIndex());
						textArea.setText("");
						textArea.append("Fecha: " + measure.getDate() + "\n");
						textArea.append("Coordenadas: " + measure.getCoordinate() + "\n");
						textArea.append("Valor: " + measure.getValue() + "\n");
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
	}
	
	public void init() {
		try {
			List<Sensor> sensors = ClientWindow.getInstance().getClient().getListSensors();
			Iterator<Sensor> iter = sensors.iterator();
			
			while (iter.hasNext()) {
				dlmsensors.addElement(iter.next().getDescription());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
