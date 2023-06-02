package Tools;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Images {

	public static HashMap<Integer, Image> SHIP_HORIZONTAL_IMAGES = new HashMap<Integer, Image>();
	public static HashMap<Integer, Image> SHIP_VERTICAL_IMAGES = new HashMap<Integer, Image>();
	public static HashMap<String, Image> MISC_IMAGES = new HashMap<String, Image>();
	
	public static void loadShipImage(String fileName, int type) throws IOException {
		
		// For ship images, load the one in the horizontal and vertical folders
		InputStream isH = Files.getResource("/ships/horizontal/" + fileName);
		InputStream isV = Files.getResource("/ships/vertical/" + fileName);
		Image imgH = ImageIO.read(isH);
		Image imgV = ImageIO.read(isV);
		
		// Map images
		SHIP_HORIZONTAL_IMAGES.put(type, imgH);
		SHIP_VERTICAL_IMAGES.put(type, imgV);
	
	}
	
	public static void loadMiscImage(String fileName, String name) throws IOException {
		
		// Just get resource and map it, similar to loadShipImage()
		InputStream is = Images.class.getResourceAsStream("/" + fileName);
		Image img = ImageIO.read(is);
				
		MISC_IMAGES.put(name, img);
				
	}
	
	public static void scaleFromCenter(Graphics2D g, Image img, int x, int y, float scale, float delta) {
		
		float m = (1 - scale) / 2;
		
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		
		float hwidth = width / 2;
		float hheight = height / 2;
		
		double time = 0.15 * Math.sin(delta / 1000000000f * 4);
				
		AffineTransform at = g.getTransform(); // Save and restore transforms
		g.translate((x + m * width) / scale + hwidth, (y + m * height) / scale + hheight);
		g.rotate(time);
		g.drawImage(img, (int) (-hwidth), (int) (-hheight), width, height, null);
		g.setTransform(at);
		
	}
	
}