package engine.graphics.models;

import engine.graphics.materials.Material;
import engine.graphics.triangle.Triangle;

/**
 * The {@code Model} class represents the 3 dimensional structure of an object. 
 * It is made of a list of triangles. 
 * 
 * @author Aidan
 * @since 1.0
 * 
 * @see Triangle
 */
public class Model {
	
	public Triangle[] mesh;
	private Material mat;
	
	/**
	 * Constructs a new Model given a mesh.
	 * 
	 * @param mesh	the list of triangles that make up the object
	 */
	public Model(Triangle[] mesh) {
		this.mesh = mesh;
	}
	
	/**
	 * Constructs a new Model given a mesh and texture.
	 * 
	 * @param mesh				the list of triangles that make up the object
	 * @param imageBufferData	the image data
	 * @param textureWidth		the image width
	 * @param textureHeight		the image height
	 */
	public Model(Triangle[] mesh, int[] imageBufferData, int textureWidth, int textureHeight) {
		this.mesh = mesh;
		this.mat = new Material(imageBufferData, textureWidth, textureHeight);
	}
	
	/**
	 * Recalculates and stores the normal directions of each vertex. 
	 * These normals are used to smoothly shade the object.
	 * 
	 * @return the model with new normals
	 */
	public Model recalcNormals() {
		this.mesh = Triangle.findSmoothTriangleNormals(this.mesh);
		return this;
	}
	
	/**
	 * @return the texture data
	 */
	public int[] getTexture() {
		return this.mat.getTexture();
	}
	
	/**
	 * @return the texture width
	 */
	public int getTexWidth() {
		return this.mat.getWidth();
	}
	
	/**
	 * @return the texture height
	 */
	public int getTexHeight() {
		return this.mat.getHeight();
	}
}
