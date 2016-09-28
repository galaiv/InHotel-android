package com.inhotelappltd.inhotel.common;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.inhotelappltd.inhotel.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class ChatUserAdapter extends BaseAdapter {
	DisplayImageOptions options;
	ImageLoader imageLoader = ImageLoader.getInstance();
	LayoutInflater laytout_inflator;
	Context context;
	int count = 0;
	Bitmap bit;

	SparseArray<Bitmap> _bitmapCache = new SparseArray<Bitmap>();

	ArrayList<ArrayList<String>> arr_chat = new ArrayList<ArrayList<String>>();

	public ChatUserAdapter(Context context, ArrayList<ArrayList<String>> arr_chat) {
		this.arr_chat = arr_chat;
		laytout_inflator = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.context = context;
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.noimg_box)
				.showStubImage(R.drawable.noimg_box).cacheOnDisc()
				.cacheInMemory().bitmapConfig(Bitmap.Config.RGB_565)
				.cacheInMemory().build();
		
		
		for(int i=0;i<arr_chat.size();i++){
			loadRoundImage(i, arr_chat.get(i).get(3));
		}

	}

	@Override
	public int getCount() {
		return arr_chat.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub 
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {

		// TODO Auto-generated method stub
		ViewHolder view_holder = new ViewHolder();

		if (convertView == null) {
			convertView = laytout_inflator.inflate(R.layout.custom_chatlist_adapter, null);
			view_holder = intitViews(convertView);
			convertView.setTag(view_holder);			
			// loadRoundImage(position,arr_chat.get(position).get(3));
		} else {
			view_holder = (ViewHolder) convertView.getTag();
		}
		/*Bitmap bit = _bitmapCache.get(position);
		if (bit != null) {
			
			notifyDataSetChanged();
		} else {
			loadRoundImage(position, arr_chat.get(position).get(3));
		}*/
		view_holder.iv_image.setImageBitmap(_bitmapCache.get(position));
		view_holder.tv_name.setText(arr_chat.get(position).get(2));
		
		if(null != arr_chat.get(position).get(4)){
			if(!arr_chat.get(position).get(4).equalsIgnoreCase(null) || !arr_chat.get(position).get(4).equalsIgnoreCase("null")){
				view_holder.tv_time.setText(arr_chat.get(position).get(4));
			}
		}
		
		Log.e("CHAT ADPATER",arr_chat.get(position).get(5)+"sd");
		
		if(null != arr_chat.get(position).get(5)){
			if(!arr_chat.get(position).get(5).equalsIgnoreCase(null) || !arr_chat.get(position).get(5).equalsIgnoreCase("null")){
				view_holder.tv_message.setText(Html.fromHtml(arr_chat.get(position).get(5)));
			}
		}
		
		
		if(arr_chat.get(position).get(7).equalsIgnoreCase("drink")){
			view_holder.drink_Arrow.setVisibility(View.VISIBLE);
			view_holder.drink_Arrow.setImageResource(R.drawable.send_message);
		}else{
			view_holder.drink_Arrow.setVisibility(View.GONE);
		}

		
		view_holder.iv_image.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.e("ONCLICK",position+"");
				
				showFullScreenUserImageDialog(position);				
			}

			
		});
		// new ImageLoadTask(context,
		// arr_chat.get(position).get(3),view_holder.iv_image).execute();
		
		return convertView;
	}
	
	/**
	 * Method for initializing the view holder object
	 * 
	 * @param view
	 * @return
	 */
	public ViewHolder intitViews(View view) {
		ViewHolder view_holder = new ViewHolder();
		view_holder.iv_image = (ImageView) view.findViewById(R.id.iv_image);
		view_holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
		view_holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
		view_holder.tv_message = (TextView) view.findViewById(R.id.tv_message);
		view_holder.drink_Arrow= (ImageView) view.findViewById(R.id.drink_Arrow);
		return view_holder;
	}

	/**
	 * View holder class
	 */
	public class ViewHolder {
		ImageView iv_image,drink_Arrow;	 
	
		TextView tv_name, tv_time, tv_message;

	}

	public void loadRoundImage(final int iv, final String url) {
		imageLoader.loadImage(context, url, options,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingComplete(Bitmap loadedImage) {
						super.onLoadingComplete(loadedImage);
						// iv.setImageBitmap(CircleImageCreator.getRoundedCornerBitmap(loadedImage));

						_bitmapCache.put(iv, CircleImageCreator.getRoundedCornerBitmap(loadedImage));
						notifyDataSetChanged();
					}
				});
	}
	
	
	
	
	
	/**
	* Shows the user coverImageURL in full screen
	*/
	public void showFullScreenUserImageDialog(int p) {
	final Dialog builder=new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
	LayoutInflater factory = LayoutInflater.from(context);
	final View view = factory.inflate(R.layout.custom_pic_alert, null);
	ImageView iv_large=(ImageView)view.findViewById(R.id.iv_large);
	ImageLoader imageLoader = ImageLoader.getInstance();
	imageLoader.init(ImageLoaderConfiguration.createDefault(context));
	imageLoader.displayImage( arr_chat.get(p).get(3), iv_large,
	new SimpleImageLoadingListener() {
	});

	//iv_large.setImageURI(Uri.parse(ProfileHomeFragment.userimageUrl));

	iv_large.setOnClickListener(new View.OnClickListener() {
	@Override
	public void onClick(View v) {
	// TODO Auto-generated method stub
	builder.cancel();
	}
	});
	builder.setContentView(view);
	builder.show();
	}
	

}
