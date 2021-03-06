package com.example.graphicstest;

import java.util.Random;

abstract public class Particle {
	int raidus;
	double x, y, dx, dy, dt, angle;
	int screenLength, screenHeight;
	private static final double EPSILON = 0.00005;
	double c=4; 
	
	public boolean onscreen() {
		return !(x > screenLength+20 || x < -20 || y > screenHeight+20 || y < -20);
	}
	public void update(double dt){
		double norm = Math.sqrt(dx*dx + dy*dy);
		dx *= c/norm;
		dy *= c/norm;
		if (Math.abs(dx)<EPSILON) dx = 0;
		if (Math.abs(dy)<EPSILON) dy = 0;
		x += dx*dt;
		y += dy*dt;
		
	}
	public int getx() {
		return (int)Math.round(x);
	}
	public int gety() {
		return (int)Math.round(y);
	}
	public float getAngle() {
		return (float)angle;
	}
	public void updateAngle(){
		angle = Math.atan2(dy, dx)*180/Math.PI; 
	}
}

class Bullet extends Particle {
	int radius;
	int x0, y0, id;
	int targetX, targetY;
	
	double angle;
	private static final double EPSILON = 0.00005;
	public Bullet(int id, int targetX, int targetY, int x0, int y0, int h, int l){
		c = 50;
		radius = 15;
		this.id = id;
		this.targetX = targetX;
		this.targetY = targetY;
		this.x0 = x0;
		this.y0 = y0;
		x = x0;
		y = y0;
		this.screenLength = l;
		this.screenHeight = h;
		dx = ((double)targetX-x0);
		dy = ((double)targetY-y0);
		double norm = Math.sqrt(dx*dx + dy*dy);
		dx *= c/norm;
		dy *= c/norm;
		if (Math.abs(dx)<EPSILON) dx = 0;
		if (Math.abs(dy)<EPSILON) dy = 0;
		angle = Math.atan2(dy, dx)*180/Math.PI;
	}
	@Override
	public void update(double dt){
		
		x += dx*dt;
		y += dy*dt;
		
	}

}
class BoxParticle extends Particle{
	int radius = 15;
	Random r;

	private int dir; //0 for left right, 1 for down up, 2 for right left, 3 for up down
	
	public BoxParticle(Random r, int h, int l) {
		c = 40;
		radius = 15;
		this.screenLength = l;
		this.screenHeight = h;
		int k = r.nextInt();
		dir = Math.abs(k%4);
		if (dir == 0){dx = 50; x = 0; y = k%h;} 
		else if (dir == 1){dy = -50; x = k%l; y = h;}
		else if (dir == 2){dx = -50; x = l; y = k%h;}
		else if (dir == 3){dy = 50; x = k%l; y = 0;}
		angle = Math.atan2(dy, dx)*180/Math.PI;
		
	}
	@Override
	public void update(double dt) {
		x += dx*dt;
		y += dy*dt;
		
		
	}
}

