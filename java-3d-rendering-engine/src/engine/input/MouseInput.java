package engine.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseInput implements MouseListener, MouseMotionListener {
	private int x = -1, y = -1, b = -1, previousx = 0, previousy = 0;
	
	public MouseInput() {
	}

	public int getX() {
		return x;
	}
	
	public int getChangeX() {
		return x - previousx;
	}
	
	public int getY() {
		return y;
	}
	
	public int getChangeY() {
		return y - previousy;
	}
	
	public int getB() {
		return b;
	}
	
	public void update() {
		previousx = x;
		previousy = y;
	}
	
	// MouseListener

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		b = e.getButton();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		b = -1;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		previousx = x;
		previousy = y;
	}

	// MouseMotionListener
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		x = e.getX();
		y = e.getY();
	}
}
