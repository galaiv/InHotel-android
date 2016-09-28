package com.inhotelappltd.inhotel.common;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.inhotelappltd.inhotel.R;


public class CircleImageCreator {
	/*public static Bitmap getRoundedShape(int height, Bitmap scaleBitmapImage) {
		// TODO Auto-generated method stub
		scaleBitmapImage = getResizedBitmap(scaleBitmapImage, height, height);

		int targetWidth = scaleBitmapImage.getWidth();
		int targetHeight = scaleBitmapImage.getHeight();
		Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight,
				Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(targetBitmap);
		Path path = new Path();
		
		 * path.addCircle(((float) targetWidth - 1) / 2, ((float) targetHeight -
		 * 1) / 2,(Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
		 * Path.Direction.CCW);
		 
		path.addCircle(((float) targetWidth - 1) / 2,
				((float) targetHeight - 1) / 2, height, Path.Direction.CCW);

		canvas.clipPath(path);

		 canvas.drawColor(Color.WHITE);
		 RectF targetRect = new RectF(left+10, top+10, left + scaledWidth,
		 top + scaledHeight);
		 final Paint paint = new Paint();
		 paint.setColor(Color.WHITE);
		 paint.setAntiAlias(true);
		 paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawColor(R.color.img_border);
		canvas.drawBitmap(scaleBitmapImage, new Rect(0, 0, targetWidth,
				targetHeight), new Rect(0, 0, targetWidth, targetHeight), null);

		return setBorder(R.color.img_border, 7, targetBitmap);
	}*/
	
	public static Bitmap getRoundedShapeNew(Bitmap scaleBitmapImage) {
	    int targetWidth = 160;
	    int targetHeight = 160;
	    Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, 
	                        targetHeight,Bitmap.Config.ARGB_8888);

	    Canvas canvas = new Canvas(targetBitmap);
	    Path path = new Path();
	    path.addCircle(((float) targetWidth - 1) / 2,
	        ((float) targetHeight - 1) / 2,
	        (Math.min(((float) targetWidth), 
	        ((float) targetHeight)) / 2),
	        Path.Direction.CCW);

	    canvas.clipPath(path);
	    Bitmap sourceBitmap = scaleBitmapImage;
	    canvas.drawBitmap(sourceBitmap, 
	        new Rect(0, 0, sourceBitmap.getWidth(),
	        sourceBitmap.getHeight()), 
	        new Rect(0, 0, targetWidth, targetHeight), null);
	    return setBorder(R.color.img_border, 7, targetBitmap);
	}
	

	public static Bitmap getResizedBitmap(Bitmap originalImage, int height,
			int width) {
		Bitmap background = Bitmap.createBitmap((int) width, (int) height,
				Config.ARGB_8888);
		float originalWidth = originalImage.getWidth(), originalHeight = originalImage
				.getHeight();
		Canvas canvas = new Canvas(background);
		float scale = width / originalWidth;
		float xTranslation = 0.0f, yTranslation = (height - originalHeight
				* scale) / 2.0f;
		Matrix transformation = new Matrix();
		transformation.postTranslate(xTranslation, yTranslation);
		transformation.preScale(scale, scale);
		Paint paint = new Paint();
		paint.setFilterBitmap(true);
		canvas.drawBitmap(originalImage, transformation, paint);
		return background;
	}

	static Bitmap setBorder(int border_color, int border_height, Bitmap bitmap) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int radius = Math.min(h / 2, w / 2);
		Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Config.ARGB_8888);

		Paint p = new Paint();
		p.setAntiAlias(true);

		Canvas c = new Canvas(output);
		c.drawARGB(0, 0, 0, 0);
		p.setStyle(Style.FILL);

		c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

		p.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));

		c.drawBitmap(bitmap, 4, 4, p);
		p.setXfermode(null);
		p.setStyle(Style.STROKE);
		// p.setColor(Color.WHITE);
		p.setColor(Color.parseColor("#3a8376"));
		p.setStrokeWidth(border_height);
		c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

		return output;
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {		
		
	     Bitmap output = Bitmap.createBitmap(bitmap .getWidth(),bitmap .getHeight(), Config.ARGB_8888);
	        Canvas canvas = new Canvas(output);

	        final int color = 0xff424242;
	        final Paint paint = new Paint();
	        final Rect rect = new Rect(0, 0,bitmap .getWidth(),bitmap .getHeight());
	        final RectF rectF = new RectF(rect);
	        final float roundPx = 30;

	        paint.setAntiAlias(true);
	        canvas.drawARGB(0, 0, 0, 0);
	        paint.setColor(color);
	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	        canvas.drawBitmap(bitmap, rect, rect, paint);
	 
	    return output;
	  }

}
