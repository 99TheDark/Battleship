package Encoding;

import java.util.Arrays;

public class GridUtils {

	public static final int EMPTY = 0;
	public static final int HIT = 1;
	public static final int MISS = 2;

	private static final char[] VALS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'};

	public static String encode(ShipPosition[] ships) throws ShipPositionOutOfBoundsException {

		String str = "";

		for (ShipPosition pos : ships) {

			if (pos == null) continue;

			try {

				str += VALS[pos.getX()];
				str += VALS[pos.getY()];
				str += VALS[pos.getWidth()];
				str += VALS[pos.getHeight()];

			} catch (Exception e) {

				throw new ShipPositionOutOfBoundsException(pos);

			}

		}

		return str;

	}

	public static ShipLocationData[] decode(String str) {
		
		ShipLocationData[] ships = new ShipLocationData[str.length() / 4];

		for (int i = 0, j = 0; i < str.length(); i += 4, j++) {

			char[] ship = str.substring(i, i + 4).toCharArray();

			int x = Arrays.binarySearch(VALS, ship[0]);
			int y = Arrays.binarySearch(VALS, ship[1]);
			int w = Arrays.binarySearch(VALS, ship[2]);
			int h = Arrays.binarySearch(VALS, ship[3]);
			
			ships[j] = new ShipLocationData(x, y, w, h);

		}

		return ships;

	}

}