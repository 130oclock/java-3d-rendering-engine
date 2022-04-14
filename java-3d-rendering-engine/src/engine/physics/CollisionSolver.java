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
		
		// rotation and translation of each object
		Mat4x4 aMatWorld = Mat4x4.generateMatrix(a.getRot(), a.getPos(), null);
		Mat4x4 bMatWorld = Mat4x4.generateMatrix(b.getRot(), b.getPos(), null);
		
		double amass = a.getMass();
		double bmass = b.getMass();

		// coefficient of restitution
		//double e = Math.min(a.getRestitution(), b.getRestitution());
		double e = a.getRestitution() * b.getRestitution();
		
		// find the points where the objects are colliding
		Vector3 aAngVel = a.getAngularVelocityAtPoint(Vector3.multiply(this.intersection, 0.5), dt);
		Vector3 bAngVel = b.getAngularVelocityAtPoint(Vector3.multiply(this.intersection, -0.5), dt);
		// find inertia tensor for object a
		Vector3 ra = intersection;
		Vector3 pa = Vector3.mutiplyMatrixVector(aMatWorld, ra);
		Vector3 raN = Vector3.crossProduct(ra, normal);
		Mat4x4 Ia = Mat4x4.quickInverse(Mat4x4.inertiaTensor(amass, ra));
		// find inertia tensor for object b
		Vector3 rb = Vector3.multiply(intersection, -1);
		Vector3 pb = Vector3.mutiplyMatrixVector(bMatWorld, rb);
		Vector3 rbN = Vector3.crossProduct(rb, normal);
		Mat4x4 Ib = Mat4x4.quickInverse(Mat4x4.inertiaTensor(bmass, rb));

		// relative velocity of both linear and angular velocities
		double relativeVelocity = Vector3.dotProduct(Vector3.subtract(a.getVelocity(), b.getVelocity()), normal) + Vector3.dotProduct(Vector3.crossProduct(normal, ra), aAngVel) - Vector3.dotProduct(Vector3.crossProduct(normal, rb), bAngVel);

		//System.out.println(relativeVelocity.x + " " + relativeVelocity.y + " " + relativeVelocity.z);
		//System.out.println(relativeVelocity);

		// the impulse magnitude
		//double j = (-(1 + e) * Vector3.dotProduct(relativeVelocity, normal)) / (Vector3.dotProduct(normal, normal) * ((1 / amass) + (1 / bmass)));
		double j = (-(1 + e) * relativeVelocity) / (/*Vector3.dotProduct(normal, normal) * */((1 / amass) + (1 / bmass)) + (Vector3.dotProduct(raN, Vector3.mutiplyMatrixVector(Ia, raN)) + Vector3.dotProduct(rbN, Vector3.mutiplyMatrixVector(Ib, rbN))));
		
		
		//System.out.println("a" + aAngVel.x + " " + aAngVel.y + " " + aAngVel.z);
		//System.out.println("b" + bAngVel.x + " " + bAngVel.y + " " + bAngVel.z);
		//double jrot = 
		
		if (!aStatic && !bStatic) {
			a.addPos(-this.intersection.x / 2, -this.intersection.y / 2, -this.intersection.z / 2);
			b.addPos(this.intersection.x / 2, this.intersection.y / 2, this.intersection.z / 2);
			
			a.addVel(Vector3.multiply(normal, j / amass)); // v2 = v1 + (j/M)*n
			b.addVel(Vector3.multiply(normal, -j / bmass));
			
			Vector3 aOmega = Vector3.mutiplyMatrixVector(Ia, Vector3.multiply(aAngVel, -j)); // w2 = w1 + (r * jn) / I
			double aMag = Vector3.length(aOmega);
			a.addAngVel(Quaternion.localRotation(aOmega, aMag));
			
			Vector3 bOmega = Vector3.mutiplyMatrixVector(Ib, Vector3.multiply(bAngVel, j));
			double bMag = Vector3.length(bOmega);
			b.addAngVel(Quaternion.localRotation(bOmega, bMag));
		}
		if (!aStatic && bStatic) { // b is static
			a.addPos(-this.intersection.x, -this.intersection.y, -this.intersection.z);
			a.addVel(Vector3.multiply(normal, j / amass));
			
			/*Vector3 aOmega = Vector3.mutiplyMatrixVector(Ia, Vector3.multiply(aAngVel, j));
			double aMag = Vector3.length(aOmega);
			a.addAngVel(Quaternion.localRotation(aOmega, aMag));*/
		}
		if (aStatic && !bStatic) { // a is static
			b.addPos(this.intersection.x, this.intersection.y, this.intersection.z);
			b.addVel(Vector3.multiply(normal, -j / bmass));
			
			/*Vector3 bOmega = Vector3.mutiplyMatrixVector(Ib, Vector3.multiply(bAngVel, -j));
			double bMag = Vector3.length(bOmega);
			a.addAngVel(Quaternion.localRotation(bOmega, bMag));*/
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
		
		/*if (check) {
			double magnitude = 1000;
			double direction = 1;
			int closestId = -1;
			
			double[] distances = new double[] { aMin.x - bMax.x, aMax.x - bMin.x, aMin.y - bMax.y, aMax.y - bMin.y, aMin.z - bMax.z, aMax.z - bMin.z };
			for (int i = 0; i < 6; i++) {
				double d = Math.abs(distances[i]);
				if (d <= magnitude) {
					magnitude = d;
					closestId = i;
					direction = distances[i];
				}
			}
			
			direction /= Math.abs(direction);
			
			if (closestId == 0 || closestId == 1) axis.x = magnitude * direction;
			if (closestId == 2 || closestId == 3) axis.y = magnitude * direction;
			if (closestId == 4 || closestId == 5) axis.z = magnitude * direction;
		}
		//System.out.println(axis.x + " " + axis.y + " " + axis.z);*/
		
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
		
		boolean result = true;
		for (int i = 0; i < aLength; i++) {
			Vector3 axis = aNorms[i].copy();
			if (projectSATTest(aPoints, bPoints, axis)) {
				result = false;
				break;
			}
			double length = Vector3.length(axis);
			if (length < magnitude) {
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
			if (length < magnitude) {
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
				if (length != 0 && length < magnitude) {
					magnitude = length;
					direction = Vector3.normalize(axis);
				}
			}
			
		}

		if (result) {
			penetration.x = direction.x * magnitude;
			penetration.y = direction.y * magnitude;
			penetration.z = direction.z * magnitude;
			//System.out.println(penetration.x + " " + penetration.y + " " + penetration.z);
		}
		
		return result;
	}
}
