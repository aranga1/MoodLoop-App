package com.aranga.moodloop.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.aranga.moodloop.R;

/**
 * Created by aakashranga on 12/31/15.
 */
public class AlbumsFragment extends Fragment {
    private ListView albumsList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.albums_fragment, container, false);
        albumsList = (ListView) myView.findViewById(R.id.albums_list);
        MainActivity temp = (MainActivity) getActivity();
        ListAdapter la = new AlbumArtistListAdapter(this.getContext(), temp.getAlbums());
        albumsList.setAdapter(la);

        albumsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity) getActivity()).changeFragment(position, "AlbumSongsFragment", null);
            }
        });
        return myView;
    }
}
