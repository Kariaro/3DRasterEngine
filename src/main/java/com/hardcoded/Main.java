package com.hardcoded;

import static com.hardcoded.InputListener.*;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.hardcoded.render.Render;

public class Main extends Canvas {
	private static final long serialVersionUID = 1L;
	public static Main INSTANCE;
	
	public static void main(String[] args) {
		JFrame frame = new JFrame(TITLE);
		Main main = new Main();
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(main, BorderLayout.CENTER);
		
		frame.setContentPane(panel);
		frame.setResizable(false);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		main.start();
		
		INSTANCE = main;
	}
	
	public static final String TITLE = "3D Game";
	public static final int HEIGHT = 1000;
	public static final int WIDTH = 1000;
	public static final int FPS = 240;
	
	public Thread thread = new Thread() {
		public void run() {
			int frames = 0;
			
			double unprocessedSeconds = 0;
			long lastTime = System.nanoTime();
			//double secondsPerTick = 1 / 60.0;
			double secondsPerTick = 1 / (FPS + 0.0);
			int tickCount = 0;
			
			requestFocus();
			
			while(true) {
				long now = System.nanoTime();
				long passedTime = now - lastTime;
				lastTime = now;
				if(passedTime < 0) passedTime = 0;
				if(passedTime > 100000000) passedTime = 100000000;
				
				unprocessedSeconds += passedTime / 1000000000.0;
				
				boolean ticked = false;
				while(unprocessedSeconds > secondsPerTick) {
					tick();
					unprocessedSeconds -= secondsPerTick;
					ticked = true;
					
					tickCount++;
					if(tickCount % FPS == 0) {
						System.out.println(frames + " fps");
						lastTime += 1000;
						frames = 0;
					}
				}
				
				if(ticked) {
					render();
					frames++;
				} else {
					try {
						Thread.sleep(1);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
	};
	
	public BufferedImage image;
	public int[] pixels;
	
	public InputListener input;
	public Render render;
	
	public Main() {
		Dimension size = new Dimension(WIDTH, HEIGHT);
		setSize(size);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		
		image = new BufferedImage(WIDTH, HEIGHT, 1);
		
		input = new InputListener();
		addMouseMotionListener(input);
		addMouseWheelListener(input);
		addFocusListener(input);
		addMouseListener(input);
		addKeyListener(input);
		
		pixels = ((DataBufferInt)this.image.getRaster().getDataBuffer()).getData();
		render = new Render(WIDTH, HEIGHT);
	}
	
	public void start() {
		this.thread.start();
	}
	
	public float xa, ya, za;
	public float rx, ry, rz;
	public float tx, ty, tz;
	
	public BufferStrategy bs;
	public void render() {
		if(getBufferStrategy() == null) {
			createBufferStrategy(2);
			bs = getBufferStrategy();
			return;
		}
		
		for(int i = 0; i < pixels.length; i++) {
			render.pixels[i] = 0;
		}
		
		// render.render();
		render.render();
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] = render.pixels[i];
		}
		
		bs.getDrawGraphics().drawImage(this.image, 0, 0, WIDTH, HEIGHT, null);
		bs.show();
	}
	
	private boolean lockCamera = true;
	private boolean lock = false;
	public void tick() {
		if(Keys[KeyEvent.VK_C]) {
			if(!lock) {
				lock = true;
				lockCamera = !lockCamera;
			}
		} else lock = false;
		
		if(MouseY < -180) MouseY = -180;
		if(MouseY >  180) MouseY =  180;
		
		float Ry = MouseY / 2.0f;
		float Rx = MouseX / 2.0f;
		float Rz = 0;
		
		if(lockCamera) {
			float rs = 0.5f;
			if(Keys[KeyEvent.VK_Q]) rx -= rs;
			if(Keys[KeyEvent.VK_E]) rx += rs;
			if(Keys[KeyEvent.VK_R]) ry -= rs;
			if(Keys[KeyEvent.VK_F]) ry += rs;
			
			if(ry < -90) ry = -90;
			if(ry >  90) ry =  90;
			
			Rx = rx;
			Ry = ry;
			Rz = rz;
		}
		
		float speed = 0.01f;
		float ra = (float)Math.toRadians(Rx);
		float ff = 0, ss = 0, dd = 0;
		
		if(Keys[KeyEvent.VK_W]) ff += 1;
		if(Keys[KeyEvent.VK_S]) ff -= 1;
		if(Keys[KeyEvent.VK_A]) ss -= 1;
		if(Keys[KeyEvent.VK_D]) ss += 1;
		if(Keys[KeyEvent.VK_SPACE]) dd -= 1;
		if(Keys[KeyEvent.VK_SHIFT]) dd += 1;
		
		this.za -= ff * Math.cos(ra) - ss * Math.sin(ra);
		this.xa -= ff * Math.sin(ra) + ss * Math.cos(ra);
		this.ya -= dd / 3.0f;
		
		tx += this.xa * speed;
		ty += this.ya * speed;
		tz += this.za * speed;
		
		this.za *= 0.5;
		this.xa *= 0.5;
		this.ya *= 0.5;
		
		render.setCameraTransform(tx, ty, tz);
		render.setCameraRotation(Rx, Ry, Rz);
	}
}