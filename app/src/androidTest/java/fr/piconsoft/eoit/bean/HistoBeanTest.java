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

package fr.piconsoft.eoit.bean;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.TreeMap;

import fr.piconsoft.eoit.ui.model.HistoBean;

/**
 * @author picon.software
 */
public class HistoBeanTest {

	private static final String JSON = "{\"uId\":\"fqsdfqds1f65q1sfqsd32f1qs3fd1\",\"m\":[{\"20141102\":[{\"12\":1}," +
			"{\"15\":3},{\"16\":1}]},{\"20141103\":[{\"15\":1},{\"16\":1},{\"18\":5}]},{\"20141104\":[{\"18\":1}]}]}";

	@Test
	public void testSerialize() {

		String uId = "fqsdfqds1f65q1sfqsd32f1qs3fd1";
		Map<String, Map<Integer, Integer>> histoMap = new TreeMap<>();

		histoMap.put("20141102", new TreeMap<Integer, Integer>());
		histoMap.put("20141103", new TreeMap<Integer, Integer>());
		histoMap.put("20141104", new TreeMap<Integer, Integer>());

		histoMap.get("20141102").put(12,1);
		histoMap.get("20141102").put(15,3);
		histoMap.get("20141102").put(16,1);

		histoMap.get("20141103").put(16,1);
		histoMap.get("20141103").put(18,5);
		histoMap.get("20141103").put(15,1);

		histoMap.get("20141104").put(18,1);

		HistoBean bean = new HistoBean(uId, histoMap);

		try {
			System.out.println(bean.toJson());
		} catch (JSONException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testUnserialize() {
		try {
			HistoBean bean = HistoBean.fromJson(JSON);

			Assert.assertNotNull(bean);
		} catch (JSONException e) {
			Assert.fail(e.getMessage());
		}
	}
}
