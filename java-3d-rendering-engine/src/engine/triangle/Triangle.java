package engine.triangle;

import java.awt.Color;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import engine.camera.Camera;
import engine.light.EnvironmentLight;
import engine.line.Line;
import engine.matrix.Mat4x4;
import engine.quaternion.Quaternion;
import engine.vector.Vector2;
import engine.vector.Vector3;

public class Triangle {
	
	private static boolean showClipping = false;
	
	private static int lightingType = 0;
	public static boolean doGouraud = true;
	private static double minimumBrightness = 0.1;

	private static boolean drawOutlines = false;
	
	public static List<Triangle> triangleRaster = new ArrayList<Triangle>();
	
	private Vector3[] p = new Vector3[3];
	private Vector3[] n = new Vector3[3];
	
	private Vector2[] t = new Vector2[3];
	
	private Color[] brightness = new Color[3];
	public Color[] color = new Color[3];
	
	public Triangle(Vector3 p0, Vector3 p1, Vector3 p2, Vector2 t0, Vector2 t1, Vector2 t2, Vector3 n0, Vector3 n1, Vector3 n2) {
		this.p[0] = p0;
		this.p[1] = p1;
		this.p[2] = p2;

		this.n[0] = n0;
		this.n[1] = n1;
		this.n[2] = n2;
		
		if (t0 == null) {
			this.t[0] = new Vector2(0, 0);
			this.t[1] = new Vector2(0, 0);
			this.t[2] = new Vector2(0, 0);
		} else {
			this.t[0] = t0;
			this.t[1] = t1;
			this.t[2] = t2;
		}
		
		this.color[0] = Color.WHITE;
		this.color[1] = Color.WHITE;
		this.color[2] = Color.WHITE;
	}
	
	public Triangle(Vector3 v1, Vector3 v2, Vector3 v3) {
		this(v1, v2, v3, new Vector2(0, 0), new Vector2(0, 0), new Vector2(0, 0), null, null, null);
	}
	
	public Triangle(double[] v1, double[] v2, double[] v3) {
		this(new Vector3(v1[0], v1[1], v1[2]), new Vector3(v2[0], v2[1], v2[2]), new Vector3(v3[0], v3[1], v3[2]), new Vector2(0, 0), new Vector2(0, 0), new Vector2(0, 0), null, null, null);
	}
	
	public Triangle(double[] v1, double[] v2, double[] v3, double[] vt1, double[] vt2, double[] vt3) {
		this(new Vector3(v1[0], v1[1], v1[2]), new Vector3(v2[0], v2[1], v2[2]), new Vector3(v3[0], v3[1], v3[2]), new Vector2(vt1[0], vt1[1]), new Vector2(vt2[0], vt2[1]), new Vector2(vt3[0], vt3[1]), null, null, null);
	}
	public Triangle(double[] v1, double[] v2, double[] v3, double[] vt1, double[] vt2, double[] vt3, double[] vn1, double[] vn2, double[] vn3) {
		this.p[0] = new Vector3(v1[0], v1[1], v1[2]);
		this.p[1] = new Vector3(v2[0], v2[1], v2[2]);
		this.p[2] = new Vector3(v3[0], v3[1], v3[2]);
		
		this.n[0] = new Vector3(vn1[0], vn1[1], vn1[2]);
		this.n[1] = new Vector3(vn2[0], vn2[1], vn2[2]);
		this.n[2] = new Vector3(vn3[0], vn3[1], vn3[2]);
		
		if (vt1 == null) {
			this.t[0] = new Vector2(0, 0);
			this.t[1] = new Vector2(0, 0);
			this.t[2] = new Vector2(0, 0);
		} else {
			this.t[0] = new Vector2(vt1[0], vt1[1]);
			this.t[1] = new Vector2(vt2[0], vt2[1]);
			this.t[2] = new Vector2(vt3[0], vt3[1]);
		}

		this.color[0] = Color.WHITE;
		this.color[1] = Color.WHITE;
		this.color[2] = Color.WHITE;
	}
	
	// Create an empty Triangle
	public static Triangle empty() {
		Vector3 v1 = new Vector3(0, 0, 0);
		Vector3 v2 = new Vector3(0, 0, 0);
		Vector3 v3 = new Vector3(0, 0, 0);
		Vector2 v4 = new Vector2(0, 0);
		Vector2 v5 = new Vector2(0, 0);
		Vector2 v6 = new Vector2(0, 0);
		return new Triangle(v1, v2, v3, v4, v5, v6, null, null, null);
	}
	
	// Return the nth point on the triangle
	public Vector3 getVector(int n) {
		return this.p[n].copy();
	}
	
