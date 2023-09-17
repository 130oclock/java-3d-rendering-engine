package engine.physics;

import java.util.ArrayList;
import java.util.List;

import engine.vector.Vector3;

public class PhysicsWorld {
	public static List<RigidBody> bodies = new ArrayList<RigidBody>();
	private static List<CollisionSolver> collisions = new ArrayList<CollisionSolver>();
	public static Vector3 gravity = new Vector3(0, -9.8, 0);//new Vector3(0, 0, 0);//
	
	public static void addObject(RigidBody rig) {
		bodies.add(rig);
	}
	
	public static void removeObject(RigidBody rig) {
		if (rig == null) return;
		bodies.remove(rig);
	}
	
	public static void update(double dt) {
		resolveCollisions(dt);
		for (RigidBody rig : bodies) {
			if (!rig.isStatic)
				rig.update(dt, gravity);
		}
	}
	
	public static void resolveCollisions(double dt) {
		
		for (RigidBody a : bodies) {
			for (RigidBody b : bodies) {
				if (a == b) break;
				
				if (a.collider == null || b.collider == null) continue;
				
				Vector3 point = new Vector3();
				
				boolean collided = false;
				//if (CollisionSolver.intersectAABB(a, b)) // broad phase
					collided = CollisionSolver.intersectOBB(a, b, point); // narrow phase
				
				if (collided) {
					collisions.add(new CollisionSolver(a, b, point)); 
				}
			}
		}
		
		for (CollisionSolver col : collisions) {
			col.solve(dt);
		}
		//System.out.println(collisions.size());
		collisions.clear();
	}
}
