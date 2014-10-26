package com.example.ehsw3.UI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.example.eshw3.R;
import com.example.eshw3.R.id;
import com.example.eshw3.R.layout;
import com.example.eshw3.player.EventHandler;
import com.example.eshw3.player.SongsManager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
 
public class Queue extends Fragment {
	public static ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
	private ArrayAdapter listAdapter;
	public static View rootView = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
		rootView  = inflater.inflate(R.layout.queue_frag, container, false);
        
        SongsManager songManager = new SongsManager();
        songsList = songManager.getPlayList();
        
        ArrayList<String> titleList = new ArrayList<String>();
        
        //String[] titleList = new String[songsList.size()];
        for(int i = 0 ;i < songsList.size(); i++) {
        	titleList.add(songsList.get(i).get("songTitle"));
        }
        
        // Collections.sort(titleList);     
        //ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        
        ListView queue_lv_list = (ListView) rootView.findViewById(R.id.queue_lv_list);
        TextView tv = (TextView) rootView.findViewById(R.id.textView1);
        
        tv.setText(songsList.size() + ", " + songManager.MEDIA_PATH);
        
        listAdapter = new ArrayAdapter(rootView.getContext(), android.R.layout.simple_list_item_activated_1, titleList);
        
        queue_lv_list.setSelected(true);
        queue_lv_list.setAdapter(listAdapter);
        
        queue_lv_list.setOnItemClickListener(EventHandler.genQueueHanlder());
        
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		EventHandler.setRelativeView(rootView);
		
	}
	
	public HashMap<String, String> getSongInfo(int index) {
		HashMap<String, String> info = songsList.get(index);
		return info;
	}
	
	public static View getRootView() {
		return rootView;
	}
}
