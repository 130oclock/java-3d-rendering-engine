package engine.light;

import java.awt.Color;

import engine.vector.Vector3d;

public class EnvironmentLight {
	
	private Vector3d direction;
	public Color color;
	
	public EnvironmentLight(Vector3d dir) {
		this.direction = Vector3d.normalize(dir);
		this.color = Color.WHITE;
	}
	
	public Vector3d getDirection() {
		return this.direction;
	}
}
