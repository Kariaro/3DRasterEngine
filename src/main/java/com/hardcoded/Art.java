package com.hardcoded;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Art {
	private static int art_sprites = 0;
	private static List<Art> sprites = new ArrayList<Art>();
	
	public final int[] sprite;
	public final int height;
	public final int width;
	public final int id;
	private Art(int[] pixels, int width, int height) {
		this.sprite = pixels;
		this.height = height;
		this.width = width;
		this.id = art_sprites++;
		sprites.add(this);
	}
	
	private Art(String path) {
		int width = 1, height = 1;
		int[] pixels = new int[1];
		try {
			BufferedImage bi = ImageIO.read(Art.class.getResourceAsStream(path));
			height = bi.getHeight();
			width = bi.getWidth();
			pixels = bi.getRGB(0, 0, width, height, null, 0, width);
			
			for(int i = 0; i < width * height; i++) {
				int color = pixels[i];
				
				pixels[i] = color & 0xffffff;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		this.sprite = pixels;
		this.height = height;
		this.width = width;
		this.id = art_sprites++;
		sprites.add(this);
	}
	
	private Art(Art texture, int x, int y, int width, int height) {
		this.sprite = new int[width * height];
		this.height = height;
		this.width = width;
		
		for(int yy = 0; yy < height; yy++) {
			int yp = yy + y;
			if(yp < 0) continue;
			if(yp >= texture.height) break;
			
			for(int xx = 0; xx < width; xx++) {
				int xp = xx + x;
				if(xp < 0) continue;
				if(xp >= texture.width) break;
				
				sprite[xx + yy * width] = texture.sprite[xp + yp * texture.width];
			}
		}
		
		this.id = art_sprites++;
		sprites.add(this);
	}
	
	public static final Art tilemap = new Art("/images2.png");
	public static final Art grass = new Art("/grass.png");
	public static final Art grass_small = new Art("/grass 32x32.png");
	public static final Art crate = new Art("/crate.png");
	public static final Art cross = new Art("/cross.png");
	public static final Art dirt = new Art(tilemap, 0, 0, 16, 16);
	public static final Art sand = new Art(tilemap, 16, 0, 16, 16);
	public static final Art gravel = new Art(tilemap, 32, 0, 16, 16);
	public static final Art brick = new Art(tilemap, 48, 0, 16, 16);
	public static final Art brick2 = new Art(tilemap, 64, 0, 16, 16);
	public static final Art debug = new Art(tilemap, 80, 0, 16, 16);
	public static final Art cmyk = new Art("/cmyk.png");
	
	//public static final Art stone_texture = new Art("/Test/StoneWall.png");
	//public static final Art stone_normal  = new Art("/Test/StoneNormal.png");
	//public static final Art stone_normal_kubic = new Art("/Test/StoneNormal_kubic.png");
	//public static final Art stone_texture_kubic = new Art("/Test/StoneWall_kubic.png");
	
	//public static final Art no_normal  = new Art("/no_normal.png");
	//public static final Art forward_normal  = new Art("/ForwardMap.png");
	//public static final Art circle_normal  = new Art("/Test/6487-normal.png");
	
	public static final Art getArtByID(int id) {
		if(id < 0) return null;
		return sprites.get(id);
	}
}
