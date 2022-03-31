package engine.models;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import engine.camera.Camera;
import engine.improvednoise.ImprovedNoise;
import engine.light.EnvironmentLight;
import engine.matrix.Mat4x4;
import engine.quaternion.Quaternion;
import engine.triangle.Triangle;
import engine.vector.Vector2;
import engine.vector.Vector3;

public class Planet {
	
	public int resolution = 16;
	public double radius = 1;
	public double oceanRadius = 1;
	public double roughness = 1;
	public double strength = 0;
	private Triangle[][] terrainFaces = new Triangle[6][];
	
	public Planet() {
		
		Vector3[] directions = { Vector3.up(), Vector3.down(), Vector3.left(), Vector3.right(), Vector3.forward(), Vector3.back() };
		
		for (int i = 0; i < 6; i++) {
			Triangle[] triangles = terrainFace(resolution, directions[i]);
			terrainFaces[i] = triangles;
		}
		
		// 1 -> 3, 1 -> 4, 1 -> 5, 1 -> 6, 2 -> 3, 2 -> 4, 2 -> 5, 2 -> 6, 3 -> 5, 3 -> 6, 4 -> 5, 4 -> 6
		/*Triangle[] list = terrainFaces[0];
		for (int i = 0; i < list.length; i++) {
			int x = i % (resolution - 1);
			int y = i / (resolution - 1);
			//if ()
		}*/
	}
	
	public void project(Camera camera, Mat4x4 matView, Mat4x4 matProj, int WIDTH, int HEIGHT, EnvironmentLight light) {
		for (int i = 0; i < 6; i++)
			Triangle.projectTriangles(this.terrainFaces[i], new Vector3(), new Quaternion(), camera, matView, matProj, WIDTH, HEIGHT, light, Color.WHITE);
	}
	
	private Triangle[] terrainFace(int resolution, Vector3 localup) {
		Vector3 axisA = new Vector3(localup.y, localup.z, localup.x);
		Vector3 axisB = Vector3.crossProduct(localup, axisA);
		
		Vector3[] vertices = new Vector3[resolution * resolution];
		Vector3[] normals = new Vector3[resolution * resolution];
		for (int i = 0; i < normals.length; i++) normals[i] = new Vector3();
		int[] triangleInds = new int[(resolution-1) * (resolution-1) * 6];
		
		for (int y = 0; y < resolution; y++) {
			for (int x = 0; x < resolution; x++) {
				int i = y * resolution + x;
				Vector3 pointOnUnitCube = Vector3.add(localup, Vector3.add(Vector3.multiply(axisA, (((double)x / (resolution-1)) - 0.5) * 2), Vector3.multiply(axisB, (((double)y / (resolution-1)) - 0.5) * 2)));
				Vector3 pointOnUnitSphere = Vector3.normalize(pointOnUnitCube);
				double elevation = ImprovedNoise.noise(pointOnUnitSphere.x * roughness + 1, pointOnUnitSphere.y * roughness + 1, pointOnUnitSphere.z * roughness + 1) * 0.5 * strength;
				elevation = Math.max(radius * (1 + elevation), oceanRadius);
				pointOnUnitSphere = Vector3.multiply(pointOnUnitSphere, elevation);
				vertices[i] = pointOnUnitSphere;
			}
		}

		int triIndex = 0;
		
		for (int y = 0; y < resolution; y++) {
			for (int x = 0; x < resolution; x++) {
				int i = y * resolution + x;
				if (x != resolution - 1 && y != resolution - 1) {
					int index1 = i;
					int index2 = i + resolution + 1;
					int index3 = i + resolution;
					
					triangleInds[triIndex]     = index1;
					triangleInds[triIndex + 1] = index2;
					triangleInds[triIndex + 2] = index3;
	
					Vector3 v1 = vertices[index1];
					Vector3 v2 = vertices[index2];
					Vector3 v3 = vertices[index3];
					
					Vector3 normal1 = Triangle.findFaceNormal(v1, v2, v3);
					
					normals[index1] = Vector3.add(normals[index1], normal1);
					normals[index2] = Vector3.add(normals[index2], normal1);
					normals[index3] = Vector3.add(normals[index3], normal1);
					
					int index4 = i;
					int index5 = i + 1;
					int index6 = i + resolution + 1;
					
					triangleInds[triIndex + 3] = index4;
					triangleInds[triIndex + 4] = index5;
					triangleInds[triIndex + 5] = index6;
					
					Vector3 v4 = vertices[index4];
					Vector3 v5 = vertices[index5];
					Vector3 v6 = vertices[index6];
					
					Vector3 normal2 = Triangle.findFaceNormal(v4, v5, v6);
					
					normals[index4] = Vector3.add(normals[index4], normal2);
					normals[index5] = Vector3.add(normals[index5], normal2);
					normals[index6] = Vector3.add(normals[index6], normal2);
					
					triIndex += 6;
				}
			}
		}
		
		for (int i = 0; i < normals.length; i++) {
			normals[i] = Vector3.normalize(normals[i]);
		}
		
		Triangle[] triangles = new Triangle[(int) triIndex / 3];
		for (int i = 0; i < triangles.length; i++) {
			int index = i * 3;
			Vector3 v1 = vertices[triangleInds[index]];
			Vector3 v2 = vertices[triangleInds[index + 1]];
			Vector3 v3 = vertices[triangleInds[index + 2]];
			
			Vector3 n1 = normals[triangleInds[index]];
			Vector3 n2 = normals[triangleInds[index + 1]];
			Vector3 n3 = normals[triangleInds[index + 2]];
			
			Triangle tri = new Triangle(v1, v2, v3, null, null, null, n1, n2, n3);
			triangles[i] = tri;
		}

		System.out.println("loaded terrain face | " + vertices.length + " vertices | " + triangles.length + " triangles");
		return triangles;
	}
}
