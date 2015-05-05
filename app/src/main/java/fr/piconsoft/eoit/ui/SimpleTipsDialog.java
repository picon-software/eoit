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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

public class SimpleTipsDialog extends SimpleDialog {

	private int preference, threshold;
	private String preferenceName;

	public SimpleTipsDialog() {
		super();
	}

	public SimpleTipsDialog(int titleId, int layoutId,
							int positiveStringId, int neutralStringId, int negativeStringId,
							Context context, String preferenceName, int threshold) {
		super(titleId, layoutId, positiveStringId, neutralStringId, negativeStringId);
		this.preferenceName = preferenceName;
		this.threshold = threshold;
		SharedPreferences preferences =
				PreferenceManager.getDefaultSharedPreferences(context);
		preference = preferences.getInt(preferenceName, threshold);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putInt("preference", preference);
		outState.putInt("threshold", threshold);
		outState.putString("preferenceName", preferenceName);
	}

	@Override
	protected void onCreateSimpleDialog(View inflatedLayout, Bundle savedInstanceState) {

		if (savedInstanceState != null) {
			preference = savedInstanceState.getInt("preference");
			threshold = savedInstanceState.getInt("threshold");
			preferenceName = savedInstanceState.getString("preferenceName");
		}

	}

	public boolean isActive(Context context) {

		if (preference == 0) {
			return true;
		}

		if (preference == -1) {
			return false;
		}

		preference--;
		savePreference(context);
		return false;
	}

	private void savePreference(Context context) {
		SharedPreferences.Editor editor =
				PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putInt(preferenceName, preference);
		editor.apply();
	}

	protected void delayNextTipsDisplay() {
		preference = threshold;
		savePreference(context);
	}

	protected void hideTips() {
		preference = -1;
		savePreference(context);
	}

	protected void openActivity(Class<? extends BaseActivity> clazz, Bundle args) {
		final Intent intent = new Intent(context, clazz);

		if (args != null) {
			intent.putExtras(args);
		}

		if(getActivity() != null) {
			getActivity().startActivity(intent);
		}
	}

	protected void openActivity(Class<? extends BaseActivity> clazz) {
		openActivity(clazz, null);
	}
}
