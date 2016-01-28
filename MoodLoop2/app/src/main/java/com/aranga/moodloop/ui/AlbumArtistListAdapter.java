package com.aranga.moodloop.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aranga.moodloop.R;

import java.util.List;

/**
 * Created by aakashranga on 1/8/16.
 */
public class AlbumArtistListAdapter extends BaseAdapter {

    private Context context;
    private List<AlbumFile> albums_artists;
    public AlbumArtistListAdapter(Context context, List<AlbumFile> albums_artists) {
        this.albums_artists = albums_artists;
        this.context = context;
    }
    @Override
    public int getCount() {
        return albums_artists.size();
    }

    @Override
    public Object getItem(int position) {
        return albums_artists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        Album_artistViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.album_artist_listview, null, false);
            viewHolder = new Album_artistViewHolder();
            viewHolder.album_artistview = (TextView) convertView.findViewById(R.id.albumorartist_name);
            viewHolder.other_textView = (TextView) convertView.findViewById(R.id.other_name);
            viewHolder.albumart = (ImageView) convertView.findViewById(R.id.album_art);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (Album_artistViewHolder) convertView.getTag();
        }
        viewHolder.album_artistview.setText(albums_artists.get(position).getAlbumname());
        String text = albums_artists.get(position).getArtist() + "\n" + albums_artists.get(position).getAlbumSongs() + " songs";
        viewHolder.other_textView.setText(text);
        Bitmap image;
        if (albums_artists.get(position).getPath() != null) {
            try {
                image = BitmapFactory.decodeFile(albums_artists.get(position).getPath());

            } catch (Exception e) {
                e.printStackTrace();
                image = null;
            }
            if (image != null) {
                viewHolder.albumart.setImageBitmap(image);
            }
        }
        else {
            viewHolder.albumart.setImageResource(R.drawable.default_album_art_white_cd);
        }


        return convertView;
    }


}
class Album_artistViewHolder {
    TextView album_artistview;
    TextView other_textView;
    ImageView albumart;
}
