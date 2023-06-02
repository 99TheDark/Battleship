package Main;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Random;

import Tools.Hitbox;
import Tools.Images;
import Tools.Mouse;
import Tools.Time;

public class Grid {

	// Values to represent states
	public static final int EMPTY = 0;
	public static final int HIT = 1;
	public static final int MISS = 2;
	public static final int SHIP = 3;
	public static final int SUNK = 4;
	public static final char[] SHIP_POSITIONS = {'-', 'X'};
	public static final char ROTATE = 'r';

	public Ship[] ships;
	public Ship draggedShip = null;
	public Hitbox hitbox;
	public int[][] markers, shipPositions;

	private int offX, offY, numShips = 0;
	private boolean clickedPOI = false;
	private boolean rotationRegistered = false;
	public boolean pressedBefore = false;
	private int pageWidth, pageHeight;
	private KeyManager keys;
	private Panel panel;

	public boolean won = false;
	public boolean isAI;
	public AI ai;

	public Grid(boolean ai) {

		ships = new Ship[Ship.TOTAL_SHIPS];

		markers = fill2D(Settings.WIDTH, Settings.HEIGHT, EMPTY);
		shipPositions = fill2D(Settings.WIDTH, Settings.HEIGHT, EMPTY);

		isAI = ai;

		if (isAI) {

			this.ai = new AI(this);

		}

	}

	public Grid(int offX, int offY, int pageWidth, int pageHeight, KeyManager keyManager, Panel panel, boolean ai) {

		keys = keyManager;

		// Create a ship with a length of Ship.TOTAL_SHIPS
		ships = new Ship[Ship.TOTAL_SHIPS];

		// Fill grids with the empty integer
		markers = fill2D(Settings.WIDTH, Settings.HEIGHT, EMPTY);
		shipPositions = fill2D(Settings.WIDTH, Settings.HEIGHT, EMPTY);

		this.pageWidth = pageWidth;
		this.pageHeight = pageHeight;

		this.offX = offX + Settings.CELL_SIZE;
		this.offY = offY + Settings.CELL_SIZE;

		this.panel = panel;

		// The hitbox for the entire grid so the selector doesn't
		// show outside
		hitbox = new Hitbox(0, 0, Settings.GRID_WIDTH, Settings.GRID_HEIGHT);

		isAI = ai;

		if (isAI) {

			this.ai = new AI(this);

		}

	}

	// Easier adding of ship to fixed length array
	public void putShip(Ship ship) {

		ships[numShips++] = ship;

		updateShipPositions();

	}

	public int getStatus(int x, int y) {

		return markers[y][x];

	}

	public void setStatus(int x, int y, int status) {

		markers[y][x] = status;

		if (isAI) ai.status(status, x, y);

	}

	// Update the map of ship or not ship markers, may update to
	// boolean
	public void updateShipPositions() {

		shipPositions = fill2D(Settings.WIDTH, Settings.HEIGHT, EMPTY);

		for (Ship ship : ships) {

			if (ship != null) {

				switch (ship.direction) {

					case Ship.HORIZONTAL:
						for (int i = 0; i < ship.length; i++) {

							shipPositions[ship.x + i][ship.y] = SHIP;

						}
						break;

					case Ship.VERTICAL:
						for (int i = 0; i < ship.length; i++) {

							shipPositions[ship.x][ship.y + i] = SHIP;

						}
						break;

				}

			}

		}

	}

	public void sink(Ship ship) {

		switch (ship.direction) {

			case Ship.HORIZONTAL:
				for (int i = 0; i < ship.length; i++) {

					markers[ship.y][ship.x + i] = SUNK;

				}
				break;

			case Ship.VERTICAL:
				for (int i = 0; i < ship.length; i++) {

					markers[ship.y + i][ship.x] = SUNK;

				}
				break;

		}

		if (isAI) ai.sink(ship);

	}

	// Check if a (x, y) position has a ship
	public boolean hasShip(int x, int y) {

		return shipPositions[x][y] == SHIP;

	}

	public Ship getShip(int x, int y) {

		for (Ship ship : ships) {

			switch (ship.direction) {

				case Ship.HORIZONTAL:
					if (ship.y == y && ship.x <= x && x < ship.x + ship.length) return ship;
					break;

				case Ship.VERTICAL:
					if (ship.x == x && ship.y <= y && y < ship.y + ship.length) return ship;
					break;

			}

		}

		return null;

	}

	// Print the ships for other Battleship projects to use
	public void printShips() {

		updateShipPositions();

		System.out.print(" ");

		for (int i = 0; i < Settings.WIDTH; i++) System.out.print(" " + (i + 1));

		System.out.println();

		for (int j = 0; j < Settings.HEIGHT; j++) {

			System.out.print(Grid.toAlphabetic(j));

			for (int i = 0; i < Settings.WIDTH; i++) {

				System.out.print(" ".repeat(Integer.toString(i).length()) + SHIP_POSITIONS[b2i(hasShip(i, j))]);

			}

			System.out.println();

		}

	}

