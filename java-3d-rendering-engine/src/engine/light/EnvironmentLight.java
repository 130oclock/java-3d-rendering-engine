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
	
	public EnvironmentLight(Vector3d dir, Color color) {
		this.direction = Vector3d.normalize(dir);
		this.color = color;
	}
	
	public Vector3d getDirection() {
		return this.direction;
	}
	
	public static Color blend(Color c0, Color c1) {
	    double totalAlpha = c0.getAlpha() + c1.getAlpha();
	    double weight0 = c0.getAlpha() / totalAlpha;
	    double weight1 = c1.getAlpha() / totalAlpha;

	    double r = weight0 * c0.getRed() + weight1 * c1.getRed();
	    double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
	    double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
	    double a = Math.max(c0.getAlpha(), c1.getAlpha());

	    return new Color((int) r, (int) g, (int) b, (int) a);
	  }
}
