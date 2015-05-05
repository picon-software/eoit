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

package fr.piconsoft.eoit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import fr.piconsoft.eoit.R;
import fr.piconsoft.eoit.ui.widget.SlidingTabLayout;

/**
 * @author picon.software
 */
public class ManufactureActivity extends StrictModeActivity {

	/**
	 * Called when the activity is first created.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.manufacture_pager_sliding_layout);

		SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs);
		tabs.setViewPager(mViewPager);
		tabs.setSelectedIndicatorColors(getResources().getColor(android.R.color.white));

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		setActionBarSubtitle(getIntent().getStringExtra("itemName"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.manufacturemenu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.STOCK_OPTION:
				final Intent intent = new Intent(this, StockActivity.class);
				startActivity(intent);
				return true;
			case R.id.PARAMETERS_OPTION:
				final Intent paramIntent = new Intent(this, SettingsActivity.class);
				startActivity(paramIntent);
				return true;

			default:
				return false;
		}
	}

	/**
	 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
	 * sections of the app.
	 */
	private class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = null;

			switch (i) {
				case 0:
					fragment = Fragment.instantiate(ManufactureActivity.this,
							ProductionPlanPremiumFragment.class.getName(),
							getIntent().getExtras());
					break;
				case 1:
					fragment = Fragment.instantiate(ManufactureActivity.this,
							PriceRepartitionPremiumFragment.class.getName(),
							getIntent().getExtras());
					break;

				default:
					break;
			}

			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return "PRODUCTION PLAN";
				case 1:
					return getString(R.string.price_repartition);
			}
			return null;
		}
	}
}
