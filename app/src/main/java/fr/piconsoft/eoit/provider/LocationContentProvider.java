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

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import fr.piconsoft.eoit.util.QueryBuilderUtil;
import fr.piconsoft.eoit.BuildConfig;
import fr.piconsoft.eoit.model.ColumnsNames;
import fr.piconsoft.eoit.db.util.DbCompatUtil;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.Prices;
import fr.piconsoft.eoit.model.Region;
import fr.piconsoft.eoit.model.SolarSystem;
import fr.piconsoft.eoit.model.Station;

/**
 * @author picon.software
 */
public class LocationContentProvider extends AbstractContentProvider {

	public static final String AUTHORITY = "fr.piconsoft.eoit.provider.LocationContentProvider";
	private static final String LOG_TAG = LocationContentProvider.class.getSimpleName();
	private static final int REGIONS_ID = 0;
	private static final int SOLAR_SYSTEMS_ID = 1;
	private static final int STATIONS_ID = 2;
	private static final int STATION_ID = 3;
	private static final int FAVORITE_STATIONS_PRICES_ITEM_ID = 4;
	private static final int ALL_STATIONS_ID = 5;

	private static Map<String, String> regionsProjectionMap = new HashMap<>();
	private static Map<String, String> solarSystemsProjectionMap = new HashMap<>();
	private static Map<String, String> stationsProjectionMap = new HashMap<>();
	private static Map<String, String> allStationsProjectionMap = new HashMap<>();
	private static Map<String, String> favoriteStationsPricesProjectionMap = new HashMap<>();

