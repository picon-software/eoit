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

package fr.piconsoft.eoit.db.updater.bean;

/**
 * @author picon.software
 */
public final class SerializableUtils {

	private SerializableUtils() {
	}

	public static String serialize(Object... objs) {
		StringBuilder buff = new StringBuilder();

		for (Object obj : objs) {
			buff.append(obj.toString()).append(SerializableBean.SEPARATOR);
		}

		return buff.toString().substring(0, buff.length() - 1);
	}
}
