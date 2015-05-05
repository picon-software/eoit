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

import android.app.SearchManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import fr.piconsoft.eoit.BuildConfig;
import fr.piconsoft.eoit.db.SearchDatabase;
import fr.piconsoft.eoit.db.util.DbCompatUtil;
import fr.piconsoft.eoit.model.AsteroidConstitution;
import fr.piconsoft.eoit.model.Blueprint;
import fr.piconsoft.eoit.model.ColumnsNames;
import fr.piconsoft.eoit.model.Groups;
import fr.piconsoft.eoit.model.InventionSkill;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.model.ItemMaterials;
import fr.piconsoft.eoit.model.ManagedItem;
import fr.piconsoft.eoit.model.Parameter;
import fr.piconsoft.eoit.model.Region;
import fr.piconsoft.eoit.model.SolarSystem;
import fr.piconsoft.eoit.model.Station;
import fr.piconsoft.eoit.model.Stock;
import fr.piconsoft.eoit.util.QueryBuilderUtil;

/**
 * @author picon.software
 *         FIXME create separate content providers for each bean
 */
public class ItemContentProvider extends AbstractContentProvider {

	public static final String AUTHORITY = "fr.piconsoft.eoit.provider.ItemContentProvider";
	private static final String LOG_TAG = "ItemContentProvider";
	private static final int ITEM_ID = 0;
	private static final int ITEMS_ID = 1;
	private static final int BLUEPRINT_ITEM_ID = 3;
	private static final int MATERIALS_ITEM_ID = 4;
	private static final int BLUEPRINT_ID = 5;
	private static final int ITEM_SEARCH_ID = 6;
	private static final int SEARCH_SUGGEST_ID = 7;
	private static final int MATERIALS_PRICES_ITEM_ID = 9;
	private static final int STOCKS_ID = 10;
	private static final int STOCK_ID = 11;
	private static final int INVENTION_SKILL_ITEM_ID = 12;
	private static final int PARAMETERS_ID = 13;
	private static final int PARAMETER_ID = 14;
	private static final int PARAMETERS_SKILLS_ID = 15;
	private static final int REGIONS_ID = 16;
	private static final int SOLAR_SYSTEMS_ID = 17;
	private static final int STATIONS_ID = 18;
	private static final int STATION_ID = 19;
	private static final int REFINE_MATERIALS_PRICES_ITEM_ID = 20;
	private static final int GROUPS_ID = 21;
	private static final int MANAGED_ITEMS_ID = 22;
	private static final int BLUEPRINT_INVENTION_REQUIRED_ITEMS_ID = 23;
	private static final int REFINE_MATERIAL_ITEMS_ID = 24;
	private static final int INVEST_ITEMS_ID = 25;
	private static final int ASTEROID_CONSTITUTION_ID = 26;
	private static final int BLUEPRINT_COPY_REQUIRED_ITEMS_ID = 27;
	private static final int REACTION_MATERIALS_PRICES_ITEM_ID = 28;
	private static final int PLANETARY_MATERIALS_PRICES_ITEM_ID = 29;

	private static Map<String, String> regionsProjectionMap = new HashMap<>();
	private static Map<String, String> solarSystemsProjectionMap = new HashMap<>();
	private static Map<String, String> stationsProjectionMap = new HashMap<>();
	private static Map<String, String> groupsProjectionMap = new HashMap<>();
	private static Map<String, String> managedItemsProjectionMap = new HashMap<>();
	private static Map<String, String> asteroidConstitutionProjectionMap = new HashMap<>();

