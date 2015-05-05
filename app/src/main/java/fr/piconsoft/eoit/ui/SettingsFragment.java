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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dagger.ObjectGraph;
import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.EOITApplication;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.task.SkillUpdateAsyncTask;
import fr.piconsoft.eoit.model.Character;
import fr.piconsoft.eoit.model.PreferencesName;

/**
 * @author picon.software
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

	private Context mContext;
	private ObjectGraph applicationGraph;
	private CharacterLoaderAsyncTask task;

	private static void setPreferenceSummary(Preference pref) {
		if (pref instanceof ListPreference) {
			pref.setSummary(((ListPreference) pref).getEntry());
		}
		if (pref instanceof EditTextPreference) {
			pref.setSummary(((EditTextPreference) pref).getText());
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mContext = activity.getApplicationContext();
		applicationGraph = ((EOITApplication) activity.getApplication()).getApplicationGraph();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	public void onResume() {
		super.onResume();

		task = new CharacterLoaderAsyncTask();
		task.execute((Void) null);

		SharedPreferences preferences = getPreferenceScreen().getSharedPreferences();

		preferences.registerOnSharedPreferenceChangeListener(this);

		setPreferenceSummary(findPreference(PreferencesName.CHARACTER_ID));
		setPreferenceSummary(findPreference(PreferencesName.ICON_SIZE));
		setPreferenceSummary(findPreference(PreferencesName.RENDER_SIZE));

		//set version
		String version = "";
		try {
			PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
			version = pInfo.versionName + " (" + pInfo.versionCode + ")";
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(SettingsFragment.class.getSimpleName(), e.getMessage());
		}
		findPreference(PreferencesName.ABOUT_VERSION).setSummary(version);
	}

	@Override
	public void onPause() {
		super.onPause();

		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		task.cancel(true);
	}

	@Override
	public void onDestroy() {
		applicationGraph = null;
		task = null;

		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		setPreferenceSummary(findPreference(key));

		if (PreferencesName.CHARACTER_ID.equals(key) &&
				!Character.LEVEL_5_PREF_VALUE.equals(sharedPreferences.getString(PreferencesName.CHARACTER_ID, Character.LEVEL_5_PREF_VALUE))) {
			updateSkillsForCurrentCharacter();
		}
	}

	public void updateSkillsForCurrentCharacter() {
		ProgressDialog waitDialog = new ProgressDialog(getActivity());
		waitDialog.setCancelable(true);
		waitDialog.setMessage(getString(R.string.refreshing_skill));
		waitDialog.show();

		SkillUpdateAsyncTask task = new SkillUpdateAsyncTask(waitDialog);
		applicationGraph.inject(task);
		task.execute();
	}

	private class CharacterLoaderAsyncTask extends AsyncTask<Void, Void, Cursor> {

		@Override
		protected Cursor doInBackground(Void... params) {

			return mContext.getContentResolver().query(
					Character.CHARACTER_URI,
					new String[]{Character._ID, Character.NAME},
					null, null, null);
		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(Cursor data) {
			ListPreference characters = (ListPreference) findPreference(PreferencesName.CHARACTER_ID);

			if (!isCancelled() && DbUtil.hasAtLeastOneRow(data)) {
				List<String> values = new ArrayList<>();
				List<String> keys = new ArrayList<>();

				keys.add("-1");
				values.add(getString(R.string.all_level_v));

				while (!data.isAfterLast()) {

					keys.add(data.getString(data.getColumnIndexOrThrow(Character._ID)));
					values.add(data.getString(data.getColumnIndexOrThrow(Character.NAME)));

					data.moveToNext();
				}

				data.close();

				characters.setEntries(values.toArray(new String[values.size()]));
				characters.setEntryValues(keys.toArray(new String[values.size()]));

				setPreferenceSummary(findPreference(PreferencesName.CHARACTER_ID));
				characters.setEnabled(true);
			}
		}
	}
}
