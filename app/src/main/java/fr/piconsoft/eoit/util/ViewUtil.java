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

package fr.piconsoft.eoit.util;

import android.view.View;

public final class ViewUtil {

	private ViewUtil() {
	}

	public static void show(View view) {
		if (view != null) {
			view.setVisibility(View.VISIBLE);
		}
	}

	public static void show(View view, View view1) {
		show(view);
		show(view1);
	}

	public static void show(View... views) {
		for (View view : views) {
			show(view);
		}
	}

	public static void hide(View view) {
		if (view != null) {
			view.setVisibility(View.GONE);
		}
	}

	public static void hide(View view, View view1) {
		hide(view);
		hide(view1);
	}

	public static void hide(View... views) {
		for (View view : views) {
			hide(view);
		}
	}
}
