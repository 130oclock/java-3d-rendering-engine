package engine.triangle;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import engine.camera.Camera;
import engine.light.EnvironmentLight;
import engine.matrix.Mat4x4;
import engine.quaternion.Quaternion;
import engine.vector.Vector2d;
import engine.vector.Vector3d;

public class Triangle {
	
	private static boolean showClipping = false;
	private static int lightingType = 0;
	public static boolean doGouraud = true;
	private static double minimumBrightness = 0.1;
	
	public static List<Triangle> triangleRaster = new ArrayList<Triangle>();
	
	private Vector3d[] p = new Vector3d[3];
	private Vector3d[] n = new Vector3d[3];
	
	private Vector2d[] t = new Vector2d[3];
	
	private Color[] brightness = new Color[3];
	public Color color;
	
	public Triangle(Vector3d p0, Vector3d p1, Vector3d p2, Vector2d t0, Vector2d t1, Vector2d t2, Vector3d n0, Vector3d n1, Vector3d n2) {
		this.p[0] = p0;
		this.p[1] = p1;
		this.p[2] = p2;

		this.n[0] = n0;
		this.n[1] = n1;
		this.n[2] = n2;
		
		this.t[0] = t0;
		this.t[1] = t1;
		this.t[2] = t2;
		
		this.color = new Color(255, 255, 255);
	}
	
	public Triangle(double[] v1, double[] v2, double[] v3) {
		this.p[0] = new Vector3d(v1[0], v1[1], v1[2]);
		this.p[1] = new Vector3d(v2[0], v2[1], v2[2]);
		this.p[2] = new Vector3d(v3[0], v3[1], v3[2]);
		
		this.n[0] = null;
		this.n[1] = null;
		this.n[2] = null;
		
		this.t[0] = new Vector2d(0, 0);
		this.t[1] = new Vector2d(0, 0);
		this.t[2] = new Vector2d(0, 0);
		
		this.color = new Color(255, 255, 255);
	}
	public Triangle(double[] v1, double[] v2, double[] v3, double[] vt1, double[] vt2, double[] vt3) {
		this.p[0] = new Vector3d(v1[0], v1[1], v1[2]);
		this.p[1] = new Vector3d(v2[0], v2[1], v2[2]);
		this.p[2] = new Vector3d(v3[0], v3[1], v3[2]);
		
		this.n[0] = null;
		this.n[1] = null;
		this.n[2] = null;
		
		this.t[0] = new Vector2d(vt1[0], vt1[1]);
		this.t[1] = new Vector2d(vt2[0], vt2[1]);
		this.t[2] = new Vector2d(vt3[0], vt3[1]);
		
		this.color = new Color(255, 255, 255);
	}
	public Triangle(double[] v1, double[] v2, double[] v3, double[] vt1, double[] vt2, double[] vt3, double[] vn1, double[] vn2, double[] vn3) {
		this.p[0] = new Vector3d(v1[0], v1[1], v1[2]);
		this.p[1] = new Vector3d(v2[0], v2[1], v2[2]);
		this.p[2] = new Vector3d(v3[0], v3[1], v3[2]);
		
		this.n[0] = new Vector3d(vn1[0], vn1[1], vn1[2]);
		this.n[1] = new Vector3d(vn2[0], vn2[1], vn2[2]);
		this.n[2] = new Vector3d(vn3[0], vn3[1], vn3[2]);
		
		if (vt1 == null) {
			this.t[0] = new Vector2d(0, 0);
			this.t[1] = new Vector2d(0, 0);
			this.t[2] = new Vector2d(0, 0);
		} else {
			this.t[0] = new Vector2d(vt1[0], vt1[1]);
			this.t[1] = new Vector2d(vt2[0], vt2[1]);
			this.t[2] = new Vector2d(vt3[0], vt3[1]);
		}
		
		this.color = new Color(255, 255, 255);
	}
	
	// Create an empty Triangle
	public static Triangle empty() {
		Vector3d v1 = new Vector3d(0, 0, 0);
		Vector3d v2 = new Vector3d(0, 0, 0);
		Vector3d v3 = new Vector3d(0, 0, 0);
		Vector2d v4 = new Vector2d(0, 0);
		Vector2d v5 = new Vector2d(0, 0);
		Vector2d v6 = new Vector2d(0, 0);
		return new Triangle(v1, v2, v3, v4, v5, v6, null, null, null);
	}
	