	// Make a duplicate object
	public Triangle copy() {
		Triangle copy = Triangle.empty();
		copy.p[0].x = this.p[0].x;
		copy.p[0].y = this.p[0].y;
		copy.p[0].z = this.p[0].z;
		copy.p[0].w = this.p[0].w;
		copy.p[1].x = this.p[1].x;
		copy.p[1].y = this.p[1].y;
		copy.p[1].z = this.p[1].z;
		copy.p[1].w = this.p[1].w;
		copy.p[2].x = this.p[2].x;
		copy.p[2].y = this.p[2].y;
		copy.p[2].z = this.p[2].z;
		copy.p[2].w = this.p[2].w;
		
		if (n[0] != null) {
			copy.n[0].x = this.n[0].x;
			copy.n[0].y = this.n[0].y;
			copy.n[0].z = this.n[0].z;
		}
		if (n[1] != null) {
			copy.n[1].x = this.n[1].x;
			copy.n[1].y = this.n[1].y;
			copy.n[1].z = this.n[1].z;
		}
		if (n[2] != null) {
			copy.n[2].x = this.n[2].x;
			copy.n[2].y = this.n[2].y;
			copy.n[2].z = this.n[2].z;
		}
		
		copy.t[0].u = this.t[0].u;
		copy.t[0].v = this.t[0].v;
		copy.t[0].w = this.t[0].w;
		copy.t[1].u = this.t[1].u;
		copy.t[1].v = this.t[1].v;
		copy.t[1].w = this.t[1].w;
		copy.t[2].u = this.t[2].u;
		copy.t[2].v = this.t[2].v;
		copy.t[2].w = this.t[2].w;
		
		copy.brightness = this.brightness;
		copy.color = this.color;
		return copy;
	}
	
	// Multiply a Triangle by a matrix
	public static Vector3[] multiplyMatrixTriangle(Mat4x4 mat, Triangle tri) {
		Vector3 vec1 = Vector3.mutiplyMatrixVector(mat, tri.p[0]);
		Vector3 vec2 = Vector3.mutiplyMatrixVector(mat, tri.p[1]);
		Vector3 vec3 = Vector3.mutiplyMatrixVector(mat, tri.p[2]);
		
		return new Vector3[] { vec1, vec2, vec3 };
	}
	
	private static double signedDist(Vector3 nPlane, Vector3 pPlane, Vector3 p) {
		return (nPlane.x * p.x + nPlane.y * p.y + nPlane.z * p.z - Vector3.dotProduct(nPlane, pPlane));
	}
	
