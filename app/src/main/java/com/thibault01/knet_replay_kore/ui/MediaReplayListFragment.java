/*
 * Copyright 2015 DanhDroid. All rights reserved.
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

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.thibault01.knet_replay_kore.R;
import com.thibault01.knet_replay_kore.host.HostManager;
import com.thibault01.knet_replay_kore.jsonrpc.ApiCallback;
import com.thibault01.knet_replay_kore.jsonrpc.HostConnection;
import com.thibault01.knet_replay_kore.jsonrpc.method.Files;
import com.thibault01.knet_replay_kore.jsonrpc.method.Player;
import com.thibault01.knet_replay_kore.jsonrpc.method.Playlist;
import com.thibault01.knet_replay_kore.jsonrpc.type.ListType;
import com.thibault01.knet_replay_kore.jsonrpc.type.PlayerType;
import com.thibault01.knet_replay_kore.jsonrpc.type.PlaylistType;
import com.thibault01.knet_replay_kore.utils.LogUtils;
import com.thibault01.knet_replay_kore.utils.UIUtils;
import com.thibault01.knet_replay_kore.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import butterknife.ButterKnife;
import butterknife.InjectView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import android.os.AsyncTask;

/**
 * Presents a list of files of different types (Video/Music)
 */
public class MediaReplayListFragment extends Fragment {
    private static final String TAG = LogUtils.makeLogTag(MediaReplayListFragment.class);

    public static final String MEDIA_TYPE = "mediaType";
    public static final String REQUEST_CHANEL = "requestChanel";
    public static final String CHANEL_ID = "chanelId";
    public static final String PATH_CONTENTS = "pathContents";
    public static final String ROOT_PATH_CONTENTS = "rootPathContents";
    public static final String ROOT_PATH_CONTENTS_SAVE = "rootPathContentsSave";
    public static final String ROOT_VISITED = "rootVisited";
    private static final String ADDON_SOURCE = "addons:";

    private HostManager hostManager;
    /**
     * Handler on which to post RPC callbacks
     */
    private Handler callbackHandler = new Handler();

    String mediaType = Files.Media.MUSIC;
    String requestChanel = "";
    int chanelId = 1;
    int playlistId = PlaylistType.MUSIC_PLAYLISTID;             // this is the ID of the music player
    private MediaReplayListAdapter adapter = null;
    boolean browseRootAlready = false;

    ArrayList<FileLocation> rootFileLocation_save = new ArrayList<FileLocation>();
    ArrayList<FileLocation> rootFileLocation = new ArrayList<FileLocation>();
    Queue<FileLocation> mediaQueueFileLocation = new LinkedList<>();

    @InjectView(R.id.list) GridView folderGridView;
    @InjectView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(android.R.id.empty) TextView emptyView;

    public static MediaReplayListFragment newInstance(final String media) {
        MediaReplayListFragment fragment = new MediaReplayListFragment();
        Bundle args = new Bundle();
        args.putString(MEDIA_TYPE, media);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MEDIA_TYPE, mediaType);
        outState.putString(REQUEST_CHANEL, requestChanel);

        outState.putInt(CHANEL_ID, chanelId);

