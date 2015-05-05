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

import android.content.Context;

import fr.piconsoft.eoit.R;

public class MissingAPITips extends TipsDialog {

    public MissingAPITips() { }

    public MissingAPITips(Context context) {
        super(R.string.missing_api_title, R.layout.missing_api_dialog, R.string.missing_api_action,
                context, "missing_api", 1,
                ApiKeyManagementActivity.class);
    }
}