	public void printStatus() {

		updateShipPositions();

		System.out.print(" ");

		for (int i = 0; i < Settings.WIDTH; i++) System.out.print(" " + (i + 1));

		System.out.println();

		for (int j = 0; j < Settings.HEIGHT; j++) {

			System.out.print(Grid.toAlphabetic(j));

			for (int i = 0; i < Settings.WIDTH; i++) {

				char statusChar = ' ';
				int status = getStatus(i, j);

				if (status == HIT) statusChar = 'X';
				if (status == MISS) statusChar = 'O';
				if (status == SUNK) statusChar = '#';

				System.out.print(" ".repeat(Integer.toString(i).length()) + statusChar);

			}

			System.out.println();

		}

	}

	public boolean positionIsValid(Ship ship, int newX, int newY) {

		if (newX < 0 || newY < 0) return false;

		Hitbox newPos;

		switch (ship.direction) {

			default:
				return false;

			case Ship.HORIZONTAL:
				newPos = new Hitbox(newX, newY, ship.length, 1);

				if (newY >= Settings.HEIGHT) return false;
				if (newX + ship.length > Settings.WIDTH) return false;
				break;

			case Ship.VERTICAL:
				newPos = new Hitbox(newX, newY, 1, ship.length);

				if (newX >= Settings.WIDTH) return false;
				if (newY + ship.length > Settings.HEIGHT) return false;
				break;

		}

		for (Ship other : ships) if (other != null && ship != other) { // Make sure not to check against itself

			if (Hitbox.colliding(newPos, other.size)) return false;

		}

		return true;

	}

	public void randomShips() {

		for (int type : Ship.SHIP_LENGTHS.keySet()) {

			Ship ship = new Ship(0, 0, type, Math.random() < 0.5 ? Ship.HORIZONTAL : Ship.VERTICAL);

			while (true) {

				ship.x = (int) (Math.random() * 10);
				ship.y = (int) (Math.random() * 10);

				if (positionIsValid(ship, ship.x, ship.y)) {

					this.putShip(ship);
					break;

				}

			}

			ship.updateHitbox();

		}

	}

	// Some famous people say D.R.Y.
	public void drawGrid(Graphics2D g) {

		int frameNum = (int) ((Time.second() * Settings.CELL_ANIMATION_SPEED) % Settings.CELL_FRAME_COUNT);
		Image cellImg = Settings.CELL_FRAMES[frameNum];

		// Draw the water at every cell on the grid
		for (int x = - 1; x < Settings.WIDTH; x++) {

			for (int y = - 1; y < Settings.HEIGHT; y++) {

				g.drawImage(cellImg, x * Settings.IMAGE_SIZE, y * Settings.IMAGE_SIZE, null);

			}

		}

		// Length of the number / letter
		int len = (int) Math.ceil(Math.max(Settings.WIDTH / 26f, Settings.HEIGHT / 10f)) + 1;

		// Scale of the text
		float scale = 1f / len;

		// Draw letters & numbers on the side of the grid
		g.setFont(Settings.FONT.deriveFont(Settings.IMAGE_SIZE * scale));
		g.setColor(Settings.TEXT_COLOR);

		FontMetrics fm = g.getFontMetrics();

		float center = (fm.getHeight() - Settings.IMAGE_SIZE) / 2f;

		// Letters
		for (int i = 0; i < Settings.WIDTH; i++) {

			String ch = Grid.toAlphabetic(i);

			// Center
			float x = i * Settings.IMAGE_SIZE + (Settings.IMAGE_SIZE - fm.stringWidth(ch)) / 2f;
			float y = center;
			g.drawString(ch, x, y);

		}

		// Numbers
		for (int i = 0; i < Settings.HEIGHT; i++) {

			String ch = (i + 1) + "";

			// Center
			float x = (Settings.IMAGE_SIZE - fm.stringWidth(ch)) / 2f - Settings.IMAGE_SIZE;
			float y = center + (i + 1) * Settings.IMAGE_SIZE;
			g.drawString(ch, x, y);

		}

	}

