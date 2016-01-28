package com.aranga.moodloop.ui;


import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaActionSound;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aranga.moodloop.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by aakashranga on 1/8/16.
 */
public class AlbumSongsFragment extends Fragment {
    private ListView songsList;
    private ImageView imageView;
    private TextView albumID;
    private TextView auxInfo;
    String[] menuitems = {"Play Next", "Add To Playlist", "Delete"};
    private ArrayList<MusicFile> songlist;
    private String albumname;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Song Fragment", "Song Fragment Instantiated");
        albumname = getArguments().getString("albumname");
        songlist = new ArrayList<MusicFile>();
        songListCreator();
        View myView = inflater.inflate(R.layout.album_songs_fragment, container, false);
        songsList = (ListView) myView.findViewById(R.id.songlistalbumsong);
        imageView = (ImageView) myView.findViewById(R.id.album_artforalbumsong);
        albumID = (TextView) myView.findViewById(R.id.albumnameforalbumsong);
        auxInfo = (TextView) myView.findViewById(R.id.artistnameforalbumsong);
        albumID.setSelected(true);
        auxInfo.setSelected(true);
        albumID.setText(albumname);
        auxInfo.setText(""+songlist.size() + " songs");
        Bitmap image;
        if (songlist.get(0).getPath() != null) {
            byte[] art;
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            try {
                mmr.setDataSource(songlist.get(0).getPath());
                art = mmr.getEmbeddedPicture();
                image = BitmapFactory.decodeByteArray(art, 0, art.length);

            } catch (Exception e) {
                e.printStackTrace();
                image = null;
            }
            if (image != null) {
                imageView.setImageBitmap(image);
            }
            else {
                imageView.setImageResource(R.drawable.default_album_art_white_cd);
            }
        }
        else {
            imageView.setImageResource(R.drawable.default_album_art_white_cd);
        }

        ListAdapter adapter = new SongListAdapter(this.getContext(), songlist);
        songsList.setAdapter(adapter);
        registerForContextMenu(songsList);

        songsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //do service calls to service here watch that youtube video. Finish this today
                ((MainActivity) getActivity()).changeFragment(position, "First_Fragment", albumname);
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
        Toast.makeText(this.getContext(), "MenuItem : " + menuItemSelected + " selected", Toast.LENGTH_SHORT).show();
        return true;
    }


    private void songListCreator() {
        String[] columns = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST};

        String where = MediaStore.Audio.Media.ALBUM + "=?";

        String whereVal[] = { albumname };

        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        ContentResolver cr = this.getActivity().getContentResolver();

        Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, columns, where, whereVal, sortOrder);

        int count = 0;

        if(cursor != null) {
            count = cursor.getCount();
            if(count > 0) {
                while(cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    MusicFile song = new MusicFile(title, artistName, albumname, path);
                    songlist.add(song);
                    // Add code to get more column here
                    // Save to your list here
                }
            }
        }
        cursor.close();
    }
}
