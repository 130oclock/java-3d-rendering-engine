package engine.quaternion;

import engine.matrix.Mat4x4;
import engine.vector.Vector3d;

public class Quaternion {
	
	public double w, x, y, z;
	
	public Quaternion(double w, double x, double y, double z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3d getForwardVector() {
		double x = 2 * ((this.x * this.z) + (this.w * this.y));
		double y = 2 * ((this.y * this.z) - (this.w * this.x));
		double z = 1 - (2 * ((this.x * this.x) + (this.y * this.y)));
		return new Vector3d(x, y, z);
	}
	
	public Vector3d getUpVector() {
		double x = 2 * ((this.x * this.y) - (this.w * this.z));
		double y = 1 - (2 * ((this.x * this.x) + (this.z * this.z)));
		double z = 2 * ((this.y * this.z) + (this.w * this.x));
		return new Vector3d(x, y, z);
	}
	
	public Vector3d getRightVector() {
		double x = 1 - (2 * ((this.y * this.y) + (this.z * this.z)));
		double y = 2 * ((this.x * this.y) + (this.w * this.z));
		double z = 2 * ((this.x * this.z) - (this.w * this.y));
		return new Vector3d(x, y, z);
	}
	
	public Vector3d getAxis() {
		return new Vector3d(this.x, this.y, this.z);
	}
	

	public Quaternion copy() {
		return new Quaternion(this.w, this.x, this.y, this.z);
	}
	
	public static Quaternion empty() {
		return new Quaternion(1, 0, 0, 0);
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
	
	public static Quaternion localRotation(Vector3d axis, double angle) {
		double angleHalf = angle * 0.5;
		double w = Math.cos(angleHalf);
		double x = axis.x * Math.sin(angleHalf);
		double y = axis.y * Math.sin(angleHalf);
		double z = axis.z * Math.sin(angleHalf);
		
		return new Quaternion(w, x, y, z);
	}
	
	public static Quaternion rotate(Quaternion q1, Vector3d axis, double angle) {
		Quaternion localRotation = Quaternion.localRotation(Vector3d.normalize(axis), angle);
		return Quaternion.multiply(localRotation, q1);
	}
	
	public static Quaternion lookAt(Vector3d pos, Vector3d target) {
		Vector3d test = new Vector3d(0, 0, 1);
		Vector3d axis;
		Vector3d forward = Vector3d.normalize(Vector3d.subtract(target, pos));
		double dot = Vector3d.dotProduct(test, forward);
		
		if (Math.abs(dot - (-1.0)) < 0.000001) {
			return new Quaternion(Math.PI, 0, 1, 0);
		}
		if (Math.abs(dot - (1.0)) < 0.000001) {
			return Quaternion.empty();
		}
		
		double angle = Math.acos(dot);
		axis = Vector3d.normalize(Vector3d.crossProduct(test, forward));
		return Quaternion.normalize(Quaternion.localRotation(axis, angle));
	}
	
	public static Mat4x4 generateMatrix(Quaternion q1, Vector3d pos) {
		double w = q1.w, x = q1.x, y = q1.y, z = q1.z;
		double sqx = x * x, sqy = y * y, sqz = z * z;
		
		Mat4x4 mat = new Mat4x4();
		Mat4x4.makeBlank(mat);
		mat.m[0][0] = 1 - (2 * sqy) - (2 * sqz);
		mat.m[0][1] = (2 * x * y) - (2 * w * z);
		mat.m[0][2] = (2 * x * z) + (2 * w * y);
		mat.m[1][0] = (2 * x * y) + (2 * w * z);
		mat.m[1][1] = 1 - (2 * sqx) - (2 * sqz);
		mat.m[1][2] = (2 * y * z) - (2 * w * x);
		mat.m[2][0] = (2 * x * z) - (2 * w * y);
		mat.m[2][1] = (2 * y * z) + (2 * w * x);
		mat.m[2][2] = 1 - (2 * sqx) - (2 * sqy);
		mat.m[3][3] = 1;
		if (pos != null) {
			mat.m[3][0] = pos.x;
			mat.m[3][1] = pos.y;
			mat.m[3][2] = pos.z;
		}
		return mat;
	}
}
