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
import engine.vector.*;

public class Engine extends Canvas implements Runnable {
	
	/*
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Thread thread;
	private JFrame frame;
	private static String title = "3D Engine";
	private static final int screenWidth = 1000;
	private static final int screenHeight = 750;
	private static boolean running = false;
	
	private static final double fps = 60;
	
	private Camera camera = new Camera(new Vector3d(0,0,10), 500);
	private EnvironmentLight light = new EnvironmentLight(new Vector3d(1,-1,1));
	
	private Mat4x4 matView = Mat4x4.makeIdentity(new Mat4x4());
	private Mat4x4 matProj = Mat4x4.makeProjection(90, screenHeight, screenWidth, 0.1, 1000);
	
	private UserInput userInput;
	
	private static Entity cube;
	
	public Engine() {
		// Generate Window
		this.frame = new JFrame();
		
		Dimension size = new Dimension(screenWidth, screenHeight);
		this.setPreferredSize(size);
		
		this.userInput = new UserInput();
		
		this.addKeyListener(this.userInput.keyboard);
	}
	
	public static void main(String[] args) {

		cube = new Entity(objFileReader.load("Models/cube.obj"), Vector3d.empty(), Quaternion.empty());
		//cube = new Entity(objFileReader.load("Models/octahedron.obj"), Vector3d.empty(), Quaternion.empty());
		
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
			
			while(delta >= 1) {
				update();
				delta--;
			}
			render();
			frames++;
			
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
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.setColor(Color.BLACK);
		g.fillRect(0,  0, screenWidth, screenHeight);
		
		Quaternion.normalize(camera.rot);
		Mat4x4 matRot = Quaternion.generateMatrix(camera.rot, null);
		Mat4x4 matTrans = Mat4x4.translationMatrix(camera.pos.x, camera.pos.y, camera.pos.z);
		matTrans = Mat4x4.quickInverse(matTrans);
		matView = Mat4x4.multiplyMatrix(matTrans, matRot);
		
		cube.draw(g, camera, matView, matProj, screenWidth, screenHeight, light);
		
		g.dispose();
		bs.show();
	}
	
	private void update() {
		Keyboard keyb = this.userInput.keyboard;
		keyb.update();

		this.camera.keyboard(keyb);
		cube.update();
	}
}
