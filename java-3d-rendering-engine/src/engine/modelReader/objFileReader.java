package engine.modelReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import engine.triangle.Triangle;
import engine.vector.Vector2d;
import engine.vector.Vector3d;

public class objFileReader {
	public static Triangle[] load(String filename) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename), 8*1024);
			Scanner scanner = new Scanner(br);
			
			String title;
			List<double[]> vertices = new ArrayList<double[]>();
			List<int[]> faces = new ArrayList<int[]>();
			List<Triangle> triangles = new ArrayList<Triangle>();
			
			while (scanner.hasNextLine()) {
				String data = scanner.nextLine();
				
				String[] segments = data.split("\s+");
				if (segments.length == 0 || segments[0].contains("#") == true) continue;
				String type = segments[0];
				//System.out.println(type);
				
				switch(type) {
				case "g": 
					title = segments[1];
					System.out.println("loaded " + segments[1]);
					break;
				case "v":
					vertices.add(new double[] { Double.parseDouble(segments[1]), Double.parseDouble(segments[2]), Double.parseDouble(segments[3]) });
					break;
				case "f":
					faces.add(new int[] { Integer.parseInt(segments[1]), Integer.parseInt(segments[2]), Integer.parseInt(segments[3]) });
					break;
				}
				
				for (int[] face : faces) {
					// The indexes of the face's 3 points
					int i1 = face[0];
					int i2 = face[1];
					int i3 = face[2];
					// The values of the face's vectors
					double[] v1 = vertices.get(i1-1);
					double[] v2 = vertices.get(i2-1);
					double[] v3 = vertices.get(i3-1);
				
					Vector3d vector1 = new Vector3d(v1[0], v1[1], v1[2]);
					Vector3d vector2 = new Vector3d(v2[0], v2[1], v2[2]);
					Vector3d vector3 = new Vector3d(v3[0], v3[1], v3[2]);
					
					Triangle tri = new Triangle(vector1, vector2, vector3, new Vector2d(0, 0), new Vector2d(0, 0), new Vector2d(0, 0), null);
					
					triangles.add(tri);
				}
			}
			scanner.close();
			
			Triangle[] trianglesArray = triangles.toArray(new Triangle[0]);
			return trianglesArray;
			/*String[] tempsArray = vertices.toArray(new String[0]);
			for (String s : tempsArray) {
				System.out.println(s);
			}*/
		} catch (FileNotFoundException e) {
			System.out.println("Cound not find file " + filename);
			e.printStackTrace();
		}
		return null;
	}
}