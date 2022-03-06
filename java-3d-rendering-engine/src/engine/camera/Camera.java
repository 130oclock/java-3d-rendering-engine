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
}
