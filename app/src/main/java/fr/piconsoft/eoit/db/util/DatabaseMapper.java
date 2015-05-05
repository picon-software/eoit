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

import android.database.Cursor;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import fr.piconsoft.eoit.util.DbUtil;

import static fr.piconsoft.eoit.util.DbUtil.getDate;

/**
 * @author picon.software
 */
public class DatabaseMapper {

	private static final String LOG_TAG = DatabaseMapper.class.getSimpleName();

	public static <T> T create(Class<T> clazz, Cursor cursor) {
		try {
			T obj = clazz.newInstance();
			inject(obj, cursor);
			return obj;
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}

	public static void inject(Object obj, Cursor cursor) {
		for (Map.Entry<String, Field> column : extractColumnNames(obj).entrySet()) {
			int columnIndex = cursor.getColumnIndex(column.getKey());
			if (columnIndex != -1) {
				injectColumnValue(obj, column.getValue(), columnIndex, cursor);
			}
		}
	}

	private static Map<String, Field> extractColumnNames(Object obj) {
		Map<String, Field> columnNames = new HashMap<>();
		for (Field field : obj.getClass().getFields()) {
			columnNames.put(
					convertFieldNameIntoColumnName(field),
					field);
		}

		return columnNames;
	}

	private static String convertFieldNameIntoColumnName(Field field) {
		StringBuilder sb = new StringBuilder();

		if(field.isAnnotationPresent(ColumnName.class)) {
			return field.getAnnotation(ColumnName.class).value();
		}

		for (char c : field.getName().toCharArray()) {
			if(Character.isLetter(c) && Character.isUpperCase(c)) {
				sb.append('_').append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	private static void injectColumnValue(Object obj, Field field, int columnIndex, Cursor cursor) {
		String typeName = field.getType().getName();
		try {
			switch (typeName) {
				case "int":
					field.setInt(obj, cursor.getInt(columnIndex));
					break;

				case "long":
					field.setLong(obj, cursor.getLong(columnIndex));
					break;

				case "double":
					field.setDouble(obj, cursor.getDouble(columnIndex));
					break;

				case "float":
					field.setFloat(obj, cursor.getFloat(columnIndex));
					break;

				case "boolean":
					field.setBoolean(obj, DbUtil.parseBoolean(cursor.getInt(columnIndex)));
					break;

				case "java.lang.String":
					field.set(obj, cursor.getString(columnIndex));
					break;

				case "java.util.Date":
					field.set(obj, getDate(cursor, columnIndex));
					break;

				case "java.lang.Integer":
					field.set(obj, cursor.isNull(columnIndex) ? null : cursor.getInt(columnIndex));
					break;

				default:
					Log.e(LOG_TAG, "Type " + typeName + " is not supported!");
			}
		} catch (IllegalAccessException e) {
			Log.e(LOG_TAG, "Field " + field.getName() + " can't be accessed!", e);
		}
	}
}
