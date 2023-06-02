package Tools;
public class Time {

	public static final float THOUSAND = 1000;
	public static final float MILLION = 1000000;
	public static final float BILLION = 1000000000;

	private static long startTime = System.nanoTime();

	public static float nano() {

		return System.nanoTime() - startTime;

	}

	public static float micro() {

		return nano() / THOUSAND;

	}

	public static float millis() {

		return nano() / MILLION;

	}

	public static float second() {

		return nano() / BILLION;

	}

}