package engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import engine.camera.Camera;
import engine.entities.*;
import engine.graphics.Screen;
import engine.input.*;
import engine.light.EnvironmentLight;
import engine.matrix.Mat4x4;
import engine.models.*;
import engine.planets.Planet;
import engine.quaternion.Quaternion;
import engine.triangle.Triangle;
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
	
	private static final int WIDTH = 1600;
	private static final int HEIGHT = 900;
	private static double[] pDepthBuffer = new double[WIDTH * HEIGHT];
	
	private Screen screen;
	
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] imageBufferData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	
	private static final double fps = 60;
	
	private Camera camera = new Camera(0, 2, -10, 500);
	private EnvironmentLight light = new EnvironmentLight(new Vector3(-1, 1, -1), Color.WHITE);
	
	private Mat4x4 matView;
	private Mat4x4 matProj = Mat4x4.makeProjection(90, HEIGHT, WIDTH, 0.1, 1000);
	
	private UserInput userInput;
	
	private static Planet planet = null;
	
	public Engine() {
		// Generate Window
		this.screen = new Screen(WIDTH, HEIGHT);
		this.frame = new JFrame();
		
		Dimension size = new Dimension(WIDTH, HEIGHT);
		this.setPreferredSize(size);
		
		this.userInput = new UserInput();
		
		this.addKeyListener(this.userInput.keyboard);
	}
	
	public static void main(String[] args) {
		loadEntities();
		
		Engine engine = new Engine();
		engine.frame.setTitle(title);
		engine.frame.add(engine);
		engine.frame.pack();
		engine.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		engine.frame.setLocationRelativeTo(null);
		engine.frame.setResizable(false);
		engine.frame.setVisible(true);
		
		engine.start();
	}
	
	public static void loadEntities() {
		// load models
		//ModelFileReader.loadDir("Models", "Textures");
		//ModelFileReader.loadObj("Models/plane.obj", "plane");
		ModelFileReader.loadObj("Models/plane.obj", "plane", "Textures/UV_Grid_Sm.jpg");
		//ModelFileReader.loadObj("Models/octahedron.obj", "octahedron");
		//ModelFileReader.loadObj("Models/cube.obj", "cube", "Textures/UV_Grid_Sm.jpg");
		ModelFileReader.loadObj("Models/cube.obj", "cube", "Textures/cardboard.jpg");
		
		// initialize any entities
		//planet = new Planet(0, 5, 0);
		
		new Entity(ModelFileReader.get("plane"), 0, 0, 0);
		new Entity(ModelFileReader.get("cube").recalcNormals(), 0, 2, 0);
		//new Entity (ModelFileReader.get("octahedron"), 3, 0, 0);
		//new Entity(ModelFileReader.get("utahTeapot").recalcNormals());
		//new Entity(ModelFileReader.get("boid"), 2, 2, 0);
		//new Entity(ModelFileReader.get("lowPolySphere"), 0, 0, 0);
		//new Entity(ModelFileReader.get("smoothBlenderMonkey"), new Vector3d(0, 0, 0), Quaternion.localRotation(Vector3d.up(), Math.PI));
	}
	
	public synchronized void start() {
		running = true;
		this.thread = new Thread(this, "Engine");
		this.thread.start();
	}
	
	public synchronized void stop() {
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
			double deltaTime = (now - lastTime) / ns;
			delta += deltaTime;
			lastTime = now;
			
			render();
			frames++;
			
			while(delta >= 1) {
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
		
		screen.clear();
		
		Quaternion.normalize(camera.rot);
		Mat4x4 matRot = Quaternion.generateMatrix(camera.rot, null);
		Mat4x4 matTrans = Mat4x4.translationMatrix(camera.pos.x, camera.pos.y, camera.pos.z);
		matTrans = Mat4x4.quickInverse(matTrans);
		matView = Mat4x4.multiplyMatrix(matTrans, matRot);
		
		for (Entity ent : Entity.entities) {
			ent.project(camera, matView, matProj, WIDTH, HEIGHT, light);
		}
		
		if (planet != null) planet.project(camera, matView, matProj, WIDTH, HEIGHT, light);
		
		Triangle.cullScreenEdges(WIDTH, HEIGHT);
		Triangle.drawTriangles(screen.imageBufferData, pDepthBuffer, WIDTH, HEIGHT);
		
		for (int i = 0; i < imageBufferData.length; i++) {
			imageBufferData[i] = screen.imageBufferData[i];
			pDepthBuffer[i] = 0;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
		
		g.dispose();
		bs.show();
		
		Triangle.clearRaster();
	}
	
	private void update(double deltaTime) {
		Keyboard keyb = this.userInput.keyboard;
		keyb.update();

		this.camera.keyboard(keyb, deltaTime);
		
		for (Entity ent : Entity.entities) {
			ent.update(deltaTime);
		}
	}
}
