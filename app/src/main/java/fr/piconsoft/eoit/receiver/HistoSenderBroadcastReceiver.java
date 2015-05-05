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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseIntArray;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.TreeMap;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.model.PreferencesName;
import fr.piconsoft.eoit.provider.HistoContentProvider;
import fr.piconsoft.eoit.ui.model.HistoBean;
import fr.piconsoft.eoit.util.RandomString;

import static fr.piconsoft.eoit.util.NetworkUtil.isNetworkAvailable;

/**
 * @author picon.software
 */
public class HistoSenderBroadcastReceiver extends AbstractHistoBroadcastReceiver {

	private static final String LOG_TAG = "HistoSender";
	private static final int UPDATE_THRESHOLD = 20;
	private static final int TIMEOUT_MILLISEC = 15000;

	@Override
	public void onReceiveWithHisto(Context context, Intent intent, Map<String, SparseIntArray> histoMap) {
		Log.v(LOG_TAG, "Send triggered.");

		if (isNetworkAvailable(context) && size(histoMap) >= UPDATE_THRESHOLD) {
			Log.v(LOG_TAG, "Starting sending json data...");

			new SenderAsyncTask(context, histoMap).execute((Void) null);
		} else if (!isNetworkAvailable(context)) {
			Log.v(LOG_TAG, "No internet connection !");
		} else if (size(histoMap) < UPDATE_THRESHOLD) {
			Log.v(LOG_TAG, "Only " + size(histoMap) + " items.");
		}
	}

	private class SenderAsyncTask extends AsyncTask<Void, Void, Void> {

		private Context context;
		private Map<String, SparseIntArray> histoMap;

		private SenderAsyncTask(Context context, Map<String, SparseIntArray> histoMap) {
			this.context = context;
			this.histoMap = histoMap;
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				sendJson(createJson(context, histoMap), context);

				Log.d(LOG_TAG, "done.");
				int nb = context.getContentResolver().delete(HistoContentProvider.HISTO_URI, null, null);
				Log.d(LOG_TAG, nb + " lines removed.");
			} catch (JSONException e) {
				Log.e(LOG_TAG, e.getLocalizedMessage());
			} catch (IOException e) {
				Log.e(LOG_TAG, "", e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			this.context = null;
			this.histoMap = null;
		}
	}

	private String createJson(Context context, Map<String, SparseIntArray> histoMap) throws JSONException {
		HistoBean bean = new HistoBean(getUserId(context), getHistoMap(histoMap));
		return bean.toJson();
	}

	private String getUserId(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String uid = preferences.getString(PreferencesName.UID, null);

		if (uid == null) {
			SharedPreferences.Editor editor = preferences.edit();

			uid = new RandomString(16).nextString();
			editor.putString(PreferencesName.UID, uid);

			editor.apply();
		}

		return uid;
	}

	private Map<String, Map<Integer, Integer>> getHistoMap(Map<String, SparseIntArray> histoMap) {
		Map<String, Map<Integer, Integer>> map = new TreeMap<>();

		for (Map.Entry<String, SparseIntArray> entry : histoMap.entrySet()) {
			map.put(entry.getKey(), new TreeMap<Integer, Integer>());
			for (int i = 0; i < entry.getValue().size(); i++) {
				map.get(entry.getKey()).put(entry.getValue().keyAt(i), entry.getValue().valueAt(i));
			}
		}

		return map;
	}

	private HttpResponse sendJson(String json, Context context) throws IOException {
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpClient client = new DefaultHttpClient(httpParams);

		URI uri = URI.create(context.getString(R.string.eoit_endpoint_us));
		HttpPost request = new HttpPost(uri);
		if (!request.containsHeader("Accept-Encoding")) {
			request.addHeader("Accept-Encoding", "gzip");
		}
		request.setEntity(new ByteArrayEntity(json.getBytes("UTF8")));

		return client.execute(request);
	}
}
