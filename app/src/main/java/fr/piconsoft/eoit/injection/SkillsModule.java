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

import android.app.Application;
import android.database.Cursor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import fr.eo.api.credential.CharacterCredential;
import fr.piconsoft.eoit.injection.bean.Skills;
import fr.piconsoft.eoit.model.Skill;
import fr.piconsoft.eoit.util.CursorIteratorWrapper;
import fr.piconsoft.eoit.formula.FormulaCalculator;

/**
 * @author picon.software
 */
@Module(complete = false, library = true,
		injects = {FormulaCalculator.class})
public class SkillsModule {

	private final Application application;

	public SkillsModule(Application application) {
		this.application = application;
	}

	@Provides
	@Singleton
	public Skills provideSkills(CharacterCredential credential) {

		Skills skills = new Skills(credential);

		if (!credential.isLevel5Char()) {
			Cursor data = application.getContentResolver().query(
					Skill.SKILL_URI,
					new String[]{Skill.SKILL_ID, Skill.LEVEL},
					null, null, null);

			for (Cursor cur : new CursorIteratorWrapper(data)) {
				int skillId = cur.getInt(cur.getColumnIndexOrThrow(Skill.SKILL_ID));
				short level = cur.getShort(cur.getColumnIndexOrThrow(Skill.LEVEL));

				skills.initSkill(skillId, level);
			}

			data.close();
		}


		return skills;
	}


}
