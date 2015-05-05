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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.model.Prices;
import fr.piconsoft.eoit.model.Station;
import fr.piconsoft.eoit.util.DbUtil;

/**
 * @author picon.software
 */
public class PriceContentProvider extends AbstractContentProvider {

	public static final String AUTHORITY = "fr.piconsoft.eoit.provider.PriceContentProvider";

	private static final int PRICE_ITEM_ID_WITH_SOLAR_SYSTEM_ID = 0;
	private static final int PRICE_ITEM_ID = 1;

	static {
		sUriMatcher.addURI(AUTHORITY, "prices/item/#/#", PRICE_ITEM_ID_WITH_SOLAR_SYSTEM_ID);
		sUriMatcher.addURI(AUTHORITY, "prices/item/#", PRICE_ITEM_ID);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.content.ContentProvider#delete(android.net.Uri,
	 * java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String arg1, String[] arg2) {
		throw new UnsupportedOperationException();
	}

	/**
	 * This is called when a client calls
	 * {@link android.content.ContentResolver#getType(Uri)}. Returns the MIME
	 * data type of the URI given as a parameter.
	 *
	 * @param uri The URI whose MIME type is desired.
	 * @return The MIME type of the URI.
	 * @throws IllegalArgumentException if the incoming URI pattern is invalid.
	 */
	@Override
	public String getType(Uri uri) {
		/**
		 * Chooses the MIME type based on the incoming URI pattern
		 */
		switch (sUriMatcher.match(uri)) {

			// If the pattern is for note IDs, returns the note ID content type.
			case PRICE_ITEM_ID_WITH_SOLAR_SYSTEM_ID:
				return Prices.CONTENT_TYPE_PRICES;

			// If the URI pattern doesn't match any permitted patterns, throws
			// an exception.
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.content.ContentProvider#insert(android.net.Uri,
	 * android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		throw new UnsupportedOperationException();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.content.ContentProvider#query(android.net.Uri,
	 * java.lang.String[], java.lang.String, java.lang.String[],
	 * java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		throw new UnsupportedOperationException();
	}

	/**
	 * This is called when a client calls
	 * {@link android.content.ContentResolver#update(Uri, ContentValues, String, String[])}
	 * Updates records in the database. The column names specified by the keys
	 * in the values map are updated with new data specified by the values in
	 * the map. If the incoming URI matches the note ID URI pattern, then the
	 * method updates the one record specified by the ID in the URI; otherwise,
	 * it updates a set of records. The record or records must match the input
	 * selection criteria specified by where and whereArgs. If rows were
	 * updated, then listeners are notified of the change.
	 *
	 * @param uri       The URI pattern to match and update.
	 * @param values    A map of column names (keys) and new values (values).
	 * @param where     An SQL "WHERE" clause that selects records based on their
	 *                  column values. If this is null, then all records that match
	 *                  the URI pattern are selected.
	 * @param whereArgs An array of selection criteria. If the "where" param contains
	 *                  value placeholders ("?"), then each placeholder is replaced by
	 *                  the corresponding element in the array.
	 * @return The number of rows updated.
	 * @throws IllegalArgumentException if the incoming URI pattern is invalid.
	 */
	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		String finalWhere;
		String itemIdStr, solarSystemIdStr;

        assert db != null;
        assert uri != null;
        assert uri.getPathSegments() != null;
        assert getContext() != null;
        assert getContext().getContentResolver() != null;

		// TODO supprimer solarSystemId
		switch (sUriMatcher.match(uri)) {

			case PRICE_ITEM_ID_WITH_SOLAR_SYSTEM_ID:
				itemIdStr = uri.getPathSegments().get(Prices.ITEM_ID_PATH_POSITION);
				solarSystemIdStr = uri.getPathSegments().get(Prices.SOLAR_SYSTEM_ID_PATH_POSITION);

				finalWhere = Prices.ITEM_ID + " = " + itemIdStr + " AND " +
						Prices.SOLAR_SYSTEM_ID + " = " + solarSystemIdStr;

				if (where != null) {
					finalWhere = finalWhere + " AND " + where;
				}

                count = db.updateWithOnConflict(Prices.TABLE_NAME,
						values,
						finalWhere,
						whereArgs,
						SQLiteDatabase.CONFLICT_IGNORE
				);

				if (count == 0) {
					values.putNull(Prices._ID);
					values.put(Prices.ITEM_ID, Integer.parseInt(itemIdStr));
					db.insert(Prices.TABLE_NAME, null, values);
				}

				break;

			case PRICE_ITEM_ID:
				itemIdStr = uri.getPathSegments().get(Prices.ITEM_ID_PATH_POSITION);

				finalWhere = Prices.ITEM_ID + " = " + itemIdStr;

				if (where != null) {
					finalWhere = finalWhere + " AND " + where;
				}

				count = db.update(Prices.TABLE_NAME,
						values,
						finalWhere,
						whereArgs
				);

				if (count == 0) {
					values.putNull(Prices._ID);
					values.put(Prices.ITEM_ID, Integer.parseInt(itemIdStr));
					db.insert(Prices.TABLE_NAME, null, values);
				}

				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		/*
		 * Gets a handle to the content resolver object for the current context,
		 * and notifies it that the incoming URI changed. The object passes this
		 * along to the resolver framework, and observers that have registered
		 * themselves for the provider are notified.
		 */
		getContext().getContentResolver().notifyChange(uri, null);

		// Returns the number of rows updated.
		return count;
	}

	@SuppressWarnings("UnusedDeclaration")
    private Long getCurrentTradeStationId() {

        assert getContext() != null;
        assert getContext().getContentResolver() != null;

		Cursor cursor = getContext().getContentResolver().query(
				Station.CONTENT_URI,
				new String[]{Station._ID},
				Station.ROLE + " IN (" +
						EOITConst.Stations.TRADE_ROLE + "," +
						EOITConst.Stations.BOTH_ROLES + ")",
				null, null);

        assert cursor != null;

		if (DbUtil.hasAtLeastOneRow(cursor)) {
			return cursor.getLong(cursor.getColumnIndexOrThrow(Station._ID));
		}

		return null;
	}

}
