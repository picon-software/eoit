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

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.model.ColumnsNames.Region;
import fr.piconsoft.eoit.model.Station;

/**
 * @author picon.software
 *
 */
public abstract class StationListFragment extends ItemListFragment<SimpleCursorAdapter> {

	protected String[] dataColumns = { Station.STATION_TYPE_ID,
			Station.NAME, Region.COLUMN_NAME_NAME_ALIAS, Station.FAVORITE};
	protected int[] viewIDs = { R.id.station_icon, R.id.station_name, R.id.location_name, R.id.favorite_station };

	public StationListFragment() {
		sectionTitleId = -1;
		layoutId = R.layout.station_list;
		itemclickable = false;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		initOrRestart();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) { }

	@Override
	protected SimpleCursorAdapter adapteurInit() {
		 return null;
	}
}
