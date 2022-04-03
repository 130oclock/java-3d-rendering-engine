package engine.graphics.line;

public class Line {
	
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