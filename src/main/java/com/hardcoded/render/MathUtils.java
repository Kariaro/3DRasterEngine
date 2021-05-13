package com.hardcoded.render;

import com.hardcoded.utils.Vector;

public class MathUtils {
	private static double[] a = new double[65536];
	private static float[] b = new float[65536];
	
	public static double PI = Math.PI;
	
	public static final double sin(double f) {
		return a[(int)(f * 10430.378) & '\uffff'];
	}
	public static final double cos(double f) {
		return a[(int)(f * 10430.378 + 16384) & '\uffff'];
	}
	
	public static final float sinf(double f) {
		return b[(int)(f * 10430.378) & '\uffff'];
	}
	public static final float cosf(double f) {
		return b[(int)(f * 10430.378 + 16384) & '\uffff'];
	}
	
	public static final float[] inverseMatrix(float[] ver) {
		float x1 = ver[0], y1 = ver[1];
		float x2 = ver[3], y2 = ver[4];
		float x3 = ver[6], y3 = ver[7];
		float A = (x1*y2 + y1*x3 + x2*y3) - (y2*x3 + x1*y3 + y1*x2);
		/*
		return new float[] {
			(y2-y3) / A, (y3-y1) / A, (y1-y2) / A,
			(x3-x2) / A, (x1-x3) / A, (x2-x1) / A,
			(x2*y3-x3*y2) / A, (x3*y1-x1*y3) / A, (x1*y2-x2*y1) / A
		};*/
		return new float[] {
			(y2-y3) / A, (x3-x2) / A, (x2*y3-x3*y2) / A,
			(y3-y1) / A, (x1-x3) / A, (x3*y1-x1*y3) / A,
			(y1-y2) / A, (x2-x1) / A, (x1*y2-x2*y1) / A
		};
	}
	
	public static final float[] multiplyMatrix(float[] ver, Vector mul) {
		float x1 = ver[0], y1 = ver[1], z1 = ver[2];
		float x2 = ver[3], y2 = ver[4], z2 = ver[5];
		float x3 = ver[6], y3 = ver[7], z3 = ver[8];
		
		return new float[] {
			x1 * mul.x + y1 * mul.y + z1 * mul.z,
			x2 * mul.x + y2 * mul.y + z2 * mul.z,
			x3 * mul.x + y3 * mul.y + z3 * mul.z,
		};
	}
	
	static {
		for(int i = 0; i < 65536; i++) {
			a[i] = Math.sin((double)i * 3.141592653589793D * 2.0D / 65536.0D);
			b[i] = (float)Math.sin((double)i * 3.141592653589793D * 2.0D / 65536.0D);
		}
	}
}
