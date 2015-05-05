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

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import fr.piconsoft.eoit.R;

/**
 * @author picon.software
 *
 */
public class ProgressActionView extends LinearLayout implements View.OnClickListener {

	private OnProgressListener listener;
	private View refreshIcon, progressbar;
	private boolean progressOnly;

	public ProgressActionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		init();
	}

	public ProgressActionView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}

	public ProgressActionView(Context context) {
		super(context);

		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.progress_actionview, this);

		refreshIcon = findViewById(R.id.refresh_icon);
		progressbar = findViewById(R.id.progressbar);
		refreshIcon.setOnClickListener(this);
		startProgress();
	}

	public void setProgressOnly(boolean progressOnly) {
		this.progressOnly = progressOnly;
		if(progressOnly) {
			refreshIcon.setVisibility(View.GONE);
		}
	}

	public void setListener(OnProgressListener listener) {
		this.listener = listener;
	}

	@Override
	public void onClick(View v) {
		startProgress();
		if(listener != null) {
			listener.onRefreshStart();
		}
	}

	public void startProgress() {
		refreshIcon.setVisibility(View.GONE);
		progressbar.setVisibility(View.VISIBLE);
	}

	public void stopProgress() {
		if(!progressOnly) {
			refreshIcon.setVisibility(View.VISIBLE);
		}
		progressbar.setVisibility(View.GONE);
	}

	public static interface OnProgressListener {
		void onRefreshStart();
	}
}
