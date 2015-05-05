/*
 * Copyright (C) 2015 Picon software
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.echo.holographlibrary;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;

import fr.piconsoft.eoit.R;

public class PieGraph extends View implements HoloGraphAnimate {

	private int mPadding;
	private int mInnerCircleRatio;
	private ArrayList<PieSlice> mSlices = new ArrayList<>();
	private Paint mPaint = new Paint();
	private int mSelectedIndex = -1;
	private OnSliceClickedListener mListener;
	private boolean mDrawCompleted = false;
	private RectF mRectF = new RectF();
	private int backgroungColor = 0xFFEEEEEE;
	private Path bgPath = new Path();

	public PieGraph(Context context) {
		this(context, null);
	}

	public PieGraph(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PieGraph(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PieGraph, 0, 0);
		mInnerCircleRatio = a.getInt(R.styleable.PieGraph_pieInnerCircleRatio, 0);
		mPadding = a.getDimensionPixelSize(R.styleable.PieGraph_pieSlicePadding, 0);
		backgroungColor = a.getColor(R.styleable.PieGraph_pieBackground, 0xFFEEEEEE);

		addEditValues();
	}

	private void addEditValues() {
		if (isInEditMode()) {
			PieSlice slice = new PieSlice();
			slice.setColor(0xFFd94d4c);
			slice.setValue(4);
			slice.setBold(true);
			addSlice(slice);
			slice = new PieSlice();
			slice.setColor(0xBBd94d4c);
			slice.setValue(3);
			slice.setBold(true);
			addSlice(slice);
			slice = new PieSlice();
			slice.setColor(0xBBd94d4c);
			slice.setValue(2);
			slice.setBold(true);
			addSlice(slice);
			slice = new PieSlice();
			slice.setColor(0x33151515);
			slice.setValue(8);
			slice.setBold(false);
			addSlice(slice);
		}
	}

	public void onDraw(Canvas canvas) {
		float midX, midY, radius, innerRadius, thinRadius;

		canvas.drawColor(Color.TRANSPARENT);
		mPaint.reset();
		mPaint.setAntiAlias(true);

		float currentAngle = 270;
		float currentSweep;
		double totalValue = 0;

		midX = getWidth() / 2;
		midY = getHeight() / 2;
		if (midX < midY) {
			radius = midX;
		} else {
			radius = midY;
		}
		radius -= mPadding;
		innerRadius = radius * mInnerCircleRatio / 255;
		thinRadius = (3*radius + innerRadius) / 4;
		float outerPadding = (float) ((360 * mPadding) / (2 * Math.PI * radius));
		float thinPadding = (float) ((360 * mPadding) / (2 * Math.PI * thinRadius));
		float innerPadding = (float) ((360 * mPadding) / (2 * Math.PI * innerRadius));

		for (PieSlice slice : mSlices) {
			totalValue += slice.getValue();
		}

		mPaint.setColor(backgroungColor);
		mRectF.set(midX - radius - mPadding, midY - radius - mPadding,
				midX + radius + mPadding, midY + radius + mPadding);
		bgPath.arcTo(mRectF, 0, 359.9f);
		mRectF.set(midX - innerRadius + mPadding, midY - innerRadius + mPadding,
				midX + innerRadius - mPadding, midY + innerRadius - mPadding);
		bgPath.arcTo(mRectF, 359.9f, -359.9f);
		bgPath.close();
		canvas.drawPath(bgPath, mPaint);
		bgPath.reset();

		mPaint.reset();
		mPaint.setAntiAlias(true);

		int count = 0;
		for (PieSlice slice : mSlices) {
			Path p = slice.getPath();
			p.reset();

			if (mSelectedIndex == count && mListener != null) {
				mPaint.setColor(slice.getSelectedColor());
			} else {
				mPaint.setColor(slice.getColor());
			}
			currentSweep = (float) ((slice.getValue() / totalValue) * (360));
			if(slice.isBold()) {
				mRectF.set(midX - radius, midY - radius, midX + radius, midY + radius);
				p.arcTo(mRectF, currentAngle + outerPadding, currentSweep - outerPadding);
			} else {
				mRectF.set(midX - thinRadius, midY - thinRadius, midX + thinRadius, midY + thinRadius);
				p.arcTo(mRectF, currentAngle + thinPadding, currentSweep - thinPadding);
			}
			mRectF.set(midX - innerRadius, midY - innerRadius, midX + innerRadius, midY + innerRadius);
			p.arcTo(mRectF, (currentAngle + innerPadding) + (currentSweep - innerPadding), -(currentSweep - innerPadding));
			p.close();

			// Create selection region
			Region r = slice.getRegion();
			r.set((int) (midX - radius), (int) (midY - radius), (int) (midX + radius), (int) (midY + radius));
			canvas.drawPath(p, mPaint);
			currentAngle = currentAngle + currentSweep;

			count++;
		}
		mDrawCompleted = true;
	}

	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event) {
		if (mDrawCompleted) {
			Point point = new Point();
			point.x = (int) event.getX();
			point.y = (int) event.getY();

			int count = 0;
			Region r = new Region();
			for (PieSlice slice : mSlices) {
				r.setPath(slice.getPath(), slice.getRegion());
				switch (event.getAction()) {
					default:
						break;
					case MotionEvent.ACTION_DOWN:
						if (r.contains(point.x, point.y)) {
							mSelectedIndex = count;
							postInvalidate();
						}
						break;
					case MotionEvent.ACTION_UP:
						if (count == mSelectedIndex && mListener != null && r.contains(point.x, point.y)) {
							mListener.onClick(slice);
						}
						break;
				}
				count++;
			}
		}
		// Reset selection
		if (MotionEvent.ACTION_UP == event.getAction() || MotionEvent.ACTION_CANCEL == event.getAction()) {
			mSelectedIndex = -1;
			postInvalidate();
		}
		return true;
	}

	/**
	 * sets padding
	 */
	public void setPadding(int padding) {
		mPadding = padding;
		postInvalidate();
	}

	public void setInnerCircleRatio(int innerCircleRatio) {
		mInnerCircleRatio = innerCircleRatio;
		postInvalidate();
	}

	public void setBackground(int color) {
		backgroungColor = color;
		postInvalidate();
	}

	public ArrayList<PieSlice> getSlices() {
		return mSlices;
	}

	public void setSlices(ArrayList<PieSlice> slices) {
		mSlices = slices;
		postInvalidate();
	}

	public PieSlice getSlice(int index) {
		return mSlices.get(index);
	}

	public void addSlice(PieSlice slice) {
		mSlices.add(slice);
		postInvalidate();
	}

	public void setOnSliceClickedListener(OnSliceClickedListener listener) {
		mListener = listener;
	}

	public void removeSlices() {
		mSlices.clear();
		postInvalidate();
	}

	int mDuration = 300;
	Interpolator mInterpolator;
	Animator.AnimatorListener mAnimationListener;

	@Override
	public int getDuration() {
		return mDuration;
	}

	@Override
	public void setDuration(int duration) {
		mDuration = duration;
	}

	@Override
	public Interpolator getInterpolator() {
		return mInterpolator;
	}

	@Override
	public void setInterpolator(Interpolator interpolator) {
		mInterpolator = interpolator;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Override
	public void animateToGoalValues() {
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
			Log.e("HoloGraphLibrary compatibility error",
					"Animation not supported on api level 12 and below. Returning without animating.");
			return;
		}
		for (PieSlice s : mSlices) {
			s.setOldValue(s.getValue());
		}
		ValueAnimator va = ValueAnimator.ofFloat(0, 1);
		va.setDuration(getDuration());
		if (mInterpolator == null) {
			mInterpolator = new LinearInterpolator();
		}
		va.setInterpolator(mInterpolator);
		if (mAnimationListener != null) {
			va.addListener(mAnimationListener);
		}
		va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float f = Math.max(animation.getAnimatedFraction(),
						0.01f);//avoid blank frames; never multiply values by 0
				// Log.d("f", String.valueOf(f));
				for (PieSlice s : mSlices) {
					double x = s.getGoalValue() - s.getOldValue();
					s.setValue(s.getOldValue() + (x * f));
				}
				postInvalidate();
			}
		});
		va.start();

	}

	@Override
	public void setAnimationListener(Animator.AnimatorListener animationListener) {
		mAnimationListener = animationListener;
	}

	public interface OnSliceClickedListener {

		public abstract void onClick(PieSlice slice);
	}
}