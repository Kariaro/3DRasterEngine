package com.hardcoded.render;

import static com.hardcoded.render.RenderUtils.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.nio.FloatBuffer;
import java.util.Random;

import com.hardcoded.Art;
import com.hardcoded.InputListener;
import com.hardcoded.Main;
import com.hardcoded.utils.Vector;

public class Render {
	public static final Random random = new Random(2);
	private final float ratio;
	public final int[] pixels;
	public final int[] zbuff;
	public final int W, H;
	
	// private TriangleStruct[] plane_Terrain_test_struct;
	public float[][] plane_Terrain_test;
	public Render(int WIDTH, int HEIGHT) {
		this.pixels = new int[WIDTH * HEIGHT];
		this.zbuff = new int[WIDTH * HEIGHT];
		this.ratio = WIDTH / (HEIGHT + 0.0f);
		this.W = WIDTH;
		this.H = HEIGHT;
		
		plane_Terrain_test = Terrain.generateTerrain(100, 100, 10.0f, 4);
		//plane_Terrain_test = Terrain.generateTerrain(10, 10, 10.0f, 4);
		for(int i = 0; i < plane_Terrain_test.length; i++) {
			for(int j = 0; j < plane_Terrain_test[i].length; j++) {
				plane_Terrain_test[i][j] = (int)plane_Terrain_test[i][j];
			}
		}
		
		/*
		float[] uv = new float[] { 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1 };
		for(int i = 0; i < plane_Terrain_test.length; i++) {
			float[] vertex = plane_Terrain_test[i];
			
			iStart(Art.brick2);
			for(int j = 0; j < vertex.length / 3 - 2; j += 2) {
				int Id = j * 3;
				iUv(uv[0], uv[1]); iColor(random.nextFloat(), random.nextFloat(), random.nextFloat());
				iVertex(vertex[Id], vertex[Id + 1], vertex[Id + 2] + i);
				
				iUv(uv[2], uv[3]); iColor(random.nextFloat(), random.nextFloat(), random.nextFloat());
				iVertex(vertex[Id + 3], vertex[Id + 4], vertex[Id + 5] + i);
				
				iUv(uv[4], uv[5]); iColor(random.nextFloat(), random.nextFloat(), random.nextFloat());
				iVertex(vertex[Id + 6], vertex[Id + 7], vertex[Id + 8] + i);
				
				iUv(uv[6], uv[7]); iColor(random.nextFloat(), random.nextFloat(), random.nextFloat());
				iVertex(vertex[Id + 9], vertex[Id + 10], vertex[Id + 11] + i);
			}
			iClose();
		}
		
		plane_Terrain_test_struct = collect();
		*/
	}
	
	private final int Z_DEPTH = 0x10000;

	private Art texture;
	private float near = 1.0f;
	private float fov  = 1.0f;
	private float far  = 1.0f;
	private float Tx, Ty, Tz;
	private float Rx, Ry, Rz;
	
	public void render() {
		random.setSeed(0);
		setFov(1.8f);
		setNear(0.05f);
		
		clearZbuff();
		
		renderFloor();
		renderCeiling();
		
		/*{
			TriangleStruct[] tri = collect();
			
			for(TriangleStruct t : tri) {
				bindTexture(t.texture);
				renderBuffer(t.vertex, t.uv, t.color, t.size());
			}
			
			for(TriangleStruct t : plane_Terrain_test_struct) {
				bindTexture(t.texture);
				renderBuffer(t.vertex, t.uv, t.color, t.size());
			}
		}*/
		
		tris = 0;
	}
	
	public void clearZbuff() {
		for(int i = 0; i < W * H; i++)
			zbuff[i] = 0x7fffffff;
	}
	
	public void renderCeiling() { // TODO: renderCeiling
		float[] N_U = { 0, 0, 1,  0, 0, 0,  1, 0, 1,  1, 0, 0, };
		float[] N_D = { 0, 1, 0,  0, 1, 1,  1, 1, 0,  1, 1, 1, };
		float[] N_L = { 0, 0, 1,  0, 1, 1,  0, 0, 0,  0, 1, 0, };
		float[] N_R = { 1, 0, 0,  1, 1, 0,  1, 0, 1,  1, 1, 1, };
		float[] N_F = { 0, 0, 0,  0, 1, 0,  1, 0, 0,  1, 1, 0, };
		float[] N_B = { 1, 0, 1,  1, 1, 1,  0, 0, 1,  0, 1, 1, };
		float[][] N = { N_U, N_D, N_L, N_R, N_F, N_B };
		
		float[] uv = new float[] { 0, 1, 0, 0, 1, 1, 1, 0 };
		float ts = ((System.currentTimeMillis() * 3) % 64000 - 32000) / 64000.0f;
		ts *= 360;
		//ts = 0;
		
		for(int i = 0; i < N.length; i++) {
			float[] polygon = Matrix(-0.5f, -0.5f, -0.5f, ts * 3, ts * 4, ts * 2, N[i]);
			for(int j = 0; j < polygon.length; j++) N[i][j] = polygon[j];
		}
		
		setCameraTransform(0, 0, 1);
		setCameraRotation(0, 0, 0);
		
		bindTexture(null);
		bindTexture(Art.brick);
		TriangleFan(0, 0, 1, N_R, uv, 0xff0000);
		TriangleFan(0, 0, 1, N_B, uv, 0xff0000);
		TriangleFan(0, 0, 1, N_L, uv, 0xff0000);
		TriangleFan(0, 0, 1, N_F, uv, 0xff0000);
		TriangleFan(0, 0, 1, N_U, uv, 0xff0000);
		TriangleFan(0, 0, 1, N_D, uv, 0xff0000);
		unbindTexture();
		
		/*
		iStart(null);
			iColor(1, 1, 0); iVertex(0, 0, 1);
			iColor(1, 0, 1); iVertex(0, 0, 0);
			iColor(0, 1, 1); iVertex(1, 0, 1);
			iColor(1, 1, 1); iVertex(1, 0, 0);
		iClose();
		*/
	}
	
