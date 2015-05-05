/*
 * Copyright (C) 2012 Picon software
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

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.piconsoft.eoit.model.ColumnsNames.BaseColumns;
import fr.piconsoft.eoit.model.ColumnsNames.BaseColumnsWithName;


/**
 * @author picon.software
 */
public final class ProjectionMapBuilder {

    private ProjectionMapBuilder() { }

    public static Map<String, String> buildBasicProjections(Class<?> fromClass, String tableAlias, boolean withSuperClass) {
        Map<String, String> projections = new HashMap<String, String>();

		if(getInterfaces(fromClass).contains(BaseColumns.class) && withSuperClass) {
			projections.putAll(buildBasicProjections(BaseColumns.class, tableAlias, false));
		}

		if(getInterfaces(fromClass).contains(BaseColumnsWithName.class) && withSuperClass) {
			projections.putAll(buildBasicProjections(BaseColumnsWithName.class, tableAlias, withSuperClass));
		}

		for(Field field : fromClass.getDeclaredFields()) {
			String fieldValue = null;
			try {
				fieldValue = field.get(null).toString();
			} catch (IllegalAccessException e) { }

			if(!field.getName().equals("TABLE_NAME")) {
				projections.put(fieldValue, tableAlias + "." + fieldValue);
			}
        }

        return projections;
    }

	private static List<Class<?>> getInterfaces(Class<?> fromClass) {
		return Arrays.asList(fromClass.getInterfaces());
	}
}
