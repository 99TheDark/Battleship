package Main;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class AI {

	// Thank you Daniel
	public static final double[][] START = {{0.0705, 0.1075, 0.1353, 0.1521, 0.1599, 0.1591, 0.1519, 0.1386, 0.1098, 0.0726}, {0.1066, 0.1302, 0.1658, 0.18, 0.1869, 0.1796, 0.1784, 0.1623, 0.1377, 0.1108}, {0.1358, 0.1614, 0.1907, 0.2045, 0.2097, 0.2104, 0.2055, 0.1918, 0.163, 0.1389}, {0.1537, 0.1739, 0.2064, 0.2115, 0.2185, 0.2134, 0.214, 0.2036, 0.171, 0.1487}, {0.1594, 0.1802, 0.2089, 0.2187, 0.2203, 0.2209, 0.2123, 0.2069, 0.1738, 0.1572}, {0.165, 0.1859, 0.2127, 0.2175, 0.2286, 0.2287, 0.2199, 0.2089, 0.1792, 0.1597}, {0.1547, 0.179, 0.2019, 0.2032, 0.2165, 0.2151, 0.2083, 0.2029, 0.173, 0.1501}, {0.1355, 0.162, 0.1882, 0.1902, 0.2074, 0.2068, 0.1969, 0.1839, 0.1611, 0.1343}, {0.112, 0.1419, 0.1667, 0.178, 0.1933, 0.1872, 0.1812, 0.1596, 0.1387, 0.1102}, {0.0754, 0.1059, 0.1333, 0.1516, 0.1591, 0.1634, 0.1552, 0.1365, 0.1141, 0.079}};

	public static final double MIN = - Double.MAX_VALUE;
	public static final double OUT = -1e308;

	private Grid grid;
	private double[][] heatmap;

	BufferedImage image;
	File output;
	Graphics2D g;

	public AI(Grid grid) {

		this.grid = grid;

		output = new File("res/out/heatmap.png");

		heatmap = START;

		generateHeatMapImage();

	}

	public void generateHeatMapImage() {

		if (Runner.MODE != Mode.AI_VERSUS_AI) {

			image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
			g = image.createGraphics();

			g.setColor(Color.WHITE);
			g.fillRect(0, 0, 10, 10);

			for (int yp = 0; yp < 10; yp++) for (int xp = 0; xp < 10; xp++) {

				int status = grid.getStatus(xp, yp);

				if (status == Grid.EMPTY) {

					double heat = heatmap[yp][xp];

					if (heat == OUT) {

						g.setColor(Color.GRAY);

					} else {

						float h = (float) heat;
						h = Math.min(Math.max(h, 0f), 0.8f);
						g.setColor(Color.getHSBColor(h, 1f, 1f));

					}

				} else {

					g.setColor(status == Grid.MISS ? Color.WHITE : Color.BLACK);

				}

				g.fillRect(xp, yp, 1, 1);

			}

			try {

				ImageIO.write(image, "png", output);

			} catch (IOException e) {

				System.out.println(e);

			}

		}

	}

	public void tryHeatChange(int x, int y, double val) {

		if (x < 0 || x > 9 || y < 0 || y > 9) return;
		if (grid.getStatus(x, y) != Grid.EMPTY) return;

		heatmap[y][x] += val;

	}

	public int tryGetStatus(int x, int y) {

		if (x < 0 || x > 9 || y < 0 || y > 9) return Grid.EMPTY;

		return grid.getStatus(x, y);

	}

	public void sink(Ship ship) {

		double edge = 0.2;
		double around = - 0.3;
		double corner = 0.05;

		switch (ship.direction) {

			case Ship.HORIZONTAL:
				tryHeatChange(ship.x - 1, ship.y, edge);
				tryHeatChange(ship.x + ship.length + 1, ship.y, edge);

				for (int i = 0; i < ship.length; i++) {

					status(Grid.MISS, ship.x + i, ship.y, around, corner);

				}
				break;

			case Ship.VERTICAL:
				tryHeatChange(ship.x, ship.y - 1, edge);
				tryHeatChange(ship.x, ship.y + ship.length + 1, edge);
				for (int i = 0; i < ship.length; i++) {

					status(Grid.MISS, ship.x, ship.y + i, around, corner);

				}
				break;

		}

	}

	private boolean mini(int x, int y) {

		int status = tryGetStatus(x, y);

		return status == Grid.MISS || status == Grid.SUNK;

	}

	public void updateHeatMap() {

		for (int yp = 0; yp < 10; yp++) {

			for (int xp = 0; xp < 10; xp++) {

				boolean top = mini(xp, yp + 1);
				boolean bottom = mini(xp, yp - 1);
				boolean left = mini(xp - 1, yp);
				boolean right = mini(xp + 1, yp);

				if (top && bottom && left && right) heatmap[yp][xp] = OUT;

			}

		}

	}

	public void status(int status, int x, int y, double change, double changeCorner) {

		tryHeatChange(x - 1, y - 1, changeCorner);
		tryHeatChange(x, y - 1, change);
		tryHeatChange(x + 1, y - 1, changeCorner);
		tryHeatChange(x - 1, y, change);
		tryHeatChange(x + 1, y, change);
		tryHeatChange(x - 1, y + 1, changeCorner);
		tryHeatChange(x, y + 1, change);
		tryHeatChange(x + 1, y + 1, changeCorner);
		
		heatmap[y][x] = OUT;

		updateHeatMap();

		generateHeatMapImage();

	}

	public void status(int status, int x, int y) {

		boolean hit = status == Grid.HIT;

		double change = hit ? 0.3 : - 0.08;
		double changeCorner = hit ? - 0.2 : 0.2;

		status(status, x, y, change, changeCorner);

	}

	public Guess guess() {

		ArrayList<Guess> guesses = new ArrayList<Guess>();

		for (int x = 0; x < 10; x++) {

			for (int y = 0; y < 10; y++) {

				int status = grid.getStatus(x, y);

				if (status == Grid.EMPTY) guesses.add(new Guess(x, y));

			}

		}

		double hottest = MIN;
		Guess hottestGuess = null;

		for (Guess guess : guesses) {

			double heat = heatmap[guess.getY()][guess.getX()];

			if (heat > hottest) {

				hottestGuess = guess;
				hottest = heat;

			}

		}

		return hottestGuess;

	}

	public String formattedHeatMap() {

		DecimalFormat df = new DecimalFormat("0.00");

		String str = "";

		for (double[] row : heatmap) {

			for (double heat : row) {

				String level = df.format(heat);
				if(heat == MIN) level = "#";
				if(heat == OUT) level = "*";

				str += " " + level + " ";

			}

			str = str.substring(0, str.length() - 1) + "\n";

		}

		return str.substring(0, str.length() - 1);

	}

	public void printHeatMap() {

		System.out.println(formattedHeatMap() + "\n");

	}

}