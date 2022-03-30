package engine.vector;

public class Vector2 {

	public double u, v, w;
	
	// Constructor
	public Vector2(double u, double v) {
		this.u = u;
		this.v = v;
		this.w = 1;
	}
	
	// Make a duplicate object
	public Vector2 copy() {
		return new Vector2(this.u, this.v);
	}
}
