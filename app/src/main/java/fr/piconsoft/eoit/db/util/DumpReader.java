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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.List;

import fr.piconsoft.eoit.db.DumpFilesDescriptor;
import fr.piconsoft.eoit.EOITConst;

/**
 * @author picon.software
 */
public class DumpReader {

	private static final String LOG_TAG = DumpReader.class.getSimpleName();

	private static final String WORKAROUND_SQL_FILENAME = "raw/dump_workaround.sql";
	private static final String DUMP_STRUCTURE_IDX_SQL_FILENAME = "raw/dump_structure_bdd_idx.sql";

	private Context context;
	private int dumpFileCpt = 0;
	private String currentLine;
	private BufferedReader reader;
	public DumpFilesDescriptor descriptor;

	@SuppressWarnings("unchecked")
	public DumpReader(Context context) {
		super();
		this.context = context;

		try {
			ObjectInputStream ois = new ObjectInputStream(context.getAssets().open(EOITConst.DUMP_FILE_DESCRIPTOR_NAME));
			descriptor = new DumpFilesDescriptor();
			descriptor.lineNumbers = (Integer) ois.readObject();
			descriptor.dumpFileNames = (List<String>) ois.readObject();

			//add the indexes definitions
			descriptor.dumpFileNames.add(DUMP_STRUCTURE_IDX_SQL_FILENAME);

			//add the workaround sql requests file
			descriptor.dumpFileNames.add(WORKAROUND_SQL_FILENAME);
		} catch (IOException | ClassNotFoundException e) {
			Log.e(LOG_TAG, e.getLocalizedMessage(), e);
		}
	}

	public boolean hasNextLine() throws IOException {

		if (reader == null) {
			Log.v(LOG_TAG, "Starting to read dump : " + descriptor.dumpFileNames.get(dumpFileCpt));
			InputStream is = context.getAssets().open(descriptor.dumpFileNames.get(dumpFileCpt));
			reader = new BufferedReader(new InputStreamReader(is));
		}

		while ((currentLine = reader.readLine()) != null) {
			currentLine = currentLine.trim();
			if (currentLine.length() != 0 && !currentLine.startsWith("--")) {
				return true;
			}
		}

		if (dumpFileCpt < descriptor.dumpFileNames.size() - 1) {
			Log.v(LOG_TAG, "Closing dump file : " + descriptor.dumpFileNames.get(dumpFileCpt));
			dumpFileCpt++;
			reader.close();
			reader = null;

			return hasNextLine();
		} else {
			reader.close();
			reader = null;

			return false;
		}

	}

	public String nextLine() {
		return currentLine;
	}

}
