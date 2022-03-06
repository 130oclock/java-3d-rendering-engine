package engine.quaternion;

public class Quaternion {
	
	public double w, x, y, z;
	
	public Quaternion(double w, double x, double y, double z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static Quaternion empty() {
		return new Quaternion(0, 0, 0, 0);
	}
}
