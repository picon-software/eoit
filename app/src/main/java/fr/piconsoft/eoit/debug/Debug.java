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

package fr.piconsoft.eoit.debug;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * @author picon.software
 */
public final class Debug {

	private static final String LOG_TAG = "Debug";

	private Debug() {
	}

	public static void backupDatabase(final String packageName, final String databaseName, final String destDir) {

		try {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();

			if (sd.canWrite()) {
				String currentDBPath = "//data//" + packageName + "//databases//" + databaseName;
				String backupDBPath = destDir + "//" + databaseName;
				File currentDB = new File(data, currentDBPath);
				File backupDB = new File(sd, backupDBPath);

				if (currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB).getChannel();
					FileChannel dst = new FileOutputStream(backupDB).getChannel();
					Log.d(LOG_TAG, "Dumping ...");
					dst.transferFrom(src, 0, src.size());
					Log.d(LOG_TAG, "Done.");
					src.close();
					dst.close();
				} else {
					Log.e(LOG_TAG, "Current DB incorrect.");
				}
			} else {
				Log.e(LOG_TAG, "Sd can't be writen.");
			}
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getLocalizedMessage(), e);
		}
	}
}
