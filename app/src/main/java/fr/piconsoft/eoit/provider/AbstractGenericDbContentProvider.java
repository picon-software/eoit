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
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;
import android.util.SparseArray;

import fr.piconsoft.eoit.BuildConfig;
import fr.piconsoft.eoit.db.util.DbCompatUtil;

/**
 * @author picon.software
 */
public abstract class AbstractGenericDbContentProvider extends AbstractContentProvider {

	private SparseArray<QueryHandler> queryHandlers = new SparseArray<>();

	private SparseArray<InsertHandler> insertHandlers = new SparseArray<>();

	private SparseArray<DeleteHandler> deleteHandlers = new SparseArray<>();

	private SparseArray<UpdateHandler> updateHandlers = new SparseArray<>();

	protected static String buildLogTag(Class<? extends AbstractGenericDbContentProvider> clazz) {
		return clazz.getSimpleName();
	}

	protected static String buildAuthority(Class<? extends AbstractGenericDbContentProvider> clazz) {
		return "fr.piconsoft.eoit.provider." + clazz.getSimpleName();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		int matcherId = sUriMatcher.match(uri);
		DeleteHandler deleteHandler = deleteHandlers.get(matcherId);
		if (deleteHandler != null) {
			return deleteHandler.onDelete(db, selection, selectionArgs);
		} else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	/**
	 * This is called when a client calls
	 * {@link android.content.ContentResolver#getType(android.net.Uri)}. Returns the MIME
	 * data type of the URI given as a parameter.
	 *
	 * @param uri
	 * 		The URI whose MIME type is desired.
	 * @return The MIME type of the URI.
	 * @throws IllegalArgumentException
	 * 		if the incoming URI pattern is invalid.
	 */
	@Override
	public String getType(Uri uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		assert db != null;

		int matcherId = sUriMatcher.match(uri);
		InsertHandler insertHandler = insertHandlers.get(matcherId);
		if (insertHandler != null) {
			insertHandler.onInsert(db, initialValues);

			return uri;
		} else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		assert db != null;
		assert uri != null;
		assert uri.getPathSegments() != null;
		assert getContext() != null;
		assert getContext().getContentResolver() != null;

		int matcherId = sUriMatcher.match(uri);
		QueryHandler queryHandler = queryHandlers.get(matcherId);
		if (queryHandler != null) {
			queryHandler.onQuery(qb, uri);
		} else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		Cursor c;
		long time = System.currentTimeMillis();
		c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		if (BuildConfig.DEBUG) {
			String query = DbCompatUtil.buildQuery(qb, projection, selection, null, null, null, sortOrder);
			Log.v(getLogTag(), "Query executed in : " + (System.currentTimeMillis() - time) + "ms : " + query);
		}

		assert c != null;

		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		int matcherId = sUriMatcher.match(uri);
		UpdateHandler updateHandler = updateHandlers.get(matcherId);
		if (updateHandler != null) {
			return updateHandler.onUpdate(db, uri, values, where, whereArgs);
		} else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	protected abstract String getLogTag();

	protected abstract String getAuthority();

	protected void registerQueryHandler(int matcherId, QueryHandler queryHandler) {
		Log.i("AbstractGenericDbContentProvider", "");
		queryHandlers.put(matcherId, queryHandler);
	}

	protected void registerInsertHandler(int matcherId, InsertHandler insertHandler) {
		insertHandlers.put(matcherId, insertHandler);
	}

	protected void registerDeleteHandler(int matcherId, DeleteHandler deleteHandler) {
		deleteHandlers.put(matcherId, deleteHandler);
	}

	protected void registerUpdateHandler(int matcherId, UpdateHandler updateHandler) {
		updateHandlers.put(matcherId, updateHandler);
	}

	public interface QueryHandler {

		void onQuery(SQLiteQueryBuilder qb, Uri uri);
	}

	public interface InsertHandler {

		void onInsert(SQLiteDatabase db, ContentValues initialValues);
	}

	public interface DeleteHandler {

		int onDelete(SQLiteDatabase db, String selection, String[] selectionArgs);
	}

	public interface UpdateHandler {

		int onUpdate(SQLiteDatabase db, Uri uri, ContentValues values, String where, String[] whereArgs);
	}
}
