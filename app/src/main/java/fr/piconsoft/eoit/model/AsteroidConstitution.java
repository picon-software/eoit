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

import android.net.Uri;

import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.provider.ItemContentProvider;

/**
 * @author picon.software
 *
 */
public class AsteroidConstitution implements ColumnsNames.AsteroidConstitution {

	public static final String[] COLUMNS = new String[] {
			ColumnsNames.AsteroidConstitution._ID,
			ColumnsNames.AsteroidConstitution.NAME,
			ColumnsNames.AsteroidConstitution.VOLUME,
			ColumnsNames.AsteroidConstitution.PORTION_SIZE,
			ColumnsNames.AsteroidConstitution.GROUP_ID,
			ColumnsNames.AsteroidConstitution.TRITANIUM_QUANTITY,
			ColumnsNames.AsteroidConstitution.PYERITE_QUANTITY,
			ColumnsNames.AsteroidConstitution.MEXALLON_QUANTITY,
			ColumnsNames.AsteroidConstitution.ISOGEN_QUANTITY,
			ColumnsNames.AsteroidConstitution.NOCXIUM_QUANTITY,
			ColumnsNames.AsteroidConstitution.ZYDRINE_QUANTITY,
			ColumnsNames.AsteroidConstitution.MEGACYTE_QUANTITY,
			ColumnsNames.AsteroidConstitution.MORPHITE_QUANTITY
	};

	/**
	 * Path part for the Notes URI
	 */
	public static final String PATH = "/" + TABLE_NAME;

	/**
	 * The content:// style URL for this table
	 */
	public static final Uri CONTENT_URI = Uri.parse(EOITConst.SCHEME + ItemContentProvider.AUTHORITY + PATH);
}