	public void renderFloor() {
		float[] N_U3 = { 0, 0, 1,  0, 0, 0,  1, 0, 1,  1, 0, 0 };
		for(int i = 0; i < 12; i++) N_U3[i] *= 100;
		
		float[] N_U  = { 0, 0, 1,  0, 0, 0,  1, 0, 1,  1, 0, 0  };
		float[] N_D  = { 0, 1, 0,  0, 1, 1,  1, 1, 0,  1, 1, 1, };
		float[] N_L  = { 0, 0, 1,  0, 1, 1,  0, 0, 0,  0, 1, 0, };
		float[] N_R  = { 1, 0, 0,  1, 1, 0,  1, 0, 1,  1, 1, 1, };
		float[] N_F  = { 0, 0, 0,  0, 1, 0,  1, 0, 0,  1, 1, 0, };
		float[] N_B  = { 1, 0, 1,  1, 1, 1,  0, 0, 1,  0, 1, 1, };
		
		float[] uv = new float[] { 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1 };
		
		/*
		TriangleFan(0, 0, 1, N_R, uv, 0xff0000);
		TriangleFan(0, 0, 1, N_B, uv, 0xff0000);
		TriangleFan(0, 0, 1, N_L, uv, 0xff0000);
		TriangleFan(0, 0, 1, N_F, uv, 0xff0000);
		TriangleFan(0, 0, 1, N_U, uv, 0xff0000);
		TriangleFan(0, 0, 1, N_D, uv, 0xff0000);
		*/
		
		// TriangleFan(0, -6.5f, 1, N_U3, uv, 0xff0000); // test14(0, -7, 1, N_U3, 0x0f0000);
		
		bindTexture(Art.grass_small);
		unbindTexture();
		
		/*
		for(int i = 0; i < plane_Terrain_test.length; i++) {
			TriangleFan(0, 0, i, plane_Terrain_test[i], uv, 0xffffff);
		}*/
		
		for(int i = 0; i < 50; i++) {
			// cout << "Triangle: " << i << endl;
			for(int v = 0; v < 50; v++) {
				TriangleFan(i - 25, 10, v - 25, N_U, uv, 0xffffff);
			}
		}
		
		/*
		for(int i = 0; i < plane_Terrain_test.length; i++) {
			float[] vertex = plane_Terrain_test[i];
			
			iStart(Art.brick2);
			for(int j = 0; j < vertex.length / 3 - 2; j += 2) {
				int Id = j * 3;
				iUv(uv[0], uv[1]); iColor(random.nextFloat(), random.nextFloat(), random.nextFloat());
				iVertex(vertex[Id], vertex[Id + 1], vertex[Id + 2] + i);
				
				iUv(uv[2], uv[3]); iColor(random.nextFloat(), random.nextFloat(), random.nextFloat());
				iVertex(vertex[Id + 3], vertex[Id + 4], vertex[Id + 5] + i);
				
				iUv(uv[4], uv[5]); iColor(random.nextFloat(), random.nextFloat(), random.nextFloat());
				iVertex(vertex[Id + 6], vertex[Id + 7], vertex[Id + 8] + i);
				
				iUv(uv[6], uv[7]); iColor(random.nextFloat(), random.nextFloat(), random.nextFloat());
				iVertex(vertex[Id + 9], vertex[Id + 10], vertex[Id + 11] + i);
			}
			iClose();
		}*/
	}
	
	public void setFov(float fov) {
		this.fov = fov;
	}
	public void setNear(float near) {
		this.near = near;
	}
	public float getNear() {
		return near;
	}
	public Art bindTexture(Art texture) {
		Art old = this.texture;
		this.texture = texture;
		return old;
	}
	public Art unbindTexture() {
		Art old = this.texture;
		this.texture = null;
		return old;
	}
	public void setCameraTransform(float tx, float ty, float tz) {
		this.Tx = tx;
		this.Ty = ty;
		this.Tz = tz;
	}
	public void setCameraRotation(float rx, float ry, float rz) {
		this.Rx = rx;
		this.Ry = ry;
		this.Rz = rz;
	}
	
	public void TriangleFan(float x, float y, float z, float[] ve, float[] uv_p, int color) {
		TriangleFan(x, y, z, ve, uv_p, new float[] { 1, 1, 0, 1, 0, 1, 0, 1, 1 }, false, 0);
	}
	
	public void TriangleFan(float[] ve, float[] uv_p, float[] color, boolean fix, int offset) {
		TriangleFan(0, 0, 0, ve, uv_p, color, fix, offset);
	}
	
