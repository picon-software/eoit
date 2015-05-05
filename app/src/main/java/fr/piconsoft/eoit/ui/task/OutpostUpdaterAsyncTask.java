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
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import fr.eo.api.manager.Manager;
import fr.eo.api.model.ConquerableStationList;
import fr.eo.api.services.EveService;
import fr.piconsoft.eoit.model.Station;
import fr.piconsoft.eoit.db.DatabaseHelper;

/**
 * @author picon.software
 */
public class OutpostUpdaterAsyncTask extends EveApiProcessAsyncTask {

	public OutpostUpdaterAsyncTask(Dialog dialog) {
		super(dialog);
	}

	@Override
	protected Void doInBackground(Void param) {

		EveService eveService = new Manager().eveService();

		ConquerableStationList stationList = eveService.conquerableStationList();

		SQLiteDatabase db = DatabaseHelper.getInstance(context).getWritableDatabase();

		DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db, Station.TABLE_NAME);
		final int idColumnId = ih.getColumnIndex(Station._ID);
		final int outpostColumnId = ih.getColumnIndex(Station.OUTPOST);
		final int nameColumnId = ih.getColumnIndex(Station.NAME);
		final int corpIdColumnId = ih.getColumnIndex(Station.CORPORATION_ID);
		final int solarSystemIdColumnId = ih.getColumnIndex(Station.SOLAR_SYSTEM_ID);
		final int stationTypeIdColumnId = ih.getColumnIndex(Station.STATION_TYPE_ID);

		try {
			if (stationList != null && stationList.getStations() != null) {
				for (ConquerableStationList.Station station : stationList.getStations()) {
					if(!isCancelled()) {
						ih.prepareForReplace();
						ih.bind(idColumnId, station.stationID);
						ih.bind(outpostColumnId, Boolean.TRUE);
						ih.bind(nameColumnId, station.stationName);
						ih.bind(corpIdColumnId, station.corporationID);
						ih.bind(solarSystemIdColumnId, station.solarSystemID);
						ih.bind(stationTypeIdColumnId, station.stationTypeID);
						ih.execute();
					}
				}
			}
		} finally {
			ih.close();
			db.close();
		}

		return null;
	}
}
