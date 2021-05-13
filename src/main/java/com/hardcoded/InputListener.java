package com.hardcoded;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class InputListener implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener, FocusListener {
	public static boolean[] Keys = new boolean[0xff];
	public static boolean Focus  = false;
	public static boolean Wait   = false;
	public static int OMouseX = 0;
	public static int OMouseY = 0;
	public static int MouseX = 0;
	public static int MouseY = 0;
	public static int AMouseX = 0;
	public static int AMouseY = 0;
	
	public void mousePressed(MouseEvent e) {
		OMouseX = -999;
	}
	
	public void mouseReleased(MouseEvent e) {
	}
	
	public void mouseWheelMoved(MouseWheelEvent e) {
	}
	
	public void mouseMoved(MouseEvent e) {
		if(!Focus) return;
		AMouseX = e.getX(); AMouseY = e.getY();
		if(Wait) return;
		if(OMouseX == -999) {
			OMouseX = AMouseX;
			OMouseY = AMouseY;
		}
		if(AMouseX != OMouseX) {
			MouseX += (AMouseX - OMouseX);
			OMouseX = AMouseX;
		}
		if(AMouseY != OMouseY) {
			MouseY += (AMouseY - OMouseY);
			OMouseY = AMouseY;
		}
	}
	public void mouseDragged(MouseEvent e) {
		if(!Focus) return;
		AMouseX = e.getX(); AMouseY = e.getY();
		if(OMouseX == -999) {
			OMouseX = AMouseX;
			OMouseY = AMouseY;
		}
		if(AMouseX != OMouseX) {
			MouseX += (AMouseX - OMouseX);
			OMouseX = AMouseX;
		}
		if(AMouseY != OMouseY) {
			MouseY += (AMouseY - OMouseY);
			OMouseY = AMouseY;
		}
	}
	
	public boolean stop = false;
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if(code >= 0xff) return;
		Keys[code] = true;
		if(code == 57 && !stop) {
			Wait = !Wait;
			System.out.println("Test: " + Wait);
			if(!Wait) OMouseX = -999;
			stop = true;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		if(code >= 0xff) return;
		Keys[code] = false;
		if(code == 57) {
			stop = false;
		}
	}
	
	public void focusGained(FocusEvent e) {
		Focus = true;
		OMouseX = -999;
		OMouseY = 0;
	}
	public void focusLost(FocusEvent e) {
		Focus = false;
		for(int i = 0; i < Keys.length; i++) {
			Keys[i] = false;
		}
	}
	
	public void keyTyped(KeyEvent e) {}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}
