package com.ae.apps.common.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * An ImageView that shows the source image within a Round shape.
 * This should be similar to how Google+ shows profile images.
 * Use it like you would an ImageView.
 * 
 * @author unattributed / from stackoverflow.com
 * 
 */
public class RoundedImageView extends ImageView {
	
	public RoundedImageView(Context context) {
		super(context);
	}
	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Drawable drawable = getDrawable();
		if (drawable == null) {
			return;
		}
		if (getWidth() == 0 || getHeight() == 0) {
			return;
		}
		Bitmap originalBitmap = ((BitmapDrawable) drawable).getBitmap();
		Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

		// Create the rounded bitmap and display it using the Canvas
		Bitmap roundBitmap = getCroppedBitmp(bitmap, getHeight());
		canvas.drawBitmap(roundBitmap, 0, 0, null);
	}

	/**
	 * Creates the cropped bitmap in a circular shape
	 * 
	 * @param bitmap Source bitmap which should be in a rectangular shape
	 * @param radius The radius for the Round shape
	 */
	private Bitmap getCroppedBitmp(Bitmap bitmap, int radius) {
		Bitmap sbmp;
		if (bitmap.getWidth() != radius || bitmap.getHeight() != radius) {
			sbmp = Bitmap.createScaledBitmap(bitmap, radius, radius, false);
		} else {
			sbmp = bitmap;
		}
		Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		// Create the drawing tools and draw
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		
		// TODO : remove hardcoding here
		paint.setColor(Color.parseColor("#bab399"));
		canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f, sbmp.getWidth() / 2 + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sbmp, rect, rect, paint);
		return output;
	}

}
