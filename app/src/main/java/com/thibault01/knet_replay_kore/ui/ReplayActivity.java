package com.thibault01.knet_replay_kore.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.thibault01.knet_replay_kore.utils.TabsAdapter;
import com.thibault01.knet_replay_kore.utils.Utils;

import com.thibault01.knet_replay_kore.R;

import java.util.ArrayList;

/**
 * Handles listing of files fragments
 */
public class ReplayActivity extends BaseActivity {

    private NavigationDrawerFragment navigationDrawerFragment;

    public static final String SEARCH = "search";
    public String search = "";

    OnBackPressedListener fragmentBackListener;

    public void setBackPressedListener(OnBackPressedListener listener) {
        fragmentBackListener = listener;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH, search);
    }

    @TargetApi(21)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Request transitions on lollipop
        if (Utils.isLollipopOrLater()) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_media);


        // Set up the drawer.
        navigationDrawerFragment = (NavigationDrawerFragment)getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        navigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        if (savedInstanceState == null) {
            ReplayListFragment replayListFragment = new ReplayListFragment();

            // Setup animations
            if (Utils.isLollipopOrLater()) {
                replayListFragment.setExitTransition(null);
                replayListFragment.setReenterTransition(TransitionInflater
                        .from(this)
                        .inflateTransition(android.R.transition.fade));
            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, replayListFragment)
                    .commit();
        }
        else
        {
            search = savedInstanceState.getString(SEARCH, "");
        }
        setupActionBar(getString(R.string.replays));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.media_info, menu);

        // Add search
        getMenuInflater().inflate(R.menu.media_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //*** setOnQueryTextListener ***
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
                search=newText;
                for(Fragment frag : getSupportFragmentManager().getFragments())
                {
                    if(frag instanceof ReplayListFragment)
                    {
                        ReplayListFragment _ReplayListFragment = (ReplayListFragment)frag;
                        TabsAdapter tabsAdaptater = (TabsAdapter)_ReplayListFragment.viewPager.getAdapter();

                        MediaReplayListFragment mediaReplayListFragment = null;
                        int countPage = tabsAdaptater.getCount();
                        int i = 0;
                        while(i<countPage)
                        {
                            mediaReplayListFragment = (MediaReplayListFragment)tabsAdaptater.getStoredFragment(i);
                            if(mediaReplayListFragment != null)
                            {
                                mediaReplayListFragment.searchReplays(newText);
                            }
                            i++;
                        }
                    }
                }
                //Toast.makeText(getBaseContext(), newText, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        //searchView.setOnQueryTextListener(this);
        //searchView.setQueryHint(getString(R.string.action_search_albums));
        //
        return super.onCreateOptionsMenu(menu);
    }
    /*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!isAdded()) {
            // HACK: Fix crash reported on Play Store. Why does this is necessary is beyond me
            super.onCreateOptionsMenu(menu, inflater);
            return;
        }

        inflater.inflate(R.menu.media_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.action_search_albums));
        super.onCreateOptionsMenu(menu, inflater);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_remote:
                // Starts remote
                Intent launchIntent = new Intent(this, RemoteActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(launchIntent);
                return true;
            default:
                //Log.i("Replay", "DEFAULT ACTION");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // tell fragment to move up one directory
        if (fragmentBackListener != null) {
            boolean handled = fragmentBackListener.onBackPressed();
            if (!handled)
                super.onBackPressed();
        }

    }

    private void setupActionBar(String title) {
        Toolbar toolbar = (Toolbar)findViewById(R.id.default_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setDisplayHomeAsUpEnabled(true);
        navigationDrawerFragment.setDrawerIndicatorEnabled(true);
        actionBar.setTitle(title);

    }
}

