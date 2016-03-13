package com.tt.main;

import java.io.File;

import com.example.coffemachinev2.R;
import com.loopj.android.http.MySSLSocketFactory;

import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android_serialport_api.DeliveryProtocol;
import android_serialport_api.DeliveryProtocol.CallBack;

public class VideoFragment extends Fragment 

{
	private final String TAG = "VideoFrag";
	private SurfaceView sv;
	private SurfaceHolder surfaceHolder;
	private int currentPosition = 0;
	private boolean isPlaying;
	private MediaPlayer mediaPlayer;
	String fileName = "ad.mp4";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);
	
        sv=(SurfaceView)rootView.findViewById(R.id.sv);
        surfaceHolder = sv.getHolder();
     // 为SurfaceHolder添加回调
        surfaceHolder.addCallback(callback);
        
     	//	play(0);
        return rootView;
    }

	private Callback callback = new Callback() {
		// SurfaceHolder被修改的时候回调
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.i(TAG, "SurfaceHolder 被销毁");
			// 销毁SurfaceHolder的时候记录当前的播放位置并停止播放
			if (mediaPlayer != null && mediaPlayer.isPlaying()) {
				currentPosition = mediaPlayer.getCurrentPosition();
				mediaPlayer.stop();
			}
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			Log.e(TAG, "SurfaceHolder 被创建");
			//if (currentPosition > 0) {
				// 创建SurfaceHolder的时候，如果存在上次播放的位置，则按照上次播放位置进行播放
				play(currentPosition);
			//	currentPosition = 0;
			//}
		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Log.i(TAG, "SurfaceHolder 大小被改变");
		}

	};
	
	
	/*
	 * 停止播放
	 */
	protected void stop() {
		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		//	btn_play.setEnabled(true);
			isPlaying = false;
		}
	}

	/**
	 * 开始播放
	 * 
	 * @param msec 播放初始位置    
	 */
	protected void play(final int msec) {
		// 获取视频文件地址

		String path=Environment.getExternalStorageDirectory()+"/"+fileName;
		Log.e(TAG, "path="+path);
		
		
		
		File file = new File(path);
		if (!file.exists()) {
			Toast.makeText(getActivity(), "视频文件路径错误", 0).show();
			return;
		}
		try {
			Log.e(TAG, "new MediaPlayer()");
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// 设置播放的视频源
			Log.e(TAG, "setDataSource");
	//		mediaPlayer.setDataSource(path);
		//  mediaPlayer.setDataSource(songArrayList.get(songIndex));
			mediaPlayer.setDataSource(file.getAbsolutePath());
			// 设置显示视频的SurfaceHolder
			Log.e(TAG, "setDisplay");
			mediaPlayer.setDisplay(surfaceHolder);
			Log.e(TAG, "开始装载");
			mediaPlayer.prepareAsync();
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					Log.e(TAG, "装载完成");
					mediaPlayer.start();
					// 按照初始位置播放
					mediaPlayer.seekTo(msec);
					// 设置进度条的最大进度为视频流的最大播放时长
					//seekBar.setMax(mediaPlayer.getDuration());
					// 开始线程，更新进度条的刻度
				}
			});
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					// 在播放完毕被回调
					//btn_play.setEnabled(true);
				}
			});

			mediaPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// 发生错误重新播放
					play(0);
					isPlaying = false;
					return false;
				}
			});
		} catch (Exception e) {
			Log.e(TAG, "Exception:"+e.toString());
			e.printStackTrace();
		}

	}
}

