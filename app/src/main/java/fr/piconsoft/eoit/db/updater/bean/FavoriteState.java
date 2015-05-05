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
public class FavoriteState implements SerializableBean<FavoriteState> {
	public int itemId;

	private FavoriteState() { }

	public FavoriteState(int itemId) {
		this.itemId = itemId;
	}

	@Override
	public String toString() {
		return String.valueOf(itemId);
	}

	@Override
	public String serialize() {
		return String.valueOf(itemId);
	}

	public static FavoriteState unserialize(String serializedBean) {
		if(StringUtils.isBlank(serializedBean)) {
			return null;
		}

		FavoriteState state = new FavoriteState();
		state.itemId = Integer.parseInt(serializedBean);
		return state;
	}
}
