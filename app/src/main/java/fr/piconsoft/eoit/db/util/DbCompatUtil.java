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
package fr.piconsoft.eoit.db.util;

import android.database.sqlite.SQLiteQueryBuilder;

/**
 * @author picon.software
 *
 */
public final class DbCompatUtil {

	private DbCompatUtil() {
	}

	@SuppressWarnings("deprecation")
	public static String buildQuery(SQLiteQueryBuilder qb, String[] projectionIn, String selection,
			String[] selectionArgs, String groupBy, String having, String sortOrder) {

		return qb.buildQuery(projectionIn, selection, selectionArgs, groupBy,
				having, sortOrder, null);
	}

}