	private final void renderBuffer(FloatBuffer vertex_buffer, FloatBuffer uv_buffer, FloatBuffer color_buffer, int size) {
		int A = W / 2, B = H / 2;
		
		float[] color = color_buffer.array();
		float[] ve = vertex_buffer.array();
		float[] uv = uv_buffer.array();
		
		float[] ver = Matrix(Tx, Ty, Tz, ve);
		
		float near = this.near * fov;
		for(int Triangle = 0; Triangle < size - 2; Triangle++) {
			int Id = Triangle * 3;
			if(ver[Id + 2] < this.near && ver[Id + 5] < this.near && ver[Id + 8] < this.near) continue;
			
			float z1 = ver[Id + 2] * fov,
				  z2 = ver[Id + 5] * fov,
				  z3 = ver[Id + 8] * fov;
			
			float[] uv_p = {
				uv[Triangle * 2    ], uv[Triangle * 2 + 1],
				uv[Triangle * 2 + 2], uv[Triangle * 2 + 3],
				uv[Triangle * 2 + 4], uv[Triangle * 2 + 5],
			};
			
			float[] co_p = {
				color[Id    ], color[Id + 1], color[Id + 2],
				color[Id + 3], color[Id + 4], color[Id + 5],
				color[Id + 6], color[Id + 7], color[Id + 8],
			};
			
			if(z1 < near || z2 < near || z3 < near) {
				renderTriangle(new float[] {
					ver[Id + 0], ver[Id + 1], ver[Id + 2], 
					ver[Id + 3], ver[Id + 4], ver[Id + 5], 
					ver[Id + 6], ver[Id + 7], ver[Id + 8], 
				}, uv_p, co_p, Triangle);
				
				continue;
			}
			
			float x1 = A + (ver[Id    ] * W) / (z1 * ratio);
			float x2 = A + (ver[Id + 3] * W) / (z2 * ratio);
			float x3 = A + (ver[Id + 6] * W) / (z3 * ratio);
			int y1 = B + (int)((ver[Id + 1] * H) / z1);
			int y2 = B + (int)((ver[Id + 4] * H) / z2);
			int y3 = B + (int)((ver[Id + 7] * H) / z3);
			x1 = (int)(x1);
			x2 = (int)(x2);
			x3 = (int)(x3);
			
			if(((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1)) * ((Triangle & 1) * 2 - 1) < 0) continue;
			int y_m = y1 > y2 ? (y2 > y3 ? 1:(y1 > y3 ? 2:0)):(y1 > y3 ? 0:(y2 > y3 ? 2:1));
			
			float[] ve_p = {
				x1, y1, z1,
				x2, y2, z2,
				x3, y3, z3
			};
			
			if(y_m == 0) {
				float x_s = (x2 - x3) / (y2 - y3);
				float x_sta = x_s * (y1 - y2) + x2;
				
				if(x_sta > x1) {
					if(y2 < y3) renderTriangle(y1, y2, y3, x1, x_sta, x3, x2, co_p, ve_p, uv_p);
					else		renderTriangle(y1, y3, y2, x1, x_sta, x2, x3, co_p, ve_p, uv_p);
				} else {
					if(y2 < y3) renderTriangle(y1, y2, y3, x_sta, x1, x3, x2, co_p, ve_p, uv_p);
					else		renderTriangle(y1, y3, y2, x_sta, x1, x2, x3, co_p, ve_p, uv_p);
				}
			} else if(y_m == 1) {
				float x_s = (x1 - x3) / (y1 - y3);
				float x_sta = x_s * (y2 - y1) + x1;
				
				if(x_sta > x2) {
					if(y1 < y3) renderTriangle(y2, y1, y3, x2, x_sta, x3, x1, co_p, ve_p, uv_p);
					else 		renderTriangle(y2, y3, y1, x2, x_sta, x1, x3, co_p, ve_p, uv_p);
				} else {
					if(y1 < y3) renderTriangle(y2, y1, y3, x_sta, x2, x3, x1, co_p, ve_p, uv_p);
					else 		renderTriangle(y2, y3, y1, x_sta, x2, x1, x3, co_p, ve_p, uv_p);
				}
			} else {
				float x_s = (x1 - x2) / (y1 - y2);
				float x_sta = x_s * (y3 - y1) + x1;
				
				if(x_sta > x3) {
					if(y1 < y2) renderTriangle(y3, y1, y2, x3, x_sta, x2, x1, co_p, ve_p, uv_p);
					else		renderTriangle(y3, y2, y1, x3, x_sta, x1, x2, co_p, ve_p, uv_p);
				} else {
					if(y1 < y2) renderTriangle(y3, y1, y2, x_sta, x3, x2, x1, co_p, ve_p, uv_p);
					else		renderTriangle(y3, y2, y1, x_sta, x3, x1, x2, co_p, ve_p, uv_p);
				}
			}
		}
	}
	
	public void TriangleFan(float x, float y, float z, float[] ve, float[] uv_p, float[] color, boolean fix, int offset) {
		int A = W / 2, B = H / 2;
		
		float[] ver = fix ?
			Matrix(x, y, z, 0, 0, 0, ve):
			Matrix(x + Tx, y + Ty, z + Tz, ve);
		
		for(int Triangle = 0; Triangle < (ver.length / 3 - 2); Triangle++) {
			int Id = Triangle * 3;
			if(ver[Id + 2] < near && ver[Id + 5] < near && ver[Id + 8] < near) continue;
			
			float z1 = ver[Id + 2] * fov,
				  z2 = ver[Id + 5] * fov,
				  z3 = ver[Id + 8] * fov;
			
			float[] uv = new float[6];
			if(uv_p != null) {
				int UID = (Triangle & 1) * 2;
				
				uv[0] = uv_p[UID + 0];
				uv[1] = uv_p[UID + 1];
				uv[2] = uv_p[UID + 2];
				uv[3] = uv_p[UID + 3];
				uv[4] = uv_p[UID + 4];
				uv[5] = uv_p[UID + 5];
			}
			tris ++;
			
			if(z1 < near * fov || z2 < near * fov || z3 < near * fov) {
				if(!fix)
				renderTriangle(new float[] {
					ver[Id + 0], ver[Id + 1], ver[Id + 2], 
					ver[Id + 3], ver[Id + 4], ver[Id + 5], 
					ver[Id + 6], ver[Id + 7], ver[Id + 8], 
				}, uv, color, Triangle);
				
				continue;
			}
			
			float x1 = A + (ver[Id    ] * W) / (z1 * ratio);
			float x2 = A + (ver[Id + 3] * W) / (z2 * ratio);
			float x3 = A + (ver[Id + 6] * W) / (z3 * ratio);
			int y1 = B + (int)(ver[Id + 1] * (H / z1));
			int y2 = B + (int)(ver[Id + 4] * (H / z2));
			int y3 = B + (int)(ver[Id + 7] * (H / z3));
			x1 = (int)(x1);
			x2 = (int)(x2);
			x3 = (int)(x3);
			
			if(((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1)) * (((Triangle + offset) & 1) * 2 - 1) < 0) continue;
			int y_m = y1 > y2 ? (y2 > y3 ? 1:(y1 > y3 ? 2:0)):(y1 > y3 ? 0:(y2 > y3 ? 2:1));
			
			float[] vertex = {
				x1, y1, z1,
				x2, y2, z2,
				x3, y3, z3
			};
			
			random.setSeed((long)(Triangle + offset) & 6);
			color = new float[] {
				random.nextFloat(), random.nextFloat(), random.nextFloat(),
				random.nextFloat(), random.nextFloat(), random.nextFloat(),
				random.nextFloat(), random.nextFloat(), random.nextFloat(),
			};
			
			
			if(y_m == 0) {
				float x_s = (x2 - x3) / (y2 - y3);
				float x_sta = x_s * (y1 - y2) + x2;
				
				if(x_sta > x1) {
					if(y2 < y3) renderTriangle(y1, y2, y3, x1, x_sta, x3, x2, color, vertex, uv);
					else		renderTriangle(y1, y3, y2, x1, x_sta, x2, x3, color, vertex, uv);
				} else {
					if(y2 < y3) renderTriangle(y1, y2, y3, x_sta, x1, x3, x2, color, vertex, uv);
					else		renderTriangle(y1, y3, y2, x_sta, x1, x2, x3, color, vertex, uv);
				}
			} else if(y_m == 1) {
				float x_s = (x1 - x3) / (y1 - y3);
				float x_sta = x_s * (y2 - y1) + x1;
				
				if(x_sta > x2) {
					if(y1 < y3) renderTriangle(y2, y1, y3, x2, x_sta, x3, x1, color, vertex, uv);
					else 		renderTriangle(y2, y3, y1, x2, x_sta, x1, x3, color, vertex, uv);
				} else {
					if(y1 < y3) renderTriangle(y2, y1, y3, x_sta, x2, x3, x1, color, vertex, uv);
					else 		renderTriangle(y2, y3, y1, x_sta, x2, x1, x3, color, vertex, uv);
				}
			} else {
				float x_s = (x1 - x2) / (y1 - y2);
				float x_sta = x_s * (y3 - y1) + x1;
				
				if(x_sta > x3) {
					if(y1 < y2) renderTriangle(y3, y1, y2, x3, x_sta, x2, x1, color, vertex, uv);
					else		renderTriangle(y3, y2, y1, x3, x_sta, x1, x2, color, vertex, uv);
				} else {
					if(y1 < y2) renderTriangle(y3, y1, y2, x_sta, x3, x2, x1, color, vertex, uv);
					else		renderTriangle(y3, y2, y1, x_sta, x3, x1, x2, color, vertex, uv);
				}
			}
		}
	}
	
