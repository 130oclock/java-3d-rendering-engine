package engine.graphics.line;

/**
 * The {@code Line} class is used to draw lines in screen space.
 * 
 * @author Aidan
 * @since 1.0
 */
public class Line {
	
	/**
	 * Draws a line on the screen given start and end points.
	 * 
	 * @param imageBufferData	an array of integers that represents the pixels on the screen
	 * @param x0				the starting pixel's x coordinate
	 * @param y0				the starting pixel's y coordinate
	 * @param x1				the ending pixel's x coordinate
	 * @param y1				the ending pixel's y coordinate
	 * @param WIDTH				the width of the screen
	 * @param color				the color of the pixel
	 */
	public static void drawline(int[] imageBufferData, int x0, int y0, int x1, int y1, int WIDTH, int color) {  
		int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
 
        int sx = x0 < x1 ? 1 : -1; 
        int sy = y0 < y1 ? 1 : -1; 
 
        int err = dx-dy;
        int e2;
 
        while (true) {
			int d = (int) ((y0 * WIDTH) + x0);
			imageBufferData[d] = color;
            if (x0 == x1 && y0 == y1) 
                break;
 
            e2 = 2 * err;
            if (e2 > -dy) {
                err = err - dy;
                x0 = x0 + sx;
            }
 
            if (e2 < dx) {
                err = err + dx;
                y0 = y0 + sy;
            }
        }            
	}  
}