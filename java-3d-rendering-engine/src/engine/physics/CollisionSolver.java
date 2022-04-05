package engine.physics;

import engine.vector.Vector3;

public class CollisionSolver {
	private RigidBody a, b;
	private Vector3 intersection, normal;
	
	public CollisionSolver(RigidBody a, RigidBody b, Vector3 point) {
		this.a = a;
		this.b = b;
		this.intersection = point;
		this.normal = Vector3.normalize(point);
	}
	
	public void solve(double dt) {
		boolean aStatic = a.isStatic;
		boolean bStatic = b.isStatic;
		
		double amass = a.getMass();
		double bmass = b.getMass();
		
		Vector3 relativeVelocity = Vector3.subtract(a.getVelocity(), b.getVelocity());
		
		// coefficient of restitution
		double e = Math.min(a.getRestitution(), b.getRestitution());
		// the impulse magnitude
		double j = (-(1 + e) * Vector3.dotProduct(relativeVelocity, normal)) / (Vector3.dotProduct(normal, normal) * ((1 / amass) + (1 / bmass)));
		
		if (!aStatic && !bStatic) {
			a.addPos(this.intersection.x / 2, this.intersection.y / 2, this.intersection.z / 2);
			b.addPos(-this.intersection.x / 2, -this.intersection.y / 2, -this.intersection.z / 2);
			a.addVel(Vector3.multiply(normal, j / amass));
			b.addVel(Vector3.multiply(normal, j / -bmass));
		}
		if (!aStatic && bStatic) { // b is a static object
			a.addPos(this.intersection.x, this.intersection.y, this.intersection.z);
			//b.addPos(-this.intersection.x / 2, -this.intersection.y / 2, -this.intersection.z / 2);
			a.addVel(Vector3.multiply(normal, j / amass));
			b.addVel(Vector3.multiply(normal, j / -bmass));
		}
		if (aStatic && !bStatic) { // a is a static object
			//a.addPos(this.intersection.x / 2, this.intersection.y / 2, this.intersection.z / 2);
			b.addPos(-this.intersection.x, -this.intersection.y, -this.intersection.z);
			a.addVel(Vector3.multiply(normal, j / amass));
			b.addVel(Vector3.multiply(normal, j / -bmass));
		}
	}
}
