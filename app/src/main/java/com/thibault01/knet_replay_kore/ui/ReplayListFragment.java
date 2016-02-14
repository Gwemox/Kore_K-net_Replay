package com.thibault01.knet_replay_kore.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import com.thibault01.knet_replay_kore.jsonrpc.method.Files;
import com.thibault01.knet_replay_kore.utils.TabsAdapter;

import com.thibault01.knet_replay_kore.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Manages the viewpager of files
 */
public class ReplayListFragment extends Fragment
        implements OnBackPressedListener {

    @InjectView(R.id.pager_tab_strip) PagerSlidingTabStrip pagerTabStrip;
    @InjectView(R.id.pager) ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_media_list, container, false);
        ButterKnife.inject(this, root);

        Bundle rts1ReplayListArgs = new Bundle();
        rts1ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        rts1ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "TSR1");
        rts1ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 1);

        Bundle rts2ReplayListArgs = new Bundle();
        rts2ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        rts2ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "TSR2");
        rts2ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 2);

        Bundle tf1ReplayListArgs = new Bundle();
        tf1ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        tf1ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "TF1");
        tf1ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 3);

        Bundle france2ReplayListArgs = new Bundle();
        france2ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        france2ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "FRANCE%202");
        france2ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 4);

        Bundle france3ReplayListArgs = new Bundle();
        france3ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        france3ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "FRANCE%203");
        france3ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 5);

        Bundle france5ReplayListArgs = new Bundle();
        france5ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        france5ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "FRANCE%205");
        france5ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 7);

        Bundle m6ReplayListArgs = new Bundle();
        m6ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        m6ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "M6");
        m6ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 8);

        Bundle arteReplayListArgs = new Bundle();
        arteReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        arteReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "ARTE");
        arteReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 9);

        Bundle d8ReplayListArgs = new Bundle();
        d8ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        d8ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "D8");
        d8ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 10);

        Bundle w9ReplayListArgs = new Bundle();
        w9ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        w9ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "W9");
        w9ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 11);

        Bundle tmcReplayListArgs = new Bundle();
        tmcReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        tmcReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "TMC");
        tmcReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 12);

        Bundle nt1ReplayListArgs = new Bundle();
        nt1ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        nt1ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "NT1");
        nt1ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 13);

        Bundle nrj12ReplayListArgs = new Bundle();
        nrj12ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        nrj12ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "NRJ%2012");
        nrj12ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 14);

        Bundle lcpReplayListArgs = new Bundle();
        lcpReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        lcpReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "LCP");
        lcpReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 15);

        Bundle france4ReplayListArgs = new Bundle();
        france4ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        france4ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "FRANCE%204");
        france4ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 16);

        Bundle d17ReplayListArgs = new Bundle();
        d17ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        d17ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "D17");
        d17ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 19);

        Bundle gulliReplayListArgs = new Bundle();
        gulliReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        gulliReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "GULLI");
        gulliReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 20);

        Bundle franceoReplayListArgs = new Bundle();
        franceoReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        franceoReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "FRANCE%20O");
        franceoReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 21);

        Bundle hd1ReplayListArgs = new Bundle();
        hd1ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        hd1ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "HD1");
        hd1ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 22);

       /* Bundle equipe21ReplayListArgs = new Bundle();
        equipe21ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        equipe21ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "");
        equipe21ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 23);
*/
        Bundle _6terReplayListArgs = new Bundle();
        _6terReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        _6terReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "6%20TER");
        _6terReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 24);

        Bundle numero23ReplayListArgs = new Bundle();
        numero23ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        numero23ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "NUMERO%2023");
        numero23ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 25);

        Bundle rmcReplayListArgs = new Bundle();
        rmcReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        rmcReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "RMC");
        rmcReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 26);

        Bundle cherie25ReplayListArgs = new Bundle();
        cherie25ReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        cherie25ReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "CHERIE%20HD");
        cherie25ReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 27);

        Bundle m6sdReplayListArgs = new Bundle();
        m6sdReplayListArgs.putString(MediaReplayListFragment.MEDIA_TYPE, Files.Media.VIDEO);
        m6sdReplayListArgs.putString(MediaReplayListFragment.REQUEST_CHANEL, "M6");
        m6sdReplayListArgs.putInt(MediaReplayListFragment.CHANEL_ID, 76);

        TabsAdapter tabsAdapter = new TabsAdapter(getActivity(), getChildFragmentManager())
                .addTab(MediaReplayListFragment.class, rts1ReplayListArgs, R.string.rts1, 1)
                .addTab(MediaReplayListFragment.class, rts2ReplayListArgs, R.string.rts2, 2)
                .addTab(MediaReplayListFragment.class, tf1ReplayListArgs, R.string.tf1, 3)
                .addTab(MediaReplayListFragment.class, france2ReplayListArgs, R.string.france2, 4)
                .addTab(MediaReplayListFragment.class, france3ReplayListArgs, R.string.france3, 5)
                .addTab(MediaReplayListFragment.class, france5ReplayListArgs, R.string.france5, 6)
                .addTab(MediaReplayListFragment.class, m6ReplayListArgs, R.string.m6hd, 7)
                .addTab(MediaReplayListFragment.class, arteReplayListArgs, R.string.arte, 8)
                .addTab(MediaReplayListFragment.class, d8ReplayListArgs, R.string.d8, 9)
                .addTab(MediaReplayListFragment.class, w9ReplayListArgs, R.string.w9, 10)
                .addTab(MediaReplayListFragment.class, tmcReplayListArgs, R.string.tmc, 11)
                .addTab(MediaReplayListFragment.class, nt1ReplayListArgs, R.string.nt1, 12)
                .addTab(MediaReplayListFragment.class, nrj12ReplayListArgs, R.string.nrj12, 13)
                .addTab(MediaReplayListFragment.class, lcpReplayListArgs, R.string.lcp, 14)
                .addTab(MediaReplayListFragment.class, d17ReplayListArgs, R.string.d17, 15)
                .addTab(MediaReplayListFragment.class, gulliReplayListArgs, R.string.gulli, 16)
                .addTab(MediaReplayListFragment.class, franceoReplayListArgs, R.string.franceo, 17)
                .addTab(MediaReplayListFragment.class, hd1ReplayListArgs, R.string.hd1, 18)
                .addTab(MediaReplayListFragment.class, _6terReplayListArgs, R.string._6ter, 19)
                .addTab(MediaReplayListFragment.class, numero23ReplayListArgs, R.string.numero23, 20)
                .addTab(MediaReplayListFragment.class, rmcReplayListArgs, R.string.rmc, 21)
                .addTab(MediaReplayListFragment.class, cherie25ReplayListArgs, R.string.cherie25, 22)
                .addTab(MediaReplayListFragment.class, m6sdReplayListArgs, R.string.m6, 23);
                // .addTab(MediaReplayListFragment.class, equipe21ReplayListArgs, R.string.equipe21, 24)
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            ReplayActivity listenerActivity = (ReplayActivity) activity;
            listenerActivity.setBackPressedListener(this);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " unable to register BackPressedListener");
        }
    }

    @Override
    public boolean onBackPressed() {
        // Tell current fragment to move up one directory, if possible
        MediaReplayListFragment curPage = (MediaReplayListFragment)((TabsAdapter)viewPager.getAdapter())
                .getStoredFragment(viewPager.getCurrentItem());
        if ((curPage != null) && !curPage.atRootDirectory()) {
            curPage.onBackPressed();
            return true;
        }

        // Not handled, let the activity handle it
        return false;
    }
}