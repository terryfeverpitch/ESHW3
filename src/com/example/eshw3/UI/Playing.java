package com.example.eshw3.UI;

import com.example.eshw3.R;
import com.example.eshw3.player.EventHandler;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
 
public class Playing extends Fragment {
	public static View rootView = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {        
        return rootView = inflater.inflate(R.layout.playing_frag, container, false);
    }
    
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		EventHandler.setRelativeView(rootView);
	}
}