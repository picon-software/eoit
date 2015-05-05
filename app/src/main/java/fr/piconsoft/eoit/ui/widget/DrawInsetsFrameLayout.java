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

package fr.piconsoft.eoit.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import fr.piconsoft.eoit.R;

/**
 * A layout that draws something in the insets passed to {@link #fitSystemWindows(Rect)}, i.e. the area above UI chrome
 * (status and navigation bars, overlay action bars).
 */
public class DrawInsetsFrameLayout extends FrameLayout {
	private Drawable mInsetBackground;

	private Rect mInsets;
	private Rect mTempRect = new Rect();
	private OnInsetsCallback mOnInsetsCallback;

	public DrawInsetsFrameLayout(Context context) {
		super(context);
		init(context, null, 0);
	}

	public DrawInsetsFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public DrawInsetsFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {
		final TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.DrawInsetsFrameLayout, defStyle, 0);
		if (a == null) {
			return;
		}
		mInsetBackground = a.getDrawable(R.styleable.DrawInsetsFrameLayout_insetBackground);
		a.recycle();

		setWillNotDraw(true);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	protected boolean fitSystemWindows(Rect insets) {
		mInsets = new Rect(insets);
		setWillNotDraw(mInsetBackground == null);
		postInvalidateOnAnimation();
		if (mOnInsetsCallback != null) {
			mOnInsetsCallback.onInsetsChanged(insets);
		}
		return true; // consume insets
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int width = getWidth();
		int height = getHeight();
		if (mInsets != null && mInsetBackground != null) {
			// Top
			mTempRect.set(0, 0, width, mInsets.top);
			mInsetBackground.setBounds(mTempRect);
			mInsetBackground.draw(canvas);

			// Bottom
			mTempRect.set(0, height - mInsets.bottom, width, height);
			mInsetBackground.setBounds(mTempRect);
			mInsetBackground.draw(canvas);

			// Left
			mTempRect.set(0, mInsets.top, mInsets.left, height - mInsets.bottom);
			mInsetBackground.setBounds(mTempRect);
			mInsetBackground.draw(canvas);

			// Right
			mTempRect.set(width - mInsets.right, mInsets.top, width, height - mInsets.bottom);
			mInsetBackground.setBounds(mTempRect);
			mInsetBackground.draw(canvas);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mInsetBackground != null) {
			mInsetBackground.setCallback(this);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mInsetBackground != null) {
			mInsetBackground.setCallback(null);
		}
	}

	/**
	 * Allows the calling container to specify a callback for custom processing when insets change (i.e. when
	 * {@link #fitSystemWindows(Rect)} is called. This is useful for setting padding on UI elements based on
	 * UI chrome insets (e.g. a Google Map or a ListView). When using with ListView or GridView, remember to set
	 * clipToPadding to false.
	 */
	public void setOnInsetsCallback(OnInsetsCallback onInsetsCallback) {
		mOnInsetsCallback = onInsetsCallback;
	}

	public static interface OnInsetsCallback {
		public void onInsetsChanged(Rect insets);
	}
}