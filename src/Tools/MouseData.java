package Tools;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

import Main.Settings;

public class MouseData implements MouseListener {

	private JFrame window;
	private boolean mouseDown = false;
	private boolean mouseInWindow = false;
	private Point lastMousePos = Settings.NULL_POINT;

	public MouseData(JFrame window) {

		this.window = window;

		window.addMouseListener(this);

	}

	public Point getMouse() {

		Point mousePos = window.getContentPane().getMousePosition();

		if (mousePos == null) mousePos = Settings.NULL_POINT;

		return mousePos;

	}

	public Point getLastMouse() {

		return lastMousePos;

	}

	public boolean isMouseDown() {

		return mouseDown;

	}

	public boolean isMouseInWindow() {

		return mouseInWindow;

	}

	@Override
	public void mousePressed(MouseEvent e) {

		mouseDown = true;
		lastMousePos = getMouse();

	}

	@Override
	public void mouseReleased(MouseEvent e) {

		mouseDown = false;

	}

	@Override
	public void mouseEntered(MouseEvent e) {

		mouseInWindow = true;

	}

	@Override
	public void mouseExited(MouseEvent e) {

		mouseInWindow = false;

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

}