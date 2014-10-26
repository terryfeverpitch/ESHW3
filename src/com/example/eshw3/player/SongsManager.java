package com.example.eshw3.player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.os.Environment;

public class SongsManager {
	public String MEDIA_PATH = "/sdcard/Music"; // Environment.getExternalStorageDirectory().getPath() + 
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private String mp3Pattern = ".mp3";
	
	public SongsManager() {

	}
	
	public ArrayList<HashMap<String, String>> getPlayList() {
	    System.out.println(MEDIA_PATH);
	    if (MEDIA_PATH != null) {
	        File home = new File(MEDIA_PATH);
	        File[] listFiles = home.listFiles();
	        Arrays.sort(listFiles);
	        if (listFiles != null && listFiles.length > 0) {
	            for (File file : listFiles) {
	                if (file.isDirectory()) {
	                	
	                    scanDirectory(file);
	                } else {
	                    addSongToList(file);
	                }
	            }
	        }
	    }
	    return songsList;
	}

	private void scanDirectory(File directory) {
	    if (directory != null) {
	        File[] listFiles = directory.listFiles();
	       
	        if (listFiles != null && listFiles.length > 0) {  	
	            for (File file : listFiles) {
	                if (file.isDirectory()) {
	                    scanDirectory(file);
	                } 
	                else {
	                    addSongToList(file);
	                }
	            }
	        }
	    }
	}

	private void addSongToList(File song) {
	    if (song.getName().endsWith(mp3Pattern)) {
	        HashMap<String, String> songMap = new HashMap<String, String>();
	        songMap.put("songTitle",
	                song.getName().substring(0, (song.getName().length() - 4)));
	        songMap.put("songPath", song.getPath());

	        songsList.add(songMap);
	    }
	}
}
