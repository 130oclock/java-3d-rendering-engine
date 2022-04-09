package engine.physics;

import engine.vector.Vector3;

public class Collider {
	private Vector3 max, min;
	
	public Collider(Vector3 max, Vector3 min) {
		this.max = max;
		this.min = min;
	}

	public Vector3 getMax() {
		return max;
	}

	public Vector3 getMin() {
		return min;
	}
	
	public static boolean intersectBoxes(RigidBody a, RigidBody b, Vector3 axis) {
		Vector3 difference = Vector3.subtract(a.getPos(), b.getPos());
		Vector3 aMax = Vector3.add(a.collider.getMax(), difference);
		Vector3 aMin = Vector3.add(a.collider.getMin(), difference);
		Vector3 bMax = b.collider.getMax();
		Vector3 bMin = b.collider.getMin();
		
		boolean check = (aMin.x <= bMax.x && aMax.x >= bMin.x) && // check overlap on x axis
						(aMin.y <= bMax.y && aMax.y >= bMin.y) && // check overlap on y axis
						(aMin.z <= bMax.z && aMax.z >= bMin.z);   // check overlap on z axis
		
		/*boolean checkx = (bStatic && aMax.x <= bMax.x && aMin.x >= bMin.x) || (aStatic && aMax.x >= bMax.x && aMin.x <= bMin.x);
		boolean checky = (bStatic && aMax.y <= bMax.y && aMin.y >= bMin.y) || (aStatic && aMax.y >= bMax.y && aMin.y <= bMin.y);
		boolean checkz = (bStatic && aMax.z <= bMax.z && aMin.z >= bMin.z) || (aStatic && aMax.z >= bMax.z && aMin.z <= bMin.z);*/
		
		if (check) {
			double magnitude = 1000;
			double direction = 1;
			int closestId = -1;
			
			double[] distances = new double[] { aMin.x - bMax.x, aMax.x - bMin.x, aMin.y - bMax.y, aMax.y - bMin.y, aMin.z - bMax.z, aMax.z - bMin.z };
			for (int i = 0; i < 6; i++) {
				double d = Math.abs(distances[i]);
				if (d <= magnitude) {
					magnitude = d;
					closestId = i;
					direction = distances[i];
				}
			}
			
			direction /= Math.abs(direction);
			
			if (closestId == 0 || closestId == 1) axis.x = magnitude * direction;
			if (closestId == 2 || closestId == 3) axis.y = magnitude * direction;
			if (closestId == 4 || closestId == 5) axis.z = magnitude * direction;
			
			/*double distancex = 1000, distancey = 1000, distancez = 1000;
			
			double[] distances = new double[] { aMin.x - bMax.x, aMax.x - bMin.x, aMin.y - bMax.y, aMax.y - bMin.y, aMin.z - bMax.z, aMax.z - bMin.z };
			for (int i = 0; i < 2; i++) {
				double d = Math.abs(distances[i]);
				if (d <= distancex) {
					distancex = d;
				}
			}
			for (int i = 2; i < 4; i++) {
				double d = Math.abs(distances[i]);
				if (d <= distancey) {
					distancey = d;
				}
			}
			for (int i = 4; i < 6; i++) {
				double d = Math.abs(distances[i]);
				if (d <= distancez) {
					distancez = d;
				}
			}
			
			//if (!checkx) axis.x = distancex;
			//if (!checky) axis.y = distancey;
			//if (!checkz) axis.z = distancez;*/
		}
		
		return check;
	}
}
