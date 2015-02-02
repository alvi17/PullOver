package com.alvi.pullover;

import java.util.ArrayList;
import java.util.List;

public class SlideshowInfo {
	
	private String name;
	private List<String> imageList;
	private String musicPath;
	
	public SlideshowInfo(String slideshowName)
	{
		name=slideshowName;
		imageList=new ArrayList<String>();
		musicPath=null;
		
	}
	public String getName()
	{
		return name;
	}
	public List<String> getImageList(){
		return imageList;
	}
	public void addImage(String path)
	{
		imageList.add(path);
	}
	public String getImageAt(int index)
	{
		if(index>=0 &&index < imageList.size())
			return imageList.get(index);
		else
			return null;
	}
	
	public String getMusicPath()
	{
		return musicPath;
	}
	public void setMusicPath(String path)
	{
		musicPath=path;
	}
	public int size()
	{
		return imageList.size();
	}

}
