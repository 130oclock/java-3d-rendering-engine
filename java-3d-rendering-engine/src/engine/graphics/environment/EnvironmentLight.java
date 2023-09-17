package engine.graphics.environment;

import java.awt.Color;

import engine.vector.Vector3;

/**
 * The {@code EnvironmentLight} class stores the direction and color of the ambient lighting caused by the environment.
 * It provides several functions that are used to blend colors.
 * 
 * @author Aidan
 * @since 1.1
 */
public class EnvironmentLight {
	
	private Vector3 direction;
	public Color color;
	
	/**
	 * Constructs a new EnvironmentLight given a direction and color.
	 * 
	 * @param direction	the direction of the light
	 * @param color		the color of the light
	 * 
	 * @see EnvironmentLight
	 * @see Vector3
	 * @see Color
	 */
	public EnvironmentLight(Vector3 direction, Color color) {
		this.direction = Vector3.normalize(direction);
		this.color = color;
	}

	/**
	 * Constructs a new EnvironmentLight given a direction.
	 * Defaults the color to WHITE.
	 * 
	 * @param direction	the direction of the light
	 * 
	 * @see EnvironmentLight
	 * @see Vector3
	 */
	public EnvironmentLight(Vector3 direction) {
		this.direction = Vector3.normalize(direction);
		this.color = Color.WHITE;
	}
	
	
	/**
	 * @return	the direction of the environment light
	 */
	public Vector3 getDirection() {
		return this.direction;
	}
	
	/**
	 * Blends two colors together.
	 * Both arguments are the Color class.
	 * 
	 * @param color0	the first color
	 * @param color1	the second color
	 * @return			a new Color that is the blend of color0 and color1
	 * @see Color
	 */
	public static Color blend(Color color0, Color color1) {
		double totalAlpha = color0.getAlpha() + color1.getAlpha();
	    double weight0 = color0.getAlpha() / totalAlpha;
	    double weight1 = color1.getAlpha() / totalAlpha;

	    double r = weight0 * color0.getRed() + weight1 * color1.getRed();
	    double g = weight0 * color0.getGreen() + weight1 * color1.getGreen();
	    double b = weight0 * color0.getBlue() + weight1 * color1.getBlue();
	    double a = Math.max(color0.getAlpha(), color1.getAlpha());

	    return new Color((int) r, (int) g, (int) b, (int) a);
	}
	
	/**
	 * Blends two colors together. Both arguments are integers.
	 * Bits 0-7 are the blue value, 8-15 the green value, and 16-23 the red value.
	 * 
	 * @param color0	the first color
	 * @param color1	the second color
	 * @return			a new Color that is the blend of color0 and color1
	 * @see Color
	 */
	public static Color blend(int color0, int color1) {
		int a1 = (color0 >> 24 & 0xff);
		int r1 = ((color0 & 0xff0000) >> 16);
		int g1 = ((color0 & 0xff00) >> 8);
		int b1 = (color0 & 0xff);
		
		int a2 = (color1 >> 24 & 0xff);
		int r2 = ((color1 & 0xff0000) >> 16);
		int g2 = ((color1 & 0xff00) >> 8);
		int b2 = (color1 & 0xff);
		
		int a = (int)((a1 * 0.5) + (a2 * 0.5));
		int r = (int)((r1 * 0.5) + (r2 * 0.5));
		int g = (int)((g1 * 0.5) + (g2 * 0.5));
		int b = (int)((b1 * 0.5) + (b2 * 0.5));
		
		return new Color( a << 24 | r << 16 | g << 8 | b );
	}
	
}
