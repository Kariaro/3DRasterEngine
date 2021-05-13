package com.hardcoded.render;

import java.nio.FloatBuffer;

import com.hardcoded.Art;

public class TriangleStruct {
	public static final int POINTS = 512;
	
	public Art texture;
	public FloatBuffer vertex;
	public FloatBuffer color;
	public FloatBuffer uv;
	private int index = 0;
	
	private TriangleStruct(Art tex) {
		vertex = FloatBuffer.allocate(3 * POINTS);
		color = FloatBuffer.allocate(3 * POINTS);
		uv = FloatBuffer.allocate(2 * POINTS);
		texture = tex;
	}
	
	public TriangleStruct iColor(float r, float g, float b) {
		color.put(index * 3    , r);
		color.put(index * 3 + 1, g);
		color.put(index * 3 + 2, b);
		return this;
	}
	
	public TriangleStruct iUv(float u, float v) {
		uv.put(index * 2    , u);
		uv.put(index * 2 + 1, v);
		return this;
	}
	
	public TriangleStruct iVertex(float x, float y, float z) {
		vertex.put(index * 3    , x);
		vertex.put(index * 3 + 1, y);
		vertex.put(index * 3 + 2, z);
		index++;
		return this;
	}
	
	public TriangleStruct iVertex(float tx, float ty, float tz, float[] ver) {
		for(int i = 0; i < ver.length / 3; i++)
			iVertex(ver[i * 3] + tx, ver[i * 3 + 1] + ty, ver[i * 3 + 2] + tz);
		return this;
	}
	
	public int size() {
		return index;
	}
	
	public static TriangleStruct create(Art tex) { return new TriangleStruct(tex); }
	public static TriangleStruct create() { return new TriangleStruct(null); }
}
