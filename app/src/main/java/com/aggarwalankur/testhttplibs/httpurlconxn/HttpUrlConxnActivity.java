package com.aggarwalankur.testhttplibs.httpurlconxn;

import android.app.FragmentManager;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aggarwalankur.testhttplibs.MovieDataItem;
import com.aggarwalankur.testhttplibs.R;
import com.aggarwalankur.testhttplibs.RecyclerItemDecoration;
import com.aggarwalankur.testhttplibs.okhttp.MovieFetchFragment;
import com.aggarwalankur.testhttplibs.okhttp.OkHttpActivity;
import com.aggarwalankur.testhttplibs.okhttp.OkHttpAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.aggarwalankur.testhttplibs.BuildConfig.MOVIE_DB_API_KEY;

public class HttpUrlConxnActivity extends AppCompatActivity implements MovieFetchWithHttpUrlConxnFragment.FetchCallbacks{

    private static final String TAG = HttpUrlConxnActivity.class.getSimpleName();

    private static final String TAG_ASYNC_FRAGMENT = "async_fragment";

    // These are the parameters to build the Movie DB URL
    private static final String URL_SCHEME = "https";
    private static final String BASE_URL = "api.themoviedb.org";

    private static final String EXTRA_PATH_1 = "3";
    private static final String EXTRA_PATH_2 = "movie";
    private static final String TOP_RATED_PATH = "top_rated";

    private static final String API_KEY_PARAMETER = "api_key";

    private MovieFetchWithHttpUrlConxnFragment mFetchFragment;
    private LinearLayoutManager mManager;

    private HttpUrlConnxnAdapter mAdapter;

    private List<MovieDataItem> mDataItems;

    private long requestStartTime = 0, requestEndTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_http_url_conxn);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            File httpCacheDir = new File(this.getCacheDir(), "http");
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (IOException e) {
            Log.i(TAG, "HTTP response cache installation failed:" + e);
        }

        FragmentManager fm = getFragmentManager();
        mFetchFragment = (MovieFetchWithHttpUrlConxnFragment) fm.findFragmentByTag(TAG_ASYNC_FRAGMENT);


        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (mFetchFragment == null) {
            mFetchFragment = new MovieFetchWithHttpUrlConxnFragment();
            fm.beginTransaction().add(mFetchFragment, TAG_ASYNC_FRAGMENT).commit();
            fm.executePendingTransactions();

            fetchTopRatedMovies();
        }else{
            fetchTopRatedMovies();
        }

        //Layout tasks
        mManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView rView = (RecyclerView)findViewById(R.id.stack_recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(mManager);
        mDataItems = new ArrayList<>();
        mAdapter = new HttpUrlConnxnAdapter(mDataItems, this);
        rView.setAdapter(mAdapter);
        RecyclerItemDecoration itemDecoration = new RecyclerItemDecoration(this, R.dimen.item_offset);
        rView.addItemDecoration(itemDecoration);

        requestStartTime = System.currentTimeMillis();


    }

    @Override
    protected void onStop() {
        HttpResponseCache cache = HttpResponseCache.getInstalled();
        if (cache != null) {
            cache.flush();
        }
        super.onStop();
    }

    private void fetchTopRatedMovies(){
        Uri.Builder uriBuilder = new Uri.Builder();

        String url = uriBuilder.scheme(URL_SCHEME)
                .authority(BASE_URL)
                .appendPath(EXTRA_PATH_1)
                .appendPath(EXTRA_PATH_2)
                .appendPath(TOP_RATED_PATH)
                .appendQueryParameter(API_KEY_PARAMETER, MOVIE_DB_API_KEY)
                .build().toString();

        mFetchFragment.fetchListFromUrl(url);

    }

    @Override
    public void onListFetchCompleted(List<MovieDataItem> movieDataItems) {
        requestEndTime = System.currentTimeMillis();

        Toast.makeText(HttpUrlConxnActivity.this, "Request took "+ (requestEndTime - requestStartTime) + " ms", Toast.LENGTH_SHORT).show();
        if(mDataItems == null){
            return;
        }

        if(movieDataItems == null){
            Toast.makeText(this, "Network Error ! Check connection ...", Toast.LENGTH_SHORT).show();
            return;
        }



        mDataItems.clear();
        mDataItems.addAll(movieDataItems);

        mAdapter.notifyDataSetChanged();
    }
}
