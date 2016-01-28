package com.aranga.moodloop.ui;

import android.app.Dialog;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.aranga.moodloop.R;

import java.util.List;

/**
 * Created by aakashranga on 12/31/15.
 */
public class SongsFragment extends Fragment {
    private ListView songsList;
    String[] menuitems = {"Play Next", "Add To Playlist", "Delete"};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Song Fragment", "Song Fragment Instantiated");
        View myView = inflater.inflate(R.layout.songs_fragment, container, false);
        songsList = (ListView) myView.findViewById(R.id.songs_list);

        final MainActivity temp = (MainActivity) getActivity();
        ListAdapter adapter = new SongListAdapter(this.getContext(), temp.getSongs());
        songsList.setAdapter(adapter);
        registerForContextMenu(songsList);

        songsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //do service calls to service here watch that youtube video. Finish this today
                ((MainActivity) getActivity()).changeFragment(position, "First_Fragment", null);
            }
        });
        return myView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.songs_list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            MainActivity temp = (MainActivity) getActivity();
            menu.setHeaderTitle(temp.getSongs().get(info.position).getSongTitle());
            for (int i = 0; i< menuitems.length; i++) {
                menu.add(Menu.NONE, i, i, menuitems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String menuItemSelected = menuitems[menuItemIndex];
        MainActivity temp = (MainActivity) getActivity();
        MusicFile contextFile = temp.getSongs().get(info.position);
        switch (menuItemSelected) {
            case "Play Next":

                break;

            case "Add To Playlist":

                break;

            case "Delete"://deleting a song from the song list directly.
                try {
                    MediaScannerConnection.scanFile(this.getContext(), new String[]{contextFile.getPath()},
                            null, new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    getContext().getContentResolver()
                                            .delete(uri, null, null);
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
        Toast.makeText(this.getContext(), "MenuItem : " + menuItemSelected + " selected", Toast.LENGTH_SHORT).show();
        return true;
    }
}
