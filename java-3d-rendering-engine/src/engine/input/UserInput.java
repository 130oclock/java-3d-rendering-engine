package engine.input;

public class UserInput {

	public Keyboard keyboard;
	public MouseInput mouse;
	
	public UserInput(int WIDTH, int HEIGHT, int SCALE) {
		this.keyboard = new Keyboard();
		this.mouse = new MouseInput();
	}
}
