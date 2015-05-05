/*
 * Copyright (C) 2013 Picon software
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

import java.util.Iterator;

/**
 * @author picon.software
 */
public class CursorIteratorWrapper implements Iterable<Cursor> {

	private Cursor data;

	public CursorIteratorWrapper(Cursor data) {
		this.data = data;
	}

	@Override
	public Iterator<Cursor> iterator() {
		return new CursorIterator(data);
	}

	private class CursorIterator implements Iterator<Cursor> {

		private Cursor data;

		private CursorIterator(Cursor data) {
			this.data = data;
		}

		@Override
		public boolean hasNext() {
			return data != null && data.getCount() > 0 && !data.isLast();
		}

		@Override
		public Cursor next() {
			if(hasNext()) {
				data.moveToNext();
			}

			return data;
		}

		@Override
		public void remove() { }
	}
}
