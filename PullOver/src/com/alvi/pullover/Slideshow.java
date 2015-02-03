package com.alvi.pullover;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Slideshow extends ListActivity{
   public static final String NAME_EXTRA="NAME";
   static List<SlideshowInfo> slideshowList;
   private ListView slideshowListView;
   private SlideshowAdapter slideshowAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		slideshowListView=getListView();
		slideshowList=new ArrayList<SlideshowInfo>();
		slideshowAdapter =new SlideshowAdapter(this,slideshowList);
		slideshowListView.setAdapter(slideshowAdapter);
		AlertDialog.Builder builder=new AlertDialog.Builder(this);
		builder.setMessage("Welcome to PullOver");
		builder.setPositiveButton("OK",null);
		//builder.show();
        final AlertDialog dlg = builder.create();
        
        dlg.show();
		final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                dlg.dismiss(); // when the task active then close the dialog
                t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
            }
        }, 2000);
	}
	@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// TODO Auto-generated method stub
			 super.onCreateOptionsMenu(menu);
			 getMenuInflater().inflate(R.menu.slideshow_menu,menu);
			 return true;
		}
	
	private static final int EDIT_ID=0;
	
	@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			// TODO Auto-generated method stub
			LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view=inflater.inflate(R.layout.slideshow_dialog, null);
			final EditText nameEditText=(EditText)view.findViewById(R.id.nameeditText);
			AlertDialog.Builder inputDialog=new AlertDialog.Builder(this);
			inputDialog.setView(view);
			inputDialog.setTitle("Set Title Name");
			inputDialog.setPositiveButton("Set Name",
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							String name=nameEditText.getText().toString().trim();
							Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
							SlideshowInfo info=new SlideshowInfo(name);
							Log.e("SlideInfo",info.getName());
							
							if(name.length()!=0)
							{
								slideshowList.add(info);
								
								Intent editSlideshowIntent=new Intent(Slideshow.this,SlideshowEditor.class);
								editSlideshowIntent.putExtra(NAME_EXTRA,name);
								startActivityForResult(editSlideshowIntent, 0);
							}
							else
							{
								Toast.makeText(getApplicationContext(), "Please Provide a Name", Toast.LENGTH_LONG).show();
							}
						}
					}
					);
			inputDialog.setNegativeButton("Cancel",null);
			inputDialog.show();
			return super.onOptionsItemSelected(item);
		}
	    @Override
	    	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    		// TODO Auto-generated method stub
	    		super.onActivityResult(requestCode, resultCode, data);
	    		slideshowAdapter.notifyDataSetChanged();
	    	}
	    
	    private class ViewHolder
	    {
	    	TextView nameTextView;
	    	ImageView imageView;
	    	Button playButton;
	    	Button editButton,deleteButton;
	    }
	    private class SlideshowAdapter extends ArrayAdapter<SlideshowInfo>
	    {
	    	private List<SlideshowInfo> items;
	    	private LayoutInflater inflater;
	    	
	    	public SlideshowAdapter(Context context,List<SlideshowInfo> items)
	    	{
	    		super(context,-1,items);
	    		this.items=items;
	    		inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	}
	    	@Override
	    	public View getView(int position, View convertView, ViewGroup parent) {
	    		// TODO Auto-generated method stub
	    		ViewHolder viewHolder;
	    		if(convertView==null)
	    		{
	    			convertView=inflater.inflate(R.layout.slideshow_list_item, null);
	    			viewHolder=new ViewHolder();
	    			viewHolder.nameTextView=(TextView)convertView.findViewById(R.id.nametextView);
	    			viewHolder.imageView=(ImageView)convertView.findViewById(R.id.slideshowImageView);
	    			viewHolder.playButton=(Button)convertView.findViewById(R.id.playbutton);
	    			viewHolder.editButton=(Button)convertView.findViewById(R.id.editbutton);
	    			viewHolder.deleteButton=(Button)convertView.findViewById(R.id.deletebutton);
	    			convertView.setTag(viewHolder);
	    		}
	    		else
	    			viewHolder=(ViewHolder)convertView.getTag();
	    		
	    		SlideshowInfo slideshowInfo=items.get(position);
	    		viewHolder.nameTextView.setText(slideshowInfo.getName());
	    		if(slideshowInfo.size()>0)
	    		{
	    			String firstItem = slideshowInfo.getImageAt(0);
	    			new LoadThumbnailTask().execute(viewHolder.imageView,Uri.parse(firstItem));
	    		}
	    		viewHolder.playButton.setTag(slideshowInfo);
	    		viewHolder.playButton.setOnClickListener(playButtonListener);
	    		
	    		viewHolder.editButton.setTag(slideshowInfo);
	    		viewHolder.editButton.setOnClickListener(editButtonListener);
	    		
	    		viewHolder.deleteButton.setTag(slideshowInfo);
	    		viewHolder.deleteButton.setOnClickListener(deleteButtonListener);
	    		
	    		
	    		return convertView;
	    	}
	    	
	    }
	    
	    
	    private class LoadThumbnailTask extends AsyncTask<Object, Object, Bitmap>
	    {
	    	ImageView imageView;
			@Override
			protected Bitmap doInBackground(Object... params) {
				// TODO Auto-generated method stub
				imageView=(ImageView)params[0];
				return Slideshow.getThumbnail((Uri)params[1],getContentResolver(),new BitmapFactory.Options());
			}			
			@Override
			protected void onPostExecute(Bitmap result) {
				// TODO Auto-generated method stub
				
				super.onPostExecute(result);
				imageView.setImageBitmap(result);
			}
	    	
	    }
	    
	 android.view.View.OnClickListener playButtonListener=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent playSlideshow=new Intent(Slideshow.this,SlideshowPlayer.class);
			playSlideshow.putExtra(NAME_EXTRA,((SlideshowInfo)v.getTag()).getName());
			startActivity(playSlideshow);
		}
	 	};
	 	
	 	
	 	private android.view.View.OnClickListener editButtonListener= new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent editSlideshow=new Intent(Slideshow.this,SlideshowEditor.class);
				editSlideshow.putExtra(NAME_EXTRA,((SlideshowInfo)v.getTag()).getName());
				startActivityForResult(editSlideshow, 0);
			}
		};
		
		android.view.View.OnClickListener deleteButtonListener= new View.OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder=new AlertDialog.Builder(Slideshow.this);
				builder.setTitle("Confirm");
				builder.setMessage("Are you sure to delete?");
				builder.setPositiveButton("Delete", 
						new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Slideshow.slideshowList.remove((SlideshowInfo) v.getTag());
								slideshowAdapter.notifyDataSetChanged();
							}
						}
						);
				builder.setNegativeButton("Cancel",null);
				builder.show();
			}
		};
		
		public static SlideshowInfo getSlideshowInfo(String name)
		{
			
			if(slideshowList.contains(name))
			{
				Log.e("Slide ", name);	
			}
			else
				Log.e("Not Slide"," "+ name);	
			//return slideshowList.get(0);
			
			
			for(SlideshowInfo slide:slideshowList)
				if(slide.getName().equals(name))
					return slide;
			
			return null;
		}
		
		public static Bitmap getThumbnail(Uri uri,ContentResolver cr,BitmapFactory.Options options)
		{
			int id=Integer.parseInt(uri.getLastPathSegment());
			Bitmap bitmap=MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, options);
			return bitmap;
			
		}
}
