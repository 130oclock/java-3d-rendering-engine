package engine.camera;

import engine.vector.Vector3d;

public class Camera {
	
	public Vector3d pos;
	public double viewDistance;
	public double moveSpeed;
	
	public Camera(Vector3d pos, double viewDistance) {
		this.pos = pos;
		this.viewDistance = viewDistance;
		this.moveSpeed = 10;
	}
}
