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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author picon.software
 */
public class SqlRequestsDump implements Dumpable {

	private List<String> lines = new ArrayList<>();
	private Iterator<String> iterator;

	public void addLine(String line) {
		this.lines.add(line);
	}

	@Override
	public boolean hasNext() {
		if(iterator == null) {
			iterator = lines.iterator();
		}

		return iterator.hasNext();
	}

	@Override
	public String nextLine() {
		return iterator.next();
	}

	@Override
	public int lineCount() {
		return lines.size();
	}
}
