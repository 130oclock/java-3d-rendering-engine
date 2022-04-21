package engine.physics;

import engine.matrix.Mat4x4;
import engine.quaternion.Quaternion;
import engine.vector.Vector3;

public class RigidBody {
	private Vector3 position, velocity, force, angularVelocity, torque;
	private Quaternion rotation;
	private Mat4x4 inertiaTensor;
	private double inverseInertia = 1 / ((0.16666) * 2 * 2 * 1);
	
	private double density = 1;
	private double mass = 1;
	// coefficient of restitution, the dampening of collision
	private double restitution = 0.99;
	
	private boolean useGravity = true;
	public boolean isStatic = false;
	
	Collider collider;
	
	public RigidBody(Vector3 pos, Quaternion rot, double mass, Collider boundingBox) {
		// linear motion
		this.position = pos.copy();
		
		this.velocity = new Vector3();
		this.force = new Vector3();

		// rotation
		this.rotation = rot.copy();
		
		this.angularVelocity = new Vector3(0, 0, 0); // direction is the axis of rotation and length is speed in radians
		this.torque = new Vector3(0, 0, 0);
		
		this.mass = mass;
		this.collider = boundingBox;
		
		PhysicsWorld.addObject(this);
	}
	
	public void setStatic() {
		this.isStatic = true;
		restitution = 0;
	}
	
	// Find p(t) = | w(t) x r(t) |
	public Vector3 getAngularVelocityAtPoint(Vector3 displacementOfPoint) {
		//Vector3 angularVelocity = new Vector3(); 
		// find the axis the object is rotating around and the angle it is rotating in radians
		//double angle = Quaternion.toAxisAngle(rotation, angularVelocity);

		//angularVelocity = Vector3.multiply(angularVelocity, angle);
		// angular velocity is the cross product of the rotations axis and the point's position from the center of mass
		return Vector3.crossProduct(angularVelocity, displacementOfPoint);
	}
	
	public void update(double dt, Vector3 gravity) { // 3d dynamics
		if (useGravity) { // constantly apply gravitational force
			force.x += mass * gravity.x;
			force.y += mass * gravity.y;
			force.z += mass * gravity.z;
		}
		
		// derive velocity from force
		velocity.x += force.x / mass * dt; // F/m = a
		velocity.y += force.y / mass * dt;
		velocity.z += force.z / mass * dt;
		// derive position from velocity
		position.x += velocity.x * dt;
		position.y += velocity.y * dt;
		position.z += velocity.z * dt;
		// prevent the position from going out of bounds
		if (this.position.y < -1000) this.position.y = -1000;
		
		// derive angular velocity from torque
		angularVelocity.x += torque.x * inverseInertia;
		angularVelocity.y += torque.y * inverseInertia;
		angularVelocity.z += torque.z * inverseInertia;
		// derive rotation from angular velocity
		Quaternion spin = Quaternion.localRotation(angularVelocity, Vector3.length(angularVelocity) * dt * 0.5); //new Quaternion(1, angularVelocity.x * dt, angularVelocity.y * dt, angularVelocity.z * dt);//
		Quaternion nRot = Quaternion.normalize(Quaternion.multiply(rotation, spin));
		rotation.w = nRot.w;
		rotation.x = nRot.x;
		rotation.y = nRot.y;
		rotation.z = nRot.z;

		force = new Vector3();
		torque = new Vector3();
	}
	
	public Vector3 getPos() {
		return this.position;
	}
	
	public Quaternion getRot() {
		return this.rotation;
	}
	
	public double getMass() {
		return this.mass;
	}
	
	public double getRestitution() {
		return this.restitution;
	}
	
	public double getInverseInertia() {
		return this.inverseInertia;
	}
	
	public Vector3 getVelocity() {
		return this.velocity;
	}
	
	public void addVel(double x, double y, double z) {
		this.velocity.x += x;
		this.velocity.y += y;
		this.velocity.z += z;
	}
	
	public void addVel(Vector3 vel) {
		this.velocity.x += vel.x;
		this.velocity.y += vel.y;
		this.velocity.z += vel.z;
	}
	
	public void setVel(double x, double y, double z) {
		this.velocity.x = x;
		this.velocity.y = y;
		this.velocity.z = z;
	}
	
	public void addForce(double x, double y, double z) {
		this.force.x += x;
		this.force.y += y;
		this.force.z += z;
	}
	
	public void addForce(Vector3 force) {
		this.force.x += force.x;
		this.force.y += force.y;
		this.force.z += force.z;
	}
	
	public void setForce(double x, double y, double z) {
		this.force.x = x;
		this.force.y = y;
		this.force.z = z;
	}
	
	public void addPos(double x, double y, double z) {
		this.position.x += x;
		this.position.y += y;
		this.position.z += z;
	}
	
	public void addPos(Vector3 pos) {
		this.position.x += pos.x;
		this.position.y += pos.y;
		this.position.z += pos.z;
	}
	
	public void setPos(double x, double y, double z) {
		this.position.x = x;
		this.position.y = y;
		this.position.z = z;
	}
	
	public void addAngVel(Vector3 axis, double angle) {
		Vector3 nVel = Vector3.multiply(axis, angle);
		angularVelocity.x += nVel.x;
		angularVelocity.y += nVel.y;
		angularVelocity.z += nVel.z;
	}
	
	public void addAngVel(Vector3 nVel) {
		angularVelocity.x += nVel.x;
		angularVelocity.y += nVel.y;
		angularVelocity.z += nVel.z;
	}
	
	public void addTorque(Vector3 force) {
		this.torque.x += force.x;
		this.torque.y += force.y;
		this.torque.z += force.z;
	}
}
