package com.hardcoded.terrain;

import java.util.Random;

public class Noise {
	private long seed;
	private double[] array = new double[27];
	private double[][] array2 = new double[8][3];
	
	public Noise(long seed) {
		generateBox(0, 0, 0);
		this.seed = seed;
	}
	
	public void setSeed(long seed) {
		this.seed = seed;
	}
	
	public long getSeed() {
		return seed;
	}
	
	public void move(int x, int y, int z) {
		generateBox(x, y, z);
	}
	
	public double getValue(int index) {
		return array[index];
	}
	
	private void generateBox(int x, int y, int z) {
		for(int i = 0; i < array.length; i++) {
			int nx = ((i % 3)    ) + x - 1;
			int ny = ((i / 9)    ) + y - 1;
			int nz = ((i / 3) % 3) + z - 1;
			
			Random random = new Random(uniquePosition(nx, ny, nz));
			array[i] = (random.nextGaussian() + random.nextGaussian() + random.nextGaussian()) / 3.0;
		}
		
		for(int i = 0; i < array2.length; i++) {
			int nx = ((i % 2)    ) + x - 1;
			int ny = ((i / 4)    ) + y - 1;
			int nz = ((i / 2) & 1) + z - 1;
			
			Random random = new Random(uniquePosition(nx, ny, nz));
			array2[i] = new double[] {
				random.nextGaussian(),
				random.nextGaussian(),
				random.nextGaussian()
			};
		}
	}
	
	private long uniquePosition(int cx, int cy, int cz) {
		long x = Integer.toUnsignedLong(cx) & 0xFFFFFFFL;
		long y = Integer.toUnsignedLong(cy) & 0xFFL;
		long z = Integer.toUnsignedLong(cz) & 0xFFFFFFFL;
		return (z << 36L) | (y << 28L) | (x);
	}
	
	public double interpolate(double x, double y, double z) {
		int xyz = (y < 8 ? 0:9) + (z < 8 ? 0:3) + (x < 8 ? 0:1);
		
		double x000 = 0, x001 = 0;
		double x100 = 0, x101 = 0;
		double x010 = 0, x011 = 0;
		double x110 = 0, x111 = 0;
		
		x000 = array[ 0 + xyz]; x001 = array[ 1 + xyz];
		x100 = array[ 3 + xyz]; x101 = array[ 4 + xyz];
		x010 = array[ 9 + xyz]; x011 = array[10 + xyz];
		x110 = array[12 + xyz]; x111 = array[13 + xyz];
		
		double yi = (y / 16.0) + (y < 8 ? 0.5:-0.5);
		double y00 = linear_interpolation(x000, x010, yi);
		double y01 = linear_interpolation(x001, x011, yi);
		double y10 = linear_interpolation(x100, x110, yi);
		double y11 = linear_interpolation(x101, x111, yi);
		
		double xi = (x / 16.0) + (x < 8 ? 0.5:-0.5);
		double x0 = linear_interpolation(y00, y01, xi);
		double x1 = linear_interpolation(y10, y11, xi);
		
		double zi = (z / 16.0) + (z < 8 ? 0.5:-0.5);
		return linear_interpolation(x0, x1, zi);
	}
	
	private double val(double x, double y, double z, double val) {
		double sin = sin(2 * Math.PI * val);
		double cos = cos(2 * Math.PI * val);
		
		double lx = x / 16.0;
		double lz = z / 16.0;
		
		double cx = sin * (lx * sin + lz * cos - 1);
		double cz = cos * (lx * sin + lz * cos - 1);
		
		return 1 - Math.sqrt(cx * cx + cz * cz);
	}
	public double interpolate_perlin(double x, double y, double z) {
		int xyz = 0/*(y < 0 ? 0:9) + (z < 0 ? 0:3) + (x < 0 ? 0:1)*/;
		
		double x000 = val(x, y, z, array[ 0 + xyz]), x001 = val(x, y, z, array[ 1 + xyz]);
		double x100 = val(x, y, z, array[ 3 + xyz]), x101 = val(x, y, z, array[ 4 + xyz]);
		double x010 = val(x, y, z, array[ 9 + xyz]), x011 = val(x, y, z, array[10 + xyz]);
		double x110 = val(x, y, z, array[12 + xyz]), x111 = val(x, y, z, array[13 + xyz]);
		
		double xi = ((x + 8) / 16.0) + (x < 0 ? 0.5:-0.5);
		double x0 = linear_interpolation(x000, x001, xi);
		double x1 = linear_interpolation(x100, x101, xi);
		
		double zi = ((z + 8) / 16.0) + (z < 0 ? 0.5:-0.5);
		return x000;//linear_interpolation(x0, x1, zi);
		
		/*
		double yi = (y / 16.0) - (y < 0 ? 0.5:-0.5);
		double y00 = linear_interpolation(x000, x010, yi);
		double y01 = linear_interpolation(x001, x011, yi);
		double y10 = linear_interpolation(x100, x110, yi);
		double y11 = linear_interpolation(x101, x111, yi);
		
		double xi = (x / 16.0) - (x < 0 ? 0.5:-0.5);
		double x0 = linear_interpolation(y00, y01, xi);
		double x1 = linear_interpolation(y10, y11, xi);
		
		double zi = (z / 16.0) - (z < 0 ? 0.5:-0.5);
		return linear_interpolation(x0, x1, zi);*/
	}
	
