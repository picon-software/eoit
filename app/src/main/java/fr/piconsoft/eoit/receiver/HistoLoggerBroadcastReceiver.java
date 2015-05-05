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

package fr.piconsoft.eoit.receiver;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.util.SparseIntArray;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import fr.piconsoft.eoit.model.Histo;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.provider.HistoContentProvider;
import fr.piconsoft.eoit.receiver.task.HistoLoaderAsyncTask;

/**
 * @author picon.software
 */
public class HistoLoggerBroadcastReceiver extends AbstractHistoBroadcastReceiver {

    private static final String LOG_TAG = "HistoLoggerBroadcastReceiver";

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat logSdf = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void onReceiveWithHisto(Context context, Intent intent, Map<String, SparseIntArray> histoMap) {
        int itemId = intent.getIntExtra(Item._ID, -1);

		incrementHistoForItemId(context, itemId, histoMap);
    }

	private void incrementHistoForItemId(Context context, int itemId, Map<String, SparseIntArray> histoMap) {
		String date = sdf.format(new Date());
		SparseIntArray subMap = HistoLoaderAsyncTask.getSubMap(histoMap, date);

		int number = subMap.get(itemId);
		number++;
		subMap.put(itemId, number);

		persistHisto(context, date, itemId, number);

		Log.d(LOG_TAG, "Logging item : " + itemId + " " + (number - 1) + "->" + number +
				" for date " + logSdf.format(new Date()));
	}

	private void persistHisto(final Context context, String date, int itemId, int number) {
		final ContentValues values = new ContentValues();
		values.put(Histo.DATE, date);
		values.put(Histo.ITEM_ID, itemId);
		values.put(Histo.NUMBER_OF_TIME, number);

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				context.getContentResolver().insert(HistoContentProvider.HISTO_URI, values);
				return null;
			}
		}.execute((Void) null);

	}
}
