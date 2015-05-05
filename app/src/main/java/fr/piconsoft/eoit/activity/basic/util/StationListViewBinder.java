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

package fr.piconsoft.eoit.activity.basic.util;

import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import fr.piconsoft.eoit.EOITConst;
import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.model.Station;
import fr.piconsoft.eoit.util.IconUtil;
import fr.piconsoft.eoit.ui.listener.FavoriteStationsOnCheckedChangeListener;
import fr.piconsoft.eoit.util.PricesUtil;

/**
 * @author picon.software
 *
 */
public class StationListViewBinder implements SimpleCursorAdapter.ViewBinder {

	private String stationName;
	private NumberFormat nf = new DecimalFormat("+0.00");
	private NumberFormat nfVolume = new DecimalFormat(EOITConst.VALUES_PATTERN);
	private boolean isInPremium;
	private OnStationMenuItemClickListener listener;

	public StationListViewBinder(OnStationMenuItemClickListener listener, boolean isInPremium) {
		this.listener = listener;
		this.isInPremium = isInPremium;
	}

	@Override
	public boolean setViewValue(final View view, Cursor cursor, int columnIndex) {
		int viewId = view.getId();
		boolean isNull = cursor.isNull(columnIndex);

		TextView textView;
		String[] stationNameArray;
		double price;
		long volume;
		final int role, stationId = cursor.getInt(cursor.getColumnIndexOrThrow(Station._ID));

		switch(viewId) {
		case R.id.station_name:
			stationName = cursor.getString(columnIndex);
			textView = (TextView) view;
			stationNameArray = stationName.split(" - ");
			textView.setText(stationNameArray[stationNameArray.length - 1]);
			break;

		case R.id.location_name:
			String regionName = cursor.getString(columnIndex);
			textView = (TextView) view;

			stationNameArray = stationName.split(" - ");
			StringBuilder sb = new StringBuilder(regionName);
			if(stationNameArray.length == 2) {
				sb.append(" > ").append(stationNameArray[0]);
			} else if(stationNameArray.length == 3) {
				sb.append(" > ").append(stationNameArray[0]).append(" - ").append(stationNameArray[1]);
			}

			textView.setText(sb.toString());
			break;

		case R.id.station_icon:
			int id = cursor.getInt(columnIndex);
			IconUtil.initRender(id, (ImageView) view);
			break;

		case R.id.favorite_station:
			final boolean favorite = cursor.getInt(columnIndex) == 1;

			if(view instanceof ImageButton) {
				final ImageButton imageButton = (ImageButton) view;
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						PopupMenu popup = new PopupMenu(imageButton.getContext(), imageButton);
						MenuInflater inflater = popup.getMenuInflater();
						inflater.inflate(R.menu.station_row_premium_menu, popup.getMenu());
						popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
							@Override
							public boolean onMenuItemClick(MenuItem item) {
								return listener != null && listener.onMenuItemClick(stationId, item);
							}
						});
						MenuItem favoriteMenuItem = popup.getMenu().findItem(R.id.favorite_option);
						favoriteMenuItem.setChecked(favorite);
						popup.show();
					}
				});
			}

			if(view instanceof CheckBox) {
				CheckBox favoriteCheckBox = (CheckBox) view;
				favoriteCheckBox.setChecked(favorite);
				favoriteCheckBox.setOnCheckedChangeListener(
						new FavoriteStationsOnCheckedChangeListener(stationId, favoriteCheckBox.getContext()));
			}
			break;

		case R.id.station_prod_icon:
			role = cursor.getInt(columnIndex);

			if((!isInPremium && role == EOITConst.Stations.BOTH_ROLES) ||
					role == EOITConst.Stations.TRADE_ROLE || isNull) {
				view.setVisibility(View.GONE);
			} else {
				view.setVisibility(View.VISIBLE);
			}
			break;

		case R.id.station_trade_icon:
			role = cursor.getInt(columnIndex);

			if((!isInPremium && role == EOITConst.Stations.BOTH_ROLES) ||
					role == EOITConst.Stations.PRODUCTION_ROLE || isNull) {
				view.setVisibility(View.GONE);
			} else {
				view.setVisibility(View.VISIBLE);
			}
			break;

		case R.id.station_both_icon:
			role = cursor.getInt(columnIndex);

			if(isInPremium && role == EOITConst.Stations.BOTH_ROLES && !isNull) {
				view.setVisibility(View.VISIBLE);
			} else {
				view.setVisibility(View.GONE);
			}
			break;

		case R.id.corp_standing:
			float standing = cursor.getFloat(columnIndex);
			textView = (TextView) view;
			textView.setText(nf.format(standing));
			if(standing > 0) {
				textView.setTextColor(view.getResources().getColor(isInPremium ? R.color.green_premium : R.color.green));
			} else if(standing == 0) {
				textView.setTextColor(view.getResources().getColor(R.color.grey));
			} else if(standing < 0) {
				textView.setTextColor(view.getResources().getColor(isInPremium ? R.color.red_premium : R.color.red));
			}

			break;

		case R.id.buy_price:
		case R.id.sell_price:
			price = cursor.getDouble(columnIndex);
			textView = (TextView) view;
			PricesUtil.setPrice(textView, price, true);
			break;

		case R.id.buy_volume:
		case R.id.sell_volume:
			volume = cursor.getLong(columnIndex);
			textView = (TextView) view;
			textView.setText(nfVolume.format(volume));
			break;

		default:
			throw new IllegalArgumentException("viewId : " + viewId);
		}

		return true;
	}

	public interface OnStationMenuItemClickListener {
		boolean onMenuItemClick(int stationId, MenuItem item);
	}
}