	public static Triangle[] clipAgainstPlane(Vector3 pPlane, Vector3 nPlane, Triangle tri) {
		nPlane = Vector3.normalize(nPlane);
		
		Vector3[] insidePoints = new Vector3[3];		int nInsidePoints = 0;
		Vector3[] outsidePoints = new Vector3[3];		int nOutsidePoints = 0;
		Color[] insideColor = new Color[3];				int nInsideColor = 0;
		Color[] outsideColor = new Color[3];			int nOutsideColor = 0;
		Vector2[] insideTex = new Vector2[3];			int nInsideTex = 0;
		Vector2[] outsideTex = new Vector2[3];		int nOutsideTex = 0;
		
		double d0 = Triangle.signedDist(nPlane, pPlane, tri.p[0]);
		double d1 = Triangle.signedDist(nPlane, pPlane, tri.p[1]);
		double d2 = Triangle.signedDist(nPlane, pPlane, tri.p[2]);
		
		if (d0 >= 0) {
			insidePoints[nInsidePoints++] = tri.p[0];
			insideColor[nInsideColor++] = tri.brightness[0];
			insideTex[nInsideTex++] = tri.t[0];
		} else {
			outsidePoints[nOutsidePoints++] = tri.p[0];
			outsideColor[nOutsideColor++] = tri.brightness[0];
			outsideTex[nOutsideTex++] = tri.t[0];
		}
		if (d1 >= 0) {
			insidePoints[nInsidePoints++] = tri.p[1];
			insideColor[nInsideColor++] = tri.brightness[1];
			insideTex[nInsideTex++] = tri.t[1];
		} else {
			outsidePoints[nOutsidePoints++] = tri.p[1];
			outsideColor[nOutsideColor++] = tri.brightness[1];
			outsideTex[nOutsideTex++] = tri.t[1];
		}
		if (d2 >= 0) {
			insidePoints[nInsidePoints++] = tri.p[2];
			insideColor[nInsideColor++] = tri.brightness[2];
			insideTex[nInsideTex++] = tri.t[2];
		} else {
			outsidePoints[nOutsidePoints++] = tri.p[2];
			outsideColor[nOutsideColor++] = tri.brightness[2];
			outsideTex[nOutsideTex++] = tri.t[2];
		}
		
		// if there are no points on the screen
		if (nInsidePoints == 0) {
			return new Triangle[] { null, null };
		}
		// if all of the points are on the screen
		if (nInsidePoints == 3) {
			return new Triangle[] { tri.copy(), null };
		}
		// if one point is on the screen and two are not on the screen
		if (nInsidePoints == 1 && nOutsidePoints == 2) { // A ; B, C
			Triangle otri1 = Triangle.empty();
			
			// A
			otri1.p[0] = insidePoints[0];
			otri1.t[0] = insideTex[0];
			otri1.brightness[0] = insideColor[0];
			
			// intersection of AB = B'
			double t1 = Vector3.intersectPlaneDouble(pPlane, nPlane, insidePoints[0], outsidePoints[0]);
			otri1.p[1] = Vector3.intersectPlaneVector3d(insidePoints[0], outsidePoints[0], t1);
			otri1.t[1].u = t1 * (outsideTex[0].u - insideTex[0].u) + insideTex[0].u;
			otri1.t[1].v = t1 * (outsideTex[0].v - insideTex[0].v) + insideTex[0].v;
			otri1.t[1].w = t1 * (outsideTex[0].w - insideTex[0].w) + insideTex[0].w;
			otri1.brightness[1] = new Color ((int) (t1 * (outsideColor[0].getRed() - insideColor[0].getRed()) + insideColor[0].getRed()), (int) (t1 * (outsideColor[0].getGreen() - insideColor[0].getGreen()) + insideColor[0].getGreen()), (int) (t1 * (outsideColor[0].getBlue() - insideColor[0].getBlue()) + insideColor[0].getBlue()));
			
			// intersection of AC = C'
			double t2 = Vector3.intersectPlaneDouble(pPlane, nPlane, insidePoints[0], outsidePoints[1]);
			otri1.p[2] = Vector3.intersectPlaneVector3d(insidePoints[0], outsidePoints[1], t2);
			otri1.t[2].u = t2 * (outsideTex[1].u - insideTex[0].u) + insideTex[0].u;
			otri1.t[2].v = t2 * (outsideTex[1].v - insideTex[0].v) + insideTex[0].v;
			otri1.t[2].w = t2 * (outsideTex[1].w - insideTex[0].w) + insideTex[0].w;
			otri1.brightness[2] = new Color ((int) (t2 * (outsideColor[1].getRed() - insideColor[0].getRed()) + insideColor[0].getRed()), (int) (t2 * (outsideColor[1].getGreen() - insideColor[0].getGreen()) + insideColor[0].getGreen()), (int) (t2 * (outsideColor[1].getBlue() - insideColor[0].getBlue()) + insideColor[0].getBlue()));
			
			if (showClipping == true) {
				otri1.brightness[0] = Color.BLUE;
				otri1.brightness[1] = Color.BLUE;
				otri1.brightness[2] = Color.BLUE;
			}
			
			return new Triangle[] { otri1, null }; // Triangle AB'C'
		}
		// if two points are on the screen and one is not on the screen
		if (nInsidePoints == 2 && nOutsidePoints == 1) { // A, B ; C
			Triangle otri1 = Triangle.empty(), otri2 = Triangle.empty();
			
			// A
			otri1.p[0] = insidePoints[0];
			otri1.t[0] = insideTex[0];
			otri1.brightness[0] = insideColor[0];
			
			// B
			otri1.p[1] = insidePoints[1];
			otri1.t[1] = insideTex[1];
			otri1.brightness[1] = insideColor[1];
			
			// intersection of AC = A'
			double t1 = Vector3.intersectPlaneDouble(pPlane, nPlane, insidePoints[0], outsidePoints[0]);
			otri1.p[2] = Vector3.intersectPlaneVector3d(insidePoints[0], outsidePoints[0], t1);
			otri1.t[2].u = t1 * (outsideTex[0].u - insideTex[0].u) + insideTex[0].u;
			otri1.t[2].v = t1 * (outsideTex[0].v - insideTex[0].v) + insideTex[0].v;
			otri1.t[2].w = t1 * (outsideTex[0].w - insideTex[0].w) + insideTex[0].w;
			otri1.brightness[2] = new Color ((int) (t1 * (outsideColor[0].getRed() - insideColor[0].getRed()) + insideColor[0].getRed()), (int) (t1 * (outsideColor[0].getGreen() - insideColor[0].getGreen()) + insideColor[0].getGreen()), (int) (t1 * (outsideColor[0].getBlue() - insideColor[0].getBlue()) + insideColor[0].getBlue()));
			
			// A'
			otri2.p[0] = otri1.p[2];
			otri2.t[0] = otri1.t[2];
			otri2.brightness[0] = otri1.brightness[2];
			
			// B
			otri2.p[1] = insidePoints[1];
			otri2.t[1] = insideTex[1];
			otri2.brightness[1] = insideColor[1];
			
			// intersection of BC = B'
			double t2 = Vector3.intersectPlaneDouble(pPlane, nPlane, insidePoints[1], outsidePoints[0]);
			otri2.p[2] = Vector3.intersectPlaneVector3d(insidePoints[1], outsidePoints[0], t2);
			otri2.t[2].u = t2 * (outsideTex[0].u - insideTex[1].u) + insideTex[1].u;
			otri2.t[2].v = t2 * (outsideTex[0].v - insideTex[1].v) + insideTex[1].v;
			otri2.t[2].w = t2 * (outsideTex[0].w - insideTex[1].w) + insideTex[1].w;
			otri2.brightness[2] = new Color ((int) (t2 * (outsideColor[0].getRed() - insideColor[1].getRed()) + insideColor[1].getRed()), (int) (t2 * (outsideColor[0].getGreen() - insideColor[1].getGreen()) + insideColor[1].getGreen()), (int) (t2 * (outsideColor[0].getBlue() - insideColor[1].getBlue()) + insideColor[1].getBlue()));
			
			if (showClipping == true) {
				otri1.brightness[0] = Color.YELLOW;
				otri1.brightness[1] = Color.YELLOW;
				otri1.brightness[2] = Color.YELLOW;
				otri2.brightness[0] = Color.GREEN;
				otri2.brightness[1] = Color.GREEN;
				otri2.brightness[2] = Color.GREEN;
			}
			
			return new Triangle[] { otri1, otri2 }; // Triangle ABA' and A'BB'
		}
		return null;
	}
	
