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

import fr.piconsoft.eoit.model.Station;

/**
 * @author picon.software
 *
 */
public class FavoriteStationsOnCheckedChangeListener implements OnCheckedChangeListener {

	private int stationId;
	private Context context;

	public FavoriteStationsOnCheckedChangeListener(int stationId, Context context) {
		super();
		this.stationId = stationId;
		this.context = context;
	}

	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(int itemId) {
		this.stationId = itemId;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		new FavoriteUpdaterTask(stationId, context).execute(isChecked);
	}

	public static class FavoriteUpdaterTask extends AsyncTask<Boolean, Void, Void> {

		private int stationId;
		private Context context;

		public FavoriteUpdaterTask(int stationId, Context context) {
			this.stationId = stationId;
			this.context = context;
		}

		@Override
		protected Void doInBackground(Boolean... isChecked) {
			Uri updateStationUri = ContentUris.withAppendedId(Station.CONTENT_ID_URI_BASE, stationId);
			ContentValues values = new ContentValues();
			values.put(Station.FAVORITE, isChecked[0]);

			context.getContentResolver().update(
					updateStationUri,
					values,
					null,
					null);

			context.getContentResolver().notifyChange(Station.CONTENT_ALL_URI, null);

			return null;
		}
	}
}
