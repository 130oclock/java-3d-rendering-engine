package engine.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener {

	private boolean[] keys = new boolean[66568];
	private boolean left, right, forward, backward, up, down, kup, kdown, kleft, kright, krleft, krright;
	
	public void update() {
		this.left = this.keys[KeyEvent.VK_A];
		this.right = this.keys[KeyEvent.VK_D];
		this.forward = this.keys[KeyEvent.VK_W];
		this.backward = this.keys[KeyEvent.VK_S];
		this.up = this.keys[KeyEvent.VK_SPACE];
		this.down = this.keys[KeyEvent.VK_SHIFT];
		
		this.kup = this.keys[KeyEvent.VK_UP];
		this.kdown = this.keys[KeyEvent.VK_DOWN];
		this.kleft = this.keys[KeyEvent.VK_LEFT];
		this.kright = this.keys[KeyEvent.VK_RIGHT];
		this.krleft = this.keys[KeyEvent.VK_Q];
		this.krright = this.keys[KeyEvent.VK_E];
	}
	
	public boolean getUp() {
		return this.up;
	}
	
	public boolean getDown() {
		return this.down;
	}

	public boolean getRight() {
		return this.right;
	}
	
	public boolean getLeft() {
		return this.left;
	}
	
	public boolean getForward() {
		return this.forward;
	}

	public boolean getBackward() {
		return this.backward;
	}
	
	public boolean getKUp() {
		return this.kup;
	}
	
	public boolean getKDown() {
		return this.kdown;
	}

	public boolean getKRight() {
		return this.kright;
	}
	
	public boolean getKLeft() {
		return this.kleft;
	}
	
	public boolean getKRRight() {
		return this.krright;
	}
	
	public boolean getKRLeft() {
		return this.krleft;
	}
	
	public boolean getAnyKey(int key) {
		return this.keys[key];
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
		
	}

}
