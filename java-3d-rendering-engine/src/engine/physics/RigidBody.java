package engine.physics;

import engine.quaternion.Quaternion;
import engine.vector.Vector3;

public class RigidBody {
	private Vector3 pos, vel, acc;
	private Quaternion rot, angl, torque;
	private double density = 1;
	private double mass = 1;
	
	public RigidBody(Vector3 pos, Quaternion rot) {
		this.pos = pos.copy();
		this.rot = rot.copy();
		
		this.vel = new Vector3();
		this.acc = new Vector3();
	}
	
	public void update(double deltaTime) {
		this.vel.x += this.acc.x * deltaTime;
		this.vel.y += this.acc.y * deltaTime;
		this.vel.z += this.acc.z * deltaTime;
		
		this.pos.x += this.vel.x * deltaTime;
		this.pos.y += this.vel.y * deltaTime;
		this.pos.z += this.vel.z * deltaTime;
		
		if (this.pos.y < 0) this.pos.y = 0;
		//this.rot = Quaternion.rotate(this.rot, Vector3d.normalize(new Vector3d(1, 1, 1)), -0.05 * deltaTime);
	}
	
	public void addVel(double x, double y, double z) {
		this.vel.x += x;
		this.vel.y += y;
		this.vel.z += z;
	}
	
	public void setVel(double x, double y, double z) {
		this.vel.x = x;
		this.vel.y = y;
		this.vel.z = z;
	}
	
	public void addAcc(double x, double y, double z) {
		this.acc.x += x;
		this.acc.y += y;
		this.acc.z += z;
	}
	
	public void setAcc(double x, double y, double z) {
		this.acc.x = x;
		this.acc.y = y;
		this.acc.z = z;
	}
	
	public Vector3 getPos() {
		return this.pos;
	}
	
	public Quaternion getRot() {
		return this.rot;
	}
}
