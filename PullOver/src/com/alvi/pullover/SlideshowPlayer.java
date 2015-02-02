package com.alvi.pullover;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;

public class SlideshowPlayer extends Activity{
	private static final String MEDIA_TIME="MEDIA_TIME";
	private static final String IMAGE_INDEXX="IMAGE_INDEX";
	private static final String SLIDESHOW_NAME="SLIDESHOW_IMAGE";
	private static final int DURATION=5000;
	private ImageView imageView;
	private String slideshowName;
	private SlideshowInfo slideshow;
	private BitmapFactory.Options options;
	private Handler handler;
	private int nextItemIndex;
	private int mediaTime;
	private MediaPlayer mediaPlayer;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slideshow_player);
		
		imageView=(ImageView)findViewById(R.id.imageView);
		if(savedInstanceState==null)
		{
			slideshowName=getIntent().getStringExtra(Slideshow.NAME_EXTRA);
			mediaTime=0;
			nextItemIndex=0;
			
		}
		else
		{
			mediaTime=savedInstanceState.getInt(MEDIA_TIME);
			nextItemIndex=savedInstanceState.getInt(IMAGE_INDEXX);
			slideshowName=savedInstanceState.getString(SLIDESHOW_NAME);
		}
		
		slideshow=Slideshow.getSlideshowInfo(slideshowName);
		
		options=new BitmapFactory.Options();
		options.inSampleSize=4;
		
		if(slideshow.getMusicPath()!=null)
		{
			try
			{
				mediaPlayer=new MediaPlayer();
				mediaPlayer.setDataSource(this,Uri.parse(slideshow.getMusicPath()));
				mediaPlayer.prepare();
				mediaPlayer.setLooping(true);
				mediaPlayer.seekTo(mediaTime);
			}
			catch(Exception e)
			{
				Log.v("TAG",e.toString());
			}
		}
		handler=new Handler();
		
	}
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		handler.post(updateSlideshow);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(mediaPlayer!=null)
			mediaPlayer.pause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(mediaPlayer!=null)
			mediaPlayer.start();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		handler.removeCallbacks(updateSlideshow);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mediaPlayer!=null)
		{
			mediaPlayer.release();
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		if(mediaPlayer!=null)
		{
			outState.putInt(MEDIA_TIME, mediaPlayer.getCurrentPosition());
		}
		outState.putInt(IMAGE_INDEXX, nextItemIndex-1);
		outState.putString(SLIDESHOW_NAME,slideshowName);
	}
	
	private Runnable updateSlideshow=new Runnable()
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(nextItemIndex>=slideshow.size())
			{
				if(mediaPlayer!=null && mediaPlayer.isPlaying())
					mediaPlayer.reset();
				finish();
			}
			else
			{
				String item=slideshow.getImageAt(nextItemIndex);
				new LoadImageTask().execute(Uri.parse(item));
				++nextItemIndex;
			}
		}
		class LoadImageTask extends AsyncTask<Uri,Object,Bitmap>
		{

			@Override
			protected Bitmap doInBackground(Uri... params) {
				// TODO Auto-generated method stub
				return getBitmap(params[0],getContentResolver(),options);
			}
			
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				BitmapDrawable next=new BitmapDrawable(result);
				next.setGravity(Gravity.CENTER);
				Drawable previous=imageView.getDrawable();
				
				if(previous instanceof TransitionDrawable)
					previous=((TransitionDrawable)previous).getDrawable(1);
				if(previous==null)
					imageView.setImageDrawable(next);
				else
				{
					Drawable[] drawables={previous,next};
					TransitionDrawable transition=new TransitionDrawable(drawables);
					imageView.setImageDrawable(transition);
					transition.startTransition(1000);
				}
				handler.postDelayed(updateSlideshow, DURATION);
				
			};
			public Bitmap getBitmap(Uri uri,ContentResolver cr,BitmapFactory.Options options)
			{
				Bitmap bitmap=null;
				try
				{
					InputStream input=cr.openInputStream(uri);
					bitmap=BitmapFactory.decodeStream(input,null,options);
					
				}
				catch(FileNotFoundException e)
				{
					Log.v("TAG",e.toString());
				}
				return bitmap;
				
			}
		}
		
	};
	
}
