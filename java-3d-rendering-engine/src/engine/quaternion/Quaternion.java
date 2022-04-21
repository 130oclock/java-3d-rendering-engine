package engine.quaternion;

import engine.matrix.Mat4x4;
import engine.vector.Vector3;

public class Quaternion {
	
	public double w, x, y, z;
	
	public Quaternion(double w, double x, double y, double z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Quaternion() {
		this(1, 0, 0, 0);
	}
	
	// Gets the forward vector from the Quaternion
	public Vector3 getForwardVector() {
		double x = 2 * ((this.x * this.z) + (this.w * this.y));
		double y = 2 * ((this.y * this.z) - (this.w * this.x));
		double z = 1 - (2 * ((this.x * this.x) + (this.y * this.y)));
		return new Vector3(x, y, z);
	}

	// Gets the up vector from the Quaternion
	public Vector3 getUpVector() {
		double x = 2 * ((this.x * this.y) - (this.w * this.z));
		double y = 1 - (2 * ((this.x * this.x) + (this.z * this.z)));
		double z = 2 * ((this.y * this.z) + (this.w * this.x));
		return new Vector3(x, y, z);
	}

	// Gets the right vector from the Quaternion
	public Vector3 getRightVector() {
		double x = 1 - (2 * ((this.y * this.y) + (this.z * this.z)));
		double y = 2 * ((this.x * this.y) + (this.w * this.z));
		double z = 2 * ((this.x * this.z) - (this.w * this.y));
		return new Vector3(x, y, z);
	}
	
	public Vector3 getBivector() {
		return new Vector3(x, y, z);
	}

	// Gets the axis and angle from the Quaternion
	public static double toAxisAngle(Quaternion q, Vector3 outAxis) {
		double w = q.w, x = q.x, y = q.y, z = q.z;
		
		if (w > 1) {
			double magnitude = Quaternion.magnitude(q);
			w /= magnitude;
			x /= magnitude;
			y /= magnitude;
			z /= magnitude;
		}
		
		double angle = 2 * Math.acos(w);
		double s = Math.sqrt(1 - w * w);
		if (s < 0.0001) { // avoid divide by zero
			outAxis.x = x;
			outAxis.y = y;
			outAxis.z = z;
		} else { // normalize the axis
			outAxis.x = x / s;
			outAxis.y = y / s;
			outAxis.z = z / s;
		}
		
		return angle;
	}

	// Copies the values from one Quaternion into a new Quaternion
	public Quaternion copy() {
		return new Quaternion(this.w, this.x, this.y, this.z);
	}
	
	public static double dotProduct(Quaternion q1, Quaternion q2) {
		return q1.x * q2.x + q1.y * q2.y + q1.z * q2.z + q1.w * q2.w;
	}
	
	public static double magnitude(Quaternion q1) {
		return Math.sqrt((q1.w * q1.w) + (q1.x * q1.x) + (q1.y * q1.y) + (q1.z * q1.z));
	}
	
	public static Quaternion normalize(Quaternion q1) {
		double magnitude = Quaternion.magnitude(q1);
		return new Quaternion(q1.w / magnitude, q1.x / magnitude, q1.y / magnitude, q1.z / magnitude);
	}
	
	public static Quaternion add(Quaternion q1, Quaternion q2) {
		return new Quaternion(q1.w + q2.w, q1.x + q2.x, q1.y + q2.y, q1.z + q2.z);
	}
	
	public static Quaternion subtract(Quaternion q1, Quaternion q2) {
		return new Quaternion(q1.w - q2.w, q1.x - q2.x, q1.y - q2.y, q1.z - q2.z);
	}
	
	public static Quaternion multiply(Quaternion q1, Quaternion q2) {
		double w = (q1.w * q2.w) - (q1.x * q2.x) - (q1.y * q2.y) - (q1.z * q2.z);
		double x = (q1.w * q2.x) + (q1.x * q2.w) + (q1.y * q2.z) - (q1.z * q2.y);
		double y = (q1.w * q2.y) - (q1.x * q2.z) + (q1.y * q2.w) + (q1.z * q2.x);
		double z = (q1.w * q2.z) + (q1.x * q2.y) - (q1.y * q2.x) + (q1.z * q2.w);
		return new Quaternion(w, x, y, z);
	}
	
	public static Quaternion conjugate(Quaternion q1) {
		return new Quaternion(q1.w, -q1.x, -q1.y, -q1.z);
	}
	
	public static Quaternion localRotation(Vector3 axis, double angle) {
		double angleHalf = angle * 0.5;
		double w = Math.cos(angleHalf);
		double x = axis.x * Math.sin(angleHalf);
		double y = axis.y * Math.sin(angleHalf);
		double z = axis.z * Math.sin(angleHalf);
		
		return new Quaternion(w, x, y, z);
	}
	
	public static Quaternion rotate(Quaternion q1, Vector3 axis, double angle) {
		Quaternion localRotation = Quaternion.localRotation(Vector3.normalize(axis), angle);
		return Quaternion.multiply(localRotation, q1);
	}
	
	public static Vector3 rotateAround(Vector3 center, Vector3 point, Vector3 axis, double angle) {
		Vector3 localPoint = Vector3.subtract(point, center);
		Quaternion q = Quaternion.localRotation(axis, angle);
		Mat4x4 worldMat = Mat4x4.generateMatrix(q, center, null);
		return Vector3.mutiplyMatrixVector(worldMat, localPoint);
	}
	
	public static Quaternion lookAt(Vector3 pos, Vector3 target) {
		Vector3 test = new Vector3(0, 0, 1);
		Vector3 axis;
		Vector3 forward = Vector3.normalize(Vector3.subtract(target, pos));
		double dot = Vector3.dotProduct(test, forward);
		
		if (Math.abs(dot - (-1.0)) < 0.000001) {
			return new Quaternion(Math.PI, 0, 1, 0);
		}
		if (Math.abs(dot - (1.0)) < 0.000001) {
			return new Quaternion();
		}
		
		double angle = Math.acos(dot);
		axis = Vector3.normalize(Vector3.crossProduct(test, forward));
		return Quaternion.normalize(Quaternion.localRotation(axis, angle));
	}
	
	public static Quaternion slerp(Quaternion qfrom, Quaternion qto, double t) {
		qfrom = Quaternion.normalize(qfrom.copy());
		qto = Quaternion.normalize(qto.copy());
		
		double dot = Quaternion.dotProduct(qfrom, qto);
		double w1 = qfrom.w, x1 = qfrom.x, y1 = qfrom.y, z1 = qfrom.z;
		double w2 = qto.w, x2 = qto.x, y2 = qto.y, z2 = qto.z;
		
		if (dot < 0) {
			w1 = -w1;
			x1 = -x1;
			y1 = -y1;
			z1 = -z1;
			dot = -dot;
		}
		
		final double DOT_THRESHOLD = 0.9995;
		if (dot > DOT_THRESHOLD) {
			Quaternion result = new Quaternion(w1 + t * (w2 - w1), x1 + t * (x2 - x1), y1 + t * (y2 - y1), z1 + t * (z2 - z1));
			return Quaternion.normalize(result);
		}
		
		double theta_0 = Math.acos(dot);
		double sin_theta_0 = Math.sin(theta_0);
		
		double theta = theta_0 * t;
		double sin_theta = Math.sin(theta);
		double cos_theta = Math.cos(theta);
		
		double s0 = cos_theta - dot * sin_theta / sin_theta_0;
		double s1 = sin_theta / sin_theta_0;
		
		return new Quaternion(s0 * w1 + s1 * w2, s0 * x1 + s1 * x2, s0 * y1 + s1 * y2, s0 * z1 + s1 * z2);
	}
}
