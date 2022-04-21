package engine.matrix;

import engine.quaternion.Quaternion;
import engine.vector.Vector3;

public class Mat4x4 {
	
	public double[][] m = new double[4][4];
	
	public Mat4x4() {
		Mat4x4.makeBlank(this);
	}
	
	// Create an empty 4x4 matrix
	public static void makeBlank(Mat4x4 mat) {
		mat.m[0][0] = 0;
		mat.m[1][0] = 0;
		mat.m[2][0] = 0;
		mat.m[3][0] = 0;
		
		mat.m[0][1] = 0;
		mat.m[1][1] = 0;
		mat.m[2][1] = 0;
		mat.m[3][1] = 0;

		mat.m[0][2] = 0;
		mat.m[1][2] = 0;
		mat.m[2][2] = 0;
		mat.m[3][2] = 0;

		mat.m[0][3] = 0;
		mat.m[1][3] = 0;
		mat.m[2][3] = 0;
		mat.m[3][3] = 0;

		return;
	}
	
	// Create a 4x4 matrix identity
	public static Mat4x4 makeIdentity(Mat4x4 mat) {
		Mat4x4.makeBlank(mat);
		mat.m[0][0] = 1;
		mat.m[1][1] = 1;
		mat.m[2][2] = 1;
		mat.m[3][3] = 1;
		
		return mat;
	}
	
	public String toString() {
		return m[0][0] + " " + m[1][0] + " " + m[2][0] + " " + m[3][0] + " | " + 
				m[0][1] + " " + m[1][1] + " " + m[2][1] + " " + m[3][1] + " | " + 
				m[0][2] + " " + m[1][2] + " " + m[2][2] + " " + m[3][2] + " | " + 
				m[0][3] + " " + m[1][3] + " " + m[2][3] + " " + m[3][3];
	}

	// Create a matrix which can be multiplied to another matrix to make a translation
	public static Mat4x4 translationMatrix(double x, double y, double z) {
		Mat4x4 mat = new Mat4x4();
		mat.m[0][0] = 1;
		mat.m[1][1] = 1;
		mat.m[2][2] = 1;
		mat.m[3][3] = 1;
		mat.m[3][0] = x;
		mat.m[3][1] = y;
		mat.m[3][2] = z;
		
		return mat;
	}
	
	// Multiply two 4x4 matrices together
	public static Mat4x4 multiplyMatrix(Mat4x4 m1, Mat4x4 m2) {
		Mat4x4 mat = new Mat4x4();
		
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 4; i++) {
				mat.m[i][j] = m1.m[i][0] * m2.m[0][j] + m1.m[i][1] * m2.m[1][j] + m1.m[i][2] * m2.m[2][j] + m1.m[i][3] * m2.m[3][j];
			}
		}
		
		return mat;
	}
	
	// Quickly invert a 4x4 matrix
	public static Mat4x4 quickInverse(Mat4x4 m) {
		Mat4x4 mat = new Mat4x4();
		mat.m[0][0] = m.m[0][0]; mat.m[0][1] = m.m[1][0]; mat.m[0][2] = m.m[2][0]; mat.m[0][3] = 0;
		mat.m[1][0] = m.m[0][1]; mat.m[1][1] = m.m[1][1]; mat.m[1][2] = m.m[2][1]; mat.m[1][3] = 0;
		mat.m[2][0] = m.m[0][2]; mat.m[2][1] = m.m[1][2]; mat.m[2][2] = m.m[2][2]; mat.m[2][3] = 0;
		mat.m[3][0] = -(m.m[3][0] * mat.m[0][0] + m.m[3][1] * mat.m[1][0] + m.m[3][2] * mat.m[2][0]);
		mat.m[3][1] = -(m.m[3][0] * mat.m[0][1] + m.m[3][1] * mat.m[1][1] + m.m[3][2] * mat.m[2][1]);
		mat.m[3][2] = -(m.m[3][0] * mat.m[0][2] + m.m[3][1] * mat.m[1][2] + m.m[3][2] * mat.m[2][2]);
		mat.m[3][3] = 1;
		
		return mat;
	}
	
	// Create a projection matrix from specified values
	public static Mat4x4 makeProjection(double fovDegrees, int screenHeight, int screenWidth, double near, double far) {
		double aspectRatio = (double) screenHeight / screenWidth;
		double FovRad = 1 / Math.tan(fovDegrees * 0.5 / 180 * Math.PI);
		Mat4x4 matrix = new Mat4x4();
		matrix.m[0][0] = aspectRatio * FovRad;
		matrix.m[1][1] = FovRad;
		matrix.m[2][2] = far / (far - near);
		matrix.m[3][2] = (-far * near) / (far - near);
		matrix.m[2][3] = 1;
		matrix.m[3][3] = 0;
		return matrix;
	}
	
	public static Mat4x4 inertiaTensor(double mass, Vector3 r) {
		double x = r.x, y = r.y, z = r.z;
		Mat4x4 mat = new Mat4x4();
		mat.m[0][0] = mass * (y * y + z * z);
		mat.m[1][1] = mass * (x * x + z * z);
		mat.m[2][2] = mass * (x * x + y * y);
		mat.m[3][3] = 1;
		mat.m[0][1] = mat.m[1][0] = -mass * x * y;
		mat.m[0][2] = mat.m[2][0] = -mass * x * z;
		mat.m[1][2] = mat.m[2][1] = -mass * y * z;
		return mat;
	}
	
	public static Mat4x4 generateMatrix(Quaternion q1, Vector3 pos, Vector3 scale) {
		double w = q1.w, x = q1.x, y = q1.y, z = q1.z;
		double sqx = x * x, sqy = y * y, sqz = z * z;
		
		Mat4x4 mat = new Mat4x4();
		mat.m[0][0] = 1 - (2 * sqy) - (2 * sqz);
		mat.m[0][1] = (2 * x * y) - (2 * w * z);
		mat.m[0][2] = (2 * x * z) + (2 * w * y);
		mat.m[1][0] = (2 * x * y) + (2 * w * z);
		mat.m[1][1] = 1 - (2 * sqx) - (2 * sqz);
		mat.m[1][2] = (2 * y * z) - (2 * w * x);
		mat.m[2][0] = (2 * x * z) - (2 * w * y);
		mat.m[2][1] = (2 * y * z) + (2 * w * x);
		mat.m[2][2] = 1 - (2 * sqx) - (2 * sqy);
		mat.m[3][3] = 1;
		if (pos != null) {
			mat.m[3][0] = pos.x;
			mat.m[3][1] = pos.y;
			mat.m[3][2] = pos.z;
		}
		if (scale != null) {
			mat.m[0][0] *= scale.x;
			mat.m[1][1] *= scale.y;
			mat.m[2][2] *= scale.z;
		}
		return mat;
	}
}
