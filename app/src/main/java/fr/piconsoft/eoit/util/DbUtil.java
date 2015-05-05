/*
 * Copyright (C) 2014 Picon software
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

package fr.piconsoft.eoit.util;

import android.database.Cursor;
import android.database.MatrixCursor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author picon.software
 *
 */
public final class DbUtil {

	private static final String TAG = DbUtil.class.getSimpleName();

	private DbUtil() { }

	public static void addRowToMatrixCursor(MatrixCursor cursor, Object... values) {
		cursor.addRow(values);
	}

	public static boolean hasAtLeastOneRow(final Cursor cursor) {
		if(cursor == null) return false;

		boolean result = cursor.getCount() > 0;
		cursor.moveToFirst();

		return result;
	}

    public static boolean getBoolean(final Cursor cursor, String fieldName) {
        return parseBoolean(cursor.getInt(cursor.getColumnIndexOrThrow(fieldName)));
    }

    public static boolean parseBoolean(int value) {
        return value == 1;
    }

	public static Date getDate(final Cursor cursor, String fieldName) {
		return getDate(cursor, cursor.getColumnIndexOrThrow(fieldName));
	}

	public static Date getDate(final Cursor cursor, int columnIndex) {
		String dateTime = cursor.getString(columnIndex);

		if(dateTime == null) {
			return new Date(0);
		}

		Date date = new Date(0);
		DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			date = iso8601Format.parse(dateTime);
		} catch (ParseException e) {
			//Log.e(TAG, "Parsing ISO8601 datetime failed", e);
		}

		return date;
	}

	public static String formatDate(Date date) {
		DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return formatDate(date, iso8601Format);
	}

	public static String formatDate(Date date, DateFormat iso8601Format) {
		if(date == null) {
			return "";
		}

		return iso8601Format.format(date);
	}
}
