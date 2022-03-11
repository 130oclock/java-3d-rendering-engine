package engine.entities;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import engine.camera.Camera;
import engine.light.EnvironmentLight;
import engine.matrix.Mat4x4;
import engine.quaternion.Quaternion;
import engine.triangle.Triangle;
import engine.vector.Vector3d;

public class Entity {
	
	public static List<Entity> entities = new ArrayList<Entity>();
	
	private Triangle[] model;
	private Vector3d pos;
	private Quaternion rot;
	
	public Entity(Triangle[] model, Vector3d pos, Quaternion rot) {
		this.model = model;
		this.pos = pos;
		this.rot = rot;
		
		entities.add(this);
	}
	
	public void draw(Graphics g, Camera camera, Mat4x4 matView, Mat4x4 matProj, int WIDTH, int HEIGHT, EnvironmentLight light) {
		Triangle.projectTriangles(g, this.model, this.pos, this.rot, camera, matView, matProj, WIDTH, HEIGHT, light);
	}
	
	public void update() {
		this.rot = Quaternion.rotate(this.rot, Vector3d.normalize(new Vector3d(1, 1, 1)), -0.04);
	}
}
