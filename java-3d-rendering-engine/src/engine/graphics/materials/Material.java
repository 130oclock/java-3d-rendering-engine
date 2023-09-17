package engine.graphics.materials;

/**
 * The {@code Material} class is used to to store texture images.
 * 
 * @author Aidan
 * @since 1.1
 */
public class Material {
	private int[] textureBufferData;
	private int textureWidth, textureHeight;
	
	/**
	 * Constructs a new Material given the image data, width, and height.
	 * 
	 * @param imageBufferData	the image data
	 * @param textureWidth		the image width
	 * @param textureHeight		the image height
	 */
	public Material(int[] imageBufferData, int textureWidth, int textureHeight) {
		this.textureBufferData = imageBufferData;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
	}

	/**
	 * @return the texture data
	 */
	public int[] getTexture() {
		return textureBufferData;
	}

	/**
	 * @return the texture width
	 */
	public int getWidth() {
		return textureWidth;
	}

	/**
	 * @return the texture height
	 */
	public int getHeight() {
		return textureHeight;
	}
}
