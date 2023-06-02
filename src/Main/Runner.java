package Main;

import java.awt.FontFormatException;
import java.io.IOException;

import javax.swing.JFrame;

import Tools.Images;
import Tools.Mouse;

public class Runner {

	/*
	 * Change the mode here.
	 * 
	 * Options:
	 * PLAYER_VERSUS_PLAYER
	 * PLAYER_VERSUS_AI
	 * AI_VERSUS_AI
	 */
	public static final Mode MODE = Mode.PLAYER_VERSUS_AI;

	// Lowest ever was 23
	
	// Size 10 batches
	// 59, 63, 61, 42, 50, 60, 67, 56, 69, 45, 61 = tot 633 avg 63
	// 53, 62, 53, 51, 47, 57, 38, 50, 57, 52, 59 = tot 579 avg 58
	// 46, 50, 39, 46, 53, 42, 60, 57, 59, 54, 47 = tot 553 avg 55
	// In all:
	// tot 1765 avg 59
	
	// AI vs AI should get <avg since only one has to win, giving a lower score
	// But somehow it always gives 92 avg (92.11762 for 1,000,000 simulations)
	// After fixing a few bugs, I still get 82 avg (82.479801 for 1,000,000 simulations)
	// But somehow some print statements change the values drastically, as well, but just in some locations
	// It's a headache

	/*
	 * Number of simulations played in AI_VERSUS_AI mode
	 */
	public static final int SIMULATIONS = 100000;

	public static void main(String[] args) throws FontFormatException, IOException {

		Panel panel;

		switch (MODE) {

			case PLAYER_VERSUS_AI:
			case PLAYER_VERSUS_PLAYER:
				// Create window
				JFrame window = new JFrame();
				window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				window.setTitle("Battleship");

				// Setup mouse class so the mouse knows which thing it
				// should be relative to
				Mouse.setWindow(window);
				// Load all assets (images & fonts)
				loadImages();
				Settings.loadImages();
				Settings.loadFont("PressStart2P-Regular.ttf");

				// Create the panel which holds everything about the game
				panel = new Panel(window, false, MODE == Mode.PLAYER_VERSUS_AI);
				// Add the panel to the window
				window.add(panel);

				// Pack up the window so it is ready to be used
				window.pack();

				// Stop from resizing, make visible & go to the middle of
				// the screen
				window.setLocationRelativeTo(null);
				window.setVisible(true);
				window.setResizable(false);

				// Start the thread (and therefore game)
				panel.begin();
				break;

			case AI_VERSUS_AI:

				long start = System.nanoTime();
				
				double total = 0;
				int semisim = SIMULATIONS / 100;

				for (int i = 0; i < SIMULATIONS; i++) {

					NoGraphicsPanel game = new NoGraphicsPanel();

					while (!game.gameover) game.play();

					total += game.turns();
					
					if(i % semisim == 0) System.out.println((i / semisim) + "%");

				}
				
				System.out.println("Average: " + (total / SIMULATIONS));
				System.out.println("Time: " + ((System.nanoTime() - start) / 1000000000d));

				break;

		}

	}

	public static void loadImages() throws IOException {

		// Load all images from res folder (resources)

		// Misc
		Images.loadMiscImage("water1.png", "cell1");
		Images.loadMiscImage("water2.png", "cell2");
		Images.loadMiscImage("water3.png", "cell3");
		Images.loadMiscImage("water4.png", "cell4");
		Images.loadMiscImage("selector.png", "select");
		Images.loadMiscImage("start.png", "start");
		Images.loadMiscImage("hit_marker.png", "hit");
		Images.loadMiscImage("miss_marker.png", "miss");
		Images.loadMiscImage("sunk_marker.png", "sunk");

		// Ships
		Images.loadShipImage("destroyer.png", Ship.DESTROYER);
		Images.loadShipImage("cruiser.png", Ship.CRUISER);
		Images.loadShipImage("submarine.png", Ship.SUBMARINE);
		Images.loadShipImage("battleship.png", Ship.BATTLESHIP);
		Images.loadShipImage("carrier.png", Ship.CARRIER);

	}

}