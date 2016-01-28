package com.aranga.moodloop.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

import com.aranga.moodloop.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by aakashranga on 12/31/15.
 */
public class ListenNow_Fragment extends Fragment implements FragmentTabHost.OnTabChangeListener {
    private FragmentTabHost mTabHost;
    private HashMap mapTabInfo = new HashMap();
    private TabInfo mLastTab = null;
   // private List<MusicFile> allSongs;

    @Override
    public void onTabChanged(String tabId) {
        TabInfo newTab = (TabInfo) this.mapTabInfo.get(tabId);
        if (mLastTab != newTab) {
            FragmentTransaction ft = this.getChildFragmentManager().beginTransaction();
            if (mLastTab != null) {
                if (mLastTab.fragment != null) {
                    ft.detach(mLastTab.fragment);
                }
            }
            if (newTab != null) {
                if (newTab.fragment == null) {
                    /*newTab.fragment = Fragment.instantiate(this.getActivity(),
                            newTab.clss.getName(), newTab.args);
                    ft.replace(R.id.realTabContent, newTab.fragment, newTab.tag);*/
                    //Log.d("LN", "This is where you should remake fragment");
                } else {
                    ft.attach(newTab.fragment);
                }
            }

            mLastTab = newTab;
            ft.commit();
            this.getChildFragmentManager().executePendingTransactions();
        }
    }

    private class TabInfo {
        private String tag;
        private Class clss;
        private Bundle args;
        private Fragment fragment;

        TabInfo(String tag, Class clazz, Bundle args) {
            this.tag = tag;
            this.clss = clazz;
            this.args = args;
        }

    }

    class TabFactory implements TabHost.TabContentFactory {

        private final Context mContext;

        /**
         * @param context
         */
        public TabFactory(Context context) {
            mContext = context;
        }

        /**
         * (non-Javadoc)
         *
         * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
         */
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.listen_now_fragment, container, false);
        Log.d("Listen Now Fragment", "Listen Now instantiated");
        mTabHost = (FragmentTabHost) myview.findViewById(android.R.id.tabhost);
        initialiseTabHost(savedInstanceState);
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
            Log.d("Listen Now Frag", "Setting tab here first");
        }
        //MainActivity temp = (MainActivity) getActivity();
        //allSongs = temp.getSongs();
        return myview;
    }



    /**
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }

    private void initialiseTabHost(Bundle args) {
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realTabContent);
        TabInfo tabInfo = null;
        ListenNow_Fragment.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("songsTab").setIndicator("Songs"), (tabInfo = new TabInfo("songsTab", SongsFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        //ListenNow_Fragment.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("artistsTab").setIndicator("Artists"), (tabInfo = new TabInfo("artistsTab", ArtistsFragment.class, args)));
        //this.mapTabInfo.put(tabInfo.tag, tabInfo);
        ListenNow_Fragment.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("albumsTab").setIndicator("Albums"), (tabInfo = new TabInfo("albumsTab", AlbumsFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);

        //
        mTabHost.setOnTabChangedListener(this);
        mTabHost.setCurrentTab(0);
    }

    private static void addTab(ListenNow_Fragment fragment, FragmentTabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        // Attach a Tab view factory to the spec
        tabSpec.setContent(fragment.new TabFactory(fragment.getActivity()));
        String tag = tabSpec.getTag();

        // Check to see if we already have a fragment for this tab, probably
        // from a previously saved state.  If so, deactivate it, because our
        // initial state is that a tab isn't shown.
        tabInfo.fragment = fragment.getChildFragmentManager().findFragmentByTag(tag);
        if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
            FragmentTransaction ft = fragment.getChildFragmentManager().beginTransaction();
            ft.detach(tabInfo.fragment);
            ft.commit();
            //activity.getFragmentManager().executePendingTransactions();
            fragment.getChildFragmentManager().executePendingTransactions();
        }
        tabHost.addTab(tabSpec, tabInfo.clss, tabInfo.args);
        //tabHost.addTab(tabSpec);
    }

}

