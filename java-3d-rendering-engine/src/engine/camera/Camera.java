package engine.camera;

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
		this.moveSpeed = 10;
	}
	
	public void translate(double x, double y, double z) {
		this.pos.x += x;
		this.pos.y += y;
		this.pos.z += z;
	}
	
	public void rotate(Vector3d axis, double angle) {
		this.rot = Quaternion.rotate(this.rot, axis, angle);
	}
}
