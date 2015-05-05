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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author picon.software
 *
 */
public final class ValueCache {

	private static Map<Value, Object> cache = new HashMap<Value, Object>();

	private ValueCache() { }

	private class Value {
		private short beanTypeId;
		private long beanId;
		private short fieldId;

		public Value(short beanTypeId, long beanId, short fieldId) {
			super();
			this.beanTypeId = beanTypeId;
			this.beanId = beanId;
			this.fieldId = fieldId;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (beanId ^ (beanId >>> 32));
			result = prime * result + beanTypeId;
			result = prime * result + fieldId;
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof Value)) { return false; }

			Value v = (Value) obj;

			return this.beanId == v.beanId && this.beanTypeId == v.beanTypeId && this.fieldId == v.fieldId;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Value [beanTypeId=" + beanTypeId + ", beanId=" + beanId
					+ ", fieldId=" + fieldId + "]";
		}
	}

	public static boolean hasChangedAndUpdateCache(short beanTypeId, short fieldId, long beanId, Object newValue) {
		Value v = new ValueCache().new Value(beanTypeId, beanId, fieldId);

		boolean hasChanged = !cache.containsKey(v) || (
				cache.containsKey(v) && !cache.get(v).equals(newValue));

		cache.put(v, newValue);

		return hasChanged;
	}

	/**
	 *
	 * @see java.util.Map#clear()
	 */
	public static void clear() {
		cache.clear();
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public static boolean containsKey(short beanTypeId, short fieldId, long beanId) {
		return cache.containsKey(new ValueCache().new Value(beanTypeId, beanId, fieldId));
	}

	/**
	 * @return
	 * @see java.util.Map#entrySet()
	 */
	public static Set<Entry<Value, Object>> entrySet() {
		return cache.entrySet();
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public static Object get(short beanTypeId, short fieldId, long beanId) {
		return cache.get(new ValueCache().new Value(beanTypeId, beanId, fieldId));
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	public static Object put(short beanTypeId, short fieldId, long beanId, Object value) {
		return cache.put(new ValueCache().new Value(beanTypeId, beanId, fieldId), value);
	}

	/**
	 * @return
	 * @see java.util.Map#size()
	 */
	public static int size() {
		return cache.size();
	}
}
