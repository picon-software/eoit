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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.SparseIntArray;

import java.util.Map;

/**
 * @author picon.software
 */
public abstract class AbstractHistoBroadcastReceiver extends BroadcastReceiver {

	private Context context;
	private Intent currentIntent;

	@Override
	public final void onReceive(Context context, Intent intent) {
		this.context = context;
		currentIntent = intent;

//		new HistoLoaderAsyncTask(this).execute();
	}

	public abstract void onReceiveWithHisto(Context context, Intent intent, Map<String, SparseIntArray> histoMap);

	public static int size(Map<String, SparseIntArray> histoMap) {
		if (histoMap == null) {
			return -1;
		}

		int count = 0;
		for (Map.Entry<String, SparseIntArray> entry : histoMap.entrySet()) {
			for (int i = 0; i < entry.getValue().size(); i++) {
				count++;
			}
		}

		return count;
	}

	public Context getContext() {
		return context;
	}

	public Intent getCurrentIntent() {
		return currentIntent;
	}
}
