package engine.camera;

import java.awt.event.KeyEvent;

import engine.input.Keyboard;
import engine.quaternion.Quaternion;
import engine.triangle.Triangle;
import engine.vector.Vector3;

public class Camera {
	
	public Vector3 pos;
	public Quaternion rot;
	public double viewDistance;
	public double moveSpeed = 0.2;
	public double rotSpeed = Math.PI/64;
	public Vector3 clippingPlane;
	
	public Camera(Vector3 pos, double viewDistance) {
		this.pos = pos;
		this.rot = new Quaternion();
		this.viewDistance = viewDistance;
		this.clippingPlane = new Vector3(0, 0, 0.1);
	}
	
	public Camera(double x, double y, double z, double viewDistance) {
		this.pos = new Vector3(x, y, z);
		this.rot = new Quaternion();
		this.viewDistance = viewDistance;
		this.clippingPlane = new Vector3(0, 0, 0.1);
	}
	
	public void translate(Vector3 vec) {
		this.pos.x += vec.x;
		this.pos.y += vec.y;
		this.pos.z += vec.z;
	}
	
	public void rotate(Vector3 axis, double angle) {
		this.rot = Quaternion.rotate(this.rot, axis, angle);
	}
	
	public void keyboard(Keyboard keyb, double deltaTime) {
		Vector3 vUp = this.rot.getUpVector();
		Vector3 vForward = this.rot.getForwardVector();
		Vector3 vRight = this.rot.getRightVector();
		
		if (keyb.getUp() == true) {
			this.translate(Vector3.multiply(vUp, this.moveSpeed));
		}
		
		if (keyb.getDown() == true) {
			this.translate(Vector3.multiply(vUp, -this.moveSpeed));
		}
		
		if (keyb.getRight() == true) {
			this.translate(Vector3.multiply(vRight, -this.moveSpeed));
		}
		
		if (keyb.getLeft() == true) {
			this.translate(Vector3.multiply(vRight, this.moveSpeed));
		}

		if (keyb.getForward() == true) {
			this.translate(Vector3.multiply(vForward, this.moveSpeed));
		}
		
		if (keyb.getBackward() == true) {
			this.translate(Vector3.multiply(vForward, -this.moveSpeed));
		}
		
		if (keyb.getKUp() == true) {
			this.rotate(vRight, -rotSpeed);
		}
		
		if (keyb.getKDown() == true) {
			this.rotate(vRight, rotSpeed);
		}
		
		if (keyb.getKRight() == true) {
			this.rotate(vUp, -rotSpeed);
		}
		
		if (keyb.getKLeft() == true) {
			this.rotate(vUp, rotSpeed);                    
		}
		
		if (keyb.getKRRight() == true) {
			this.rotate(vForward, rotSpeed);
		}
		
		if (keyb.getKRLeft() == true) {
			this.rotate(vForward, -rotSpeed);                    
		}
		
		if (keyb.getAnyKey(KeyEvent.VK_X)) {
			this.pos = new Vector3(0, 5, -5);
			this.rot = new Quaternion();
		}
		
		if (keyb.getAnyKey(KeyEvent.VK_O)) {
			Triangle.doGouraud = false;
		}
		
		if (keyb.getAnyKey(KeyEvent.VK_L)) {
			Triangle.doGouraud = true;
		}
	}
}
