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
public class ArtistsFragment extends Fragment {
    private ListView artistList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Artist Fragment", "Artists instantiated");
        View myView = inflater.inflate(R.layout.artists_fragment, container, false);
        artistList = (ListView) myView.findViewById(R.id.artists_list);
        MainActivity temp = (MainActivity) getActivity();
       // ListAdapter la = new AlbumArtistListAdapter(this.getContext(), temp.getArtists());
        //artistList.setAdapter(la);

        artistList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        return myView;
    }
}