	static {

		sUriMatcher.addURI(AUTHORITY, "regions", REGIONS_ID);
		sUriMatcher.addURI(AUTHORITY, "solar_systems", SOLAR_SYSTEMS_ID);
		sUriMatcher.addURI(AUTHORITY, "stations", STATIONS_ID);
		sUriMatcher.addURI(AUTHORITY, "stations/all", ALL_STATIONS_ID);
		sUriMatcher.addURI(AUTHORITY, "stations/#", STATION_ID);
		sUriMatcher.addURI(AUTHORITY, "stations/prices/item/#", FAVORITE_STATIONS_PRICES_ITEM_ID);

		regionsProjectionMap.put(Region._ID, "r." + Region._ID);
		regionsProjectionMap.put(Region._COUNT, "r." + Region._COUNT);
		regionsProjectionMap.put(Region.NAME, "r." + Region.NAME);

		solarSystemsProjectionMap.put(SolarSystem._ID, "ss." + SolarSystem._ID);
		solarSystemsProjectionMap.put(SolarSystem._COUNT, "ss." + SolarSystem._COUNT);
		solarSystemsProjectionMap.put(SolarSystem.NAME, "ss." + SolarSystem.NAME);
		solarSystemsProjectionMap.put(SolarSystem.COLUMN_NAME_REGION_ID, "ss." + SolarSystem.COLUMN_NAME_REGION_ID);

		stationsProjectionMap.put(Station._ID, "s." + Station._ID);
		stationsProjectionMap.put(Station._COUNT, "s." + Station._COUNT);
		stationsProjectionMap.put(Station.STATION_TYPE_ID, "s." + Station.STATION_TYPE_ID);
		stationsProjectionMap.put(Station.NAME, "s." + Station.NAME);
		stationsProjectionMap.put(Station.REGION_ID, "s." + Station.REGION_ID);
		stationsProjectionMap.put(Station.SOLAR_SYSTEM_ID, "s." + Station.SOLAR_SYSTEM_ID);
		stationsProjectionMap.put(Station.CORPORATION_ID, "s." + Station.CORPORATION_ID);
		stationsProjectionMap.put(Region.COLUMN_NAME_NAME_ALIAS, Region.COLUMN_NAME_NAME_ALIAS);
		stationsProjectionMap.put(Station.FAVORITE, "s." + Station.FAVORITE);
		stationsProjectionMap.put(Station.ROLE, "s." + Station.ROLE);
		stationsProjectionMap.put(Station.STANDING, "s." + Station.STANDING);

		allStationsProjectionMap.put(Station._ID, Station._ID);
		allStationsProjectionMap.put(Station._COUNT, Station._COUNT);
		allStationsProjectionMap.put(Station.STATION_TYPE_ID, Station.STATION_TYPE_ID);
		allStationsProjectionMap.put(Station.NAME, Station.NAME);
		allStationsProjectionMap.put(Station.REGION_ID, Station.REGION_ID);
		allStationsProjectionMap.put(Station.SOLAR_SYSTEM_ID, Station.SOLAR_SYSTEM_ID);
		allStationsProjectionMap.put(Station.CORPORATION_ID, Station.CORPORATION_ID);
		allStationsProjectionMap.put(Region.COLUMN_NAME_NAME_ALIAS, Region.COLUMN_NAME_NAME_ALIAS);
		allStationsProjectionMap.put(Station.FAVORITE, Station.FAVORITE);
		allStationsProjectionMap.put(Station.ROLE, Station.ROLE);
		allStationsProjectionMap.put(Station.STANDING, Station.STANDING);
		allStationsProjectionMap.put(ColumnsNames.Categories.COLUMN_NAME_NAME_ALIAS, ColumnsNames.Categories.COLUMN_NAME_NAME_ALIAS);

		favoriteStationsPricesProjectionMap.put(Station._ID, "s." + Station._ID);
		favoriteStationsPricesProjectionMap.put(Station._COUNT, "s." + Station._COUNT);
		favoriteStationsPricesProjectionMap.put(Station.STATION_TYPE_ID, "s." + Station.STATION_TYPE_ID);
		favoriteStationsPricesProjectionMap.put(Station.NAME, "s." + Station.NAME);
		favoriteStationsPricesProjectionMap.put(Station.REGION_ID, "s." + Station.REGION_ID);
		favoriteStationsPricesProjectionMap.put(Station.SOLAR_SYSTEM_ID, "s." + Station.SOLAR_SYSTEM_ID);
		favoriteStationsPricesProjectionMap.put(Region.COLUMN_NAME_NAME_ALIAS, Region.COLUMN_NAME_NAME_ALIAS);
		favoriteStationsPricesProjectionMap.put(Station.FAVORITE, "s." + Station.FAVORITE);
		favoriteStationsPricesProjectionMap.put(Prices.BUY_PRICE, "p." + Prices.BUY_PRICE);
		favoriteStationsPricesProjectionMap.put(Prices.BUY_VOLUME, "p." + Prices.BUY_VOLUME);
		favoriteStationsPricesProjectionMap.put(Prices.SELL_PRICE, "p." + Prices.SELL_PRICE);
		favoriteStationsPricesProjectionMap.put(Prices.SELL_VOLUME, "p." + Prices.SELL_VOLUME);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		String whereClause = selection, tableName;

		switch (sUriMatcher.match(uri)) {

			case STATION_ID:
				if(selection != null) {
					whereClause = selection + " AND " + Station._ID + " = " + ContentUris.parseId(uri);
				} else {
					whereClause = Station._ID + " = " + ContentUris.parseId(uri);
				}
			case STATIONS_ID:
				tableName = Station.TABLE_NAME;
				break;

			default:
				// If the URI doesn't match any of the known patterns, throw an
				// exception.
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return db.delete(tableName, whereClause, selectionArgs);
	}

	/**
	 * This is called when a client calls
	 * {@link android.content.ContentResolver#getType(Uri)}. Returns the MIME
	 * data type of the URI given as a parameter.
	 *
	 * @param uri
	 *            The URI whose MIME type is desired.
	 * @return The MIME type of the URI.
	 * @throws IllegalArgumentException
	 *             if the incoming URI pattern is invalid.
	 */
	@Override
	public String getType(Uri uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		switch (sUriMatcher.match(uri)) {
			case STATIONS_ID:
				try {
					db.insertWithOnConflict(Station.TABLE_NAME, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
				} catch (SQLiteConstraintException e) {
					Log.w(LOG_TAG, "Station with id: " + initialValues.get(Station._ID) + " can't be inserted.");
				}

				return uri;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
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
		// Constructs a new query builder and sets its table name
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setDistinct(true);

		// Opens the database object in "read" mode, since no writes need to be
		// done.
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		/**
		 * Choose the projection and adjust the "where" clause based on URI
		 * pattern-matching.
		 */
		switch (sUriMatcher.match(uri)) {

		case REGIONS_ID:
			qb.setTables(Region.TABLE_NAME + " r");
			qb.setProjectionMap(regionsProjectionMap);

			break;

		case SOLAR_SYSTEMS_ID:
			qb.setTables(
					QueryBuilderUtil.table(SolarSystem.TABLE_NAME + " ss")
					.join(Region.TABLE_NAME + " r")
					.on("r." + Region._ID, "ss." + SolarSystem.COLUMN_NAME_REGION_ID).toString());
			qb.setProjectionMap(solarSystemsProjectionMap);

			break;

		case STATIONS_ID:
			qb.setTables(
					QueryBuilderUtil.table(Station.TABLE_NAME + " s")
					.join(SolarSystem.TABLE_NAME + " ss")
					.on("s." + Station.SOLAR_SYSTEM_ID, "ss." + SolarSystem._ID)
					.join(Region.TABLE_NAME + " r")
					.on("r." + Region._ID, "ss." + SolarSystem.COLUMN_NAME_REGION_ID)
					.toString());
			qb.setProjectionMap(stationsProjectionMap);

			break;

		case STATION_ID:
			qb.setTables(Station.TABLE_NAME + " s");
			qb.setProjectionMap(stationsProjectionMap);
			qb.appendWhere("s." + Station._ID +
					" = " +
					uri.getPathSegments().get(Station.STATIONS_ID_PATH_POSITION));
			break;

		case ALL_STATIONS_ID:
			qb.setTables(Station.ALL_STATION_VIEW);
			qb.setProjectionMap(allStationsProjectionMap);
			break;

		case FAVORITE_STATIONS_PRICES_ITEM_ID:
			qb.setTables(QueryBuilderUtil.table(Station.TABLE_NAME + " s")
					.join(SolarSystem.TABLE_NAME + " ss")
					.on("s." + Station.SOLAR_SYSTEM_ID, "ss." + SolarSystem._ID)
					.join(Region.TABLE_NAME + " r")
					.on("r." + Region._ID, "ss." + SolarSystem.COLUMN_NAME_REGION_ID)
					.join(Prices.TABLE_NAME + " p")
					.on("ss." + SolarSystem._ID, "p." + Prices.SOLAR_SYSTEM_ID)
					.join(Item.TABLE_NAME + " i")
					.on("i." + Item._ID, "p." + Prices.ITEM_ID)
					.toString());
			qb.setProjectionMap(favoriteStationsPricesProjectionMap);
			qb.appendWhere("s." + Station.FAVORITE + " = 1 AND i." + Item._ID +
					" = " +
					uri.getPathSegments().get(Station.ITEM_ID_PATH_POSITION));
			break;

		default:
			// If the URI doesn't match any of the known patterns, throw an
			// exception.
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		/*
		 * Performs the query. If no problems occur trying to read the database,
		 * then a Cursor object is returned; otherwise, the cursor variable
		 * contains null. If no records were selected, then the Cursor object is
		 * empty, and Cursor.getCount() returns 0.
		 */
		String query = DbCompatUtil.buildQuery(qb, projection, selection, null, null, sortOrder, null);
		if(BuildConfig.DEBUG) Log.v(LOG_TAG,
				"Launching query : " + query);
		Cursor c;
		long time = System.currentTimeMillis();
		c = qb.query(db, // The database to query
				projection, // The columns to return from the query
				selection, // The columns for the where clause
				selectionArgs, // The values for the where clause
				null, // don't group the rows
				null, // don't filter by row groups
				sortOrder // The sort order
				);
		if(BuildConfig.DEBUG) Log.v(LOG_TAG, "Query executed in : " + (System.currentTimeMillis() - time) + "ms");
		// Tells the Cursor what URI to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	/**
	 * This is called when a client calls
	 * {@link android.content.ContentResolver#update(Uri,ContentValues,String,String[])}
	 * Updates records in the database. The column names specified by the keys
	 * in the values map are updated with new data specified by the values in
	 * the map. If the incoming URI matches the note ID URI pattern, then the
	 * method updates the one record specified by the ID in the URI; otherwise,
	 * it updates a set of records. The record or records must match the input
	 * selection criteria specified by where and whereArgs. If rows were
	 * updated, then listeners are notified of the change.
	 *
	 * @param uri
	 *            The URI pattern to match and update.
	 * @param values
	 *            A map of column names (keys) and new values (values).
	 * @param where
	 *            An SQL "WHERE" clause that selects records based on their
	 *            column values. If this is null, then all records that match
	 *            the URI pattern are selected.
	 * @param whereArgs
	 *            An array of selection criteria. If the "where" param contains
	 *            value placeholders ("?"), then each placeholder is replaced by
	 *            the corresponding element in the array.
	 * @return The number of rows updated.
	 * @throws IllegalArgumentException
	 *             if the incoming URI pattern is invalid.
	 */
	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		String finalWhere = where;

		switch (sUriMatcher.match(uri)) {

		case STATION_ID:
			String stationId = uri.getPathSegments().get(Station.STATIONS_ID_PATH_POSITION);

			finalWhere = Station._ID + " = " + stationId;

			if (where != null) {
				finalWhere = finalWhere + " AND " + where;
			}

		case STATIONS_ID:

			count = db.update(Station.TABLE_NAME,
					values,
					finalWhere,
					whereArgs
					);

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
}
