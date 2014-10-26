package com.example.ehsw3.UI;

import com.example.eshw3.R;
import com.example.eshw3.R.layout;
import com.example.eshw3.player.EventHandler;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
 
public class Playing extends Fragment {
	public static View rootView = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.playing_frag, container, false);
        return rootView;
    }
    
    @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		EventHandler.setRelativeView(rootView);
	}
}