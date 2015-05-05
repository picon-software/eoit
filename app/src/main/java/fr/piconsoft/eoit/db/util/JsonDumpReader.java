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

package fr.piconsoft.eoit.db.util;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.db.util.bean.Dumpable;
import fr.piconsoft.eoit.db.util.bean.JsonTableDump;
import fr.piconsoft.eoit.db.util.bean.SqlRequestsDump;

/**
 * @author picon.software
 */
public class JsonDumpReader {

	private static final String LOG_TAG = JsonDumpReader.class.getSimpleName();

	private static final Integer[] JSON_DUMP_TABLES = {
			R.raw.dump_categories,
			R.raw.dump_groups,
			R.raw.dump_items,
			R.raw.dump_regions,
			R.raw.dump_solar_systems,
			R.raw.dump_stations,
			R.raw.dump_planet_schematics,
			R.raw.dump_planet_schematics_type_map,
			R.raw.dump_reaction_materials,
			R.raw.dump_refine_materials,
			R.raw.dump_blueprint,
			R.raw.dump_blueprint_activity,
			R.raw.dump_blueprint_material,
			R.raw.dump_blueprint_product,
			R.raw.dump_blueprint_skill,
			R.raw.dump_translation_key,
			R.raw.dump_translation
	};

	private Context context;

	private List<LazyDumpable> dumpables = new ArrayList<>();
	private Iterator<LazyDumpable> dumpableIterator;
	private LazyDumpable currentDumpable;

	public JsonDumpReader(Context context) throws IOException {
		super();
		this.context = context;

		dumpables.add(new LazyDumpable(R.raw.dump_structure_bdd, false));
		dumpables.add(new LazyDumpable(R.raw.dump_structure_bdd_views, false));

		for (int jsonDumpTable : JSON_DUMP_TABLES) {
			dumpables.add(new LazyDumpable(jsonDumpTable, true));
		}

		dumpables.add(new LazyDumpable(R.raw.dump_structure_bdd_idx, false));
		dumpables.add(new LazyDumpable(R.raw.dump_workaround, false));
		dumpableIterator = dumpables.iterator();
		currentDumpable = dumpableIterator.next();
	}

	public JsonDumpReader(Context context, int... dumpResIds) throws IOException {
		super();
		this.context = context;

		for (int dumpResId : dumpResIds) {
			if(Arrays.asList(JSON_DUMP_TABLES).contains(dumpResId)) {
				dumpables.add(new LazyDumpable(dumpResId, true));
			} else {
				dumpables.add(new LazyDumpable(dumpResId, false));
			}
		}

		dumpableIterator = dumpables.iterator();
		currentDumpable = dumpableIterator.next();
	}

	public boolean hasNextLine() throws IOException {
		if (!currentDumpable.get().hasNext() && !dumpableIterator.hasNext()) {
			return false;
		}
		if (!currentDumpable.get().hasNext() && dumpableIterator.hasNext()) {
			currentDumpable.clear();
			currentDumpable = dumpableIterator.next();
			if (currentDumpable.get() instanceof JsonTableDump) {
				Log.v(LOG_TAG, "Starting to read dump : " + ((JsonTableDump) currentDumpable.get()).name);
			}
		}

		return currentDumpable.get().hasNext();
	}

	public String nextLine() {
		return currentDumpable.get().nextLine();
	}

	public int getLineCount() {
//		FIXME
//		int count = 0;
//		for (Dumpable dumpable : dumpables) {
//			count += dumpable.lineCount();
//		}

		return 182943;
	}

	private SqlRequestsDump loadSqlRequestDump(int rawResourceId) throws IOException {
		InputStream is = context.getResources().openRawResource(rawResourceId);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		SqlRequestsDump dump = new SqlRequestsDump();

		try {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() != 0 && !line.startsWith("--")) {
					dump.addLine(line);
				}
			}
			return dump;
		} finally {
			br.close();
		}
	}

	private JsonTableDump loadJsonTableDump(int rawResourceId) throws IOException {
		InputStream is = context.getResources().openRawResource(rawResourceId);
		Gson gson = new GsonBuilder().create();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			return gson.fromJson(br, JsonTableDump.class);
		} finally {
			br.close();
		}
	}

	private class LazyDumpable {
		private int resourceId;
		private boolean isJson;
		private Dumpable instance;

		private LazyDumpable(int resourceId, boolean isJson) {
			this.resourceId = resourceId;
			this.isJson = isJson;
		}

		private Dumpable get() {
			if (instance == null) {
				try {
					if (isJson) {
						instance = loadJsonTableDump(resourceId);
					} else {
						instance = loadSqlRequestDump(resourceId);
					}
				} catch (IOException e) {
					instance = null;
				}
			}

			return instance;
		}

		private void clear() {
			instance = null;
		}
	}
}
