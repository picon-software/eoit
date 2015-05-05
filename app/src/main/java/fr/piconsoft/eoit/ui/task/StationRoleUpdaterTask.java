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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.model.Station;

public class StationRoleUpdaterTask extends AsyncTask<Integer, Void, Void> {

    private Context context;
    private int stationId;

    public StationRoleUpdaterTask(Context context, int stationId) {
        this.context = context;
        this.stationId = stationId;
    }

    @Override
    protected Void doInBackground(Integer... role) {
        Uri updateStationUri = ContentUris.withAppendedId(Station.CONTENT_ID_URI_BASE, stationId);
        ContentValues values = new ContentValues();

        // Cleaning before saving values
        if (role[0] == EOITConst.Stations.PRODUCTION_ROLE) {
            values.putNull(Station.ROLE);
            context.getContentResolver().update(
                    Station.CONTENT_URI,
                    values,
                    Station.ROLE + " = " + EOITConst.Stations.PRODUCTION_ROLE,
                    null);

            values.put(Station.ROLE, EOITConst.Stations.TRADE_ROLE);
            context.getContentResolver().update(
                    Station.CONTENT_URI,
                    values,
                    Station.ROLE + " = " + EOITConst.Stations.BOTH_ROLES,
                    null);
        } else if (role[0] == EOITConst.Stations.TRADE_ROLE) {
            values.putNull(Station.ROLE);
            context.getContentResolver().update(
                    Station.CONTENT_URI,
                    values,
                    Station.ROLE + " = " + EOITConst.Stations.TRADE_ROLE,
                    null);

            values.put(Station.ROLE, EOITConst.Stations.PRODUCTION_ROLE);
            context.getContentResolver().update(
                    Station.CONTENT_URI,
                    values,
                    Station.ROLE + " = " + EOITConst.Stations.BOTH_ROLES,
                    null);
        } else if (role[0] == EOITConst.Stations.BOTH_ROLES) {
            values.putNull(Station.ROLE);
            context.getContentResolver().update(
                    Station.CONTENT_URI,
                    values,
                    null,
                    null);
        }

        values.put(Station.ROLE, role[0]);

        context.getContentResolver().update(
		        updateStationUri,
		        values,
		        null,
		        null);

        return null;
    }
}
