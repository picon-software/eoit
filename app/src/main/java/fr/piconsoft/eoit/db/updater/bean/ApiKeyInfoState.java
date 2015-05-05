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
public class ApiKeyInfoState implements SerializableBean<ApiKeyInfoState> {

	public long keyId;
	public String vCode, mask;

	public ApiKeyInfoState(long keyId, String vCode, String mask) {
		this.keyId = keyId;
		this.vCode = vCode;
		this.mask = mask;
	}

	@Override
	public String serialize() {
		return SerializableUtils.serialize(keyId, vCode, mask);
	}

	public static ApiKeyInfoState unserialize(String serializedBean) {
		if (StringUtils.isBlank(serializedBean)) {
			return null;
		}

		String[] parts = serializedBean.split(SEPARATOR);

		if (parts.length != 3) {
			return null;
		}

		return new ApiKeyInfoState(Long.parseLong(parts[0]), parts[1], parts[2]);
	}
}