	// Compute barycentric coordinates (u, v, w) for
	// point p with respect to triangle (a, b, c)
	private Vector Barycentric(
		float v0_x, float v0_y,
		float v1_x, float v1_y,
		float v2_x, float v2_y,
		float d00, float d01, float d11, float denom
	) {
		float d20 = v2_x * v0_x + v2_y * v0_y;
		float d21 = v2_x * v1_x + v2_y * v1_y;
		float v = (d11 * d20 - d01 * d21) / denom;
		float w = (d00 * d21 - d01 * d20) / denom;
		float u = 1.0f - v - w;
		
		return Vector.get(v, w, u); 
	}
	
	public void renderTriangle(float[] ver, float[] uv, float[] col, int tris) {
		float x1 = ver[0], y1 = ver[1], z1 = ver[2];
		float x2 = ver[3], y2 = ver[4], z2 = ver[5];
		float x3 = ver[6], y3 = ver[7], z3 = ver[8];
		
		if(z1 > near && z2 > near && z3 > near) return;
		if(z1 < near && z2 < near && z3 < near) return;
		
		float x12 = x1 - x2;
		float y12 = y1 - y2;
		float m12 = (near - z2) / (z1 - z2);
		x12 *= m12;
		y12 *= m12;
		
		float x13 = x1 - x3;
		float y13 = y1 - y3;
		float m13 = (near - z3) / (z1 - z3);
		x13 *= m13;
		y13 *= m13;
		
		float x23 = x2 - x3;
		float y23 = y2 - y3;
		float m23 = (near - z3) / (z2 - z3);
		x23 *= m23;
		y23 *= m23;
		
		Vector p12 = Vector.get(x2 + x12, y2 + y12, near);
		Vector p13 = Vector.get(x3 + x13, y3 + y13, near);
		Vector p23 = Vector.get(x3 + x23, y3 + y23, near);
		
		if(z1 < near) {
			if(z2 > near) {
				if(z3 > near) {		// . 2 3
					line(p12.x, p12.y, p12.z, p13.x, p13.y, p13.z, 0xffffff);
					{
						float[] _v = {
							p12.x, p12.y, near,
							x2, y2, z2,
							x3, y3, z3,
						};
						float[] _u = {
							(uv[0] - uv[2]) * m12 + uv[2],
							(uv[1] - uv[3]) * m12 + uv[3],
							uv[2], uv[3],
							uv[4], uv[5],
						};
						float[] _c = {
							(col[0] - col[3]) * m12 + col[3],
							(col[1] - col[4]) * m12 + col[4],
							(col[2] - col[5]) * m12 + col[5],
							col[3], col[4], col[5],
							col[6], col[7], col[8],
						};
						TriangleFan(_v, _u, _c, true, tris);
					}
					{
						float[] _v = {
							p12.x, p12.y, near,
							x3, y3, z3,
							p13.x, p13.y, near,
						};
						float[] _u = {
							(uv[0] - uv[2]) * m12 + uv[2],
							(uv[1] - uv[3]) * m12 + uv[3],
							uv[4], uv[5],
							(uv[0] - uv[4]) * m13 + uv[4],
							(uv[1] - uv[5]) * m13 + uv[5],
						};
						float[] _c = {
							(col[0] - col[3]) * m12 + col[3],
							(col[1] - col[4]) * m12 + col[4],
							(col[2] - col[5]) * m12 + col[5],
							col[6], col[7], col[8],
							(col[0] - col[6]) * m13 + col[6],
							(col[1] - col[7]) * m13 + col[7],
							(col[2] - col[8]) * m13 + col[8],
						};
						TriangleFan(_v, _u, _c, true, tris);
					}
				} else {			// . 2 .
					line(p23.x, p23.y, p23.z, p12.x, p12.y, p12.z, 0xffffff);
					float[] _v = {
						p12.x, p12.y, near,
						x2, y2, z2,
						p23.x, p23.y, near,
					};
					float[] _u = {
						(uv[0] - uv[2]) * m12 + uv[2],
						(uv[1] - uv[3]) * m12 + uv[3],
						uv[2], uv[3],
						(uv[2] - uv[4]) * m23 + uv[4],
						(uv[3] - uv[5]) * m23 + uv[5],
					};
					float[] _c = {
						(col[0] - col[3]) * m12 + col[3],
						(col[1] - col[4]) * m12 + col[4],
						(col[2] - col[5]) * m12 + col[5],
						col[3], col[4], col[5],
						(col[3] - col[6]) * m23 + col[6],
						(col[4] - col[7]) * m23 + col[7],
						(col[5] - col[8]) * m23 + col[8],
					};
					TriangleFan(_v, _u, _c, true, tris);
				}
			} else {
				if(z3 > near) {		// . . 3
					line(p13.x, p13.y, p13.z, p23.x, p23.y, p23.z, 0xffffff);
					float[] _v = {
						p13.x, p13.y, near,
						p23.x, p23.y, near,
						x3, y3, z3,
					};
					float[] _u = {
						(uv[0] - uv[4]) * m13 + uv[4],
						(uv[1] - uv[5]) * m13 + uv[5],
						(uv[2] - uv[4]) * m23 + uv[4],
						(uv[3] - uv[5]) * m23 + uv[5],
						uv[4], uv[5],
					};
					float[] _c = {
						(col[0] - col[6]) * m13 + col[6],
						(col[1] - col[7]) * m13 + col[7],
						(col[2] - col[8]) * m13 + col[8],
						(col[3] - col[6]) * m23 + col[6],
						(col[4] - col[7]) * m23 + col[7],
						(col[5] - col[8]) * m23 + col[8],
						col[6], col[7], col[8],
					};
					TriangleFan(_v, _u, _c, true, tris);
				} else {			// . . .
					
				}
			}
		} else {
			if(z2 < near) {
				if(z3 > near) {		// 1 . 3
					line(p12.x, p12.y, p12.z, p23.x, p23.y, p23.z, 0xffffff);
					{
						float[] _v = {
							x1, y1, z1,
							p12.x, p12.y, near,
							x3, y3, z3,
						};
						float[] _u = {
							uv[0], uv[1],
							(uv[0] - uv[2]) * m12 + uv[2],
							(uv[1] - uv[3]) * m12 + uv[3],
							uv[4], uv[5],
						};
						float[] _c = {
							col[0], col[1], col[2],
							(col[0] - col[3]) * m12 + col[3],
							(col[1] - col[4]) * m12 + col[4],
							(col[2] - col[5]) * m12 + col[5],
							col[6], col[7], col[8],
						};
						TriangleFan(_v, _u, _c, true, tris);
					}
					{
						float[] _v = {
							p12.x, p12.y, near,
							p23.x, p23.y, near,
							x3, y3, z3,
						};
						float[] _u = {
							(uv[0] - uv[2]) * m12 + uv[2],
							(uv[1] - uv[3]) * m12 + uv[3],
							(uv[2] - uv[4]) * m23 + uv[4],
							(uv[3] - uv[5]) * m23 + uv[5],
							uv[4], uv[5],
						};
						float[] _c = {
							(col[0] - col[3]) * m12 + col[3],
							(col[1] - col[4]) * m12 + col[4],
							(col[2] - col[5]) * m12 + col[5],
							(col[3] - col[6]) * m23 + col[6],
							(col[4] - col[7]) * m23 + col[7],
							(col[5] - col[8]) * m23 + col[8],
							col[6], col[7], col[8],
						};
						TriangleFan(_v, _u, _c, true, tris);
					}
				} else {			// 1 . .
					line(p12.x, p12.y, p12.z, p13.x, p13.y, p13.z, 0xffffff);
					float[] _v = {
						x1, y1, z1,
						p12.x, p12.y, near,
						p13.x, p13.y, near,
					};
					float[] _u = {
						uv[0], uv[1],
						(uv[0] - uv[2]) * m12 + uv[2],
						(uv[1] - uv[3]) * m12 + uv[3],
						(uv[0] - uv[4]) * m13 + uv[4],
						(uv[1] - uv[5]) * m13 + uv[5],
					};
					float[] _c = {
						col[0], col[1], col[2],
						(col[0] - col[3]) * m12 + col[3],
						(col[1] - col[4]) * m12 + col[4],
						(col[2] - col[5]) * m12 + col[5],
						(col[0] - col[6]) * m13 + col[6],
						(col[1] - col[7]) * m13 + col[7],
						(col[2] - col[8]) * m13 + col[8],
					};
					TriangleFan(_v, _u, _c, true, tris);
				}
			} else {
				if(z3 < near) {		// 1 2 .
					line(p23.x, p23.y, p23.z, p13.x, p13.y, p13.z, 0xffffff);
					{
						float[] _v = {
							x1, y1, z1,
							x2, y2, z2,
							p13.x, p13.y, near,
						};
						float[] _u = {
							uv[0], uv[1],
							uv[2], uv[3],
							(uv[0] - uv[4]) * m13 + uv[4],
							(uv[1] - uv[5]) * m13 + uv[5],
						};
						float[] _c = {
							col[0], col[1], col[2],
							col[3], col[4], col[5],
							(col[0] - col[6]) * m13 + col[6],
							(col[1] - col[7]) * m13 + col[7],
							(col[2] - col[8]) * m13 + col[8],
						};
						TriangleFan(_v, _u, _c, true, tris);
					}
					{
						float[] _v = {
							x2, y2, z2,
							p23.x, p23.y, near,
							p13.x, p13.y, near,
						};
						float[] _u = {
							uv[2], uv[3],
							(uv[2] - uv[4]) * m23 + uv[4],
							(uv[3] - uv[5]) * m23 + uv[5],
							(uv[0] - uv[4]) * m13 + uv[4],
							(uv[1] - uv[5]) * m13 + uv[5],
						};
						float[] _c = {
							col[3], col[4], col[5],
							(col[3] - col[6]) * m23 + col[6],
							(col[4] - col[7]) * m23 + col[7],
							(col[5] - col[8]) * m23 + col[8],
							(col[0] - col[6]) * m13 + col[6],
							(col[1] - col[7]) * m13 + col[7],
							(col[2] - col[8]) * m13 + col[8],
						};
						TriangleFan(_v, _u, _c, true, tris);
					}
				} else {			// 1 2 3
					
				}
			}
		}
	}
	