        try {
            outState.putParcelableArrayList(PATH_CONTENTS, (ArrayList<FileLocation>)adapter.getFileItemList());
        } catch (NullPointerException npe) {
            // adapter is null probably nothing was save in bundle because the directory is empty
            // ignore this so that the empty message would display later on
        }
        outState.putParcelableArrayList(ROOT_PATH_CONTENTS, rootFileLocation);
        outState.putParcelableArrayList(ROOT_PATH_CONTENTS_SAVE, rootFileLocation_save);
        outState.putBoolean(ROOT_VISITED, browseRootAlready);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            requestChanel = args.getString(REQUEST_CHANEL, "");
            chanelId = args.getInt(CHANEL_ID, 1);
            mediaType = args.getString(MEDIA_TYPE, Files.Media.MUSIC);
            if (mediaType.equalsIgnoreCase(Files.Media.VIDEO)) {
                playlistId = PlaylistType.VIDEO_PLAYLISTID;
            } else if (mediaType.equalsIgnoreCase(Files.Media.PICTURES)) {
                playlistId = PlaylistType.PICTURE_PLAYLISTID;
            }
        }
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_generic_media_list, container, false);
        ButterKnife.inject(this, root);

        hostManager = HostManager.getInstance(getActivity());
        swipeRefreshLayout.setEnabled(false);

        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseReplays();
            }
        });
        folderGridView.setEmptyView(emptyView);
        folderGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleFileSelect(adapter.getItem(position));
            }
        });

        if (adapter == null) {
            adapter = new MediaReplayListAdapter(getActivity(), R.layout.grid_item_replay);
        }
        folderGridView.setAdapter(adapter);
        if (savedInstanceState != null) {
            requestChanel = savedInstanceState.getString(REQUEST_CHANEL);
            chanelId = savedInstanceState.getInt(CHANEL_ID);
            mediaType = savedInstanceState.getString(MEDIA_TYPE);
            //currentPath = savedInstanceState.getString(CURRENT_PATH);
            if (mediaType.equalsIgnoreCase(Files.Media.VIDEO)) {
                playlistId = PlaylistType.VIDEO_PLAYLISTID;
            } else if (mediaType.equalsIgnoreCase(Files.Media.PICTURES)) {
                playlistId = PlaylistType.PICTURE_PLAYLISTID;
            }
            ArrayList<FileLocation> list = savedInstanceState.getParcelableArrayList(PATH_CONTENTS);
            rootFileLocation = savedInstanceState.getParcelableArrayList(ROOT_PATH_CONTENTS);
            rootFileLocation_save = savedInstanceState.getParcelableArrayList(ROOT_PATH_CONTENTS_SAVE);
            browseRootAlready = savedInstanceState.getBoolean(ROOT_VISITED);
            adapter.setFilelistItems(list);
        }
        else {
            browseReplays();
        }
        return root;
    }

    void handleFileSelect(FileLocation f) {
        if(f.isDirectory ==false)
        {
            playMediaReplay(f.file);
        }
        else
        {
            browseDirectoy(f);
        }
    }

    public void onBackPressed() {
        // Emulate a click on ..
        handleFileSelect(adapter.getItem(0));
    }

    public boolean atRootDirectory() {
        if (adapter.getCount() == 0)
            return true;
        FileLocation fl = adapter.getItem(0);
        if (fl == null)
            return true;
        else
            // if we still see "..", it is not the real root directory
            return fl.isRootDir() && (fl.title.contentEquals("..") == false);
    }

    /**
     * Contact WebServer to get replays
    **/
    private class GetListReplays extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... message) {
            StringBuffer chaine = new StringBuffer("");
            try{
                URL url = new URL("http://httptoudp.kwaoo.me/tv/replay/" + requestChanel);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestProperty("User-Agent", "");
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                InputStream inputStream = connection.getInputStream();

                BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while ((line = rd.readLine()) != null) {
                    chaine.append(line);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                // writing exception to log
                e.printStackTrace();
            }

            return chaine.toString();
        }

        protected void onPostExecute(String result) {
            String[] listReplays = result.split("%#%");
            String[] paramReplay;
            FileLocation fl;
            FileLocation dir;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            String saveDate ="";
            DateFormat dateStringFormat = new SimpleDateFormat("EEEE dd MMMM");
            Date date = null;


            rootFileLocation.clear();
            rootFileLocation_save.clear();

            for (String resultReplay : listReplays) {
                paramReplay = resultReplay.split("\\|");
                if (paramReplay.length > 6)
                {
                    try {
                        date = (Date)formatter.parse(paramReplay[4]);

                        if(!saveDate.equalsIgnoreCase((dateStringFormat.format(date))))
                        {
                            saveDate=dateStringFormat.format(date);
                            dir=new FileLocation(saveDate,"",true,"");
                            dir.setRootDir(true);
                            rootFileLocation.add(dir);
                            rootFileLocation_save.add(dir);
                        }
                        //Log.i(paramReplay[1], "http://ktv.zone/m3u.m3u8?channel=" + chanelId + "&timestamp=" + date.getTime()/1000 +"&duration=" + Integer.parseInt(paramReplay[6]) * 6);
                        fl = new FileLocation(paramReplay[1], "http://ktv.zone/m3u.m3u8?channel=" + chanelId + "&timestamp=" + date.getTime()/1000 +"&duration=" + Integer.parseInt(paramReplay[6]) * 6, false, paramReplay[3], "du " + paramReplay[4].substring(0,16).replace(":","h") + " au " + paramReplay[5].substring(0,16).replace(":", "h"), "", saveDate);
                        //fl.setRootDir(true);
                        //rootFileLocation.add(fl);
                        rootFileLocation_save.add(fl);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
            Collections.reverse(rootFileLocation);
            Collections.reverse(rootFileLocation_save);

            emptyView.setText(getString(R.string.source_empty));
            String charSequence = ((ReplayActivity)getActivity()).search;
            if(charSequence !="")
            {
                searchReplays(charSequence);
            }
            else
            {
                adapter.setFilelistItems(rootFileLocation);
            }
        }

        public String parseTime(long milliseconds) {
            return String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(milliseconds),
                    TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(
                            TimeUnit.MILLISECONDS.toHours(milliseconds)),
                    TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
        }
    }

    /**
     *
     */
    public void searchReplays(String charSequence) {
        if(adapter !=null)
        {
            rootFileLocation.clear();
            if(!charSequence.equalsIgnoreCase(""))
            {
                for(FileLocation file : rootFileLocation_save)
                {
                    if(file.title.trim().toLowerCase().contains(charSequence.trim().toLowerCase())) {
                        rootFileLocation.add(file);
                    }
                }
                adapter.setFilelistItems(rootFileLocation);
            }
            else
            {
                browseDirectoy();
            }

        }
    }

    /**
     * Gets all replays of Directoy
     */
    private void browseDirectoy() {
        browseDirectoy(null);
    }
    private void browseDirectoy(FileLocation Directory) {

        rootFileLocation.clear();
        if(Directory == null || Directory.title.trim().equalsIgnoreCase(".."))
        {
            for(FileLocation file : rootFileLocation_save)
            {
                if(file.isDirectory) {
                    rootFileLocation.add(file);
                }
            }
        }
        else
        {
            for(FileLocation file : rootFileLocation_save)
            {
                if(file.parentDirectoryName.trim().toLowerCase().contains(Directory.title.trim().toLowerCase())) {
                    rootFileLocation.add(file);
                }
            }
            FileLocation dirReturn = new FileLocation("..", "..", true , "");
            rootFileLocation.add(0, dirReturn);
        }

        adapter.setFilelistItems(rootFileLocation);
        //adapter.setFilelistItems(rootFileLocation);
    }
    /**
     * Gets all replays of channel
     */
    private void browseReplays() {


       new GetListReplays().execute();

        browseRootAlready = true;
        emptyView.setText("En cours de chargement");
        //adapter.setFilelistItems(rootFileLocation);
    }

    /**
     * Starts playing the given media file
     * @param filename Filename to start playing
     */
    private void playMediaReplay(final String filename) {
        PlaylistType.Item item = new PlaylistType.Item();
        item.file = filename;
        Player.Open action = new Player.Open(item);
        action.execute(hostManager.getConnection(), new ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                while (mediaQueueFileLocation.size() > 0) {
                    queueMediaReplay(mediaQueueFileLocation.poll().file);
                }
            }

            @Override
            public void onError(int errorCode, String description) {
                if (!isAdded()) return;
                Toast.makeText(getActivity(),
                        String.format(getString(R.string.error_play_media_file), description),
                        Toast.LENGTH_SHORT).show();
            }
        }, callbackHandler);
    }

    /**
     * Queues the given media file on the active playlist, and starts it if nothing is playing
     * @param filename File to queue
     */
    private void queueMediaReplay(final String filename) {
        final HostConnection connection = hostManager.getConnection();
        PlaylistType.Item item = new PlaylistType.Item();
        item.file = filename;
        Playlist.Add action = new Playlist.Add(playlistId, item);
        action.execute(connection, new ApiCallback<String>() {
            @Override
            public void onSuccess(String result ) {
                startPlaylistIfNoActivePlayers(connection, playlistId, callbackHandler);
            }

            @Override
            public void onError(int errorCode, String description) {
                if (!isAdded()) return;
                Toast.makeText(getActivity(),
                        String.format(getString(R.string.error_queue_media_file), description),
                        Toast.LENGTH_SHORT).show();
            }
        }, callbackHandler);
    }

    /**
     * Starts a playlist if no active players are playing
     * @param connection Host connection
     * @param playlistId PlaylistId to start
     * @param callbackHandler Handler on which to post method callbacks
     */
    private void startPlaylistIfNoActivePlayers(final HostConnection connection,
                                                final int playlistId,
                                                final Handler callbackHandler) {
        Player.GetActivePlayers action = new Player.GetActivePlayers();
        action.execute(connection, new ApiCallback<ArrayList<PlayerType.GetActivePlayersReturnType>>() {
            @Override
            public void onSuccess(ArrayList<PlayerType.GetActivePlayersReturnType> result ) {
                // find out if any player is running. If it is not, start one
                if (result.isEmpty()) {
                    Player.Open action = new Player.Open(Player.Open.TYPE_PLAYLIST, playlistId);
                    action.execute(connection, new ApiCallback<String>() {
                        @Override
                        public void onSuccess(String result) { }

                        @Override
                        public void onError(int errorCode, String description) {
                            if (!isAdded()) return;
                            Toast.makeText(getActivity(),
                                    String.format(getString(R.string.error_play_media_file), description),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }, callbackHandler);
                }
            }

            @Override
            public void onError(int errorCode, String description) {
                if (!isAdded()) return;
                Toast.makeText(getActivity(),
                        String.format(getString(R.string.error_get_active_player), description),
                        Toast.LENGTH_SHORT).show();
            }
        }, callbackHandler);

    }

    /**
     * return the path of the parent based on path
     * @param path of the current media file
     * @return path of the parent
     */
    public static String getParentDirectory(final String path) {
        String p = path;
        String pathSymbol = "/";        // unix style
        if (path.contains("\\")) {
            pathSymbol = "\\";          // windows style
        }

        // if path ends with /, remove it before removing the directory name
        if (path.endsWith(pathSymbol)) {
            p = path.substring(0, path.length() - 1);
        }

        if (p.lastIndexOf(pathSymbol) != -1) {
            p = p.substring(0, p.lastIndexOf(pathSymbol));
        }
        p = p + pathSymbol;            // add it back to make it look like path
        return p;
    }

    private class MediaReplayListAdapter extends BaseAdapter implements ListAdapter {

        Context ctx;
        int resource;
        List<FileLocation> fileLocationItems = null;

        int artWidth;
        int artHeight;

        private View.OnClickListener itemMenuClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = (Integer)v.getTag();
                if (fileLocationItems != null) {
                    final FileLocation loc = fileLocationItems.get(position);
                    if (!loc.isDirectory) {
                        final PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                        popupMenu.getMenuInflater().inflate(R.menu.replaylist_item, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.action_queue_item:
                                        queueMediaReplay(loc.file);
                                        return true;
                                    case R.id.action_play_item:
                                        playMediaReplay(loc.file);
                                        return true;
                                    case R.id.action_details_replay:
                                        Toast.makeText(getActivity(), loc.title + " : " + loc.details, Toast.LENGTH_LONG).show();
                                        return true;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                }
            }
        };

        public MediaReplayListAdapter(Context context, int resource) {
            super();
            this.ctx = context;
            this.resource = resource;
            this.fileLocationItems = null;

            // Get the art dimensions
            Resources resources = context.getResources();
            artWidth = (int)(resources.getDimension(R.dimen.replaylist_art_width) /
                    UIUtils.IMAGE_RESIZE_FACTOR);
            artHeight = (int)(resources.getDimension(R.dimen.replaylist_art_heigth) /
                    UIUtils.IMAGE_RESIZE_FACTOR);

        }

        /**
         * Manually set the items on the adapter
         * Calls notifyDataSetChanged()
         *
         * @param items list of files/directories
         */
        public void setFilelistItems(List<FileLocation> items) {
            this.fileLocationItems = items;
            notifyDataSetChanged();
        }

        public List<FileLocation> getFileItemList() {
            if (fileLocationItems == null)
                return new ArrayList<FileLocation>();
            return new ArrayList<FileLocation>(fileLocationItems);
        }

        @Override
        public int getCount() {
            if (fileLocationItems == null) {
                return 0;
            } else {
                return fileLocationItems.size();
            }
        }

        @Override
        public FileLocation getItem(int position) {
            if (fileLocationItems == null) {
                return null;
            } else {
                return fileLocationItems.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount () {
            return 1;
        }

        /** {@inheritDoc} */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(ctx)
                        .inflate(resource, parent, false);

                // Setup View holder pattern
                viewHolder = new ViewHolder();
                viewHolder.art = (ImageView) convertView.findViewById(R.id.art);
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.details = (TextView) convertView.findViewById(R.id.details);
                viewHolder.contextMenu = (ImageView) convertView.findViewById(R.id.list_context_menu);
                viewHolder.sizeDuration = (TextView) convertView.findViewById(R.id.size_duration);

                convertView.setTag(viewHolder);
            }

            viewHolder = (ViewHolder) convertView.getTag();
            FileLocation fileLocation = this.getItem(position);

//            if (fileLocation.isDirectory) {
//                viewHolder.title.setText(fileLocation.title);
//                viewHolder.details.setText("");
//            } else {
//                viewHolder.title.setText("");
//                viewHolder.details.setText(fileLocation.title);
//            }
            viewHolder.title.setText(fileLocation.title);
            viewHolder.details.setText(fileLocation.details);
            viewHolder.sizeDuration.setText(fileLocation.sizeDuration);

            UIUtils.loadImageWithCharacterAvatar(getActivity(), hostManager,
                    fileLocation.artUrl, fileLocation.title,
                    viewHolder.art, artWidth, artHeight);
            // For the popup menu
            if (fileLocation.isDirectory) {
                viewHolder.contextMenu.setVisibility(View.GONE);
            } else {
                viewHolder.contextMenu.setVisibility(View.VISIBLE);
                viewHolder.contextMenu.setTag(position);
                viewHolder.contextMenu.setOnClickListener(itemMenuClickListener);
            }

            return convertView;
        }
    }

    /**
     * View holder pattern
     */
    private static class ViewHolder {
        ImageView art;
        TextView title;
        TextView details;
        TextView sizeDuration;
        ImageView contextMenu;
    }

    public static class FileLocation implements Parcelable {
        public final String title;
        public final String details;
        public final String sizeDuration;
        public final String artUrl;
        public final String parentDirectoryName;

        public final String file;
        public final boolean isDirectory;

        private boolean isRoot;


        public boolean isRootDir() { return this.isRoot; }
        public void setRootDir(boolean root) { this.isRoot = root; }

        public FileLocation(String title, String path, boolean isDir, String _parentDirectoryName) {
            this(title, path, isDir, null, null, null, _parentDirectoryName);
        }

        public FileLocation(String title, String path, boolean isDir, String details, String sizeDuration, String artUrl, String _parentDirectoryName) {
            this.title = title;
            this.file = path;
            this.isDirectory = isDir;
            this.isRoot = false;
            this.parentDirectoryName=_parentDirectoryName;
            this.details = details;
            this.sizeDuration = sizeDuration;
            this.artUrl = artUrl;
        }

        private FileLocation(Parcel in) {
            this.title = in.readString();
            this.file = in.readString();
            this.isDirectory = (in.readInt() != 0);
            this.isRoot = (in.readInt() != 0);

            this.details = in.readString();
            this.sizeDuration = in.readString();
            this.artUrl = in.readString();
            this.parentDirectoryName = in.readString();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel out, int flags) {
            out.writeString(title);
            out.writeString(file);
            out.writeInt(isDirectory ? 1 : 0);
            out.writeInt(isRoot ? 1 : 0);

            out.writeString(details);
            out.writeString(sizeDuration);
            out.writeString(artUrl);
            out.writeString(parentDirectoryName);
        }

        public static final Parcelable.Creator<FileLocation> CREATOR = new Parcelable.Creator<FileLocation>() {
            public FileLocation createFromParcel(Parcel in) {
                return new FileLocation(in);
            }

            public FileLocation[] newArray(int size) {
                return new FileLocation[size];
            }
        };
    }
}

