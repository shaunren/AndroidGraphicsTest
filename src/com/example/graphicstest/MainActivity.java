package com.example.graphicstest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
}
class Panel extends SurfaceView implements SurfaceHolder.Callback{
	long t = -1000, f = 0;
	Paint _paint = new Paint();
	Random r = new Random();
	ArrayList<BoxParticle> squares = new ArrayList<BoxParticle>();
	//BoxParticle b = new BoxParticle(r, 400, 400);
	private DrawThread _thread;
	private int _x = 20;
	private int _y = 20;
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
		boolean retry = true;
		_thread.setRunning(false);
		while(retry){
			try {
				_thread.join();
				retry = false;
			}
			catch (InterruptedException e){
				
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
		Bitmap test = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		Bitmap square = BitmapFactory.decodeResource(getResources(), R.drawable.square);
		int testWidth = test.getWidth();
		int testHeight = test.getHeight();
		canvas.drawColor(Color.BLACK);
		
		long nt = SystemClock.elapsedRealtime();
		//canvas.drawBitmap(square, b.getx(), b.gety(), null);
		
		if(f%25==0) {
			squares.add(new BoxParticle(r, canvas.getHeight(), canvas.getWidth()));
			Log.d("av", "av" + canvas.getWidth() + " " + canvas.getHeight());
		}
		for(int i = 0; i <squares.size(); i++){
			if(!squares.get(i).onscreen()) squares.remove(i);
		}
		for(BoxParticle i : squares) {
			i.update(((double)nt-t)/1000);
			canvas.drawBitmap(square, i.getx()-testWidth/2, i.gety()-testHeight/2, null);				
		}
		canvas.drawBitmap(test, _x, _y, null);
		f++;
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
				if(c != null) {
					_surfaceHolder.unlockCanvasAndPost(c);
				}
				
			}
			SystemClock.sleep(40);
		}
	}
}
class BoxParticle {
	Random r;
	private int x, y, h, l;
	private int dir; //0 for left right, 1 for down up, 2 for right left, 3 for up down
	private double dx = 0;
	private double dy = 0;
	
	public BoxParticle(Random r, int h, int l) {
		this.l = l;
		this.h = h;
		int k = r.nextInt();
		dir = k%4;
		if (dir == 0){dx = 50; x = 0; y = k%h;} 
		else if (dir == 1){dy = -50; x = k%l; y = h;}
		else if (dir == 2){dx = -50; x = l; y = k%h;}
		else if (dir == 3){dy = 50; x = k%l; y = 0;}
		
	}
	public boolean onscreen() {
		return !(x > l+20 || x < -20 || y > h+20 || y < -20);
	}
	public void update(double dt){
		x += dx*dt;
		y += dy*dt;
	}
	public int getx() {
		return x;
	}
	public int gety() {
		return y;
	}
	
}