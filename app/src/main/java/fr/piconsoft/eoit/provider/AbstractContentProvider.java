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

package fr.piconsoft.eoit.provider;

import android.content.ContentProvider;
import android.content.UriMatcher;

import java.util.Map;

import fr.piconsoft.eoit.db.DatabaseHelper;

/**
 * @author picon.software
 */
public abstract class AbstractContentProvider extends ContentProvider {

	protected final static String PARAM_PATH_FRAGMENT = "/#";

	/**
	 * A UriMatcher instance
	 */
	protected static UriMatcher sUriMatcher;

	// Handle to a new DatabaseHelper.
	protected static DatabaseHelper mOpenHelper;

	static {
		/*
		 * Creates and initializes the URI matcher
		 */
		// Create a new instance
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	}

	protected static void addProjectionMapping(Map<String, String> map, String prefix, String... columnNames) {
		for (String columnName : columnNames) {
			map.put(columnName, prefix + columnName);
		}
	}

	/**
	 *
	 * Initializes the provider by creating a new DatabaseHelper. onCreate() is
	 * called automatically when Android creates the provider in response to a
	 * resolver request from a client.
	 */
	@Override
	public boolean onCreate() {
		// Creates a new helper object. Note that the database itself isn't
		// opened until
		// something tries to access it, and it's only created if it doesn't
		// already exist.
		mOpenHelper = DatabaseHelper.getInstance(getContext());

		// Assumes that any failures will be reported by a thrown exception.
		return true;
	}
}
