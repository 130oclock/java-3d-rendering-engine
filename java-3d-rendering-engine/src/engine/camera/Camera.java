package engine.camera;

import java.awt.event.KeyEvent;

import engine.input.Keyboard;
import engine.quaternion.Quaternion;
import engine.triangle.Triangle;
import engine.vector.Vector3d;

public class Camera {
	
	public Vector3d pos;
	public Quaternion rot;
	public double viewDistance;
	public double moveSpeed = 0.2;
	public double rotSpeed = Math.PI/64;
	public Vector3d clippingPlane;
	
	public Camera(Vector3d pos, double viewDistance) {
		this.pos = pos;
		this.rot = Quaternion.empty();
		this.viewDistance = viewDistance;
		this.clippingPlane = new Vector3d(0, 0, 0.1);
	}
	
	public Camera(double x, double y, double z, double viewDistance) {
		this.pos = new Vector3d(x, y, z);
		this.rot = Quaternion.empty();
		this.viewDistance = viewDistance;
		this.clippingPlane = new Vector3d(0, 0, 0.1);
	}
	
	public void translate(Vector3d vec) {
		this.pos.x += vec.x;
		this.pos.y += vec.y;
		this.pos.z += vec.z;
	}
	
	public void rotate(Vector3d axis, double angle) {
		this.rot = Quaternion.rotate(this.rot, axis, angle);
	}
	
	public void keyboard(Keyboard keyb, double deltaTime) {
		Vector3d vUp = this.rot.getUpVector();
		Vector3d vForward = this.rot.getForwardVector();
		Vector3d vRight = this.rot.getRightVector();
		
		if (keyb.getUp() == true) {
			this.translate(Vector3d.multiply(vUp, this.moveSpeed));
		}
		
		if (keyb.getDown() == true) {
			this.translate(Vector3d.multiply(vUp, -this.moveSpeed));
		}
		
		if (keyb.getRight() == true) {
			this.translate(Vector3d.multiply(vRight, -this.moveSpeed));
		}
		
		if (keyb.getLeft() == true) {
			this.translate(Vector3d.multiply(vRight, this.moveSpeed));
		}

		if (keyb.getForward() == true) {
			this.translate(Vector3d.multiply(vForward, this.moveSpeed));
		}
		
		if (keyb.getBackward() == true) {
			this.translate(Vector3d.multiply(vForward, -this.moveSpeed));
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
			this.pos = new Vector3d(0, 0, -5);
			this.rot = Quaternion.empty();
		}
		
		if (keyb.getAnyKey(KeyEvent.VK_O)) {
			Triangle.doGouraud = false;
		}
		
		if (keyb.getAnyKey(KeyEvent.VK_L)) {
			Triangle.doGouraud = true;
		}
	}
}
