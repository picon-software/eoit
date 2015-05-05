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

package fr.piconsoft.eoit.injection;

import android.app.Application;
import android.database.Cursor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.injection.bean.Stations;
import fr.piconsoft.eoit.model.Station;
import fr.piconsoft.eoit.util.CursorIteratorWrapper;

/**
 * @author picon.software
 */
@Module(library = true)
public class StationsModule {

	private final Application application;

	public StationsModule(Application application) {
		this.application = application;
	}

	@Provides
	@Singleton
	public Stations provideStations() {

		Stations stations = new Stations();

		Cursor data = application.getContentResolver().query(
				Station.CONTENT_URI,
				new String[]{Station._ID, Station.REGION_ID, Station.SOLAR_SYSTEM_ID,
						Station.ROLE, Station.NAME, Station.STANDING},
				Station.ROLE + " IS NOT NULL",
				null,
				null);

		for (Cursor cursor : new CursorIteratorWrapper(data)) {
			int stationId = cursor.getInt(cursor.getColumnIndexOrThrow(Station._ID));
			int regionId = cursor.getInt(cursor.getColumnIndexOrThrow(Station.REGION_ID));
			int solarSystemId = cursor.getInt(cursor.getColumnIndexOrThrow(Station.SOLAR_SYSTEM_ID));
			int role = cursor.getInt(cursor.getColumnIndexOrThrow(Station.ROLE));
			String stationName = cursor.getString(cursor.getColumnIndexOrThrow(Station.NAME));
			float standing = cursor.getFloat(cursor.getColumnIndexOrThrow(Station.STANDING));

			switch (role) {
				case EOITConst.Stations.PRODUCTION_ROLE:
					stations.initProdStation(regionId, solarSystemId, stationId, stationName, standing);
					break;
				case EOITConst.Stations.TRADE_ROLE:
					stations.initTradeStation(regionId, solarSystemId, stationId, stationName, standing);
					break;
				case EOITConst.Stations.BOTH_ROLES:
					stations.initProdStation(regionId, solarSystemId, stationId, stationName, standing);
					stations.initTradeStation(regionId, solarSystemId, stationId, stationName, standing);
					break;

				default:
					break;
			}
		}


		return stations;
	}


}
