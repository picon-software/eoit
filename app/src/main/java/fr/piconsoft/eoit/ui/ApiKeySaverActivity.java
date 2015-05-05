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

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import fr.eo.api.helper.AccessMaskHelper;
import fr.eo.api.manager.Manager;
import fr.eo.api.model.ApiKeyInfo;
import fr.eo.api.services.AccountService;
import fr.piconsoft.eoit.util.DbUtil;
import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.model.ApiKey;
import fr.piconsoft.eoit.model.Character;
import fr.piconsoft.eoit.model.Corporation;
import fr.piconsoft.eoit.model.PreferencesName;
import fr.piconsoft.eoit.model.Skill;
import fr.piconsoft.eoit.provider.EveOnlineApiContentProvider;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static fr.eo.api.model.ApiKeyInfo.CharacterInfo;
import static fr.piconsoft.eoit.activity.common.util.RetrofitErrorHandler.handle;

/**
 * @author picon.software
 */
public class ApiKeySaverActivity extends LoaderActivity<Cursor> {

	private static final int DB_API_KEY_LOADER_ID = EOITConst.getNextLoaderIdSequence();

	private static final String LOG_TAG = "ApiKeySaverActivity";
	private static final Pattern ALPHANUMERIC = Pattern.compile("[A-Za-z0-9]+");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.api_key_saver);

		setTitle(R.string.api_key_saver_activity_title);

		final Intent intent = getIntent();
		final String action = intent.getAction();

		EditText keyIdEditText = (EditText) findViewById(R.id.key_id);
		EditText vCodeEditText = (EditText) findViewById(R.id.v_code);

		Bundle args = getIntent().getExtras();

		// eve://api.eveonline.com/installKey?keyID=XX&vCode=YY
		if (Intent.ACTION_VIEW.equals(action)) {
			Uri apiUri = intent.getData();
			String keyId = apiUri.getQueryParameter("keyID");
			String vCode = apiUri.getQueryParameter("vCode");

			keyIdEditText.setText(keyId);
			vCodeEditText.setText(vCode);
		} else if (args != null && !args.isEmpty()) {
			// from api update tips
			keyIdEditText.setText(String.valueOf(args.getLong("keyId")));
			vCodeEditText.setText(args.getString("vCode"));
		} else {
			initLoader(DB_API_KEY_LOADER_ID, null);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == DB_API_KEY_LOADER_ID) {
			setActionBarInderterminate(true);

			return new CursorLoader(this, ApiKey.API_KEY_URI, new String[]{ApiKey._ID, ApiKey.V_CODE}, null, null,
					null);
		}

		return null;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (loader.getId() == DB_API_KEY_LOADER_ID && DbUtil.hasAtLeastOneRow(data)) {

			long keyId = data.getLong(data.getColumnIndexOrThrow(ApiKey._ID));
			String vCode = data.getString(data.getColumnIndexOrThrow(ApiKey.V_CODE));

			EditText keyIdEditText = (EditText) findViewById(R.id.key_id);
			EditText vCodeEditText = (EditText) findViewById(R.id.v_code);

			keyIdEditText.setText(Long.toString(keyId));
			vCodeEditText.setText(vCode);

			setActionBarInderterminate(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.refresh_only_menu, menu);

		initProgressActionView(menu.findItem(R.id.refresh_option), true);
		setActionBarInderterminate(false);

		return super.onCreateOptionsMenu(menu);
	}

	private void validateWithApi(final long keyId, final String vCode) {

		AccountService accountService = new Manager().accountService();

		try {
			accountService.apiKeyInfo(keyId, vCode, new Callback<ApiKeyInfo>() {
				@Override
				public void success(ApiKeyInfo apiKeyInfo, Response response) {
					List<CharacterInfo> characterInfos =
							apiKeyInfo.getCharacters();

					CharacterInfo firstCharacter = characterInfos.get(0);

					long accessMask = firstCharacter.accessMask;

					boolean characterListAccessible = AccessMaskHelper.isAccessible(accessMask,
							AccessMaskHelper.CHARACTER_SHEET);
					boolean assetListAccessible = AccessMaskHelper.isAccessible(accessMask, AccessMaskHelper.ASSET_LIST);
					boolean standingAccessible = AccessMaskHelper.isAccessible(accessMask, AccessMaskHelper.STANDINGS);

					setValid(R.id.valid_img1, characterListAccessible);
					setValid(R.id.valid_img2, assetListAccessible);
					setValid(R.id.valid_img3, standingAccessible);

					setActionBarInderterminate(false);

					if (characterListAccessible && assetListAccessible && standingAccessible) {
						new ApiKeySaverAsyncTask(keyId, vCode)
								.execute(characterInfos.toArray(new CharacterInfo[characterInfos.size()]));
					} else {
						((Button) findViewById(android.R.id.button1)).setText(R.string.refresh);
					}
				}

				@Override
				public void failure(RetrofitError error) {
				}
			});
		} catch (RetrofitError e) {
			handle(getApplicationContext(), e);
		} catch (Exception e) {
			Crashlytics.logException(e);
		}
	}

	private void validateFields() {
		EditText keyIdEditText = (EditText) findViewById(R.id.key_id);
		EditText vCodeEditText = (EditText) findViewById(R.id.v_code);

		long keyId;
		String vCode;

		try {
			keyId = Long.valueOf(keyIdEditText.getText().toString());
		} catch (Exception e) {
			keyIdEditText.setError("KeyId is not valid !",
					getApplicationContext().getResources().getDrawable(R.drawable.icon_ko));
			return;
		}

		vCode = vCodeEditText.getText().toString();
		boolean validVCode = true;

		if (!ALPHANUMERIC.matcher(vCode).matches()) {
			validVCode = false;
			vCodeEditText.setError("Only a-z A-Z 0-9 are valid.",
					getApplicationContext().getResources().getDrawable(R.drawable.icon_ko));
		} else if (vCode.length() > 64) {
			validVCode = false;
			vCodeEditText.setError("vCodeEditText too long.",
					getApplicationContext().getResources().getDrawable(R.drawable.icon_ko));
		}

		if (validVCode) {
			setActionBarInderterminate(true);
			validateWithApi(keyId, vCode);
		}
	}

	public void onClickOkBtn(View view) {
		validateFields();
	}

	public void onClickCancelBtn(View view) {
		final Intent paramIntent = new Intent(getApplicationContext(), ApiKeyManagementActivity.class);
		startActivity(paramIntent);
		finish();
	}

	private void setValid(int viewId, boolean valid) {
		if (valid) {
			((ImageView) findViewById(viewId)).setImageResource(R.drawable.icon_ok);
		} else {
			((ImageView) findViewById(viewId)).setImageResource(R.drawable.icon_ko);
		}
	}

	private class ApiKeySaverAsyncTask extends AsyncTask<CharacterInfo, Void, Void> {

		private long keyId;
		private String vCode;

		private ApiKeySaverAsyncTask(long keyId, String vCode) {
			this.keyId = keyId;
			this.vCode = vCode;
		}

		@Override
		protected Void doInBackground(CharacterInfo... characterInfos) {

			CharacterInfo firstCharacter = characterInfos[0];

			ArrayList<ContentProviderOperation> operations = new ArrayList<>();

			operations.add(ContentProviderOperation.newDelete(Skill.SKILL_URI).build());
			operations.add(ContentProviderOperation.newDelete(Character.CHARACTER_URI).build());
			operations.add(ContentProviderOperation.newDelete(Corporation.CORP_URI).build());
			operations.add(ContentProviderOperation.newDelete(ApiKey.API_KEY_URI).build());

			ContentValues apikeyCV = new ContentValues();
			apikeyCV.put(ApiKey._ID, keyId);
			apikeyCV.put(ApiKey.V_CODE, vCode);
			apikeyCV.put(ApiKey.MASK, firstCharacter.accessMask);

			operations.add(ContentProviderOperation.newInsert(ApiKey.API_KEY_URI).withValues(apikeyCV).build());

			for (CharacterInfo characterInfo : characterInfos) {
				ContentValues corpCV = new ContentValues();
				corpCV.put(Corporation.KEY_ID, keyId);
				corpCV.put(Corporation._ID, characterInfo.corporationID);
				corpCV.put(Corporation.NAME, characterInfo.corporationName);

				operations.add(ContentProviderOperation.newInsert(Corporation.CORP_URI).withValues(corpCV).build());

				ContentValues charCV = new ContentValues();
				charCV.put(Character.KEY_ID, keyId);
				charCV.put(Character._ID, characterInfo.characterID);
				charCV.put(Character.CORP_ID, characterInfo.corporationID);
				charCV.put(Character.NAME, characterInfo.characterName);

				operations.add(ContentProviderOperation.newInsert(Character.CHARACTER_URI).withValues(charCV).build());
			}

			try {
				getContentResolver().applyBatch(EveOnlineApiContentProvider.AUTHORITY, operations);
			} catch (RemoteException | OperationApplicationException e) {
				Log.e(LOG_TAG, e.getMessage(), e);
			}

			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
			editor.putString(PreferencesName.CHARACTER_ID, Character.LEVEL_5_PREF_VALUE);
			editor.apply();

			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			setActionBarInderterminate(false);
			Toast.makeText(getApplicationContext(), R.string.key_saved, Toast.LENGTH_LONG).show();

			final Intent paramIntent = new Intent(getApplicationContext(), SettingsActivity.class);
			startActivity(paramIntent);
			finish();
		}
	}

	@SuppressWarnings({"unused", "deprecation"})
	private int readAPIKey() {

		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(new File(EOITConst.APIKEY_FILE_PATH)));

			String line;

			if ((line = bufferedReader.readLine()) != null) {
				String[] lineArray = line.split(":");
                //noinspection StatementWithEmptyBody
                if (lineArray.length == 2) {
//					Parameters.keyId = Long.parseLong(lineArray[0]);
//					Parameters.vCode = lineArray[1].replaceAll("\\s", "");
				} else {
					return R.string.apikey_file_format_error;
				}
			}
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			return R.string.apikey_file_error;
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
			return R.string.apikey_file_format_error;
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e) {
				Log.e(LOG_TAG, e.getMessage(), e);
				//noinspection ReturnInsideFinallyBlock
				return R.string.apikey_file_format_error;
			}
		}

		return 0;
	}
}