	public static Vector3 findFaceNormal(Vector3 v1, Vector3 v2, Vector3 v3) {
		Vector3 normal, line1, line2;
		line1 = Vector3.subtract(v2, v1);
		line2 = Vector3.subtract(v3, v1);
		//System.out.println(line1.x + " " + line1.y + " " + line1.z + " | " + line2.x + " " + line2.y + " " + line2.z);
		
		normal = Vector3.crossProduct(line1, line2);
		//System.out.println(normal.x + " " + normal.y + " " + normal.z);
		
		return Vector3.normalize(normal);
	}
	
	private static int findDuplicate(Vector3 v1, List<Vector3> arr) {
		if (arr == null) return -1;
		int index = 0;
		int len = arr.size();
		double x1 = v1.x, y1 = v1.y, z1 = v1.z;
		while (index < len) {
			Vector3 v2 = arr.get(index);
			double x2 = v2.x, y2 = v2.y, z2 = v2.z;
			
			if (Math.abs(x1 - x2) < 0.0001 && Math.abs(y1 - y2) < 0.0001 && Math.abs(z1 - z2) < 0.0001) {
				return index;
			} else index++;
		}
		return -1;
	}
	
	public static Triangle[] findSmoothTriangleNormals(Triangle[] triangles) {
		List<Vector3> vertices = new ArrayList<Vector3>(); // list of all vertices
		List<Vector3> normals = new ArrayList<Vector3>(); // list of all normals corresponding to a vertex
		//List<ArrayList<Integer> > triangleInds = new ArrayList<ArrayList<Integer> >(); // a list for each vertex of all the triangle indices that share that vertex
		int[][] vertexInds = new int[triangles.length][]; // list of the vertex indices to reconstruct the triangles
		Vector3[] triNormals = new Vector3[triangles.length]; // list of all triangle face normals
		//List<Triangle> smoothTriangles = new ArrayList<Triangle>(); // list of all triangles with smoothed normals
		
		for (int i = 0; i < triangles.length; i++) {
			Triangle tri = triangles[i];
			Vector3 v1 = tri.p[0], v2 = tri.p[1], v3 = tri.p[2];
			
			Vector3 normal = findFaceNormal(v1, v2, v3);
			triNormals[i] = normal;
			
			int[] indices = new int[3];
			
			int i1 = findDuplicate(v1, vertices);
			if (i1 == -1) { // vertex doesn't exist yet
				// add new vertex
				vertices.add(v1);
				// add normal to list
				normals.add(normal);
				// log the index of the vertex for later reconstruction
				indices[0] = vertices.size() - 1;
			} else { // vertex already exists
				// get the normal
				Vector3 norm = normals.get(i1);
				// sum the normals
				normals.set(i1, Vector3.add(norm, normal));
				// log the index of the vertex for later reconstruction
				indices[0] = i1;
			}
			
			int i2 = findDuplicate(v2, vertices);
			if (i2 == -1) { // vertex doesn't exist yet
				// add new vertex
				vertices.add(v2);
				// add normal to list
				normals.add(normal);
				// log the index of the vertex for later reconstruction
				indices[1] = vertices.size() - 1;
			} else { // vertex already exists
				// get the normal
				Vector3 norm = normals.get(i2);
				// sum the normals
				normals.set(i2, Vector3.add(norm, normal));
				// log the index of the vertex for later reconstruction
				indices[1] = i2;
			}
			
			int i3 = findDuplicate(v3, vertices);
			if (i3 == -1) { // vertex doesn't exist yet
				// add new vertex
				vertices.add(v3);
				// add normal to list
				normals.add(normal);
				// log the index of the vertex for later reconstruction
				indices[2] = vertices.size() - 1;
			} else { // vertex already exists
				// get the normal
				Vector3 norm = normals.get(i3);
				// sum the normals
				normals.set(i3, Vector3.add(norm, normal));
				// log the index of the vertex for later reconstruction
				indices[2] = i3;
			}
			
			vertexInds[i] = indices;
		}
		
		for (int n = 0; n < normals.size(); n++) {
			Vector3 norm = normals.get(n);
			normals.set(n, Vector3.normalize(norm));
		}

		for (int i = 0; i < triangles.length; i++) {
			Triangle tri = triangles[i];
			int[] indices = vertexInds[i];
			Vector3 n1 = normals.get(indices[0]), n2 = normals.get(indices[1]), n3 = normals.get(indices[2]);
			tri.n[0] = n1;
			tri.n[1] = n2;
			tri.n[2] = n3;
		}
		
		return triangles;
	}
	
