package com.example.graphicstest;

import java.util.ArrayList;
import java.math.*;
import java.util.Calendar;
import java.util.Random;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(new Panel(this));
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
}
class Panel extends SurfaceView implements SurfaceHolder.Callback{
	private static final String AVTAG = "GraphicsTest";
	long t = -1000, f = -1000;
	Paint _paint = new Paint();
	Random r = new Random();
	ArrayList<BoxParticle> squares = new ArrayList<BoxParticle>();
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	//BoxParticle b = new BoxParticle(r, 400, 400);
	private DrawThread _thread;
	private int _x = 20;
	private int _y = 20;
	private int bulletid = 0;
	Matrix matrix = new Matrix();
	Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
	Bitmap test = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
	Bitmap square = BitmapFactory.decodeResource(getResources(), R.drawable.square);
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		_paint.setColor(Color.WHITE);
		t = SystemClock.elapsedRealtime();
		_thread.setRunning(true);
		_thread.start();
		
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		_thread.setRunning(false);
		while(true) {
			try {
				_thread.join();
				break;
			}
			catch (InterruptedException e) {
				
			}
		}
		
	}
	public Panel(Context context) {
		
		super(context);
		getHolder().addCallback(this);
		_thread = new DrawThread(getHolder(), this);
		setFocusable(true);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onDraw (Canvas canvas){
		long nt = SystemClock.elapsedRealtime();
		//if (nt-t < 16) return; // max framerate 60 FPS
		if (t < 0) t=nt;
		canvas.drawColor(Color.BLACK);
		//canvas.drawBitmap(square, b.getx(), b.gety(), null);
		
		if(nt-f >= 1000) {
			squares.add(new BoxParticle(r, canvas.getHeight(), canvas.getWidth()));
			bullets.add(new Bullet(bulletid++, _x, _y, 100, 100, canvas.getHeight(), canvas.getWidth()));
			f = nt;
		}
		for (int i=0; i<squares.size(); i++) {
			if(!squares.get(i).onscreen()) squares.remove(i);
		}
		for (int i=0; i<bullets.size(); i++) {
			if(!bullets.get(i).onscreen()) bullets.remove(i);
		}
		for (BoxParticle i : squares) {
			i.update(((double)(nt-t))/1000);
			
			matrix.setRotate(i.getAngle(),square.getWidth()/2,square.getHeight()/2);
			matrix.postTranslate(i.getx(), i.gety());
			
			//Log.d(AVTAG, "Position: " + i.getx() + ", " + i.gety());
			
			canvas.drawBitmap(square, matrix, p);
			//canvas.drawBitmap(square, i.getx()-testWidth>>1, i.gety()-testHeight>>1, null);				
			//Log.d(AVTAG, Double.toString(Math.atan2(canvas.getHeight()/2 - _y,  canvas.getWidth()/2 - _x)));
		}
		for(Bullet i : bullets) {
			i.update(((double)(nt-t))/1000);
			
			//canvas.drawBitmap(square, i.getx(), i.gety(), null);
			//Matrix matrix = new Matrix();
			
			matrix.setRotate(i.getAngle(),square.getWidth()/2,square.getHeight()/2);
			matrix.postTranslate(i.getx(), i.gety());
			//if (i.id==15)
			//	Log.d(AVTAG, "Position:" + i.getx() + " " + i.gety());
			canvas.drawBitmap(square, matrix, p);
			//Log.d(AVTAG,""+ i.getAngle());
		}
		
		
		canvas.drawBitmap(test, _x-test.getWidth()/2, _y-test.getHeight()/2, null);
		t = nt;
	}
	@Override
	public boolean onTouchEvent (MotionEvent event){
		_x = (int) event.getX();
		_y = (int) event.getY();
		return true;
	}
}
class DrawThread extends Thread{
	private SurfaceHolder _surfaceHolder;
	private Panel _panel;
	private boolean _run = false;
	public DrawThread(SurfaceHolder surfaceHolder, Panel panel){
		_surfaceHolder = surfaceHolder;
		_panel = panel;
		
	}
	public void setRunning(boolean run){
		_run = run;
	}
	@Override
	public void run() {
		Canvas c;
		while(_run) {
			c = null;
			try {
				c = _surfaceHolder.lockCanvas();
				synchronized(_surfaceHolder) {
					_panel.onDraw(c);
				}
			}
			finally {
				if(c != null)
					_surfaceHolder.unlockCanvasAndPost(c);
				
			}
		}
	}
}
class BoxParticle {
	Random r;
	private int h, l;
	private double x,y;
	private double c;
	private int dir; //0 for left right, 1 for down up, 2 for right left, 3 for up down
	private double dx = 0;
	private double dy = 0;
	private double angle;
	
	public BoxParticle(Random r, int h, int l) {
		this.l = l;
		this.h = h;
		dir = r.nextInt()%4;
		if (dir == 0){dx = 50; x = 0; y = r.nextInt()%h;} 
		else if (dir == 1){dy = -50; x = r.nextInt()%l; y = h;}
		else if (dir == 2){dx = -50; x = l; y = r.nextInt()%h;}
		else if (dir == 3){dy = 50; x = r.nextInt()%l; y = 0;}
		angle = Math.atan2(dy, dx)*180/Math.PI;
		
	}
	public boolean onscreen() {
		return !(x > l+20 || x < -20 || y > h+20 || y < -20);
	}
	public void update(double dt) {
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
	
}
class Bullet {
	int x0, y0, id;
	int targetX, targetY;
	double x, y;
	int l, h; 
	double c = 50;
	double dx, dy, dt;
	double angle;
	private static final double EPSILON = 0.00005;
	public Bullet(int id, int targetX, int targetY, int x0, int y0, int h, int l){
		this.id = id;
		this.targetX = targetX;
		this.targetY = targetY;
		this.x0 = x0;
		this.y0 = y0;
		x = x0;
		y = y0;
		this.l = l;
		this.h = h;
		dx = ((double)targetX-x0);
		dy = ((double)targetY-y0);
		double norm = Math.sqrt(dx*dx + dy*dy);
		dx *= c/norm;
		dy *= c/norm;
		if (Math.abs(dx)<EPSILON) dx = 0;
		if (Math.abs(dy)<EPSILON) dy = 0;
		angle = Math.atan2(dy, dx)*180/Math.PI;
	}
	public void update(double dt){
		x += dx*dt;
		y += dy*dt;
		
	}
	public boolean onscreen() {
		return !(x > l+20 || x < -20 || y > h+20 || y < -20);
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
	
}