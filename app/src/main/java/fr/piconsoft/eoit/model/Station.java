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
import fr.piconsoft.eoit.provider.LocationContentProvider;

/**
 * @author picon.software
 *
 */
public final class Station implements ColumnsNames.Station {

	private Station() { }

	public static final String CONTENT_TYPE_PARAMETERS = "vnd.android.cursor.list/fr.piconsoft.eoit.stations";
	public static final String CONTENT_TYPE_PARAMETER = "vnd.android.cursor.item/fr.piconsoft.eoit.stations";

	public static final String PATH_STATIONS = "/" + TABLE_NAME;

	public static final String PATH_STATIONS_ID = PATH_STATIONS + "/";
	public static final String PATH_STATIONS_ALL_ID = PATH_STATIONS + "/all";

	public static final Uri CONTENT_URI = Uri.parse(EOITConst.SCHEME + LocationContentProvider.AUTHORITY + PATH_STATIONS);

	public static final Uri CONTENT_ID_URI_BASE = Uri.parse(EOITConst.SCHEME + LocationContentProvider.AUTHORITY + PATH_STATIONS_ID);

	public static final Uri CONTENT_ALL_URI = Uri.parse(EOITConst.SCHEME + LocationContentProvider.AUTHORITY + PATH_STATIONS_ALL_ID);

	public static final Uri CONTENT_ID_FAVORITE_STATIONS_PRICES_URI_BASE =
			Uri.parse(EOITConst.SCHEME + LocationContentProvider.AUTHORITY + PATH_STATIONS + Prices.PATH_PRICES + Item.PATH_ITEMS + "/");

    public static final int STATIONS_ID_PATH_POSITION = 1;
    public static final int ITEM_ID_PATH_POSITION = 3;
}
