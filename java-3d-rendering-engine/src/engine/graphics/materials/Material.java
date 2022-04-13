package engine.graphics.materials;

public class Material {
	private int[] textureBufferData;
	private int textureWidth, textureHeight;
	
	public Material(int[] imageBufferData, int textureWidth, int textureHeight) {
		this.textureBufferData = imageBufferData;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	public int[] getTexture() {
		return textureBufferData;
	}

	public int getWidth() {
		return textureWidth;
	}

	public int getHeight() {
		return textureHeight;
	}
}
