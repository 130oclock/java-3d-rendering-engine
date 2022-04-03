package engine.graphics;

public class Screen {
	
	public int WIDTH, HEIGHT;
	public int[] imageBufferData;
	public double[] pDepthBuffer;
	public int length;
	private int backgroundColor = 0x000000;
	
	public Screen(int WIDTH, int HEIGHT) {
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		
		length = WIDTH * HEIGHT;
		
		imageBufferData = new int[length];
		pDepthBuffer = new double[length];
	}
}
