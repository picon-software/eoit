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

import java.util.ArrayList;
import java.util.List;

import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.model.ColumnsNames.Region;
import fr.piconsoft.eoit.model.Station;
import fr.piconsoft.eoit.util.QueryBuilderUtil;
import fr.piconsoft.eoit.activity.basic.util.StationListViewBinder;

/**
 * @author picon.software
 */
public class MainHubsStationListFragment extends StationListFragment {

	public MainHubsStationListFragment() {
		sectionTitleId = R.string.station_main_hubs_title;
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

		List<Integer> ids = new ArrayList<Integer>();
		for (int station_id : EOITConst.Stations.MAIN_HUBS_STATION_IDS) {
			ids.add(station_id);
		}

		return new CursorLoader(
				context,
				Station.CONTENT_URI,
				new String[]{Station._ID, Station.STATION_TYPE_ID, Station.NAME,
						"r." + Region.NAME + " AS " + Region.COLUMN_NAME_NAME_ALIAS, Station.FAVORITE},
				QueryBuilderUtil.buildInClause("s." + Station._ID, ids),
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
