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

package fr.piconsoft.eoit.helper;

import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;

import java.lang.ref.WeakReference;

import fr.eo.api.credential.CharacterCredential;
import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.model.ApiKey;
import fr.piconsoft.eoit.model.Character;
import fr.piconsoft.eoit.model.PreferencesName;
import fr.piconsoft.eoit.model.Skill;
import fr.piconsoft.eoit.model.Station;
import fr.piconsoft.eoit.ui.model.Skills;
import fr.piconsoft.eoit.ui.model.Stations;
import fr.piconsoft.eoit.util.CursorIteratorWrapper;
import fr.piconsoft.eoit.util.DbUtil;

/**
 * @author picon.software
 */
public final class ContextHelper {

	private static WeakReference<CharacterCredential> currentCredentialRef;

	private ContextHelper() {
	}

	public static void populateSkills(Context context) {

		if(!Skills.isLevelVChar() && Skills.isSkillMapEmpty()) {
			Cursor data = context.getContentResolver().query(
					Skill.SKILL_URI,
					new String[]{Skill.SKILL_ID, Skill.LEVEL},
					null, null, null);

			for(Cursor cur : new CursorIteratorWrapper(data)) {
				int skillId = cur.getInt(cur.getColumnIndexOrThrow(Skill.SKILL_ID));
				short level = cur.getShort(cur.getColumnIndexOrThrow(Skill.LEVEL));

				Skills.initSkill(skillId, level);
			}

			data.close();
		}
	}

	public static void populateStationBeans(Context context, boolean force) {

		if(force ||
				!Stations.getTradeStation().initialized ||
				!Stations.getProductionStation().initialized) {

			Stations.clear();

			Cursor cursor = context.getContentResolver().query(
					Station.CONTENT_URI,
					new String[]{Station._ID, Station.REGION_ID, Station.SOLAR_SYSTEM_ID,
							Station.ROLE, Station.NAME, Station.STANDING},
					Station.ROLE + " IS NOT NULL",
					null,
					null);

			try {
				parseStationCursor(cursor);
			} finally {
				cursor.close();
			}
		}
	}

	private static void parseStationCursor(Cursor data) {
		for (Cursor cursor : new CursorIteratorWrapper(data)) {
			int stationId = cursor.getInt(cursor.getColumnIndexOrThrow(Station._ID));
			int regionId = cursor.getInt(cursor.getColumnIndexOrThrow(Station.REGION_ID));
			int solarSystemId = cursor.getInt(cursor.getColumnIndexOrThrow(Station.SOLAR_SYSTEM_ID));
			int role = cursor.getInt(cursor.getColumnIndexOrThrow(Station.ROLE));
			String stationName = cursor.getString(cursor.getColumnIndexOrThrow(Station.NAME));
			float standing = cursor.getFloat(cursor.getColumnIndexOrThrow(Station.STANDING));

			switch (role) {
				case EOITConst.Stations.PRODUCTION_ROLE:
					Stations.initProdStation(regionId, solarSystemId, stationId, stationName, standing);
					break;
				case EOITConst.Stations.TRADE_ROLE:
					Stations.initTradeStation(regionId, solarSystemId, stationId, stationName, standing);
					break;
				case EOITConst.Stations.BOTH_ROLES:
					Stations.initProdStation(regionId, solarSystemId, stationId, stationName, standing);
					Stations.initTradeStation(regionId, solarSystemId, stationId, stationName, standing);
					break;

				default:
					break;
			}
		}
	}

	public static CharacterCredential getCurrentCredential(Context context) {

		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String characterIdStr = preferences.getString(PreferencesName.CHARACTER_ID, null);

		if (characterIdStr != null && !Character.LEVEL_5_PREF_VALUE.equals(characterIdStr)) {
			if (currentCredentialRef == null || currentCredentialRef.get() == null) {
				long characterId = Long.valueOf(characterIdStr);

				Uri uri = ContentUris.withAppendedId(Character.CHARACTER_URI, characterId);

				Cursor cursor = context.getContentResolver().query(uri, new String[]{Character.KEY_ID, ApiKey.V_CODE},
						null, null, null);

				if (DbUtil.hasAtLeastOneRow(cursor)) {
					long keyId = cursor.getLong(cursor.getColumnIndexOrThrow(Character.KEY_ID));
					String vCode = cursor.getString(cursor.getColumnIndexOrThrow(ApiKey.V_CODE));

					cursor.close();

					currentCredentialRef = new WeakReference<>(
							new CharacterCredential(keyId, vCode, characterId));
				}
			}

			return currentCredentialRef.get();
		}

		return CharacterCredential.LEVEL_5_CHAR;
	}
}