	public void renderTris(Vector p0, Vector p1, Vector p2, Vector p3) {
		
	}
	
	// TODO: Make a left side triangle and a right side triangle
	/*        |          :           |
	 *        1          :           1
	 *      .'|          :           |'.
	 *     2  |          :           |  2
	 *      '.|          :           |.'
	 *        '.         :          .'
	 *        | '3       :         3 |
	 */
	int tris = 0;
	public void renderTriangle(int y_mid, int y_max, int y_min, float x_sta, float x_end, float x_bot, float x_top, float[] color, float[] vertex, float[] uv) {
		float y_d0 = y_min - y_mid;
		float y_d1 = y_mid - y_max;
		
		x_sta = (int)x_sta;
		x_end = (int)x_end;
		x_bot = (int)x_bot;
		x_top = (int)x_top;
		
		boolean zbuffer = false;
		
		Art tex = this.texture;
		boolean texture = tex != null;
		
		float[] c0 = { 1, 1, 0 };
		float[] c1 = { 1, 0, 1 };
		float[] c2 = { 0, 1, 1 };
		c2 = new float[] { color[0], color[1], color[2] };
		c0 = new float[] { color[3], color[4], color[5] };
		c1 = new float[] { color[6], color[7], color[8] };
		
		float v0_x = vertex[3] - vertex[0];
		float v0_y = vertex[4] - vertex[1];
		float v1_x = vertex[6] - vertex[0];
		float v1_y = vertex[7] - vertex[1];
		
		float d00 = v0_x * v0_x + v0_y * v0_y;
		float d01 = v0_x * v1_x + v0_y * v1_y;
		float d11 = v1_x * v1_x + v1_y * v1_y;
		float denom = d00 * d11 - d01 * d01;
		
		c0[0] /= vertex[5]; c0[1] /= vertex[5]; c0[2] /= vertex[5];
		c1[0] /= vertex[8]; c1[1] /= vertex[8]; c1[2] /= vertex[8];
		c2[0] /= vertex[2]; c2[1] /= vertex[2]; c2[2] /= vertex[2];
		
		uv[0] /= vertex[2]; uv[1] /= vertex[2];
		uv[2] /= vertex[5]; uv[3] /= vertex[5];
		uv[4] /= vertex[8]; uv[5] /= vertex[8];
		
		vertex[2] = 1 / vertex[2];
		vertex[5] = 1 / vertex[5];
		vertex[8] = 1 / vertex[8];
		
		// float y_d0 = y_min - y_mid;
		if(y_d0 > 0) { // TODO BOTTOM TRIANGLE
			float x13_s0 = (x_bot - x_sta) / y_d0;
			float x23_s0 = (x_bot - x_end) / y_d0;
			
			int ys = 0;
			if(y_min > H) y_d0 = H - y_mid;
			if(y_mid < 0) ys = -y_mid; 
			
			for(int yy = ys; yy <= y_d0; yy++) {
				int yb = (yy + y_mid) * W;
				if(yb >= W * H) break;
				
				int xs = (int)(yy * x13_s0 + x_sta) - 1;
				int xe = (int)(yy * x23_s0 + x_end) + 1;
				
				if(xs <  0) xs = 0;
				if(xe >= W) xe = W - 1;
				int v2_y = yy + y_mid - (int)vertex[1];
				
				for(int xx = xs + yb; xx <= xe + yb; xx++) {
					if(xx < 0) {
						xx = 0;
						continue;
					}
					int v2_x = xx - yb - (int)vertex[0];
					
					Vector centric = Barycentric(
						v0_x, v0_y,
						v1_x, v1_y,
						v2_x, v2_y,
						d00, d01, d11, denom
					);
					
					float depth = 1 / (centric.x * vertex[5] + centric.y * vertex[8] + centric.z * vertex[2]);
					
					if(Float.isNaN(depth)) continue;
					if(zbuff[xx] <= depth * Z_DEPTH) continue;
					if(centric.x < 0 || centric.y < 0 || centric.z < 0 ||
					   centric.x > 1 || centric.y > 1 || centric.z > 1) continue;
					zbuff[xx] = (int)(depth * Z_DEPTH);
					
					{
						int rg2 = 0xffffff;
						if(texture) {
							float xc = depth * (centric.x * uv[2] + centric.y * uv[4] + centric.z * uv[0]);
							float yc = depth * (centric.x * uv[3] + centric.y * uv[5] + centric.z * uv[1]);
							
							int texd = (int)(xc * tex.width);
							if(texd < 0) texd = 0;
							if(texd >= tex.width) texd = tex.width - 1;
							
							int teyd = (int)(yc * tex.height);
							if(teyd < 0) teyd = 0;
							if(teyd >= tex.height) teyd = tex.height - 1;
							
							int teid = texd + teyd * tex.width;
							if(teid >= tex.sprite.length) teid = tex.sprite.length - 1;
							rg2 = tex.sprite[teid];
						}
						
						float cr = (rg2 >> 16) & 0xff;
						float cg = (rg2 >>  8) & 0xff;
						float cb = (rg2      ) & 0xff;
						
						cr *= depth * (centric.x * c0[0] + centric.y * c1[0] + centric.z * c2[0]);
						cg *= depth * (centric.x * c0[1] + centric.y * c1[1] + centric.z * c2[1]);
						cb *= depth * (centric.x * c0[2] + centric.y * c1[2] + centric.z * c2[2]);
						
						cr = cr < 0 ? 0:(cr > 255 ? 255:cr);
						cg = cg < 0 ? 0:(cg > 255 ? 255:cg);
						cb = cb < 0 ? 0:(cb > 255 ? 255:cb);
						int rgb = ((int)cr << 16) | ((int)cg << 8) | (int)cb;
						pixels[xx] = rgb;
					}
					
					if(zbuffer) {
						pixels[xx] = (zbuff[xx] & 0xff) * 0x010101;
					}
				}
			}
		}
		
		// float y_d1 = y_mid - y_max;
		if(y_d1 > 0) { // TODO TOP TRIANGLE
			float x13_s1 = (x_sta - x_top) / y_d1;
			float x23_s1 = (x_end - x_top) / y_d1;
			
			int ys = 0;
			if(y_mid > H) y_d1 = H - y_max;
			if(y_max < 0) ys = -y_max;
			
			for(int yy = ys; yy <= y_d1; yy++) {
				int yb = (int)(yy + y_max) * W;
				if(yb >= W * H) break;
				
				int xs = (int)(yy * x13_s1 + x_top) - 1;
				int xe = (int)(yy * x23_s1 + x_top) + 1;
				
				if(xs <  0) xs = 0;
				if(xe >= W) xe = W - 1;
				
				int v2_y = yy + y_max - (int)vertex[1];
				
				for(int xx = xs + yb; xx <= xe + yb; xx++) {
					if(xx < 0) break;
					int v2_x = xx - yb - (int)vertex[0];
					
					Vector centric = Barycentric(
						v0_x, v0_y,
						v1_x, v1_y,
						v2_x, v2_y,
						d00, d01, d11, denom
					);
					
					float depth = 1 / (centric.x * vertex[5] + centric.y * vertex[8] + centric.z * vertex[2]);
					
					// depth is NaN if
					// if (centric.x | centric.y | centric.z) == NaN
					// or (centric.x * verts2[5] + centric.y * verts2[8] + centric.z * verts2[2]) == 0
					// or (verts[2] | verts[5] | verts[8]) == NaN
					
					if(Float.isNaN(depth)) continue;
					if(zbuff[xx] <= depth * Z_DEPTH) continue;
					if(centric.x < 0 || centric.y < 0 || centric.z < 0 ||
					   centric.x > 1 || centric.y > 1 || centric.z > 1) continue;
					zbuff[xx] = (int)(depth * Z_DEPTH);
					
					{
						int rg2 = 0xffffff;
						if(texture) {
							float xc = depth * (centric.x * uv[2] + centric.y * uv[4] + centric.z * uv[0]);
							float yc = depth * (centric.x * uv[3] + centric.y * uv[5] + centric.z * uv[1]);
							
							int texd = (int)(xc * tex.width);
							if(texd < 0) texd = 0;
							if(texd >= tex.width) texd = tex.width - 1;
							
							int teyd = (int)(yc * tex.height);
							if(teyd < 0) teyd = 0;
							if(teyd >= tex.height) teyd = tex.height - 1;
							
							int teid = texd + teyd * tex.width;
							if(teid >= tex.sprite.length) teid = tex.sprite.length - 1;
							rg2 = tex.sprite[teid];
						}
						
						float cr = (rg2 >> 16) & 0xff;
						float cg = (rg2 >>  8) & 0xff;
						float cb = (rg2      ) & 0xff;
						
						cr *= depth * (centric.x * c0[0] + centric.y * c1[0] + centric.z * c2[0]);
						cg *= depth * (centric.x * c0[1] + centric.y * c1[1] + centric.z * c2[1]);
						cb *= depth * (centric.x * c0[2] + centric.y * c1[2] + centric.z * c2[2]);
						
						cr = cr < 0 ? 0:(cr > 255 ? 255:cr);
						cg = cg < 0 ? 0:(cg > 255 ? 255:cg);
						cb = cb < 0 ? 0:(cb > 255 ? 255:cb);
						int rgb = ((int)cr << 16) | ((int)cg << 8) | (int)cb;
						pixels[xx] = rgb;
					}
					
					if(zbuffer) {
						pixels[xx] = (zbuff[xx] & 0xff) * 0x010101;
					}
				}
			}
		}
	}
	
	
	public void test14(float x, float y, float z, float[] ve, int col) {
		int A = W / 2, B = H / 2;
		
		int HOLD = -1;
		if(InputListener.Wait) {
			int xx = (InputListener.AMouseX) + (InputListener.AMouseY) * W;
			if(!(xx < 0 || xx >= W * H)) {
				int Triangle = zbuff[xx];
				if(Triangle > 0) {
					//TriangleFan(x, y, z, ve, 0xffffff, Triangle - 1);
					HOLD = Triangle - 1;
				}
			}
		}
		//clearZbuff();
		
		Graphics g = Main.INSTANCE.image.getGraphics();
		g.setFont(new Font("Consolas", Font.PLAIN, 11));
		FontMetrics met = Main.INSTANCE.getFontMetrics(g.getFont());
		Color cc = new Color(0x000000);
		Color ee = new Color(0xeeeeee);
		Color ff = new Color(0xff0000);
		
		boolean SID = false;
		boolean DEF = false;
		boolean VEP = false;
		boolean VER = true;
		x += Tx; y += Ty; z += Tz;
		float[] ver = Matrix(x, y, z, ve);
		for(int Triangle = 0; Triangle < (ver.length / 3 - 2); Triangle++) {
			int Id = Triangle * 3;
			
			float z1 = ver[Id + 2] * fov,
				  z2 = ver[Id + 5] * fov,
				  z3 = ver[Id + 8] * fov;
			
			if(z1 <= 0 || z2 <= 0 || z3 <= 0) continue;
			if(HOLD == Triangle || true) {
				int x1 = (int)(A + (ver[Id    ]) * (W / z1)), y1 = (int)(B + (ver[Id + 1]) * (H / z1));
				int x2 = (int)(A + (ver[Id + 3]) * (W / z2)), y2 = (int)(B + (ver[Id + 4]) * (H / z2));
				int x3 = (int)(A + (ver[Id + 6]) * (W / z3)), y3 = (int)(B + (ver[Id + 7]) * (H / z3));
				float X1 = ve[Id    ], Y1 = ve[Id + 1], Z1 = ve[Id + 2];
				float X2 = ve[Id + 3], Y2 = ve[Id + 4], Z2 = ve[Id + 5];
				float X3 = ve[Id + 6], Y3 = ve[Id + 7], Z3 = ve[Id + 8];
				String P1 = "", P2 = "", P3 = "";
				
				if(SID) {
					P1 += String.format("[%2d 0]", Triangle);
					P2 += String.format("[%2d 1]", Triangle);
					P3 += String.format("[%2d 2]", Triangle);
				}
				if(DEF) {
					P1 += String.format("[%d, %d]", x1, y1);
					P2 += String.format("[%d, %d]", x2, y2);
					P3 += String.format("[%d, %d]", x3, y3);
				}
				if(VEP) {
					P1 += String.format("[%.1f, %.1f, %.1f](%.3f)", X1, Y1, Z1, z1);
					P2 += String.format("[%.1f, %.1f, %.1f](%.3f)", X2, Y2, Z2, z2);
					P3 += String.format("[%.1f, %.1f, %.1f](%.3f)", X3, Y3, Z3, z3);
				}
				if(VER) {
					P1 += String.format("[%.1f, %.1f, %.1f]", ver[Id    ], ver[Id + 1], ver[Id + 2]);
					P2 += String.format("[%.1f, %.1f, %.1f]", ver[Id + 3], ver[Id + 4], ver[Id + 5]);
					P3 += String.format("[%.1f, %.1f, %.1f]", ver[Id + 6], ver[Id + 7], ver[Id + 8]);
				}
				
				y1 += Triangle * 10;
				y2 += Triangle * 10;
				y3 += Triangle * 10;
				g.setColor(cc); g.fillRect(x1 + 1, y1 - 9, met.stringWidth(P1) - 2, 12);
				g.setColor(ee); g.drawString(P1.replaceAll( "[\\[\\]/,]", " "), x1, y1);
				g.setColor(ff); g.drawString(P1.replaceAll("[^\\[\\]/,]", " "), x1, y1);
				
				g.setColor(cc); g.fillRect(x2 + 1, y2 - 9, met.stringWidth(P2) - 2, 12);
				g.setColor(ee); g.drawString(P2.replaceAll( "[\\[\\]/,]", " "), x2, y2);
				g.setColor(ff); g.drawString(P2.replaceAll("[^\\[\\]/,]", " "), x2, y2);
				
				g.setColor(cc); g.fillRect(x3 + 1, y3 - 9, met.stringWidth(P3) - 2, 12);
				g.setColor(ee); g.drawString(P3.replaceAll( "[\\[\\]/,]", " "), x3, y3);
				g.setColor(ff); g.drawString(P3.replaceAll("[^\\[\\]/,]", " "), x3, y3);
			}
		}
	}
	