	// Return the nth point on the triangle
	public Vector3d getVector(int n) {
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
		return copy;
	}
	
	// Multiply a Triangle by a matrix
	public static Vector3d[] multiplyMatrixTriangle(Mat4x4 mat, Triangle tri) {
		Vector3d vec1 = Vector3d.mutiplyMatrixVector(mat, tri.p[0]);
		Vector3d vec2 = Vector3d.mutiplyMatrixVector(mat, tri.p[1]);
		Vector3d vec3 = Vector3d.mutiplyMatrixVector(mat, tri.p[2]);
		
		return new Vector3d[] { vec1, vec2, vec3 };
	}
	
	private static double signedDist(Vector3d nPlane, Vector3d pPlane, Vector3d p) {
		return (nPlane.x * p.x + nPlane.y * p.y + nPlane.z * p.z - Vector3d.dotProduct(nPlane, pPlane));
	}
	
	public static Triangle[] clipAgainstPlane(Vector3d pPlane, Vector3d nPlane, Triangle tri) {
		nPlane = Vector3d.normalize(nPlane);
		
		Vector3d[] insidePoints = new Vector3d[3];		int nInsidePoints = 0;
		Vector3d[] outsidePoints = new Vector3d[3];		int nOutsidePoints = 0;
		Vector3d[] insideNorm = new Vector3d[3];		int nInsideNorm = 0;
		Vector3d[] outsideNorm = new Vector3d[3];		int nOutsideNorm = 0;
		Vector2d[] insideTex = new Vector2d[3];			int nInsideTex = 0;
		Vector2d[] outsideTex = new Vector2d[3];		int nOutsideTex = 0;
		
		double d0 = Triangle.signedDist(nPlane, pPlane, tri.p[0]);
		double d1 = Triangle.signedDist(nPlane, pPlane, tri.p[1]);
		double d2 = Triangle.signedDist(nPlane, pPlane, tri.p[2]);
		
		if (d0 >= 0) {
			insidePoints[nInsidePoints++] = tri.p[0];
			insideNorm[nInsideNorm++] = tri.n[0];
			insideTex[nInsideTex++] = tri.t[0];
		} else {
			outsidePoints[nOutsidePoints++] = tri.p[0];
			outsideNorm[nOutsideNorm++] = tri.n[0];
			outsideTex[nOutsideTex++] = tri.t[0];
		}
		if (d1 >= 0) {
			insidePoints[nInsidePoints++] = tri.p[1];
			insideNorm[nInsideNorm++] = tri.n[1];
			insideTex[nInsideTex++] = tri.t[1];
		} else {
			outsidePoints[nOutsidePoints++] = tri.p[1];
			outsideNorm[nOutsideNorm++] = tri.n[1];
			outsideTex[nOutsideTex++] = tri.t[1];
		}
		if (d2 >= 0) {
			insidePoints[nInsidePoints++] = tri.p[2];
			insideNorm[nInsideNorm++] = tri.n[2];
			insideTex[nInsideTex++] = tri.t[2];
		} else {
			outsidePoints[nOutsidePoints++] = tri.p[2];
			outsideNorm[nOutsideNorm++] = tri.n[2];
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
			if (showClipping == true) {
				otri1.brightness[0] = Color.BLUE;
				otri1.brightness[1] = Color.BLUE;
				otri1.brightness[2] = Color.BLUE;
			} else otri1.brightness = tri.brightness;
			
			// A
			otri1.p[0] = insidePoints[0];
			otri1.n[0] = insideNorm[0];
			otri1.t[0] = insideTex[0];

			otri1.n[1] = insideNorm[1];
			otri1.n[2] = insideNorm[2];
			
			// intersection of AB = B'
			double t1 = Vector3d.intersectPlaneDouble(pPlane, nPlane, insidePoints[0], outsidePoints[0]);
			otri1.p[1] = Vector3d.intersectPlaneVector3d(insidePoints[0], outsidePoints[0], t1);
			otri1.t[1].u = t1 * (outsideTex[0].u - insideTex[0].u) + insideTex[0].u;
			otri1.t[1].v = t1 * (outsideTex[0].v - insideTex[0].v) + insideTex[0].v;
			otri1.t[1].w = t1 * (outsideTex[0].w - insideTex[0].w) + insideTex[0].w;
			
			// intersection of AC = C'
			double t2 = Vector3d.intersectPlaneDouble(pPlane, nPlane, insidePoints[0], outsidePoints[1]);
			otri1.p[2] = Vector3d.intersectPlaneVector3d(insidePoints[0], outsidePoints[1], t2);
			otri1.t[2].u = t2 * (outsideTex[1].u - insideTex[0].u) + insideTex[0].u;
			otri1.t[2].v = t2 * (outsideTex[1].v - insideTex[0].v) + insideTex[0].v;
			otri1.t[2].w = t2 * (outsideTex[1].w - insideTex[0].w) + insideTex[0].w;
			
			return new Triangle[] { otri1, null }; // Triangle AB'C'
		}
		// if two points are on the screen and one is not on the screen
		if (nInsidePoints == 2 && nOutsidePoints == 1) { // A, B ; C
			Triangle otri1 = Triangle.empty(), otri2 = Triangle.empty();
			if (showClipping == true) {
				otri1.brightness[0] = Color.YELLOW;
				otri1.brightness[1] = Color.YELLOW;
				otri1.brightness[2] = Color.YELLOW;
			} else otri1.brightness = tri.brightness;

			if (showClipping == true) {
				otri2.brightness[0] = Color.GREEN;
				otri2.brightness[1] = Color.GREEN;
				otri2.brightness[2] = Color.GREEN;
			} else otri2.brightness = tri.brightness;
			
			// A
			otri1.p[0] = insidePoints[0];
			otri1.n[0] = insideNorm[0];
			otri1.t[0] = insideTex[0];
			
			// B
			otri1.p[1] = insidePoints[1];
			otri1.n[1] = insideNorm[1];
			otri1.t[1] = insideTex[1];
			
			otri1.n[2] = insideNorm[2];
			
			// intersection of AC = A'
			double t1 = Vector3d.intersectPlaneDouble(pPlane, nPlane, insidePoints[0], outsidePoints[0]);
			otri1.p[2] = Vector3d.intersectPlaneVector3d(insidePoints[0], outsidePoints[0], t1);
			otri1.t[2].u = t1 * (outsideTex[0].u - insideTex[0].u) + insideTex[0].u;
			otri1.t[2].v = t1 * (outsideTex[0].v - insideTex[0].v) + insideTex[0].v;
			otri1.t[2].w = t1 * (outsideTex[0].w - insideTex[0].w) + insideTex[0].w;
			
			// A'
			otri2.p[0] = otri1.p[2];
			otri2.t[0] = otri1.t[2];
			
			// B
			otri2.p[1] = insidePoints[1];
			otri2.t[1] = insideTex[1];
			
			
			// intersection of BC = B'
			double t2 = Vector3d.intersectPlaneDouble(pPlane, nPlane, insidePoints[1], outsidePoints[0]);
			otri2.p[2] = Vector3d.intersectPlaneVector3d(insidePoints[1], outsidePoints[0], t2);
			otri2.t[2].u = t2 * (outsideTex[0].u - insideTex[1].u) + insideTex[1].u;
			otri2.t[2].v = t2 * (outsideTex[0].v - insideTex[1].v) + insideTex[1].v;
			otri2.t[2].w = t2 * (outsideTex[0].w - insideTex[1].w) + insideTex[1].w;
			
			return new Triangle[] { otri1, otri2 }; // Triangle ABA' and A'BB'
		}
		return null;
	}
	
