package engine.graphics;

public class Screen {
	
	private int WIDTH, HEIGHT;
	public int[] imageBufferData;
	private int backgroundColor = 0x000000;
	
	public Screen(int WIDTH, int HEIGHT) {
		this.WIDTH = WIDTH;
		this.HEIGHT = HEIGHT;
		
		imageBufferData = new int[WIDTH * HEIGHT];
	}
	
	public void clear() {
		for (int i = 0; i < imageBufferData.length; i++) {
			imageBufferData[i] = 0;
		}
	}
}
