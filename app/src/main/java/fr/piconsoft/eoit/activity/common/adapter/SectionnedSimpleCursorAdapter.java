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

package fr.piconsoft.eoit.activity.common.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AlphabetIndexer;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.model.ColumnsNames;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.ui.widget.AmazingListView;
import fr.piconsoft.eoit.activity.basic.util.CategoryIndexer;

/**
 * @author picon.software
 */
public class SectionnedSimpleCursorAdapter extends SimpleCursorAdapter implements SectionIndexer, OnScrollListener {
	public static final String TAG = SectionnedSimpleCursorAdapter.class.getSimpleName();
	private static final int INDEXER_LINE_THRESHOLD = 1;

	public interface HasMorePagesListener {
		void noMorePages();

		void mayHaveMorePages();
	}

	public enum OrderType {
		ID, NAME_ALPHA, CATEGORY
	}

	/**
	 * The <em>current</em> page, not the page that is going to be loaded.
	 */
	int page = 1;
	int initialPage = 1;
	boolean automaticNextPageLoading = false;
	private HasMorePagesListener hasMorePagesListener;
	private SectionIndexer defaultIndexer;
	private String alphaColumnName;
	private OrderType orderType = OrderType.CATEGORY;

	public void setHasMorePagesListener(HasMorePagesListener hasMorePagesListener) {
		this.hasMorePagesListener = hasMorePagesListener;
	}

	/**
	 * Pinned header state: don't show the header.
	 */
	public static final int PINNED_HEADER_GONE = 0;

	/**
	 * Pinned header state: show the header at the top of the list.
	 */
	public static final int PINNED_HEADER_VISIBLE = 1;

	/**
	 * Pinned header state: show the header. If the header extends beyond
	 * the bottom of the first shown element, push it up and clip.
	 */
	public static final int PINNED_HEADER_PUSHED_UP = 2;

	public SectionnedSimpleCursorAdapter(Context context, int layout, String[] from, int[] to) {
		super(context, layout, null, from, to, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		this.alphaColumnName = Item.NAME;
	}

	public SectionnedSimpleCursorAdapter(Context context, int layout, String[] from, int[] to,
										 String alphaColumnName) {
		super(context, layout, null, from, to, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		this.alphaColumnName = alphaColumnName;
	}

	/**
	 * Computes the desired state of the pinned header for the given
	 * position of the first visible list item. Allowed return values are
	 * {@link #PINNED_HEADER_GONE}, {@link #PINNED_HEADER_VISIBLE} or
	 * {@link #PINNED_HEADER_PUSHED_UP}.
	 */
	public int getPinnedHeaderState(int position) {
		if (position < 0 || getCount() == 0 || defaultIndexer == null) {
			return PINNED_HEADER_GONE;
		}

		// The header should get pushed up if the top item shown
		// is the last item in a section for a particular letter.
		int section = getSectionForPosition(position);
		int nextSectionPosition = getPositionForSection(section + 1);
		if (nextSectionPosition != -1 && position == nextSectionPosition - 1) {
			return PINNED_HEADER_PUSHED_UP;
		}

		return PINNED_HEADER_VISIBLE;
	}

	/**
	 * Sets the initial page when {@link #resetPage()} is called.
	 * Default is 1 (for APIs with 1-based page number).
	 */
	public void setInitialPage(int initialPage) {
		this.initialPage = initialPage;
	}

	/**
	 * Resets the current page to the page specified in {@link #setInitialPage(int)}.
	 */
	public void resetPage() {
		this.page = this.initialPage;
	}

	/**
	 * Increases the current page number.
	 */
	public void nextPage() {
		this.page++;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (view instanceof AmazingListView) {
			((AmazingListView) view).configureHeaderView(firstVisibleItem);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// nop
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		View res = super.getView(position, convertView, parent);

		if (position == getCount() - 1 && automaticNextPageLoading) {
			onNextPageRequested(page + 1);
		}

		final int section = getSectionForPosition(position);
		boolean displaySectionHeaders = (getPositionForSection(section) == position);

		bindSectionHeader(res, position, displaySectionHeaders);

		return res;
	}

	@Override
	public void changeCursor(Cursor c) {
		// Create our indexer
		if (c != null && c.getCount() > INDEXER_LINE_THRESHOLD) {
			switch (orderType) {
				case NAME_ALPHA:
					defaultIndexer = new AlphabetIndexer(c, c.getColumnIndexOrThrow(alphaColumnName),
							" 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
					break;

				case CATEGORY:
					defaultIndexer = new CategoryIndexer(c);
					break;

				default:
					break;
			}
		} else {
			defaultIndexer = null;
		}

		super.changeCursor(c);
	}

	public void notifyNoMorePages() {
		automaticNextPageLoading = false;
		if (hasMorePagesListener != null) hasMorePagesListener.noMorePages();
	}

	public void notifyMayHaveMorePages() {
		automaticNextPageLoading = true;
		if (hasMorePagesListener != null) hasMorePagesListener.mayHaveMorePages();
	}

	/**
	 * The last item on the list is requested to be seen, so do the request
	 *
	 * @param page the page number to load.
	 */
	protected void onNextPageRequested(int page) {
	}

	/**
	 * Configure the view (a listview item) to display headers or not based on displaySectionHeader
	 * (e.g. if displaySectionHeader header.setVisibility(VISIBLE) else header.setVisibility(GONE)).
	 */
	protected void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
		if (displaySectionHeader && defaultIndexer != null) {
			((LinearLayout) view.findViewById(R.id.header).getParent()).setVisibility(View.VISIBLE);
			TextView lSectionTitle = (TextView) view.findViewById(R.id.header);
			((LinearLayout) lSectionTitle.getParent()).setBackgroundColor(0x222222);
			lSectionTitle.setText(String.valueOf(getSections()[getSectionForPosition(position)]));
			if(position == 0 && view.findViewById(R.id.separator) != null) {
				view.findViewById(R.id.separator).setVisibility(View.GONE);
			}
		} else {
			((LinearLayout) view.findViewById(R.id.header).getParent()).setVisibility(View.GONE);
		}
	}

	/**
	 * Configures the pinned header view to match the first visible list item.
	 *
	 * @param header   pinned header view.
	 * @param position position of the first visible list item.
	 * @param alpha    fading of the header view, between 0 and 255.
	 */
	public void configurePinnedHeader(View header, int position, int alpha) {
		if (defaultIndexer != null) {
			TextView lSectionHeader = (TextView) header.findViewById(R.id.header);
			lSectionHeader.setText(String.valueOf(getSections()[getSectionForPosition(position)]));
		}
	}

	@Override
	public int getPositionForSection(int section) {
		if (defaultIndexer != null) {
			return defaultIndexer.getPositionForSection(section); //use the indexer
		}
		return 0;
	}

	@Override
	public int getSectionForPosition(int position) {
		if (defaultIndexer != null) {
			return defaultIndexer.getSectionForPosition(position); //use the indexer
		}
		return 0;
	}

	@Override
	public Object[] getSections() {
		if (defaultIndexer != null) {
			return defaultIndexer.getSections(); //use the indexer
		}
		return new Object[]{};
	}

	/**
	 * @return the orderType
	 */
	public OrderType getOrderType() {
		return orderType;
	}

	/**
	 * @param orderType the orderType to set
	 */
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public static String getOrderByQuery(OrderType orderType) {
		switch (orderType) {
			case NAME_ALPHA:
				return Item.NAME;
			case CATEGORY:
				return ColumnsNames.Categories.COLUMN_NAME_NAME_ALIAS;
			case ID:
				return Item._ID;

			default:
				return null;
		}
	}
}