package engine.graphics.models;

import engine.graphics.triangle.Triangle;

public class Model {
	
	public Triangle[] mesh;
	private int[] textureBufferData;
	private int textureWidth, textureHeight;
	
	public Model(Triangle[] mesh) {
		this.mesh = mesh;
	}
	
	public Model(Triangle[] mesh, int[] imageBufferData, int textureWidth, int textureHeight) {
		this.mesh = mesh;
		this.textureBufferData = imageBufferData;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
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
	
	public int getTexHeight() {
		return this.textureHeight;
	}
}
