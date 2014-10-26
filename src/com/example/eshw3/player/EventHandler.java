package com.example.eshw3.player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.example.ehsw3.UI.MainActivity;
import com.example.ehsw3.UI.Playing;
import com.example.ehsw3.UI.Queue;
import com.example.ehsw3.UI.VolumeBar;
import com.example.eshw3.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.Albums;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class EventHandler {
	private static Player player;
	private static MainActivity activity;
	
	private static SeekBar sb;
	private static ImageButton ibtn_play, ibtn_next, ibtn_previous, ibtn_repeat, ibtn_shuffle, ibtn_volume;
	private static Button btn_queue;
	private static TextView tv_time;
	private static TextView tv_duration;
	private static ListView queue_lv_list;
	private static ImageView cover;
	private static VolumeBar volumeBar;
	
	public EventHandler(MainActivity a, Player p) {
		setPlayer(p);
		setActivity(a);
		setComponent();
	}
	
	public static void setPlayer(Player p) {
		player = p;
	}
	
	public static void setActivity(MainActivity a) {
		activity = a;
	}
	
	public static void setRelativeView(View v) {
		if(v.equals(Queue.rootView)) {
			queue_lv_list = (ListView) Queue.rootView.findViewById(R.id.queue_lv_list);       
	        if(Queue.songsList.size() == 0) {
	        	ibtn_play.setEnabled(false);
	        	ibtn_next.setEnabled(false);
	        	ibtn_previous.setEnabled(false);
	        	ibtn_volume.setEnabled(false);
	        	sb.setEnabled(false);
	        }
		}
		else if(v.equals(Playing.rootView)) {
			cover = (ImageView) Playing.rootView.findViewById(R.id.player_img_cover);
			btn_queue = (Button) Playing.rootView.findViewById(R.id.player_btn_queue);
			btn_queue.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					activity.getViewPager().setCurrentItem(0, true);
				}
			});
			
			volumeBar.getLayoutParams().height = cover.getHeight() - btn_queue.getHeight();
		}
	}
	
	public static void setComponent() {
		sb            = (SeekBar) activity.findViewById(R.id.player_sb_timeline);
		tv_time       = (TextView) activity.findViewById(R.id.player_tv_time);
		tv_duration   = (TextView) activity.findViewById(R.id.player_tv_duration);
		ibtn_play     = (ImageButton) activity.findViewById(R.id.player_ibtn_play);
		ibtn_next     = (ImageButton) activity.findViewById(R.id.player_ibtn_next);
        ibtn_previous = (ImageButton) activity.findViewById(R.id.player_ibtn_previous);
		ibtn_repeat   = (ImageButton) activity.findViewById(R.id.player_ibtn_repeat);
		ibtn_shuffle  = (ImageButton) activity.findViewById(R.id.player_ibtn_shuffle);
		ibtn_volume   = (ImageButton) activity.findViewById(R.id.player_ibtn_volume);
		volumeBar	  = (VolumeBar) activity.findViewById(R.id.player_view_volume);
		
		sb.setEnabled(false);
		volumeBar.setVisibility(View.INVISIBLE);
		
		/*volumeBar.setMaxVolume(Player.audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		volumeBar.getLayoutParams().width = ibtn_volume.getWidth();*/
	}
	
	private static Handler timeHandler = new Handler();
	private static Runnable seekbar_runnable = new Runnable() {
	    @Override
	    public void run() {
	        if(player != null){        
	        	tv_time.setText(player.getCurrentPositionString());
	    		sb.setProgress(player.getCurrentPosition());
	        }
	        sb.postDelayed(this, 500);
	    }
	};
	
	private static Runnable pause_runnable = new Runnable() {
	    @Override
	    public void run() {
	        if(player != null){        
	    		if(tv_time.getVisibility() == View.INVISIBLE) {
	    			tv_time.setVisibility(View.VISIBLE);
	    			tv_duration.setVisibility(View.VISIBLE);
	    		}
	    		else {
	    			tv_time.setVisibility(View.INVISIBLE);
	    			tv_duration.setVisibility(View.INVISIBLE);
	    		}
	        }
	        timeHandler.postDelayed(this, 500);
	    }
	};
	
	public static Handler volumeHandler = new Handler();
	public static Runnable volume_runnable = new Runnable() {
	    @Override
	    public void run() {
	    	ibtn_volume.setSelected(!ibtn_volume.isSelected());
			volumeBar.setVisibility(View.INVISIBLE);
			volumeHandler.removeCallbacks(volume_runnable);
	    }
	};
	
	public static AdapterView.OnItemClickListener genQueueHanlder() {
		return new AdapterView.OnItemClickListener () {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(player.getPlayerState() != Player.OVER) {
					ListView listView = (ListView) parent;		
					player.setPlayerIndex(position);
					player.setPlayerState(Player.CHANGING);
				}

				play();
			}
		};
	}
	
	public static View.OnClickListener genPlayBtnClickListener() {
		return new View.OnClickListener () {
			@Override
			public void onClick(View v) {	
				if(player.getPlayerState() == Player.INIT || player.getPlayerState() == Player.OVER) {
					updateListView();
				}
				else if(player.getPlayerState() == Player.PLAYING) {
					player.setPlayerState(Player.PAUSE);
					player.pause();
					sb.removeCallbacks(seekbar_runnable);
					timeHandler.removeCallbacks(pause_runnable);
					timeHandler.post(pause_runnable);
					ibtn_play.setImageResource(android.R.drawable.ic_media_play);
				}
				else if(player.getPlayerState() == Player.PAUSE ) {
					play();
					ibtn_play.setImageResource(android.R.drawable.ic_media_pause);
				}
			}
		};
	}
	
	public static View.OnClickListener genNextBtnClickListener() {
		return new View.OnClickListener () {
			@Override
			public void onClick(View v) {
				if(player.isShuffle())
					player.setShuffleIndex((player.getShuffleIndex () + 1));
				else 
					player.setPlayerIndex((player.getPlayerIndex() + 1));
				updateListView();
			}
		};
	}
	
	public static View.OnClickListener genPreviousBtnClickListener() {
		return new View.OnClickListener () {
			@Override
			public void onClick(View v) {		
				if(player.isShuffle())
					player.setShuffleIndex((player.getShuffleIndex () - 1));
				else 
					player.setPlayerIndex((player.getPlayerIndex() - 1));
				updateListView();
			}
		};
	}	
	
	public static View.OnClickListener genRepeatShuffleBtnClickListener() {
		return new View.OnClickListener () {
			@Override
			public void onClick(View v) {		
				ImageButton ibtn = (ImageButton) v;
				String toast;

				if(ibtn.equals(ibtn_repeat)) {
					int mode = player.getPlayerMode();
					
					switch(mode) {
						case Player.SINGLE :
							ibtn.setSelected(true);
							toast = "repeat all";
							player.setPlayerMode(Player.REPEAT_ALL);
							break;
						case Player.REPEAT_ALL :
							ibtn.setSelected(true);
							toast = "repeat single";
							player.setPlayerMode(Player.REPEAT_SINGLE);
							ibtn.setImageResource(R.drawable.media_repeat_one);
							break;
						case Player.REPEAT_SINGLE : default :
							ibtn.setSelected(false);
							toast = "single";
							player.setPlayerMode(Player.SINGLE);
							ibtn.setImageResource(R.drawable.media_repeat);
							break;		
					}
				}
				else {
					ibtn.setSelected(!ibtn.isSelected());
					if(ibtn.isSelected())  {
						player.setShuffle(true);
						toast = "shuffle on";
					}
					else  {
						player.setShuffle(false);
						toast = "shuffle off"; 
					}
				}
				CustomToast.showToast(activity, toast, 500);
			}
		};
	}
	
	public static View.OnClickListener genVolumeBtnClickListener() {
		return new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				ibtn_volume.setSelected(!ibtn_volume.isSelected());
				
				if(ibtn_volume.isSelected()) {
					volumeBar.setVisibility(View.VISIBLE);
					volumeBar.getLayoutParams().height = cover.getHeight() - btn_queue.getHeight();
					volumeBar.getLayoutParams().width = ibtn_volume.getWidth();
					volumeBar.setBackgroundColor(Color.parseColor("#55000000"));
					volumeBar.setVolume(player.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
					volumeBar.invalidate();
					volumeHandler.removeCallbacks(volume_runnable);
					volumeHandler.postDelayed(volume_runnable, 2500);
				}
				else {
					volumeBar.setVisibility(View.INVISIBLE);
				}
			}
		};
	}
	
	public static SeekBar.OnSeekBarChangeListener genSeekBarChangeListener() {
		return new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(fromUser) {
					player.seekTo(progress);
					tv_time.setText(player.getCurrentPositionString());
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				seekBar.removeCallbacks(seekbar_runnable);
				timeHandler.removeCallbacks(pause_runnable);
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if(player.getPlayerState() == Player.PLAYING) {
					seekBar.post(seekbar_runnable);
				}
				else if(player.getPlayerState() == Player.PAUSE){
					timeHandler.post(pause_runnable);
				}
				else if(player.getPlayerState() == Player.INIT){
					try {
						player.setPlayerState(Player.OVER);
						player.seekTo(seekBar.getProgress());
						tv_time.setText(player.getCurrentPositionString());
					} catch (IllegalStateException  e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
	}
	
	public static MediaPlayer.OnCompletionListener genPlayerCompletionListener() {
		return new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				try { 
					if(ibtn_repeat.isSelected()) { // repeat
						if(player.getPlayerMode() == Player.REPEAT_ALL) {
							if(player.isShuffle())
								player.setShuffleIndex((player.getShuffleIndex() + 1));
							else
								player.setPlayerIndex((player.getPlayerIndex() + 1));
							player.setPlayerState(Player.CHANGING);
							updateListView();
						}
						else {
							player.setPlayerState(Player.PLAYING);
							play();
						}
					}
					else {
						ibtn_play.setImageResource(android.R.drawable.ic_media_play);
						timeHandler.removeCallbacks(pause_runnable);
						sb.removeCallbacks(seekbar_runnable);
						sb.setProgress(player.getDuration());
						tv_time.setText(player.getDurationString());
						player.setPlayerState(Player.INIT);
					}
		         } catch (Exception e) { 
		           e.printStackTrace(); 
		         }
			}
		};
	}
	// private
	private static void play() {
		player.play();
		
		String title = Queue.songsList.get(player.getPlayerIndex()).get("songTitle");
		
		updateCover(title + ".mp3");

		btn_queue.setText((player.getPlayerIndex() + 1) + ". " + title);	
		ibtn_play.setImageResource(android.R.drawable.ic_media_pause);
		tv_time.setVisibility(View.VISIBLE);
		tv_duration.setVisibility(View.VISIBLE);
		tv_duration.setText(player.getDurationString());
		timeHandler.removeCallbacks(pause_runnable);	
		sb.setMax(player.getDuration());
		sb.removeCallbacks(seekbar_runnable);
		sb.post(seekbar_runnable);

		CustomToast.showToast(activity, "Play : \"" + title + "\"", 500);
	}
	
	private static void updateListView() {
		View listAdapter = queue_lv_list.getAdapter().getView(player.getPlayerIndex(), null, null);
		int position = player.getPlayerIndex();
		long id = queue_lv_list.getAdapter().getItemId(player.getPlayerIndex());
		queue_lv_list.performItemClick(listAdapter, position, id);
	}
	
	private static void updateCover(String displayName) {
		Cursor cursorID = activity.getContentResolver().query(
			MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
			new String[] {MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.ALBUM}, 
			MediaStore.Audio.Media.DISPLAY_NAME + "=?", 
            new String[] {displayName},  
			null);
		
		String id = "";
		if(cursorID.moveToFirst()) {
			id = cursorID.getString(cursorID.getColumnIndex(MediaStore.Audio.Media.ALBUM));
		}
		cursorID.close();
		
		Cursor cursorCover = activity.getContentResolver().query(
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
				new String[] {MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ALBUM_ART}, 
				MediaStore.Audio.Albums.ALBUM + "=?", 
	            new String[] {id}, 
				null);	
		if (cursorCover.moveToFirst()) {
			String path = cursorCover.getString(cursorCover.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));	
			if(path != null) {
				cover.setImageURI(Uri.fromFile(new File(path)));
				cover.setAlpha(0.8f);
			}
			else
				cover.setImageResource(R.drawable.default_album);
		}
		cursorCover.close();
	}
	
	private static class CustomToast {
		private static Toast toast;
		private static Handler toastHandler = new Handler();
		private static Runnable toastRunnable = new Runnable() {
			public void run() {
				toast.cancel();
			}
		};
		
		public static void showToast(Context ctx, String text, int duration) {
			toastHandler.removeCallbacks(toastRunnable);
			if (toast != null)
				toast.setText(text);
			else
				toast = toast.makeText(ctx, text, Toast.LENGTH_SHORT);
			toastHandler.postDelayed(toastRunnable, duration + 1000);
			toast.show();
		}
	}
}