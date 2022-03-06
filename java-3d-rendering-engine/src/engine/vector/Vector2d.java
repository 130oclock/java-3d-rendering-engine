package engine.vector;

public class Vector2d {

	public double u, v, w;
	
	// Constructor
	public Vector2d(double u, double v) {
		this.u = u;
		this.v = v;
		this.w = 1;
	}
	
	// Make a duplicate object
	public Vector2d copy() {
		return new Vector2d(this.u, this.v);
	}
}
