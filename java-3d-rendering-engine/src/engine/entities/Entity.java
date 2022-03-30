package engine.entities;

import java.awt.Color;
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
	private Color color = new Color(255, 255, 255);
	
	public Entity(Triangle[] model) {
		this.model = model;
		this.pos = Vector3d.empty();
		this.rot = Quaternion.empty();

		entities.add(this);
	}
	
	public Entity(Triangle[] model, double x, double y, double z) {
		this.model = model;
		this.pos = new Vector3d(x, y, z);
		this.rot = Quaternion.empty();
		
		entities.add(this);
	}
	
	public Entity(Triangle[] model, double x, double y, double z, Color color) {
		this.model = model;
		this.pos = new Vector3d(x, y, z);
		this.rot = Quaternion.empty();
		this.color = color;
		
		entities.add(this);
	}
	
	public Entity(Triangle[] model, Vector3d pos) {
		this.model = model;
		this.pos = pos;
		this.rot = Quaternion.empty();
		
		entities.add(this);
	}
	
	public Entity(Triangle[] model, Vector3d pos, Quaternion rot) {
		this.model = model;
		this.pos = pos;
		this.rot = rot;
		
		entities.add(this);
	}
	
	public void project(Camera camera, Mat4x4 matView, Mat4x4 matProj, int WIDTH, int HEIGHT, EnvironmentLight light) {
		Triangle.projectTriangles(this.model, this.pos, this.rot, camera, matView, matProj, WIDTH, HEIGHT, light, this.color);
	}
	
	public void update(double deltaTime) {
		//this.rot = Quaternion.rotate(this.rot, Vector3d.normalize(new Vector3d(1, 1, 1)), -0.05 * deltaTime);
	}
}
