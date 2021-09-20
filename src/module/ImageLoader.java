package module;

import javax.swing.ImageIcon;

// Helferklasse, um auch Images sp�ter aus dem Jar-File laden zu k�nnen
public class ImageLoader {

	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	public ImageLoader() {

	}

}
