package com.hardcoded.utils;

public class Vector3 {
	public double x, y, z, w;
	public Vector3(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	public Vector3(double x, double y, double z) { this(x, y, z, 0); }
	public Vector3(double x, double y) { this(x, y, 0, 0); }
	public Vector3(double x) { this(x, 0, 0, 0); }
	public Vector3() { this(0, 0, 0, 0); }
	
	public Vector3 div(Vector3 v) { return get(x / v.x, y / v.y, z / v.z, w / v.w); }
	public Vector3 div(double d)  { return get(x / d, y / d, z / d, w / d); }
	public Vector3 mul(Vector3 v) { return get(x * v.x, y * v.y, z * v.z, w * v.w); }
	public Vector3 mul(double d) { return get(x * d, y * d, z * d, w * d); }
	public Vector3 add(Vector3 v) { return get(x + v.x, y + v.y, z + v.z, w + v.w); }
	public Vector3 sub(Vector3 v) { return get(x - v.x, y - v.y, z - v.z, w - v.w); }
	public Vector3 set(Vector3 v) { x = v.x; y = v.y; z = v.z; w = v.w; return this; }
	public Vector3 set(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}
	public Vector3 set(double x, double y, double z) { return this.set(x, y, z, w); }
	public Vector3 set(double x, double y) { return this.set(x, y, z, w); }
	public Vector3 set(double x) { return this.set(x, y, z, w); }
	
	public double dot(Vector3 v) {
		return x * v.x + y * v.y + z * v.z + w * v.w;
	}
	public Vector3 toEulerAngles() {
		if(z == 0) z = 0;
		if(y == 0) y = 0;
		if(x == 0) x = 0;
		
		double len = Math.sqrt(x * x + z * z);
		double ax = -Math.toDegrees(Math.atan(x / z));
		double ay = -Math.toDegrees(Math.atan(y / len));
		if(z < 0) ax += 180;
		//if(z == 0) ax = 0;
		//if(x == 0) ax = 0;
		
		ax = (ax + 3600) % 360;
		ay = (ay + 3600) % 360;
		
		if(ax != ax) ax = 0;
		if(ay != ay) ay = 0;
		
		return Vector3.get(ax, ay, 0);
	}
	public Vector3 cross(Vector3 v) {
		return get(
			y * v.z - z * v.y,
			z * v.x - x * v.z,
			x * v.y - y * v.x
		);
	}
	public double len() {
		return (double)Math.sqrt(x * x + y * y + z * z);
	}
	public double[] getArray() { return new double[] { x, y, z }; }
	
	public static Vector3 get_plane(Vector3 v1, Vector3 v2, Vector3 v3) {
		double a1 = v2.x - v3.x;
		double b1 = v2.y - v3.y;
		double c1 = v2.z - v3.z;
		double a2 = v1.x - v3.x;
		double b2 = v1.y - v3.y;
		double c2 = v1.z - v3.z;
		
		double a = b1 * c2 - b2 * c1;
		double b = a2 * c1 - a1 * c2;
		double c = a1 * b2 - b1 * a2;
		double d = (-a * v1.x - b * v1.y - c * v1.z);
		return get(a, b, c, d);
	}
	/*public static Vector get_plane(Vector origin, Vector v1, Vector v2) {
		double a = v1.y * v2.z - v2.y * v1.z;
		double b = v2.x * v1.z - v1.x * v2.z;
		double c = v1.x * v2.y - v2.y * v1.x;
		double d = (-a * origin.x - b * origin.y - c * origin.z);
		return get(a, b, c, d);
	}*/
	
	public void zeroNaN() {
		if(x != x) x = 0;
		if(y != y) y = 0;
		if(z != z) z = 0;
		if(w != w) w = 0;
	}
	
	public boolean hasNaN() { return x != x || y != y || z != z || w != w; }
	
	public static Vector3 get(double x, double y, double z, double w) { return new Vector3(x, y, z, w); }
	public static Vector3 get(double x, double y, double z) { return new Vector3(x, y, z); }
	public static Vector3 get(double x, double y) { return new Vector3(x, y); }
	public static Vector3 get(double x) { return new Vector3(x); }
	public static Vector3 get() { return new Vector3(); }
	public static Vector3 get(double[] axis) {
		if(axis.length > 3)      return new Vector3(axis[0], axis[1], axis[2], axis[3]);
		else if(axis.length > 2) return new Vector3(axis[0], axis[1], axis[2]);
		else if(axis.length > 1) return new Vector3(axis[0], axis[1]);
		else if(axis.length > 0) return new Vector3(axis[0]);
		return Vector3.get();
	}
	
	public static Vector3 get(int offset, int count, double[] axis) {
		int len = axis.length - offset;
		if(len > 3 & count > 3)      return new Vector3(axis[offset], axis[offset + 1], axis[offset + 2], axis[offset + 3]);
		else if(len > 2 && count > 2) return new Vector3(axis[offset], axis[offset + 1], axis[offset + 2]);
		else if(len > 1 && count > 1) return new Vector3(axis[offset], axis[offset + 1]);
		else if(len > 0 && count > 0) return new Vector3(axis[offset]);
		return Vector3.get();
	}
	public static Vector3 get(int offset, double[] axis) {
		return Vector3.get(offset, 4, axis);
	}
	
	public String toString() {
		return "Vector {" + x + ", " + y + ", " + z + ", " + w + "}";
	}
}
