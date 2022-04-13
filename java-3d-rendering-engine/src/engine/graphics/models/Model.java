package engine.graphics.models;

import engine.graphics.materials.Material;
import engine.graphics.triangle.Triangle;

public class Model {
	
	public Triangle[] mesh;
	private Material mat;
	
	public Model(Triangle[] mesh) {
		this.mesh = mesh;
	}
	
	public Model(Triangle[] mesh, int[] imageBufferData, int textureWidth, int textureHeight) {
		this.mesh = mesh;
		this.mat = new Material(imageBufferData, textureWidth, textureHeight);
	}
	
	public Model recalcNormals() {
		this.mesh = Triangle.findSmoothTriangleNormals(this.mesh);
		return this;
	}
	
	public int[] getTexture() {
		return this.mat.getTexture();
	}
	
	public int getTexWidth() {
		return this.mat.getWidth();
	}
	
	public int getTexHeight() {
		return this.mat.getHeight();
	}
}
