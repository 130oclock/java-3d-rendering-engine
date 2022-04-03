package engine.graphics.environment;

import java.awt.Color;

import engine.vector.Vector3;

public class EnvironmentLight {
	
	private Vector3 direction;
	public Color color;
	
	public EnvironmentLight(Vector3 dir) {
		this.direction = Vector3.normalize(dir);
		this.color = Color.WHITE;
	}
	
	public EnvironmentLight(Vector3 dir, Color color) {
		this.direction = Vector3.normalize(dir);
		this.color = color;
	}
	
	public Vector3 getDirection() {
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
	
	public static Color blend(int c0, int c1) {
		int a1 = (c0 >> 24 & 0xff);
		int r1 = ((c0 & 0xff0000) >> 16);
		int g1 = ((c0 & 0xff00) >> 8);
		int b1 = (c0 & 0xff);
		
		int a2 = (c1 >> 24 & 0xff);
		int r2 = ((c1 & 0xff0000) >> 16);
		int g2 = ((c1 & 0xff00) >> 8);
		int b2 = (c1 & 0xff);
		
		int a = (int)((a1 * 0.5) + (a2 * 0.5));
		int r = (int)((r1 * 0.5) + (r2 * 0.5));
		int g = (int)((g1 * 0.5) + (g2 * 0.5));
		int b = (int)((b1 * 0.5) + (b2 * 0.5));
		
		return new Color( a << 24 | r << 16 | g << 8 | b );
	}
	
}