	public static Vector3d findFaceNormal(Vector3d v1, Vector3d v2, Vector3d v3) {
		Vector3d normal, line1, line2;
		line1 = Vector3d.subtract(v2, v1);
		line2 = Vector3d.subtract(v3, v1);
		//System.out.println(line1.x + " " + line1.y + " " + line1.z + " | " + line2.x + " " + line2.y + " " + line2.z);
		
		normal = Vector3d.crossProduct(line1, line2);
		//System.out.println(normal.x + " " + normal.y + " " + normal.z);
		
		return Vector3d.normalize(normal);
	}
	
	// Project a list of triangles to screen view
	public static void projectTriangles(Triangle[] trianglesToRaster, Vector3d pos, Quaternion rot, Camera camera, Mat4x4 matView, Mat4x4 matProj, int WIDTH, int HEIGHT, EnvironmentLight light) {
		Mat4x4 matWorld = Quaternion.generateMatrix(rot, pos);
		Mat4x4 matRot = Quaternion.generateMatrix(rot, Vector3d.empty());
		
		for (Triangle tri : trianglesToRaster) {
			//System.out.println(tri.p[0].x + " " + tri.p[0].y + " " + tri.p[0].z + " | " + tri.p[1].x + " " + tri.p[1].y + " " + tri.p[1].z + " | " + tri.p[2].x + " " + tri.p[2].y + " " + tri.p[2].z);
			
			Triangle triTransformed = Triangle.empty();
			Triangle triViewed = Triangle.empty();
			
			triTransformed.p = Triangle.multiplyMatrixTriangle(matWorld, tri);
			
			//System.out.println("Transformed" + triTransformed.p[0].x + " " + triTransformed.p[0].y + " " + triTransformed.p[0].z + " | " + triTransformed.p[1].x + " " + triTransformed.p[1].y + " " + triTransformed.p[1].z + " | " + triTransformed.p[2].x + " " + triTransformed.p[2].y + " " + triTransformed.p[2].z);
			
			triTransformed.t[0] = tri.t[0];
			triTransformed.t[1] = tri.t[1];
			triTransformed.t[2] = tri.t[2];
			
			Vector3d normal = Triangle.findFaceNormal(triTransformed.p[0], triTransformed.p[1], triTransformed.p[2]);
			
			// Check if the triangle is facing towards the camera
			Vector3d vCameraRay = Vector3d.subtract(triTransformed.p[0], camera.pos);
			//System.out.println(vCameraRay.x + " " + vCameraRay.y + " " + vCameraRay.z + " normal: " + normal.x + " " + normal.y + " " + normal.z);
			if (Vector3d.dotProduct(normal, vCameraRay) < 0) {
				Vector3d lightDirection = light.getDirection();

				double dp1, dp2, dp3;
				boolean doNotGouraud = false;
				if (tri.n[0] == null || doGouraud == false) doNotGouraud = true;
				if (doNotGouraud) {
					dp1 = Math.max(minimumBrightness, Vector3d.dotProduct(lightDirection, normal));
					dp2 = dp1;
					dp3 = dp1;
				} else {
					Vector3d vec1 = Vector3d.mutiplyMatrixVector(matRot, tri.n[0]);
					Vector3d vec2 = Vector3d.mutiplyMatrixVector(matRot, tri.n[1]);
					Vector3d vec3 = Vector3d.mutiplyMatrixVector(matRot, tri.n[2]);
					
					dp1 = Math.max(minimumBrightness, Vector3d.dotProduct(lightDirection, vec1));
					dp2 = Math.max(minimumBrightness, Vector3d.dotProduct(lightDirection, vec2));
					dp3 = Math.max(minimumBrightness, Vector3d.dotProduct(lightDirection, vec3));
				}
				
				if (lightingType == 0) {
					Color adjustedColor = EnvironmentLight.blend(light.color, tri.color);
					if (doNotGouraud) {
						int red1 = (int) ((dp1 * 255) * (adjustedColor.getRed() / 255));
						int green1 = (int) ((dp1 * 255) * (adjustedColor.getGreen() / 255));
						int blue1 = (int) ((dp1 * 255) * (adjustedColor.getBlue() / 255));
						triViewed.brightness[0] = new Color(red1, green1, blue1);
						triViewed.brightness[1] = new Color(red1, green1, blue1);
						triViewed.brightness[2] = new Color(red1, green1, blue1);
					} else {
						int red1 = (int) ((dp1 * 255) * (adjustedColor.getRed() / 255));
						int green1 = (int) ((dp1 * 255) * (adjustedColor.getGreen() / 255));
						int blue1 = (int) ((dp1 * 255) * (adjustedColor.getBlue() / 255));
						triViewed.brightness[0] = new Color(red1, green1, blue1);
	
						int red2 = (int) ((dp2 * 255) * (adjustedColor.getRed() / 255));
						int green2 = (int) ((dp2 * 255) * (adjustedColor.getGreen() / 255));
						int blue2 = (int) ((dp2 * 255) * (adjustedColor.getBlue() / 255));
						triViewed.brightness[1] = new Color(red2, green2, blue2);
	
						int red3 = (int) ((dp3 * 255) * (adjustedColor.getRed() / 255));
						int green3 = (int) ((dp3 * 255) * (adjustedColor.getGreen() / 255));
						int blue3 = (int) ((dp3 * 255) * (adjustedColor.getBlue() / 255));
						triViewed.brightness[2] = new Color(red3, green3, blue3);
					}
				} else {
					triViewed.brightness[0] = tri.color;
					triViewed.brightness[1] = tri.color;
					triViewed.brightness[2] = tri.color;
				}
				
				triViewed.p = Triangle.multiplyMatrixTriangle(matView, triTransformed);
				
				//System.out.println("triViewed" + triViewed.p[0].x + " " + triViewed.p[0].y + " " + triViewed.p[0].z + " | " + triViewed.p[1].x + " " + triViewed.p[1].y + " " + triViewed.p[1].z + " | " + triViewed.p[2].x + " " + triViewed.p[2].y + " " + triViewed.p[2].z);
				
				triViewed.t[0] = triTransformed.t[0];
				triViewed.t[1] = triTransformed.t[1];
				triViewed.t[2] = triTransformed.t[2];
				
				Triangle[] clipped = Triangle.clipAgainstPlane(camera.clippingPlane, new Vector3d(0, 0, 1), triViewed);
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
	
					triProjected.p[0] = Vector3d.divide(triProjected.p[0], triProjected.p[0].w);
					triProjected.p[1] = Vector3d.divide(triProjected.p[1], triProjected.p[1].w);
					triProjected.p[2] = Vector3d.divide(triProjected.p[2], triProjected.p[2].w);
	
					triProjected.p[0].x *= -1;
					triProjected.p[1].x *= -1;
					triProjected.p[2].x *= -1;
					triProjected.p[0].y *= -1;
					triProjected.p[1].y *= -1;
					triProjected.p[2].y *= -1;
	
					var offsetView = new Vector3d(1, 1, 0);
					triProjected.p[0] = Vector3d.add(triProjected.p[0], offsetView);
					triProjected.p[1] = Vector3d.add(triProjected.p[1], offsetView);
					triProjected.p[2] = Vector3d.add(triProjected.p[2], offsetView);
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
							clipped = Triangle.clipAgainstPlane(new Vector3d(0, 0, 0), new Vector3d(0, 1, 0), test); 
							break; //Top
						case 1: 
							clipped = Triangle.clipAgainstPlane(new Vector3d(0, HEIGHT-1, 0), new Vector3d(0, -1, 0), test); 
							break; //Bottom
						case 2: 
							clipped = Triangle.clipAgainstPlane(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0), test); 
							break; //Right
						case 3: 
							clipped = Triangle.clipAgainstPlane(new Vector3d(WIDTH-1, 0, 0), new Vector3d(-1, 0, 0), test); 
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
		// sort variables by y value: y0 <= y1 <= y2
		
		double maxX = Math.min(WIDTH, Math.max(x1, Math.max(x2, x3)));
		double minX = Math.max(0, Math.min(x1, Math.min(x2, x3)));
			
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
		
		//System.out.println("color1 " + color1[0] + " " + color1[1] + " " + color1[2] + " color2 " + color2[0] + " " + color2[1] + " " + color2[2] + " color3 " + color3[0] + " " + color3[1] + " " + color3[2]);
		
		//System.out.print("Tri " + color + " : " + y0 + " " + y1 + " " + y2);

		// find the slope components of the first line of triangle
		double dy1 = y2 - y1;
		double dx1 = x2 - x1;
		
		double dc1 = color2[2] - color1[2];
		
		double dv1 = v2 - v1;
		double du1 = u2 - u1;
		double dw1 = w2 - w1;
		
		// find the slope components of the second line of triangle
		double dy2 = y3 - y1;
		double dx2 = x3 - x1;
		
		double dc2 = color3[2] - color1[2];
		
		double dv2 = v3 - v1;
		double du2 = u3 - u1;
		double dw2 = w3 - w1;
		
		double c_c, tex_u, tex_v, tex_w;

		// step distances of the line
		double dax_step = 0, dbx_step = 0,
				du1_step = 0, dv1_step = 0,
				du2_step = 0, dv2_step = 0,
				dw1_step = 0, dw2_step = 0;

		if (dy1 != 0) dax_step = dx1 / Math.abs(dy1);
		if (dy2 != 0) dbx_step = dx2 / Math.abs(dy2);
	
		if (dy1 != 0) {
			double absdy1 = Math.abs(dy1);
			du1_step = du1 / absdy1;
			dv1_step = dv1 / absdy1;
			dw1_step = dw1 / absdy1;
		}

		if (dy2 != 0) {
			double absdy2 = Math.abs(dy2);
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
					
					temp1 = sc;
					sc = ec;
					ec = temp1;
				}
				
				// always make tex_su come first
				c_c = sc;
				
				tex_u = tex_su;
				tex_v = tex_sv;
				tex_w = tex_sw;

				double tstep = 1 / (bx - ax);
				double t = 0;
				
				double xStart = Math.max(minX, ax);
				double xEnd = Math.min(bx, maxX);

				for (int x = (int) xStart; x < (int) xEnd; x++) {
					if (y > 0 && y < HEIGHT) {
						c_c = (((x - xStart) / (xEnd - xStart)) * (ec - sc)) + sc;
						
						tex_u = (1 - t) * tex_su + t * tex_eu;
						tex_v = (1 - t) * tex_sv + t * tex_ev;
						tex_w = (1 - t) * tex_sw + t * tex_ew;

						int d = (int) ((y * WIDTH) + x);
						if (tex_w > pDepthBuffer[d]) {
							imageBufferData[d] = Color.HSBtoRGB(color1[0], color1[1], (float) c_c);
							pDepthBuffer[d] = tex_w;
						}
						t += tstep;
					}
				}
			}
		}
		
