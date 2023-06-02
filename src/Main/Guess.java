package Main;

public class Guess {
	
	private int x, y;
	
	public Guess(int x, int y) {
		
		this.x = x;
		this.y = y;
		
	}
	
	public int getX() {
		
		return x;
		
	}
	
	public int getY() {
		
		return y;
		
	}
	
	public String toString() {
		
		return "(" + x + ", " + y + ")";
		
	}

}