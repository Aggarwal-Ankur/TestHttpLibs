package com.aggarwalankur.testhttplibs.volley;

import android.net.Uri;
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
import com.aggarwalankur.testhttplibs.MovieResults;
import com.aggarwalankur.testhttplibs.QuestionPOJO;
import com.aggarwalankur.testhttplibs.R;
import com.aggarwalankur.testhttplibs.RecyclerItemDecoration;
import com.aggarwalankur.testhttplibs.StackoverflowResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static com.aggarwalankur.testhttplibs.BuildConfig.MOVIE_DB_API_KEY;

public class VolleyActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener{

    private static final String TAG = VolleyActivity.class.getSimpleName();

    //Stack-overflow url
    //private static final String REQUEST_URL = "https://api.stackexchange.com/2.2/search?order=desc&sort=activity&tagged=android&site=stackoverflow";


    // These are the parameters to build the Movie DB URL
    private static final String URL_SCHEME = "https";
    private static final String BASE_URL = "api.themoviedb.org";

    private static final String EXTRA_PATH_1 = "3";
    private static final String EXTRA_PATH_2 = "movie";
    private static final String POPULAR_PATH = "popular";
    private static final String TOP_RATED_PATH = "top_rated";

    private static final String API_KEY_PARAMETER = "api_key";



    private LinearLayoutManager mManager;

    private VolleyAdapter mAdapter;
    private List<MovieDataItem> mDataItems;

    private long requestStartTime = 0, requestEndTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate()");


        setContentView(R.layout.activity_volley);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //Layout tasks
        mManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RecyclerView rView = (RecyclerView)findViewById(R.id.stack_recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(mManager);
        mDataItems = new ArrayList<>();
        mAdapter = new VolleyAdapter(mDataItems, this);
        rView.setAdapter(mAdapter);
        RecyclerItemDecoration itemDecoration = new RecyclerItemDecoration(this, R.dimen.item_offset);
        rView.addItemDecoration(itemDecoration);


        final JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                fetchMoviesUrl(),
                null, //Request body
                this, //Response listener
                this) //Error listener
                {
                    //Override the priority
                    @Override
                    public Priority getPriority() {
                        return Priority.IMMEDIATE;
                    }
                };
        //Required if we want to cancel the request. DO NOT use "this" as is given in many examples
        request.setTag(VolleyActivity.class);

        request.setRetryPolicy(new RetryPolicy() {
            private static final int MAX_RETRIES = 5;
            private int mCurrentTimeoutMs = 10000;
            private int mCurrentRetryCount = 0;

            private static final int BACK_OFF_MULTIPLIER = 5;

            @Override
            public int getCurrentTimeout() {
                return mCurrentTimeoutMs;
            }

            @Override
            public int getCurrentRetryCount() {
                return mCurrentRetryCount;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                mCurrentRetryCount++;

                //Some custom retry logic
                mCurrentTimeoutMs += (mCurrentTimeoutMs * BACK_OFF_MULTIPLIER);

                if (mCurrentRetryCount > MAX_RETRIES) {
                    throw error;
                }
            }
        });
        VolleyHelper.getInstance(this).addToRequestQueue(request);

        //Get a request queue and make a volley request
        requestStartTime = System.currentTimeMillis();

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Network Error ! Check connection ...", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Volley Error = " + error.toString());
    }

    @Override
    public void onResponse(Object response) {
        requestEndTime = System.currentTimeMillis();

        Toast.makeText(VolleyActivity.this, "Request took "+ (requestEndTime - requestStartTime) + " ms", Toast.LENGTH_SHORT).show();

        Log.d(TAG, "Volley Response = " + response.toString());

        //Now that the response has been received, we can parse it using Gson and send it to our adapter for display
        Gson mGsonObject = new Gson();
        MovieResults parsedResponse = mGsonObject.fromJson(response.toString(), MovieResults.class);


        mDataItems.clear();
        mDataItems.addAll(parsedResponse.getResults());
        mAdapter.notifyDataSetChanged();

    }


    private String fetchMoviesUrl(){
        Uri.Builder uriBuilder = new Uri.Builder();

        String url = uriBuilder.scheme(URL_SCHEME)
                .authority(BASE_URL)
                .appendPath(EXTRA_PATH_1)
                .appendPath(EXTRA_PATH_2)
                .appendPath(TOP_RATED_PATH)
                .appendQueryParameter(API_KEY_PARAMETER, MOVIE_DB_API_KEY)
                .build().toString();

        return url;
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy()");

        //Request cancellation.
        // Although this works but we do not want to cancel our requests on device rotation. So, commenting this
        //VolleyHelper.getInstance(this).getRequestQueue().cancelAll(VolleyActivity.class);
        super.onDestroy();
    }
}
