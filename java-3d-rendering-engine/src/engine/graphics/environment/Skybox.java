package engine.graphics.environment;

import java.awt.Color;

import engine.camera.Camera;
import engine.graphics.models.Model;
import engine.graphics.triangle.Triangle;
import engine.matrix.Mat4x4;

public class Skybox {
	private Model model;
	private Color color = Color.DARK_GRAY;
	
	public Skybox(Model model) {
		this.model = model;
	}
	
	public void project(Camera camera, Mat4x4 matView, Mat4x4 matProj, int WIDTH, int HEIGHT, EnvironmentLight light) {
		Triangle.projectSkybox(this.model.mesh, camera, matView, matProj, WIDTH, HEIGHT, color);
	}
}