	public float[] Matrix(float px, float py, float pz, float rx, float ry, float rz, float[] ver) {
		float[] ret = new float[ver.length];
		
		float A = toRadians(rx);
		float B = toRadians(ry);
		float C = toRadians(rz);
		
		float cosf_A = MathUtils.cosf(A);
		float sinf_A = MathUtils.sinf(A);
		float cosf_B = MathUtils.cosf(B);
		float sinf_B = MathUtils.sinf(B);
		float cosf_C = MathUtils.cosf(C);
		float sinf_C = MathUtils.sinf(C);
		
		for(int i = 0; i < ver.length / 3; i++) {
			float x = ver[i * 3 + 0] + px;
			float y = ver[i * 3 + 1] + py;
			float z = ver[i * 3 + 2] + pz;
			float nx_A = x * cosf_A - z * sinf_A;
			//oat ny_A = y;
			float nz_A = x * sinf_A + z * cosf_A;
			
			//oat nx_B = nx_A;
			float ny_B = y * cosf_B - nz_A * sinf_B;
			float nz_B = y * sinf_B + nz_A * cosf_B;
			
			float nx_C = nx_A * cosf_C - ny_B * sinf_C;
			float ny_C = nx_A * sinf_C + ny_B * cosf_C;
			//oat nz_C = nz_B;
			
			ret[i * 3 + 0] = nx_C;
			ret[i * 3 + 1] = ny_C;
			ret[i * 3 + 2] = nz_B;
		}
		return ret;
	}
	
