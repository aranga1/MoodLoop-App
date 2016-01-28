package com.aranga.moodloop.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Albums;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import com.aranga.moodloop.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private List<MusicFile> allSongs;

    private List<AlbumFile> albums;

    //private List<AlbumFile> artists;



    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ViewPager viewPager = null;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allSongs = new ArrayList<MusicFile>();
        albums = new ArrayList<AlbumFile>();
       // artists = new ArrayList<AlbumFile>();
        songListCreator();
        albumListCreator();
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        //mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private void songListCreator() {
        ContentResolver cr = this.getContentResolver();

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        Cursor cursor = cr.query(uri, null, selection, null, sortOrder);
        int count = 0;

        if(cursor != null) {
            count = cursor.getCount();
            if(count > 0) {
                while(cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    MusicFile song = new MusicFile(title, artistName, albumName, path);
                    allSongs.add(song);
                    // Add code to get more column here
                    // Save to your list here
                }
            }
        }
        cursor.close();

    }

    private void albumListCreator() {
        ContentResolver cr = this.getContentResolver();
        String[] projection = new String[] { Albums._ID, Albums.ALBUM, Albums.ARTIST, Albums.NUMBER_OF_SONGS,Albums.ALBUM_ART };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC";
        Cursor cursor = cr.query(Albums.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder);

        int count = 0;

        if(cursor != null) {
            count = cursor.getCount();
            if (count > 0) {
                while (cursor.moveToNext()) {
                    String albumName = cursor.getString(cursor.getColumnIndex(Albums.ALBUM));
                    String artistName = cursor.getString(cursor.getColumnIndex(Albums.ARTIST));
                    String numberofSongs = cursor.getString(cursor.getColumnIndex(Albums.NUMBER_OF_SONGS));
                    String path = cursor.getString(cursor.getColumnIndex(Albums.ALBUM_ART));
                    AlbumFile album = new AlbumFile(albumName, artistName, numberofSongs, path);
                    albums.add(album);
                }
            }
        }
        cursor.close();
    }
    /*
    private void artistListCreator() {
        ContentResolver cr = this.getContentResolver();
        String[] projection = new String[] {MediaStore.Audio.Artists.ARTIST, MediaStore.Audio.Artists.NUMBER_OF_ALBUMS };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC";
        Cursor cursor = cr.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder);
        int count = 0;

        if(cursor != null) {
            count = cursor.getCount();
            if (count > 0) {
                while (cursor.moveToNext()) {
                    String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
                    String numberofAlbums = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
                }
            }
        }
    }*/



    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment myFragment = null;


        switch (position) {
            case 0:
                myFragment = new First_fragment();
                break;
            case 1:
                myFragment = new ListenNow_Fragment();
                break;
            case 2:
                myFragment = new Second_fragment();
                break;
            case 3:
                myFragment = new Third_fragment();
                break;
            case 4:
                myFragment = new Fourth_fragment();
                break;
            default:
                myFragment = new First_fragment();
                break;
        }
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, myFragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.title_section1);
                break;
            case 1:
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                mTitle = getString(R.string.title_section3);
                break;
            case 3:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
        actionBar.setTitle("MoodLoop");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public List<MusicFile> getSongs() {
        return allSongs;
    }

    public List<AlbumFile> getAlbums() {
        return albums;
    }
/*
    public List<AlbumFile> getArtists() {
        return artists;
    }*/

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public void changeFragment(int position, String fragment, String fromalbum) {

        if (fragment.equals("First_Fragment")) {
            Fragment fragmentA = new First_fragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Song Position", position);
            bundle.putString("albumname", fromalbum);
            fragmentA.setArguments(bundle);
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragmentA);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            mNavigationDrawerFragment.mDrawerListView.setItemChecked(0, true);
        }

        if (fragment.equals("AlbumSongsFragment")) {
            Fragment fragmentA = new AlbumSongsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("albumname", albums.get(position).getAlbumname());
            fragmentA.setArguments(bundle);
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragmentA);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.stopService(new Intent(this, MyPlayService.class));
    }
}
