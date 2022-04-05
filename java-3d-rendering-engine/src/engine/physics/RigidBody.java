package engine.physics;

import engine.quaternion.Quaternion;
import engine.vector.Vector3;

public class RigidBody {
	private Vector3 pos, vel, force;
	private Quaternion rot, angl, torque;
	
	private double density = 1;
	private double mass = 1;
	// coefficient of restitution, the dampening of collision
	private double restitution = 1;
	
	private boolean useGravity = true;
	public boolean isStatic = false;
	
	Collider collider;
	
	public RigidBody(Vector3 pos, Quaternion rot, double mass, Vector3 boundingBox) {
		this.pos = pos.copy();
		this.rot = rot.copy();
		
		this.vel = new Vector3();
		this.force = new Vector3();
		
		this.mass = mass;
		this.collider = new Collider(boundingBox);
		
		PhysicsWorld.addObject(this);
	}
	
	public void setStatic() {
		this.isStatic = true;
		restitution = 0;
	}
	
	public Vector3 getPos() {
		return this.pos;
	}
	
	public Quaternion getRot() {
		return this.rot;
	}
	
	public double getMass() {
		return this.mass;
	}
	
	public double getRestitution() {
		return this.restitution;
	}
	
	public Vector3 getVelocity() {
		return this.vel;
	}
	
	public void update(double dt, Vector3 gravity) { // 3d dynamics
		if (useGravity) { // constantly apply gravitational force
			force.x += mass * gravity.x;
			force.y += mass * gravity.y;
			force.z += mass * gravity.z;
		}
		
		vel.x += force.x / mass * dt; // F/m = a
		vel.y += force.y / mass * dt;
		vel.z += force.z / mass * dt;
		
		pos.x += vel.x * dt;
		pos.y += vel.y * dt;
		pos.z += vel.z * dt;
		
		if (this.pos.y < -1000) this.pos.y = -1000;

		force = new Vector3();
	}
	
	public void addVel(double x, double y, double z) {
		this.vel.x += x;
		this.vel.y += y;
		this.vel.z += z;
	}
	
	public void addVel(Vector3 vel) {
		this.vel.x += vel.x;
		this.vel.y += vel.y;
		this.vel.z += vel.z;
	}
	
	public void setVel(double x, double y, double z) {
		this.vel.x = x;
		this.vel.y = y;
		this.vel.z = z;
	}
	
	public void addForce(double x, double y, double z) {
		this.force.x += x;
		this.force.y += y;
		this.force.z += z;
	}
	
	public void setForce(double x, double y, double z) {
		this.force.x = x;
		this.force.y = y;
		this.force.z = z;
	}
	
	public void addPos(double x, double y, double z) {
		this.pos.x += x;
		this.pos.y += y;
		this.pos.z += z;
	}
	
	public void setPos(double x, double y, double z) {
		this.pos.x = x;
		this.pos.y = y;
		this.pos.z = z;
	}
}
