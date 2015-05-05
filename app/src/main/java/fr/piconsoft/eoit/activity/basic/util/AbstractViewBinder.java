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

package fr.piconsoft.eoit.activity.basic.util;

import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.piconsoft.eoit.util.IconUtil;

/**
 * @author picon.software
 */
public abstract class AbstractViewBinder implements SimpleCursorAdapter.ViewBinder {

	protected void initIcon(View view, long id) {
		ImageView imageView = (ImageView) view;
		IconUtil.initIcon(id, imageView);
	}

	protected void initText(View view, String value) {
		TextView textView = (TextView) view;
		textView.setText(value);
	}

	protected void initWarnIcon(View view, double price) {
		if (Double.isNaN(price)) {
			view.setVisibility(View.VISIBLE);
		} else {
			view.setVisibility(View.GONE);
		}
	}
}
