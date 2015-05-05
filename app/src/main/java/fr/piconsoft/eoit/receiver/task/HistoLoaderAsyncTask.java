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

package fr.piconsoft.eoit.receiver.task;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.SparseIntArray;

import java.util.HashMap;
import java.util.Map;

import fr.piconsoft.eoit.model.Histo;
import fr.piconsoft.eoit.provider.HistoContentProvider;
import fr.piconsoft.eoit.receiver.AbstractHistoBroadcastReceiver;
import fr.piconsoft.eoit.util.CursorIteratorWrapper;

/**
 * @author picon.software
 */
public class HistoLoaderAsyncTask extends AsyncTask<Void, Void, Map<String, SparseIntArray>> {

	private AbstractHistoBroadcastReceiver receiverInstance;

	public HistoLoaderAsyncTask(AbstractHistoBroadcastReceiver receiverInstance) {
		this.receiverInstance = receiverInstance;
	}

	@Override
	protected Map<String, SparseIntArray> doInBackground(Void... params) {
		Map<String, SparseIntArray> histoMap = new HashMap<>();

		Cursor cursor = receiverInstance.getContext().getContentResolver().query(
				HistoContentProvider.HISTO_URI, Histo.COLUMNS, null, null,
				null);

		for (Cursor data : new CursorIteratorWrapper(cursor)) {
			String date = data.getString(data.getColumnIndex(Histo.DATE));
			int itemId = data.getInt(data.getColumnIndex(Histo.ITEM_ID));
			int number = data.getInt(data.getColumnIndex(Histo.NUMBER_OF_TIME));

			SparseIntArray subMap = getSubMap(histoMap, date);
			subMap.put(itemId, number);
		}

		if(cursor != null) {
			cursor.close();
		}

		return histoMap;
	}

	@Override
	protected void onPostExecute(Map<String, SparseIntArray> histoMap) {
		receiverInstance.onReceiveWithHisto(
				receiverInstance.getContext(),
				receiverInstance.getCurrentIntent(),
				histoMap
		);
	}

	public static SparseIntArray getSubMap(Map<String, SparseIntArray> histoMap, String date) {
		SparseIntArray subMap = histoMap.get(date);
		if (subMap == null) {
			subMap = new SparseIntArray();
			histoMap.put(date, subMap);
		}

		return subMap;
	}
}
