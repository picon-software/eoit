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
public final class Blueprint implements ColumnsNames.Blueprint {

	private Blueprint() { }

	public static final String CONTENT_TYPE_BLUEPRINT = "vnd.android.cursor.item/fr.piconsoft.eoit.blueprint";

    /**
     * Path part for the Notes URI
     */
    private static final String PATH_BLUEPRINT = "/blueprint";
    private static final String PATH_BLUEPRINT_INVENTION = PATH_BLUEPRINT + "/invention";
    private static final String PATH_ITEM_COPY = PATH_BLUEPRINT + "/copy/items";

	/**
	 * The content URI base for a single item. Callers must append a numeric
	 * item id to this Uri to retrieve a note
	 */
	public static final Uri CONTENT_ITEM_ID_URI_BASE = Uri.parse(EOITConst.SCHEME + ItemContentProvider.AUTHORITY + PATH_BLUEPRINT + Item.PATH_ITEM_ID);

	/**
	 * The content URI base for a single item. Callers must append a numeric
	 * item id to this Uri to retrieve a note
	 */
	public static final Uri CONTENT_ID_URI_BASE = Uri.parse(EOITConst.SCHEME + ItemContentProvider.AUTHORITY + PATH_BLUEPRINT + "/");
	public static final Uri CONTENT_ID_URI_INVENTION_BASE = Uri.parse(EOITConst.SCHEME + ItemContentProvider.AUTHORITY + PATH_BLUEPRINT_INVENTION + "/");
	public static final Uri CONTENT_ID_URI_BLUEPRINT_COPY_REQUIRED_ITEMS_BASE = Uri.parse(EOITConst.SCHEME + ItemContentProvider.AUTHORITY + PATH_ITEM_COPY + "/");

	/**
     * 0-relative position of a note ID segment in the path part of a note ID URI
     */
    public static final int ITEM_ID_PATH_POSITION = 2;

	/**
     * 0-relative position of a note ID segment in the path part of a note ID URI
     */
    public static final int BLUEPRINT_COPY_ID_PATH_POSITION = 3;

	/**
     * 0-relative position of a note ID segment in the path part of a note ID URI
     */
    public static final int BLUEPRINT_ID_PATH_POSITION = 1;

}
