package engine.vector;

import engine.matrix.Mat4x4;

public class Vector3 {
	
	public double x, y, z, w;
	
	// Constructor
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = 1;
	}
	
	// Project a 3d point to the screen
	public static Vector2 project(Vector3 vec3d) {
		double x, y;
		x = 0; y = 0;
		Vector2 vec2d = new Vector2(x, y);
		
		return vec2d;
	}
	
	// Make a duplicate object
	public Vector3 copy() {
		return new Vector3(this.x, this.y, this.z);
	}
	
	public static Vector3 up() {
		return new Vector3(0, 1, 0);
	}
	
	public static Vector3 empty() {
		return new Vector3(0, 0, 0);
	}
	
	// Find the distance between two 3d points
	public static double distance(Vector3 v1, Vector3 v2) {
		double dx = v1.x - v2.x;
		double dy = v1.y - v2.y;
		double dz = v1.z - v2.z;
		
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	// Find the dot product between two 3d points
	public static double dotProduct(Vector3 v1, Vector3 v2) {
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}
	
	// Find the distance of a 3d point from (0, 0, 0), also known as its length
	public static double length(Vector3 v1) {
		return Math.sqrt(Vector3.dotProduct(v1, v1));
	}
	
	// Multiplies a vector by a matrix
	public static Vector3 mutiplyMatrixVector(Mat4x4 mat, Vector3 v1) {
		double sx = (v1.x * mat.m[0][0]) + (v1.y * mat.m[1][0]) + (v1.z * mat.m[2][0]) + (v1.w * mat.m[3][0]);
		double sy = (v1.x * mat.m[0][1]) + (v1.y * mat.m[1][1]) + (v1.z * mat.m[2][1]) + (v1.w * mat.m[3][1]);
		double sz = (v1.x * mat.m[0][2]) + (v1.y * mat.m[1][2]) + (v1.z * mat.m[2][2]) + (v1.w * mat.m[3][2]);
		double sw = (v1.x * mat.m[0][3]) + (v1.y * mat.m[1][3]) + (v1.z * mat.m[2][3]) + (v1.w * mat.m[3][3]);
		
		Vector3 vec = new Vector3(sx, sy, sz);
		vec.w = sw;
		return vec;
	}
	
	// Add two 3d points
	public static Vector3 add(Vector3 v1, Vector3 v2) {
		double sx = v1.x + v2.x;
		double sy = v1.y + v2.y;
		double sz = v1.z + v2.z;
		
		return new Vector3(sx, sy, sz);
	}
	
	// Subtract two 3d points
	public static Vector3 subtract(Vector3 v1, Vector3 v2) {
		double sx = v1.x - v2.x;
		double sy = v1.y - v2.y;
		double sz = v1.z - v2.z;
		
		return new Vector3(sx, sy, sz);
	}
	
	// Multiply a 3d point by a constant
	public static Vector3 multiply(Vector3 v1, double k) {
		double rx = v1.x * k;
		double ry = v1.y * k;
		double rz = v1.z * k;
		
		return new Vector3(rx, ry, rz);
	}
	
	// Divide a 3d point by a constant
	public static Vector3 divide(Vector3 v1, double k) {
		double rx = v1.x / k;
		double ry = v1.y / k;
		double rz = v1.z / k;
		
		return new Vector3(rx, ry, rz);
	}
	
	// Linear interpolation from "v1" to "v2" at "value" time
	public static Vector3 lerp(Vector3 v1, Vector3 v2, double value) {
		double rx = (1 - value) * v1.x + value * v2.x;
		double ry = (1 - value) * v1.y + value * v2.y;
		double rz = (1 - value) * v1.z + value * v2.z;
		
		return new Vector3(rx, ry, rz);
	}
	
	// Normalizes a vector, meaning that it reduces the length to 1 while keeping the same direction
	public static Vector3 normalize(Vector3 v1) {
		double length = Vector3.length(v1);
		return new Vector3(v1.x / length, v1.y / length, v1.z / length);
	}
	
	// Finds the cross product of two vectors
	public static Vector3 crossProduct(Vector3 v1, Vector3 v2) {
		double rx = v1.y * v2.z - v1.z * v2.y;
		double ry = v1.z * v2.x - v1.x * v2.z;
		double rz = v1.x * v2.y - v1.y * v2.x;
		
		return new Vector3(rx, ry, rz);
	}
	
	// Makes the tangent orthogonal to the normal
	public static void orthoNormal(Vector3 normal, Vector3 tangent) {
		var n = Vector3.normalize(normal);
		var dot = Vector3.dotProduct(tangent, n);
		var proj = Vector3.multiply(n, dot);
		var t = Vector3.subtract(tangent, proj);
		t = Vector3.normalize(t);
		normal.x = n.x;
		normal.y = n.y;
		normal.z = n.z;
		tangent.x = t.x;
		tangent.y = t.y;
		tangent.z = t.z;
	}
	
	// Finds the percentage on a line between two points where they are intersected by a plane
	public static double intersectPlaneDouble(Vector3 pPlane, Vector3 nPlane, Vector3 start, Vector3 end) {
		nPlane = Vector3.normalize(nPlane);
		double d = Vector3.dotProduct(nPlane, pPlane);
		double ad = Vector3.dotProduct(start, nPlane);
		double bd = Vector3.dotProduct(end, nPlane);
		return (d - ad) / (bd - ad);
	}
	
	// Finds the exact vector on a line between two points where they are intersected by a plane
	public static Vector3 intersectPlaneVector3d(Vector3 start, Vector3 end, double t) {
		Vector3 lineStartToEnd = Vector3.subtract(end, start);
		Vector3 lineToIntersect = Vector3.multiply(lineStartToEnd, t);
		return Vector3.add(start, lineToIntersect);
	}
}