	// TODO: Matrix
	private static final float tes = 180.f / 3.141592653589793f;
	public float toRadians(float angdeg) { return angdeg / tes;}
	public float[] Matrix(float px, float py, float pz, float[] ver) {
		float[] ret = new float[ver.length];
		
		float A = toRadians(Rx);
		float B = toRadians(Ry);
		float C = toRadians(Rz);
		boolean use_real = true;
		float cosf_A = MathUtils.cosf(A);
		float sinf_A = MathUtils.sinf(A);
		float cosf_B = MathUtils.cosf(B);
		float sinf_B = MathUtils.sinf(B);
		float cosf_C = MathUtils.cosf(C);
		float sinf_C = MathUtils.sinf(C);
		
		if(use_real) { // This is really slow
			cosf_A = (float)Math.cos(A);
			sinf_A = (float)Math.sin(A);
			cosf_B = (float)Math.cos(B);
			sinf_B = (float)Math.sin(B);
			cosf_C = (float)Math.cos(C);
			sinf_C = (float)Math.sin(C);
		}
		
		for(int i = 0; i < ver.length / 3; i++) {
			float x = ver[i * 3 + 0] + px;
			float y = ver[i * 3 + 1] + py;
			float z = ver[i * 3 + 2] + pz;
			
			float nx_A = x * cosf_A - z * sinf_A;
			//oat ny_A = y;
			float nz_A = x * sinf_A + z * cosf_A;
			
			//oat nx_B = nx_A;
			float ny_B = y * cosf_B - nz_A * sinf_B;
			float nz_B = y * sinf_B + nz_A * cosf_B;
			
			float nx_C = nx_A * cosf_C - ny_B * sinf_C;
			float ny_C = nx_A * sinf_C + ny_B * cosf_C;
			//oat nz_C = nz_B;
			
			ret[i * 3 + 0] = nx_C;
			ret[i * 3 + 1] = ny_C;
			ret[i * 3 + 2] = nz_B;
		}
		return ret;
	}
	
