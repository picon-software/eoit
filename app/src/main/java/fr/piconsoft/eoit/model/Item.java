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
public final class Item implements ColumnsNames.Item {

	private Item() { }

	public static final String CONTENT_TYPE_ITEMS = "vnd.android.cursor.list/fr.piconsoft.eoit.item";
	public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/fr.piconsoft.eoit.item";

	/**
	 * Path part for the Notes URI
	 */
	public static final String PATH_ITEMS = "/" + TABLE_NAME;

	/**
	 * Path part for the Note ID URI
	 */
	public static final String PATH_ITEM_ID = PATH_ITEMS + "/";

	public static final String PATH_SEARCH = PATH_ITEMS + "/search";

	public static final String PATH_INVEST = PATH_ITEMS + "/invest";

	/**
	 * The content:// style URL for this table
	 */
	public static final Uri CONTENT_URI = Uri.parse(EOITConst.SCHEME + ItemContentProvider.AUTHORITY + PATH_ITEMS);

	/**
	 * The content URI base for a single item. Callers must append a numeric
	 * item id to this Uri to retrieve a note
	 */
	public static final Uri CONTENT_ID_URI_BASE = Uri.parse(EOITConst.SCHEME + ItemContentProvider.AUTHORITY + PATH_ITEM_ID);

	/**
	 * The content URI match pattern for a single note, specified by its ID.
	 * Use this to match incoming URIs or to construct an Intent.
	 */
	public static final Uri CONTENT_ID_URI_PATTERN = Uri.parse(EOITConst.SCHEME + ItemContentProvider.AUTHORITY + PATH_ITEM_ID + "/#");

	public static final Uri CONTENT_URI_SEARCH = Uri.parse(EOITConst.SCHEME + ItemContentProvider.AUTHORITY + PATH_SEARCH);

	public static final Uri CONTENT_ID_URI_INVEST = Uri.parse(EOITConst.SCHEME + ItemContentProvider.AUTHORITY + PATH_INVEST + "/#");

	/**
	 * 0-relative position of a note ID segment in the path part of a note ID URI
	 */
	public static final int ITEM_ID_PATH_POSITION = 1;
	public static final int ITEM_ID_PATH_POSITION_INVEST = 2;
}
