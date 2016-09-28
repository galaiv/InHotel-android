package com.inhotelappltd.inhotel.utils;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inhotelappltd.inhotel.R;


public class ConversationAdapter extends BaseAdapter {

		LayoutInflater laytout_inflator;
		Context context;
		int count = 0;
		SharedPreferences prefs;
		ArrayList<ArrayList<String>> arr_chat = new ArrayList<ArrayList<String>>();

		public ConversationAdapter(Context context,
				ArrayList<ArrayList<String>> arr_conv) {
			this.arr_chat = arr_conv;
			laytout_inflator = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.context = context;

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
			String lineSep = System.getProperty("line.separator");

			// TODO Auto-generated method stub
			ViewHolder view_holder = new ViewHolder();

			if (convertView == null) {
				convertView = laytout_inflator.inflate(
						R.layout.custom_chat_view, null);
				view_holder = intitViews(convertView);
				convertView.setTag(view_holder);
			} else {
				view_holder = (ViewHolder) convertView.getTag();
			}

			/*
			 * arr_det.add(jobj_loop.getString("from"));//from 1
			 * arr_det.add(jobj_loop.getString("to"));//to 2
			 * arr_det.add(jobj_loop.getString("from_name"));//from_name 3
			 * arr_det.add(jobj_loop.getString("to_name"));//to_name 4
			 * arr_det.add(jobj_loop.getString("from_image"));//from_image 5
			 * arr_det.add(jobj_loop.getString("to_image"));//to_image 6
			 * arr_det.add(jobj_loop.getString("video"));//video 7
			 * arr_det.add(jobj_loop.getString("video_thumb"));//video_thumb 8
			 * arr_det.add(jobj_loop.getString("sent"));//sent 9
			 * arr_det.add(jobj_loop.getString("message"));//message 10
			 * arr_det.add
			 * (jobj_loop.getString("drink_offerings_id"));//drink_offerings_id  11
			 *
			 * 
			 * 
			 * arr_det.add(jobj_drink.getString("drinks_id"));//drinks_id 12
			 * arr_det.add(jobj_drink.getString("drink_title"));//drink_title 13
			 * arr_det.add(jobj_drink.getString("status_flag"));//status_flag 14
			 * arr_det.add(jobj_drink.getString("image"));//image 15			 */

		

			// CHAT FROM OUR SIDE(ME)
			if (arr_chat.get(position).get(0)
					.equalsIgnoreCase(prefs.getString("user_id", ""))) {
				view_holder.tv_cht_me_name.setText(arr_chat.get(position)
						.get(2));
				view_holder.tv_cht_me_time.setText(arr_chat.get(position)
						.get(8));

				if (arr_chat.get(position).get(10).equalsIgnoreCase("0")) {
					view_holder.tv_cht_me_message.setText((Html
							.fromHtml(arr_chat.get(position).get(9))));
				}

				view_holder.me.setVisibility(View.VISIBLE);
				view_holder.you.setVisibility(View.GONE);

				/*new ImageLoadTask(context, arr_chat.get(position).get(4),
						view_holder.iv_me, 0).execute();*/

				// VIDEO
				if (!arr_chat.get(position).get(6).equalsIgnoreCase("")) {
					view_holder.rl_me_video.setVisibility(View.VISIBLE);
				/*	new ImageLoadTask(context, arr_chat.get(position).get(7),
							view_holder.iv_thumb, 1).execute();

					view_holder.rl_me_video
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									playVideo(arr_chat.get(position).get(6));

								}
							});*/
				} else {
					view_holder.rl_me_video.setVisibility(View.GONE);
				}

				// DRINK
				if (!arr_chat.get(position).get(10).equalsIgnoreCase("0")) {
					view_holder.rl_me_drink.setVisibility(View.VISIBLE);

					view_holder.iv_accept.setVisibility(View.INVISIBLE);
					view_holder.iv_thanks.setVisibility(View.INVISIBLE);
					/*new ImageLoadTask(context, arr_chat.get(position).get(14),
							view_holder.iv_image, 1).execute();*/
					view_holder.tv_name.setText(arr_chat.get(position).get(12));

					if (arr_chat.get(position).get(13).equalsIgnoreCase("A")) {
						view_holder.tv_cht_me_message
								.setText("Accepted the drink offer");
					} else if (arr_chat.get(position).get(13)
							.equalsIgnoreCase("D")) {
						view_holder.tv_cht_me_message.setText(arr_chat.get(
								position).get(3)
								+ " said Thanks, but no thanks");
					} else if (arr_chat.get(position).get(13)
							.equalsIgnoreCase("P")) {
						view_holder.tv_cht_me_message
								.setText(/* arr_chat.get(position).get(3)+ */"You offered a drink");
					}
				} else {
					view_holder.rl_me_drink.setVisibility(View.GONE);
				}

			} else {// ***************************************************************************************

				view_holder.me.setVisibility(View.GONE);
				view_holder.you.setVisibility(View.VISIBLE);
				view_holder.tv_cht_you_name.setText(arr_chat.get(position).get(
						2));
				view_holder.tv_cht_you_time.setText(arr_chat.get(position).get(
						8));
				view_holder.tv_cht_ypu_message.setText((Html.fromHtml(arr_chat
						.get(position).get(9))));

				/*new ImageLoadTask(context, arr_chat.get(position).get(4),
						view_holder.iv_you, 0).execute();*/

				// VIDEO
				if (!arr_chat.get(position).get(6).equalsIgnoreCase("")) {
					view_holder.rl_you_video.setVisibility(View.VISIBLE);

					view_holder.rl_you_video
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									
									//playVideo(arr_chat.get(position).get(6));
								}
							});
				/*	new ImageLoadTask(context, arr_chat.get(position).get(7),
							view_holder.iv_you_thumb, 1).execute();*/

				} else {
					view_holder.rl_you_video.setVisibility(View.GONE);
				}

			

				// DRINK
				if (!arr_chat.get(position).get(10).equalsIgnoreCase("0")) {
					
					view_holder.rl_you_drink.setVisibility(View.VISIBLE);

				/*	new ImageLoadTask(context, arr_chat.get(position).get(14),
							view_holder.iv_you_image, 1).execute();*/
					view_holder.tv_you_name.setText(arr_chat.get(position).get(
							12));

					/*
					 * if(arr_chat.get(position).get(13).equalsIgnoreCase("A")){
					 * view_holder
					 * .tv_cht_ypu_message.setText("Accepted the drink offer");
					 * }else
					 * if(arr_chat.get(position).get(13).equalsIgnoreCase("D")){
					 * view_holder
					 * .tv_cht_ypu_message.setText(arr_chat.get(position
					 * ).get(3)+" said Thanks, but no thanks"); }else
					 */
					view_holder.tv_cht_ypu_message.setText(arr_chat.get(
							position).get(3)
							+ " offered a drink");
					if (arr_chat.get(position).get(13).equalsIgnoreCase("P")) {
						view_holder.iv_you_accept.setVisibility(View.VISIBLE);
						view_holder.iv_you_thanks.setVisibility(View.VISIBLE);

					} else {
						view_holder.iv_you_accept.setVisibility(View.INVISIBLE);
						view_holder.iv_you_thanks.setVisibility(View.INVISIBLE);
					}
				} else {
					// view_holder.rl_you_drink.setVisibility(View.GONE);
					view_holder.iv_you_accept.setVisibility(View.INVISIBLE);
					view_holder.iv_you_thanks.setVisibility(View.INVISIBLE);
				}

			}

			//

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

			// tv_cht_me_message
			//

			// **************************ME*****************************
			view_holder.me = (RelativeLayout) view.findViewById(R.id.me);
			view_holder.iv_me = (ImageView) view.findViewById(R.id.iv_me);
			view_holder.tv_cht_me_name = (TextView) view
					.findViewById(R.id.tv_cht_me_name);
			view_holder.tv_cht_me_time = (TextView) view
					.findViewById(R.id.tv_cht_me_time);
			view_holder.tv_cht_me_message = (TextView) view
					.findViewById(R.id.tv_cht_me_message);
			view_holder.rl_me_drink = (RelativeLayout) view
					.findViewById(R.id.rl_me_drink);
			view_holder.rl_me_video = (RelativeLayout) view
					.findViewById(R.id.rl_me_video);
			// **************************YOU*****************************

			view_holder.you = (RelativeLayout) view.findViewById(R.id.you);
			view_holder.iv_you = (ImageView) view.findViewById(R.id.iv_you);
			view_holder.tv_cht_you_name = (TextView) view
					.findViewById(R.id.tv_cht_you_name);
			view_holder.tv_cht_you_time = (TextView) view
					.findViewById(R.id.tv_cht_you_time);
			view_holder.tv_cht_ypu_message = (TextView) view
					.findViewById(R.id.tv_cht_ypu_message);

			view_holder.rl_you_video = (RelativeLayout) view
					.findViewById(R.id.rl_you_video);
			view_holder.rl_you_drink = (RelativeLayout) view
					.findViewById(R.id.rl_you_drink);

			// **************************DRINK*****************************
			view_holder.iv_image = (ImageView) view.findViewById(R.id.iv_image);
			view_holder.iv_thanks = (ImageView) view
					.findViewById(R.id.iv_thanks);
			view_holder.iv_accept = (ImageView) view
					.findViewById(R.id.iv_accept);
			view_holder.tv_name = (TextView) view.findViewById(R.id.tv_name);

			view_holder.iv_you_image = (ImageView) view
					.findViewById(R.id.iv_you_image);
			view_holder.iv_you_thanks = (ImageView) view
					.findViewById(R.id.iv_you_thanks);
			view_holder.iv_you_accept = (ImageView) view
					.findViewById(R.id.iv_you_accept);
			view_holder.tv_you_name = (TextView) view
					.findViewById(R.id.tv_you_name);

			// **************************VIDEO*****************************
			view_holder.iv_thumb = (ImageView) view.findViewById(R.id.iv_thumb);
			view_holder.iv_you_thumb = (ImageView) view
					.findViewById(R.id.iv_you_thumb);

			return view_holder;
		}

		/**
		 * View holder class
		 */
		public class ViewHolder {
			ImageView iv_me, iv_you, iv_image, iv_thanks, iv_accept, iv_thumb,
					iv_you_thumb, iv_you_image, iv_you_thanks, iv_you_accept;
			TextView tv_you_name, tv_name, tv_cht_me_name, tv_cht_me_time,
					tv_cht_me_message, tv_cht_you_time, tv_cht_you_name,
					tv_cht_ypu_message;
			RelativeLayout you, me;
			RelativeLayout rl_me_drink, rl_you_video, rl_me_video,
					rl_you_drink;
		}
		
}