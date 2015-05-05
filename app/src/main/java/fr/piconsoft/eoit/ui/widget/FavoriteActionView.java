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
import android.widget.CheckBox;
import android.widget.LinearLayout;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.listener.FavoriteOnCheckedChangeListener;

/**
 * @author picon.software
 *
 */
public class FavoriteActionView extends LinearLayout {

	private CheckBox favoriteTb;

	public FavoriteActionView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public FavoriteActionView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public FavoriteActionView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.favorite_actionview, this);
		favoriteTb = (CheckBox) findViewById(R.id.FAVORITE_ITEM);
	}

	public void init(boolean checked, int itemId) {
		favoriteTb.setChecked(checked);
		favoriteTb.setOnCheckedChangeListener(new FavoriteOnCheckedChangeListener(itemId, getContext()));
	}
}
