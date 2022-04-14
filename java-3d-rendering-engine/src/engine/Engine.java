package engine;

import java.awt.AWTException;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import engine.camera.Camera;
import engine.entities.*;
import engine.graphics.Screen;
import engine.graphics.environment.EnvironmentLight;
import engine.graphics.environment.Skybox;
import engine.graphics.models.*;
import engine.graphics.triangle.Triangle;
import engine.input.*;
import engine.matrix.Mat4x4;
import engine.physics.PhysicsWorld;
import engine.planets.Planet;
import engine.quaternion.Quaternion;
import engine.vector.*;

public class Engine extends Canvas implements Runnable {
	
	/*
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Thread thread;
	private JFrame frame;
	private static String title = "3D Engine";

	private static boolean running = false;
	
	private static final int WIDTH = 600;
	private static final int HEIGHT = WIDTH / 16 * 9;
	private static final int SCALE = 2;

	// Graphics
	private Screen screen;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] imageBufferData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	
	private static final double fps = 60;
	private Camera camera = new Camera(0, 4, -10, 500, WIDTH, HEIGHT, SCALE);
	private EnvironmentLight light = new EnvironmentLight(new Vector3(-1, 1, -1), new Color(0, 0, 0, 0));
	private Skybox skybox = null;
	
	private Mat4x4 matView;
	private Mat4x4 matProj = Mat4x4.makeProjection(90, HEIGHT, WIDTH, 0.1, 1000);
	
	// Input
	private UserInput userInput;
	
	private static Planet planet = null;
	
	public Engine() {
		// Generate Window
		this.screen = new Screen(WIDTH, HEIGHT);
		this.frame = new JFrame();
		
		Dimension size = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
		this.setPreferredSize(size);
		
		this.userInput = new UserInput(WIDTH, HEIGHT, SCALE);
		
		this.addKeyListener(this.userInput.keyboard);
		this.addMouseListener(this.userInput.mouse);
		this.addMouseMotionListener(this.userInput.mouse);
	}
	
	public static void main(String[] args) {
		loadEntities(); // load model and texture files into memory
		
		Engine engine = new Engine(); 
		engine.frame.setTitle(title);
		engine.frame.add(engine);
		engine.frame.pack();
		engine.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		engine.frame.setLocationRelativeTo(null);
		engine.frame.setResizable(false);
		engine.frame.setVisible(true);

		engine.skybox = new Skybox(ModelFileReader.get("sky_box")); // instantiate the sky box\
		
		engine.start(); // start the engine
	}
	
	public static void loadEntities() {
		// load models
		ModelFileReader.loadObj("Models/sky_box.obj", "sky_box", "Textures/png-clipart-cube-mapping-night-sky-star-counter-strike-1-6-star-angle-video-game.png"); //skybox.png
		//ModelFileReader.loadObj("Models/sky_box.obj", "sky_box", "Textures/UV_Grid_Sm.jpg");
		//ModelFileReader.loadDir("Models", "Textures");
		//ModelFileReader.loadObj("Models/plane.obj", "plane");
		ModelFileReader.loadObj("Models/utahTeapot.obj", "utahTeapot");
		ModelFileReader.loadObj("Models/plane.obj", "carpet", "Textures/carpet.jpg");
		ModelFileReader.loadObj("Models/octahedron.obj", "octahedron");
		//ModelFileReader.loadObj("Models/cube.obj", "cube", "Textures/UV_Grid_Sm.jpg");
		ModelFileReader.loadObj("Models/cube.obj", "cube1", "Textures/crate.png");
		ModelFileReader.loadObj("Models/cube.obj", "cube", "Textures/cardboard.jpg");
		
		// initialize any entities
		//planet = new Planet(0, 5, 0);
		
		new Entity(ModelFileReader.get("carpet"), 0, 0, 0, 10000).rig.setStatic();
		new Entity(ModelFileReader.get("cube1"), 0, 3, 0, 3);
		new Entity(ModelFileReader.get("cube"), 0, 5.5, 0, 1.6).rig.addVel(0,-0.5,0);
		new Entity(ModelFileReader.get("octahedron"), 0, 20, 0, 1);
		//new Entity(ModelFileReader.get("utahTeapot").recalcNormals(), 5, 30, 0, 20);
		//new Entity(ModelFileReader.get("boid"), 2, 2, 0);
		//new Entity(ModelFileReader.get("lowPolySphere"), 0, 0, 0);
		//new Entity(ModelFileReader.get("smoothBlenderMonkey"), new Vector3d(0, 0, 0), Quaternion.localRotation(Vector3d.up(), Math.PI));
	}
	
	public synchronized void start() {
		running = true;
		this.thread = new Thread(this, "Engine");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.thread.start();
	}
	
	public synchronized void stop() {
		System.out.println("Stopped");
		running = false;
		try {
			this.thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / fps;
		double delta = 0;
		int frames = 0;
		
		while(running) {
			long now = System.nanoTime();
			double deltaTime = (now - lastTime);
			delta += deltaTime / ns;
			lastTime = now;
			
			render(); // draw to the screen as many times as it can
			frames++;
			
			while(delta >= 1) { // update the physics at a fixed rate
				update(deltaTime);
				
				delta--;
			}
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				frame.setTitle(title + " | " + frames + " fps");
				frames = 0;
			}
		}
		
		stop();
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		// create a buffer strategy only if it does not already exist
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		// calculate a rotation and translation matrix that is the inverse of the camera's position and rotation
		Quaternion.normalize(camera.rot);
		Mat4x4 matRot = Mat4x4.generateMatrix(camera.rot, null, null);
		Mat4x4 matTrans = Mat4x4.translationMatrix(camera.pos.x, camera.pos.y, camera.pos.z);
		matTrans = Mat4x4.quickInverse(matTrans);
		matView = Mat4x4.multiplyMatrix(matTrans, matRot);
		
		for (Entity ent : Entity.entities) { // project all triangles in all objects to screen space and store them in the list of triangles
			ent.project(camera, matView, matProj, WIDTH, HEIGHT, light);
		}
		
		skybox.project(camera, matView, matProj, WIDTH, HEIGHT, light); // project all triangles in the sky box to screen space and store them in the list of triangles
		
		if (planet != null) planet.project(camera, matView, matProj, WIDTH, HEIGHT, light);
		
		Triangle.cullScreenEdges(WIDTH, HEIGHT); // cull all triangles not on the screen and clip all triangles intersected by the edges of the screen
		Triangle.drawTriangles(screen, light); // draw all triangles to the screen
		
		int length = imageBufferData.length;
		for (int i = 0; i < length; i++) { // copy all pixels from the image buffer to the screen
			imageBufferData[i] = screen.imageBufferData[i];
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null); // draw the image to the screen
		
		g.dispose();
		bs.show();
		// clear the list of triangles
		Triangle.clearRaster();
	}
	
	private void update(double dt) {
		
		dt = dt * 0.000000001; // change in time between each update. 1/60 s
		
		Keyboard keyb = this.userInput.keyboard;
		keyb.update(); // update the keyboard to the current inputs

		this.camera.input(this.userInput, dt); // send the inputs to the camera
		//this.camera.lookAt(Entity.entities.get(3).pos, dt);
		
		PhysicsWorld.update(dt); // update the physics
		
		if (keyb.getAnyKey(KeyEvent.VK_ESCAPE)) { // if the user presses "escape" then close the window
			System.out.println("Closed");
			this.frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
		
		this.userInput.mouse.update(); // update the mouse input
	}
}
