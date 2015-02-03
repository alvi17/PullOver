package com.alvi.pullover;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SlideshowEditor extends ListActivity{
	private SlideshowEditorAdapter slideshowEditorAdapter;
	private SlideshowInfo slideshow;
	private List<String> slide;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slideshow_editor);
		
		
		String name =getIntent().getStringExtra(Slideshow.NAME_EXTRA);
		
		slideshow=Slideshow.getSlideshowInfo(name);
		
		Button doneButton=(Button)findViewById(R.id.doneButton);
		doneButton.setOnClickListener(doneButtonListener);
		
		Button addPictureButton=(Button)findViewById(R.id.addPictureButton);
		addPictureButton.setOnClickListener(addPictureButtonListener);
		
		Button addMusicButton=(Button)findViewById(R.id.addMusicButton);
		addMusicButton.setOnClickListener(addMusicButtonListener);
		
		Button playButton=(Button)findViewById(R.id.playButton);
		playButton.setOnClickListener(playButtonListener);
		
		slide=slideshow.getImageList();
		slideshowEditorAdapter=new SlideshowEditorAdapter(this,slide);
		getListView().setAdapter(slideshowEditorAdapter);
			
	}
	
	private static final int PICTURE_ID=1;
	private static final int MUSIC_ID=2;
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK)
		{
			Uri selectedUri=data.getData();
			if(requestCode==PICTURE_ID)
			{
				slideshow.addImage(selectedUri.toString());
				slideshowEditorAdapter.notifyDataSetChanged();
			}
			else if(requestCode==MUSIC_ID)
			{
				slideshow.setMusicPath(selectedUri.toString());
			}
		}
	}
	
	private OnClickListener doneButtonListener =new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	
	private OnClickListener addPictureButtonListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			
	        intent.setType("image/*");
	        intent.setAction(Intent.ACTION_GET_CONTENT);
	        startActivityForResult(Intent.createChooser(intent,"Select Picture"),1);
//			Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
//			intent.setType("images/");
//			startActivityForResult(Intent.createChooser(intent,getResources().getText(R.string.choose)),PICTURE_ID);
		}
	};
	
	private OnClickListener addMusicButtonListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("audio/");
			startActivityForResult(Intent.createChooser(intent,"Select Music"), MUSIC_ID);
			
		}
	};
	
	private OnClickListener playButtonListener =new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent playSlideshow=new Intent(SlideshowEditor.this,SlideshowPlayer.class);
			playSlideshow.putExtra(Slideshow.NAME_EXTRA, slideshow.getName());
			startActivity(playSlideshow);
		}
	};
	
	private OnClickListener deleteButtonListener=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			slideshowEditorAdapter.remove((String)v.getTag());
		}
	};
	
	private static class ViewHolder
	{
		ImageView slideImageView;
		Button deleteButton;
		
	}
	
	
	private class SlideshowEditorAdapter extends ArrayAdapter<String>
	{
		private List<String> items;
		private LayoutInflater inflater;
		

		public SlideshowEditorAdapter(Context context, 
				List<String> items) {
			
			super(context,-1, items);
			this.items=items;
			inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder;
			Log.e("GETVIEW","IN getView");
			if(convertView==null){
				convertView=inflater.inflate(R.layout.slideshow_edit_item,null);
				viewHolder=new ViewHolder();
				viewHolder.slideImageView=(ImageView)convertView.findViewById(R.id.slideshowImageView);
				viewHolder.deleteButton=(Button)convertView.findViewById(R.id.deleteButton);
				convertView.setTag(viewHolder);
				
			}
			else
				viewHolder=(ViewHolder)convertView.getTag();
			
			String item=items.get(position);
			new LoadThumbnailTask().execute(viewHolder.slideImageView,Uri.parse(item));
			
			viewHolder.deleteButton.setTag(item);
			viewHolder.deleteButton.setOnClickListener(deleteButtonListener);
			return convertView;
			
		}
		
	}
	private class LoadThumbnailTask extends AsyncTask<Object,Object,Bitmap>
	{
		ImageView imageView;
		
		@Override
		protected Bitmap doInBackground(Object... params) {
			// TODO Auto-generated method stub
			imageView=(ImageView)params[0];
			Log.e("GETVIEW 1","IN Thumbnail");
			return Slideshow.getThumbnail((Uri) params[1],getContentResolver(),new BitmapFactory.Options());
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			imageView.setImageBitmap(result);
		}
		
	}
	

}
