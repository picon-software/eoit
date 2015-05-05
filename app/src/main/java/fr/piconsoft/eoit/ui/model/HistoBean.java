/*
 * Copyright (C) 2014 Picon software
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

package fr.piconsoft.eoit.ui.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author picon.software
 */
public class HistoBean {

	private String userId;
	public List<HistoByDate> histos;
	private Map<String, Map<Integer, Integer>> histoMap;

	public HistoBean() {
	}

	public HistoBean(String userId, Map<String, Map<Integer, Integer>> histoMap) {
		this.userId = userId;
		this.histoMap = histoMap;
	}

	public String getUserId() {
		return userId;
	}

	public Map<String, Map<Integer, Integer>> getHistoMap() {
		return histoMap;
	}

	public String toJson() throws JSONException {

		JSONObject object = new JSONObject();
		object.put("uId", userId);

		JSONArray jsonArr = new JSONArray();
		for (Map.Entry<String, Map<Integer, Integer>> entry : histoMap.entrySet()) {
			JSONObject entryObject = new JSONObject();
			JSONArray jsonSubArr = new JSONArray();
			Map<Integer, Integer> subMap = entry.getValue();
			for (Map.Entry<Integer, Integer> subEntry : subMap.entrySet()) {
				JSONObject entrySubObject = new JSONObject();
				int itemId = subEntry.getKey();
				int number = subEntry.getValue();
				entrySubObject.put(String.valueOf(itemId), number);
				jsonSubArr.put(entrySubObject);
			}

			entryObject.put(entry.getKey(), jsonSubArr);
			jsonArr.put(entryObject);
		}
		object.put("m", jsonArr);

		return object.toString();
	}

	public static HistoBean fromJson(String json) throws JSONException {
		HistoBean bean = new HistoBean();

		JSONObject jsonObject = new JSONObject(json);

		bean.userId = jsonObject.getString("uId");
		JSONArray map = jsonObject.getJSONArray("m");

		bean.histos = new ArrayList<HistoByDate>();
		for (int i = 0; i < map.length(); i++) {
			HistoByDate histoByDate = new HistoByDate();
			JSONObject object = map.getJSONObject(i);
			histoByDate.date = object.names().getString(0);
			histoByDate.counts = new ArrayList<ItemCount>();

			JSONArray itemsCounts = object.getJSONArray(histoByDate.date);
			for (int j = 0; j < itemsCounts.length(); j++) {
				JSONObject itemEntry = itemsCounts.getJSONObject(j);
				ItemCount itemCount = new ItemCount();
				String itemId = itemEntry.names().getString(0);
				itemCount.count = itemEntry.getInt(itemId);
				itemCount.itemId = Integer.parseInt(itemId);
				histoByDate.counts.add(itemCount);
			}

			bean.histos.add(histoByDate);
		}

		return bean;
	}

	public static class HistoByDate {
		public String date;
		public List<ItemCount> counts;
	}

	public static class ItemCount {
		public int itemId;
		public int count;
	}
}
