package engine.camera;

import java.awt.event.KeyEvent;

import engine.graphics.triangle.Triangle;
import engine.input.Keyboard;
import engine.input.MouseInput;
import engine.input.UserInput;
import engine.quaternion.Quaternion;
import engine.vector.Vector3;

public class Camera {
	
	public Vector3 pos;
	private Vector3 startPos;
	public Quaternion rot;
	public double viewDistance;
	public double moveSpeed = 0.2;
	public double rotSpeed = Math.PI/64;
	public Vector3 clippingPlane;
	private int WIDTH = 0;
	private int HEIGHT = 0;
	private int SCALE = 1;
	
	public Camera(Vector3 pos, double viewDistance, int WIDTH, int HEIGHT, int SCALE) {
		this.pos = pos;
		this.startPos = pos.copy();
		this.rot = new Quaternion();
		this.viewDistance = viewDistance;
		this.clippingPlane = new Vector3(0, 0, 0.1);
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		this.SCALE = SCALE;
	}
	
	public Camera(double x, double y, double z, double viewDistance, int WIDTH, int HEIGHT, int SCALE) {
		this(new Vector3(x, y, z), viewDistance, WIDTH, HEIGHT, SCALE);
	}
	
	public void translate(Vector3 vec) { // move the camera
		this.pos.x += vec.x;
		this.pos.y += vec.y;
		this.pos.z += vec.z;
	}
	
	public void rotate(Vector3 axis, double angle) { // rotate the camera
		this.rot = Quaternion.rotate(this.rot, axis, angle);
	}
	
	public void lookAt(Vector3 target, double dt) {
		Quaternion rot = Quaternion.lookAt(pos, target);
		this.rot = Quaternion.slerp(this.rot, rot, dt);
	}
	
	public void input(UserInput input, double deltaTime) { // update the camera based on the current inputs
		Keyboard keyb = input.keyboard;
		MouseInput mouse = input.mouse;
		
		Vector3 vUp = this.rot.getUpVector();
		Vector3 vForward = this.rot.getForwardVector();
		Vector3 vRight = this.rot.getRightVector();
		
		if (keyb.getUp() == true) { // Space
			this.translate(Vector3.multiply(vUp, this.moveSpeed));
		}
		
		if (keyb.getDown() == true) { // Shift
			this.translate(Vector3.multiply(vUp, -this.moveSpeed));
		}
		
		if (keyb.getRight() == true) { // D
			this.translate(Vector3.multiply(vRight, this.moveSpeed));
		}
		
		if (keyb.getLeft() == true) { // A
			this.translate(Vector3.multiply(vRight, -this.moveSpeed));
		}

		if (keyb.getForward() == true) { // W
			this.translate(Vector3.multiply(vForward, this.moveSpeed));
		}
		
		if (keyb.getBackward() == true) { // S
			this.translate(Vector3.multiply(vForward, -this.moveSpeed));
		}
		
		if (keyb.getKUp() == true) { // Key_up
			this.rotate(vRight, -rotSpeed);
		}
		
		if (keyb.getKDown() == true) { // Key_down
			this.rotate(vRight, rotSpeed);
		}
		
		if (keyb.getKRight() == true) { // Key_right
			this.rotate(vUp, rotSpeed);
		}
		
		if (keyb.getKLeft() == true) { // Key_left
			this.rotate(vUp, -rotSpeed);                    
		}
		
		/*double change = (double) mouse.getChangeX() / WIDTH;
		this.rotate(vUp, -change);*/
		
		if (keyb.getKRRight() == true) { // E
			this.rotate(vForward, -rotSpeed);
		}
		
		if (keyb.getKRLeft() == true) { // Q
			this.rotate(vForward, rotSpeed);                    
		}
		
		if (keyb.getAnyKey(KeyEvent.VK_X)) { // X, reset position
			this.pos.x = this.startPos.x;
			this.pos.y = this.startPos.y;
			this.pos.z = this.startPos.z;
			this.rot = new Quaternion();
		}
		
		if (keyb.getAnyKey(KeyEvent.VK_O)) { // O, change lighting mode
			Triangle.doGouraud = false;
		}
		
		if (keyb.getAnyKey(KeyEvent.VK_L)) { // L, change lighting mode
			Triangle.doGouraud = true;
		}
	}
}