	// Draw & update
	public void draw(Graphics2D g, boolean paused) {

		if (!isAI || paused) {

			// TODO: Fix this
			Point mouse = panel.started ? Mouse.position(- 16 * Settings.CELL_SIZE - offX + 9, - offY) : Mouse.position(- offX, - offY);
			// Difference in mouse position from last frame
			Point diff = Mouse.difference();

			// Transformation
			AffineTransform start = g.getTransform();
			g.translate(offX, offY);
			g.scale(Settings.SCALE, Settings.SCALE);

			drawGrid(g);

			AffineTransform boats = g.getTransform();
			g.setTransform(start);

			int xmove = pageWidth - Settings.CELL_SIZE * (Settings.WIDTH - 1);
			g.translate(xmove - offX, offY);
			g.scale(Settings.SCALE, Settings.SCALE);
			drawGrid(g);

			for (int x = 0; x < Settings.WIDTH; x++) {

				for (int y = 0; y < Settings.HEIGHT; y++) {

					Image img = null;

					int status = getStatus(x, y);

					switch (status) {

						case HIT:
							img = Images.MISC_IMAGES.get("hit");
							break;

						case MISS:
							img = Images.MISC_IMAGES.get("miss");
							break;

						case SUNK:
							img = Images.MISC_IMAGES.get("sunk");
							break;

					}

					g.drawImage(img, x * Settings.IMAGE_SIZE, y * Settings.IMAGE_SIZE, null);

					if (!paused) {

						boolean selected = new Hitbox(x * Settings.CELL_SIZE, y * Settings.CELL_SIZE, Settings.CELL_SIZE, Settings.CELL_SIZE).hasPoint(mouse);

						if (panel.started && selected && status == EMPTY && Mouse.up() && pressedBefore) {

							panel.attemptShipAttack(x, y);

						}

					}

				}

			}

			g.setTransform(boats);

			// For each ship in the ships array
			for (Ship ship : ships) {

				if (paused) {

					ship.draw(g);

				} else if (!panel.started) {

					// If the ship exists, ie if you are still placing or I am
					// testing
					if (ship != null) {
						// If the mouse is down, there is no ship being dragged and
						// your mouse is over the ship

						if (Mouse.down() && draggedShip == null && !clickedPOI && ship.hitbox.hasPoint(mouse)) {

							// Set flag 'dragged' to true, and set the currently dragged
							// ship to itself.
							ship.startDrag();
							draggedShip = ship;

						}

						// If the mouse is released
						if (Mouse.up()) {

							// Turn the flag 'dragged' to false
							ship.endDrag();

							// If this is the ship that was being dragged
							if (draggedShip == ship) {

								// Get snapped to grid position
								int snapX = Math.round(ship.x + (float) (ship.dragX + ship.offsetX) / Settings.IMAGE_SIZE);
								int snapY = Math.round(ship.y + (float) (ship.dragY + ship.offsetY) / Settings.IMAGE_SIZE);

								if (positionIsValid(ship, snapX, snapY)) {

									// Snap to grid
									ship.x = snapX;
									ship.y = snapY;

									// Update the hitbox based on it's new position
									ship.updateHitbox();

								} else {

									ship.direction = ship.originalDirection;
									ship.reloadRotation(ship.direction);

								}

								// Set there to be no dragged ship
								draggedShip = null;

								// Haven't clicked anything anymore
								clickedPOI = false;

							}

							ship.dragX = 0;
							ship.dragY = 0;
							ship.offsetX = 0;
							ship.offsetY = 0;

						}

						// If a ship is being dragged, change the drag position
						// which is it's offset
						if (draggedShip == ship) {

							ship.dragX = diff.x / Settings.SCALE;
							ship.dragY = diff.y / Settings.SCALE;

						} else {

							// Draw the ship to the screen
							ship.draw(g);

						}

					}

				} else {

					ship.draw(g);

				}

			}

			if (!paused) {

				if (draggedShip == null) {

					// Draw the selector when not dragging & mouse is within
					// grid
					if (hitbox.hasPoint(mouse)) {

						// Gotta fix…
						int selectedX = mouse.x / Settings.CELL_SIZE;
						int selectedY = mouse.y / Settings.CELL_SIZE;

						int move = panel.started ? pageWidth - Settings.GRID_WIDTH + Settings.CELL_SIZE : 2 * offX;

						g.setTransform(start);
						g.scale(Settings.SCALE, Settings.SCALE);
						g.drawImage(Images.MISC_IMAGES.get("select"), selectedX * Settings.IMAGE_SIZE + (move - offX) / Settings.SCALE, offY / Settings.SCALE + selectedY * Settings.IMAGE_SIZE, null);

					}

				} else {

					if (keys.keyDown(ROTATE)) {

						if (!rotationRegistered) {

							rotationRegistered = true;
							draggedShip.rotate();

						}

					} else {

						rotationRegistered = false;

					}

					draggedShip.draw(g);

				}

			}

			g.setTransform(start);

			pressedBefore = !paused && Mouse.down();

		} else {

			if (panel.started) {

				Guess guess = ai.guess();

				int status = getStatus(guess.getX(), guess.getY());

				if (status == EMPTY) {

					panel.attemptShipAttack(guess.getX(), guess.getY());

				}

			}

		}

	}

	// Function for quickly filling a 2D array with one value
	public int[][] fill2D(int w, int h, int value) {

		int[][] arr = new int[w][h];
		Arrays.stream(arr).forEach(v -> Arrays.fill(v, value));
		return arr;

	}

	// Function to convert false to 0 and true to 1 for cleaner
	// code
	public int b2i(boolean bool) {

		return bool ? 1 : 0;

	}

	// So you can get AA AB… after the alphabet is used
	// From
	// https://stackoverflow.com/questions/10813154/how-do-i-convert-a-number-to-a-letter-in-java
	public static String toAlphabetic(int i) {

		if (i < 0) {

			return "-" + toAlphabetic(- i - 1);

		}

		int quot = i / 26;
		int rem = i % 26;
		char letter = (char) ((int) 'A' + rem);

		if (quot == 0) {

			return "" + letter;

		} else {

			return toAlphabetic(quot - 1) + letter;

		}

	}

}