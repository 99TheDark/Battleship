package Tools;
import java.awt.Point;

import javax.swing.JFrame;

public class Mouse {

	private static MouseData MOUSE_DATA;

	public static void setWindow(JFrame window) {

		MOUSE_DATA = new MouseData(window);

	}

	public static boolean down() {

		return MOUSE_DATA.isMouseDown();

	}

	public static boolean up() {

		return !MOUSE_DATA.isMouseDown();

	}

	public static boolean in() {

		return MOUSE_DATA.isMouseInWindow();

	}

	public static boolean out() {

		return !MOUSE_DATA.isMouseInWindow();

	}

	public static Point position() {

		return MOUSE_DATA.getMouse();

	}

	public static Point position(int x, int y) {

		Point mouse = position();
		
		return new Point(mouse.x + x, mouse.y + y);

	}

	public static Point lastPosition() {

		return MOUSE_DATA.getLastMouse();

	}
	
	public static Point lastPosition(int x, int y) {

		Point mouse = lastPosition();
		
		return new Point(mouse.x + x, mouse.y + y);

	}
	
	public static Point difference() {
		
		Point mouse = position();
		Point lmouse = lastPosition();
		
		return new Point(mouse.x - lmouse.x, mouse.y - lmouse.y);
		
	}

}