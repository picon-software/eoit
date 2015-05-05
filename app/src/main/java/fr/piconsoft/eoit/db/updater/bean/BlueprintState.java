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
public class BlueprintState implements SerializableBean<BlueprintState> {
	public int id, ml, pl, decryptorId, metalevel;
	public double researchPrice;

	private BlueprintState() { }

	public BlueprintState(int id, int ml, int pl, int decryptorId, int metalevel, double researchPrice) {
		this.id = id;
		this.ml = ml;
		this.pl = pl;
		this.decryptorId = decryptorId;
		this.metalevel = metalevel;
		this.researchPrice = researchPrice;
	}

	@Override
	public String serialize() {
		return "" + id + SEPARATOR +
				ml + SEPARATOR +
				pl + SEPARATOR +
				decryptorId + SEPARATOR +
				metalevel;
	}

	public static BlueprintState unserialize(String serializedBean) {
		if(StringUtils.isBlank(serializedBean)) {
			return null;
		}

		String[] parts = serializedBean.split(SEPARATOR);

		if(parts.length != 5) {
			return null;
		}

		BlueprintState state = new BlueprintState();
		state.id = Integer.parseInt(parts[0]);
		state.ml = Integer.parseInt(parts[1]);
		state.pl = Integer.parseInt(parts[2]);
		state.decryptorId = Integer.parseInt(parts[3]);
		state.metalevel = Integer.parseInt(parts[4]);
		return state;
	}
}
