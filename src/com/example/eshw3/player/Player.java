package com.example.eshw3.player;

import java.io.IOException;
import java.util.ArrayList;

import com.example.ehsw3.UI.*;
import com.example.eshw3.R;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.MediaPlayer;


public class Player extends MediaPlayer {
	public AudioManager audioManager;
	
	private int state = 0; // init = 0; playing = 1; pause = 2; changing = 3; replay = 4;
	private int mode  = 0; // single = 0; repeat all = 1; repeat single = 2;
	private int currentIndex = 0;

	final int HOUR   = 60 * 60 * 1000;
    final int MINUTE = 60 * 1000;
    final int SECOND = 1000;
 
    // state constant
    public final static int INIT	 = 0;
    public final static int PLAYING  = 1;
    public final static int PAUSE	 = 2;
    public final static int CHANGING = 3;
    public final static int OVER     = 4;
    // mode constant
    public final static int SINGLE        = 0;
    public final static int REPEAT_ALL    = 1;
    public final static int REPEAT_SINGLE = 2;
    // shuffle
    private boolean isShuffle = false;
    public int[] shuffleArray;
    private int shuffleIndex = 0;
    private ArrayList<Integer> ar;
    
	public Player(Activity parentActivity) {
		super();
		audioManager = (AudioManager) parentActivity.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 3, 0);

		setOnCompletionListener(EventHandler.genPlayerCompletionListener());
	}
	// state and mode
	public void play() {
		try {
			if(state == INIT || state == CHANGING) {
				String currentPath = Queue.songsList.get(currentIndex).get("songPath");
	    		reset();
				setDataSource(currentPath);
	    		prepare();
			}	
    		start();
    		state = PLAYING;
		} catch (IllegalStateException | IllegalArgumentException | SecurityException | IOException e) {
			stop();
        }
	}
	
	public void setPlayerState(final int STATE_CONSTANT) {
		state = STATE_CONSTANT;
	}
	
	public void setPlayerMode(final int MODE_CONSTANT) {
		mode = MODE_CONSTANT;
	}
	
	public void setShuffle(boolean isShuffle) {
		this.isShuffle = isShuffle;
		if(isShuffle) {
			ar = null;
			ar = new ArrayList<Integer>();
			for(int i = 0; i < Queue.songsList.size(); i++) {
				ar.add(new Integer(i));
			}
			
			shuffleArray = null;
			shuffleArray = new int[Queue.songsList.size()];
			for(int j = 0; j < shuffleArray.length; j++) {
				shuffleArray[j] = random();
			}
		}
	}
	
	private int random() {
		int size = ar.size();
		if(size > 0) {
			return (Integer)ar.remove((int)(size * Math.random())).intValue();
		}
		else
			return -1;
	}
	
	public boolean isShuffle() {
		return isShuffle;
	}

	public int getPlayerState() {
		return state;
	}

	public int getPlayerMode() {
		return mode;
	}
	// time
	public String getDurationString() {
		return milliToString(getDuration());
	}
	
	public String getCurrentPositionString() {
		return milliToString(getCurrentPosition());
	}
	
	private String milliToString(int millisecond) {
		int ms = millisecond;
		int h, m, s;
		
		if(ms == -1) 
			return "00:00";
		
		h = ms / HOUR;   ms = (h > 0) ? (ms % (h * HOUR)) : (ms);
		m = ms / MINUTE; ms = (m > 0) ? (ms % (m * MINUTE)) : (ms);
		s = ms / SECOND;
		
		if(h > 0) {
			return String.format("%02d:%02d:%02d", h, m, s);
		}
		else {
			return String.format("%02d:%02d", m, s);
		}
	}
	// index
	public void setPlayerIndex(int i) {
		if(i < 0) {
			currentIndex = Queue.songsList.size() - 1;
		}
		else {
			currentIndex = i % Queue.songsList.size();
		}
	}
	
	public int getPlayerIndex() {
		return currentIndex;
	}
	
	public void setShuffleIndex(int i) {
		if(i < 0) {
			shuffleIndex = Queue.songsList.size() - 1;
		}
		else {
			shuffleIndex = i % Queue.songsList.size();
		}
		
		currentIndex = shuffleArray[shuffleIndex];
	}
	
	public int getShuffleIndex() {
		return shuffleIndex;
	}
}
