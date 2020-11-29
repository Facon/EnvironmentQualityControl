package es.deusto.redes.client.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.h2.util.IOUtils;

public class ClientPhotoJPanel extends JPanel {
	private static final long serialVersionUID = -7280246446088222533L;
	
	private BufferedImage image;
	private JLabel coordinate = new JLabel();
	
	public ClientPhotoJPanel() {
		add(new JPanel() {
			private static final long serialVersionUID = 4484041605618189263L;

			protected void paintComponent(Graphics g) {
				g.drawImage(image, 0, 0, null);

				super.paintComponent(g);
			}
		});
		add(coordinate);
	}
	
	public void init() {
		try {                
			byte[] rawImage = ClientWindow.getInstance().getClient().getPhoto();

			if (rawImage != null) {
				image = ImageIO.read(new ByteArrayInputStream(rawImage));
				//image = ImageIO.read(new URL("http://upload.wikimedia.org/wikipedia/commons/thumb/a/a1/Latin_G.svg/435px-Latin_G.svg.png"));
				
				coordinate.setText(ClientWindow.getInstance().getClient().getLoc());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters            
    }
}
