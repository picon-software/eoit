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

/**
 *
 */
package fr.piconsoft.eoit.ui.task;

import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.os.RemoteException;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import javax.inject.Inject;

import fr.eo.api.credential.CharacterCredential;
import fr.eo.api.model.Jobs;
import fr.eo.api.services.CharacterService;
import fr.eo.api.services.wraper.ServiceWraper;
import fr.piconsoft.eoit.model.IndustryJobs;
import fr.piconsoft.eoit.provider.IndustryJobsContentProvider;

import static fr.piconsoft.eoit.util.DbUtil.formatDate;

public class UpdateIndustryJobsAsyncTask extends EveApiProcessWithCredentialAsyncTask {

	@Inject protected ServiceWraper serviceWraper;
	@Inject protected CharacterService characterService;

	public UpdateIndustryJobsAsyncTask(Dialog dialog) {
		super(dialog);
	}

	@Override
	protected void doInBackground(final CharacterCredential characterCredential) {
		Jobs jobs = serviceWraper.invoke(new ServiceWraper.Callable<Jobs>() {
			@Override
			public String cacheKey() {
				return "IndustryJobs" + characterCredential.getKey();
			}

			@Override
			public Jobs call() {
				return characterService.industryJobs(
						characterCredential.keyId, characterCredential.vCode,
						characterCredential.characterId);
			}
		});

		ArrayList<ContentProviderOperation> operations = new ArrayList<>();

		operations.add(ContentProviderOperation
				.newDelete(IndustryJobsContentProvider.JOBS_URI)
				.build());

		for (Jobs.Job job : jobs.getJobs()) {
			ContentValues values = new ContentValues();
			values.put(IndustryJobs._ID, job.jobID);
			values.put(IndustryJobs.ACTIVITY_ID, job.activityID);
			values.put(IndustryJobs.BLUEPRINT_ID, job.blueprintTypeID);
			values.put(IndustryJobs.END_DATE, formatDate(job.endDate));
			values.put(IndustryJobs.FACILITY_ID, job.facilityID);
			values.put(IndustryJobs.SOLARSYSTEM_ID, job.solarSystemID);
			values.put(IndustryJobs.START_DATE, formatDate(job.startDate));
			values.put(IndustryJobs.STATION_ID, job.stationID);
			operations.add(ContentProviderOperation
					.newInsert(IndustryJobsContentProvider.JOBS_URI)
					.withValues(values)
					.build());
		}

		try {
			context.getContentResolver()
					.applyBatch(IndustryJobsContentProvider.AUTHORITY, operations);
		} catch (RemoteException | OperationApplicationException e) {
			Crashlytics.logException(e);
		}
	}
}
