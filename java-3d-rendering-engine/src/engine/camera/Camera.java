package engine.camera;

import java.awt.event.KeyEvent;

import engine.graphics.triangle.Triangle;
import engine.input.Keyboard;
import engine.input.MouseInput;
import engine.input.UserInput;
import engine.quaternion.Quaternion;
import engine.vector.Vector3;

/**
 * The {@code Camera} class represents the user's point of view. It stores the position and 
 * rotation of the user and allows them to control the {@code Camera} through key inputs.
 * <p>
 * The {@code Engine} class creates and uses only one {@code Camera} when rendering the screen.
 * The position and rotation of the {@code Camera} are used to project triangles in their correct 
 * relative positions and rotations onto screen space.
 * 
 * @author Aidan
 * @since 1.0
 */
public class Camera {
	
	public Vector3 position; // position of the camera
	private Vector3 startPosition; // starting position of the camera, used when resetting position
	public Quaternion rotation; // rotation of the camera
	public double viewDistance; // the maximum distance that entities will be shown
	public double moveSpeed = 0.2;
	public double rotationSpeed = Math.PI/64; // the rotation speed of the camera in radians
	public Vector3 clippingPlane;
	private int WIDTH = 0;
	private int HEIGHT = 0;
	private int SCALE = 1;
	
	/**
	 * Constructs a new Camera given a position as a {@code Vector3}.
	 * 
	 * @param position		the position of the Camera
	 * @param viewDistance	the distance from the Camera within which entities will be shown
	 * @param WIDTH			the width of the screen
	 * @param HEIGHT		the height of the screen
	 * @param SCALE			the scale of the pixels displayed in the window
	 * 
	 * @see Vector3
	 */
	public Camera(Vector3 position, double viewDistance, int WIDTH, int HEIGHT, int SCALE) {
		this.position = position;
		this.startPosition = position.copy();
		this.rotation = new Quaternion();
		this.viewDistance = viewDistance;
		this.clippingPlane = new Vector3(0, 0, 0.1);
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		this.SCALE = SCALE;
	}
	
	/**
	 * Constructs a new Camera given a position as x, y, z coordinates.
	 * 
	 * @param x				the x position of the Camera
	 * @param y				the y position of the Camera
	 * @param z				the z position of the Camera
	 * @param viewDistance	the distance from the Camera within which objects will load
	 * @param WIDTH			the width of the screen
	 * @param HEIGHT		the height of the screen
	 * @param SCALE			the scale of the pixels displayed in the window	
	 */
	public Camera(double x, double y, double z, double viewDistance, int WIDTH, int HEIGHT, int SCALE) {
		this(new Vector3(x, y, z), viewDistance, WIDTH, HEIGHT, SCALE);
	}
	
	/**
	 * Moves the camera by some translation.
	 * <p>
	 * Adds the {@code Vector3} translation to the camera's current position.
	 * 
	 * @param translation	the change in position
	 * 
	 * @see Vector3
	 */
	public void translate(Vector3 translation) {
		this.position.x += translation.x;
		this.position.y += translation.y;
		this.position.z += translation.z;
	}
	
	/**
	 * Rotates the camera by some angle around an axis.
	 * <p>
	 * The axis is defined by a normalized {@code Vector3}. The angle is measured in radians.
	 * 
	 * @param axis	the axis around which to rotate
	 * @param angle	the angle to rotate
	 * 
	 * @see Vector3
	 */
	public void rotate(Vector3 axis, double angle) {
		this.rotation = Quaternion.rotate(this.rotation, axis, angle);
	}
	
	/**
	 * Makes the camera turn to look at the target's position.
	 * <p>
	 * This function uses {@code Quaternion.slerp()} to smoothly turn towards the target.
	 * 
	 * @param target		the target's position
	 * @param deltaTime		the change in time between updates 
	 * 						(scaling this changes the speed of the camera's rotation)
	 * 
	 * @see Vector3
	 */
	public void lookAt(Vector3 target, double deltaTime) {
		Quaternion rot = Quaternion.lookAt(position, target);
		this.rotation = Quaternion.slerp(this.rotation, rot, deltaTime);
	}
	
	/**
	 * Updates the camera based on user inputs.
	 * 
	 * @param input the UserInput object
	 * @param deltaTime
	 * 
	 * @see UserInput
	 */
	public void input(UserInput input, double deltaTime) {
		Keyboard keyb = input.keyboard;
		MouseInput mouse = input.mouse;
		
		Vector3 vUp = this.rotation.getUpVector();
		Vector3 vForward = this.rotation.getForwardVector();
		Vector3 vRight = this.rotation.getRightVector();
		
		if (keyb.getUp() == true) { // Space
			this.translate(Vector3.multiply(vUp, this.moveSpeed));
		}
		
		if (keyb.getDown() == true) { // Shift
			this.translate(Vector3.multiply(vUp, -this.moveSpeed));
		}
		
		if (keyb.getRight() == true) { // D
			this.translate(Vector3.multiply(vRight, this.moveSpeed));
		}
		
		if (keyb.getLeft() == true) { // A
			this.translate(Vector3.multiply(vRight, -this.moveSpeed));
		}

		if (keyb.getForward() == true) { // W
			this.translate(Vector3.multiply(vForward, this.moveSpeed));
		}
		
		if (keyb.getBackward() == true) { // S
			this.translate(Vector3.multiply(vForward, -this.moveSpeed));
		}
		
		if (keyb.getKUp() == true) { // Key_up
			this.rotate(vRight, -rotationSpeed);
		}
		
		if (keyb.getKDown() == true) { // Key_down
			this.rotate(vRight, rotationSpeed);
		}
		
		if (keyb.getKRight() == true) { // Key_right
			this.rotate(vUp, rotationSpeed);
		}
		
		if (keyb.getKLeft() == true) { // Key_left
			this.rotate(vUp, -rotationSpeed);                    
		}
		
		/*double change = (double) mouse.getChangeX() / WIDTH;
		this.rotate(vUp, -change);*/
		
		if (keyb.getKRRight() == true) { // E
			this.rotate(vForward, -rotationSpeed);
		}
		
		if (keyb.getKRLeft() == true) { // Q
			this.rotate(vForward, rotationSpeed);                    
		}
		
		if (keyb.getAnyKey(KeyEvent.VK_X)) { // X, reset position
			this.position.x = this.startPosition.x;
			this.position.y = this.startPosition.y;
			this.position.z = this.startPosition.z;
			this.rotation = new Quaternion();
		}
		
		if (keyb.getAnyKey(KeyEvent.VK_O)) { // O, change lighting mode
			Triangle.doGouraud = false;
		}
		
		if (keyb.getAnyKey(KeyEvent.VK_L)) { // L, change lighting mode
			Triangle.doGouraud = true;
		}
	}
}
