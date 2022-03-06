package engine.triangle;

import java.awt.Color;
import java.awt.Graphics;

import engine.camera.Camera;
import engine.light.EnvironmentLight;
import engine.matrix.Mat4x4;
import engine.vector.Vector2d;
import engine.vector.Vector3d;

public class Triangle {
	
	private Vector3d[] p = new Vector3d[3];
	private Vector3d normal;
	
	private Vector2d[] t = new Vector2d[3];
	
	private Color color;
	private Color brightness;
	
	public Triangle(Vector3d p0, Vector3d p1, Vector3d p2, Vector2d t0, Vector2d t1, Vector2d t2, Vector3d normal) {
		this.p[0] = p0;
		this.p[1] = p1;
		this.p[2] = p2;

		this.normal = normal;
		
		this.t[0] = t0;
		this.t[1] = t1;
		this.t[2] = t2;
	}
	
	public static Triangle empty() {
		Vector3d v1 = new Vector3d(0, 0, 0);
		Vector3d v2 = new Vector3d(0, 0, 0);
		Vector3d v3 = new Vector3d(0, 0, 0);
		Vector3d normal = new Vector3d(0, 0, 0);
		Vector2d v4 = new Vector2d(0, 0);
		Vector2d v5 = new Vector2d(0, 0);
		Vector2d v6 = new Vector2d(0, 0);
		return new Triangle(v1, v2, v3, v4, v5, v6, normal);
	}
	
	public Vector3d getVector(int n) {
		return this.p[n].copy();
	}
	
	public Triangle copy() {
		return new Triangle(this.p[0].copy(), this.p[1].copy(), this.p[2].copy(), this.t[0].copy(), this.t[1].copy(), this.t[2].copy(), this.normal);
	}
	
	public static Vector3d[] multiplyMatrixTriangle(Mat4x4 mat, Triangle tri) {
		Vector3d vec1 = Vector3d.mutiplyMatrixVector(mat, tri.p[0]);
		Vector3d vec2 = Vector3d.mutiplyMatrixVector(mat, tri.p[1]);
		Vector3d vec3 = Vector3d.mutiplyMatrixVector(mat, tri.p[2]);
		
		return new Vector3d[] { vec1, vec2, vec3 };
	}
	
	public static void projectTriangles(Graphics g, Triangle[] trianglesToRaster, Camera camera, Mat4x4 matView, Mat4x4 matProj, int WIDTH, int HEIGHT, EnvironmentLight light) {
		Mat4x4 matWorld = new Mat4x4();
		Mat4x4.makeIdentity(matWorld);
		
		for (int i = 0; i < trianglesToRaster.length; i++) {
			Triangle tri = trianglesToRaster[i];
			
			Triangle triTransformed = Triangle.empty();
			Triangle triViewed = Triangle.empty();
			
			triTransformed.p = Triangle.multiplyMatrixTriangle(matWorld, tri);
			
			triTransformed.t[0] = tri.t[0];
			triTransformed.t[1] = tri.t[1];
			triTransformed.t[2] = tri.t[2];
			
			Vector3d normal, line1, line2;
			if (tri.normal == null) {
				line1 = Vector3d.subtract(triTransformed.p[1], triTransformed.p[0]);
				line2 = Vector3d.subtract(triTransformed.p[1], triTransformed.p[0]);
				
				normal = Vector3d.crossProduct(line1, line2);
				
				normal = Vector3d.normalize(normal);
			} else {
				normal = tri.normal;
			}
			
			// Check if the triangle is facing towards the camera
			Vector3d vCameraRay = Vector3d.subtract(triTransformed.p[0], camera.pos);
			if (Vector3d.dotProduct(normal, vCameraRay) < 0) {
				Vector3d lightDirection = light.getDirection();
				
				double dp = Math.max(0.1, Vector3d.dotProduct(lightDirection, normal));
				
				int red = (int) ((dp * 255) * (light.color.getRed() / 255));
				int green = (int) ((dp * 255) * (light.color.getGreen() / 255));
				int blue = (int) ((dp * 255) * (light.color.getBlue() / 255));
				triViewed.brightness = new Color(red, green, blue);
				
				triViewed.p = Triangle.multiplyMatrixTriangle(matView, triTransformed);
				
				triViewed.t[0] = triTransformed.t[0];
				triViewed.t[1] = triTransformed.t[1];
				triViewed.t[2] = triTransformed.t[2];
				
				Triangle triProjected = Triangle.empty();
				triProjected.p = Triangle.multiplyMatrixTriangle(matProj, triViewed);
				
				triProjected.brightness = triViewed.brightness;
				
				triProjected.t[0] = triViewed.t[0];
				triProjected.t[1] = triViewed.t[1];
				triProjected.t[2] = triViewed.t[2];
				
				triProjected.t[0].u = triProjected.t[0].u / triProjected.p[0].w;
				triProjected.t[1].u = triProjected.t[1].u / triProjected.p[1].w;
				triProjected.t[2].u = triProjected.t[2].u / triProjected.p[2].w;
				
				triProjected.t[0].v = triProjected.t[0].v / triProjected.p[0].w;
				triProjected.t[1].v = triProjected.t[1].v / triProjected.p[1].w;
				triProjected.t[2].v = triProjected.t[2].v / triProjected.p[2].w;
				
				triProjected.t[0].w = 1 / triProjected.p[0].w;
				triProjected.t[1].w = 1 / triProjected.p[1].w;
				triProjected.t[2].w = 1 / triProjected.p[2].w;

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
				
				g.fillPolygon(new int[]{ (int) triProjected.p[0].x, (int) triProjected.p[1].x, (int) triProjected.p[2].x }, new int[]{ (int) triProjected.p[0].y, (int) triProjected.p[1].y, (int) triProjected.p[2].y }, 3);
			}
		}
	}
}
