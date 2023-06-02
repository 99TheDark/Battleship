package Encoding;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ShipLocationData implements ShipPosition {

	private int x, y, w, h;

	public ShipLocationData(int x, int y, int w, int h) {

		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;

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

		return w;

	}

	@Override
	public int getHeight() {

		return h;

	}

	public String toString() {

		return "[" + x + ", " + y + ", " + w + ", " + h + "]";

	}

	public static String toString(ShipLocationData[] ships) {

		return "[ " + Arrays.asList(ships).stream().map(ShipLocationData::toString).collect(Collectors.joining(", ")) + "]";

	}

}
