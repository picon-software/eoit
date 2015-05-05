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

package fr.piconsoft.eoit;

/**
 * @author picon.software
 *
 */
public abstract class BaseConst {

	/**
	 * The scheme part for this provider's URI
	 */
	public static final String SCHEME = "content://";

	public static final String VALUES_PATTERN = "###,###,###,##0.##";
	public static final String VALUES_WITHOUT_SEPARATOR_PATTERN = "#0.##";

    public static final String DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String HOUR_FORMAT = "HH:mm";

	// loader
	private static int LOADER_ID_SEQ = 0;

	public static int getNextLoaderIdSequence() {
		return ++LOADER_ID_SEQ;
	}
}
