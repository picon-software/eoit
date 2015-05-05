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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import fr.piconsoft.eoit.BuildConfig;
import fr.piconsoft.eoit.EOITApplication;
import fr.piconsoft.eoit.ui.task.BasicContextAsyncTask;
import fr.piconsoft.eoit.helper.ContextHelper;
import fr.piconsoft.eoit.model.Character;
import fr.piconsoft.eoit.model.PreferencesName;
import fr.piconsoft.eoit.ui.model.Skills;

/**
 * @author picon.software
 */
public abstract class BaseListActivity extends ListActivity {

	public static final String TAG = "BaseActivity";

	protected boolean loadParametersOnCreate = true;
	private ObjectGraph activityGraph;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create the activity graph by .plus-ing our modules onto the application graph.
		EOITApplication application = (EOITApplication) getApplication();
		activityGraph = application.getApplicationGraph().plus(getModules().toArray());

		Crashlytics.getInstance().setDebugMode(BuildConfig.DEBUG);
		Crashlytics.start(this);

		if (loadParametersOnCreate) {
			new ContextLoaderAsyncTask().execute(this);
		}
	}

	@Override
	protected void onDestroy() {
		// Eagerly clear the reference to the activity graph to allow it to be garbage collected as
		// soon as possible.
		activityGraph = null;

		super.onDestroy();
	}

	/**
	 * A list of modules to use for the individual activity graph. Subclasses can override this
	 * method to provide additional modules provided they call and include the modules returned by
	 * calling {@code super.getModules()}.
	 */
	protected List<Object> getModules() {
		return Arrays.asList();
	}

	/**
	 * Inject the supplied {@code object} using the activity-specific graph.
	 */
	public void inject(Object object) {
		activityGraph.inject(object);
	}

	private class ContextLoaderAsyncTask extends BasicContextAsyncTask<Void, Void> {

		@Override
		protected Void doInBackgroundSingleParam(Context context) {

			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
			String characterId = preferences.getString(PreferencesName.CHARACTER_ID, Character.LEVEL_5_PREF_VALUE);

			Skills.setLevelVChar(Character.LEVEL_5_PREF_VALUE.equals(characterId));
			try {
				ContextHelper.populateSkills(context);
				ContextHelper.populateStationBeans(context, false);
			} catch (SQLiteException e) {
				Crashlytics.logException(e);

				// Redirect to the main activity to update the database
				openActivity(EOITActivity.class);
			}

			return null;
		}
	}

	protected void setActionBarTitle(int titleId) {
		getActionBar().setTitle(titleId);
	}

	protected void setActionBarTitle(CharSequence title) {
		getActionBar().setTitle(title);
	}

	protected void setActionBarSubtitle(int subTitleId) {
		getActionBar().setSubtitle(subTitleId);
	}

	protected void setActionBarSubtitle(CharSequence subTitle) {
		getActionBar().setSubtitle(subTitle);
	}

	@TargetApi(14)
	public void setActionBarIcon(Drawable drawable) {
		getActionBar().setIcon(drawable);
	}

	protected void setActionBarInderterminate(boolean indeterminate) {
		setProgressBarIndeterminate(indeterminate);
		setProgressBarIndeterminateVisibility(indeterminate);
	}

	protected void openActivity(Class<? extends Activity> clazz, Uri uri, Bundle args) {
		final Intent intent = new Intent(this, clazz);

		if (uri != null) {
			intent.setData(uri);
		}
		if (args != null) {
			intent.putExtras(args);
		}

		startActivity(intent);
	}

	protected void openActivity(Class<? extends Activity> clazz, Bundle args) {
		openActivity(clazz, null, args);
	}

	protected void openActivity(Class<? extends Activity> clazz) {
		openActivity(clazz, null);
	}

	/**
	 * Detects and toggles immersive mode (also known as "hidey bar" mode).
	 */
	protected void toggleHideyBar() {
		toggleHideyBar(null);
	}

	/**
	 * Detects and toggles immersive mode (also known as "hidey bar" mode).
	 */
	private void toggleHideyBar(Boolean on) {

		// The UI options currently enabled are represented by a bitfield.
		// getSystemUiVisibility() gives us that bitfield.
		int uiOptions = getWindow().getDecorView().getSystemUiVisibility();
		int newUiOptions = uiOptions;
		boolean isImmersiveModeEnabled =
				((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);

		if (on != null && !on) {
			return;
		}

		if (isImmersiveModeEnabled) {
			Log.i(TAG, "Turning immersive mode mode off. ");
		} else {
			Log.i(TAG, "Turning immersive mode mode on.");
		}

		// Navigation bar hiding:  Backwards compatible to ICS.
		if (Build.VERSION.SDK_INT >= 14) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		}

		// Status bar hiding: Backwards compatible to Jellybean
		if (Build.VERSION.SDK_INT >= 16) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
		}

		// Immersive mode: Backward compatible to KitKat.
		// Note that this flag doesn't do anything by itself, it only augments the behavior
		// of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
		// all three flags are being toggled together.
		// Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
		// Sticky immersive mode differs in that it makes the navigation and status bars
		// semi-transparent, and the UI flag does not get cleared when the user interacts with
		// the screen.
		if (Build.VERSION.SDK_INT >= 18) {
			newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		}

		getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
	}

	protected void showHideyBar() {
		toggleHideyBar(Boolean.TRUE);
	}

	protected void hideHideyBar() {
		toggleHideyBar(Boolean.FALSE);
	}
}