	// Project a list of triangles to screen view
	public static void projectTriangles(Triangle[] trianglesToRaster, Vector3 pos, Quaternion rot, Camera camera, Mat4x4 matView, Mat4x4 matProj, int WIDTH, int HEIGHT, EnvironmentLight light, Color color) {
		Mat4x4 matWorld = Quaternion.generateMatrix(rot, pos);
		Mat4x4 matRot = Quaternion.generateMatrix(rot, new Vector3());
		
		for (Triangle tri : trianglesToRaster) {
			//System.out.println(tri.p[0].x + " " + tri.p[0].y + " " + tri.p[0].z + " | " + tri.p[1].x + " " + tri.p[1].y + " " + tri.p[1].z + " | " + tri.p[2].x + " " + tri.p[2].y + " " + tri.p[2].z);
			
			Triangle triTransformed = Triangle.empty();
			Triangle triViewed = Triangle.empty();
			
			triTransformed.p = Triangle.multiplyMatrixTriangle(matWorld, tri);
			
			//System.out.println("Transformed" + triTransformed.p[0].x + " " + triTransformed.p[0].y + " " + triTransformed.p[0].z + " | " + triTransformed.p[1].x + " " + triTransformed.p[1].y + " " + triTransformed.p[1].z + " | " + triTransformed.p[2].x + " " + triTransformed.p[2].y + " " + triTransformed.p[2].z);
			
			triTransformed.t[0] = tri.t[0].copy();
			triTransformed.t[1] = tri.t[1].copy();
			triTransformed.t[2] = tri.t[2].copy();
			
			Vector3 normal = Triangle.findFaceNormal(triTransformed.p[0], triTransformed.p[1], triTransformed.p[2]);
			
			// Check if the triangle is facing towards the camera
			Vector3 vCameraRay = Vector3.subtract(triTransformed.p[0], camera.pos);
			//System.out.println(vCameraRay.x + " " + vCameraRay.y + " " + vCameraRay.z + " normal: " + normal.x + " " + normal.y + " " + normal.z);
			if (Vector3.dotProduct(normal, vCameraRay) < 0) {
				Vector3 lightDirection = light.getDirection();

				double[] dp = new double[3];
				boolean doNotGouraud = false;
				if (tri.n[0] == null || doGouraud == false) doNotGouraud = true;
				if (doNotGouraud) {
					dp[0] = Math.max(minimumBrightness, Vector3.dotProduct(lightDirection, normal));
				} else {
					for (int i = 0; i < 3; i++) {
						Vector3 vec1 = Vector3.mutiplyMatrixVector(matRot, tri.n[i]);
						dp[i] = Math.max(minimumBrightness, Vector3.dotProduct(lightDirection, vec1));
					}
				}
				
				if (lightingType == 0) {
					Color adjustedColor = EnvironmentLight.blend(light.color, color);
					if (doNotGouraud) {
						adjustedColor = EnvironmentLight.blend(adjustedColor, tri.color[0]);
						int red1	= (int) ((dp[0] * 255) * ((double) adjustedColor.getRed() / 255));
						int green1	= (int) ((dp[0] * 255) * ((double) adjustedColor.getGreen() / 255));
						int blue1	= (int) ((dp[0] * 255) * ((double) adjustedColor.getBlue() / 255));
						triViewed.brightness[0] = new Color(red1, green1, blue1);
						triViewed.brightness[1] = new Color(red1, green1, blue1);
						triViewed.brightness[2] = new Color(red1, green1, blue1);
					} else {
						for (int i = 0; i < 3; i++) {
							Color adjustedColor1 = EnvironmentLight.blend(adjustedColor, tri.color[i]);
							int red1	= (int) ((dp[i] * 255) * ((double) adjustedColor1.getRed() / 255));
							int green1	= (int) ((dp[i] * 255) * ((double) adjustedColor1.getGreen() / 255));
							int blue1	= (int) ((dp[i] * 255) * ((double) adjustedColor1.getBlue() / 255));
							triViewed.brightness[i] = new Color(red1, green1, blue1);
						}
					}
				} else {
					triViewed.brightness = tri.color;
				}
				
				triViewed.p = Triangle.multiplyMatrixTriangle(matView, triTransformed);
				
				//System.out.println("triViewed" + triViewed.p[0].x + " " + triViewed.p[0].y + " " + triViewed.p[0].z + " | " + triViewed.p[1].x + " " + triViewed.p[1].y + " " + triViewed.p[1].z + " | " + triViewed.p[2].x + " " + triViewed.p[2].y + " " + triViewed.p[2].z);
				
				triViewed.t[0] = triTransformed.t[0];
				triViewed.t[1] = triTransformed.t[1];
				triViewed.t[2] = triTransformed.t[2];
				
				Triangle[] clipped = Triangle.clipAgainstPlane(camera.clippingPlane, new Vector3(0, 0, 1), triViewed);
				int nClippedTriangles = 0; 
				if (clipped[0] != null) nClippedTriangles++;
				if (clipped[1] != null) nClippedTriangles++;
				
				for (int n = 0; n < nClippedTriangles; n++) {
					Triangle triProjected = Triangle.empty();
					triProjected.p = Triangle.multiplyMatrixTriangle(matProj, clipped[n]);
					
					triProjected.brightness = clipped[n].brightness;
					
					triProjected.t[0] = clipped[n].t[0];
					triProjected.t[1] = clipped[n].t[1];
					triProjected.t[2] = clipped[n].t[2];
					
					triProjected.t[0].u = triProjected.t[0].u / triProjected.p[0].w;
					triProjected.t[1].u = triProjected.t[1].u / triProjected.p[1].w;
					triProjected.t[2].u = triProjected.t[2].u / triProjected.p[2].w;
					
					triProjected.t[0].v = triProjected.t[0].v / triProjected.p[0].w;
					triProjected.t[1].v = triProjected.t[1].v / triProjected.p[1].w;
					triProjected.t[2].v = triProjected.t[2].v / triProjected.p[2].w;
					
					triProjected.t[0].w = 1 / triProjected.p[0].w;
					triProjected.t[1].w = 1 / triProjected.p[1].w;
					triProjected.t[2].w = 1 / triProjected.p[2].w;
					
					//System.out.println(triProjected.p[0].x + " " + triProjected.p[0].y + " | " + triProjected.p[1].x + " " + triProjected.p[1].y + " | " + triProjected.p[2].x + " " + triProjected.p[2].y);
					//System.out.println(triProjected.p[0].w + " " + triProjected.p[1].w + " " + triProjected.p[2].w);
	
					triProjected.p[0] = Vector3.divide(triProjected.p[0], triProjected.p[0].w);
					triProjected.p[1] = Vector3.divide(triProjected.p[1], triProjected.p[1].w);
					triProjected.p[2] = Vector3.divide(triProjected.p[2], triProjected.p[2].w);
	
					triProjected.p[0].x *= -1;
					triProjected.p[1].x *= -1;
					triProjected.p[2].x *= -1;
					triProjected.p[0].y *= -1;
					triProjected.p[1].y *= -1;
					triProjected.p[2].y *= -1;
	
					var offsetView = new Vector3(1, 1, 0);
					triProjected.p[0] = Vector3.add(triProjected.p[0], offsetView);
					triProjected.p[1] = Vector3.add(triProjected.p[1], offsetView);
					triProjected.p[2] = Vector3.add(triProjected.p[2], offsetView);
					triProjected.p[0].x *= 0.5 * WIDTH;
					triProjected.p[0].y *= 0.5 * HEIGHT;
					triProjected.p[1].x *= 0.5 * WIDTH;
					triProjected.p[1].y *= 0.5 * HEIGHT;
					triProjected.p[2].x *= 0.5 * WIDTH;
					triProjected.p[2].y *= 0.5 * HEIGHT;
					
					//System.out.println(triProjected.p[0].x + " " + triProjected.p[0].y + " | " + triProjected.p[1].x + " " + triProjected.p[1].y + " | " + triProjected.p[2].x + " " + triProjected.p[2].y);
					triangleRaster.add(triProjected);
				}
			}
		}
	}
	
