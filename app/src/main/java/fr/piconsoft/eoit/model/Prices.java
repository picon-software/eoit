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
import fr.piconsoft.eoit.provider.PriceContentProvider;

/**
 * @author picon.software
 *
 */
public final class Prices implements ColumnsNames.Prices {

	private Prices() { }

	public static final String CONTENT_TYPE_PRICES = "vnd.android.cursor.item/fr.piconsoft.eoit.prices";

    /**
     * Path part for the Notes URI
     */
	public static final String PATH_PRICES = "/" + TABLE_NAME;

	/**
	 * The content URI base for a single item. Callers must append a numeric
	 * item id to this Uri to retrieve a note
	 */
	public static final Uri CONTENT_URI_BASE = Uri.parse(EOITConst.SCHEME + PriceContentProvider.AUTHORITY + PATH_PRICES);

	/**
	 * The content URI base for a single item. Callers must append a numeric
	 * item id to this Uri to retrieve a note
	 */
	public static final Uri CONTENT_ITEM_ID_URI_BASE = Uri.parse(EOITConst.SCHEME + PriceContentProvider.AUTHORITY + PATH_PRICES + Item.PATH_ITEM_ID);

	/**
     * 0-relative position of a note ID segment in the path part of a note ID URI
     */
    public static final int ITEM_ID_PATH_POSITION = 2;
    public static final int SOLAR_SYSTEM_ID_PATH_POSITION = 3;
}
