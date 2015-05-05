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
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import fr.piconsoft.eoit.R;

public class ApiUpgradeTips extends SimpleTipsDialog {

	long keyId;
	String vCode;

	public ApiUpgradeTips() {
		super();
	}

	public ApiUpgradeTips(Context context) {
		super(R.string.api_upgrade_title, R.layout.api_upgrade_dialog,
				android.R.string.ok,
				-1, -1,
				context, "api_upgrade", 0);
	}

	@Override
	public boolean isActive(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

		if (preferences.contains("user_id") && preferences.contains("api_key")) {
			keyId = preferences.getLong("user_id", -1);
			vCode = preferences.getString("api_key", null);

			SharedPreferences.Editor editor = preferences.edit();
			editor.remove("user_id");
			editor.remove("api_key");
			editor.apply();

			return true;
		}

		return false;
	}

	@Override
	protected void onPositive(DialogInterface dialog, int whichButton) {
		hideTips();

		Bundle args = new Bundle();
		args.putLong("keyId", keyId);
		args.putString("vCode", vCode);

		openActivity(ApiKeySaverActivity.class, args);
	}
}
