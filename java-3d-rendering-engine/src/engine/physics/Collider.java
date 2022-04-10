package engine.physics;

import engine.matrix.Mat4x4;
import engine.quaternion.Quaternion;
import engine.vector.Vector3;

public class Collider {
	private Vector3 max, min;
	private Vector3[] points;
	private Vector3[] normals;
	private double radius;
	private int type;
	
	public Collider(Vector3 max, Vector3 min) {
		this.max = max;
		this.min = min;
		type = 0;
		
		this.points = this.getPoints();
		this.normals = new Vector3[] { Vector3.left(), Vector3.up(), Vector3.forward() };
	}
	
	public Collider(double radius) {
		this.radius = radius;
		type = 1;
	}

	public Vector3 getMax() {
		return max;
	}

	public Vector3 getMin() {
		return min;
	}
	
	public Vector3[] getPoints() {
		double maxX = max.x, maxY = max.y, maxZ = max.z, minX = min.x, minY = min.y, minZ = min.z;
		Vector3[] points = {
				new Vector3(minX, minY, minZ),
				new Vector3(maxX, minY, minZ),
				new Vector3(maxX, maxY, minZ),
				new Vector3(minX, maxY, minZ),
				new Vector3(minX, minY, maxZ),
				new Vector3(maxX, minY, maxZ),
				new Vector3(maxX, maxY, maxZ),
				new Vector3(minX, maxY, maxZ),
		};
		return points;
	}
	
	public Vector3[] getAdjustedPoints(Vector3 pos, Quaternion rot) {
		Mat4x4 matRot = Quaternion.generateMatrix(rot, pos);
		int length = this.points.length;
		Vector3[] points = new Vector3[length];
		for (int i = 0; i < length; i++) {
			points[i] = Vector3.mutiplyMatrixVector(matRot, this.points[i]);
		}
		return points;
	}
	
	public Vector3[] getNormals() {
		return this.normals;
	}
	
	public Vector3[] getAdjustedNormals(Quaternion rot) {
		Mat4x4 matRot = Quaternion.generateMatrix(rot, null);
		int length = this.normals.length;
		Vector3[] points = new Vector3[length];
		for (int i = 0; i < length; i++) {
			points[i] = Vector3.mutiplyMatrixVector(matRot, this.normals[i]);
		}
		return points;
	}
}
