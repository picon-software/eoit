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

import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.model.ManufacturingPlans;
import fr.piconsoft.eoit.model.ManufacturingSession;
import fr.piconsoft.eoit.model.ManufacturingShoppingList;

/**
 * @author picon.software
 */
public class ManufacturingContentProvider extends AbstractGenericDbContentProvider {

	private static final String LOG_TAG =
			buildLogTag(ManufacturingContentProvider.class);

	public static final String AUTHORITY =
			buildAuthority(ManufacturingContentProvider.class);

	//----------------------
	//----------------- Uris
	public static final Uri[] PLANS_URI = {
			Uri.parse(EOITConst.SCHEME + AUTHORITY + ManufacturingPlans.PATHS[0]),
			Uri.parse(EOITConst.SCHEME + AUTHORITY + ManufacturingPlans.PATHS[1]),
			Uri.parse(EOITConst.SCHEME + AUTHORITY + ManufacturingPlans.PATHS[2]),
			Uri.parse(EOITConst.SCHEME + AUTHORITY + ManufacturingPlans.PATHS[3]),
			Uri.parse(EOITConst.SCHEME + AUTHORITY + ManufacturingPlans.PATHS[4]),
	};
    public static final Uri SHOPPING_LIST_URI =
            Uri.parse(EOITConst.SCHEME + AUTHORITY + ManufacturingShoppingList.PATH);
	public static final Uri SESSION_URI =
			Uri.parse(EOITConst.SCHEME + AUTHORITY + ManufacturingSession.PATH);

	//--------------------------------
	//----------------- Uri mapping id
	private static final int MANUFACTURING_PLAN_LVL0 = 0;
	private static final int MANUFACTURING_PLAN_LVL1 = 1;
	private static final int MANUFACTURING_PLAN_LVL2 = 2;
	private static final int MANUFACTURING_PLAN_LVL3 = 3;
	private static final int MANUFACTURING_PLAN_LVL4 = 4;
	private static final int MANUFACTURING_SHOPPING_LIST = 5;
	private static final int MANUFACTURING_SESSION = 6;

	//---------------------------------
	//----------------- Projection maps
	private static Map<String, String> projectionMapPlan = new HashMap<String, String>();
	private static Map<String, String> projectionMapShoppingList = new HashMap<String, String>();
	private static Map<String, String> projectionMapSession = new HashMap<String, String>();

	//------------------------------
	//----------------- Static init.
	static {
		sUriMatcher.addURI(AUTHORITY, ManufacturingPlans.TABLE_NAMES[0], MANUFACTURING_PLAN_LVL0);
		sUriMatcher.addURI(AUTHORITY, ManufacturingPlans.TABLE_NAMES[1], MANUFACTURING_PLAN_LVL1);
		sUriMatcher.addURI(AUTHORITY, ManufacturingPlans.TABLE_NAMES[2], MANUFACTURING_PLAN_LVL2);
		sUriMatcher.addURI(AUTHORITY, ManufacturingPlans.TABLE_NAMES[3], MANUFACTURING_PLAN_LVL3);
		sUriMatcher.addURI(AUTHORITY, ManufacturingPlans.TABLE_NAMES[4], MANUFACTURING_PLAN_LVL4);
		sUriMatcher.addURI(AUTHORITY, ManufacturingShoppingList.PATH, MANUFACTURING_SHOPPING_LIST);
		sUriMatcher.addURI(AUTHORITY, ManufacturingSession.PATH, MANUFACTURING_SESSION);

		addProjectionMapping(projectionMapPlan, "", ManufacturingPlans.COLUMNS);
		addProjectionMapping(projectionMapShoppingList, "", ManufacturingShoppingList.COLUMNS);
		addProjectionMapping(projectionMapSession, "", ManufacturingSession.COLUMNS);
	}

	@Override
	public boolean onCreate() {
		super.onCreate();

		registerQueryHandler(MANUFACTURING_PLAN_LVL0,
				new ManufacturingPlanQueryHandler(ManufacturingPlans.TABLE_NAMES[0]));
		registerQueryHandler(MANUFACTURING_PLAN_LVL1,
				new ManufacturingPlanQueryHandler(ManufacturingPlans.TABLE_NAMES[1]));
		registerQueryHandler(MANUFACTURING_PLAN_LVL2,
				new ManufacturingPlanQueryHandler(ManufacturingPlans.TABLE_NAMES[2]));
		registerQueryHandler(MANUFACTURING_PLAN_LVL3,
				new ManufacturingPlanQueryHandler(ManufacturingPlans.TABLE_NAMES[3]));
		registerQueryHandler(MANUFACTURING_PLAN_LVL4,
				new ManufacturingPlanQueryHandler(ManufacturingPlans.TABLE_NAMES[4]));

        registerQueryHandler(MANUFACTURING_SHOPPING_LIST, new QueryHandler() {
            @Override
            public void onQuery(SQLiteQueryBuilder qb, Uri uri) {
                qb.setTables(ManufacturingShoppingList.TABLE_NAME);
                qb.setProjectionMap(projectionMapShoppingList);
            }
        });

        registerQueryHandler(MANUFACTURING_SESSION, new QueryHandler() {
            @Override
            public void onQuery(SQLiteQueryBuilder qb, Uri uri) {
                qb.setTables(ManufacturingSession.TABLE_NAME);
                qb.setProjectionMap(projectionMapSession);
            }
        });

		return true;
	}

	@Override
	protected String getLogTag() {
		return LOG_TAG;
	}

	@Override
	protected String getAuthority() {
		return AUTHORITY;
	}

	private class ManufacturingPlanQueryHandler implements QueryHandler {

		private String tableName;

		private ManufacturingPlanQueryHandler(String tableName) {
			this.tableName = tableName;
		}

		@Override
		public void onQuery(SQLiteQueryBuilder qb, Uri uri) {
			qb.setTables(tableName);
			qb.setProjectionMap(projectionMapPlan);
		}
	}
}