	public void rect(float x, float y, float w, float h, int col) {
		for(float xp = x; xp < x + w; xp++)
		for(float yp = y; yp < y + h; yp++) {
			if(xp < 0 || xp >= W || yp < 0 || yp >= H) continue;
			int pos = (int)xp + (int)yp * W;
			if(pos < 0 || pos >= W * H) continue;
			pixels[pos] = col;
		}
	}
	
	
	public void line3D(float x1, float y1, float z1, float x2, float y2, float z2, int col) {
		float[] ver = Matrix(Tx, Ty, Tz, Rx, Ry, Rz, new float[] { x1, y1, z1, x2, y2, z2 });
		line(ver[0], ver[1], ver[2], ver[3], ver[4], ver[5], col);
	}
	
	public void line(float x1, float y1, float z1, float x2, float y2, float z2, int col) {
		float nx1 = (x1 * W) / (z1 * ratio * fov) + W / 2;
		float ny1 = (y1 * H) / (z1 * fov) + H / 2;
		float nx2 = (x2 * W) / (z2 * ratio * fov) + W / 2;
		float ny2 = (y2 * H) / (z2 * fov) + H / 2;
		
		line(nx1, ny1, nx2, ny2, col);
	}
	
	public void line(float x1, float y1, float x2, float y2, int col) {
		//if((x1 < 0 && x2 < 0) || (x1 > W && x2 > W)) return;
		//if((y1 < 0 && y2 < 0) || (y1 > H && y2 > H)) return;
		
		// https://www.desmos.com/calculator/uwpdhcjyxr
		float s = (y1 - y2) / (x1 - x2);
		if(x1 < 0) {
			y1 = s * (-x1) + y1;
			x1 = 0;
			s = (y1 - y2) / (x1 - x2);
		}
		if(x2 > W) {
			//System.out.println("0: " + x1 + ", " + y1);
			y2 = s * (-x1 + H) + y1;
			x2 = W;
			//System.out.println("1: " + x1 + ", " + y1);
		}

		float xd = x2 - x1, yd = y2 - y1;
		float dist = Math.max(Math.abs(xd), Math.abs(yd));

		if(dist > 16384) {
			//System.out.printf("%g/%g | %g/%g | %g/%g | %g/%g | %g\n",x1, ox1, x2, ox2, y1, oy1, y2, oy2, dist);
			dist = 16384;
		}
		float xs = xd / dist, ys = yd / dist;
		for(float pp = 0; pp < dist; pp++) {
			float xp = x1 + pp * xs - 0.5f;
			float yp = y1 + pp * ys;
			if(xp < 0 || xp >= W || yp < 0 || yp >= H) continue;
			int pos = (int)xp + (int)yp * W;
			if(pos < 0 || pos >= W * H) continue;
			pixels[pos] = col;
			zbuff[pos] = 0xff;
		}
	}
}
