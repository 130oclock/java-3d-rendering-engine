package engine.vector;

import engine.matrix.Mat4x4;

public class Vector3d {
	
	public double x, y, z, w;
	
	// Constructor
	public Vector3d(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = 1;
	}
	
	// Project a 3d point to the screen
	public static Vector2d project(Vector3d vec3d) {
		double x, y;
		x = 0; y = 0;
		Vector2d vec2d = new Vector2d(x, y);
		
		return vec2d;
	}
	
	// Make a duplicate object
	public Vector3d copy() {
		return new Vector3d(this.x, this.y, this.z);
	}
	
	public static Vector3d up() {
		return new Vector3d(0, 1, 0);
	}
	
	public static Vector3d empty() {
		return new Vector3d(0, 0, 0);
	}
	
	// Find the distance between two 3d points
	public static double distance(Vector3d v1, Vector3d v2) {
		double dx = v1.x - v2.x;
		double dy = v1.y - v2.y;
		double dz = v1.z - v2.z;
		
		return Math.sqrt(dx * dx + dy * dy + dz * dz);
	}
	
	// Find the dot product between two 3d points
	public static double dotProduct(Vector3d v1, Vector3d v2) {
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}
	
	// Find the distance of a 3d point from (0, 0, 0), also known as its length
	public static double length(Vector3d v1) {
		return Math.sqrt(Vector3d.dotProduct(v1, v1));
	}
	
	// Multiplies a vector by a matrix
	public static Vector3d mutiplyMatrixVector(Mat4x4 mat, Vector3d v1) {
		double sx = (v1.x * mat.m[0][0]) + (v1.y * mat.m[1][0]) + (v1.z * mat.m[2][0]) + (v1.w * mat.m[3][0]);
		double sy = (v1.x * mat.m[0][1]) + (v1.y * mat.m[1][1]) + (v1.z * mat.m[2][1]) + (v1.w * mat.m[3][1]);
		double sz = (v1.x * mat.m[0][2]) + (v1.y * mat.m[1][2]) + (v1.z * mat.m[2][2]) + (v1.w * mat.m[3][2]);
		double sw = (v1.x * mat.m[0][3]) + (v1.y * mat.m[1][3]) + (v1.z * mat.m[2][3]) + (v1.w * mat.m[3][3]);
		
		Vector3d vec = new Vector3d(sx, sy, sz);
		vec.w = sw;
		return vec;
	}
	
	// Add two 3d points
	public static Vector3d add(Vector3d v1, Vector3d v2) {
		double sx = v1.x + v2.x;
		double sy = v1.y + v2.y;
		double sz = v1.z + v2.z;
		
		return new Vector3d(sx, sy, sz);
	}
	
	// Subtract two 3d points
	public static Vector3d subtract(Vector3d v1, Vector3d v2) {
		double sx = v1.x - v2.x;
		double sy = v1.y - v2.y;
		double sz = v1.z - v2.z;
		
		return new Vector3d(sx, sy, sz);
	}
	
	// Multiply a 3d point by a constant
	public static Vector3d multiply(Vector3d v1, double k) {
		double rx = v1.x * k;
		double ry = v1.y * k;
		double rz = v1.z * k;
		
		return new Vector3d(rx, ry, rz);
	}
	
	// Divide a 3d point by a constant
	public static Vector3d divide(Vector3d v1, double k) {
		double rx = v1.x / k;
		double ry = v1.y / k;
		double rz = v1.z / k;
		
		return new Vector3d(rx, ry, rz);
	}
	
	// Linear interpolation from "v1" to "v2" at "value" time
	public static Vector3d lerp(Vector3d v1, Vector3d v2, double value) {
		double rx = (1 - value) * v1.x + value * v2.x;
		double ry = (1 - value) * v1.y + value * v2.y;
		double rz = (1 - value) * v1.z + value * v2.z;
		
		return new Vector3d(rx, ry, rz);
	}
	
	// Normalizes a vector, meaning that it reduces the length to 1 while keeping the same direction
	public static Vector3d normalize(Vector3d v1) {
		double length = Vector3d.length(v1);
		return new Vector3d(v1.x / length, v1.y / length, v1.z / length);
	}
	
	// Finds the cross product of two vectors
	public static Vector3d crossProduct(Vector3d v1, Vector3d v2) {
		double rx = v1.y * v2.z - v1.z * v2.y;
		double ry = v1.z * v2.x - v1.x * v2.z;
		double rz = v1.x * v2.y - v1.y * v2.x;
		
		return new Vector3d(rx, ry, rz);
	}
	
	// Makes the tangent orthogonal to the normal
	public static void orthoNormal(Vector3d normal, Vector3d tangent) {
		var n = Vector3d.normalize(normal);
		var dot = Vector3d.dotProduct(tangent, n);
		var proj = Vector3d.multiply(n, dot);
		var t = Vector3d.subtract(tangent, proj);
		t = Vector3d.normalize(t);
		normal.x = n.x;
		normal.y = n.y;
		normal.z = n.z;
		tangent.x = t.x;
		tangent.y = t.y;
		tangent.z = t.z;
	}
	
	// Finds the percentage on a line between two points where they are intersected by a plane
	public static double intersectPlaneDouble(Vector3d pPlane, Vector3d nPlane, Vector3d start, Vector3d end) {
		nPlane = Vector3d.normalize(nPlane);
		double d = Vector3d.dotProduct(nPlane, pPlane);
		double ad = Vector3d.dotProduct(start, nPlane);
		double bd = Vector3d.dotProduct(end, nPlane);
		return (d - ad) / (bd - ad);
	}
	
	// Finds the exact vector on a line between two points where they are intersected by a plane
	public static Vector3d intersectPlaneVector3d(Vector3d start, Vector3d end, double t) {
		Vector3d lineStartToEnd = Vector3d.subtract(end, start);
		Vector3d lineToIntersect = Vector3d.multiply(lineStartToEnd, t);
		return Vector3d.add(start, lineToIntersect);
	}
}
