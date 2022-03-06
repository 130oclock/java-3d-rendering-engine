package engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import engine.camera.Camera;
import engine.input.*;
import engine.light.EnvironmentLight;
import engine.matrix.Mat4x4;
import engine.quaternion.Quaternion;
import engine.vector.*;
import engine.triangle.Triangle;

public class Engine extends Canvas implements Runnable {
	
	/*
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Thread thread;
	private JFrame frame;
	private static String title = "3D Engine";
	private static final int screenWidth = 800;
	private static final int screenHeight = 600;
	private static boolean running = false;
	
	private static final double fps = 60;
	
	private Camera camera = new Camera(new Vector3d(0,0,20), 500);
	private EnvironmentLight light = new EnvironmentLight(new Vector3d(1,1,1));
	
	private Mat4x4 matView = Mat4x4.makeIdentity(new Mat4x4());
	private Mat4x4 matProj = Mat4x4.makeProjection(90, screenHeight, screenWidth, 0.1, 1000);
	
	private UserInput userInput;
	
	public Engine() {
		// Generate Window
		this.frame = new JFrame();
		
		Dimension size = new Dimension(screenWidth, screenHeight);
		this.setPreferredSize(size);
		
		this.userInput = new UserInput();
		
		this.addKeyListener(this.userInput.keyboard);
	}
	
	public static void main(String[] args) {
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
		
		Triangle test1 = new Triangle(new Vector3d(-5, -5, 5), new Vector3d(5, 5, 5), new Vector3d(-5, 5, 5), new Vector2d(0,0), new Vector2d(0,0), new Vector2d(0,0), null);
		Triangle test2 = new Triangle(new Vector3d(-5, -5, 5), new Vector3d(5, -5, 5), new Vector3d(5, 5, 5), new Vector2d(0,0), new Vector2d(0,0), new Vector2d(0,0), null);
		Triangle test3 = new Triangle(new Vector3d(-5, -5, -5), new Vector3d(-5, 5, 5), new Vector3d(-5, 5, -5), new Vector2d(0,0), new Vector2d(0,0), new Vector2d(0,0), null);
		Triangle test4 = new Triangle(new Vector3d(-5, -5, -5), new Vector3d(-5, -5, 5), new Vector3d(-5, 5, 5), new Vector2d(0,0), new Vector2d(0,0), new Vector2d(0,0), null);
		Triangle test5 = new Triangle(new Vector3d(5, -5, 5), new Vector3d(5, 5, -5), new Vector3d(5, 5, 5), new Vector2d(0,0), new Vector2d(0,0), new Vector2d(0,0), null);
		Triangle test6 = new Triangle(new Vector3d(5, -5, 5), new Vector3d(5, -5, -5), new Vector3d(5, 5, -5), new Vector2d(0,0), new Vector2d(0,0), new Vector2d(0,0), null);
		Triangle test7 = new Triangle(new Vector3d(5, -5, -5), new Vector3d(-5, 5, -5), new Vector3d(5, 5, -5), new Vector2d(0,0), new Vector2d(0,0), new Vector2d(0,0), null);
		Triangle test8 = new Triangle(new Vector3d(5, -5, -5), new Vector3d(-5, -5, -5), new Vector3d(-5, 5, -5), new Vector2d(0,0), new Vector2d(0,0), new Vector2d(0,0), null);
		Triangle test9 = new Triangle(new Vector3d(-5, 5, 5), new Vector3d(5, 5, -5), new Vector3d(-5, 5, -5), new Vector2d(0,0), new Vector2d(0,0), new Vector2d(0,0), null);
		Triangle test10 = new Triangle(new Vector3d(-5, 5, 5), new Vector3d(5, 5, 5), new Vector3d(5, 5, -5), new Vector2d(0,0), new Vector2d(0,0), new Vector2d(0,0), null);
		Triangle test11 = new Triangle(new Vector3d(5, -5, 5), new Vector3d(-5, -5, -5), new Vector3d(5, -5, -5), new Vector2d(0,0), new Vector2d(0,0), new Vector2d(0,0), null);
		Triangle test12 = new Triangle(new Vector3d(5, -5, 5), new Vector3d(-5, -5, 5), new Vector3d(-5, -5, -5), new Vector2d(0,0), new Vector2d(0,0), new Vector2d(0,0), null);
		Triangle[] trianglesToRaster = new Triangle[] { test1, test2, test3, test4, test5, test6, test7, test8, test9, test10, test11, test12 };
		
		Triangle.projectTriangles(g, trianglesToRaster, camera, matView, matProj, screenWidth, screenHeight, light);
		
		g.dispose();
		bs.show();
	}
	
	private void update() {
		Keyboard keyb = this.userInput.keyboard;
		keyb.update();

		Vector3d vUp = this.camera.rot.getUpVector();
		Vector3d vForward = this.camera.rot.getForwardVector();
		Vector3d vRight = this.camera.rot.getRightVector();
		
		if (keyb.getUp() == true) {
			this.camera.translate(-vUp.x, -vUp.y, -vUp.z);
		}
		
		if (keyb.getDown() == true) {
			this.camera.translate(vUp.x, vUp.y, vUp.z);
		}
		
		if (keyb.getRight() == true) {
			this.camera.translate(vRight.x, vRight.y, vRight.z);
		}
		
		if (keyb.getLeft() == true) {
			this.camera.translate(-vRight.x, -vRight.y, -vRight.z);
		}

		if (keyb.getForward() == true) {
			this.camera.translate(-vForward.x, -vForward.y, -vForward.z);
		}
		
		if (keyb.getBackward() == true) {
			this.camera.translate(vForward.x, vForward.y, vForward.z);
		}
		
		if (keyb.getKUp() == true) {
			this.camera.rotate(vRight, -0.04);
		}
		
		if (keyb.getKDown() == true) {
			this.camera.rotate(vRight, 0.04);
		}
		
		if (keyb.getKRight() == true) {
			this.camera.rotate(vUp, -0.04);
		}
		
		if (keyb.getKLeft() == true) {
			this.camera.rotate(vUp, 0.04);                    
		}
	}
}
