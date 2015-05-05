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

package fr.piconsoft.eoit.db.updater.bean;

import fr.piconsoft.eoit.util.StringUtils;

/**
* @author picon.software
*/
public class CharacterState implements SerializableBean<CharacterState> {
	public long id;
	public String name;
	public long keyId;
	public long corpId;
	public boolean active;

	public CharacterState(long id, String name, long keyId, long corpId, boolean active) {
		this.id = id;
		this.name = name;
		this.keyId = keyId;
		this.corpId = corpId;
		this.active = active;
	}

	@Override
	public String serialize() {
		return SerializableUtils.serialize(id, name, keyId, corpId, active);
	}

	public static CharacterState unserialize(String serializedBean) {
		if (StringUtils.isBlank(serializedBean)) {
			return null;
		}

		String[] parts = serializedBean.split(SEPARATOR);

		if (parts.length != 3) {
			return null;
		}

		return new CharacterState(Long.parseLong(parts[0]), parts[1],
				Long.parseLong(parts[2]), Long.parseLong(parts[3]),
				Boolean.parseBoolean(parts[4]));
	}
}
