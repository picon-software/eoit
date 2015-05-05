/*
 * Copyright (C) 2013 Picon software
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
package fr.piconsoft.eoit.model;

import fr.piconsoft.eoit.model.ColumnsNames.BaseColumns;
import fr.piconsoft.eoit.model.ColumnsNames.BaseColumnsWithName;

/**
 * @author picon.software
 */
@SuppressWarnings("UnusedDeclaration")
public interface ApiColumnsNames {

	interface ApiKey extends BaseColumns {
		String TABLE_NAME = "api_key";
		String API_KEY_ALIAS = "key_id";
		String V_CODE = "v_code";
		String MASK = "mask";
	}

	interface Corporation extends BaseColumnsWithName {
		String TABLE_NAME = "corporation";
		String CORP_ID = "corp_id";
		String CORP_NAME = "corp_name";
		String KEY_ID = "key_id";
		String NAME_ALIAS = "corp_name";
	}

	interface Character extends BaseColumnsWithName {
		String TABLE_NAME = "character";
		String CHARACTER_ID = "character_id";
		String CHARACTER_NAME = "character_name";
		String KEY_ID = "key_id";
		String ACTIVE = "active";
		String CORP_ID = "corp_id";
	}

	interface Skill extends BaseColumns {
		String TABLE_NAME = "skill";
		String CHARACTER_ID = "character_id";
		String SKILL_ID = "skill_id";
		String LEVEL = "level";
	}
}
