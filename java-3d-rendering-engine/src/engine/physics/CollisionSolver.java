package engine.physics;

import engine.matrix.Mat4x4;
import engine.quaternion.Quaternion;
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
		
		// temporary midpoint of both objects where they collided
		Vector3 posDif = Vector3.divide(Vector3.subtract(a.getPos(), b.getPos()), 2);
		
		double amass = a.getMass();
		double bmass = b.getMass();

		// coefficient of restitution
		//double e = Math.min(a.getRestitution(), b.getRestitution());
		double e = a.getRestitution() * b.getRestitution();
		
		// find the points where the objects are colliding relative to the object's center of mass
		Vector3 ra = new Vector3(posDif.x, posDif.y, posDif.z); // the point on A where the collision occurred
		Vector3 rb = new Vector3(-posDif.x, -posDif.y, -posDif.z); // the point on B where the collision occurred

		// find the angular velocity at each point on each object
		Vector3 aAngVel = a.getAngularVelocityAtPoint(ra); // this is the direction and speed of the point of collision
		Vector3 bAngVel = b.getAngularVelocityAtPoint(rb);
		
		double raN = Vector3.dotProduct(ra, normal);
		double rbN = Vector3.dotProduct(rb, normal);
		
		// find inertia tensors
		double Ia = a.getInverseInertia();
		double Ib = b.getInverseInertia();
		//Mat4x4 Ia = Mat4x4.quickInverse(Mat4x4.inertiaTensor(amass, Vector3.crossProduct(aAngVel, ra)));
		//Mat4x4 Ib = Mat4x4.quickInverse(Mat4x4.inertiaTensor(bmass, Vector3.crossProduct(bAngVel, rb)));

		// relative velocity of both linear and angular velocities
		//double relativeVelocity = Vector3.dotProduct(Vector3.subtract(a.getVelocity(), b.getVelocity()), normal);
		double relativeVelocity = Vector3.dotProduct(Vector3.subtract(a.getVelocity(), b.getVelocity()), normal) + Vector3.dotProduct(aAngVel, normal) - Vector3.dotProduct(bAngVel, normal);

		//System.out.println(Ia.toString());
		//System.out.println(Ib.toString());
		//System.out.println(relativeVelocity);

		// the impulse magnitude
		//double j = (-(1 + e) * relativeVelocity) / (Vector3.dotProduct(normal, normal) * ((1 / amass) + (1 / bmass)));
		//double j = (-(1 + e) * relativeVelocity) / (((1 / amass) + (1 / bmass)) + (Vector3.dotProduct(Vector3.mutiplyMatrixVector(Ia, raN), Vector3.mutiplyMatrixVector(Ia, raN)) + Vector3.dotProduct(Vector3.mutiplyMatrixVector(Ib, rbN), Vector3.mutiplyMatrixVector(Ib, rbN))));
		double j = (-(1 + e) * relativeVelocity) / (((1 / amass) + (1 / bmass)) + (raN * raN * Ia) + (rbN * rbN * Ib));
		
		if (!aStatic && !bStatic) {
			a.addPos(-intersection.x * 0.5, -intersection.y * 0.5, -intersection.z * 0.5);
			b.addPos(intersection.x * 0.5, intersection.y * 0.5, intersection.z * 0.5);
			
			a.addVel(Vector3.multiply(normal, j / amass)); // v2 = v1 + (j/M)*n
			b.addVel(Vector3.multiply(normal, -j / bmass));
			
			a.addTorque(Vector3.crossProduct(ra, Vector3.multiply(normal, j / amass))); // w2 = w1 + (r * jn) / I
			b.addTorque(Vector3.crossProduct(rb, Vector3.multiply(normal, -j / bmass)));
		}
		if (!aStatic && bStatic) { // b is static
			a.addPos(-intersection.x, -intersection.y, -intersection.z);
			a.addVel(Vector3.multiply(normal, j / amass));
			
			a.addTorque(Vector3.crossProduct(ra, Vector3.multiply(normal, j / amass)));
		}
		if (aStatic && !bStatic) { // a is static
			b.addPos(intersection.x, intersection.y, intersection.z);
			b.addVel(Vector3.multiply(normal, -j / bmass));
			
			b.addTorque(Vector3.crossProduct(rb, Vector3.multiply(normal, -j / bmass)));
		}
	}
	
	public static boolean intersectAABB(RigidBody a, RigidBody b) {
		Vector3 difference = Vector3.subtract(b.getPos(), a.getPos()); // a is always at 0,0,0
		Vector3 aMax = a.collider.getMax();
		Vector3 aMin = a.collider.getMin();
		Vector3 bMax = Vector3.add(b.collider.getMax(), difference);
		Vector3 bMin = Vector3.add(b.collider.getMin(), difference);
		
		boolean check = (aMin.x <= bMax.x && aMax.x >= bMin.x) && // check overlap on x axis
						(aMin.y <= bMax.y && aMax.y >= bMin.y) && // check overlap on y axis
						(aMin.z <= bMax.z && aMax.z >= bMin.z);   // check overlap on z axis
		
		return check;
	}
	
	private static boolean projectSATTest(Vector3[] aVerts, Vector3[] bVerts, Vector3 axis) {
		if (axis.x == 0 && axis.y == 0 && axis.z == 0) // checks that the axis is not (0, 0, 0)
			return false; 
		
		double aMin = Double.MAX_VALUE, aMax = -Double.MAX_VALUE, bMin = Double.MAX_VALUE, bMax = -Double.MAX_VALUE;
		
		for(int i = 0; i < 8; i++) {
			double aDist = Vector3.dotProduct(aVerts[i], axis);
			aMin = (aDist < aMin) ? aDist : aMin;
			aMax = (aDist > aMax) ? aDist : aMax;
			double bDist = Vector3.dotProduct(bVerts[i], axis);
			bMin = (bDist < bMin) ? bDist : bMin;
			bMax = (bDist > bMax) ? bDist : bMax;
		}
		
		// One-dimensional intersection test between a and b
		double longSpan = Math.max(aMax, bMax) - Math.min(aMin, bMin);
		double sumSpan = aMax - aMin + bMax - bMin;
		boolean check = longSpan > sumSpan;
		
		if (!check) {
			if (aMin > bMax) {
				double mod = (bMax - aMin);
				axis.x *= mod;
				axis.y *= mod;
				axis.z *= mod;
			} else {
				double mod = (aMax - bMin);
				axis.x *= mod;
				axis.y *= mod;
				axis.z *= mod;
			}
		}
		
		return check;
	}
	
	public static boolean intersectOBB(RigidBody a, RigidBody b, Vector3 penetration) {
		Vector3[] aPoints = a.collider.getAdjustedPoints(a.getPos(), a.getRot());
		Vector3[] aNorms = a.collider.getAdjustedNormals(a.getRot());
		double aLength = aNorms.length;
		
		Vector3[] bPoints = b.collider.getAdjustedPoints(b.getPos(), b.getRot());
		Vector3[] bNorms = b.collider.getAdjustedNormals(b.getRot());
		double bLength = aNorms.length;
		
		Vector3 direction = new Vector3();
		double magnitude = Double.MAX_VALUE;
		double tolerance = 0;
		
		boolean result = true;
		for (int i = 0; i < aLength; i++) {
			Vector3 axis = aNorms[i].copy();
			if (projectSATTest(aPoints, bPoints, axis)) {
				result = false;
				break;
			}
			double length = Vector3.length(axis);
			if (length > tolerance && length < magnitude) {
				magnitude = length;
				direction = Vector3.normalize(axis);
			}
		}
		
		for (int i = 0; i < bLength; i++) {
			Vector3 axis = bNorms[i].copy();
			if (projectSATTest(aPoints, bPoints, axis)) {
				result = false;
				break;
			}
			double length = Vector3.length(axis);
			if (length > tolerance && length < magnitude) {
				magnitude = length;
				direction = Vector3.normalize(axis);
			}
		}
		
		for (int i = 0; i < aLength; i++) {
			for (int j = 0; j < bLength; j++) {
				Vector3 axis = Vector3.crossProduct(aNorms[i], bNorms[j]);
				if (projectSATTest(aPoints, bPoints, axis)) {
					result = false;
					break;
				}
				double length = Vector3.length(axis);
				if (length > tolerance && length != 0 && length < magnitude) {
					magnitude = length;
					direction = Vector3.normalize(axis);
				}
			}
			
		}

		if (result) {
			penetration.x = direction.x * magnitude;
			penetration.y = direction.y * magnitude;
			penetration.z = direction.z * magnitude;
			//System.out.println(penetration.toString());
		}
		
		return result;
	}
}
