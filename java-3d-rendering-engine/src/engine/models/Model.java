package engine.models;

import engine.triangle.Triangle;

public class Model {
	
	public Triangle[] mesh;
	private int[] textureBufferData;
	private int textureWidth;
	
	public Model(Triangle[] mesh) {
		this.mesh = mesh;
	}
	
	public Model(Triangle[] mesh, int[] imageBufferData, int textureWidth) {
		this.mesh = mesh;
		this.textureBufferData = imageBufferData;
		this.textureWidth = textureWidth;
	}
	
	public Model recalcNormals() {
		this.mesh = Triangle.findSmoothTriangleNormals(this.mesh);
		return this;
	}
	
	public int[] getTexture() {
		return this.textureBufferData;
	}
	
	public int getTexWidth() {
		return this.textureWidth;
	}
}
