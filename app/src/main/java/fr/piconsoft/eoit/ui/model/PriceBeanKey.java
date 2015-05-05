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

package fr.piconsoft.eoit.ui.model;

/**
 * @author picon.software
 */
public class PriceBeanKey {

	public int itemId, solarSystemId;

	public PriceBeanKey(int itemId, int solarSystemId) {
		this.itemId = itemId;
		this.solarSystemId = solarSystemId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PriceBeanKey that = (PriceBeanKey) o;

		return itemId == that.itemId && solarSystemId == that.solarSystemId;

	}

	@Override
	public int hashCode() {
		int result = itemId;
		result = 31 * result + solarSystemId;
		return result;
	}
}