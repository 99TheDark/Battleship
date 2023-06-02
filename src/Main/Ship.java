package Main;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.util.HashMap;

import Encoding.ShipPosition;
import Tools.Hitbox;
import Tools.Images;

public class Ship implements ShipPosition {

	// A bunch of states
	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;

	public static final int DESTROYER = 2;
	public static final int CRUISER = 3;
	public static final int SUBMARINE = 4;
	public static final int BATTLESHIP = 5;
	public static final int CARRIER = 6;

	public static final HashMap<Integer, Integer> SHIP_LENGTHS;

	// Map the ship state to its length
	static {

		SHIP_LENGTHS = new HashMap<Integer, Integer>();

		SHIP_LENGTHS.put(DESTROYER, 2);
		SHIP_LENGTHS.put(CRUISER, 3);
		SHIP_LENGTHS.put(SUBMARINE, 3);
		SHIP_LENGTHS.put(BATTLESHIP, 4);
		SHIP_LENGTHS.put(CARRIER, 5);

	}

	// Calculate total # of ships
	public static final int TOTAL_SHIPS = SHIP_LENGTHS.size();

	public int x, y, type, length, direction, dragX, dragY, offsetX, offsetY, hits, originalDirection;
	public Hitbox hitbox, size;
	public boolean dragged, sunk;
	public Image sprite;
	public long start;

	private int offrotlength;

	public Ship(int x, int y, int type, int direction) {

		this.x = x;
		this.y = y;
		this.type = type;
		this.direction = direction;
		
		start = -1;

		// Offset when being dragged
		dragX = 0;
		dragY = 0;

		// I can't get the code to work, so I'll just bs it
		offsetX = 0;
		offsetY = 0;

		// If it is currently being dragged
		dragged = false;

		// Calculate length based on HashMap defined above
		length = SHIP_LENGTHS.get(type);
		
		// Not hit yet!
		hits = 0;

		// For rotations
		offrotlength = (int) ((length - 1) / 2.0 * Settings.IMAGE_SIZE);
		
		sunk = false;

		// Make it's hitbox and sprite different depending on its
		// type and direction
		reloadRotation(direction);

	}

	public void reloadRotation(int direction) {

		switch (direction) {

			case HORIZONTAL:
				hitbox = new Hitbox(x * Settings.CELL_SIZE, y * Settings.CELL_SIZE, length * Settings.CELL_SIZE, Settings.CELL_SIZE);
				size = new Hitbox(x, y, length, 1);
				sprite = Images.SHIP_HORIZONTAL_IMAGES.get(type);
				break;

			case VERTICAL:
				hitbox = new Hitbox(x * Settings.CELL_SIZE, y * Settings.CELL_SIZE, Settings.CELL_SIZE, length * Settings.CELL_SIZE);
				size = new Hitbox(x, y, 1, length);
				sprite = Images.SHIP_VERTICAL_IMAGES.get(type);
				break;

		}

	}

	public void rotate() {

		// Swap rotation (assuming only one of these)
		direction = direction == HORIZONTAL ? VERTICAL : HORIZONTAL;

		switch (direction) {

			case VERTICAL:
				offsetX += offrotlength;
				offsetY += - offrotlength;
				break;

			case HORIZONTAL:
				offsetX += - offrotlength;
				offsetY += offrotlength;
				break;

		}

		reloadRotation(direction);

	}

	public void startDrag() {

		dragged = true;
		start = System.nanoTime();
		
		originalDirection = direction;

	}

	public void endDrag() {

		dragged = false;
		start = - 1;

	}

	// Update its hitbox, used after being let go and snapped to
	// new position on grid
	public void updateHitbox() {

		hitbox.x = x * Settings.CELL_SIZE;
		hitbox.y = y * Settings.CELL_SIZE;

		size.x = x;
		size.y = y;

	}

	// Draw the ship to the screen
	public void draw(Graphics2D g) {

		int xp = x * Settings.IMAGE_SIZE;
		int yp = y * Settings.IMAGE_SIZE;
		float scale = 1f;

		// If dragged, offset by that amount
		if (dragged) {

			xp += dragX + offsetX;
			yp += dragY + offsetY;
			scale = 1.2f;

		}

		float delta = (start == - 1) ? 0f : (float) (System.nanoTime() - start);

		AffineTransform at = g.getTransform();
		g.scale(scale, scale);
		Images.scaleFromCenter(g, sprite, xp, yp, scale, delta);
		g.setTransform(at);

	}

	@Override
	public int getX() {

		return x;

	}

	@Override
	public int getY() {

		return y;

	}

	@Override
	public int getWidth() {

		switch (direction) {

			case HORIZONTAL:
				return length;

			case VERTICAL:
				return 1;

		}

		return 0;

	}

	@Override
	public int getHeight() {

		switch (direction) {

			case HORIZONTAL:
				return 1;

			case VERTICAL:
				return length;

		}

		return 0;

	}

}