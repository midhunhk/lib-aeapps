package com.ae.apps.common.views;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * The SimpleGraphView is a very basic Graph view that displays a pie chart and optionally the labels as legend. A
 * custom theme can be specified through a constructor version.
 * 
 * Note that not all validations are made in this class, hence please make sure of the data that are passed in.
 * 
 * @author midhun_harikumar
 * 
 */
public class SimpleGraphView extends View {
	/**
	 * If a theme is not provided, this will be used
	 */
	private static int[]		DEFAULT_THEME	= { Color.LTGRAY, Color.YELLOW, Color.GREEN, Color.GRAY, Color.BLACK,
			Color.RED, Color.DKGRAY, Color.CYAN };
	private static final int	PADDING			= 10;
	private float				temp			= 0;
	private float				mDensity		= 1;
	private float[]				value_degree	= null;
	private String[]			labels			= null;
	private int[]				colors			= null;
	private Paint				mChartPaint		= new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint				mTextPaint		= new Paint(Paint.ANTI_ALIAS_FLAG);
	private RectF				mBoundingRect	= new RectF(PADDING, PADDING, 250, 250);

	public SimpleGraphView(Context context, float[] values, int[] themeColors) {
		super(context);

		// Check if a valid themeColors array is passed to us
		if (themeColors != null && themeColors.length > 0) {
			colors = themeColors;
		} else {
			colors = DEFAULT_THEME;
		}
		value_degree = new float[values.length];
		for (int i = 0; i < values.length; i++) {
			value_degree[i] = values[i];
		}
		// Set FontSize based on scale
		mDensity = getResources().getDisplayMetrics().density;
		mTextPaint.setTextSize(12 * mDensity);
	}

	/**
	 * Simple Pie Chart with label fields
	 * 
	 * @param context
	 *            the application context
	 * @param values
	 *            Values in degrees for the Pie Chart
	 * @param labels
	 *            Labels to display for each part of the pie
	 */
	public SimpleGraphView(Context context, float[] values, String[] labels) {
		this(context, values, DEFAULT_THEME);
		this.labels = labels;
	}

	/**
	 * Create a Simple Pie Chart with label and values. Specify a custom theme for the Pie Chart
	 * 
	 * @param context
	 *            the application context
	 * @param values
	 *            Values in degrees for the Pie Chart
	 * @param labels
	 *            Labels to display for each part of the pie
	 * @param themeColors
	 *            Theme colors as array of packed ints alpha, r, g, b
	 */
	public SimpleGraphView(Context context, float[] values, String[] labels, int[] themeColors) {
		this(context, values, themeColors);
		this.labels = labels;
	}

	public SimpleGraphView(Context context) {
		super(context);
	}

	public SimpleGraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SimpleGraphView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		for (int i = 0; i < value_degree.length; i++) {
			if (i == 0) {
				mChartPaint.setColor(colors[i % colors.length]);
				canvas.drawArc(mBoundingRect, - 90 , value_degree[i], true, mChartPaint);
			} else {
				temp += value_degree[i - 1];
				mChartPaint.setColor(colors[i % colors.length]);
				canvas.drawArc(mBoundingRect, temp - 90 , value_degree[i], true, mChartPaint);
			}
		}

		if (labels != null && labels.length > 0) {
			// Draw the labels
			int xCoordinate = (int) (PADDING * mDensity);
			int yCoordinate = (int) mBoundingRect.right + PADDING;
			for (int i = 0; i < labels.length; i++) {
				mTextPaint.setColor(colors[i % colors.length]);
				canvas.drawText(labels[i], yCoordinate, xCoordinate, mTextPaint);
				xCoordinate += 30;
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int rectSize = 0;
		int specWidth = MeasureSpec.getSize(widthMeasureSpec);
		int specHeight = MeasureSpec.getSize(heightMeasureSpec);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// We are going to divide the screen width into two for chart and the text block
			rectSize = Math.min(specHeight, specWidth / 2);
		} else {
			// For landscape, use 65% height instead of 50%
			rectSize = (int) (specHeight * 0.75);
		}
		// The Bounding rectangle becomes a square and the chart is drawn on it
		mBoundingRect.set(PADDING, PADDING, rectSize, rectSize);
		setMeasuredDimension(specWidth, rectSize + PADDING);
	}

}
