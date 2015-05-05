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

package fr.piconsoft.eoit.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.model.ColumnsNames.Region;
import fr.piconsoft.eoit.model.Station;
import fr.piconsoft.eoit.activity.basic.util.StationListViewBinder;

/**
 * @author picon.software
 */
public class CurrentFavoriteStationListFragment extends StationListFragment {

	public CurrentFavoriteStationListFragment() {
		sectionTitleId = R.string.station_current_favorite_title;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	@Override
	protected SimpleCursorAdapter adapteurInit() {
		return null;
	}

	@Override
	protected Loader<Cursor> getCursorLoader(int id, Bundle args) {

		return new CursorLoader(
				context,
				Station.CONTENT_URI,
				new String[]{Station._ID, Station.STATION_TYPE_ID, Station.NAME,
						"r." + Region.NAME + " AS " + Region.COLUMN_NAME_NAME_ALIAS, Station.FAVORITE},
				"s." + Station.FAVORITE + " = 1",
				null,
				null);
	}

	@Override
	protected void onLoadFinishedAdapteur(Cursor cursor,
										  SimpleCursorAdapter adapter) {

		refreshAdapteur(new SimpleCursorAdapter(
				context,
				R.layout.station_row_favorite,
				cursor,
				dataColumns,
				viewIDs, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));

		getAdapter().setViewBinder(new StationListViewBinder(null, false));
	}
}
