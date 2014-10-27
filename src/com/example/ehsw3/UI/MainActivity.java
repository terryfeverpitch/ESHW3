package com.example.ehsw3.UI;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.eshw3.R;
import com.example.eshw3.player.*;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.ImageButton;
import android.widget.SeekBar;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {
	private Player player;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles
    private String[] tabs = { "Playing", "Queue" };
    private ImageButton player_ibtn_play, player_ibtn_next, player_ibtn_previous, player_ibtn_repeat, player_ibtn_shuffle, player_ibtn_volume;
    private SeekBar player_sb_timeline;
    private VolumeBar player_view_volume;
    public ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  
        initTabs();
        
        player = new Player(MainActivity.this);
        
        EventHandler.setPlayer(player);
        EventHandler.setActivity(this);
        EventHandler.setComponent();
        
        player_ibtn_play     = (ImageButton) this.findViewById(R.id.player_ibtn_play);
        player_ibtn_next     = (ImageButton) this.findViewById(R.id.player_ibtn_next);
        player_ibtn_previous = (ImageButton) this.findViewById(R.id.player_ibtn_previous);
        player_ibtn_repeat   = (ImageButton) this.findViewById(R.id.player_ibtn_repeat);
        player_ibtn_shuffle  = (ImageButton) this.findViewById(R.id.player_ibtn_shuffle);
        player_ibtn_volume   = (ImageButton) this.findViewById(R.id.player_ibtn_volume);
        player_sb_timeline	 = (SeekBar) this.findViewById(R.id.player_sb_timeline);
        player_view_volume   = (VolumeBar) this.findViewById(R.id.player_view_volume);
        
        player_ibtn_play.setOnClickListener(EventHandler.genPlayBtnClickListener());
        player_ibtn_next.setOnClickListener(EventHandler.genNextBtnClickListener());
        player_ibtn_previous.setOnClickListener(EventHandler.genPreviousBtnClickListener());
        player_ibtn_repeat.setOnClickListener(EventHandler.genRepeatShuffleBtnClickListener());        
        player_ibtn_shuffle.setOnClickListener(EventHandler.genRepeatShuffleBtnClickListener());  
        player_ibtn_volume.setOnClickListener(EventHandler.genVolumeBtnClickListener());
        player_sb_timeline.setOnSeekBarChangeListener(EventHandler.genSeekBarChangeListener());
        player_view_volume.setAudioManager(player.audioManager);
        
        
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				player_sb_timeline.setEnabled(true);
				player.setOnPreparedListener(null);
			}
		});
    }
    
    private void initTabs() {
    	// Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
 
        viewPager.setAdapter(mAdapter);
        //actionBar.setHomeButtonEnabled(false);
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        
 
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }
        viewPager.setCurrentItem(1);
        viewPager.setOnPageChangeListener(EventHandler.genPagerChangeListener());
    }
 
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }
 
    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        viewPager.setCurrentItem(tab.getPosition());
    }
 
    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }
    
    public ViewPager getViewPager() {
    	return viewPager;
    }
}
