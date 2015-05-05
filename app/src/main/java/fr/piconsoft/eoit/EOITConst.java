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

package fr.piconsoft.eoit;

/**
 * @author picon.software
 */
public abstract class EOITConst extends Const {
	public static final long APPLICATION_SIZE = 25 * 1024 * 1024;

	public static final String HISTO_LOGGER_BR_ACTION = "fr.piconsoft.eoit.HistoLoggerBroadcastReceiver";

	public static final int[] skillIconsResourceIds = new int[]{
			R.drawable.level0,
			R.drawable.level1,
			R.drawable.level2,
			R.drawable.level3,
			R.drawable.level4,
			R.drawable.level5
	};

	public static final int[] requiredSkillIconsResourceIds = new int[]{
			0,
			R.drawable.level1_required,
			R.drawable.level2_required,
			R.drawable.level3_required,
			R.drawable.level4_required,
			R.drawable.level5_required
	};
}
