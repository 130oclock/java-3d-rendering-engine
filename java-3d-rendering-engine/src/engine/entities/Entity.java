package engine.entities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import engine.camera.Camera;
import engine.graphics.environment.EnvironmentLight;
import engine.graphics.models.Model;
import engine.graphics.triangle.Triangle;
import engine.matrix.Mat4x4;
import engine.quaternion.Quaternion;
import engine.vector.Vector3;

public class Entity {
	
	public static List<Entity> entities = new ArrayList<Entity>();
	
	private Model model;
	public Vector3 pos;
	private Quaternion rot;
	private Color color;
	
	public Entity(Model model, Vector3 pos, Quaternion rot, Color color) {
		this.model = model;
		this.pos = pos;
		this.rot = rot;
		this.color = color;
		
		entities.add(this);
	}
	
	public Entity(Model model) {
		this(model, new Vector3(), new Quaternion(), Color.WHITE);
	}
	
	public Entity(Model model, double x, double y, double z) {
		this(model, new Vector3(x, y, z), new Quaternion(), Color.WHITE);
	}
	
	public Entity(Model model, double x, double y, double z, Color color) {
		this(model, new Vector3(x, y, z), new Quaternion(), color);
	}
	
	public Entity(Model model, Vector3 pos) {
		this(model, pos, new Quaternion(), Color.WHITE);
	}
	
	public void project(Camera camera, Mat4x4 matView, Mat4x4 matProj, int WIDTH, int HEIGHT, EnvironmentLight light) {
		Triangle.projectTriangles(this.model.mesh, this.pos, this.rot, camera, matView, matProj, WIDTH, HEIGHT, light, this.color);
	}
	
	public void update(double deltaTime) {
		//this.rot = Quaternion.rotate(this.rot, Vector3d.normalize(new Vector3d(1, 1, 1)), -0.05 * deltaTime);
	}
}
