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

package com.thibault01.knet_replay_kore.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.thibault01.knet_replay_kore.jsonrpc.ApiCallback;
import com.thibault01.knet_replay_kore.jsonrpc.method.Playlist;
import com.thibault01.knet_replay_kore.ui.RemoteActivity;

import com.thibault01.knet_replay_kore.R;
import com.thibault01.knet_replay_kore.Settings;
import com.thibault01.knet_replay_kore.host.HostManager;
import com.thibault01.knet_replay_kore.jsonrpc.method.Player;
import com.thibault01.knet_replay_kore.jsonrpc.type.PlaylistType;

import java.util.ArrayList;

public class MediaPlayerUtils {

    /**
     * Clears current playlist and starts playing item
     * @param fragment Fragment instance from which this method is called
     * @param item PlaylistType.Item that needs to be played
     */
    public static void play(final Fragment fragment, final PlaylistType.Item item) {
        HostManager hostManager = HostManager.getInstance(fragment.getActivity());

        final Handler callbackHandler = new Handler();

        final Context context = fragment.getActivity();

        Player.Open action = new Player.Open(item);
        action.execute(hostManager.getConnection(), new ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!fragment.isAdded()) return;
                boolean switchToRemote = PreferenceManager
                        .getDefaultSharedPreferences(context)
                        .getBoolean(Settings.KEY_PREF_SWITCH_TO_REMOTE_AFTER_MEDIA_START,
                                Settings.DEFAULT_PREF_SWITCH_TO_REMOTE_AFTER_MEDIA_START);
                if (switchToRemote) {
                    Intent launchIntent = new Intent(context, RemoteActivity.class);
                    context.startActivity(launchIntent);
                } else {
                    Toast.makeText(context, R.string.now_playing, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onError(int errorCode, String description) {
                if (!fragment.isAdded()) return;
                // Got an error, show toast
                String errorMessage = context.getString(R.string.error_play_media_file, description);
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT)
                        .show();
            }
        }, callbackHandler);
    }

    /**
     * Queues item to current playlist
     * @param fragment Fragment instance from which this method is called
     * @param item PlaylistType.Item that needs to be added to the current playlist
     * @param type {@link PlaylistType.GetPlaylistsReturnType}
     */
    public static void queue(final Fragment fragment, final PlaylistType.Item item, final String type) {
        Playlist.GetPlaylists getPlaylists = new Playlist.GetPlaylists();

        final Handler callbackHandler = new Handler();

        final Context context = fragment.getActivity();

        final HostManager hostManager = HostManager.getInstance(fragment.getActivity());

        getPlaylists.execute(hostManager.getConnection(), new ApiCallback<ArrayList<PlaylistType.GetPlaylistsReturnType>>() {
            @Override
            public void onSuccess(ArrayList<PlaylistType.GetPlaylistsReturnType> result) {
                if (!fragment.isAdded()) return;
                // Ok, loop through the playlists, looking for the correct one
                int playlistId = -1;
                for (PlaylistType.GetPlaylistsReturnType playlist : result) {
                    if (playlist.type.equals(type)) {
                        playlistId = playlist.playlistid;
                        break;
                    }
                }
                // If found, add to playlist
                if (playlistId != -1) {
                    Playlist.Add action = new Playlist.Add(playlistId, item);
                    action.execute(hostManager.getConnection(), new ApiCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            if (!fragment.isAdded()) return;
                            Toast.makeText(context, R.string.item_added_to_playlist, Toast.LENGTH_SHORT)
                                    .show();
                        }

                        @Override
                        public void onError(int errorCode, String description) {
                            if (!fragment.isAdded()) return;
                            // Got an error, show toast
                            String errorMessage = context.getString(R.string.error_queue_media_file, description);
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }, callbackHandler);
                } else {
                    Toast.makeText(context, R.string.no_suitable_playlist, Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onError(int errorCode, String description) {
                if (!fragment.isAdded()) return;
                // Got an error, show toast
                Toast.makeText(context, R.string.error_getting_playlist, Toast.LENGTH_SHORT)
                        .show();
            }
        }, callbackHandler);
    }
}