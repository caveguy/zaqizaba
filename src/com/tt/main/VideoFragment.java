package com.tt.main;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Fragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.text.style.LineHeightSpan.WithDensity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.coffemachinev2.R;

public class VideoFragment extends Fragment 

{
	private final String TAG = "VideoFrag";
	private SurfaceView sv;
	private SurfaceHolder surfaceHolder;
	private int currentPosition = 0;
	private boolean isPlaying;
	private MediaPlayer mediaPlayer=null;
//	String fileName = "ad.mp4";
	Timer myTimer=null;
	int which_file=0;
	
	 List<String> allVideoList = new ArrayList<String>();// 视频信息集合
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);
	
        sv=(SurfaceView)rootView.findViewById(R.id.sv);
        surfaceHolder = sv.getHolder();
     // 为SurfaceHolder添加回调
        surfaceHolder.addCallback(callback);
        new FindThread().start();
        myTimer=new Timer();
        myTimer.schedule(new MyTimerTask(), 5000, 60*1000);
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

				play(which_file);

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
	protected void play(int which) {
		// 获取视频文件地址

		String path;
		if(allVideoList.isEmpty())
			return;
		if(allVideoList.get(which)!=null)
			path=allVideoList.get(which);
		else{
			which_file=0;
			path=allVideoList.get(which_file);
			}
		//String path=Environment.getExternalStorageDirectory()+"/"+fileName;
		Log.e(TAG, "path="+path);
		
		
		
		File file = new File(path);
		if (!file.exists()) {
			Toast.makeText(getActivity(), "视频文件路径错误", 0).show();
			return;
		}
		try {
			if(mediaPlayer==null){
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				Log.e(TAG, "setDisplay");
				mediaPlayer.setDisplay(surfaceHolder);
			}
			mediaPlayer.setDataSource(file.getAbsolutePath());
			Log.e(TAG, "开始装载");
			mediaPlayer.prepareAsync();
			mediaPlayer.setLooping(false);//不循环
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					Log.e(TAG, "装载完成");
					mediaPlayer.start();
					// 按照初始位置播放
					mediaPlayer.seekTo(0);
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
					 play(which_file++);
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
	
	
	
	
	
	
	
	
	  
//	allVideoList = new ArrayList<String>();  
//	getVideoFile(allVideoList,Environment.getExternalStorageDirectory());// 获得视频文件  
	  
	private void getVideoFile(final List<String> list, File file) {// 获得视频文件  
	  
	        file.listFiles(new FileFilter() {  
	  
	            @Override  
	            public boolean accept(File file) {  
	                // sdCard找到视频名称  
	                String name = file.getName();  
	  
	                int i = name.indexOf('.');  
	                if (i != -1) {  
	                    name = name.substring(i);  
	                    if (name.equalsIgnoreCase(".mp4")  
	                            || name.equalsIgnoreCase(".3gp")  
	                           // || name.equalsIgnoreCase(".wmv")  
	                          //  || name.equalsIgnoreCase(".ts")  
	                          //  || name.equalsIgnoreCase(".rmvb")  
	                            || name.equalsIgnoreCase(".mov")  
	                            || name.equalsIgnoreCase(".m4v")  
	                            || name.equalsIgnoreCase(".avi")  
	                           // || name.equalsIgnoreCase(".m3u8")  
	                          //  || name.equalsIgnoreCase(".3gpp")  
	                          //  || name.equalsIgnoreCase(".3gpp2")  
	                            || name.equalsIgnoreCase(".mkv")  
	                           // || name.equalsIgnoreCase(".flv")  
	                            || name.equalsIgnoreCase(".divx")  
	                            || name.equalsIgnoreCase(".f4v")  
	                          //  || name.equalsIgnoreCase(".rm")  
	                           // || name.equalsIgnoreCase(".asf")  
	                           // || name.equalsIgnoreCase(".ram")  
	                            || name.equalsIgnoreCase(".mpg")  
	                           // || name.equalsIgnoreCase(".v8")  
	                          //  || name.equalsIgnoreCase(".swf")  
	                         //   || name.equalsIgnoreCase(".m2v")  
	                          //  || name.equalsIgnoreCase(".asx")  
	                          //  || name.equalsIgnoreCase(".ra")  
	                          //  || name.equalsIgnoreCase(".ndivx")  
	                            || name.equalsIgnoreCase(".xvid")) {  
	                       // String vi = new String();  
//	                        vi.setDisplayName(file.getName());  
//	                        vi.setPath(file.getAbsolutePath()); 
	                    	String vi=file.getAbsolutePath();
	                        list.add(vi);
	       
	                        return true;  
	                    }  
	                } 
//	                else if (file.isDirectory()) {  //嵌套还是不嵌套？
//	                    getVideoFile(list, file);  
//	                }  
	                return false;  
	            }  
	        });  
	    }  
	
	boolean isPlaying(){
		if(mediaPlayer==null){
			return false;
		}
		else if(mediaPlayer.isPlaying()){
			return true;
		}
		return false;
	}
	
	
	class FindThread extends Thread{
		@Override
		public void run() {
			getVideoFile(allVideoList,Environment.getExternalStorageDirectory());// 获得视频文件  
			super.run();
		}

	}

	
	class MyTimerTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(!isPlaying()){
				play(which_file);
			}
		}
		
	}
	

}
