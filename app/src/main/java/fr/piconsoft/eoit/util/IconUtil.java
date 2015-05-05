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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.model.PreferencesName;

import static java.net.URLEncoder.encode;

/**
 * @author picon.software
 */
public final class IconUtil {

	// 24h cache.
	private static final int REFRESH_TIME_SECONDS = 60 * 60 * 6;

	private IconUtil() {
	}

	@SuppressWarnings("deprecation")
	private static String getCachedUrl(String url) {
		return "https://images1-focus-opensocial.googleusercontent.com/gadgets/proxy?container=focus&url=" + encode(url) + "&refresh=" + REFRESH_TIME_SECONDS;
	}

	public static String getImageUrl(final long itemId, Context context) {
		String url = EOITConst.IMAGE_SERVER_PATH_INVENTORY_TYPE + itemId + "_" + iconSize(
				context) + ".png";
		return getCachedUrl(url);
	}

	public static String getCharImageUrl(final long charId) {
		String url = EOITConst.IMAGE_SERVER_PATH_CHARACTER + charId + "_128.jpg";
		return getCachedUrl(url);
	}

	public static void initIcon(final long itemId, ImageView imageView) {
		Context context = imageView.getContext();

		setTransitionName(itemId, imageView);

		Picasso.with(context).load(getImageUrl(itemId, context)).into(imageView);
	}

	public static void initCharImage(final long charId, ImageView imageView) {
		Context context = imageView.getContext();

		Picasso.with(context).load(getCharImageUrl(charId)).into(imageView);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private static void setTransitionName(final long itemId, ImageView imageView) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			imageView.setTransitionName(String.valueOf(itemId));
		}
	}

	public static void initRender(final long itemId, ImageView imageView) {
		String url = EOITConst.IMAGE_SERVER_PATH_RENDER + itemId + "_" + renderSize(imageView.getContext()) + ".png";
		Picasso.with(imageView.getContext()).load(getCachedUrl(url))
				.placeholder(imageView.getDrawable()).into(imageView);
	}

	public static void initIconOrRender(final int itemId, final int categoryId, final int groupId,
										final ImageView imageView) {
		if (isRender(categoryId, groupId)) {
			IconUtil.initRender(itemId, imageView);
		} else {
			IconUtil.initIcon(itemId, imageView);
		}
	}

	public static boolean isRender(final int categoryId, final int groupId) {
		return isRenderCategory(categoryId) || isRenderGroup(groupId);
	}

	private static boolean isRenderCategory(final int categoryId) {
		return categoryId == EOITConst.Categories.SHIP_CATEGORIE_ID ||
				categoryId == EOITConst.Categories.DRONE_CATEGORIE_ID ||
				categoryId == EOITConst.Categories.STRUCTURE_CATEGORIE_ID ||
				categoryId == EOITConst.Categories.APPAREL_CATEGORIE_ID;
	}

	private static boolean isRenderGroup(final int groupId) {
		List<Integer> missileLauncherGroupIds = Arrays.asList(EOITConst.Groups.MISSILE_LAUNCHER_GROUP_IDS);
		List<Integer> turretsGroupIds = Arrays.asList(EOITConst.Groups.TURRETS_GROUP_IDS);
		//List<Integer> missileGroupIds = Arrays.asList(EOITConst.Groups.MISSILE_GROUP_IDS);

		return turretsGroupIds.contains(groupId) || missileLauncherGroupIds.contains(groupId) /*||
				missileGroupIds.contains(groupId)*/;
	}

	private static String iconSize(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getString(PreferencesName.ICON_SIZE, "64");
	}

	private static String renderSize(Context context) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getString(PreferencesName.RENDER_SIZE, "256");
	}
}
