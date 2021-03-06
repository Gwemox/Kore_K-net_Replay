/*
 * Copyright 2015 Synced Synapse. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thibault01.knet_replay_kore.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import com.thibault01.knet_replay_kore.R;
import com.thibault01.knet_replay_kore.utils.LogUtils;
import com.thibault01.knet_replay_kore.utils.TabsAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Container for the various PVR lists
 */
public class PVRListFragment extends Fragment
        implements OnBackPressedListener {

    private static final String TAG = LogUtils.makeLogTag(PVRListFragment.class);

    private TabsAdapter tabsAdapter;

    @InjectView(R.id.pager_tab_strip) PagerSlidingTabStrip pagerTabStrip;
    @InjectView(R.id.pager) ViewPager viewPager;

    public static final String PVR_LIST_TYPE_KEY = "pvr_list_type_key";
    public static final int LIST_TV_CHANNELS = 0,
            LIST_RADIO_CHANNELS = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_default_view_pager, container, false);
        ButterKnife.inject(this, root);

        Bundle tvArgs = new Bundle(), radioArgs = new Bundle();
        if (getArguments() != null) {
            tvArgs.putAll(getArguments());
            radioArgs.putAll(getArguments());
        }
        tvArgs.putInt(PVR_LIST_TYPE_KEY, LIST_TV_CHANNELS);
        radioArgs.putInt(PVR_LIST_TYPE_KEY, LIST_RADIO_CHANNELS);

        tabsAdapter = new TabsAdapter(getActivity(), getChildFragmentManager())
                .addTab(PVRChannelsListFragment.class, tvArgs, R.string.tv_channels, 1)
                .addTab(PVRChannelsListFragment.class, radioArgs, R.string.radio_channels, 2)
                .addTab(PVRRecordingsListFragment.class, getArguments(), R.string.recordings, 3);

        viewPager.setAdapter(tabsAdapter);
        pagerTabStrip.setViewPager(viewPager);

        return root;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public boolean onBackPressed() {
        // Tell current fragment to move up one directory, if possible
        Fragment visibleFragment = ((TabsAdapter)viewPager.getAdapter())
                .getStoredFragment(viewPager.getCurrentItem());

        if (visibleFragment instanceof OnBackPressedListener) {
            return ((OnBackPressedListener) visibleFragment).onBackPressed();
        }

        // Not handled, let the activity handle it
        return false;
    }

}
