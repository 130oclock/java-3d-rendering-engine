package engine.graphics.models;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import engine.graphics.triangle.Triangle;

public class ModelFileReader {
	
	public static List<String> modelnames = new ArrayList<String>();
	public static List<Model> models = new ArrayList<Model>();
	
	public static Model get(String modelname) {
		int index = modelnames.indexOf(modelname);
		if (index == -1) return null;
		return models.get(index);
	}
	
	public static void loadDir(String directoryname, String texturedirname) {
		try (Stream<Path> paths = Files.walk(Paths.get(directoryname))) {
			List<Path> files = paths.filter(Files::isRegularFile).toList();
			int fileLength = files.size();
			for (int i = 0; i < fileLength; i++) {
				Path filepath = files.get(i);
				String filename = filepath.getFileName().toString();
				filename = filename.substring(0, filename.lastIndexOf("."));
				
				String texturename = texturedirname + "/" + filename + ".jpg";
				File file = new File(texturename);
				boolean exists = file.exists() && !file.isDirectory();
				if (exists) ModelFileReader.loadObj(filepath.toString(), filename, texturename);
				else ModelFileReader.loadObj(filepath.toString(), filename);
				
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static void loadObj(String filename, String modelname, String texturepath) {
		try {
			FileInputStream inputStream = null;
			Scanner scanner = null;
			
			int[] imageBufferData = null;
			int imageBufferWidth = 0;
			int imageBufferHeight = 0;
			try {
				BufferedImage image = ImageIO.read(new File(texturepath));
				BufferedImage convertedImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
				convertedImg.getGraphics().drawImage(image, 0, 0, null);
				imageBufferWidth = convertedImg.getWidth();
				imageBufferHeight = convertedImg.getHeight();
				imageBufferData = ((DataBufferInt) convertedImg.getRaster().getDataBuffer()).getData();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			List<double[]> vertexInd = null;
			List<double[]> vertexTextInd = null;
			List<double[]> vertexNormInd = null;
			List<Triangle> triangles = null;
			
			int num_faces = 0;
			try {
				inputStream = new FileInputStream(filename);
				scanner = new Scanner(inputStream);
				
				String title = null;
				vertexInd = new ArrayList<double[]>();
				vertexTextInd = new ArrayList<double[]>();
				vertexNormInd = new ArrayList<double[]>();
				triangles = new ArrayList<Triangle>();
			
				while (scanner.hasNextLine()) {
					String data = scanner.nextLine();
					
					String[] segments = data.split("\s+");
					if (segments.length == 0 || segments[0].contains("#") == true) continue;
					String type = segments[0];
					//System.out.println(type);
					
					switch(type) {
					case "g": 
					case "o": 
						title = segments[1];
						break;
					case "v": // List of geometric vertices, with (x, y, z [,w]) coordinates, w is optional and defaults to 1.0.
						vertexInd.add(new double[] { Double.parseDouble(segments[1]), Double.parseDouble(segments[2]), Double.parseDouble(segments[3]) });
						break;
					case "vt": // List of texture coordinates, in (u, [,v ,w]) coordinates, these will vary between 0 and 1. v, w are optional and default to 0.
						vertexTextInd.add(new double[] { Double.parseDouble(segments[1]), Double.parseDouble(segments[2]) });
						break;
					case "vn": // List of vertex normals in (x,y,z) form; normals might not be unit vectors.
						vertexNormInd.add(new double[] { Double.parseDouble(segments[1]), Double.parseDouble(segments[2]), Double.parseDouble(segments[3]) });
						break;
					case "f": // Polygonal face element. f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3
						int length = segments.length;
						int[] verticesIndex = new int[length-1];
						int[] textureIndex = new int[length-1];
						int[] normalsIndex = new int[length-1];
						for (int i = -1; i < length - 1; i++) {
							if (i == -1) continue;
							String[] indices = segments[i+1].split("/");
							if (indices.length > 0 && indices[0] != null && indices[0] != "") verticesIndex[i] = Integer.parseInt(indices[0]) - 1;
							if (indices.length > 1 && indices[1] != null && indices[1] != "") textureIndex[i] = Integer.parseInt(indices[1]) - 1;
							if (indices.length > 2 && indices[2] != null && indices[2] != "") normalsIndex[i] = Integer.parseInt(indices[2]) - 1;
						}
						
						int num_Triangles = verticesIndex.length - 2;

						String[] indices = segments[1].split("/");
						int format = 0;
						if (indices.length == 1) format = 1;
						if (indices.length == 2) format = 2;
						if (indices.length == 3 && indices[1] != "") format = 3;
						if (indices.length == 3 && indices[1] == "") format = 4;
						
						for (int i = 1; i <= num_Triangles; i++) {
							num_faces++;
							int index1 = 0, index2 = i, index3 = i + 1;
							
							double[] v1, v2, v3, vt1, vt2, vt3, vn1, vn2, vn3;
							// The values of the face's vertices
							v1 = vertexInd.get(verticesIndex[index1]);
							v2 = vertexInd.get(verticesIndex[index2]);
							v3 = vertexInd.get(verticesIndex[index3]);
							if (format == 1) {
								Triangle tri = new Triangle(v1, v2, v3);
								tri.modelIndex = models.size();
								triangles.add(tri);
							}
							if (format == 2) {
								// The values of the face's texture vertices
								vt1 = vertexTextInd.get(textureIndex[index1]);
								vt2 = vertexTextInd.get(textureIndex[index2]);
								vt3 = vertexTextInd.get(textureIndex[index3]);
								Triangle tri = new Triangle(v1, v2, v3, vt1, vt2, vt3);
								tri.modelIndex = models.size();
								triangles.add(tri);
							}
							if (format == 3) {
								// The values of the face's texture vertices
								vt1 = vertexTextInd.get(textureIndex[index1]);
								vt2 = vertexTextInd.get(textureIndex[index2]);
								vt3 = vertexTextInd.get(textureIndex[index3]);
								// The values of the face's normal vectors
								vn1 = vertexNormInd.get(normalsIndex[index1]);
								vn2 = vertexNormInd.get(normalsIndex[index2]);
								vn3 = vertexNormInd.get(normalsIndex[index3]);
								Triangle tri = new Triangle(v1, v2, v3, vt1, vt2, vt3, vn1, vn2, vn3);
								tri.modelIndex = models.size();
								triangles.add(tri);
							}
							if (format == 4) {
								// The values of the face's normal vectors
								vn1 = vertexNormInd.get(normalsIndex[index1]);
								vn2 = vertexNormInd.get(normalsIndex[index2]);
								vn3 = vertexNormInd.get(normalsIndex[index3]);
								Triangle tri = new Triangle(v1, v2, v3, null, null, null, vn1, vn2, vn3);
								tri.modelIndex = models.size();
								triangles.add(tri);
							}
						}
						break;
					}
				}
				
				System.out.println("loaded model: " + modelname + " | " + vertexInd.size() + " vertices | " + num_faces + " triangles | " + texturepath);
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (scanner != null) {
					scanner.close();
				}
			}
			
			Triangle[] trianglesArray = triangles.toArray(new Triangle[0]);
			
			Model model = new Model(trianglesArray, imageBufferData, imageBufferWidth, imageBufferHeight);
			
			modelnames.add(modelname);
			models.add(model);
			/*String[] tempsArray = vertices.toArray(new String[0]);
			for (String s : tempsArray) {
				System.out.println(s);
			}*/
		} catch (FileNotFoundException e) {
			System.out.println("Cound not find file " + filename);
			e.printStackTrace();
		}
	}
	
	public static void loadObj(String filename, String modelname) {
		try {
			FileInputStream inputStream = null;
			Scanner scanner = null;
			
			List<double[]> vertexInd = null;
			List<double[]> vertexTextInd = null;
			List<double[]> vertexNormInd = null;
			List<Triangle> triangles = null;
			
			int num_faces = 0;
			try {
				inputStream = new FileInputStream(filename);
				scanner = new Scanner(inputStream);
				
				String title = null;
				vertexInd = new ArrayList<double[]>();
				vertexTextInd = new ArrayList<double[]>();
				vertexNormInd = new ArrayList<double[]>();
				triangles = new ArrayList<Triangle>();
			
				while (scanner.hasNextLine()) {
					String data = scanner.nextLine();
					
					String[] segments = data.split("\s+");
					if (segments.length == 0 || segments[0].contains("#") == true) continue;
					String type = segments[0];
					//System.out.println(type);
					
					switch(type) {
					case "g": 
					case "o": 
						title = segments[1];
						break;
					case "v": // List of geometric vertices, with (x, y, z [,w]) coordinates, w is optional and defaults to 1.0.
						vertexInd.add(new double[] { Double.parseDouble(segments[1]), Double.parseDouble(segments[2]), Double.parseDouble(segments[3]) });
						break;
					case "vt": // List of texture coordinates, in (u, [,v ,w]) coordinates, these will vary between 0 and 1. v, w are optional and default to 0.
						vertexTextInd.add(new double[] { Double.parseDouble(segments[1]), Double.parseDouble(segments[2]) });
						break;
					case "vn": // List of vertex normals in (x,y,z) form; normals might not be unit vectors.
						vertexNormInd.add(new double[] { Double.parseDouble(segments[1]), Double.parseDouble(segments[2]), Double.parseDouble(segments[3]) });
						break;
					case "f": // Polygonal face element. f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3
						int length = segments.length;
						int[] verticesIndex = new int[length-1];
						int[] textureIndex = new int[length-1];
						int[] normalsIndex = new int[length-1];
						for (int i = -1; i < length - 1; i++) {
							if (i == -1) continue;
							String[] indices = segments[i+1].split("/");
							if (indices.length > 0 && indices[0] != null && indices[0] != "") verticesIndex[i] = Integer.parseInt(indices[0]) - 1;
							if (indices.length > 1 && indices[1] != null && indices[1] != "") textureIndex[i] = Integer.parseInt(indices[1]) - 1;
							if (indices.length > 2 && indices[2] != null && indices[2] != "") normalsIndex[i] = Integer.parseInt(indices[2]) - 1;
						}
						
						int num_Triangles = verticesIndex.length - 2;

						String[] indices = segments[1].split("/");
						int format = 0;
						if (indices.length == 1) format = 1;
						if (indices.length == 2) format = 2;
						if (indices.length == 3 && indices[1] != "") format = 3;
						if (indices.length == 3 && indices[1] == "") format = 4;
						
						for (int i = 1; i <= num_Triangles; i++) {
							num_faces++;
							int index1 = 0, index2 = i, index3 = i + 1;
							
							double[] v1, v2, v3, vt1, vt2, vt3, vn1, vn2, vn3;
							// The values of the face's vertices
							v1 = vertexInd.get(verticesIndex[index1]);
							v2 = vertexInd.get(verticesIndex[index2]);
							v3 = vertexInd.get(verticesIndex[index3]);
							if (format == 1) {
								Triangle tri = new Triangle(v1, v2, v3);
								triangles.add(tri);
							}
							if (format == 2) {
								// The values of the face's texture vertices
								vt1 = vertexTextInd.get(textureIndex[index1]);
								vt2 = vertexTextInd.get(textureIndex[index2]);
								vt3 = vertexTextInd.get(textureIndex[index3]);
								Triangle tri = new Triangle(v1, v2, v3, vt1, vt2, vt3);
								triangles.add(tri);
							}
							if (format == 3) {
								// The values of the face's texture vertices
								vt1 = vertexTextInd.get(textureIndex[index1]);
								vt2 = vertexTextInd.get(textureIndex[index2]);
								vt3 = vertexTextInd.get(textureIndex[index3]);
								// The values of the face's normal vectors
								vn1 = vertexNormInd.get(normalsIndex[index1]);
								vn2 = vertexNormInd.get(normalsIndex[index2]);
								vn3 = vertexNormInd.get(normalsIndex[index3]);
								Triangle tri = new Triangle(v1, v2, v3, vt1, vt2, vt3, vn1, vn2, vn3);
								triangles.add(tri);
							}
							if (format == 4) {
								// The values of the face's normal vectors
								vn1 = vertexNormInd.get(normalsIndex[index1]);
								vn2 = vertexNormInd.get(normalsIndex[index2]);
								vn3 = vertexNormInd.get(normalsIndex[index3]);
								Triangle tri = new Triangle(v1, v2, v3, null, null, null, vn1, vn2, vn3);
								triangles.add(tri);
							}
						}
						break;
					}
				}
				
				System.out.println("loaded model: " + modelname + " | " + vertexInd.size() + " vertices | " + num_faces + " triangles");
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (scanner != null) {
					scanner.close();
				}
			}
			
			Triangle[] trianglesArray = triangles.toArray(new Triangle[0]);
			
			Model model = new Model(trianglesArray);
			
			modelnames.add(modelname);
			models.add(model);
			/*String[] tempsArray = vertices.toArray(new String[0]);
			for (String s : tempsArray) {
				System.out.println(s);
			}*/
		} catch (FileNotFoundException e) {
			System.out.println("Cound not find file " + filename);
			e.printStackTrace();
		}
	}
}