	static {

		// Add a pattern that routes URIs terminated with "items" to a ITEMS
		// operation
		sUriMatcher.addURI(AUTHORITY, "item", ITEMS_ID);

		// Add a pattern that routes URIs terminated with "item" plus an integer
		// to a item ID operation
		sUriMatcher.addURI(AUTHORITY, "item/#", ITEM_ID);
		sUriMatcher.addURI(AUTHORITY, "item/invest/#", INVEST_ITEMS_ID);
		sUriMatcher.addURI(AUTHORITY, "blueprint/item/#", BLUEPRINT_ITEM_ID);
		sUriMatcher.addURI(AUTHORITY, "blueprint/#", BLUEPRINT_ID);
		sUriMatcher.addURI(AUTHORITY, "blueprint/invention/#", BLUEPRINT_INVENTION_REQUIRED_ITEMS_ID);
		sUriMatcher.addURI(AUTHORITY, "blueprint/copy/items/#", BLUEPRINT_COPY_REQUIRED_ITEMS_ID);
		sUriMatcher.addURI(AUTHORITY, "invention_skills/item/#", INVENTION_SKILL_ITEM_ID);
		sUriMatcher.addURI(AUTHORITY, "materials/item/#", MATERIALS_ITEM_ID);
		sUriMatcher.addURI(AUTHORITY, "materials/prices/item/#", MATERIALS_PRICES_ITEM_ID);
		sUriMatcher.addURI(AUTHORITY, "refine/materials/prices/item/#", REFINE_MATERIALS_PRICES_ITEM_ID);
		sUriMatcher.addURI(AUTHORITY, "reaction/materials/prices/item/#", REACTION_MATERIALS_PRICES_ITEM_ID);
		sUriMatcher.addURI(AUTHORITY, "planetary/materials/prices/item/#", PLANETARY_MATERIALS_PRICES_ITEM_ID);
		sUriMatcher.addURI(AUTHORITY, "stocks", STOCKS_ID);
		sUriMatcher.addURI(AUTHORITY, "stocks/#", STOCK_ID);
		sUriMatcher.addURI(AUTHORITY, "parameters", PARAMETERS_ID);
		sUriMatcher.addURI(AUTHORITY, "parameters/#", PARAMETER_ID);
		sUriMatcher.addURI(AUTHORITY, "parameters/skills", PARAMETERS_SKILLS_ID);
		sUriMatcher.addURI(AUTHORITY, "regions", REGIONS_ID);
		sUriMatcher.addURI(AUTHORITY, "solar_systems", SOLAR_SYSTEMS_ID);
		sUriMatcher.addURI(AUTHORITY, "stations", STATIONS_ID);
		sUriMatcher.addURI(AUTHORITY, "stations/#", STATION_ID);
		sUriMatcher.addURI(AUTHORITY, "groups", GROUPS_ID);
		sUriMatcher.addURI(AUTHORITY, "managed_items", MANAGED_ITEMS_ID);
		sUriMatcher.addURI(AUTHORITY, AsteroidConstitution.TABLE_NAME, ASTEROID_CONSTITUTION_ID);

		sUriMatcher.addURI(AUTHORITY, "item/search", ITEM_SEARCH_ID);
		// to get suggestions...
		sUriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST_ID);
		sUriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST_ID);

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

		groupsProjectionMap.put(Groups._ID, "g." + Groups._ID);
		groupsProjectionMap.put(Groups.CATEGORIE_ID, "g." + Groups.CATEGORIE_ID);
		groupsProjectionMap.put("c." + ColumnsNames.Categories.NAME, "c." + ColumnsNames.Categories.NAME);
		groupsProjectionMap.put("g." + Groups.NAME, "g." + Groups.NAME);

		managedItemsProjectionMap.put(ManagedItem._ID, ManagedItem._ID);
		managedItemsProjectionMap.put(ManagedItem.COLUMN_NAME_ITEM_ID, ManagedItem.COLUMN_NAME_ITEM_ID);

		addProjectionMapping(asteroidConstitutionProjectionMap, "ac.",
				AsteroidConstitution._ID, AsteroidConstitution.NAME,
				AsteroidConstitution.VOLUME, AsteroidConstitution.PORTION_SIZE,
				AsteroidConstitution.TRITANIUM_QUANTITY, AsteroidConstitution.PYERITE_QUANTITY,
				AsteroidConstitution.MEXALLON_QUANTITY, AsteroidConstitution.ISOGEN_QUANTITY,
				AsteroidConstitution.NOCXIUM_QUANTITY, AsteroidConstitution.ZYDRINE_QUANTITY,
				AsteroidConstitution.MEGACYTE_QUANTITY, AsteroidConstitution.MORPHITE_QUANTITY);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.content.ContentProvider#delete(android.net.Uri,
	 * java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		assert db != null;

		String tableName;

		switch (sUriMatcher.match(uri)) {

			case STOCKS_ID:
				tableName = Stock.TABLE_NAME;
				break;

			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}

		return db.delete(tableName, selection, selectionArgs);
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

			// If the pattern is for notes or live folders, returns the general
			// content type.
			case ITEMS_ID:
			case ITEM_SEARCH_ID:
			case SEARCH_SUGGEST_ID:
				return Item.CONTENT_TYPE_ITEMS;

			// If the pattern is for note IDs, returns the note ID content type.
			case ITEM_ID:
				return Item.CONTENT_TYPE_ITEMS;

			// If the pattern is for note IDs, returns the note ID content type.
			case BLUEPRINT_ITEM_ID:
			case BLUEPRINT_ID:
				return Blueprint.CONTENT_TYPE_BLUEPRINT;

			// If the pattern is for note IDs, returns the note ID content type.
			case MATERIALS_ITEM_ID:
				return ItemMaterials.CONTENT_TYPE_ITEM_MATERIALS;

			case MATERIALS_PRICES_ITEM_ID:
			case REFINE_MATERIALS_PRICES_ITEM_ID:
			case REFINE_MATERIAL_ITEMS_ID:
				return ItemMaterials.CONTENT_TYPE_ITEMS_MATERIALS;

			case STOCKS_ID:
				return Stock.CONTENT_TYPE_STOCKS;

			case PARAMETERS_ID:
				return Parameter.CONTENT_TYPE_PARAMETERS;

			case PARAMETER_ID:
				return Parameter.CONTENT_TYPE_PARAMETER;

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
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		assert db != null;

		/**
		 * Chooses the MIME type based on the incoming URI pattern
		 */
		switch (sUriMatcher.match(uri)) {
			case STOCKS_ID:
				try {
					db.insertWithOnConflict(Stock.TABLE_NAME, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
				} catch (SQLiteConstraintException e) {
					Log.w(LOG_TAG, "Item with id: " + initialValues.get(Stock.COLUMN_NAME_ITEM_ID) + " is not present.");
				}

				return uri;

			case PARAMETERS_ID:
				db.insert(Parameter.TABLE_NAME, null, initialValues);

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
		//qb.setDistinct(true);

		//String tradeStationSolarSystemId = String.valueOf(Stations.getTradeStation().solarSystemId);

		// Opens the database object in "read" mode, since no writes need to be
		// done.
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		assert db != null;
		assert uri != null;
		assert uri.getPathSegments() != null;
		assert getContext() != null;
		assert getContext().getContentResolver() != null;

		/**
		 * Choose the projection and adjust the "where" clause based on URI
		 * pattern-matching.
		 */
		switch (sUriMatcher.match(uri)) {
			/*
					 * If the incoming URI is for a single item identified by its ID,
					 * chooses the item ID projection, and appends "_ID = <itemID>" to the
					 * where clause, so that it selects that single item
					 */
			case ITEM_ID:
				qb.appendWhere(Item._ID + // the name of the ID column
						" = " +
						// the position of the item ID itself in the incoming URI
						uri.getPathSegments().get(Item.ITEM_ID_PATH_POSITION));
				// If the incoming URI is for items
			case ITEMS_ID:
				qb.setTables("item_info");
				break;

			case BLUEPRINT_ID:
				qb.setTables("blueprint_info");
				qb.appendWhere(Blueprint._ID +
						" = " +
						// the position of the item ID itself in the incoming URI
						uri.getPathSegments().get(Blueprint.BLUEPRINT_ID_PATH_POSITION));
				break;

			case BLUEPRINT_ITEM_ID:
				qb.setTables("blueprint_info");
				qb.appendWhere(Blueprint.PRODUCE_ITEM_ID +
						" = " +
						// the position of the item ID itself in the incoming URI
						uri.getPathSegments().get(Blueprint.ITEM_ID_PATH_POSITION));
				break;

			case MATERIALS_ITEM_ID:
				qb.setTables("manufacturing_items");
				qb.appendWhere(ItemMaterials.ITEM_ID +
						" = " +
						// the position of the item ID itself in the incoming URI
						uri.getPathSegments().get(ItemMaterials.ITEM_ID_PATH_POSITION));
				break;

			case INVENTION_SKILL_ITEM_ID:
				qb.setTables("invention_skill");
				qb.appendWhere(InventionSkill.ITEM_ID +
						" = " +
						// the position of the item ID itself in the incoming URI
						uri.getPathSegments().get(InventionSkill.ITEM_ID_PATH_POSITION));
				break;

			case ITEM_SEARCH_ID:
				if (selectionArgs == null) {
					throw new IllegalArgumentException(
							"selectionArgs must be provided for the Uri: " + uri);
				}
				return search(selectionArgs[0], sortOrder);

			case SEARCH_SUGGEST_ID:
				if (selectionArgs == null) {
					throw new IllegalArgumentException(
							"selectionArgs must be provided for the Uri: " + uri);
				}
				return getSuggestions(selectionArgs[0]);

			case MATERIALS_PRICES_ITEM_ID:
				qb.setTables("material_price_item");
				qb.appendWhere(ItemMaterials.ITEM_ID +
						" = " +
						// the position of the item ID itself in the incoming URI
						uri.getPathSegments().get(ItemMaterials.ITEM_ID_PRICES_PATH_POSITION));
				break;

			case REFINE_MATERIALS_PRICES_ITEM_ID:
				qb.setTables("refine_price_item");
				qb.appendWhere(ItemMaterials.ITEM_ID +
						" = " +
						// the position of the item ID itself in the incoming URI
						uri.getPathSegments().get(ItemMaterials.ITEM_ID_REFINE_PRICES_PATH_POSITION));
				break;

			case REACTION_MATERIALS_PRICES_ITEM_ID:
				qb.setTables("reaction_price_item");
				qb.appendWhere(ItemMaterials.ITEM_ID +
						" = " +
						// the position of the item ID itself in the incoming URI
						uri.getPathSegments().get(ItemMaterials.ITEM_ID_REACTION_PRICES_PATH_POSITION));
				break;

			case PLANETARY_MATERIALS_PRICES_ITEM_ID:
				qb.setTables("planetary_price_item");
				qb.appendWhere(ItemMaterials.ITEM_ID +
						" = " +
						// the position of the item ID itself in the incoming URI
						uri.getPathSegments().get(ItemMaterials.ITEM_ID_PLANETARY_PRICES_PATH_POSITION));
				break;

			case STOCKS_ID:
				qb.setTables("stock_info");

				break;

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

			case GROUPS_ID:
				qb.setTables(
						QueryBuilderUtil.table(Groups.TABLE_NAME + " g")
								.join(ColumnsNames.Categories.TABLE_NAME + " c")
								.on("g." + Groups.CATEGORIE_ID, "c." + ColumnsNames.Categories._ID)
								.toString());
				qb.setProjectionMap(groupsProjectionMap);

				break;

			case MANAGED_ITEMS_ID:
				qb.setTables(ManagedItem.TABLE_NAME);
				qb.setProjectionMap(managedItemsProjectionMap);

				break;

			case BLUEPRINT_INVENTION_REQUIRED_ITEMS_ID:
				qb.setTables("invention_material_price_item");
				qb.appendWhere(Blueprint.PRODUCE_ITEM_ID +
						" = " +
						// the position of the item ID itself in the incoming URI
						uri.getPathSegments().get(Blueprint.ITEM_ID_PATH_POSITION));
				break;

			case BLUEPRINT_COPY_REQUIRED_ITEMS_ID:
				qb.setTables("material_blueprint_copy");
				qb.appendWhere(Blueprint.PRODUCE_ITEM_ID +
						" = " +
						// the position of the item ID itself in the incoming URI
						uri.getPathSegments().get(Blueprint.BLUEPRINT_COPY_ID_PATH_POSITION));
				break;

			case INVEST_ITEMS_ID:
//				qb.setTables(
//						QueryBuilderUtil.table(Item.TABLE_NAME + " i")
//								.join(ItemMaterials.TABLE_NAME + " im")
//								.on("im." + ItemMaterials.ITEM_ID, "i." + Item._ID)
//								.join(Item.TABLE_NAME + " i2")
//								.on("im." + ItemMaterials.MATERIAL_ITEM_ID, "i2." + Item._ID)
//								.join(Groups.TABLE_NAME + " g")
//								.on("i2." + Item.GROUP_ID, "g." + Groups._ID)
//								.join(Blueprint.TABLE_NAME + " b")
//								.on("b." + Blueprint.PRODUCE_ITEM_ID, "i2." + Item._ID)
//								.join(Item.TABLE_NAME + " i3")
//								.on("b." + Blueprint._ID, "i3." + Item._ID)
//								.join(Station.TABLE_NAME + " s")
//								.onIn(Station.ROLE,
//										EOITConst.Stations.TRADE_ROLE, EOITConst.Stations.BOTH_ROLES)
//								.leftJoin(Prices.TABLE_NAME + " p")
//								.on("i3." + Item._ID, "p." + Prices.ITEM_ID)
//								.andOn("p." + Prices.SOLAR_SYSTEM_ID, "s." + Station.SOLAR_SYSTEM_ID)
//								.toString());
//				qb.setProjectionMap(investItemProjectionMap);
//				qb.appendWhere("i." + Item._ID +
//						" = " +
//						// the position of the item ID itself in the incoming URI
//						uri.getPathSegments().get(Item.ITEM_ID_PATH_POSITION_INVEST) +
//						" and (" +
//						ItemMaterials.COLUMN_NAME_ACTIVITY_ID +
//						" = 1 or " +
//						ItemMaterials.COLUMN_NAME_ACTIVITY_ID +
//						" is null) and " +
//						"(g." + Groups.CATEGORIE_ID +
//						" <> " +
//						EOITConst.Categories.SKILL_CATEGORIE_ID +
//						" OR " +
//						"g." + Groups.CATEGORIE_ID +
//						" <> " +
//						EOITConst.Categories.PLANETARY_COMMODITIES_CATEGORIE_ID +
//						") and " +
//						"b." + Blueprint._ID +
//						" is not null and " +
//						"i2." + Item.CHOSEN_PRICE_ID +
//						" = 2");
				break;

			case ASTEROID_CONSTITUTION_ID:
				qb.setTables(AsteroidConstitution.TABLE_NAME);
				//qb.setProjectionMap(asteroidConstitutionProjectionMap);
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
		Cursor c;
		long time = System.nanoTime();
		c = qb.query(db, // The database to query
				projection, // The columns to return from the query
				selection, // The columns for the where clause
				selectionArgs, // The values for the where clause
				null, // don't group the rows
				null, // don't filter by row groups
				sortOrder // The sort order
		);
		if (BuildConfig.DEBUG) {
			String query = DbCompatUtil.buildQuery(qb, projection, selection, null, null, null, sortOrder);
			Log.v(LOG_TAG, "Query executed in : " + ((System.nanoTime() - time) / 1000) / 1000f + "ms : " + query);
		}
		// Tells the Cursor what URI to watch, so it knows when its source data
		// changes

		assert c != null;

		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
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

		assert db != null;
		assert uri != null;
		assert uri.getPathSegments() != null;
		assert getContext() != null;
		assert getContext().getContentResolver() != null;

		int count;
		String finalWhere = where;
		String itemIdStr;

		switch (sUriMatcher.match(uri)) {

			case ITEM_ID:
				itemIdStr = uri.getPathSegments().get(Item.ITEM_ID_PATH_POSITION);

				finalWhere = Item._ID + " = " + itemIdStr;

				if (where != null) {
					finalWhere = finalWhere + " AND " + where;
				}

			case ITEMS_ID:
				count = db.update(Item.TABLE_NAME,
						values,
						finalWhere,
						whereArgs
				);
				break;

			case BLUEPRINT_ITEM_ID:
			case BLUEPRINT_ID:
				String blueprintId = uri.getPathSegments().get(Blueprint.BLUEPRINT_ID_PATH_POSITION);

				finalWhere = Blueprint._ID + " = " + blueprintId;

				if (where != null) {
					finalWhere = finalWhere + " AND " + where;
				}

				count = db.update(Blueprint.TABLE_NAME,
						values,
						finalWhere,
						whereArgs
				);
				break;

			case STOCK_ID:
				String stockId = uri.getPathSegments().get(Stock.STOCK_ID_PATH_POSITION);

				finalWhere = Stock._ID + " = " + stockId;

				if (where != null) {
					finalWhere = finalWhere + " AND " + where;
				}

				count = db.update(Stock.TABLE_NAME,
						values,
						finalWhere,
						whereArgs
				);

				break;

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

			case PARAMETER_ID:
				String parameterId = uri.getPathSegments().get(Parameter.PARAMETERS_ID_PATH_POSITION);

				finalWhere = Parameter._ID + " = " + parameterId;

				if (where != null) {
					finalWhere = finalWhere + " AND " + where;
				}

				count = db.update(Parameter.TABLE_NAME,
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

	private Cursor search(String query, String sortOrder) {
		query = query.toLowerCase();
		String[] columns = new String[]{
				SearchDatabase.ITEM_ID + " AS " + Item._ID,
				SearchManager.SUGGEST_COLUMN_TEXT_1};

		return new SearchDatabase(mOpenHelper).getWordMatches(query, columns, sortOrder);
	}

	private Cursor getSuggestions(String query) {
		query = query.toLowerCase();
		String[] columns = new String[]{
				Item._ID,
				SearchManager.SUGGEST_COLUMN_TEXT_1,
				SearchManager.SUGGEST_COLUMN_TEXT_2,
				/* SearchManager.SUGGEST_COLUMN_SHORTCUT_ID,
					(only if you want to refresh shortcuts) */
				SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID};

		return new SearchDatabase(mOpenHelper).getWordMatches(query, columns, null);
	}
}
