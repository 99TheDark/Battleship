package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

public class KeyManager implements KeyListener {

	private HashMap<Character, Boolean> keys;

	public KeyManager() {

		keys = new HashMap<Character, Boolean>();

	}

	public boolean keyDown(char ch) {

		if(keys.containsKey(ch)) return keys.get(ch);
		
		return false;

	}

	public boolean keyDown(String str) {

		return keyDown(str.charAt(0));

	}
	
	public boolean keyUp(char ch) {
		
		return !keyDown(ch);
		
	}
	
	public boolean keyUp(String str) {
		
		return keyUp(str.charAt(0));
		
	}

	@Override
	public void keyPressed(KeyEvent e) {

		keys.put(e.getKeyChar(), true);
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		keys.put(e.getKeyChar(), false);
		
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

}
