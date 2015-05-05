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

package fr.piconsoft.eoit.ui;

import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.model.IndustryJobs;
import fr.piconsoft.eoit.provider.IndustryJobsContentProvider;
import fr.piconsoft.eoit.ui.task.UpdateIndustryJobsAsyncTask;

/**
 * @author picon.software
 */
public class IndustryJobsActivity extends LoaderListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Creates the backing adapter for the ListView.
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				this,
				android.R.layout.simple_list_item_1,
				null,
				new String[]{IndustryJobs.END_DATE},
				new int[]{android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

		setListAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();

		ProgressDialog waitDialog = new ProgressDialog(this);
		waitDialog.setCancelable(true);
		waitDialog.setMessage(getString(R.string.retreiving_industry_jobs));
		waitDialog.show();

		UpdateIndustryJobsAsyncTask task = new UpdateIndustryJobsAsyncTask(waitDialog);
		inject(task);
		task.execute();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(this,
				IndustryJobsContentProvider.JOBS_URI,
				new String[]{IndustryJobs._ID, IndustryJobs.END_DATE},
				null, null, null);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	}
}
