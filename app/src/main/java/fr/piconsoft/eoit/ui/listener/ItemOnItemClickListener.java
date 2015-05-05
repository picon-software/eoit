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

package fr.piconsoft.eoit.ui.listener;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.widget.AdapterView;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.model.Item;
import fr.piconsoft.eoit.ui.ItemInfoActivity;

/**
 * @author picon.software
 */
public class ItemOnItemClickListener implements AdapterView.OnItemClickListener, PieGraph.OnSliceClickedListener {

	private Activity activity;
	private boolean withTransition = true;

	public ItemOnItemClickListener(Activity activity) {
		this.activity = activity;
	}

	public ItemOnItemClickListener(Activity activity, boolean withTransition) {
		this.activity = activity;
		this.withTransition = withTransition;
	}

	@Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		launchIntent(id, view);
    }

	@Override
	public void onClick(PieSlice slice) {
		if(slice.getInternalId() > 0) {
			launchIntent(slice.getInternalId());
		}
	}

	private void launchIntent(long id) {
		final Intent intent = new Intent(activity, ItemInfoActivity.class);
		intent.setData(ContentUris.withAppendedId(Item.CONTENT_ID_URI_BASE, id));

		activity.startActivity(intent);
	}

	private void launchIntent(long id, View view) {
		final Intent intent = new Intent(activity, ItemInfoActivity.class);
		intent.setData(ContentUris.withAppendedId(Item.CONTENT_ID_URI_BASE, id));

		View iconView = view.findViewById(R.id.item_icon);

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && withTransition) {
			ActivityOptionsCompat options =
					ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
							iconView != null ? iconView : view,   // The view which starts the transition
							String.valueOf(id)    // The transitionName of the view we’re transitioning to
					);
			ActivityCompat.startActivity(activity, intent, options.toBundle());
		} else {
			activity.startActivity(intent);
		}
	}
}
