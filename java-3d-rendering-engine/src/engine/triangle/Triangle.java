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
	
	private static int drawType = 0;
	private static boolean showW = false;
	private static boolean showClipping = false;
	private static boolean doLighting = true;
	
	public static List<Triangle> triangleRaster = new ArrayList<Triangle>();
	
	private Vector3d[] p = new Vector3d[3];
	private Vector3d normal;
	
	private Vector2d[] t = new Vector2d[3];
	
	private Color brightness;
	public Color color;
	
	public Triangle(Vector3d p0, Vector3d p1, Vector3d p2, Vector2d t0, Vector2d t1, Vector2d t2, Vector3d normal) {
		this.p[0] = p0;
		this.p[1] = p1;
		this.p[2] = p2;

		this.normal = normal;
		
		this.t[0] = t0;
		this.t[1] = t1;
		this.t[2] = t2;
		
		this.color = new Color(255, 255, 255);
	}
	
	// Create an empty Triangle
	public static Triangle empty() {
		Vector3d v1 = new Vector3d(0, 0, 0);
		Vector3d v2 = new Vector3d(0, 0, 0);
		Vector3d v3 = new Vector3d(0, 0, 0);
		Vector3d normal = null;
		Vector2d v4 = new Vector2d(0, 0);
		Vector2d v5 = new Vector2d(0, 0);
		Vector2d v6 = new Vector2d(0, 0);
		return new Triangle(v1, v2, v3, v4, v5, v6, normal);
	}
	
	// Return the nth point on the triangle
	public Vector3d getVector(int n) {
		return this.p[n].copy();
	}
	
	// Make a duplicate object
	public Triangle copy() {
		Triangle copy = new Triangle(this.p[0].copy(), this.p[1].copy(), this.p[2].copy(), this.t[0].copy(), this.t[1].copy(), this.t[2].copy(), this.normal);
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
		Vector2d[] insideTex = new Vector2d[3];			int nInsideTex = 0;
		Vector2d[] outsideTex = new Vector2d[3];		int nOutsideTex = 0;
		
		double d0 = Triangle.signedDist(nPlane, pPlane, tri.p[0]);
		double d1 = Triangle.signedDist(nPlane, pPlane, tri.p[1]);
		double d2 = Triangle.signedDist(nPlane, pPlane, tri.p[2]);
		
		if (d0 >= 0) {
			insidePoints[nInsidePoints++] = tri.p[0];
			insideTex[nInsideTex++] = tri.t[0];
		} else {
			outsidePoints[nOutsidePoints++] = tri.p[0];
			outsideTex[nOutsideTex++] = tri.t[0];
		}
		if (d1 >= 0) {
			insidePoints[nInsidePoints++] = tri.p[1];
			insideTex[nInsideTex++] = tri.t[1];
		} else {
			outsidePoints[nOutsidePoints++] = tri.p[1];
			outsideTex[nOutsideTex++] = tri.t[1];
		}
		if (d2 >= 0) {
			insidePoints[nInsidePoints++] = tri.p[2];
			insideTex[nInsideTex++] = tri.t[2];
		} else {
			outsidePoints[nOutsidePoints++] = tri.p[2];
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
			if (showClipping == true) otri1.brightness = Color.BLUE;
			else otri1.brightness = tri.brightness;
			
			// A
			otri1.p[0] = insidePoints[0];
			otri1.t[0] = insideTex[0];
			
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
			if (showClipping == true) otri1.brightness = Color.YELLOW;
			else otri1.brightness = tri.brightness;

			if (showClipping == true) otri2.brightness = Color.GREEN;
			else otri2.brightness = tri.brightness;
			
			// A
			otri1.p[0] = insidePoints[0];
			otri1.t[0] = insideTex[0];
			
			// B
			otri1.p[1] = insidePoints[1];
			otri1.t[1] = insideTex[1];
			
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
	
	// Project a list of triangles to screen view
	public static void projectTriangles(Triangle[] trianglesToRaster, Vector3d pos, Quaternion rot, Camera camera, Mat4x4 matView, Mat4x4 matProj, int WIDTH, int HEIGHT, EnvironmentLight light) {
		Mat4x4 matWorld = Quaternion.generateMatrix(rot, pos);
		
		for (Triangle tri : trianglesToRaster) {
			//System.out.println(tri.p[0].x + " " + tri.p[0].y + " " + tri.p[0].z + " | " + tri.p[1].x + " " + tri.p[1].y + " " + tri.p[1].z + " | " + tri.p[2].x + " " + tri.p[2].y + " " + tri.p[2].z);
			
			Triangle triTransformed = Triangle.empty();
			Triangle triViewed = Triangle.empty();
			
			triTransformed.p = Triangle.multiplyMatrixTriangle(matWorld, tri);
			
			//System.out.println("Transformed" + triTransformed.p[0].x + " " + triTransformed.p[0].y + " " + triTransformed.p[0].z + " | " + triTransformed.p[1].x + " " + triTransformed.p[1].y + " " + triTransformed.p[1].z + " | " + triTransformed.p[2].x + " " + triTransformed.p[2].y + " " + triTransformed.p[2].z);
			
			triTransformed.t[0] = tri.t[0];
			triTransformed.t[1] = tri.t[1];
			triTransformed.t[2] = tri.t[2];
			
			Vector3d normal, line1, line2;
			if (tri.normal == null) {
				line1 = Vector3d.subtract(triTransformed.p[1], triTransformed.p[0]);
				line2 = Vector3d.subtract(triTransformed.p[2], triTransformed.p[0]);
				//System.out.println(line1.x + " " + line1.y + " " + line1.z + " | " + line2.x + " " + line2.y + " " + line2.z);
				
				normal = Vector3d.crossProduct(line1, line2);
				//System.out.println(normal.x + " " + normal.y + " " + normal.z);
				
				normal = Vector3d.normalize(normal);
			} else {
				normal = tri.normal;
			}
			
			// Check if the triangle is facing towards the camera
			Vector3d vCameraRay = Vector3d.subtract(triTransformed.p[0], camera.pos);
			//System.out.println(vCameraRay.x + " " + vCameraRay.y + " " + vCameraRay.z + " normal: " + normal.x + " " + normal.y + " " + normal.z);
			if (Vector3d.dotProduct(normal, vCameraRay) < 0) {
				Vector3d lightDirection = light.getDirection();
				
				double dp = Math.max(0.1, Vector3d.dotProduct(lightDirection, normal));
				
				if (doLighting == true) {
					Color adjustedColor = EnvironmentLight.blend(light.color, tri.color);
					int red = (int) ((dp * 255) * (adjustedColor.getRed() / 255));
					int green = (int) ((dp * 255) * (adjustedColor.getGreen() / 255));
					int blue = (int) ((dp * 255) * (adjustedColor.getBlue() / 255));
					//System.out.println(red + " " + green + " " + blue);
					triViewed.brightness = new Color(red, green, blue);
				} else {
					triViewed.brightness = tri.color;
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
	
	public static void drawTriangles(Graphics g, double[] pDepthBuffer, int WIDTH, int HEIGHT) {
		for (int i = 0; i < triangleRaster.size(); i++) {
			Triangle tri = triangleRaster.get(i);
			g.setColor(tri.brightness);
			/*if (drawType == 2) {
				g.drawPolygon(new int[]{ (int) tri.p[0].x, (int) tri.p[1].x, (int) tri.p[2].x }, new int[]{ (int) tri.p[0].y, (int) tri.p[1].y, (int) tri.p[2].y }, 3);
			}*/
			if (drawType == 2) g.fillPolygon(new int[]{ (int) tri.p[0].x, (int) tri.p[1].x, (int) tri.p[2].x }, new int[]{ (int) tri.p[0].y, (int) tri.p[1].y, (int) tri.p[2].y }, 3);
			if (drawType == 0) texturedTriangle(g, pDepthBuffer, tri.p[0].x, tri.p[0].y, tri.t[0].u, tri.t[0].v, tri.t[0].w, tri.p[1].x, tri.p[1].y, tri.t[1].u, tri.t[1].v, tri.t[1].w, tri.p[2].x, tri.p[2].y, tri.t[2].u, tri.t[2].v, tri.t[2].w, WIDTH, HEIGHT);
			if (drawType == 1) drawTriangle(g, tri.p[0], tri.p[1], tri.p[2]);
		}
	}
	
	public static void drawHorizontalLine(Graphics g, int x0, int x1, int y) {
		if (x0 > x1) {
			for (int i = x1; i <= x0; i++) {
				g.fillRect(i, y, 1, 1);
			}
		} else {
			for (int i = x0; i <= x1; i++) {
				g.fillRect(i, y, 1, 1);
			}
		}
	}
	
	public static void fillBottomFlatTriangle(Graphics g, double x1, double y1, double x2, double y2, double x3, double y3) {
		double invslope1 = (x2 - x1) / (y2 - y1);
		double invslope2 = (x3 - x1) / (y3 - y1);

		double curx1 = x1;
		double curx2 = x1;

		for (int scanlineY = (int) y1; scanlineY <= (int) y2; scanlineY++) {
			drawHorizontalLine(g, (int)curx1, (int)curx2, (int)scanlineY);
			curx1 += invslope1;
			curx2 += invslope2;
		}
	}
	
	public static void fillTopFlatTriangle(Graphics g, double x1, double y1, double x2, double y2, double x3, double y3) {
		double invslope1 = (x3 - x1) / (y3 - y1);
		double invslope2 = (x3 - x2) / (y3 - y2);

		double curx1 = x3;
		double curx2 = x3;

		for (int scanlineY = (int) y3; scanlineY >= (int) y1; scanlineY--) {
			drawHorizontalLine(g, (int)curx1, (int)curx2, (int)scanlineY);
			curx1 -= invslope1;
			curx2 -= invslope2;
		}
	}
	
	public static void drawTriangle(Graphics g, Vector3d v1, Vector3d v2, Vector3d v3) {
		//System.out.print("Tri x: " + v1.x + "y:" + v1.y + ", x: " + v2.x + "y:" + v2.y + ", x: " + v3.x + "y:" + v3.y);
		
		Vector3d temp;
		
		if (v2.y < v1.y) {
			temp = v1;
			v1 = v2;
			v2 = temp;
		}
		if (v3.y < v1.y) {
			temp = v1;
			v1 = v3;
			v3 = temp;
		}
		if (v3.y < v2.y) {
			temp = v2;
			v2 = v3;
			v3 = temp;
		}	
		if (v2.y == v3.y) {
			fillBottomFlatTriangle(g, v1.x, v1.y, v2.x, v2.y, v3.x, v3.y);
		} else if (v1.y == v2.y) {
			fillTopFlatTriangle(g, v1.x, v1.y, v2.x, v2.y, v3.x, v3.y);
		} else {
			Vector3d v4 = new Vector3d((v1.x + ((double)(v2.y - v1.y) / (double)(v3.y - v1.y)) * (v3.x - v1.x)), v2.y, 0);
			fillBottomFlatTriangle(g, v1.x, v1.y, v2.x, v2.y, v4.x, v4.y);
			fillTopFlatTriangle(g, v2.x, v2.y, v4.x, v4.y, v3.x, v3.y);
		}
	}
	
	private static void texturedTriangle(Graphics g, double[] pDepthBuffer, double x1, double y1, double u0, double v0, double w0, 
																		 double x2, double y2, double u1, double v1, double w1, 
																		 double x3, double y3, double u2, double v2, double w2, int WIDTH, int HEIGHT) {
		// sort variables by y value: y0 <= y1 <= y2
		
		double maxX = Math.min(WIDTH, Math.max(x1, Math.max(x2, x3)));
		double minX = Math.max(0, Math.min(x1, Math.min(x2, x3)));
		
		double temp;
		if (y2 < y1) {
			temp = y1;
			y1 = y2;
			y2 = temp;

			temp = x1;
			x1 = x2;
			x2 = temp;

			temp = u0;
			u0 = u1;
			u1 = temp;

			temp = v0;
			v0 = v1;
			v1 = temp;

			temp = w0;
			w0 = w1;
			w1 = temp;
		}
		if (y3 < y1) {
			temp = y1;
			y1 = y3;
			y3 = temp;

			temp = x1;
			x1 = x3;
			x3 = temp;
			
			temp = u0;
			u0 = u2;
			u2 = temp;

			temp = v0;
			v0 = v2;
			v2 = temp;

			temp = w0;
			w0 = w2;
			w2 = temp;
		}
		if (y3 < y2) {
			temp = y2;
			y2 = y3;
			y3 = temp;

			temp = x2;
			x2 = x3;
			x3 = temp;

			temp = u1;
			u1 = u2;
			u2 = temp;

			temp = v1;
			v1 = v2;
			v2 = temp;

			temp = w1;
			w1 = w2;
			w2 = temp;
		}
		
		
		
		//System.out.print("Tri " + color + " : " + y0 + " " + y1 + " " + y2);

		// first line of triangle
		double dy1 = y2 - y1;
		double dx1 = x2 - x1;
		
		double dv1 = v1 - v0;
		double du1 = u1 - u0;
		double dw1 = w1 - w0;
		
		// second line of triangle
		double dy2 = y3 - y1;
		double dx2 = x3 - x1;
		
		double dv2 = v2 - v0;
		double du2 = u2 - u0;
		double dw2 = w2 - w0;
		
		double tex_u, tex_v, tex_w;

		// steps distance of the line
		double dax_step = 0, dbx_step = 0,
				du1_step = 0, dv1_step = 0,
				du2_step = 0, dv2_step = 0,
				dw1_step = 0, dw2_step = 0;

		if (dy1 != 0) dax_step = dx1 / Math.abs(dy1);
		if (dy2 != 0) dbx_step = dx2 / Math.abs(dy2);
	
		if (dy1 != 0) {
			du1_step = du1 / Math.abs(dy1);
			dv1_step = dv1 / Math.abs(dy1);
			dw1_step = dw1 / Math.abs(dy1);
		}

		if (dy2 != 0) {
			du2_step = du2 / Math.abs(dy2);
			dv2_step = dv2 / Math.abs(dy2);
			dw2_step = dw2 / Math.abs(dy2);
		}
		
		if (dy1 != 0) {
			for (int y = (int) y1; y <= (int) y2; y++) { // raws until it hits the flat bottom or middle of the triangle.
				
				double ax = (x1 + (y - y1) * dax_step);
				double bx = (x1 + (y - y1) * dbx_step);
				
				// start point
				double tex_su = u0 + (y - y1) * du1_step;
				double tex_sv = v0 + (y - y1) * dv1_step;
				double tex_sw = w0 + (y - y1) * dw1_step;
				// end point
				double tex_eu = u0 + (y - y1) * du2_step;
				double tex_ev = v0 + (y - y1) * dv2_step;
				double tex_ew = w0 + (y - y1) * dw2_step;

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
				}
				
				// always make tex_su come first
				tex_u = tex_su;
				tex_v = tex_sv;
				tex_w = tex_sw;

				double tstep = 1 / (bx - ax);
				double t = 0;

				for (int x = (int) Math.max(minX, ax); x < (int) Math.min(bx, maxX); x++) {
					if (y > 0 && y < HEIGHT) {
						tex_u = (1 - t) * tex_su + t * tex_eu;
						tex_v = (1 - t) * tex_sv + t * tex_ev;
						tex_w = (1 - t) * tex_sw + t * tex_ew;

						int d = (int) ((y * WIDTH) + x);
						if (tex_w > pDepthBuffer[d]) {
							if (showW) {
								int green = (int) (300 * tex_w);
								green = Math.min(green, 255);
								g.setColor(new Color(0, green, 0));
							}
							g.fillRect(x, y, 1, 1);
							pDepthBuffer[d] = tex_w;
						}
						t += tstep;
					}
				}
			}
		}
		
		dy1 = y3 - y2;
		dx1 = x3 - x2;
		
		dv1 = v2 - v1;
		du1 = u2 - u1;
		dw1 = w2 - w1;
		
		if (dy1 != 0) dax_step = dx1 / Math.abs(dy1);
		if (dy2 != 0) dbx_step = dx2 / Math.abs(dy2);

		du1_step = 0; 
		dv1_step = 0;

		if (dy1 != 0) {
			du1_step = du1 / Math.abs(dy1);
			dv1_step = dv1 / Math.abs(dy1);
			dw1_step = dw1 / Math.abs(dy1);
		}

		if (dy1 != 0) {
			for (int y = (int) y2; y <= (int) y3; y++) {
				double ax = (x2 + (y - y2) * dax_step);
				double bx = (x1 + (y - y1) * dbx_step);
				
				double tex_su = u1 + (y - y2) * du1_step;
				double tex_sv = v1 + (y - y2) * dv1_step;
				double tex_sw = w1 + (y - y2) * dw1_step;

				double tex_eu = u0 + (y - y1) * du2_step;
				double tex_ev = v0 + (y - y1) * dv2_step;
				double tex_ew = w0 + (y - y1) * dw2_step;

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
				}

				tex_u = tex_su;
				tex_v = tex_sv;
				tex_w = tex_sw;

				double tstep = 1 / (double) (bx - ax);
				double t = 0;

				for (int x = (int) Math.max(minX, ax); x < (int) Math.min(bx, maxX); x++) {
					if (y > 0 && y < HEIGHT) {
						tex_u = (1 - t) * tex_su + t * tex_eu;
						tex_v = (1 - t) * tex_sv + t * tex_ev;
						tex_w = (1 - t) * tex_sw + t * tex_ew;
						int d = (int) ((y * WIDTH) + x);
						if (tex_w > pDepthBuffer[d]) {
							if (showW) {
								int green = (int) (300 * tex_w);
								green = Math.min(green, 255);
								g.setColor(new Color(0, green, 0));
							}
							g.fillRect(x, y, 1, 1);
							pDepthBuffer[d] = tex_w;
						}
						t += tstep;
					}
				}
			}
		}
	}
}
