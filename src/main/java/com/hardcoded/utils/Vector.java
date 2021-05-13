package com.hardcoded.utils;

public class Vector {
	public float x, y, z, w;
	public Vector(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	public Vector(float x, float y, float z) { this(x, y, z, 0); }
	public Vector(float x, float y) { this(x, y, 0, 0); }
	public Vector(float x) { this(x, 0, 0, 0); }
	public Vector() { this(0, 0, 0, 0); }
	
	public Vector div(Vector v) { return get(x / v.x, y / v.y, z / v.z, w / v.w); }
	public Vector mul(Vector v) { return get(x * v.x, y * v.y, z * v.z, w * v.w); }
	public Vector add(Vector v) { return get(x + v.x, y + v.y, z + v.z, w + v.w); }
	public Vector sub(Vector v) { return get(x - v.x, y - v.y, z - v.z, w - v.w); }
	public Vector set(Vector v) { x = v.x; y = v.y; z = v.z; w = v.w; return this; }
	public Vector set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
		return this;
	}
	public Vector set(float x, float y, float z) { return this.set(x, y, z, w); }
	public Vector set(float x, float y) { return this.set(x, y, z, w); }
	public Vector set(float x) { return this.set(x, y, z, w); }
	
	public static Vector get_plane(Vector v1, Vector v2, Vector v3) {
		float a1 = v2.x - v3.x;
		float b1 = v2.y - v3.y;
		float c1 = v2.z - v3.z;
		float a2 = v1.x - v3.x;
		float b2 = v1.y - v3.y;
		float c2 = v1.z - v3.z;
		
		float a = b1 * c2 - b2 * c1;
		float b = a2 * c1 - a1 * c2;
		float c = a1 * b2 - b1 * a2;
		float d = (-a * v1.x - b * v1.y - c * v1.z);
		return get(a, b, c, d);
	}
	/*public static Vector get_plane(Vector origin, Vector v1, Vector v2) {
		float a = v1.y * v2.z - v2.y * v1.z;
		float b = v2.x * v1.z - v1.x * v2.z;
		float c = v1.x * v2.y - v2.y * v1.x;
		float d = (-a * origin.x - b * origin.y - c * origin.z);
		return get(a, b, c, d);
	}*/
	
	public static Vector get(float x, float y, float z, float w) { return new Vector(x, y, z, w); }
	public static Vector get(float x, float y, float z) { return new Vector(x, y, z); }
	public static Vector get(float x, float y) { return new Vector(x, y); }
	public static Vector get(float x) { return new Vector(x); }
	public static Vector get() { return new Vector(); }
	public static Vector get(float[] axis) {
		if(axis.length > 3)      return new Vector(axis[0], axis[1], axis[2], axis[3]);
		else if(axis.length > 2) return new Vector(axis[0], axis[1], axis[2]);
		else if(axis.length > 1) return new Vector(axis[0], axis[1]);
		else if(axis.length > 0) return new Vector(axis[0]);
		return Vector.get();
	}
	
	public String toString() {
		return "Vector {" + x + ", " + y + ", " + z + ", " + w + "}";
	}
}
