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

package fr.piconsoft.eoit.activity.basic.util;

import android.database.Cursor;
import android.widget.SectionIndexer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.piconsoft.eoit.model.ColumnsNames;
import fr.piconsoft.eoit.util.DbUtil;

/**
 * @author picon.software
 *
 */
public class CategoryIndexer implements SectionIndexer {

	private Integer[] sectionPositions;
	private String[] sectionNames;

	public CategoryIndexer(Cursor cursor) {
		if(DbUtil.hasAtLeastOneRow(cursor)) {

			Set<String> sectionNamesSet = new HashSet<String>();
			List<Integer> sectionPostionList = new ArrayList<Integer>();
			List<String> sectionNamesList = new ArrayList<String>();

			while (!cursor.isAfterLast()) {
				String categoryName = cursor.getString(cursor.getColumnIndexOrThrow(ColumnsNames.Categories.COLUMN_NAME_NAME_ALIAS));
				int cursorPos = cursor.getPosition();

				if(sectionNamesSet.add(categoryName)) {
					sectionPostionList.add(cursorPos);
					sectionNamesList.add(categoryName.toUpperCase());
				}

				cursor.moveToNext();
			}

			sectionPositions = sectionPostionList.toArray(new Integer[sectionPostionList.size()]);
			sectionNames = sectionNamesList.toArray(new String[sectionNamesList.size()]);
			cursor.moveToFirst();
		}
	}

	/* (non-Javadoc)
	 * @see android.widget.SectionIndexer#getPositionForSection(int)
	 */
	@Override
	public int getPositionForSection(int section) {
		if(section >= sectionPositions.length) {
			return sectionPositions[sectionPositions.length - 1];
		}
		return sectionPositions[section];
	}

	/* (non-Javadoc)
	 * @see android.widget.SectionIndexer#getSectionForPosition(int)
	 */
	@Override
	public int getSectionForPosition(int position) {

		for(int i = 0; i < sectionPositions.length - 1; i++) {
			if(position >= sectionPositions[i] && position < sectionPositions[i+1]) {
				return i;
			}
		}

		if(position >= sectionPositions[sectionPositions.length - 1]) {
			return sectionPositions.length - 1;
		}

		return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.SectionIndexer#getSections()
	 */
	@Override
	public Object[] getSections() {
		return sectionNames;
	}

}