	public static void cullScreenEdges(int WIDTH, int HEIGHT) {
		List<Triangle> allTriangles = new ArrayList<Triangle>();
		for (Triangle tri : triangleRaster) {
			List<Triangle> listTriangles = new ArrayList<Triangle>();
			
			listTriangles.add(tri);
			int nNewTriangles = 1;
			Triangle[] clipped = null;
			// Draw Triangles
			for (int p = 0; p < 4; p++) {
				//System.out.println(p);
				while (nNewTriangles > 0) {
					Triangle test = listTriangles.remove(0);
					nNewTriangles--;
					switch (p) {
						case 0: 
							clipped = Triangle.clipAgainstPlane(new Vector3(0, 0, 0), new Vector3(0, 1, 0), test); 
							break; //Top
						case 1: 
							clipped = Triangle.clipAgainstPlane(new Vector3(0, HEIGHT-1, 0), new Vector3(0, -1, 0), test); 
							break; //Bottom
						case 2: 
							clipped = Triangle.clipAgainstPlane(new Vector3(0, 0, 0), new Vector3(1, 0, 0), test); 
							break; //Right
						case 3: 
							clipped = Triangle.clipAgainstPlane(new Vector3(WIDTH-1, 0, 0), new Vector3(-1, 0, 0), test); 
							break; //Left
					}
					
					for (var w = 0; w < clipped.length; w++) {
						if (clipped[w] == null) continue;
						listTriangles.add(clipped[w]);
					}
				}
				nNewTriangles = listTriangles.size();
			}
			
			for (Triangle t : listTriangles) {
				allTriangles.add(t);
			}
		}
		triangleRaster = allTriangles;
	}
	
	public static void clearRaster() {
		triangleRaster.clear();
	}
	
