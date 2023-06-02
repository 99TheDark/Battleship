package Tools;
import java.awt.Point;

public class Hitbox {
	
	public int x, y, width, height;
	
	public Hitbox(int x, int y, int width, int height) {
		
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
	}
	
	public static boolean colliding(Hitbox h1, Hitbox h2) {
		
		return 
			h1.x + h1.width > h2.x && 
			h1.x < h2.x + h2.width && 
			h1.y + h1.height > h2.y && 
			h1.y < h2.y + h2.height;
		
	}
	
	public boolean hasPoint(Point point) {
		
		return x < point.x && point.x < x + width && y < point.y && point.y < y + height;
		
	}
	
	public String toString() {
		
		return "[" + x + ", " + y + ", " + width + ", " + height + "]";
		
	}
	
}