package com.hardcoded.render;

import java.util.ArrayList;
import java.util.List;

import com.hardcoded.Art;

public class RenderUtils {
	private static TriangleStruct struct;
	private static List<TriangleStruct> list = new ArrayList<TriangleStruct>();
	
	public static void iStart() {
		struct = TriangleStruct.create();
	}
	
	public static void iStart(Art texture) {
		struct = TriangleStruct.create(texture);
	}
	
	public static void iClose() {
		if(struct != null) list.add(struct);
		struct = null;
	}
	
	private static float tx, ty, tz;
	public static void iTranslate(float x, float y, float z) {
		tx = x;
		ty = y;
		tz = z;
	}
	
	public static void iVertex(float[] v) {
		struct.iVertex(tx, ty, tz, v);
	}
	
	public static void iVertex(float x, float y, float z) {
		struct.iVertex(x, y, z);
	}
	
	public static void iColor(float r, float g, float b) {
		struct.iColor(r, g, b);
	}
	
	public static void iUv(float u, float v) {
		struct.iUv(u, v);
	}
	
	static TriangleStruct[] collect() {
		TriangleStruct[] array = list.toArray(new TriangleStruct[0]);
		list.clear();
		return array;
	}
}
