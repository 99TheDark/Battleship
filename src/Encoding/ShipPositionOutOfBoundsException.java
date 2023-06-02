package Encoding;

public class ShipPositionOutOfBoundsException extends Exception {

	private static final long serialVersionUID = 5445379157790155466L;

	public ShipPositionOutOfBoundsException(ShipPosition pos) {
		
		super(
			"Ship (" + pos.getX() + ", " + pos.getY() + ") with size [" + 
			pos.getWidth() + ", " + pos.getHeight() + "] is out of bounds!"
		);
		
	}
	
}
