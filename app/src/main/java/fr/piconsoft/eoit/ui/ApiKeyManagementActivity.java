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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.listener.GenericIntentLauncherOnClickListener;

public class ApiKeyManagementActivity extends StrictModeActivity {

	private final static Uri CREATE_PREDIFINED_KEY_URL = Uri.parse("https://community.eveonline.com/support/api-key/CreatePredefined?accessMask=524298");
	private final static Uri INSTALL_KEY_URL = Uri.parse("https://community.eveonline.com/support/api-key/ActivateInstallLinks?activate=true");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.api_key_management);

		Button createKeyButton = (Button) findViewById(R.id.CREATE_KEY_BTN);
		createKeyButton.setOnClickListener(
				new GenericIntentLauncherOnClickListener(
						new Intent(Intent.ACTION_VIEW, CREATE_PREDIFINED_KEY_URL)));

		Button installKeyButton = (Button) findViewById(R.id.INSTALL_KEY_BTN);
		installKeyButton.setOnClickListener(
				new GenericIntentLauncherOnClickListener(
						new Intent(Intent.ACTION_VIEW, INSTALL_KEY_URL)));

		Button manualKeyButton = (Button) findViewById(R.id.MANUAL_KEY_BTN);
		manualKeyButton.setOnClickListener(
				new GenericIntentLauncherOnClickListener(
						new Intent(getApplicationContext(), ApiKeySaverActivity.class)));
	}
}
