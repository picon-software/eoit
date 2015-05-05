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

/**
 *
 */
package fr.piconsoft.eoit.ui.task;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import fr.piconsoft.eoit.util.DecryptorUtil;
import fr.piconsoft.eoit.model.Blueprint;

public class UpdateDecryptorIdTask extends AsyncTask<Integer, Void, Integer> {

	private Context context;

	public UpdateDecryptorIdTask(Context context) {
		this.context = context;
	}

	@Override
	protected Integer doInBackground(Integer... params) {
		int blueprintId = params[0];
		int decryptorId = params[1];

		DecryptorUtil.DecryptorBonuses currentDecryptorBonuses = DecryptorUtil.getDecryptorBonusesOrDefault(decryptorId);

		Uri updateBlueprintUri = ContentUris.withAppendedId(Blueprint.CONTENT_ID_URI_BASE, blueprintId);
		ContentValues values = new ContentValues();
		values.put(Blueprint.DECRYPTOR_ID, decryptorId);
		values.put(Blueprint.ML, (2 + currentDecryptorBonuses.meModifier));
		values.put(Blueprint.PL, (4 + currentDecryptorBonuses.peModifier));

		context.getContentResolver().update(
				updateBlueprintUri,
				values,
				null,
				null);

		return decryptorId;
	}
}
