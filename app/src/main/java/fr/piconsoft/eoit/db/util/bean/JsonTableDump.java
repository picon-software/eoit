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

package fr.piconsoft.eoit.db.util.bean;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author picon.software
 */
public class JsonTableDump implements Dumpable {

	public String name;
	public List<List<Object>> insert = new ArrayList<>();
	public int columns = 0;

	transient Iterator<List<Object>> iterator;

	@Override
	public boolean hasNext() {
		if (iterator == null) {
			iterator = insert.iterator();
		}

		return this.iterator.hasNext();
	}

	@Override
	public String nextLine() {
		List<Object> columnsList = new ArrayList<>(iterator.next());
		for (int i = columnsList.size(); i < columns; i++) {
			columnsList.add(null);
		}

		return formatLine(columnsList);
	}

	@Override
	public int lineCount() {
		return insert.size();
	}

	private String formatLine(List<Object> columnsList) {
		return "INSERT INTO " + name + " VALUES(" + join(columnsList) + ");";
	}

	private String join(List<Object> columnsList) {
		return FluentIterable.from(columnsList).transform(new Function<Object, String>() {
			@Override
			public String apply(Object o) {
				return convert(o);
			}
		}).join(Joiner.on(','));
	}

	private String convert(Object column) {
		if (column == null) {
			return "NULL";
		}
		if (column instanceof String) {
			return "'" + column + "'";
		} else {
			String converted = String.valueOf(column);

			if (converted.endsWith(".0")) {
				converted = converted.substring(0, converted.length() - 2);
			}

			return converted;
		}
	}
}
