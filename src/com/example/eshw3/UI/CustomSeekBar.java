package com.example.eshw3.UI;

import com.example.eshw3.player.EventHandler;
import com.example.eshw3.player.Player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Paint.Align;
import android.media.AudioManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomSeekBar extends View implements View.OnTouchListener {
	private Player player = null;
	
	private Paint paint;
	private String time = "00:00", duration = "00:00";
	private int width, current, max;
	private int fontSize = 50;
	private int view_width, view_height; /*55FF0000*/
	private int space = 10;
	private boolean fromUser = false;
	
	public CustomSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		paint = new Paint();
		paint.setStrokeWidth(5);
		paint.setTextSize(fontSize);
		paint.setTextAlign(Align.LEFT);
		
		setOnTouchListener(this);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		view_width  = w;
		view_height = h;
	}
	
	@SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
		super.onDraw(canvas); 
		paint.setColor(Color.parseColor("#000000"));
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		
		paint.setColor(Color.parseColor("#000000"));

		paint.setTextAlign(Align.RIGHT);
		canvas.drawText(duration, view_width - space, view_height - 10, paint);
		
		paint.setTextAlign(Align.LEFT);
		if(!isEnabled()) {
			canvas.drawText("00:00", space, view_height - 10, paint);
			return;
		}
		else {
			if(player.getPlayerState() == Player.OVER)
				canvas.drawText(duration, space, view_height - 10, paint);
			else 
				canvas.drawText(player.getCurrentPositionString(), space, view_height - 10, paint);
		}
		
		if(fromUser) {
			player.seekTo((int)(((float)width / (float)view_width) * (float)max));
		}
		else {
			width = (int)(((float)player.getCurrentPosition() / (float)max) * (float)view_width);
		}
		
		canvas.drawLine(width, 0, width, view_height, paint);
		canvas.drawLine(0, 0, width, 0, paint);
		canvas.drawLine(0, view_height, width, view_height, paint);
		paint.setColor(Color.parseColor("#55FF0000"));
		canvas.drawRect(0, 0, width, view_height, paint);
	}
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
			player.setPlayerState(Player.PAUSE);
			player.pause();
			EventHandler.ibtn_play.setImageResource(android.R.drawable.ic_media_play);
			EventHandler.seekBarHandler.removeCallbacks(EventHandler.seekbar_runnable);
			width = clamp((int) event.getX());
			fromUser = true;
			invalidate();
		}
		else if(event.getAction() == MotionEvent.ACTION_MOVE) {
			EventHandler.seekBarHandler.removeCallbacks(EventHandler.seekbar_runnable);
			width = clamp((int) event.getX());
			fromUser = true;
			invalidate();
		}
		else if(event.getAction() == MotionEvent.ACTION_UP) {
			player.setPlayerState(Player.PLAYING);
			player.play();
			EventHandler.ibtn_play.setImageResource(android.R.drawable.ic_media_pause);
			
			fromUser = false;
			EventHandler.seekBarHandler.removeCallbacks(EventHandler.seekbar_runnable);
			EventHandler.seekBarHandler.postDelayed(EventHandler.seekbar_runnable, 500);
		}
		
		return true;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void setCurrentTimeText(String time) {
		this.time = time;
		invalidate();
	}
	
	public void setDurationText(String duration) {
		this.duration = duration;
		invalidate();
	}
	
	public void setDuration(final int duration) {
		max = duration;
	}
	
	private int clamp(int n) {
		if(n >= view_width)
			n = view_width;
		else if(n <= 0) 
			n = 0;
		
		return n;
	}
}
