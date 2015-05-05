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
package fr.piconsoft.eoit.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author picon.software
 *
 */
public final class QueryBuilderUtil {

	private QueryBuilderUtil() { }

	@SafeVarargs
	public static <T> String buildInClause(String fieldName, T... elements) {
		return buildInClause(fieldName, Arrays.asList(elements));
	}

	public static String buildInClause(String fieldName, Collection<?> elements) {
		StringBuilder sb = new StringBuilder(fieldName).append(" IN (");

		int cpt = 1;
		for(Object element : elements) {
			sb.append(element.toString());
			if(cpt < elements.size()) {
				sb.append(", ");
			}
			cpt++;
		}

		sb.append(")");

		return sb.toString();
	}

	public class Join {

		private String table1 = null;
		private String table2 = null;
		private List<JoinOnClause> joinClauses = new ArrayList<JoinOnClause>();
		private String joinType;

		public Join join(String table2) {
			return join(table2, " JOIN ");
		}

		public Join leftJoin(String table2) {
			return join(table2, " LEFT JOIN ");
		}

		public Join rightJoin(String table2) {
			return join(table2, " RIGHT JOIN ");
		}

		public Join join(String table2, String joinType) {
			if(this.table2 == null) {
				this.table2 = table2;
				this.joinType = joinType;
			} else {
				this.table1 = this.toString();
				this.table2 = table2;
				this.joinType = joinType;
				joinClauses.clear();
			}

			return this;
		}

		public Join on(String onCloseField1, String onCloseField2) {
			JoinOnClause clause = new JoinOnClause();
			clause.onCloseField1 = onCloseField1;
			clause.onCloseField2 = onCloseField2;

			joinClauses.add(clause);

			return this;
		}

		public Join on(String close) {
			CustomJoinOnClause clause = new CustomJoinOnClause();
			clause.close = close;

			joinClauses.add(clause);

			return this;
		}

		public Join andOn(String onCloseField1, String onCloseField2) {
			JoinOnClause clause = new JoinOnClause();
			clause.onCloseField1 = onCloseField1;
			clause.onCloseField2 = onCloseField2;
			clause.requireAnd = true;

			joinClauses.add(clause);

			return this;
		}

		public Join onIn(String onCloseField1, Object... onInCloseField2) {
			JoinOnClause clause = new JoinOnClause();
			clause.onCloseField1 = onCloseField1;
			clause.onCloseField2 = StringUtils.join(onInCloseField2, ",", "(", ")");
			clause.isInClause = true;

			joinClauses.add(clause);

			return this;
		}

		public Join andOnIn(String onCloseField1, Object... onInCloseField2) {
			JoinOnClause clause = new JoinOnClause();
			clause.onCloseField1 = onCloseField1;
			clause.onCloseField2 = StringUtils.join(onInCloseField2, ",", "(", ")");
			clause.requireAnd = true;
			clause.isInClause = true;

			joinClauses.add(clause);

			return this;
		}

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append(table1).append(joinType).append(table2).append(" ON (");

			for(JoinOnClause clause : joinClauses) {
				sb.append(clause.toString());
			}

			sb.append(") ");

			return sb.toString();
		}
	}

	public static Join table(String table1) {
		Join join = new QueryBuilderUtil().new Join();
		join.table1 = table1;

		return join;
	}

	private class JoinOnClause {
		private String onCloseField1 = null;
		private String onCloseField2 = null;
		private boolean requireAnd = false;
		private boolean isInClause = false;

		@Override
		public String toString() {
			return (requireAnd ? " AND " : "") +
					onCloseField1 +
					(isInClause ? " IN " : " = ") +
					onCloseField2;
		}
	}

	private class CustomJoinOnClause extends JoinOnClause {
		private String close = null;

		@Override
		public String toString() {
			return close;
		}
	}
}
