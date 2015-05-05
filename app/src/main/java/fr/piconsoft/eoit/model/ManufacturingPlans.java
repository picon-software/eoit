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

package fr.piconsoft.eoit.model;

/**
 * @author picon.software
 */
public class ManufacturingPlans extends AbstractDbBean
		implements ColumnsNames.ManufacturingPlans {

	public static final String[] COLUMNS = new String[] {
			ColumnsNames.ManufacturingPlans._ID,
			ColumnsNames.ManufacturingPlans.ITEM_ID,
			ColumnsNames.ManufacturingPlans.NAME,
			ColumnsNames.ManufacturingPlans.CHOSEN_PRICE_ID,
			ColumnsNames.ManufacturingPlans.TOT_REQUIRED_QUANTITY,
			ColumnsNames.ManufacturingPlans.TOT_NEEDED_QUANTITY,
			ColumnsNames.ManufacturingPlans.REMAINING_QUANTITY,
	};

	/**
	 * Path part for the Notes URI
	 */
	public static final String[] PATHS = {
			"/" + TABLE_NAMES[0],
			"/" + TABLE_NAMES[1],
			"/" + TABLE_NAMES[2],
			"/" + TABLE_NAMES[3],
			"/" + TABLE_NAMES[4],
	};

	@Override
	public String[] getColumns() {
		return COLUMNS;
	}
}
