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

package fr.piconsoft.eoit.ui.listener;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import fr.piconsoft.eoit.model.Item;

/**
 * @author picon.software
 *
 */
public class FavoriteOnCheckedChangeListener implements OnCheckedChangeListener {

	private int itemId;
	private Context context;

	public FavoriteOnCheckedChangeListener(int itemId, Context context) {
		super();
		this.itemId = itemId;
		this.context = context;
	}

	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		new FavoriteUpdaterTask().execute(isChecked);
	}

	private class FavoriteUpdaterTask extends AsyncTask<Boolean, Void, Void> {

		@Override
		protected Void doInBackground(Boolean... isChecked) {
			Uri updateItemUri = ContentUris.withAppendedId(Item.CONTENT_ID_URI_BASE, itemId);
			ContentValues values = new ContentValues();
			values.put(Item.FAVORITE, isChecked[0]);

			context.getContentResolver().update(
					updateItemUri,
					values,
					null,
					null);

			return null;
		}
	}
}
