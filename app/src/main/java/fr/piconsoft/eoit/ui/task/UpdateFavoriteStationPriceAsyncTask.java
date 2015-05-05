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

package fr.piconsoft.eoit.ui.task;

import android.app.Dialog;
import android.content.ContentUris;
import android.database.Cursor;

import fr.piconsoft.eoit.EOITApplication;
import fr.piconsoft.eoit.model.Station;
import fr.piconsoft.eoit.util.DbUtil;

/**
 * @author picon.software
 */
public class UpdateFavoriteStationPriceAsyncTask extends AbstractDialogRetrofitAsyncTask {

	private final EOITApplication application;
	private int itemId;

	public UpdateFavoriteStationPriceAsyncTask(Dialog dialog, EOITApplication application, int itemId) {
		super(dialog);
		this.itemId = itemId;
		this.application = application;
	}

	@Override
	protected Void doInBackground(Void params) {
		Cursor cursor = context.getContentResolver().query(Station.CONTENT_URI,
				new String[] {Station._ID, Station.REGION_ID, Station.SOLAR_SYSTEM_ID},
				Station.FAVORITE + " = 1",
				null,
				null);

		try {
			if(DbUtil.hasAtLeastOneRow(cursor)) {
				while (!cursor.isAfterLast()) {
					int solarSystemId =
							cursor.getInt(cursor.getColumnIndexOrThrow(Station.SOLAR_SYSTEM_ID));
					int regionId =
							cursor.getInt(cursor.getColumnIndexOrThrow(Station.REGION_ID));

					PriceUpdaterAsyncTask task = new PriceUpdaterAsyncTask(context);
					application.inject(task);
					task.updatePricesForItemId(regionId, solarSystemId, itemId);

					cursor.moveToNext();
				}
			}
		} finally {
			cursor.close();
		}

		context.getContentResolver().notifyChange(
				ContentUris.withAppendedId(Station.CONTENT_ID_FAVORITE_STATIONS_PRICES_URI_BASE, itemId), null);

		return null;
	}
}
