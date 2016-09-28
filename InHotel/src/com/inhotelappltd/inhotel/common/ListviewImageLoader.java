package com.inhotelappltd.inhotel.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.inhotelappltd.inhotel.R;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ListviewImageLoader {
	DisplayImageOptions options;
	ImageLoader imageLoader = ImageLoader.getInstance();
	Context context;

	public ListviewImageLoader(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		options = new DisplayImageOptions.Builder()
		    .showImageForEmptyUri(R.drawable.noimg_box)				
		   .showStubImage(R.drawable.noimg_box)
		   .cacheOnDisc()
				.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory().build();
	}

	public void loadRoundImage(final ImageView iv, String url) {
		imageLoader.loadImage(context, url, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(Bitmap loadedImage) {
				super.onLoadingComplete(loadedImage);
				iv.setImageBitmap(CircleImageCreator.getRoundedCornerBitmap(loadedImage));
				
				
			}
		});
	}
	
	public void loadRoundedShape(final ImageView iv, String url) {
		imageLoader.loadImage(context, url, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingComplete(Bitmap loadedImage) {
				super.onLoadingComplete(loadedImage);
				iv.setImageBitmap(CircleImageCreator.getRoundedShapeNew(loadedImage));
				
				
			}
		});
	}

	public void loadImage(ImageView iv, String url) {
		options = new DisplayImageOptions.Builder()
				.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory()
				.showImageForEmptyUri(R.drawable.noimg_box)
				.showStubImage(R.drawable.noimg_box).build();
		imageLoader.displayImage(url, iv, options,
				new SimpleImageLoadingListener() {
				});
	}
}
