package Main;

import java.awt.Color;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JPanel;

import Tools.Hitbox;
import Tools.Images;
import Tools.Mouse;

public class Panel extends JPanel implements Runnable {

	// To get rid of the annoying warnings
	private static final long serialVersionUID = 8118108382085995733L;

	JFrame window;
	Thread thread;
	Grid grid1;
	Grid grid2;
	Grid grid;
	Grid opponent;
	KeyManager keyM;

	public int width, height, totalTurns;

	private Hitbox button;

	// Frames per second
	float fps;

	// Has started?
	boolean started;

	// False = player 1, True = player 2
	boolean turn;

	// For clean clicks
	boolean wasDown = false;

	long start = - 1;
	int millis = 0;
	Runnable method;

	public boolean gameover = false;

	public Panel(JFrame window, boolean isAI1, boolean isAI2) {

		this.window = window;

		keyM = new KeyManager();

		// Dimensions of the window
		width = Settings.GRID_TOTAL_WIDTH * 3;
		height = Settings.GRID_TOTAL_HEIGHT * 3 / 2;

		button = new Hitbox((width - 24 * Settings.SCALE) / 2, (height - 8 * Settings.SCALE) / 2, 24 * Settings.SCALE, 8 * Settings.SCALE);

		started = false;
		turn = false;

		// Set size, background color, make it so you can click &
		// focus and save the last rendered frame
		this.setPreferredSize(new Dimension(width, height));
		this.setDoubleBuffered(true);
		this.setFocusable(true);
		this.setBackground(Color.WHITE);
		this.addKeyListener(keyM);

		// Temporarily create grid offset to the center, and
		// manually add ships
		grid1 = new Grid(Settings.GRID_TOTAL_WIDTH * 2 / 7, (height - Settings.GRID_TOTAL_HEIGHT) / 2, width, height, keyM, this, isAI1);

		// Some testing values
		grid1.randomShips();

		grid2 = new Grid(Settings.GRID_TOTAL_WIDTH * 2 / 7, (height - Settings.GRID_TOTAL_HEIGHT) / 2, width, height, keyM, this, isAI2);
		grid2.randomShips();

		grid = grid1;
		opponent = grid2;

	}

	private void wait(double seconds, Runnable runner) {

		start = System.currentTimeMillis();
		millis = (int) (seconds * 1000);
		method = runner;

	}

	public void next() {

		turn = !turn;
		grid = turn ? grid2 : grid1;
		opponent = turn ? grid1 : grid2;

		grid1.pressedBefore = false;
		grid2.pressedBefore = false;

		totalTurns++;

	}

	public void begin() {

		// Create and start the thread
		thread = new Thread(this);
		thread.start();

	}

	@Override
	public void run() {

		long lastTime = System.nanoTime();
		float billion = 1000000000f;

		while (thread != null) {

			// Update & draw
			repaint();

			if (start != - 1 && System.currentTimeMillis() - start >= millis) {

				start = - 1;
				millis = 0;
				method.run();

			}

			// Calculate the fps based on the difference in time from
			// each frame
			fps = billion / (System.nanoTime() - lastTime);

			lastTime = System.nanoTime();

		}

	}

	public void attemptShipAttack(int x, int y) {

		Ship ship = opponent.getShip(x, y);

		if (ship == null) {

			grid.setStatus(x, y, Grid.MISS);

		} else {

			grid.setStatus(x, y, Grid.HIT);
			ship.hits++;

			if (ship.hits >= ship.length) {

				grid.sink(ship);
				ship.sunk = true;

			}

			boolean won = true;

			for (Ship s : opponent.ships) {

				if (!s.sunk) {

					won = false;
					break;

				}

			}

			if (won) {

				grid.won = true;
				gameover = true;

				if (Runner.MODE != Mode.AI_VERSUS_AI) {

					System.out.println("Game over!");
					System.out.println("Player #" + (turn ? 2 : 1) + " won in " + (totalTurns / 2) + " turns!");
					System.exit(200);

				}

			}

		}

		if (Runner.MODE == Mode.PLAYER_VERSUS_PLAYER) {

			wait(3, this::next);

		} else {

			next();

		}

	}

	public void paintComponent(Graphics graphics) {

		Graphics2D g = (Graphics2D) graphics;

		AffineTransform at = g.getTransform();

		// Draw the grid
		grid.draw(g, start != - 1);

		if (!started) {

			g.translate(button.x, button.y);

			g.scale(Settings.SCALE, Settings.SCALE);
			g.drawImage(Images.MISC_IMAGES.get("start"), 0, 0, null);

			if (Mouse.up()) wasDown = true;

			if (wasDown && Mouse.down()) {

				if (button.hasPoint(Mouse.position())) {

					next();

					if (!turn || grid.isAI) started = true;

				}

				wasDown = false;

			}

		}

		g.setTransform(at);
		g.drawImage(grid2.ai.image, 608, 150, 100, 100, null);

		// Remove from memory
		g.dispose();

	}

}