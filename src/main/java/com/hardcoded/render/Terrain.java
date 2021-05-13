package com.hardcoded.render;

import com.hardcoded.terrain.Noise;

public class Terrain {
	private static Noise NOISE = new Noise(323);
	
	public static float[][] generateTerrain(int width, int height, float scale, int octaves) {
		float[][] terrain = new float[height][(width * 2 + 2) * 3];
		
		float[] noise = generateNoise(width + 1, height + 1, scale, 1, octaves);
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width + 1; x++) {
				float xn0 = noise[y * (width + 1) + x] * -30;
				int pos = x * 6;
				terrain[y][pos + 3] =   x;
				terrain[y][pos + 4] = xn0;
				terrain[y][pos + 5] =  -1;
				

				float xn1 = noise[(y+1) * (width + 1) + x] * -30;
				terrain[y][pos + 0] =   x;
				terrain[y][pos + 1] = xn1;
				terrain[y][pos + 2] =   0;
				
				float col = noise[y * (width + 1) + x] * 1000;
				if(col <    0) col =    0;
				if(col > 1000) col = 1000;
			}
		}
		return terrain;
	}
	
	public static float[] generateNoise(int width, int height, float scale, float id, int octaves) {
		float[] noise = new float[width * height];
		float persistance = 2.5f;
		float lacunarity  = 0.5f;
		
		float maxNoiseHeight = Float.MIN_VALUE;
		float minNoiseHeight = Float.MAX_VALUE;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				float amplitude = 1;
				float frequency = 1;
				float noiseHeight = 0;
				
				for(int i = 0; i < octaves; i++) {
					float sampleX = x / scale * frequency;
					float sampleY = y / scale * frequency;
					
					float perlinValue = (float)NOISE.perlin(sampleX, sampleY, id) * 2 - 1;
					noiseHeight += perlinValue * amplitude;
					
					amplitude *= persistance;
					frequency *= lacunarity;
				}
				
				if(noiseHeight > maxNoiseHeight) {
					maxNoiseHeight = noiseHeight;
				} else if(noiseHeight < minNoiseHeight) {
					minNoiseHeight = noiseHeight;
				}
				noise[x + y * width] = noiseHeight;
			}
		}
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				float val = noise[x + y * width] - minNoiseHeight;
				
				noise[x + y * width] = val / (maxNoiseHeight - minNoiseHeight);
			}
		}
		
		return noise;
	}
}
