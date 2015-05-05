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

package fr.piconsoft.eoit.ui.task;

import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

import javax.inject.Inject;

import fr.eo.api.credential.CharacterCredential;
import fr.eo.api.model.CharacterSheet;
import fr.eo.api.services.CharacterService;
import fr.piconsoft.eoit.model.Skill;
import fr.piconsoft.eoit.ui.model.Skills;
import fr.piconsoft.eoit.provider.EveOnlineApiContentProvider;

/**
 * @author picon.software
 */
public class SkillUpdateAsyncTask extends EveApiProcessWithCredentialAsyncTask {

	@Inject protected CharacterService characterService;

	public SkillUpdateAsyncTask(Dialog dialog) {
		super(dialog);
	}

	@Override
	protected void doInBackground(final CharacterCredential characterCredential) {
		CharacterSheet characterSheet = characterService.characterSheet(
				characterCredential.keyId,
				characterCredential.vCode,
				characterCredential.characterId);

		Map<Long, Short> skills = characterSheet.getSkills();

		ArrayList<ContentProviderOperation> operations = new ArrayList<>();

		operations.add(ContentProviderOperation.newDelete(Skill.SKILL_URI).build());

		Skills.clearSkillMap();

		for (Map.Entry<Long, Short> entry : skills.entrySet()) {
			ContentValues values = new ContentValues();

			int skillId = entry.getKey().intValue();
			short level = entry.getValue();
			Skills.initSkill(skillId, level);

			values.put(Skill.SKILL_ID, skillId);
			values.put(Skill.LEVEL, level);
			values.put(Skill.CHARACTER_ID, characterCredential.characterId);

			operations.add(ContentProviderOperation.newInsert(Skill.SKILL_URI).withValues(values).build());
		}

		try {
			context.getContentResolver().applyBatch(EveOnlineApiContentProvider.AUTHORITY, operations);
		} catch (RemoteException | OperationApplicationException e) {
			Log.e(SkillUpdateAsyncTask.class.getSimpleName(), e.getMessage(), e);
		}
	}
}
