package com.aranga.moodloop.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aranga.moodloop.R;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by aakashranga on 1/5/16.
 */
public class SongListAdapter extends BaseAdapter {

    private Context context;
    private List<MusicFile> songs;
    public SongListAdapter(Context context, List<MusicFile> songs) {
        //super(context, R.layout.song_listview, songs);
        this.songs = songs;
        this.context = context;
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int position) {
        return songs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.song_listview, null, false);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.song_name);
            viewHolder.artistTextView = (TextView) convertView.findViewById(R.id.artist_name);
            //viewHolder.imageView = (ImageView) convertView.findViewById(R.id.songImage);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(songs.get(position).getSongTitle());
        viewHolder.artistTextView.setText(songs.get(position).getArtistName());
        //viewHolder.imageView.setBackgroundColor(Color.GRAY);


        return convertView;
    }
}

class ViewHolder {
    TextView textView;
    TextView artistTextView;
    //ImageView imageView;
}
