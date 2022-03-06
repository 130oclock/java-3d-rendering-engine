package engine.camera;

import engine.input.Keyboard;
import engine.quaternion.Quaternion;
import engine.vector.Vector3d;

public class Camera {
	
	public Vector3d pos;
	public Quaternion rot;
	public double viewDistance;
	public double moveSpeed;
	
	public Camera(Vector3d pos, double viewDistance) {
		this.pos = pos;
		this.rot = Quaternion.empty();
		this.viewDistance = viewDistance;
		this.moveSpeed = 0.2;
	}
	
	public void translate(Vector3d vec) {
		this.pos.x += vec.x;
		this.pos.y += vec.y;
		this.pos.z += vec.z;
	}
	
	public void rotate(Vector3d axis, double angle) {
		this.rot = Quaternion.rotate(this.rot, axis, angle);
	}
	
	public void keyboard(Keyboard keyb) {
		Vector3d vUp = this.rot.getUpVector();
		Vector3d vForward = this.rot.getForwardVector();
		Vector3d vRight = this.rot.getRightVector();
		
		if (keyb.getUp() == true) {
			this.translate(Vector3d.multiply(vUp, -this.moveSpeed));
		}
		
		if (keyb.getDown() == true) {
			this.translate(Vector3d.multiply(vUp, this.moveSpeed));
		}
		
		if (keyb.getRight() == true) {
			this.translate(Vector3d.multiply(vRight, this.moveSpeed));
		}
		
		if (keyb.getLeft() == true) {
			this.translate(Vector3d.multiply(vRight, -this.moveSpeed));
		}

		if (keyb.getForward() == true) {
			this.translate(Vector3d.multiply(vForward, -this.moveSpeed));
		}
		
		if (keyb.getBackward() == true) {
			this.translate(Vector3d.multiply(vForward, this.moveSpeed));
		}
		
		if (keyb.getKUp() == true) {
			this.rotate(vRight, -0.04);
		}
		
		if (keyb.getKDown() == true) {
			this.rotate(vRight, 0.04);
		}
		
		if (keyb.getKRight() == true) {
			this.rotate(vUp, -0.04);
		}
		
		if (keyb.getKLeft() == true) {
			this.rotate(vUp, 0.04);                    
		}
		
		if (keyb.getKRRight() == true) {
			this.rotate(vForward, 0.04);
		}
		
		if (keyb.getKRLeft() == true) {
			this.rotate(vForward, -0.04);                    
		}
	}
}