		dy1 = y3 - y2;
		dx1 = x3 - x2;
		
		dc1 = color3[2] - color2[2];
		
		dv1 = v3 - v2;
		du1 = u3 - u2;
		dw1 = w3 - w2;
		
		if (dy1 != 0) dax_step = dx1 / Math.abs(dy1);
		if (dy2 != 0) dbx_step = dx2 / Math.abs(dy2);

		du1_step = 0; 
		dv1_step = 0;

		if (dy1 != 0) {
			double absdy1 = Math.abs(dy1);
			du1_step = du1 / absdy1;
			dv1_step = dv1 / absdy1;
			dw1_step = dw1 / absdy1;
		}

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

					temp1 = sc;
					sc = ec;
					ec = temp1;
				}

				c_c = sc;	
				
				tex_u = tex_su;
				tex_v = tex_sv;
				tex_w = tex_sw;

				double tstep = 1 / (double) (bx - ax);
				double t = 0;
				
				double xStart = Math.max(minX, ax);
				double xEnd = Math.min(bx, maxX);
				
				for (int x = (int) xStart; x < (int) xEnd; x++) {
					if (y > 0 && y < HEIGHT) {
						c_c = (((x - xStart) / (xEnd - xStart)) * (ec - sc)) + sc;
						
						tex_u = (1 - t) * tex_su + t * tex_eu;
						tex_v = (1 - t) * tex_sv + t * tex_ev;
						tex_w = (1 - t) * tex_sw + t * tex_ew;
						int d = (int) ((y * WIDTH) + x);
						if (tex_w > pDepthBuffer[d]) {
							imageBufferData[d] = Color.HSBtoRGB(color1[0], color1[1], (float) c_c);
							pDepthBuffer[d] = tex_w;
						}
						t += tstep;
					}
				}
			}
		}
	}
}
