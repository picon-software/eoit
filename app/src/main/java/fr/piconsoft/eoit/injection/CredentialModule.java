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

package fr.piconsoft.eoit.injection;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import fr.eo.api.credential.CharacterCredential;
import fr.piconsoft.eoit.model.ApiKey;
import fr.piconsoft.eoit.model.Character;
import fr.piconsoft.eoit.model.PreferencesName;
import fr.piconsoft.eoit.util.DbUtil;

/**
 * @author picon.software
 */
@Module(library = true, complete = false)
public class CredentialModule {

	@Provides
	@Singleton
	public CharacterCredential provideCurrentCredential(Context context) {

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String characterIdStr = preferences.getString(PreferencesName.CHARACTER_ID, null);

		if (characterIdStr != null && !Character.LEVEL_5_PREF_VALUE.equals(characterIdStr)) {
			long characterId = Long.valueOf(characterIdStr);

			Uri uri = ContentUris.withAppendedId(Character.CHARACTER_URI, characterId);

			Cursor cursor = context.getContentResolver().query(uri, new String[]{Character.KEY_ID, ApiKey.V_CODE},
					null, null, null);

			if (DbUtil.hasAtLeastOneRow(cursor)) {
				long keyId = cursor.getLong(cursor.getColumnIndexOrThrow(Character.KEY_ID));
				String vCode = cursor.getString(cursor.getColumnIndexOrThrow(ApiKey.V_CODE));

				cursor.close();

				return new CharacterCredential(keyId, vCode, characterId);
			}
		}

		return CharacterCredential.LEVEL_5_CHAR;
	}


}
