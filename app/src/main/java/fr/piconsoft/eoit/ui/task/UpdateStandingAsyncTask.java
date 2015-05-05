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

import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.Map;

import fr.eo.api.credential.CharacterCredential;
import fr.eo.api.manager.Manager;
import fr.eo.api.model.Standings;
import fr.eo.api.services.CharacterService;
import fr.piconsoft.eoit.model.Station;
import fr.piconsoft.eoit.provider.LocationContentProvider;

public class UpdateStandingAsyncTask extends EveApiProcessWithCredentialAsyncTask {

	public UpdateStandingAsyncTask(Dialog dialog) {
		super(dialog);
	}

	@Override
	protected void doInBackground(CharacterCredential characterCredential) {
		CharacterService characterService = new Manager().characterService();
		Standings standings = characterService.standings(characterCredential.keyId, characterCredential.vCode,
				characterCredential.characterId);

		ArrayList<ContentProviderOperation> operations = new ArrayList<>();
		for (Map.Entry<Long, Float> standingEntry : standings.getStandings().entrySet()) {
			long corpId = standingEntry.getKey();
			float standing = standingEntry.getValue();

			ContentValues values = new ContentValues();
			values.put(Station.STANDING, standing);
			operations.add(ContentProviderOperation.newUpdate(Station.CONTENT_URI).withValues(values).withSelection(Station.CORPORATION_ID + "=" + corpId, null).build());
		}

		try {
			context.getContentResolver().applyBatch(LocationContentProvider.AUTHORITY, operations);
		} catch (RemoteException | OperationApplicationException e) {
			Crashlytics.logException(e);
		}
	}
}
