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
import fr.piconsoft.eoit.provider.EveOnlineApiContentProvider;

/**
 * @author picon.software
 */
public final class Character implements ColumnsNames.Character {

	private Character() { }

	public static final String LEVEL_5_PREF_VALUE = "-1";

	public static final String PATH_CHARACTER = "/" + TABLE_NAME;

	public static final Uri CHARACTER_URI = Uri.parse(EOITConst.SCHEME + EveOnlineApiContentProvider.AUTHORITY + PATH_CHARACTER);

	public static final int CHARCTER_ID_PATH_POSITION = 1;

}
