package Main;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.io.IOException;

import Tools.Files;
import Tools.Images;

public class Settings {

	// Global settings and constants
	public static final int WIDTH = 10;
	public static final int HEIGHT = 10;
	public static final int SCALE = 5;
	public static final int IMAGE_SIZE = 8;
	public static final Color TEXT_COLOR = Color.BLACK;
	public static final int TEXT_WIDTH = 7;
	public static final int TEXT_HEIGHT = 8; 

	// Calculated constants
	public static final int CELL_SIZE = SCALE * IMAGE_SIZE;
	public static final int GRID_WIDTH = CELL_SIZE * WIDTH;
	public static final int GRID_HEIGHT = CELL_SIZE * HEIGHT;
	public static final int GRID_TOTAL_WIDTH = CELL_SIZE * (WIDTH + 1); // +1 for numbers & letters on the side
	public static final int GRID_TOTAL_HEIGHT = CELL_SIZE * (HEIGHT + 1);

	// So points still have positions and I don't have to check if they equal null, super far to the top left
	public static final Point NULL_POINT = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

	// For animating water
	public static final String[] CELL_FRAME_NAMES = {"cell1", "cell2", "cell3", "cell4"};
	public static final int CELL_FRAME_COUNT = CELL_FRAME_NAMES.length;
	public static final Image[] CELL_FRAMES = new Image[CELL_FRAME_COUNT];
	public static final float CELL_ANIMATION_SPEED = 4.0f;

	// Font for text
	public static Font FONT = null;

	public static void loadFont(String location) throws FontFormatException, IOException {

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		FONT = Font.createFont(Font.TRUETYPE_FONT, Files.getResource("/fonts/" + location));
		ge.registerFont(FONT);

	}

	public static void loadImages() {

		// Populate CELL_FRAMES
		for (int i = 0; i < CELL_FRAME_COUNT; i++) {

			CELL_FRAMES[i] = Images.MISC_IMAGES.get(CELL_FRAME_NAMES[i]);

		}

	}

}