	public double interpolate_2d(double x, double z) {
		double xi = (x / 16.0) + (x < 8 ? 0.5:-0.5);
		double zi = (z / 16.0) + (z < 8 ? 0.5:-0.5);
		int xz = (x < 8 ? 0:1) + (z < 8 ? 0:3);
		
		double x0 = cosine_interpolate(array[0 + xz], array[1 + xz], xi);
		double x1 = cosine_interpolate(array[3 + xz], array[4 + xz], xi);
		return cosine_interpolate(x0, x1, zi);
	}
	
	private double linear_interpolation(double d0, double d1, double i) {
		return (d1 - d0) * i + d0;
	}
	
	private double cosine_interpolate(double d0, double d1, double i) {
		return ((1 - Math.cos(i * Math.PI)) / 2.0) * (d1 - d0) + d0;
	}
	
	private static final double[] sin_d = new double[65536];
	private static double sin(double d) {
		return sin_d[(int)(d * 10430.378F) & '\uffff'];
	}
	private static final double cos(double d) {
		return sin_d[(int)(d * 10430.378F + 16384.0F) & '\uffff'];
	}
	
	static {
		for(int i = 0; i < 65536; i++) {
			sin_d[i] = Math.sin((double)i * 3.141592653589793D * 2.0D / 65536.0D);
		}
	}
	

	public double lerp(double a0, double a1, double w) {
		return (1.0 - w) * a0 + w * a1;
	}
	
	public double dotGridGradient(int ix, int iy, int iz, double x, double y, double z) {
		// Compute the distance vector
		iz = 0;
		double dx = x - ix;
		double dy = y - iy;
		double dz = z - iz;
		
		return dx * array2[(ix + iy * 2 + iz * 4) & 7][0]
			 + dz * array2[(ix + iy * 2 + iz * 4) & 7][1]/*
			 + dy * array2[(ix + iy * 2 + iz * 4) & 7][2]*/;
	}
	
	public double perlin(double x, double y, double z) {
		x *= 0.0625;
		y *= 0.0625;
		z *= 0.0625;
		
		int x0 = (int)x;
		int x1 = x0 + 1;
		
		int y0 = (int)y;
		int y1 = y0 + 1;
		
		int z0 = (int)z;
		int z1 = z0 + 1;
		
		double sx = x - x0;
		double sy = y - y0;
		double sz = z - z0;
		/*
		double n000 = dotGridGradient(x0, y0, z0, x, y, z);
		double n001 = dotGridGradient(x0, y0, z1, x, y, z);
		double n100 = dotGridGradient(x1, y0, z0, x, y, z);
		double n101 = dotGridGradient(x1, y0, z1, x, y, z);
		double n00 = lerp(n000, n001, sz);
		double n01 = lerp(n100, n101, sz);
		*/
		double n010 = dotGridGradient(x0, 0, z0, x, y, z);
		double n011 = dotGridGradient(x1, 0, z0, x, y, z);
		double n110 = dotGridGradient(x0, 0, z1, x, y, z);
		double n111 = dotGridGradient(x1, 0, z1, x, y, z);
		double n10 = lerp(n010, n011, sx);
		double n11 = lerp(n110, n111, sx);
		
		//double n0 = lerp(n00, n01, sx);
		//double n1 = lerp(n10, n11, sx);
		return lerp(n11, n10, sz) * 100;
	}
}
