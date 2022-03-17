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
	
	private static boolean drawOutlines = false;
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
	
	public static double signedDist(Vector3d nPlane, Vector3d pPlane, Vector3d p) {
		Vector3d n = Vector3d.normalize(p);
		return (nPlane.x * n.x + nPlane.y * n.y + nPlane.z * n.z - Vector3d.dotProduct(nPlane, pPlane));
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
							clipped = Triangle.clipAgainstPlane(new Vector3d(0, HEIGHT - 1, 0), new Vector3d(0, -1, 0), test); 
							break; //Bottom
						case 2: 
							clipped = Triangle.clipAgainstPlane(new Vector3d(0, 0, 0), new Vector3d(1, 0, 0), test); 
							break; //Right
						case 3: 
							clipped = Triangle.clipAgainstPlane(new Vector3d(WIDTH - 1, 0, 0), new Vector3d(-1, 0, 0), test); 
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
	
	public static void drawTriangles(Graphics g, int[] pDepthBuffer, int WIDTH, int HEIGHT) {
		for (Triangle tri : triangleRaster) {
			if (drawOutlines == true) {
				g.setColor(Color.BLACK);
				g.drawPolygon(new int[]{ (int) tri.p[0].x, (int) tri.p[1].x, (int) tri.p[2].x }, new int[]{ (int) tri.p[0].y, (int) tri.p[1].y, (int) tri.p[2].y }, 3);
			}
			g.setColor(tri.brightness);
			g.fillPolygon(new int[]{ (int) tri.p[0].x, (int) tri.p[1].x, (int) tri.p[2].x }, new int[]{ (int) tri.p[0].y, (int) tri.p[1].y, (int) tri.p[2].y }, 3);
			//drawTriangle(g, pDepthBuffer, WIDTH, HEIGHT, tri.p[0].x, tri.p[1].x, tri.p[2].x, tri.p[0].y, tri.p[1].y, tri.p[2].y, tri.t[0].u, tri.t[1].u, tri.t[2].u, tri.t[0].v, tri.t[1].v, tri.t[2].v, tri.t[0].w, tri.t[1].w, tri.t[2].w, tri.brightness);
		}
	}
	
	private static void drawTriangle(Graphics g, int[] pDepthBuffer, int WIDTH, int HEIGHT, double x1_, double x2_, double x3_, double y1_, double y2_, double y3_, double u1, double u2, double u3, double v1, double v2, double v3, double w1, double w2, double w3, Color color) {
		int x1 = (int) x1_;
		int x2 = (int) x2_;
		int x3 = (int) x3_;
		int y1 = (int) y1_;
		int y2 = (int) y2_;
		int y3 = (int) y3_;
		
		if (y2 < y1) {
			int temp = y1;
			y1 = y2;
			y2 = temp;
			
			temp = x1;
			x1 = x2;
			x2 = temp;
			
			double temp1 = u1;
			u1 = u2;
			u2 = temp1;
			
			temp1 = v1;
			v1 = v2;
			v2 = temp1;
			
			temp1 = w1;
			w1 = w2;
			w2 = temp1;
		}
		if (y3 < y1) {
			int temp = y1;
			y1 = y3;
			y3 = temp;
			
			temp = x1;
			x1 = x3;
			x3 = temp;
			
			double temp1 = u1;
			u1 = u3;
			u3 = temp1;
			
			temp1 = v1;
			v1 = v3;
			v3 = temp1;
			
			temp1 = w1;
			w1 = w3;
			w3 = temp1;
		}
		if (y3 < y2) {
			int temp = y2;
			y2 = y3;
			y3 = temp;
			
			temp = x2;
			x2 = x3;
			x3 = temp;
			
			double temp1 = u2;
			u2 = u3;
			u3 = temp1;
			
			temp1 = v2;
			v2 = v3;
			v3 = temp1;
			
			temp1 = w2;
			w2 = w3;
			w3 = temp1;
			
		}
		
		int dy1 = y2 - y1;
		int dx1 = x2 - x1;
		double du1 = u2 - u1;
		double dv1 = v2 - v1;
		double dw1 = w2 - w1;
		
		int dy2 = y3 - y1;
		int dx2 = x3 - x1;
		double du2 = u3 - u1;
		double dv2 = v3 - v1;
		double dw2 = w3 - w1;
		
		double tex_u, tex_v, tex_w;
		
		double dax_step = 0, dbx_step = 0,
				du1_step = 0, dv1_step = 0,
				du2_step = 0, dv2_step = 0,
				dw1_step = 0, dw2_step = 0;
				
		if (dy1 != 0) {
			dax_step = dx1 / Math.abs(dy1);
			du1_step = du1 / Math.abs(dy1);
			dv1_step = dv1 / Math.abs(dy1);
			dw1_step = dw1 / Math.abs(dy1);
		}

		if (dy2 != 0) {
			dbx_step = dx2 / Math.abs(dy2);
			du2_step = du2 / Math.abs(dy2);
			dv2_step = dv2 / Math.abs(dy2);
			dw2_step = dw2 / Math.abs(dy2);
		}
		
		if (dy1 != 0) {
			for (int i = y1; i <= y2; i++) {
				double ax = x1 + (i - y1) * dax_step;
				double bx = x1 + (i - y1) * dbx_step;
				
				double tex_su = u1 + (i - y1) * du1_step;
				double tex_sv = v1 + (i - y1) * dv1_step;
				double tex_sw = w1 + (i - y1) * dw1_step;
				
				double tex_eu = u1 + (i - y1) * du2_step;
				double tex_ev = v1 + (i - y1) * dv2_step;
				double tex_ew = w1 + (i - y1) * dw2_step;
				
				if (ax > bx) {
					double temp = ax;
					ax = bx;
					bx = temp;
					temp = tex_su;
					tex_su = tex_eu;
					tex_eu = temp;
					temp = tex_sv;
					tex_sv = tex_ev;
					tex_ev = temp;
					temp = tex_sw;
					tex_sw = tex_ew;
					tex_ew = temp;
				}
				
				tex_u = tex_su;
				tex_v = tex_sv;
				tex_w = tex_sw;
				
				double tstep = 1 / (bx - ax);
				double t = 0;
				
				for (double j = ax; j < bx; j++) {
					if (i > 0 && i <= HEIGHT && j > 0 && j <= WIDTH) {
						tex_u = ((1 - t) * tex_su) + (t * tex_eu);
						tex_v = ((1 - t) * tex_sv) + (t * tex_ev);
						tex_w = ((1 - t) * tex_sw) + (t * tex_ew);
						
						int d = (int) ((i * WIDTH) + j);
						if (tex_w > pDepthBuffer[d]) {
							g.setColor(color);
							g.fillRect((int) j, i, 1, 1);
							pDepthBuffer[d] = (int) tex_w;
						}
						t += tstep;
					}
				}
			}
			
			dy1 = y3 - y2;
			dx1 = x3 - x2;
			dv1 = v3 - v2;
			du1 = u3 - u2;
			dw1 = w3 - w2;

			du1_step = 0; 
			dv1_step = 0;
			
			if (dy1 != 0) {
				dax_step = dx1 / Math.abs(dy1);
				du1_step = du1 / Math.abs(dy1);
				dv1_step = dv1 / Math.abs(dy1);
				dw1_step = dw1 / Math.abs(dy1);
			}
			
			if (dy2 != 0) dbx_step = dx2 / Math.abs(dy2);
				
			if (dy1 != 0) {
				for (int i = y2; i <= y3; i++) {
					double ax = x2 + (i - y2) * dax_step;
					double bx = x1 + (i - y1) * dbx_step;
					
					double tex_su = u2 + (i - y2) * du1_step;
					double tex_sv = v2 + (i - y2) * dv1_step;
					double tex_sw = w2 + (i - y2) * dw1_step;
					
					double tex_eu = u1 + (i - y1) * du2_step;
					double tex_ev = v1 + (i - y1) * dv2_step;
					double tex_ew = w1 + (i - y1) * dw2_step;
					
					if (ax > bx) {
						double temp = ax;
						ax = bx;
						bx = temp;
						temp = tex_su;
						tex_su = tex_eu;
						tex_eu = temp;
						temp = tex_sv;
						tex_sv = tex_ev;
						tex_ev = temp;
						temp = tex_sw;
						tex_sw = tex_ew;
						tex_ew = temp;
					}
					
					tex_u = tex_su;
					tex_v = tex_sv;
					tex_w = tex_sw;
					
					double tstep = 1 / (bx - ax);
					double t = 0;
					
					for (double j = ax; j < bx; j++) {
						if (i > 0 && i <= HEIGHT && j > 0 && j <= WIDTH) {
							tex_u = ((1 - t) * tex_su) + (t * tex_eu);
							tex_v = ((1 - t) * tex_sv) + (t * tex_ev);
							tex_w = ((1 - t) * tex_sw) + (t * tex_ew);
							int d = (int) ((i * WIDTH) + j);
							if (tex_w > pDepthBuffer[d]) {
								g.setColor(color);
								g.fillRect((int) j, i, 1, 1);
								pDepthBuffer[d] = (int) tex_w;
							}
							t += tstep;
						}
					}
				}
			}
		}
	}
}
