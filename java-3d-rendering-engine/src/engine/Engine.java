package engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import engine.camera.Camera;
import engine.light.EnvironmentLight;
import engine.matrix.Mat4x4;
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
	
	private static Camera camera = new Camera(new Vector3d(0,0,50), 500);
	private static EnvironmentLight light = new EnvironmentLight(new Vector3d(1,0,1));
	
	private static Mat4x4 matProj = Mat4x4.makeProjection(90, screenHeight/screenWidth, 0.1, 1000);
	
	public Engine() {
		// Generate Window
		this.frame = new JFrame();
		
		Dimension size = new Dimension(screenWidth, screenHeight);
		this.setPreferredSize(size);
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
		
		/*
		 * Quaternion.normalize(camera.rot);
		 * Mat4x4 matRot = Quaternion.generateMatrix(camera.rot);
		 * Mat4x4 matTrans = mat4x4.matrix_MakeTranslation(camera.pos.x, camera.pos.y, camera.pos.z);
		 * matTrans = mat4x4.matrix_QuickInverse(matTrans);
		 * matView = mat4x4.matrix_MultiplyMatrix(matTrans, matRot);
		 */
		
		Triangle test = new Triangle(new Vector3d(0,0,0), new Vector3d(100,0,0), new Vector3d(50,50,0), new Vector2d(0,0), new Vector2d(0,0), new Vector2d(0,0), null);
		Triangle[] trianglesToRaster = new Triangle[] { test };
		
		Triangle.projectTriangles(g, trianglesToRaster, camera, null, matProj, screenWidth, screenHeight, light);
		
		g.setColor(Color.BLACK);
		g.fillRect(0,  0, screenWidth, screenHeight);
		
		g.dispose();
		bs.show();
	}
	
	private void update() {
		
	}
}