	public static void drawTriangles(int[] imageBufferData, double[] pDepthBuffer, int WIDTH, int HEIGHT) {
		for (int i = 0; i < triangleRaster.size(); i++) {
			Triangle tri = triangleRaster.get(i);
			float[][] colors = new float[3][3];
			for (int j = 0; j < tri.brightness.length; j++) {
				Color c = tri.brightness[j];
				Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), colors[j]);
			}
			texturedTriangle(imageBufferData, pDepthBuffer, tri.p[0].x, tri.p[0].y, tri.t[0].u, tri.t[0].v, tri.t[0].w, 
															tri.p[1].x, tri.p[1].y, tri.t[1].u, tri.t[1].v, tri.t[1].w, 
															tri.p[2].x, tri.p[2].y, tri.t[2].u, tri.t[2].v, tri.t[2].w, 
															WIDTH, HEIGHT, colors[0], colors[1], colors[2]);
		}
	}
	
	private static void texturedTriangle(int[] imageBufferData, double[] pDepthBuffer, double x1, double y1, double u1, double v1, double w1, 
																					   double x2, double y2, double u2, double v2, double w2, 
																					   double x3, double y3, double u3, double v3, double w3, 
																					   int WIDTH, int HEIGHT, float[] color1, float[] color2, float[] color3) {
		x1 = (int) x1;
		y1 = (int) y1;
		x2 = (int) x2;
		y2 = (int) y2;
		x3 = (int) x3;
		y3 = (int) y3;
		
		// sort variables by y value: y0 <= y1 <= y2
		double temp;
		float[] tempc;
		if (y2 < y1) {
			temp = y1;
			y1 = y2;
			y2 = temp;

			temp = x1;
			x1 = x2;
			x2 = temp;

			temp = u1;
			u1 = u2;
			u2 = temp;

			temp = v1;
			v1 = v2;
			v2 = temp;

			temp = w1;
			w1 = w2;
			w2 = temp;
			
			tempc = color1;
			color1 = color2;
			color2 = tempc;
		}
		if (y3 < y1) {
			temp = y1;
			y1 = y3;
			y3 = temp;

			temp = x1;
			x1 = x3;
			x3 = temp;
			
			temp = u1;
			u1 = u3;
			u3 = temp;

			temp = v1;
			v1 = v3;
			v3 = temp;

			temp = w1;
			w1 = w3;
			w3 = temp;

			tempc = color1;
			color1 = color3;
			color3 = tempc;
		}
		if (y3 < y2) {
			temp = y2;
			y2 = y3;
			y3 = temp;

			temp = x2;
			x2 = x3;
			x3 = temp;

			temp = u2;
			u2 = u3;
			u3 = temp;

			temp = v2;
			v2 = v3;
			v3 = temp;

			temp = w2;
			w2 = w3;
			w3 = temp;
			
			tempc = color2;
			color2 = color3;
			color3 = tempc;
		}

		// find the slope components of the first line of triangle
		double dy1 = y2 - y1;
		double dx1 = x2 - x1;

		double dh1 = color2[0] - color1[0];
		double ds1 = color2[1] - color1[1];
		double dc1 = color2[2] - color1[2];
		
		double dv1 = v2 - v1;
		double du1 = u2 - u1;
		double dw1 = w2 - w1;
		
		// find the slope components of the second line of triangle
		double dy2 = y3 - y1;
		double dx2 = x3 - x1;

		double dh2 = color3[0] - color1[0];
		double ds2 = color3[1] - color1[1];
		double dc2 = color3[2] - color1[2];
		
		double dv2 = v3 - v1;
		double du2 = u3 - u1;
		double dw2 = w3 - w1;
		
		double c_h, c_c, c_s, tex_u, tex_v, tex_w;

		// step distances of the line
		double dax_step = 0, dbx_step = 0,
				du1_step = 0, dv1_step = 0,
				du2_step = 0, dv2_step = 0,
				dw1_step = 0, dw2_step = 0;
		
		if (dy1 != 0) {
			double absdy1 = Math.abs(dy1);
			dax_step = dx1 / absdy1;
			du1_step = du1 / absdy1;
			dv1_step = dv1 / absdy1;
			dw1_step = dw1 / absdy1;
		}

		if (dy2 != 0) {
			double absdy2 = Math.abs(dy2);
			dbx_step = dx2 / absdy2;
			du2_step = du2 / absdy2;
			dv2_step = dv2 / absdy2;
			dw2_step = dw2 / absdy2;
		}
		
		if (dy1 != 0) {
			for (int y = (int) y1; y <= (int) y2; y++) { // raws until it hits the flat bottom or middle of the triangle.
				// interpolate the x start and end values 
				double ax = (x1 + (y - y1) * dax_step);
				double bx = (x1 + (y - y1) * dbx_step);
				// start point
				double tex_su = u1 + (y - y1) * du1_step;
				double tex_sv = v1 + (y - y1) * dv1_step;
				double tex_sw = w1 + (y - y1) * dw1_step;
				// end point
				double tex_eu = u1 + (y - y1) * du2_step;
				double tex_ev = v1 + (y - y1) * dv2_step;
				double tex_ew = w1 + (y - y1) * dw2_step;
				// color
				double sh = ((y - y1) / dy1) * dh1 + color1[0];
				double eh = ((y - y1) / dy2) * dh2 + color1[0];
				double ss = ((y - y1) / dy1) * ds1 + color1[1];
				double es = ((y - y1) / dy2) * ds2 + color1[1];
				double sc = ((y - y1) / dy1) * dc1 + color1[2];
				double ec = ((y - y1) / dy2) * dc2 + color1[2];

				if (ax > bx) {
					double temp1 = ax;
					ax = bx;
					bx = temp1;
					
					temp1 = tex_su;
					tex_su = tex_eu;
					tex_eu = temp1;
					
					temp1 = tex_sv;
					tex_sv = tex_ev;
					tex_ev = temp1;
					
					temp1 = tex_sw;
					tex_sw = tex_ew;
					tex_ew = temp1;

					temp1 = ss;
					ss = es;
					es = temp1;

					temp1 = sh;
					sh = eh;
					eh = temp1;
					
					temp1 = sc;
					sc = ec;
					ec = temp1;
				}
				
				// always make tex_su come first
				c_h = sh;
				c_s = ss;
				c_c = sc;
				
				tex_u = tex_su;
				tex_v = tex_sv;
				tex_w = tex_sw;

				double tstep = 1 / (bx - ax);
				double t = 0;
				
				double xStart = ax;
				double xEnd = bx;

				for (int x = (int) xStart; x < (int) xEnd; x++) {
					if (y > 0 && y < HEIGHT && x > 0 && x < WIDTH) {
						c_h = (((x - xStart) / (xEnd - xStart)) * (eh - sh)) + sh;
						c_s = (((x - xStart) / (xEnd - xStart)) * (es - ss)) + ss;
						c_c = (((x - xStart) / (xEnd - xStart)) * (ec - sc)) + sc;
						
						tex_u = (1 - t) * tex_su + t * tex_eu;
						tex_v = (1 - t) * tex_sv + t * tex_ev;
						tex_w = (1 - t) * tex_sw + t * tex_ew;

						int d = (int) ((y * WIDTH) + x);
						if (tex_w > pDepthBuffer[d]) {
							imageBufferData[d] = Color.HSBtoRGB((float) c_h, (float) c_s, (float) c_c);
							pDepthBuffer[d] = tex_w;
						}
						t += tstep;
					}
				}
			}
		}
		
		dy1 = y3 - y2;
		dx1 = x3 - x2;

		dh1 = color3[0] - color2[0];
		ds1 = color3[1] - color2[1];
		dc1 = color3[2] - color2[2];
		
		dv1 = v3 - v2;
		du1 = u3 - u2;
		dw1 = w3 - w2;
		
		du1_step = 0; 
		dv1_step = 0;

		if (dy1 != 0) {
			double absdy1 = Math.abs(dy1);
			dax_step = dx1 / absdy1;
			du1_step = du1 / absdy1;
			dv1_step = dv1 / absdy1;
			dw1_step = dw1 / absdy1;
		}
		
		if (dy2 != 0) dbx_step = dx2 / Math.abs(dy2);

		if (dy1 != 0) {
			for (int y = (int) y2; y < (int) y3; y++) { // raws until it hits the flat top or middle of the triangle.
				// interpolate the x start and end values 
				double ax = (x2 + (y - y2) * dax_step);
				double bx = (x1 + (y - y1) * dbx_step);
				// start point
				double tex_su = u2 + (y - y2) * du1_step;
				double tex_sv = v2 + (y - y2) * dv1_step;
				double tex_sw = w2 + (y - y2) * dw1_step;
				// end point
				double tex_eu = u1 + (y - y1) * du2_step;
				double tex_ev = v1 + (y - y1) * dv2_step;
				double tex_ew = w1 + (y - y1) * dw2_step;
				// color
				double sh = ((y - y2) / dy1) * dh1 + color2[0];
				double eh = ((y - y1) / dy2) * dh2 + color1[0];
				double ss = ((y - y2) / dy1) * ds1 + color2[1];
				double es = ((y - y1) / dy2) * ds2 + color1[1];
				double sc = ((y - y2) / dy1) * dc1 + color2[2];
				double ec = ((y - y1) / dy2) * dc2 + color1[2];
				
				if (ax > bx) {
					double temp1 = ax;
					ax = bx;
					bx = temp1;
					
					temp1 = tex_su;
					tex_su = tex_eu;
					tex_eu = temp1;
					
					temp1 = tex_sv;
					tex_sv = tex_ev;
					tex_ev = temp1;
					
					temp1 = tex_sw;
					tex_sw = tex_ew;
					tex_ew = temp1;

					temp1 = ss;
					ss = es;
					es = temp1;

					temp1 = sh;
					sh = eh;
					eh = temp1;
					
					temp1 = sc;
					sc = ec;
					ec = temp1;
				}

				c_h = sh;
				c_s = ss;
				c_c = sc;	
				
				tex_u = tex_su;
				tex_v = tex_sv;
				tex_w = tex_sw;

				double tstep = 1 / (double) (bx - ax);
				double t = 0;
				
				double xStart = ax;
				double xEnd = bx;
				
				for (int x = (int) xStart; x < (int) xEnd; x++) {
					if (y > 0 && y < HEIGHT && x > 0 && x < WIDTH) {
						c_h = (((x - xStart) / (xEnd - xStart)) * (eh - sh)) + sh;
						c_s = (((x - xStart) / (xEnd - xStart)) * (es - ss)) + ss;
						c_c = (((x - xStart) / (xEnd - xStart)) * (ec - sc)) + sc;
						
						tex_u = (1 - t) * tex_su + t * tex_eu;
						tex_v = (1 - t) * tex_sv + t * tex_ev;
						tex_w = (1 - t) * tex_sw + t * tex_ew;
						int d = (int) ((y * WIDTH) + x);
						if (tex_w > pDepthBuffer[d]) {
							imageBufferData[d] = Color.HSBtoRGB((float) c_h, (float) c_s, (float) c_c);
							pDepthBuffer[d] = tex_w;
						}
						t += tstep;
					}
				}
			}
		}
		
		if (drawOutlines) {
			int outline = Color.BLACK.getRGB();
			Line.drawline(imageBufferData, (int) x1, (int) y1, (int) x2, (int) y2, WIDTH, outline);
			Line.drawline(imageBufferData, (int) x1, (int) y1, (int) x3, (int) y3, WIDTH, outline);
			Line.drawline(imageBufferData, (int) x2, (int) y2, (int) x3, (int) y3, WIDTH, outline);
		}
	}
}
