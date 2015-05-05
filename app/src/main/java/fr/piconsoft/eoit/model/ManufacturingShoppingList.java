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
public class ManufacturingShoppingList extends AbstractDbBean
		implements ColumnsNames.ManufacturingShoppingList {

	public static final String[] COLUMNS = new String[] {
			_ID,
			NAME,
			CHOSEN_PRICE_ID,
			TOT_NEEDED_QUANTITY,
	};

    public static final String PATH = "/" + TABLE_NAME;

	@Override
	public String[] getColumns() {
		return COLUMNS;
	}
}
