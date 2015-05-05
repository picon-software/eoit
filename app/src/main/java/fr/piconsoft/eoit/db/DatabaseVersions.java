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

package fr.piconsoft.eoit.db;

import fr.piconsoft.eoit.R;

/**
 * @author picon.software
 */
public enum DatabaseVersions {
	V_2_4(77),
	V_2_5(80),
	V_2_9_0(99),
	V_3_1_7(190),
	V_3_2_0(195),
	V_3_3_0(201),
	V_3_3_2(206, V_3_3_0.version, R.raw.dump_structure_bdd_views),
	V_3_3_7(211),
	V_3_3_8(221),
	V_3_3_9(222),
	V_3_3_10(223),
	V_3_3_11(224),
	V_3_4_0_MOSAIC(225);

	private final static int NO_DIFF = -42;

	public int version;
	public int diffFromVersion = NO_DIFF;
	public int[] diffResId;

	DatabaseVersions(int version) {
		this.version = version;
	}

	DatabaseVersions(int version, int diffFromVersion, int... diffResId) {
		this.version = version;
		this.diffFromVersion = diffFromVersion;
		this.diffResId = diffResId;
	}

	boolean diffCanBeApplied(int version) {
		return diffFromVersion != NO_DIFF && version >= diffFromVersion;
	}

	static DatabaseVersions current() {
		return V_3_4_0_MOSAIC;
	}
}
