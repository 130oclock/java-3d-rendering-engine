package engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import engine.camera.Camera;
import engine.entities.Entity;
import engine.input.*;
import engine.light.EnvironmentLight;
import engine.matrix.Mat4x4;
import engine.modelReader.objFileReader;
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
	
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 750;
	private static double[] pDepthBuffer;
	
	private static boolean running = false;
	
	private static final double fps = 60;
	
	private Camera camera = new Camera(0, 0, -5, 500);
	private EnvironmentLight light = new EnvironmentLight(new Vector3d(-1,1,-2));
	
	private Mat4x4 matView = Mat4x4.makeIdentity(new Mat4x4());
	private Mat4x4 matProj = Mat4x4.makeProjection(90, HEIGHT, WIDTH, 0.1, 1000);
	
	private UserInput userInput;
	
	public Engine() {
		// Generate Window
		this.frame = new JFrame();
		
		Dimension size = new Dimension(WIDTH, HEIGHT);
		this.setPreferredSize(size);
		
		this.userInput = new UserInput();
		
		this.addKeyListener(this.userInput.keyboard);
	}
	
	public static void main(String[] args) {
		// initialize any entities
		new Entity(objFileReader.load("Models/cube.obj"), 0, 0, 0);
		new Entity(objFileReader.load("Models/cube.obj"), 0.5, -0.5, 0.5);
		//new Entity(objFileReader.load("Models/octahedron.obj"));
		//new Entity(objFileReader.load("Models/utahTeapot.obj"));
		
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
			delta += (now - lastTime) / ns;
			lastTime = now;
			
			render();
			frames++;
			
			while(delta >= 1) {
				update();
				
				delta--;
			}
			
			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				frame.setTitle(title + " | " + frames + " fps | x " + String.format("%.2f", this.camera.pos.x) + " | y " + String.format("%.2f", this.camera.pos.y) + " | z " + String.format("%.2f", this.camera.pos.z));
				frames = 0;
			}
		}
		
		stop();
	}
	
	private void render() {
		BufferStrategy bs = this.getBufferStrategy();
		
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.BLACK);
		g.fillRect(0,  0, WIDTH, HEIGHT);
		
		pDepthBuffer = new double[WIDTH * HEIGHT + 1];
		/*for (int i = 0; i < pDepthBuffer.length; i++) {
			pDepthBuffer[i] = 0;
		}*/
		
		Quaternion.normalize(camera.rot);
		Mat4x4 matRot = Quaternion.generateMatrix(camera.rot, null);
		Mat4x4 matTrans = Mat4x4.translationMatrix(camera.pos.x, camera.pos.y, camera.pos.z);
		matTrans = Mat4x4.quickInverse(matTrans);
		matView = Mat4x4.multiplyMatrix(matTrans, matRot);
		
		Triangle.clearRaster();
		
		for (Entity ent : Entity.entities) {
			ent.project(camera, matView, matProj, WIDTH, HEIGHT, light);
		}
		
		//Triangle.cullScreenEdges(WIDTH, HEIGHT);
		
		Triangle.drawTriangles(g, pDepthBuffer, WIDTH, HEIGHT);
		
		g.dispose();
		bs.show();
	}
	
	private void update() {
		Keyboard keyb = this.userInput.keyboard;
		keyb.update();

		this.camera.keyboard(keyb);
		
		for (Entity ent : Entity.entities) {
			ent.update();
		}
	}
}
