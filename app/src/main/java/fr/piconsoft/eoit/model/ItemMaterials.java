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
public final class ItemMaterials implements ColumnsNames.ItemMaterials {

	private ItemMaterials() { }

	public static final String CONTENT_TYPE_ITEM_MATERIALS = "vnd.android.cursor.item/fr.piconsoft.eoit.itemmaterials";
	public static final String CONTENT_TYPE_ITEMS_MATERIALS = "vnd.android.cursor.list/fr.piconsoft.eoit.itemmaterials";

    private static final String PATH_MATERIALS = "/materials";

    private static final String PATH_REFINE = "/refine";

    private static final String PATH_REACTION = "/reaction";

    private static final String PATH_PLANETARY = "/planetary";

	public static final Uri CONTENT_ITEM_ID_URI_BASE = Uri.parse(EOITConst.SCHEME + ItemContentProvider.AUTHORITY + PATH_MATERIALS + Item.PATH_ITEM_ID);

	public static final Uri CONTENT_ITEM_ID_URI_BASE_PRICES = Uri.parse(EOITConst.SCHEME + ItemContentProvider.AUTHORITY + PATH_MATERIALS + Prices.PATH_PRICES + Item.PATH_ITEM_ID );

	public static final Uri CONTENT_ITEM_ID_URI_BASE_PRICES_FOR_REFINE = Uri.parse(EOITConst.SCHEME + ItemContentProvider.AUTHORITY + PATH_REFINE + PATH_MATERIALS + Prices.PATH_PRICES + Item.PATH_ITEM_ID );

	public static final Uri CONTENT_ITEM_ID_URI_BASE_PRICES_FOR_REACTION = Uri.parse(EOITConst.SCHEME + ItemContentProvider.AUTHORITY + PATH_REACTION + PATH_MATERIALS + Prices.PATH_PRICES + Item.PATH_ITEM_ID );

	public static final Uri CONTENT_ITEM_ID_URI_BASE_PRICES_FOR_PLANETARY = Uri.parse(EOITConst.SCHEME + ItemContentProvider.AUTHORITY + PATH_PLANETARY + PATH_MATERIALS + Prices.PATH_PRICES + Item.PATH_ITEM_ID );

    public static final int ITEM_ID_PATH_POSITION = 2;

    public static final int ITEM_ID_PRICES_PATH_POSITION = 3;

    public static final int ITEM_ID_REFINE_PRICES_PATH_POSITION = 4;

	public static final int ITEM_ID_REACTION_PRICES_PATH_POSITION = 4;

	public static final int ITEM_ID_PLANETARY_PRICES_PATH_POSITION = 4;
}
